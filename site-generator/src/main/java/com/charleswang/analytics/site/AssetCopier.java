package com.charleswang.analytics.site;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class AssetCopier {

    public static void copyAssets(Path sourceDir, Path targetDir)
            throws IOException {

        if (!Files.exists(sourceDir)) {
            System.out.println("No assets directory found: " + sourceDir);
            return;
        }

        Files.createDirectories(targetDir);

        try (DirectoryStream<Path> stream =
                     Files.newDirectoryStream(sourceDir)) {

            for (Path file : stream) {
                Files.copy(
                    file,
                    targetDir.resolve(file.getFileName()),
                    StandardCopyOption.REPLACE_EXISTING
                );
            }
        }
    }
}
