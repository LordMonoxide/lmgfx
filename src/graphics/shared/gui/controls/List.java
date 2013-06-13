package graphics.shared.gui.controls;

import java.util.LinkedList;

import org.lwjgl.input.Keyboard;

import graphics.gl00.Context;
import graphics.gl00.Drawable;
import graphics.shared.gui.Control;
import graphics.shared.gui.GUI;
import graphics.shared.textures.Texture;
import graphics.shared.textures.Textures;
import graphics.themes.Theme;

public class List extends Control<List.ListItem.Events> {
  private LinkedList<ListItem> _items = new LinkedList<ListItem>();
  private ListItem _selected;
  private Scrollbar _scroll;
  private int _start, _length;
  
  public List(GUI gui) {
    this(gui, Theme.getInstance());
  }
  
  public List(GUI gui, Theme theme) {
    super(gui, theme);
    
    _events = new List.ListItem.Events(this);
    
    _scroll = new Scrollbar(gui);
    _scroll.events().addChangeHandler(new Scrollbar.Events.Change() {
      public void change(int delta) {
        _start -= delta;
      }
    });
    
    _scroll.setMax(0);
    
    Controls().add(_scroll);
    
    theme.create(this);
  }
  
  protected void resize() {
    for(ListItem l : _items) {
      l.setW(_loc[2] - 16);
    }
    
    _scroll.setH(_loc[3]);
    _scroll.setX(_loc[2] - 16);
    
    _length = (int)_loc[3] / 40;
  }
  
  public ListItem getSelected() {
    return _selected;
  }
  
  public int size() {
    return _items.size();
  }
  
  public ListItem addItem(String text, Texture icon) {
    ListItem l = new ListItem(_gui);
    l.setText(text);
    l.setIcon(icon);
    return addItem(l);
  }
  
  public ListItem addItem(final ListItem l) {
    final List _this = this;
    
    l._index = _items.size();
    l.setXYWH(0, _items.size() * 41, _loc[2] - 16, 40);
    l.events().addSelectHandler(new ListItem.Events.Select() {
      public void select() {
        handleSelect(l);
      }
    });
    
    l.events().addKeyHandler(new Control.Events.Key() {
      public void down( int key) { _this.events().raiseKeyDown(key); }
      public void up  ( int key) { _this.events().raiseKeyUp(key); }
      public void text(char key) { _this.events().raiseKeyText(key); }
    });
    
    _items.add(l);
    _scroll.setMax(_items.size() - _length);
    _scroll.setVal(_scroll.getMax());
    _start = _scroll.getMax() - _scroll.getVal();
    
    if(_items.size() == 1) {
      _selected = l;
      _selected.setFocus(true);
    }
    
    return l;
  }
  
  public void removeItem(ListItem l) {
    _items.remove(l);
    _scroll.setMax(_scroll.getMax() - 1);
  }
  
  public void draw() {
    if(drawBegin()) {
      switch(_items.size()) {
        case 0:
          break;
          
        case 2:
          _items.get(1).draw();
        case 1:
          _items.get(0).draw();
          break;
          
        default:
          _matrix.push();
          _matrix.translate(0, -_start * 41);
          for(int i = _start; i < _start + (_length > _items.size() ? _items.size() : _length); i++) {
            _items.get(i).draw();
          }
          _matrix.pop();
      }
    }
    
    drawEnd();
  }
  
  protected void drawEnd() {
    if(_visible) {
      _events.raiseDraw();
      
      _controlList.draw();
      _matrix.pop();
    }
    
    if(_controlNext != null) {
      _controlNext.draw();
    }
  }
  
