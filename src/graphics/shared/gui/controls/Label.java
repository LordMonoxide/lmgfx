package graphics.shared.gui.controls;

import graphics.gl00.Context;
import graphics.shared.fonts.Font;
import graphics.shared.fonts.Fonts;
import graphics.shared.gui.Control;
import graphics.shared.gui.GUI;
import graphics.themes.Theme;

public class Label extends Control<Control.Events> {
  private Fonts _fonts = Context.getFonts();
  private Font _font = _fonts.getDefault();
  
  private boolean _autoSize;
  private String _text;
  private int _textY;
  
  public Label(GUI gui) {
    this(gui, Theme.getInstance());
  }
  
  public Label(GUI gui, Theme theme) {
    super(gui, theme, false);
    _events = new Control.Events(this);
    setAcceptsFocus(false);
    theme.create(this);
  }
  
  public Font getFont() {
    return _font;
  }
  
  public void setFont(Font font) {
    _font = font;
  }
  
  protected void resize() {
    _textY = (int)(_loc[3] - _font.getH()) / 2;
  }
  
  public boolean getAutoSize() {
    return _autoSize;
  }
  
  public void setAutoSize(boolean autoSize) {
    _autoSize = autoSize;
  }
  
  public String getText() {
    return _text;
  }
  
  public void setText(String text) {
    _text = text;
    
    if(_text != null) {
      if(_autoSize) {
        setWH(_font.getW(_text), _font.getH());
      }
    }
  }
  
  public void draw() {
    if(drawBegin()) {
      _font.draw(0, _textY, _text, _foreColour);
    }
    
    drawEnd();
  }
}