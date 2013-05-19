package graphics.shared.gui;

import java.util.LinkedList;

import org.lwjgl.input.Keyboard;

import graphics.gl00.Context;
import graphics.gl00.Drawable;
import graphics.gl00.Matrix;
import graphics.shared.textures.Textures;
import graphics.themes.Theme;
import graphics.util.Time;

public class Control<T> {
  protected GUI      _gui;
  protected Matrix   _matrix   = Context.getMatrix();
  protected Textures _textures = Context.getTextures();
  
  protected Control<?>  _controlParent;
  protected ControlList _controlList = new ControlList(this);
  protected Control<?>  _controlNext;
  protected Control<?>  _controlPrev;
  
  protected T        _events;
  
  protected Drawable _border;
  protected Drawable _background;
  protected float[]  _loc          = {0, 0, 0, 0};
  protected float[]  _foreColour   = {1, 1, 1, 1};
  protected boolean  _enabled      = true;
  protected boolean  _visible      = true;
  protected boolean  _acceptsFocus = true;
  protected boolean  _focus        = false;
  
  protected Drawable _selBox;
  protected int[] _selColour;
  
  private double _lastClick;
  
  public Control(GUI gui) {
    this(gui, Theme.getInstance(), true);
  }
  
  public Control(GUI gui, Theme theme) {
    this(gui, theme, true);
  }
  
  public Control(GUI gui, boolean register) {
    this(gui, Theme.getInstance(), register);
  }
  
  @SuppressWarnings("unchecked")
  public Control(GUI gui, Theme theme, boolean register) {
    _gui = gui;
    
    _events = (T)new Events(this);
    
    if(register) {
      _selBox = Context.newDrawable();
      _selColour = Context.getContext().getNextSelectColour();
      
      float[] floatColour = new float[4];
      
      for(int i = 0; i < floatColour.length; i++) {
        floatColour[i] = _selColour[i] / 255f;
      }
      
      _selBox.setColour(floatColour);
      _selBox.createQuad();
    }
    
    _background = Context.newDrawable();
    _background.setColour(null);
    
    _border = Context.newDrawable();
    _border.setColour(null);
    _border.setXY(-1, -1);
  }
  
  public Control<?> getParent() {
    return _controlParent;
  }
  
  protected void setParent(Control<?> parent) {
    _controlParent = parent;
  }
  
  protected Control<?> getRoot() {
    if(_controlParent != null) {
      return _controlParent.getRoot();
    }
    
    return this;
  }
  
  public ControlList Controls() {
    return _controlList;
  }
  
  protected final Control<?> getControlNext() {
    return _controlNext;
  }
  
  protected final void setControlNext(Control<?> control) {
    _controlNext = control;
  }
  
  protected final Control<?> getControlPrev() {
    return _controlPrev;
  }
  
  protected final void setControlPrev(Control<?> control) {
    _controlPrev = control;
  }
  
  public T events() {
    return _events;
  }
  
  public Drawable getBackground()  { return _background; }
  public float getAllX()           { return _gui.getAllX(this); }
  public float getAllY()           { return _gui.getAllY(this); }
  public float getX()              { return _loc[0]; }
  public float getY()              { return _loc[1]; }
  public float getW()              { return _loc[2]; }
  public float getH()              { return _loc[3]; }
  public boolean getEnabled()      { return _enabled; }
  public boolean getVisible()      { return _visible; }
  public float[] getBackColour()   { return _background.getColour(); }
  public float[] getForeColour()   { return _foreColour; }
  public float[] getBorderColour() { return _border.getColour(); }
  public boolean getAcceptsFocus() { return _acceptsFocus; }
  
  public void setBackground(Drawable d)             { _background = d; }
  public void setX(float x)                         { _loc[0] = x; }
  public void setY(float y)                         { _loc[1] = y; }
  public void setEnabled(boolean enabled)           { _enabled = enabled; }
  public void setBackColour(float[] c)              { _background.setColour(c); }
  public void setForeColour(float[] c)              { _foreColour = c; }
  public void setBorderColour(float[] c)            { _border.setColour(c); }
  public void setAcceptsFocus(boolean acceptsFocus) { _acceptsFocus = acceptsFocus; }
  
