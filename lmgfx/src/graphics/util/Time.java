package graphics.util;

import org.lwjgl.Sys;

public class Time {
  public static double getTime() {
    return Sys.getTime();
  }
  
  public static double MSToTicks(double ms) {
    return (ms * Sys.getTimerResolution()) / 1000;
  }
  
  public static double HzToTicks(double hz) {
    return ((1000 / hz) * Sys.getTimerResolution()) / 1000;
  }
}