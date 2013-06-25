package graphics.shared.fonts;

import graphics.gl00.Context;
import graphics.gl00.Drawable;
import graphics.gl00.Matrix;
import graphics.shared.textures.Texture;

public class Font {
  private Matrix _matrix = Context.getMatrix();
  private Texture _texture;
  
  private int _h;
  
  private Glyph[] _glyph;
  
  protected Font() { }
  
  protected void load(int h, Glyph[] glyph) {
    _h = h;
    _glyph = glyph;
  }
  
  protected void setTexture(Texture texture) {
    _texture = texture;
    
    for(int i = 0; i < _glyph.length; i++) {
      if(_glyph[i] != null) {
        _glyph[i].create(_texture);
      }
    }
  }
  
  public int getW(String text) {
    if(text == null) return 0;
    
    int w = 0;
    for(int i = 0; i < text.length(); i++) {
      int n = text.codePointAt(i);
      if(_glyph[n] != null) {
        w += _glyph[text.codePointAt(i)].w;
      }
    }
    
    return w;
  }
  
  public int getH() {
    return _h;
  }
  
  public void draw(int x, int y, String text, float[] c) {
    draw(x, y, text, c, 0);
  }
  
  public void draw(int x, int y, String text, float[] c, int mask) {
    if(text == null) return;
    
    _matrix.push();
    _matrix.translate(x, y);
    
    for(int i = 0; i < text.length(); i++) {
      Glyph glyph = _glyph[mask == 0 ? text.codePointAt(i) : mask];
      
      if(glyph != null) {
        //TODO: This is just a hack to temporarily get font colour working
        glyph.setColour(c);
        glyph.draw();
        _matrix.translate(glyph.w, 0);
      } else {
        _matrix.translate(4, 0);
      }
    }
    
    _matrix.pop();
  }
  
  protected static class Glyph {
    protected Drawable sprite;
    protected int  w,  h;
    protected int tx, ty;
    protected int tw, th;
    
    protected void create(Texture texture) {
      sprite = Context.newDrawable();
      sprite.setTexture(texture);
      sprite.setWH(tw, th);
      sprite.setTXYWH(tx, ty, tw, th);
      sprite.createQuad();
    }
    
    protected void setColour(float[] c) {
      sprite.setColour(c);
      sprite.createQuad();
    }
    
    public void draw() {
      sprite.draw();
    }
  }
}