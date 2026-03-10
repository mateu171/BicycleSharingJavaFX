package org.example.bicyclesharing.controller;

import javafx.fxml.FXML;
import javafx.scene.web.WebView;

public class MapController {

@FXML private WebView webView;

  @FXML
  public void initialize() {
    webView.getEngine().loadContent("""
<html>
<head>
<link rel="stylesheet" href="https://unpkg.com/leaflet/dist/leaflet.css"/>
<script src="https://unpkg.com/leaflet/dist/leaflet.js"></script>

<style>
html, body {
    margin:0;
    padding:0;
    height:100%;
}
#map {
    width:100%;
    height:100%;
}
</style>

</head>

<body>

<div id="map"></div>

<script>
var map = L.map('map').setView([50.0, 20.0], 5);

L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '&copy; OpenStreetMap contributors'
}).addTo(map);

setTimeout(function() {
    map.invalidateSize();
}, 2);

</script>

</body>
</html>
""");
  }
}