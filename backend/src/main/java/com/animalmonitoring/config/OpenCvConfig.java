package com.animalmonitoring.config;

import com.animalmonitoring.ai.FrameProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

/**
 * Loads the OpenCV native library once when the Spring context is ready.
 *
 * The openpnp {@code org.openpnp:opencv} jar bundles the platform-specific
 * native library (DLL on Windows, .so on Linux, .dylib on macOS) and extracts
 * it at runtime.  This must happen before any OpenCV API is called.
 */
@Configuration
public class OpenCvConfig {

    private static final Logger log = LoggerFactory.getLogger(OpenCvConfig.class);

    @EventListener(ApplicationReadyEvent.class)
    public void loadOpenCv() {
        log.info("Loading OpenCV native library...");
        FrameProcessor.loadNativeLibrary();
    }
}
