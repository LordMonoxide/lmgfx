package graphics.shared.gui.controls;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import graphics.gl00.Context;
import graphics.gl00.Drawable;
import graphics.shared.fonts.Font;
import graphics.shared.fonts.Fonts;
import graphics.shared.gui.Control;
import graphics.shared.gui.GUI;
import graphics.themes.Theme;

public class Dropdown extends Control<Dropdown.Events> implements Iterable<Dropdown.Item> {
  private Fonts _fonts = Context.getFonts();
  private Font _font = _fonts.getDefault();
  
  private DropdownGUI _drop;
  
  private Picture _picDrop;
  private Button _btnDrop;
  
  private ArrayList<Item> _text = new ArrayList<Item>();
  private int _textIndex = -1;
  private int _textY = 0;
  
  private Drawable _selected;
  private int _selectedIndex;
  
  private boolean _hover;
  
  private float[] _backColour = {0, 0, 0, 0};
  private float[] _glowColour = {0, 0, 0, 0};
  private float   _fade;
  
  public Dropdown(GUI gui) {
    this(gui, Theme.getInstance());
  }
  
  public Dropdown(GUI gui, Theme theme) {
    super(gui, theme);
    
    _events = new Events(this);
    _events.addClickHandler(new Events.Click() {
      public void click() {
        _btnDrop.handleMouseDown(0, 0, 0);
        _btnDrop.handleMouseUp(0, 0, 0);
      }
      
      public void clickDbl() {
        click();
      }
    });
    
    _events.addHoverHandler(new Events.Hover() {
      public void enter() { _hover = true;  }
      public void leave() { _hover = false; }
    });
    
    _btnDrop = new Button(gui, theme);
    _btnDrop.setText("\u25BC");
    _btnDrop.events().addClickHandler(new Events.Click() {
      public void clickDbl() { }
      public void click() {
        if(!_picDrop.getVisible()) {
          if(_text.size() != 0) {
            _selectedIndex = _textIndex;
            _selected.setY(_selectedIndex * _font.getH());
            _picDrop.setVisible(true);
            _picDrop.setXY(getAllX(), getAllY() + getH());
            _drop.push();
          }
        } else {
          _picDrop.setVisible(false);
          _drop.pop();
        }
      }
    });
    
    _picDrop = new Picture(gui, true);
    _picDrop.setVisible(false);
    _picDrop.events().addDrawHandler(new Events.Draw() {
      public void draw() {
        if(_selectedIndex != -1) {
          _selected.draw();
        }
        
        int y = 0;
        for(Item d : _text) {
          _font.draw(0, y, d._text, _foreColour);
          y += _font.getH();
        }
      }
    });
    
    _picDrop.events().addMouseHandler(new Events.Mouse() {
      public void down(int x, int y, int button) { }
      public void up(int x, int y, int button) { }
      public void move(int x, int y, int button) {
        _selectedIndex = (y - 1) / _font.getH();
        _selected.setY(_selectedIndex * _font.getH());
      }
    });
    
    _picDrop.events().addClickHandler(new Events.Click() {
      public void clickDbl() { }
      public void click() {
        _btnDrop.handleMouseDown(0, 0, 0);
        _btnDrop.handleMouseUp(0, 0, 0);
        setSeletected(_selectedIndex);
        _events.raiseSelect(_textIndex != -1 ? _text.get(_textIndex) : null);
      }
    });
    
    Controls().add(_btnDrop);
    
    _selected = Context.newDrawable();
    _selected.setColour(new float[] {1, 1, 1, 0.33f});
    _selected.setH(_font.getH());
    
    _drop = new DropdownGUI(_picDrop);
    _drop.load();
    _drop.Controls().add(_picDrop);
    
    theme.create(this);
  }
  
  public void clear() {
    _text.clear();
    _textIndex = -1;
    _selected = null;
    _selectedIndex = -1;
  }
  
  public void add(Item item) {
    _text.add(item);
    _picDrop.setH(_text.size() * _font.getH());
  }
  
  public Item get() {
    return get(_textIndex);
  }
  
  public Item get(int index) {
    return index != -1 ? _text.get(index) : null;
  }
  
  public int getSize() {
    return _text.size();
  }
  
  public int getSelected() {
    return _textIndex;
  }
  
  public void setSeletected(int index) {
    _textIndex = index;
  }
  
  public void setBackColour(float[] c) {
    _backColour = c;
    
    if(!_hover) {
      _background.setColour(c);
      _background.createQuad();
    }
    
    _picDrop.setBackColour(_backColour);
  }
  
  public float[] getGlowColour() {
    return _glowColour;
  }
  
  public void setGlowColour(float[] c) {
    _glowColour = c;
  }
  
  public void setBorderColour(float[] c) {
    super.setBorderColour(c);
    _picDrop.setBorderColour(c);
  }
  
  protected void resize() {
    _btnDrop.setWH(_loc[3], _loc[3]);
    _btnDrop.setX(_loc[2] - _btnDrop.getW());
    
    _picDrop.setY(_loc[3]);
    _picDrop.setW(_loc[2]);
    
    _selected.setW(_loc[2]);
    _selected.createQuad();
    
    _textY = (int)((_loc[3] - _font.getH()) / 2);
  }
  
  public void draw() {
    if(drawBegin()) {
      if(_textIndex != -1) {
        _font.draw(0, _textY, _text.get(_textIndex)._text, _foreColour);
      }
    }
    
    drawEnd();
  }
  
  public void logic() {
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
  }
  
  public Iterator<Item> iterator() {
    return _text.iterator();
  }
  
  public static class Item {
    private String _text;
    
    public Item(String text) {
      _text = text;
    }
    
    public String getText() {
      return _text;
    }
    
    public void setText(String text) {
      _text = text;
    }
  }
  
  public static class Events extends Control.Events {
    private LinkedList<Select> _select = new LinkedList<Select>();
    
    public void addSelectHandler(Select e) { _select.add(e); }
    
    protected Events(Control<?> c) {
      super(c);
    }
    
    public void raiseSelect(Item l) {
      for(Select e : _select) {
        e.setControl(_control);
        e.select(l);
      }
    }
    
    public static abstract class Select extends Event {
      public abstract void select(Item item);
    }
  }
  
  private class DropdownGUI extends GUI {
    private Picture _drop;
    
    public DropdownGUI(Picture p) {
      _drop = p;
    }
    
    public void load() {
      
    }
    
    public void destroy() {
      
    }
    
    public void resize() {
      
    }
    
    public void draw() {
      
    }
    
    public boolean logic() {
      return false;
    }
    
    public boolean handleMouseDown(int x, int y, int button) {
      return true;
    }
    
    public boolean handleMouseUp(int x, int y, int button) {
      _drop.setVisible(false);
      pop();
      return true;
    }
    
    public boolean handleMouseMove(int x, int y, int button) {
      return true;
    }
  }
}