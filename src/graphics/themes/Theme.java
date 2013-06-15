package graphics.themes;

import graphics.gl00.Context;
import graphics.gl00.Scalable;
import graphics.shared.gui.controls.Button;
import graphics.shared.gui.controls.Checkbox;
import graphics.shared.gui.controls.Dropdown;
import graphics.shared.gui.controls.Label;
import graphics.shared.gui.controls.List;
import graphics.shared.gui.controls.Menu;
import graphics.shared.gui.controls.Picture;
import graphics.shared.gui.controls.Scrollbar;
import graphics.shared.gui.controls.Textbox;
import graphics.shared.gui.controls.Scrollbar.Orientation;
import graphics.shared.gui.controls.compound.Window;
import graphics.shared.textures.Textures;

public class Theme {
  private static Theme _instance = new Theme();
  public static Theme getInstance() { return _instance; }
  
  protected Textures _textures = Context.getTextures();
  
  protected String _fontName = "Arial";
  protected    int _fontSize = 11;
  
  protected Theme() { };
  
  public String   getFontName() { return _fontName; }
  public    int   getFontSize() { return _fontSize; }
  
  public void create(Button c) {
    Scalable s = Context.newScalable();
    s.setTexture(_textures.getTexture("gui/button.png"));
    s.setSize(
        new float[] {2, 2, 2, 2},
        new float[] {2, 2, 2, 2},
        5, 5, 1
    );
    
    c.setBackground(s);
    c.setBackColour(new float[] {0.2f, 0.2f, 0.2f, 1});
    c.setGlowColour(new float[] {0.3f, 0.3f, 0.3f, 1});
    c.setClickColour(new float[] {0x3F / 255f, 0xCF / 255f, 0, 1});
    c.setForeColour(new float[] {1, 1, 1, 1});
    c.setWH(90, 20);
    c.setText("Button");
  }
  
  public void create(Checkbox c) {
    Scalable s = Context.newScalable();
    s.setTexture(_textures.getTexture("gui/button.png"));
    s.setSize(
        new float[] {2, 2, 2, 2},
        new float[] {2, 2, 2, 2},
        5, 5, 1
    );
    
    c.setBackground(s);
    c.setBackColour(new float[] {0.2f, 0.2f, 0.2f, 1});
    c.setGlowColour(new float[] {0.3f, 0.3f, 0.3f, 1});
    c.setClickColour(new float[] {0x3F / 255f, 0xCF / 255f, 0, 1});
    c.setForeColour(new float[] {1, 1, 1, 1});
    c.setWH(90, 20);
    c.setText("Checkbox");
  }
  
  public void create(Dropdown c) {
    c.setBackColour(new float[] {0.2f, 0.2f, 0.2f, 1});
    c.setGlowColour(new float[] {0.3f, 0.3f, 0.3f, 1});
    c.setForeColour(new float[] {1, 1, 1, 1});
    c.setBorderColour(new float[] {0, 0, 0, 1});
    c.setWH(200, 19);
  }
  
  public void create(Label c) {
    c.setBackColour(null);
    c.setForeColour(new float[] {1, 1, 1, 1});
    c.setAutoSize(true);
    c.setText("Label");
  }
  
  public void create(List c) {
    c.setBackColour(new float[] {0.2f, 0.2f, 0.2f, 1});
    c.setWH(200, 80);
  }
  
  public void create(Menu c) {
    c.setBackColour(new float[] {0.2f, 0.2f, 0.2f, 1});
    c.setForeColour(new float[] {1, 1, 1, 1});
    c.setBorderColour(new float[] {0, 0, 0, 1});
    c.setWH(150, 300);
  }
  
  public void create(Scrollbar c) {
    c.setBackColour(null);
    c.setMin(0);
    c.setMax(99);
    c.setVal(0);
    c.setOrientation(Orientation.VERTICAL);
    c.setWH(16, 100);
  }
  
  public void create(Textbox c) {
    c.setBackColour(new float[] {0.2f, 0.2f, 0.2f, 1});
    c.setGlowColour(new float[] {0.3f, 0.3f, 0.3f, 1});
    c.setForeColour(new float[] {1, 1, 1, 1});
    c.setBorderColour(new float[] {0, 0, 0, 1});
    c.setWH(200, 19);
  }
  
  public void create(Window c, Picture title, Label text, Button close) {
    title.setBorderColour(new float[] {0, 0, 0, 1});
    title.setH(20);
    
    text.setText("Window");
    
    close.setBackColour(new float[] {0.75f, 0, 0, 1});
    close.setGlowColour(new float[] {1, 0, 0, 1});
    close.setWH(title.getH() + 1, title.getH() + 1);
    close.setText("\u00D7");
    
    c.setBackColour(new float[] {0.33f, 0.33f, 0.33f, 0.66f});
    c.setBorderColour(new float[] {0, 0, 0, 1});
    c.setWH(250, 300);
  }
  
  public void createWindowTab(Button tab, Picture panel) {
    create(tab);
    tab.setWH(50, 21);
  }
}