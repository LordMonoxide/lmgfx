package graphics.shared.gui.controls;

import java.util.LinkedList;

import graphics.shared.gui.Control;
import graphics.shared.gui.GUI;
import graphics.themes.Theme;

public class Scrollbar extends Control<Scrollbar.Events> {
  private Button _up, _down;
  private int _min, _max, _val;
  private Orientation _orientation;
  
  public Scrollbar(GUI gui) {
    this(gui, Theme.getInstance());
  }
  
  public Scrollbar(GUI gui, Theme theme) {
    super(gui, theme);
    
    Control.Events.Scroll scroll = new Control.Events.Scroll() {
      public void scroll(int delta) {
        while(delta > 0) {
          delta -= 120;
          _up.handleMouseUp(0, 0, 0);
        }
        
        while(delta < 0) {
          delta += 120;
          _down.handleMouseUp(0, 0, 0);
        }
      }
    };
    
    _events = new Events(this);
    _events.addScrollHandler(scroll);
    
    _up = new Button(gui, theme);
    _up.events().addScrollHandler(scroll);
    _up.events().addClickHandler(new Events.Click() {
      public void click()    { if(_val > _min) setVal(_val - 1); }
      public void clickDbl() { click(); }
    });
    
    _down = new Button(gui, theme);
    _down.events().addScrollHandler(scroll);
    _down.events().addClickHandler(new Events.Click() {
      public void click()    { if(_val < _max) setVal(_val + 1); }
      public void clickDbl() { click(); }
    });
    
    controls().add(_up);
    controls().add(_down);
    
    theme.create(this);
  }
  
  public int getMin() { return _min; }
  public int getMax() { return _max; }
  public int getVal() { return _val; }
  
  public void setMin(int min) { 
    _min = min;
    if(_min > _max) _min = _max;
    if(_val < _min) setVal(_min);
  }
  
  public void setMax(int max) {
    _max = max;
    if(_max < _min) _max = _min;
    if(_val > _max) setVal(_max);
  }
  
  public void setVal(int val) {
    if(val > _max) val = _max;
    if(val < _min) val = _min;
    if(_val != val) {
      int delta = val - _val;
      _val = val;
      _events.raiseChange(delta);
    }
  }
  
  public Orientation getOrientation() {
    return _orientation;
  }
  
  public void setOrientation(Orientation orientation) {
    _orientation = orientation;
    resize();
  }
  
  protected void resize() {
    switch(_orientation) {
      case VERTICAL:
        _up  .setText("\u25B2");
        _down.setText("\u25BC");
        _up.setWH(_loc[2], _loc[3] / 2);
        _down.setWH(_loc[2], _loc[3] / 2);
        _down.setXY(0, _up.getH());
        break;
        
      case HORIZONTAL:
        _up  .setText("\u25C4");
        _down.setText("\u25BA");
        _up.setWH(_loc[2] / 2, _loc[3]);
        _down.setWH(_loc[2] / 2, _loc[3]);
        _down.setXY(_up.getW(), 0);
    }
  }
  
  public static class Events extends Control.Events {
    private LinkedList<Change> _change = new LinkedList<Change>();
    
    public void addChangeHandler(Change e) { _change.add(e); }
    
    protected Events(Control<?> c) {
      super(c);
    }
    
    protected void raiseChange(int delta) {
      for(Change e : _change) {
        e.setControl(_control);
        e.change(delta);
      }
    }
    
    public static abstract class Change extends Event {
      public abstract void change(int delta);
    }
  }
  
  public static enum Orientation {
    HORIZONTAL, VERTICAL;
  }
}