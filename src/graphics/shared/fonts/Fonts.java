package graphics.shared.fonts;

import io.netty.util.internal.chmv8.ConcurrentHashMapV8;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import graphics.gl00.Context;
import graphics.shared.textures.Texture;
import graphics.shared.textures.Textures;
import graphics.themes.Theme;
import graphics.util.Math;

public class Fonts {
  private static Fonts _instance = new Fonts();
  public static Fonts getInstance() { return _instance; }
  
  private Textures _textures = Context.getTextures();
  private ConcurrentHashMapV8<String, Font> _fonts = new ConcurrentHashMapV8<String, Font>();
  
  private Font _default = getFont(Theme.getInstance().getFontName(), Theme.getInstance().getFontSize());
  public Font getDefault() { return _default; }
  
  private Fonts() { }
  
  public Font getFont(final String name, final int size) {
    final String fullName = name + "." + size;
    if(_fonts.containsKey(fullName)) {
      return _fonts.get(fullName);
    }
    
    final Font f = new Font();
    
    final java.awt.Font font = new java.awt.Font(name, 0, size);
    FontRenderContext rendCont = new FontRenderContext(null, true, true);
    FontMetrics fm = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB_PRE).getGraphics().getFontMetrics(font);
    final ArrayList<Metrics> metrics = new ArrayList<Metrics>();
    
    int highIndex = 0;
    int start = 0x20;
    int end   = 0x3FF;
    
    for(int i = start; i <= end; i++) {
      highIndex = addGlyph(i, font, rendCont, metrics, highIndex);
    }
    
    highIndex = addGlyph(0x25B2, font, rendCont, metrics, highIndex); // Triangle up
    highIndex = addGlyph(0x25BA, font, rendCont, metrics, highIndex); // Triangle right
    highIndex = addGlyph(0x25BC, font, rendCont, metrics, highIndex); // Triangle down
    highIndex = addGlyph(0x25C4, font, rendCont, metrics, highIndex); // Triangle left
    
    int x = 0;
    int y = 0;
    final int w = 512;
    final int h = 512;
    
    final Font.Glyph[] glyph = new Font.Glyph[highIndex + 1];
    
    byte[] data = new byte[w * h * 4];
    for(Metrics m : metrics) {
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
    
    final ByteBuffer buffer = ByteBuffer.allocateDirect(data.length);
    buffer.put(data);
    buffer.position(0);
    
    Context.getContext().addLoadCallback(new Context.Loader.Callback() {
      public void load() {
        Texture texture = _textures.getTexture("Font." + font.getFontName() + "." + font.getSize(), w, h, buffer);
        
        f.load(metrics.get(0).h, glyph);
        f.setTexture(texture);
        _fonts.put(fullName, f);
        
        System.out.println("Font \"" + fullName + "\" created (" + w + "x" + h + ").");
      }
    }, true);
    
    return f;
  }
  
  private int addGlyph(int i, java.awt.Font font, FontRenderContext rendCont, ArrayList<Metrics> metrics, int highIndex) {
    if(!Character.isValidCodePoint(i)) return highIndex;
    
    char[] character = Character.toChars(i);
    
    Rectangle2D bounds = font.getStringBounds(character, 0, character.length, rendCont);
    
    if(bounds.getWidth() == 0) {
      return highIndex;
    }
    
    BufferedImage bi = new BufferedImage((int)bounds.getWidth(), (int)bounds.getHeight(), BufferedImage.TYPE_INT_ARGB);
    
    Graphics2D g = (Graphics2D)bi.getGraphics();
    //g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g.setFont(font);
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
    metrics.add(m);
    
    if(i > highIndex) highIndex = i;
    return highIndex;
  }
  
  private class Metrics {
    private int code;
    private int w, h;
    private int w2, h2;
    private byte[] argb;
  }
}