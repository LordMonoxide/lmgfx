package graphics.shared.gui;

public class ControlList {
  private Control<?> _parent;
  private Control<?> _first;
  private Control<?> _last;
  private int _size;
  
  protected ControlList(Control<?> parent) {
    _parent = parent;
  }
  
  public int size() {
    return _size;
  }
  
  public void add(Control<?> control) {
    control.setParent(_parent);
    
    if(_first != null) {
      control.setControlNext(null);
      control.setControlPrev(_first);
      _first.setControlNext(control);
      _first = control;
    } else {
      control.setControlNext(null);
      control.setControlPrev(null);
      _first = control;
      _last  = control;
    }
    
    _size++;
  }
  
  public void remove(Control<?> control) {
    Control<?> c = control.getControlNext();
    if(c != null) {
      c.setControlPrev(control.getControlPrev());
      
      if(c.getControlPrev() == null) {
        _last = c;
      }
    } else {
      c = control.getControlPrev();
      if(c != null) {
        c.setControlNext(null);
      }
      _first = c;
    }
    
    c = control.getControlPrev();
    if(c != null) {
      c.setControlNext(control.getControlNext());
      
      if(c.getControlNext() == null) {
        _first = c;
      }
    } else {
      c = control.getControlNext();
      if(c != null) {
        c.setControlPrev(null);
      }
      _last = c;
    }
    
    _size--;
  }
  
  public Control<?> getFirst() {
    return _first;
  }
  
  public Control<?> getLast() {
    return _last;
  }
  
  public void killFocus() {
    Control<?> c = _last;
    
    while(c != null) {
      c.setFocus(false);
      c.Controls().killFocus();
      c = c._controlNext;
    }
  }
  
  public void draw() {
    if(_last != null) {
      _last.draw();
    }
  }
  
  public void logic() {
    if(_last != null) {
      _last.logicControl();
    }
  }
  
  public void drawSelect() {
    if(_last != null) {
      _last.drawSelect();
    }
  }
  
  public Control<?> getSelectControl(int[] colour) {
    if(_last != null) {
      return _last.getSelectControl(colour);
    } else {
      return null;
    }
  }
}