  public void setXY(float x, float y) {
    _loc[0] = x;
    _loc[1] = y;
  }
  
  public void setW(float w) {
    _loc[2] = w;
    updateSize();
  }
  
  public void setH(float h) {
    _loc[3] = h;
    updateSize();
  }
  
  public void setWH(float w, float h) {
    _loc[2] = w;
    _loc[3] = h;
    updateSize();
  }
  
  public void setXYWH(float x, float y, float w, float h) {
    _loc[0] = x;
    _loc[1] = y;
    _loc[2] = w;
    _loc[3] = h;
    updateSize();
  }
  
  public void setXYWH(float[] loc) {
    _loc = loc;
    updateSize();
  }
  
  private void updateSize() {
    if(_selBox != null) {
      _selBox.setWH(_loc[2], _loc[3]);
      _selBox.createQuad();
    }
    
    _background.setWH(_loc[2] - _background.getX() * 2, _loc[3] - _background.getY() * 2);
    _background.createQuad();
    
    _border.setWH(_loc[2] - _border.getX() * 2, _loc[3] - _border.getY() * 2);
    _border.createBorder();
    
    resize();
  }
  
  protected void resize() { };
  
  public void setVisible(boolean visible) {
    _visible = visible;
    
    if(!_visible) {
      setFocus(false);
      _controlList.killFocus();
    }
  }
  
  public void setFocus(boolean focus) {
    if(_focus != focus) {
      if(focus) {
        _gui.setFocus(this);
        _focus = true;
        handleGotFocus();
      } else {
        _gui.setFocus(null);
        _focus = false;
        handleLostFocus();
      }
    }
  }
  
  public void handleKeyDown(int key) {
    if(key == Keyboard.KEY_TAB) {
      Control<?> c = _controlNext;
      if(c == null) {
        if(_controlParent != null) {
          c = _controlParent.Controls().getLast();
        }
      }
      
      while(c != null) {
        if(c == this) break;
        
        if(c.getAcceptsFocus()) {
          c.setFocus(true);
          break;
        } else {
          c = c.getControlNext();
          if(c == null) {
            if(_controlParent != null) {
              c = _controlParent.Controls().getLast();
            }
          }
        }
      }
    }
    
    ((Events)_events).raiseKeyDown(key);
  }
  
  public void handleKeyUp(int key) {
    ((Events)_events).raiseKeyUp(key);
  }
  
  public void handleCharDown(char key) {
    ((Events)_events).raiseCharDown(key);
  }
  
  public void handleMouseDown(int x, int y, int button) {
    ((Events)_events).raiseMouseDown(x, y, button);
  }
  
  public void handleMouseUp(int x, int y, int button) {
    ((Events)_events).raiseMouseUp(x, y, button);
    
    if(Time.getTime() - _lastClick <= 250) {
      ((Events)_events).raiseDoubleClick();
    } else {
      ((Events)_events).raiseClick();
      _lastClick = Time.getTime();
    }
  }
  
  public void handleMouseMove(int x, int y, int button) {
    ((Events)_events).raiseMouseMove(x, y, button);
  }
  
  public void handleMouseWheel(int delta) {
    ((Events)_events).raiseMouseWheel(delta);
  }
  
  public void handleMouseEnter() {
    ((Events)_events).raiseMouseEnter();
  }
  
  public void handleMouseLeave() {
    ((Events)_events).raiseMouseLeave();
  }
  
  public void handleAxisLeft(double angle, float x, float y) {
    ((Events)_events).raiseAxisLeft(angle, x, y);
  }
  
  public void handleAxisRight(double angle, float x, float y) {
    ((Events)_events).raiseAxisRight(angle, x, y);
  }
  
  public void handleButtonDown(int button) {
    ((Events)_events).raiseButtonDown(button);
  }
  
  public void handleButtonUp(int button) {
    ((Events)_events).raiseButtonUp(button);
  }
  
  public void handleGotFocus() {
    ((Events)_events).raiseGotFocus();
  }
  
  public void handleLostFocus() {
    ((Events)_events).raiseLostFocus();
  }
  
