package graphics.shared.fonts;

public class GlyphMetrics {
  private int _code;
  private int _w, _h;
  private int _w2, _h2;
  private byte[] _data;
  private int _index = 0;
  
  protected GlyphMetrics(int code, int w, int h, byte[] data) {
    _code = code;
    _w = w;
    _h = h;
    _w2 = graphics.util.Math.nextPowerOfTwo(_w);
    _h2 = graphics.util.Math.nextPowerOfTwo(_h);
    _data = data;
    
    if(_w * _h * 4 != _data.length) {
      System.err.println("Array is fucky");
    }
  }
  
  public int getCode() {
    return _code;
  }
  
  public int getW() {
    return _w;
  }
  
  public int getH() {
    return _h;
  }
  
  public int getW2() {
    return _w2;
  }
  
  public int getH2() {
    return _h2;
  }
  
  public byte[] getRow() {
    byte[] row = new byte[_w2 * 4];
    System.arraycopy(_data, _index, row, 0, _w * 4);
    _index += (_w * 4);
    return row ;
  }
}