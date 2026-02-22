package org.example.bicyclesharing.util;

import java.util.Random;

public class CodeGenerate {

  public static int generate() {
    Random random = new Random();
    return 100000 + random.nextInt(900000);
  }
}
