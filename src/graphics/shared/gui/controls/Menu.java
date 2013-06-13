package graphics.shared.gui.controls;

import java.util.ArrayList;
import java.util.LinkedList;

import graphics.gl00.Context;
import graphics.gl00.Drawable;
import graphics.shared.fonts.Font;
import graphics.shared.fonts.Fonts;
import graphics.shared.gui.Control;
import graphics.shared.gui.GUI;
import graphics.themes.Theme;

public class Menu extends Control<Menu.Events> {
  private Fonts _fonts = Context.getFonts();
  private Font _font = _fonts.getDefault();
  
  private MenuGUI _drop;
  
  private Picture _picDrop;
  
  private ArrayList<String> _text = new ArrayList<String>();
  private int _textIndex = -1;
  
  private Drawable _selected;
  private int _selectedIndex;
  
  public Menu(GUI gui) {
    this(gui, Theme.getInstance());
  }
  
  public Menu(GUI gui, Theme theme) {
    super(gui, theme);
    
    _events = new Events(this);
    
    _picDrop = new Picture(gui, true);
    _picDrop.events().addDrawHandler(new Events.Draw() {
      public void draw() {
        if(_selectedIndex != -1) {
          _selected.draw();
        }
        
        int y = 0;
        for(String s : _text) {
          _font.draw(0, y, s, _foreColour);
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
        _textIndex = _selectedIndex;
        _events.raiseSelect(_textIndex);
        hide();
      }
    });
    
    _selected = Context.newDrawable();
    _selected.setColour(new float[] {1, 1, 1, 0.33f});
    _selected.setH(_font.getH());
    
    _drop = new MenuGUI();
    _drop.load();
    _drop.Controls().add(_picDrop);
    
    setVisible(false);
    
    theme.create(this);
  }
  
  protected void resize() {
    _picDrop.setW(_loc[2]);
    _selected.setW(_loc[2]);
    _selected.createQuad();
  }
  
  public void setBackColour(float[] c) {
    super.setBackColour(c);
    _picDrop.setBackColour(c);
  }
  
  public void setBorderColour(float[] c) {
    super.setBorderColour(c);
    _picDrop.setBorderColour(c);
  }
  
  public void show(int x, int y) {
    setXY(x, y);
    _picDrop.setXY(getAllX(), getAllY());
    setVisible(true);
    _drop.push();
  }
  
  public void hide() {
    setVisible(false);
    _drop.pop();
  }
  
  public void add(String text) {
    _text.add(text);
    _picDrop.setH(_text.size() * _font.getH());
    setH(_text.size() * _font.getH());
  }
  
  public String get() {
    return get(_textIndex);
  }
  
  public String get(int index) {
    return _text.get(index);
  }
  
  public int getSize() {
    return _text.size();
  }
  
  public static class Events extends Control.Events {
    private LinkedList<Select> _select = new LinkedList<Select>();
    
    public void addSelectHandler(Select e) { _select.add(e); }
    
    protected Events(Control<?> c) {
      super(c);
    }
    
    public void raiseSelect(int index) {
      for(Select e : _select) {
        e.setControl(_control);
        e.select(index);
      }
    }
    
    public static abstract class Select extends Event {
      public abstract void select(int index);
    }
  }
  
  private class MenuGUI extends GUI {
    public void load() { }
    public void destroy() { }
    public void resize() { }
    public void draw() { }
    public boolean logic() { return false; }
    
    public boolean handleMouseDown(int x, int y, int button) {
      return true;
    }
    
    public boolean handleMouseUp(int x, int y, int button) {
      hide();
      return true;
    }
    
    public boolean handleMouseMove(int x, int y, int button) {
      return true;
    }
  }
}