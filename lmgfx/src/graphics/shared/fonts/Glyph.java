package graphics.shared.fonts;

import graphics.gl00.Context;
import graphics.gl00.Drawable;
import graphics.shared.textures.Texture;

public class Glyph {
  private Drawable _sprite;
  private int _x,  _y;
  private int _w,  _h;
  private int _w2, _h2;
  
  protected Glyph(int x, int y, int w, int h, int w2, int h2, Texture texture) {
    _x = x;
    _y = y;
    _w = w;
    _h = h;
    _w2 = w2;
    _h2 = h2;
    
    _sprite = Context.newDrawable();
    _sprite.setTexture(texture);
    _sprite.setWH(_w2, _h2);
    _sprite.setTXYWH(_x, _y, _w2, _h2);
    _sprite.createQuad();
  }
  
  public void setColour(float[] c) {
    _sprite.setColour(c);
    _sprite.createQuad();
  }
  
  public int getW() {
    return _w;
  }
  
  public int getH() {
    return _h;
  }
  
  public void draw() {
    _sprite.draw();
  }
}