  protected boolean drawBegin() {
    if(_visible) {
      _matrix.push();
      _matrix.translate(_loc[0], _loc[1]);
      
      if(_background.getColour() != null) {
        _background.draw();
      }
      
      if(_border.getColour() != null) {
        _border.draw();
      }
      
      return true;
    }
    
    return false;
  }
  
  protected void drawEnd() {
    if(_visible) {
      ((Events)_events).raiseDraw();
      
      _controlList.draw();
      _matrix.pop();
    }
    
    if(_controlNext != null) {
      _controlNext.draw();
    }
  }
  
  public void draw() {
    if(drawBegin()) {
      
    }
    
    drawEnd();
  }
  
  public void logic() { }
  public void logicControl() {
    logic();
    _controlList.logic();
    
    if(_controlNext != null) {
      _controlNext.logicControl();
    }
  }
  
  public void drawSelect() {
    if(_visible && _enabled) {
      _matrix.push();
      _matrix.translate(_loc[0], _loc[1]);
      
      if(_selBox != null)
        _selBox.draw();
      
      _controlList.drawSelect();
      
      _matrix.pop();
    }
    
    if(_controlNext != null) {
      _controlNext.drawSelect();
    }
  }
  
  public Control<?> getSelectControl(int[] colour) {
    if(_selBox != null && colour[0] == _selColour[0] && colour[1] == _selColour[1] && colour[2] == _selColour[2]) {
      return this;
    } else {
      Control<?> control = _controlList.getSelectControl(colour);
      if(control != null) {
        return control;
      } else {
        if(_controlNext != null) {
          return _controlNext.getSelectControl(colour);
        }
      }
    }
    
    return null;
  }
  
  public static class Events {
    private LinkedList<Draw>   _draw        = new LinkedList<Draw>();
    private LinkedList<Mouse>  _mouseDown   = new LinkedList<Mouse>();
    private LinkedList<Mouse>  _mouseUp     = new LinkedList<Mouse>();
    private LinkedList<Mouse>  _mouseMove   = new LinkedList<Mouse>();
    private LinkedList<Wheel>  _mouseWheel  = new LinkedList<Wheel>();
    private LinkedList<Hover>  _mouseEnter  = new LinkedList<Hover>();
    private LinkedList<Hover>  _mouseLeave  = new LinkedList<Hover>();
    private LinkedList<Key>    _keyDown     = new LinkedList<Key>();
    private LinkedList<Key>    _keyUp       = new LinkedList<Key>();
    private LinkedList<Char>   _charDown    = new LinkedList<Char>();
    private LinkedList<Axis>   _axisLeft    = new LinkedList<Axis>();
    private LinkedList<Axis>   _axisRight   = new LinkedList<Axis>();
    private LinkedList<Button> _buttonDown  = new LinkedList<Button>();
    private LinkedList<Button> _buttonUp    = new LinkedList<Button>();
    private LinkedList<Click>  _click       = new LinkedList<Click>();
    private LinkedList<Click>  _doubleClick = new LinkedList<Click>();
    private LinkedList<Focus>  _gotFocus    = new LinkedList<Focus>();
    private LinkedList<Focus>  _lostFocus   = new LinkedList<Focus>();
    
    public void onDraw       (Draw   e) { _draw       .add(e); }
    public void onMouseDown  (Mouse  e) { _mouseDown  .add(e); }
    public void onMouseUp    (Mouse  e) { _mouseUp    .add(e); }
    public void onMouseMove  (Mouse  e) { _mouseMove  .add(e); }
    public void onMouseWheel (Wheel  e) { _mouseWheel .add(e); }
    public void onMouseEnter (Hover  e) { _mouseEnter .add(e); }
    public void onMouseLeave (Hover  e) { _mouseLeave .add(e); }
    public void onKeyDown    (Key    e) { _keyDown    .add(e); }
    public void onKeyUp      (Key    e) { _keyUp      .add(e); }
    public void onCharDown   (Char   e) { _charDown   .add(e); }
    public void onAxisLeft   (Axis   e) { _axisLeft   .add(e); }
    public void onAxisRight  (Axis   e) { _axisRight  .add(e); }
    public void onButtonDown (Button e) { _buttonDown .add(e); }
    public void onButtonUp   (Button e) { _buttonUp   .add(e); }
    public void onClick      (Click  e) { _click      .add(e); }
    public void onDoubleClick(Click  e) { _doubleClick.add(e); }
    public void onGotFocus   (Focus  e) { _gotFocus   .add(e); }
    public void onLostFocus  (Focus  e) { _lostFocus  .add(e); }
    
