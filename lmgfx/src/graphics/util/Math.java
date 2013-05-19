package graphics.util;

public class Math {
  public static int nextPowerOfTwo(int number) {
    int power = 2;
    
    while(power < number) {
      power *= 2;
    }
    
    return power;
  }
  
  public static int nextPowerOfTwo(float number) {
    return nextPowerOfTwo(java.lang.Math.round(number));
  }
  
  public static int nextPowerOfTwo(double number) {
    return nextPowerOfTwo(java.lang.Math.round(number));
  }
}