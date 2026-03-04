package org.example.bicyclesharing.controller;

import com.dlsc.gmapsfx.GoogleMapView;
import com.dlsc.gmapsfx.MapComponentInitializedListener;
import com.dlsc.gmapsfx.javascript.object.GoogleMap;
import com.dlsc.gmapsfx.javascript.object.LatLong;
import com.dlsc.gmapsfx.javascript.object.MapOptions;
import com.dlsc.gmapsfx.javascript.object.MapTypeIdEnum;
import com.dlsc.gmapsfx.javascript.object.Marker;
import com.dlsc.gmapsfx.javascript.object.MarkerOptions;
import javafx.fxml.FXML;

public class MapController implements MapComponentInitializedListener {

  @FXML
  private GoogleMapView mapView;

  private GoogleMap map;

  @Override
  public void mapInitialized() {
    LatLong center = new LatLong(48.6208, 22.2879);

    MapOptions options = new MapOptions();
    options.center(center)
        .zoom(13)
        .mapType(MapTypeIdEnum.ROADMAP)
        .overviewMapControl(false)
        .panControl(true)
        .rotateControl(false)
        .scaleControl(true)
        .streetViewControl(true)
        .zoomControl(true);

    map = mapView.createMap(options, false);

    MarkerOptions markerOptions = new MarkerOptions();
    markerOptions.position(center)
        .title("Велосипед 🚲");

    Marker marker = new Marker(markerOptions);
    map.addMarker(marker);
  }

  @FXML
  public void initialize() {
    mapView.setKey("AIzaSyC28oHxwEK9PlF_s0bUzuOlKz9uEXuacZo"); // заміни на свій ключ
    mapView.addMapInitializedListener(this); // виправлено
  }
}