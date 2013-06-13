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

public class Textbox extends Control<Textbox.Events> {
  private Fonts _fonts = Context.getFonts();
  private Font _font = _fonts.getDefault();
  
  private Drawable _caret;
  private Drawable _sel;
  private String   _text;
  private String[] _textSel = new String[3];
  private int      _textY = 0;
  
  private int _caretPos;
  private int _selStart;
  private int _selEnd;
  
  private boolean _editable = true;
  private boolean _numeric;
  
  private boolean _shiftDown;
  private boolean _hover;
  
  private float[] _backColour = {0, 0, 0, 0};
  private float[] _glowColour = {0, 0, 0, 0};
  private float   _caretAlpha;
  private float   _fade;
  
  public Textbox(GUI gui) {
    this(gui, Theme.getInstance());
  }
  
  public Textbox(GUI gui, Theme theme) {
    super(gui, theme);
    
    _events = new Events(this);
    
    _caret = Context.newDrawable();
    _caret.setWH(1, _font.getH());
    _caret.createQuad();
    
    _sel = Context.newDrawable();
    _sel.setColour(new float[] {1, 1, 1, 0.33f});
    _sel.setVisible(false);
    _sel.setH(_font.getH());
    
    _events.addMouseHandler(new Events.Mouse() {
      public void down(int x, int y, int button) {
        setCaretPos(getCharAtX(x));
      }
      
      public void up(int x, int y, int button) {
        
      }
      
      public void move(int x, int y, int button) {
        if(button == 0) {
          int end = getCharAtX(x);
          
          if(end != _caretPos) {
            if(_selStart != _selEnd) {
              if(_caretPos == _selStart) {
                setCaretPos(end, false);
                setSelStart(end);
              } else {
                setCaretPos(end, false);
                setSelEnd(end);
              }
            } else {
              if(end > _caretPos) {
                setCaretPos(end, false);
                setSelEnd(end);
              } else {
                setCaretPos(end, false);
                setSelStart(end);
              }
            }
          }
        }
      }
    });
    
    _events.addHoverHandler(new Events.Hover() {
      public void enter() { _hover = true;  }
      public void leave() { _hover = false; }
    });
    
    _events.addKeyHandler(new Events.Key() {
      public void down(int key) {
        switch(key) {
          case Keyboard.KEY_LSHIFT:
          case Keyboard.KEY_RSHIFT:
            _shiftDown = true;
            break;
            
          case Keyboard.KEY_LEFT:
            if(_caretPos != 0) {
              if(!_shiftDown) {
                setCaretPos(_caretPos - 1);
              } else {
                if(_caretPos == _selStart) {
                  setCaretPos(_caretPos - 1, false);
                  setSelStart(_caretPos);
                } else {
                  setCaretPos(_caretPos - 1, false);
                  setSelEnd(_caretPos);
                }
              }
            }
            break;
            
          case Keyboard.KEY_RIGHT:
            if(_caretPos != _text.length()) {
              if(!_shiftDown) {
                setCaretPos(_caretPos + 1);
              } else {
                if(_caretPos == _selEnd) {
                  setCaretPos(_caretPos + 1, false);
                  setSelEnd(_caretPos);
                } else {
                  setCaretPos(_caretPos + 1, false);
                  setSelStart(_caretPos);
                }
              }
            }
            break;
            
          case Keyboard.KEY_BACK:
            if(_selStart == _selEnd) {
              if(_caretPos != 0) {
                setText(_textSel[0].substring(0, _textSel[0].length() - 1) + _textSel[2], false);
                setCaretPos(_caretPos - 1);
                _events.raiseChange();
              }
            } else {
              setText(_textSel[0] + _textSel[2], false);
              setCaretPos(_textSel[0].length());
              _events.raiseChange();
            }
            break;
            
          case Keyboard.KEY_DELETE:
            if(_selStart == _selEnd) {
              if(_caretPos != _text.length()) {
                setText(_textSel[0] + _textSel[2].substring(1), false);
                setCaretPos(_caretPos); // Force update
                _events.raiseChange();
              }
            } else {
              setText(_textSel[0] + _textSel[2], false);
              setCaretPos(_textSel[0].length());
              _events.raiseChange();
            }
            break;
        }
      }
      
      public void up(int key) {
        switch(key) {
          case Keyboard.KEY_LSHIFT:
          case Keyboard.KEY_RSHIFT:
            _shiftDown = false;
        }
      }
      
      public void text(char key) {
        if(_editable) {
          if(_numeric) {
            if(key < 0x30 || key > 0x39) {
              return;
            }
          }
          
          if(_text != null) {
            setText(_textSel[0] + key + _textSel[2], false);
          } else {
            setText(Character.toString(key), false);
          }
          
          setCaretPos(_selStart + 1);
          _events.raiseChange();
        }
      }
    });
    
    _events.addFocusHandler(new Events.Focus() {
      public void lost() { }
      public void got() {
        _caretAlpha = 1;
      }
    });
    
    theme.create(this);
  }
  
