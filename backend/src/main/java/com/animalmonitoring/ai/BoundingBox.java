package com.animalmonitoring.ai;

/**
 * Axis-aligned bounding box returned by the YOLO model.
 * All coordinates are in pixel space relative to the original image dimensions.
 */
public class BoundingBox {

    /** Left edge of the box (pixels from image left). */
    private final float x;

    /** Top edge of the box (pixels from image top). */
    private final float y;

    /** Width of the box in pixels. */
    private final float width;

    /** Height of the box in pixels. */
    private final float height;

    public BoundingBox(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public float getX()      { return x; }
    public float getY()      { return y; }
    public float getWidth()  { return width; }
    public float getHeight() { return height; }

    /** X coordinate of the bounding-box centre. */
    public float getCenterX() { return x + width / 2.0f; }

    /** Y coordinate of the bounding-box centre. */
    public float getCenterY() { return y + height / 2.0f; }

    /** Right edge of the box. */
    public float getX2() { return x + width; }

    /** Bottom edge of the box. */
    public float getY2() { return y + height; }

    /**
     * Intersection-over-Union with another bounding box.
     * Used for Non-Maximum Suppression.
     */
    public float iou(BoundingBox other) {
        float interX1 = Math.max(this.x, other.x);
        float interY1 = Math.max(this.y, other.y);
        float interX2 = Math.min(this.getX2(), other.getX2());
        float interY2 = Math.min(this.getY2(), other.getY2());

        if (interX2 <= interX1 || interY2 <= interY1) return 0.0f;

        float interArea = (interX2 - interX1) * (interY2 - interY1);
        float unionArea  = this.width * this.height
                         + other.width * other.height
                         - interArea;

        return unionArea <= 0 ? 0.0f : interArea / unionArea;
    }

    @Override
    public String toString() {
        return String.format("BoundingBox{x=%.1f, y=%.1f, w=%.1f, h=%.1f}", x, y, width, height);
    }
}
