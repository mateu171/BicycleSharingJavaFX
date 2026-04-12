package org.example.bicyclesharing.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.bicyclesharing.controller.view.shared.ErrorDialogController;

public class DialogUtil {

  public static void  showError(String message)
  {
    try
    {
      FXMLLoader loader = new FXMLLoader(
          DialogUtil.class.getResource(
              "/org/example/bicyclesharing/presentation/view/shared/ErrorDialogView.fxml"
          )
      );

      Parent root = loader.load();

      ErrorDialogController controller = loader.getController();
      controller.initData(message);

      Scene scene = new Scene(root);
      scene.setFill(Color.TRANSPARENT);
      scene.getStylesheets().add(
          DialogUtil.class.getResource("/org/example/bicyclesharing/css/style.css").toExternalForm()
      );

      Stage stage = new Stage();
      stage.initModality(Modality.APPLICATION_MODAL);
      stage.initStyle(StageStyle.TRANSPARENT);
      stage.setScene(scene);
      stage.showAndWait();
    }catch (Exception e)
    {
      e.printStackTrace();
    }
  }
}
