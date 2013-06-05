package graphics.shared.fonts;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import graphics.gl00.Context;
import graphics.shared.textures.Texture;
import graphics.shared.textures.Textures;
import graphics.themes.Theme;
import graphics.util.Math;

public class Fonts {
  private static Fonts _instance = new Fonts();
  
  public static Fonts getInstance() {
    return _instance;
  }
  
  private Textures _textures = Context.getTextures();
  private HashMap<String, Font> _fonts = new HashMap<String, Font>();
  
  private Font _default = getFont(Theme.getInstance().getFontName(), Theme.getInstance().getFontSize());
  
  private Fonts() { }
  
  public Font getDefault() {
    return _default;
  }
  
  private java.awt.Font _font;
  private FontRenderContext _rendCont;
  
  private ArrayList<Metrics> _metrics;
  
  private int _highIndex;
  
  public Font getFont(String name, int size) {
    String fullName = name + "." + size;
    if(_fonts.containsKey(fullName)) {
      System.out.println("Font \"" + fullName + "\" already loaded.");
      return _fonts.get(fullName);
    }
    
    _font = new java.awt.Font(name, 0, size);
    _rendCont = new FontRenderContext(null, true, true);
    
    FontMetrics fm = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB_PRE).getGraphics().getFontMetrics(_font);
    
    _metrics = new ArrayList<Metrics>();
    
    int start = 0x20;
    int end   = 0x3FF;
    
    for(int i = start; i <= end; i++) {
      addGlyph(i);
    }
    
    addGlyph(0x25B2); // Triangle up
    addGlyph(0x25BA); // Triangle right
    addGlyph(0x25BC); // Triangle down
    addGlyph(0x25C4); // Triangle left
    
    int x = 0;
    int y = 0;
    int w = 512;
    int h = 512;
    
    Font.Glyph[] glyph = new Font.Glyph[_highIndex + 1];
    
    byte[] data = new byte[w * h * 4];
    for(Metrics m : _metrics) {
      if(x + m.w2 > w) {
        x = 0;
        y += m.h2;
      }
      
      int i1 = (y * w * 4) + x * 4;
      int i2 = 0;
      
      for(int n = 0; n < m.h; n++) {
        System.arraycopy(m.argb, i2, data, i1, m.w * 4);
        
        i1 += w * 4;
        i2 += m.w * 4;
      }
      
      Font.Glyph g = new Font.Glyph();
      g.w = fm.charWidth(m.code);
      g.h = m.h;
      g.tx = x;
      g.ty = y;
      g.tw = m.w2;
      g.th = m.h2;
      glyph[m.code] = g;
      
      x += m.w2;
    }
    
    ByteBuffer buffer = ByteBuffer.allocateDirect(data.length);
    buffer.put(data);
    buffer.position(0);
    
    Texture texture = _textures.getTexture("Font." + _font.getFontName() + "." + _font.getSize(), w, h, buffer);
    
    Font f = new Font(_metrics.get(0).h, glyph);
    f.setTexture(texture);
    _fonts.put(fullName, f);
    
    System.out.println("Font \"" + fullName + "\" created (" + w + "x" + h + ").");
    
    return f;
  }
  
  private void addGlyph(int i) {
    if(!Character.isValidCodePoint(i)) return;
    
    char[] character = Character.toChars(i);
    
    Rectangle2D bounds = _font.getStringBounds(character, 0, character.length, _rendCont);
    
    if(bounds.getWidth() == 0) {
      return;
    }
    
    BufferedImage bi = new BufferedImage((int)bounds.getWidth(), (int)bounds.getHeight(), BufferedImage.TYPE_INT_ARGB);
    
    Graphics2D g = (Graphics2D)bi.getGraphics();
    //g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g.setFont(_font);
    g.drawString(new String(character), 0, (int)(bounds.getHeight() - bounds.getMaxY()));
    
    int[] argb = null;
    argb = bi.getData().getPixels(0, 0, (int)bounds.getWidth(), (int)bounds.getHeight(), argb);
    
    byte[] argbByte = new byte[argb.length];
    for(int n = 0; n < argb.length; n++) {
      argbByte[n] = (byte)argb[n];
    }
    
    Metrics m = new Metrics();
    m.code = i;
    m.w = (int)bounds.getWidth();
    m.h = (int)bounds.getHeight();
    m.w2 = Math.nextPowerOfTwo(m.w);
    m.h2 = Math.nextPowerOfTwo(m.h);
    m.argb = argbByte;
    _metrics.add(m);
    
    if(i > _highIndex) _highIndex = i;
  }
  
  private class Metrics {
    private int code;
    private int w, h;
    private int w2, h2;
    private byte[] argb;
  }
}