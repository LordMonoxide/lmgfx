package graphics.shared.gui.controls.compound;

import java.util.ArrayList;
import java.util.LinkedList;

import graphics.shared.gui.Control;
import graphics.shared.gui.ControlList;
import graphics.shared.gui.GUI;
import graphics.shared.gui.controls.Button;
import graphics.shared.gui.controls.Label;
import graphics.shared.gui.controls.Picture;
import graphics.shared.gui.controls.Scrollbar;

public class ScrollPanel extends Control<ScrollPanel.Events> {
  private ArrayList<Item> _item = new ArrayList<Item>();
  
  private Picture   _tabs;
  private Picture   _panel;
  private Scrollbar _scroll;
  private Label     _num;
  
  private Button    _add;
  private Button    _del;
  
  private Item _sel;
  
  public ScrollPanel(GUI gui) {
    super(gui);
    
    Events.Scroll scroll = new Events.Scroll() {
      public void scroll(int delta) {
        if(_scroll.getEnabled()) {
          _scroll.handleMouseWheel(delta);
        }
      }
    };
    
    _events = new Events(this);
    _events.addScrollHandler(scroll);
    
    _add = new Button(gui);
    _add.setText("Add");
    _add.events().addClickHandler(new Events.Click() {
      public void click() { _events.raiseButtonAdd(); }
      public void clickDbl() { click(); }
    });
    
    _del = new Button(gui);
    _del.setText("Delete");
    _del.setX(_add.getW());
    _del.events().addClickHandler(new Events.Click() {
      public void click() { _events.raiseButtonDel(); }
      public void clickDbl() { click(); }
    });
    
    _tabs = new Picture(gui);
    _tabs.setH(_add.getH());
    
    _panel = new Picture(gui);
    _panel.setY(_tabs.getH());
    _panel.setBackColour(new float[] {0.33f, 0.33f, 0.33f, 0.66f});
    _panel.events().addScrollHandler(scroll);
    
    _scroll = new Scrollbar(gui);
    _scroll.setY(_panel.getY());
    _scroll.setH(88);
    _scroll.events().addChangeHandler(new Scrollbar.Events.Change() {
      public void change(int delta) {
        setItem(_scroll.getVal());
      }
    });
    
    _num = new Label(gui);
    _num.setAutoSize(false);
    _num.setText(null);
    
    _add.setX(_scroll.getW());
    _del.setX(_add.getX() + _add.getW());
    _tabs.setX(_del.getX() + _del.getW() + 4);
    _panel.setX(_scroll.getW());
    
    super.Controls().add(_add);
    super.Controls().add(_del);
    super.Controls().add(_tabs);
    super.Controls().add(_panel);
    super.Controls().add(_scroll);
    super.Controls().add(_num);
    
    setWH(400, 100);
    
    _panel.setEnabled(false);
    _scroll.setEnabled(false);
    _del.setEnabled(false);
  }
  
  public ControlList Buttons() {
    return _tabs.Controls();
  }
  
  public ControlList Controls() {
    return _panel.Controls();
  }
  
  public void add(Item item) {
    item._index = _item.size();
    _item.add(item);
    _scroll.setMax(_item.size() - 1);
    
    _panel.setEnabled(true);
    _scroll.setEnabled(true);
    _del.setEnabled(true);
    
    setItem(item);
  }
  
  public void remove() {
    remove(_sel);
  }
  
  public void remove(Item item) {
    remove(item._index);
  }
  
  public void remove(int index) {
    _item.remove(index);
    
    if(_item.size() != 0) {
      for(int i = index; i < _item.size(); i++) {
        _item.get(i)._index--;
      }
      
      if(_scroll.getVal() != _scroll.getMax()) {
        _scroll.setMax(_item.size() - 1);
        setItem(_scroll.getVal());
      } else {
        _scroll.setMax(_item.size() - 1);
      }
    } else {
      _panel.setEnabled(false);
      _scroll.setEnabled(false);
      _del.setEnabled(false);
      setItem(null);
    }
  }
  
  public void clear() {
    _item.clear();
    _panel.setEnabled(false);
    _scroll.setEnabled(false);
    _del.setEnabled(false);
    setItem(null);
  }
  
  public Item getItem() {
    return _sel;
  }
  
  public void setItem(int index) {
    setItem(_item.get(index));
  }
  
  public void setItem(Item item) {
    _sel = item;
    
    if(_sel != null) {
      _scroll.setVal(_sel._index);
      _num.setText(String.valueOf(_sel._index));
    } else {
      _num.setText(null);
    }
    
    _events.raiseSelect(_sel);
  }
  
  public int size() {
    return _item.size();
  }
  
  protected void resize() {
    _tabs.setW(getW() - _tabs.getX());
    _panel.setWH(getW() - _panel.getX(), getH() - _panel.getY());
    _num.setWH(_add.getX(), _scroll.getY());
  }
  
  public static class Item {
    private int _index;
    
    public int getIndex() {
      return _index;
    }
    
    public void setIndex(int index) {
      _index = index;
    }
  }
  
  public static class Events extends Control.Events {
    private LinkedList<Button> _buttonAdd = new LinkedList<Button>();
    private LinkedList<Button> _buttonDel = new LinkedList<Button>();
    private LinkedList<Select> _select    = new LinkedList<Select>();
    
    public void onButtonAdd(Button e) { _buttonAdd.add(e); }
    public void onButtonDel(Button e) { _buttonDel.add(e); }
    public void onSelect   (Select e) { _select   .add(e); }
    
    public Events(Control<?> c) {
      super(c);
    }
    
    public void raiseButtonAdd() {
      for(Button e : _buttonAdd) {
        e.setControl(_control);
        e.event();
      }
    }
    
    public void raiseButtonDel() {
      for(Button e : _buttonDel) {
        e.setControl(_control);
        e.event();
      }
    }
    
    public void raiseSelect(Item item) {
      for(Select e : _select) {
        e.setControl(_control);
        e.event(item);
      }
    }
    
    public static abstract class Button extends Event {
      public abstract void event();
    }
    
    public static abstract class Select extends Event {
      public abstract void event(Item item);
    }
  }
}