  public void drawSelect() {
    if(drawBegin()) {
      if(_selBox != null)
        _selBox.draw();
      
      switch(_items.size()) {
        case 0:
          break;
          
        case 2:
          _items.get(1).drawSelect();
        case 1:
          _items.get(0).drawSelect();
          break;
          
        default:
          _matrix.push();
          _matrix.translate(0, -_start * 41);
          for(int i = _start; i < _start + (_length > _items.size() ? _items.size() : _length); i++) {
            _items.get(i).drawSelect();
          }
          _matrix.pop();
      }
      
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
      
      if(control == null) {
        switch(_items.size()) {
          case 0:
            break;
            
          case 1:
            control = _items.get(0).getSelectControl(colour);
            break;
            
          case 2:
            control = _items.get(0).getSelectControl(colour);
            
            if(control == null) {
              control = _items.get(1).getSelectControl(colour);
            }
            break;
            
          default:
            for(int i = _start; i < _start + (_length > _items.size() ? _items.size() : _length); i++) {
              if((control = _items.get(i).getSelectControl(colour)) != null) {
                break;
              }
            }
        }
      }
      
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
  
  public void setFocus(boolean focus) {
    if(_selected != null) {
      _selected.setFocus(focus);
    } else {
      super.setFocus(focus);
    }
  }
  
  private void handleSelect(ListItem l) {
    _selected = l;
    _events.raiseSelect(l);
  }
  
  public static class ListItem extends Control<ListItem.Events> {
    private Textures _textures = Context.getTextures();
    private Drawable _borderT = Context.newDrawable();
    private Drawable _borderB = Context.newDrawable();
    private Drawable _background = Context.newDrawable();
    
    private Picture _icon;
    private Label _text;
    protected int _index;
    
    protected ListItem(GUI gui) {
      super(gui);
      
      _events = new Events(this);
      
      _borderT.setTexture(_textures.getTexture("gui/textbox.png"));
      _borderT.setH(1);
      _borderT.setTXYWH(5, 21, 1, 1);
      
      _borderB.setTexture(_textures.getTexture("gui/textbox.png"));
      _borderB.setH(2);
      _borderB.setTXYWH(12, 3, 1, 2);
      
      _background.setColour(new float[] {1, 1, 1, 0.5f});
      _background.setVisible(false);
      
      _icon = new Picture(gui);
      _icon.setWH(40, 40);
      
      _text = new Label(gui);
      _text.setAutoSize(false);
      _text.setX(56);
      
      setWH(120, 40);
    }
    
    public void setW(float w) {
      super.setW(w);
      _text.setW(w);
      _background.setW(w);
      _borderT.setW(w);
      _borderB.setW(w);
      _borderT.createQuad();
      _borderB.createQuad();
      _background.createQuad();
    }
    
    public void setH(float h) {
      super.setH(h);
      _text.setH(h);
      _background.setH(h);
      _borderB.setY(h - _borderB.getH());
      _borderB.createQuad();
      _background.createQuad();
    }
    
    public void setWH(float w, float h) {
      super.setWH(w, h);
      _text.setWH(w, h);
      _background.setWH(w, h);
      _borderT.setW(w);
      _borderB.setW(w);
      _borderB.setY(0);
      _borderT.createQuad();
      _borderB.createQuad();
      _borderB.setY(h - _borderB.getH());
      _background.createQuad();
    }
    
    public void setXYWH(float x, float y, float w, float h) {
      super.setXYWH(x, y, w, h);
      _text.setWH(w, h);
      _background.setWH(w, h);
      _borderT.setW(w);
      _borderB.setW(w);
      _borderB.setY(0);
      _borderT.createQuad();
      _borderB.createQuad();
      _borderB.setY(h - _borderB.getH());
      _background.createQuad();
    }
    
    public void setXYWH(float[] loc) {
      super.setXYWH(loc);
      _text.setWH(loc[2], loc[3]);
      _background.setWH(loc[2], loc[3]);
      _borderT.setW(loc[2]);
      _borderB.setW(loc[2]);
      _borderB.setY(0);
      _borderT.createQuad();
      _borderB.createQuad();
      _borderB.setY(loc[3] - _borderB.getH());
      _background.createQuad();
    }
    
    public Texture getIcon() {
      return _icon.getTexture();
    }
    
    public void setIcon(Texture icon) {
      _icon.setTexture(icon);
      _icon.setWH(40, 40);
    }
    
    public String getText() {
      return _text.getText();
    }
    
    public void setText(String text) {
      _text.setText(text);
    }
    
    public int getIndex() {
      return _index;
    }
    
    public void draw() {
      if(drawBegin()) {
        _icon.draw();
        _text.draw();
        _background.draw();
        _borderT.draw();
        _borderB.draw();
      }
      
      drawEnd();
    }
    
    public void handleKeyDown(int key) {
      switch(key) {
        case Keyboard.KEY_UP:
          if(_controlPrev != null) {
            _controlPrev.setFocus(true);
          }
          break;
          
        case Keyboard.KEY_DOWN:
          if(_controlNext != null) {
            _controlNext.setFocus(true);
          }
      }
      
      super.handleKeyDown(key);
    }
    
    public void handleGotFocus() {
      handleSelect();
      _background.setVisible(true);
      super.handleGotFocus();
    }
    
    public void handleLostFocus() {
      _background.setVisible(false);
      super.handleLostFocus();
    }
    
    public void handleSelect() {
      _events.raiseSelect(this);
    }
    
    public static class Events extends Control.Events {
      private LinkedList<Select> _select = new LinkedList<Select>();
      
      public void addSelectHandler(Select e) { _select.add(e); }
      
      protected Events(Control<?> c) {
        super(c);
      }
      
      public void raiseSelect(ListItem i) {
        for(Select e : _select) {
          e.setControl(i);
          e.select();
        }
      }
      
      public static abstract class Select extends Event {
        public abstract void select();
      }
    }
  }
}