  public void setBackColour(float[] c) {
    _backColour = c;
    
    if(!_hover) {
      _background.setColour(c);
      _background.createQuad();
    }
  }
  
  public float[] getGlowColour() {
    return _glowColour;
  }
  
  public void setGlowColour(float[] c) {
    _glowColour = c;
  }
  
  public void setForeColour(float[] c) {
    super.setForeColour(c);
    float[] caret = {c[0], c[1], c[2], c[3]};
    _caret.setColour(caret);
    _caret.createQuad();
  }
  
  public boolean getEditable() {
    return _editable;
  }
  
  public void setEditable(boolean editable) {
    _editable = editable;
  }
  
  public boolean getNumeric() {
    return _numeric;
  }
  
  public void setNumeric(boolean numeric) {
    _numeric = numeric;
  }
  
  protected void resize() {
    _caret.setY((_loc[3] - _caret.getH()) / 2);
    _sel.setY((_loc[3] - _sel.getH()) / 2);
    
    _textY = (int)_caret.getY();
  }
  
  public int getCaretPos() {
    return _caretPos;
  }
  
  public void setCaretPos(int caretPos) {
    setCaretPos(caretPos, true);
  }
  
  private void setCaretPos(int caretPos, boolean cancelSelection) {
    _caretPos = caretPos;
    _caretAlpha = 1;
    
    if(_text != null) {
      _caret.setX(_font.getW(_text.substring(0, _caretPos)));
    } else {
      _caret.setX(0);
    }
    
    if(cancelSelection) {
      _selStart = _caretPos;
      _selEnd = _caretPos;
      updateSel();
    }
  }
  
  private void setSelStart(int selStart) {
    _selStart = selStart;
    updateSel();
  }
  
  private void setSelEnd(int selEnd) {
    _selEnd = selEnd;
    updateSel();
  }
  
  private void updateSel() {
    if(_text != null) {
      _textSel[0] = _text.substring(0, _selStart);
      _textSel[1] = _text.substring(_selStart, _selEnd);
      _textSel[2] = _text.substring(_selEnd);
    } else {
      _textSel[0] = null;
      _textSel[1] = null;
      _textSel[2] = null;
    }
    
    if(_selStart != _selEnd) {
      _sel.setX(_font.getW(_textSel[0]));
      _sel.setW(_font.getW(_textSel[1]));
      _sel.createQuad();
      _sel.setVisible(true);
    } else {
      _sel.setVisible(false);
    }
  }
  
  public String getText() {
    return _text;
  }
  
  public void setText(String text) {
    setText(text, true);
  }
  
  private void setText(String text, boolean moveCursor) {
    if(_numeric) {
      if(text == null || text.length() == 0) {
        text = "0";
      }
    }
    
    _text = text;
    
    if(moveCursor) {
      if(_text != null) {
        setCaretPos(_text.length());
      } else {
        setCaretPos(0);
      }
    }
  }
  
  public int getCharAtX(float x) {
    if(_text == null) return 0;
    
    int w = 0, w2;
    
    for(int i = 0; i < _text.length(); i++) {
      w2 = _font.getW(_text.substring(i, i + 1));
      if(w + w2 / 2 > x) return i;
      w += w2;
    }
    
    return _text.length();
  }
  
  public void draw() {
    if(drawBegin()) {
      if(_focus) {
        _sel.draw();
      }
      
      _font.draw(0, _textY, _text, _foreColour);
      
      if(_focus) {
        _caret.draw();
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
    
    if(_focus) {
      _caretAlpha -= 0.01f;
      if(_caretAlpha < 0) {
        _caretAlpha = 1;
      }
      
      float[] c = _caret.getColour();
      c[3] = _caretAlpha;
      _caret.setColour(c);
      _caret.createQuad();
    }
  }
  
  public static class Events extends Control.Events {
    private LinkedList<Change> _change = new LinkedList<Change>();
    
    public void addChangeHandler(Change e) { _change.add(e); }
    
    protected Events(Control<?> c) {
      super(c);
    }
    
    public void raiseChange() {
      for(Change e : _change) {
        e.setControl(_control);
        e.change();
      }
    }
    
    public static abstract class Change extends Event {
      public abstract void change();
    }
  }
}