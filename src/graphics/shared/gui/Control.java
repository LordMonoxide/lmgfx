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
  public void setBackColour(float[] c)              { _background.setColour(c); _background.createQuad(); }
  public void setForeColour(float[] c)              { _foreColour = c; }
  public void setBorderColour(float[] c)            { _border.setColour(c); _border.createBorder(); }
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
    ((Events)_events).raiseKeyText(key);
  }
  
  public void handleMouseDown(int x, int y, int button) {
    ((Events)_events).raiseMouseDown(x, y, button);
  }
  
  public void handleMouseUp(int x, int y, int button) {
    ((Events)_events).raiseMouseUp(x, y, button);
    
    if(Time.getTime() - _lastClick <= 250) {
      ((Events)_events).raiseClickDbl();
    } else {
      ((Events)_events).raiseClick();
      _lastClick = Time.getTime();
    }
  }
  
  public void handleMouseMove(int x, int y, int button) {
    ((Events)_events).raiseMouseMove(x, y, button);
  }
  
  public void handleMouseWheel(int delta) {
    ((Events)_events).raiseMouseScroll(delta);
  }
  
  public void handleMouseEnter() {
    ((Events)_events).raiseHoverEnter();
  }
  
  public void handleMouseLeave() {
    ((Events)_events).raiseHoverLeave();
  }
  
  public void handleGotFocus() {
    ((Events)_events).raiseFocusGot();
  }
  
  public void handleLostFocus() {
    ((Events)_events).raiseFocusLost();
  }
  
  protected boolean drawBegin() {
    if(_visible) {
      _matrix.push();
      _matrix.translate(_loc[0], _loc[1]);
      
      if(_background.getColour() != null) {
        _background.draw();
      }
      
      return true;
    }
    
    return false;
  }
  
  protected void drawEnd() {
    if(_visible) {
      ((Events)_events).raiseDraw();
      
      _controlList.draw();
      
      if(_border.getColour() != null) {
        _border.draw();
      }
      
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
    private LinkedList<Draw>   _draw   = new LinkedList<Draw>();
    private LinkedList<Mouse>  _mouse  = new LinkedList<Mouse>();
    private LinkedList<Key>    _key    = new LinkedList<Key>();
    private LinkedList<Click>  _click  = new LinkedList<Click>();
    private LinkedList<Scroll> _scroll = new LinkedList<Scroll>();
    private LinkedList<Hover>  _hover  = new LinkedList<Hover>();
    private LinkedList<Focus>  _focus  = new LinkedList<Focus>();
    
    public void addDrawHandler  (Draw   e) { _draw  .add(e); }
    public void addMouseHandler (Mouse  e) { _mouse .add(e); }
    public void addKeyHandler   (Key    e) { _key   .add(e); }
    public void addClickHandler (Click  e) { _click .add(e); }
    public void addScrollHandler(Scroll e) { _scroll.add(e); }
    public void addHoverHandler (Hover  e) { _hover .add(e); }
    public void addFocusHandler (Focus  e) { _focus .add(e); }
    
    protected Control<?> _control;
    
    public Events(Control<?> c) {
      _control = c;
    }
    
    public void raiseDraw() {
      for(Draw e : _draw) {
        e.setControl(_control);
        e.draw();
      }
    }
    
    public void raiseMouseDown(int x, int y, int button) {
      for(Mouse e : _mouse) {
        e.setControl(_control);
        e.down(x, y, button);
      }
    }
    
    public void raiseMouseUp(int x, int y, int button) {
      for(Mouse e : _mouse) {
        e.setControl(_control);
        e.up(x, y, button);
      }
    }
    
    public void raiseMouseMove(int x, int y, int button) {
      for(Mouse e : _mouse) {
        e.setControl(_control);
        e.move(x, y, button);
      }
    }
    
    public void raiseMouseScroll(int delta) {
      for(Scroll e : _scroll) {
        e.setControl(_control);
        e.scroll(delta);
      }
    }
    
    public void raiseHoverEnter() {
      for(Hover e : _hover) {
        e.setControl(_control);
        e.enter();
      }
    }
    
    public void raiseHoverLeave() {
      for(Hover e : _hover) {
        e.setControl(_control);
        e.leave();
      }
    }
    
    public void raiseClick() {
      for(Click e : _click) {
        e.setControl(_control);
        e.click();
      }
    }
    
    public void raiseClickDbl() {
      for(Click e : _click) {
        e.setControl(_control);
        e.clickDbl();
      }
    }
    
    public void raiseKeyDown(int key) {
      for(Key e : _key) {
        e.setControl(_control);
        e.down(key);
      }
    }
    
    public void raiseKeyUp(int key) {
      for(Key e : _key) {
        e.setControl(_control);
        e.up(key);
      }
    }
    
    public void raiseKeyText(char key) {
      for(Key e : _key) {
        e.setControl(_control);
        e.text(key);
      }
    }
    
    public void raiseFocusGot() {
      for(Focus e : _focus) {
        e.setControl(_control);
        e.got();
      }
    }
    
    public void raiseFocusLost() {
      for(Focus e : _focus) {
        e.setControl(_control);
        e.lost();
      }
    }
    
    public static class Event {
      private Control<?> _control;
      public  Control<?> getControl() { return _control; }
      public     void    setControl(Control<?> control) { _control = control; }
    }
    
    public static abstract class Draw extends Event {
      public abstract void draw();
    }
    
    public static abstract class Mouse extends Event {
      public abstract void move(int x, int y, int button);
      public abstract void down(int x, int y, int button);
      public abstract void up  (int x, int y, int button);
    }
    
    public static abstract class Key extends Event {
      public abstract void down(int key);
      public abstract void up  (int key);
      public abstract void text(char key);
    }
    
    public static abstract class Click extends Event {
      public abstract void click();
      public abstract void clickDbl();
    }
    
    public static abstract class Scroll extends Event {
      public abstract void scroll(int delta);
    }
    
    public static abstract class Hover extends Event {
      public abstract void enter();
      public abstract void leave();
    }
    
    public static abstract class Focus extends Event {
      public abstract void got();
      public abstract void lost();
    }
  }
}