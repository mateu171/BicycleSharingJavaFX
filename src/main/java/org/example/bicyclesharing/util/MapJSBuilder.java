package org.example.bicyclesharing.util;

import java.util.List;
import org.example.bicyclesharing.domain.Impl.Bicycle;

public  class MapJSBuilder {
  public static String buildAddBikesScript(List<Bicycle> bikes)
  {
    StringBuilder js = new StringBuilder();
    for (Bicycle bike : bikes) {
      js.append(String.format(
          java.util.Locale.US,
          "addBike(%f,%f,'%s',%f,'%s');",
          bike.getModel().replace("'", "\\'"),
          bike.getPricePerMinute(),
          bike.getId().toString()
      ));
    }

    return js.toString();
  }
}
