package graphics.shared.gui.controls.compound;

import java.util.LinkedList;

import graphics.gl00.Context;
import graphics.shared.fonts.Font;
import graphics.shared.gui.Control;
import graphics.shared.gui.ControlList;
import graphics.shared.gui.GUI;
import graphics.shared.gui.controls.Button;
import graphics.shared.gui.controls.Label;
import graphics.shared.gui.controls.Picture;
import graphics.themes.Theme;

public class Window extends Control<Window.Events> {
  private Font _font = Context.getFonts().getDefault();
  
  private Theme _theme;
  
  private Picture _title;
  private Label   _text;
  private Button  _close;
  private Picture _buttons;
  private Picture _panels;
  
  private LinkedList<Button>  _button = new LinkedList<Button>();
  private LinkedList<Button>  _tab    = new LinkedList<Button>();
  private LinkedList<Picture> _panel  = new LinkedList<Picture>();
  
  private int _index;
  
  private int _mouseDownX;
  private int _mouseDownY;
  
  private Events.Click _tabClick;
  
  public Window(GUI gui) {
    this(gui, Theme.getInstance());
  }
  
  public Window(GUI gui, Theme theme) {
    super(gui);
    
    _events = new Events(this);
    
    _title = new Picture(gui, true);
    _title.events().addMouseHandler(new Events.Mouse() {
      public void up(int x, int y, int button) { }
      public void down(int x, int y, int button) {
        _mouseDownX = x;
        _mouseDownY = y;
      }
      
      public void move(int x, int y, int button) {
        if(button == 0) {
          setXY(_loc[0] + x - _mouseDownX, _loc[1] + y - _mouseDownY);
        }
      }
    });
    
    _text = new Label(gui);
    
    _close = new Button(gui);
    _close.events().addClickHandler(new Events.Click() {
      public void clickDbl() { click(); }
      public void click() {
        if(!_events.raiseClose()) {
          setVisible(false);
        }
      }
    });
    
    _buttons = new Picture(gui);
    
    _title.Controls().add(_text);
    _title.Controls().add(_buttons);
    _title.Controls().add(_close);
    
    _panels = new Picture(gui);
    
    super.Controls().add(_title);
    super.Controls().add(_panels);
    
    _theme = theme;
    _theme.create(this, _title, _text, _close);
    
    _buttons.setH(_close.getH());
    _panels.setY(_title.getH());
    
    _tabClick = new Events.Click() {
      public void clickDbl() { }
      public void click() {
        for(int i = 0; i < _tab.size(); i++) {
          if(getControl() == _tab.get(i)) {
            setTab(i);
            break;
          }
        }
      }
    };
  }
  
  public float getClientW() {
    return _panels.getW();
  }
  
  public float getClientH() {
    return _panels.getH();
  }
  
  public String getText() {
    return _text.getText();
  }
  
  public void setText(String text) {
    _text.setText(text);
    resize();
  }
  
  public ControlList Controls() {
    return Controls(_index);
  }
  
  public ControlList Controls(int index) {
    if(_panel.size() != 0) {
      return _panel.get(index).Controls();
    } else {
      return _panels.Controls();
    }
  }
  
  public Button addButton(String text) {
    Button button = new Button(_gui);
    button.setText(text);
    button.setWH(_font.getW(text) + 8, _close.getH());
    
    for(Button b : _button) {
      b.setX(b.getX() + button.getW());
    }
    
    _buttons.setW(_buttons.getW() + button.getW());
    _buttons.Controls().add(button);
    return button;
  }
  
  public void addTab(String text) {
    Button tab = new Button(_gui);
    Picture panel = new Picture(_gui);
    
    _theme.createWindowTab(tab, panel);
    
    tab.setText(text);
    tab.events().addClickHandler(_tabClick);
    
    if(_tab.size() != 0) {
      tab.setX(_tab.getLast().getX() + _tab.getLast().getW() - 1);
    } else {
      tab.setX(-1);
    }
    
    if(_font.getW(text) + 12 > tab.getW()) {
      tab.setW(_font.getW(text) + 12);
    }
    
    panel.setVisible(false);
    
    _tab.add(tab);
    _panel.add(panel);
    
    _title.Controls().add(tab);
    _panels.Controls().add(panel);
    
    resize();
    
    if(_panel.size() == 1) {
      setTab(0);
    }
  }
  
  public void setTab(int index) {
    _panel.get(_index).setVisible(false);
    
    _index = index;
    
    _panel.get(_index).setVisible(true);
  }
  
  protected void resize() {
    _title.setW(_loc[2]);
    _close.setX(_title.getW() - _close.getW() + 1);
    _buttons.setX(_close.getX() - _buttons.getW());
    
    int x = 0;
    if(_tab.size() != 0) {
      x = (int)(_tab.getLast().getX() + _tab.getLast().getW());
    }
    
    int w = (int)(_title.getW() - _close.getW() - _buttons.getW()) - x;
    _text.setXY((w - _text.getW()) / 2 + x, (_title.getH() - _text.getH()) / 2);
    
    _panels.setWH(_loc[2], _loc[3] - _title.getH());
    
    for(Picture p : _panel) {
      p.setWH(_panels.getW(), _panels.getH());
    }
  }
  
  public static class Events extends Control.Events {
    private LinkedList<Close> _close = new LinkedList<Close>();
    
    public void addCloseHandler(Close e) { _close.add(e); }
    
    public Events(Control<?> c) {
      super(c);
    }
    
    private boolean raiseClose() {
      boolean ret = false;
      
      for(Close e : _close) {
        e.setControl(_control);
        if(e.close()) {
          ret = true;
        }
      }
      
      return ret;
    }
    
    public static abstract class Close extends Event {
      public abstract boolean close();
    }
  }
}