    protected Control<?> _control;
    
    public Events(Control<?> c) {
      _control = c;
    }
    
    public void raiseDraw() {
      for(Draw e : _draw) {
        e.setControl(_control);
        e.event();
      }
    }
    
    public void raiseMouseDown(int x, int y, int button) {
      for(Mouse e : _mouseDown) {
        e.setControl(_control);
        e.event(x, y, button);
      }
    }
    
    public void raiseMouseUp(int x, int y, int button) {
      for(Mouse e : _mouseUp) {
        e.setControl(_control);
        e.event(x, y, button);
      }
    }
    
    public void raiseMouseMove(int x, int y, int button) {
      for(Mouse e : _mouseMove) {
        e.setControl(_control);
        e.event(x, y, button);
      }
    }
    
    public void raiseMouseWheel(int delta) {
      for(Wheel e : _mouseWheel) {
        e.setControl(_control);
        e.event(delta);
      }
    }
    
    public void raiseMouseEnter() {
      for(Hover e : _mouseEnter) {
        e.setControl(_control);
        e.event();
      }
    }
    
    public void raiseMouseLeave() {
      for(Hover e : _mouseLeave) {
        e.setControl(_control);
        e.event();
      }
    }
    
    public void raiseKeyDown(int key) {
      for(Key e : _keyDown) {
        e.setControl(_control);
        e.event(key);
      }
    }
    
    public void raiseKeyUp(int key) {
      for(Key e : _keyUp) {
        e.setControl(_control);
        e.event(key);
      }
    }
    
    public void raiseCharDown(char key) {
      for(Char e : _charDown) {
        e.setControl(_control);
        e.event(key);
      }
    }
    
    public void raiseAxisLeft(double angle, float x, float y) {
      for(Axis e : _axisLeft) {
        e.setControl(_control);
        e.event(x, y, angle);
      }
    }
    
    public void raiseAxisRight(double angle, float x, float y) {
      for(Axis e : _axisRight) {
        e.setControl(_control);
        e.event(x, y, angle);
      }
    }
    
    public void raiseButtonDown(int button) {
      for(Button e : _buttonDown) {
        e.setControl(_control);
        e.event(button);
      }
    }
    
    public void raiseButtonUp(int button) {
      for(Button e : _buttonUp) {
        e.setControl(_control);
        e.event(button);
      }
    }
    
    public void raiseClick() {
      for(Click e : _click) {
        e.setControl(_control);
        e.event();
      }
    }
    
    public void raiseDoubleClick() {
      for(Click e : _doubleClick) {
        e.setControl(_control);
        e.event();
      }
    }
    
    public void raiseGotFocus() {
      for(Focus e : _gotFocus) {
        e.setControl(_control);
        e.event();
      }
    }
    
    public void raiseLostFocus() {
      for(Focus e : _lostFocus) {
        e.setControl(_control);
        e.event();
      }
    }
    
    public static class Event {
      private Control<?> _control;
      
      public Control<?> getControl() { return _control; }
      public    void    setControl(Control<?> control) { _control = control; }
    }
    
    public static abstract class Draw extends Event {
      public abstract void event();
    }
    
    public static abstract class Mouse extends Event {
      public abstract void event(int x, int y, int button);
    }
    
    public static abstract class Wheel extends Event {
      public abstract void event(int delta);
    }
    
    public static abstract class Hover extends Event {
      public abstract void event();
    }
    
    public static abstract class Key extends Event {
      public abstract void event(int key);
    }
    
    public static abstract class Char extends Event {
      public abstract void event(char key);
    }
    
    public static abstract class Axis extends Event {
      public abstract void event(float x, float y, double angle);
    }
    
    public static abstract class Button extends Event {
      public abstract void event(int button);
    }
    
    public static abstract class Click extends Event {
      public abstract void event();
    }
    
    public static abstract class Focus extends Event {
      public abstract void event();
    }
  }
}