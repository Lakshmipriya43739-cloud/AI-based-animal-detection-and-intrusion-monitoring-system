package com.animalmonitoring.ai;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * OpenCV-based image and frame pre-processing component.
 *
 * <h3>Native library loading</h3>
 * The openpnp {@code org.openpnp:opencv} jar bundles the native JNI library.
 * Call {@link #loadNativeLibrary()} once at application startup — this is done
 * automatically by {@link com.animalmonitoring.config.OpenCvConfig}.
 *
 * <h3>Input sources supported</h3>
 * <ul>
 *   <li>Raw image bytes (JPEG / PNG) from a REST upload</li>
 *   <li>A filesystem image path</li>
 *   <li>A webcam / video device index (for live capture)</li>
 *   <li>An existing {@link Mat} (programmatic use)</li>
 * </ul>
 *
 * <p>All paths ultimately produce a pre-processed float array suitable for
 * YOLO inference via {@link OnnxModelService#runInference}.
 */
@Component
public class FrameProcessor {

    private static final Logger log = LoggerFactory.getLogger(FrameProcessor.class);

    private final AiModelConfiguration config;

    public FrameProcessor(AiModelConfiguration config) {
        this.config = config;
    }

    // ------------------------------------------------------------------ native lib
    /**
     * Loads the OpenCV native library bundled inside the openpnp jar.
     * Safe to call multiple times — subsequent calls are no-ops.
     */
    public static void loadNativeLibrary() {
        try {
            nu.pattern.OpenCV.loadShared();
            log.info("OpenCV native library loaded (version: {})", Core.VERSION);
        } catch (Exception e) {
            log.warn("Could not load OpenCV native library via shared loader, "
                    + "falling back to System.loadLibrary: {}", e.getMessage());
            try {
                System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
            } catch (UnsatisfiedLinkError ule) {
                log.error("OpenCV native library not available. "
                        + "Image pre-processing will be unavailable: {}", ule.getMessage());
            }
        }
    }

    // ------------------------------------------------------------------ image decoding
    /**
     * Decode raw image bytes (JPEG / PNG / BMP) into an OpenCV {@link Mat}.
     *
     * @param imageBytes  Raw bytes from a multipart upload.
     * @return Decoded colour image, or an empty Mat if decoding fails.
     */
    public Mat decodeImageBytes(byte[] imageBytes) {
        MatOfByte mob = new MatOfByte(imageBytes);
        Mat decoded = Imgcodecs.imdecode(mob, Imgcodecs.IMREAD_COLOR);
        mob.release();
        if (decoded.empty()) {
            log.warn("Image decoding produced an empty Mat — invalid or unsupported format.");
        }
        return decoded;
    }

    /**
     * Load an image from the filesystem.
     *
     * @param filePath Absolute or relative path to the image file.
     */
    public Mat loadImageFromPath(String filePath) {
        Mat img = Imgcodecs.imread(filePath, Imgcodecs.IMREAD_COLOR);
        if (img.empty()) {
            log.warn("Could not load image from path: {}", filePath);
        }
        return img;
    }

    /**
     * Capture a single frame from a video device or file.
     *
     * @param source Device index (e.g., 0 for the default webcam) or a video file path.
     * @return Captured frame, or an empty Mat if capture fails.
     */
    public Mat captureFrame(String source) {
        VideoCapture cap;
        try {
            int deviceIndex = Integer.parseInt(source);
            cap = new VideoCapture(deviceIndex);
        } catch (NumberFormatException e) {
            cap = new VideoCapture(source);
        }

        Mat frame = new Mat();
        if (!cap.isOpened()) {
            log.warn("Cannot open video source: {}", source);
            cap.release();
            return frame;
        }

        cap.read(frame);
        cap.release();

        if (frame.empty()) {
            log.warn("Captured empty frame from source: {}", source);
        }
        return frame;
    }

    // ------------------------------------------------------------------ preprocessing
    /**
     * Pre-process an OpenCV Mat into a normalised float array ready for YOLO inference.
     *
     * <p>Steps:
     * <ol>
     *   <li>Convert BGR → RGB</li>
     *   <li>Resize to the configured model input dimensions (with letter-boxing if needed)</li>
     *   <li>Normalise pixel values to [0.0, 1.0]</li>
     *   <li>Convert HWC layout → CHW layout (required by ONNX/YOLO)</li>
     * </ol>
     *
     * @param src        Input image in BGR colour space (standard OpenCV output).
     * @return Float array of shape [1 × 3 × H × W] suitable for ONNX input.
     */
    public float[] preprocessForYolo(Mat src) {
        int targetW = config.getInput().getWidth();
        int targetH = config.getInput().getHeight();

        // 1. BGR → RGB
        Mat rgb = new Mat();
        Imgproc.cvtColor(src, rgb, Imgproc.COLOR_BGR2RGB);

        // 2. Resize to model input size
        Mat resized = new Mat();
        Imgproc.resize(rgb, resized, new Size(targetW, targetH));
        rgb.release();

        // 3. Convert to float and normalise to [0,1]
        Mat floatMat = new Mat();
        resized.convertTo(floatMat, CvType.CV_32F, 1.0 / 255.0);
        resized.release();

        // 4. HWC → CHW (split channels and flatten in C, H, W order)
        int channels = floatMat.channels(); // 3
        float[] chw = new float[channels * targetH * targetW];

        // Extract each channel
        java.util.List<Mat> channelMats = new java.util.ArrayList<>();
        Core.split(floatMat, channelMats);

        for (int c = 0; c < channels; c++) {
            float[] row = new float[targetH * targetW];
            channelMats.get(c).get(0, 0, row);
            System.arraycopy(row, 0, chw, c * targetH * targetW, row.length);
            channelMats.get(c).release();
        }

        floatMat.release();
        return chw;
    }

    /**
     * Returns the original width of a Mat (safe — returns 0 for empty Mat).
     */
    public int getWidth(Mat mat) {
        return mat.empty() ? 0 : mat.cols();
    }

    /**
     * Returns the original height of a Mat (safe — returns 0 for empty Mat).
     */
    public int getHeight(Mat mat) {
        return mat.empty() ? 0 : mat.rows();
    }

    /**
     * Save a Mat to disk as JPEG.
     *
     * @param mat      Image to save.
     * @param filePath Destination file path (must include .jpg / .png extension).
     * @return {@code true} if written successfully.
     */
    public boolean saveImage(Mat mat, String filePath) {
        if (mat.empty()) return false;
        boolean ok = Imgcodecs.imwrite(filePath, mat);
        if (!ok) log.warn("Imgcodecs.imwrite failed for path: {}", filePath);
        return ok;
    }
}
