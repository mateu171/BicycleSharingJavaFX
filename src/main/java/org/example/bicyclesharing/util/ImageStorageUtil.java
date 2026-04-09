package org.example.bicyclesharing.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public final class ImageStorageUtil {

  private ImageStorageUtil() {
  }

  public static File chooseImage(Stage stage) {
    FileChooser fileChooser = new FileChooser();
    fileChooser.getExtensionFilters().add(
        new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.webp")
    );

    return fileChooser.showOpenDialog(stage);
  }

  public static void showPreview(File file, ImageView imageView, double width, double height) {
    if (file == null || imageView == null) {
      return;
    }

    Image image = new Image(file.toURI().toString(), width, height, true, true);
    imageView.setImage(image);
  }

  public static String saveImage(File imageFile, String directoryName) throws IOException {
    if (imageFile == null) {
      return null;
    }

    String originalName = imageFile.getName();
    String extension = "";

    int dotIndex = originalName.lastIndexOf('.');
    if (dotIndex >= 0) {
      extension = originalName.substring(dotIndex);
    }

    String newFileName = UUID.randomUUID() + extension;

    Path imagesDir = Path.of("images", directoryName);
    Files.createDirectories(imagesDir);

    Path targetPath = imagesDir.resolve(newFileName);

    Files.copy(
        imageFile.toPath(),
        targetPath,
        StandardCopyOption.REPLACE_EXISTING
    );

    return targetPath.toString().replace("\\", "/");
  }

  public static Image loadImageOrDefault(String path, String defaultResourcePath, double w, double h) {
    try {
      if (path != null && !path.isBlank()) {
        File file = new File(path);
        if (file.exists()) {
          return new Image(file.toURI().toString(), w, h, true, true);
        }
      }

      return new Image(
          ImageStorageUtil.class.getResource(defaultResourcePath).toExternalForm(),
          w, h, true, true
      );

    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
}