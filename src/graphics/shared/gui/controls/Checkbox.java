package graphics.shared.gui.controls;

import java.util.LinkedList;

import org.lwjgl.input.Keyboard;

import graphics.gl00.Context;
import graphics.gl00.Drawable;
import graphics.shared.fonts.Font;
import graphics.shared.fonts.Fonts;
import graphics.shared.gui.Control;
import graphics.shared.gui.GUI;
import graphics.themes.Theme;

public class Checkbox extends Control<Checkbox.Events> {
  private Fonts _fonts = Context.getFonts();
  private Font _font = _fonts.getDefault();
  private String _text;
  private int[] _textLoc = {0, 0, 0, 0};
  
  private float[] _backColour = {0, 0, 0, 0};
  private float[] _glowColour = {0, 0, 0, 0};
  private float[] _clickColour = {0, 0, 0, 0};
  private float _fade;
  private float _click;
  private boolean _hover;
  
  private Drawable _check;
  private boolean _checked;
  
  public Checkbox(GUI gui) {
    this(gui, Theme.getInstance());
  }
  
  public Checkbox(GUI gui, Theme theme) {
    super(gui, theme);
    
    _events = new Events(this);
    _events.addClickHandler(new Events.Click() {
      public void click()    { _click = 1; setChecked(!_checked); _events.raiseChecked(); }
      public void clickDbl() { click(); }
    });
    
    _events.addHoverHandler(new Events.Hover() {
      public void enter() { _hover = true;  }
      public void leave() { _hover = false; }
    });
    
    _check = Context.newDrawable();
    _check.setWH(_font.getH(), _font.getH());
    _check.setColour(new float[] {0, 0, 0, 1});
    _check.createBorder();
    
    theme.create(this);
  }
  
  public float[] getGlowColour() {
    return _glowColour;
  }
  
  public void setGlowColour(float[] c) {
    _glowColour = c;
  }
  
  public float[] getClickColour() {
    return _clickColour;
  }
  
  public void setClickColour(float[] c) {
    _clickColour = c;
  }
  
  public void setBackColour(float[] c) {
    _backColour = c;
    
    if(!_hover && _click == 0) {
      _background.setColour(c);
      _background.createQuad();
    }
  }
  
  public String getText() {
    return _text;
  }
  
  public void setText(String text) {
    _text = text;
    resize();
  }
  
  public boolean getChecked() {
    return _checked;
  }
  
  public void setChecked(boolean checked) {
    _checked = checked;
  }
  
  protected void resize() {
    _textLoc[2] = _font.getW(_text);
    _textLoc[3] = _font.getH();
    _textLoc[0] = (int)(_check.getX() * 2 + _check.getW());
    _textLoc[1] = (int)(_loc[3] - _textLoc[3]) / 2;
    _check.setXY(_textLoc[1], _textLoc[1]);
  }
  
  public void draw() {
    if(drawBegin()) {
      _check.draw();
      _font.draw(_textLoc[0], _textLoc[1], _text, _foreColour);
      
      if(_checked) {
        _font.draw((int)(_check.getX() + (_check.getW() - _font.getW("\u00D7")) / 2), (int)_check.getY(), "\u00D7", _foreColour);
      }
    }
    
    drawEnd();
  }
  
  public void logic() {
    if(_click == 0) {
      if(_hover) {
        if(_fade < 1) {
          _fade += 0.1f;
          float[] c = new float[4];
          for(int i = 0; i < c.length; i++) {
            c[i] = (_glowColour[i] - _backColour[i]) * _fade + _backColour[i];
          }
          _background.setColour(c);
          _background.createQuad();
        }
      } else {
        if(_fade > 0) {
          _fade -= 0.05f;
          float[] c = new float[4];
          for(int i = 0; i < c.length; i++) {
            c[i] = (_glowColour[i] - _backColour[i]) * _fade + _backColour[i];
          }
          _background.setColour(c);
          _background.createQuad();
        }
      }
    } else {
      _click -= 0.05f;
      float[] c = new float[4];
      for(int i = 0; i < c.length; i++) {
        c[i] = (_clickColour[i] - (_hover ? _glowColour[i] : _backColour[i])) * _click + (_hover ? _glowColour[i] : _backColour[i]);
      }
      _background.setColour(c);
      _background.createQuad();
      
      if(_click < 0) {
        _click = 0;
      }
    }
  }
  
  public void handleKeyDown(int key) {
    super.handleKeyDown(key);
    
    if(key == Keyboard.KEY_RETURN) {
      _events.raiseClick();
    }
  }
  
  public static class Events extends Control.Events {
    private LinkedList<Checked> _checked = new LinkedList<Checked>();
    
    public void addChangeHandler(Checked e) { _checked.add(e); }
    
    protected Events(Control<?> c) {
      super(c);
    }
    
    public void raiseChecked() {
      for(Checked e : _checked) {
        e.setControl(_control);
        e.checked();
      }
    }
    
    public static abstract class Checked extends Event {
      public abstract void checked();
    }
  }
}