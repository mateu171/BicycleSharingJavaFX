package org.example.bicyclesharing.controller;

import javafx.fxml.FXML;
import javafx.scene.web.WebView;

public class MapController {

  @FXML
  private WebView webView;

  @FXML
  public void initialize() {
    var url = getClass().getResource("/org/example/bicyclesharing/map.html");
    webView.getEngine().load(url.toExternalForm());
  }
}