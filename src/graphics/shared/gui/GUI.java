package graphics.shared.gui;

import org.lwjgl.input.Keyboard;

import graphics.gl00.Context;
import graphics.gl00.Matrix;
import graphics.shared.textures.Textures;

public abstract class GUI {
  protected Matrix _matrix = Context.getMatrix();
  protected Textures _textures = Context.getTextures();
  
  private boolean _visible = true;
  
  protected Context _context;
  private Control<?> _control;
  private Control<?> _focus;
  
  private Control<?> _keyDownControl;
  private Control<?> _selectControl;
  private Control<?> _selectControlMove;
  private     int _selectButton = -1;
  private     int _mouseX, _mouseY;
  
  private boolean _forceSelect;
  
  public GUI() {
    _context = Context.getContext();
    _control = new Control<Control.Events>(this);
    _control.setAcceptsFocus(false);
  }
  
  public boolean getVisible() {
    return _visible;
  }
  
  public void setVisible(boolean visible) {
    _visible = visible;
  }
  
  public Context getContext() {
    return _context;
  }
  
  protected Control<?> getFocus() {
    return _focus;
  }
  
  public ControlList Controls() {
    return _control.Controls();
  }
  
  public void setFocus(Control<?> control) {
    if(_selectControl == _focus) _selectControl = null;
    
    if(_focus != null) {
      Control<?> focus = _focus;
      _focus = null;
      focus.setFocus(false);
    }
    
    _focus = control;
  }
  
  public void setWH(int w, int h) {
    _control.setWH(w, h);
  }
  
  public abstract void load();
  public abstract void destroy();
  public abstract void resize();
  public abstract void draw();
  public abstract boolean logic();
  
  public final boolean logicGUI() {
    Boolean b = logic();
    _control.logicControl();
    return b;
  }
  
  public final void drawGUI() {
    draw();
    drawControls();
  }
  
  protected final void drawSelect() {
    _context.clear(new float[] {0, 0, 0, 1});
    _control.drawSelect();
  }
  
  protected final void drawControls() {
    if(_visible) {
      _matrix.push();
      _matrix.reset();
      
      if(!_forceSelect) {
        _control.draw();
      } else {
        drawSelect();
      }
      
      _matrix.pop();
    }
  }
  
  public void push() {
    _context.GUI().push(this);
  }
  
  public void pop() {
    _context.GUI().pop(this);
  }
  
  private final Control<?> getSelectControl(int[] colour) {
    if(_control != null)
      return _control.getSelectControl(colour);
    else
      return null;
  }
  
  protected final int getAllX(Control<?> control) {
    int x = (int)control.getX();
    
    if(control.getParent() != null) {
      x += getAllX(control.getParent());
    }
    
    return x;
  }
  
  protected final int getAllY(Control<?> control) {
    int y = (int)control.getY();
    
    if(control.getParent() != null) {
      y += getAllY(control.getParent());
    }
    
    return y;
  }
  
  public final boolean mouseDown(int x, int y, int button) {
    _selectButton = button;
    
    drawSelect();
    
    int[] pixel = _context.getPixel(x, y);
    
    if(pixel[0] != 0 || pixel[1] != 0  || pixel[2] != 0) {
      _selectControl = getSelectControl(pixel);
      
      if(_selectControl != null) {
        if(_selectControl.getAcceptsFocus()) {
          _selectControl.setFocus(true);
        }
        
        _selectControl.handleMouseDown(x - getAllX(_selectControl), y - getAllY(_selectControl), button);
      } else {
        System.err.println("Found no controls of this colour");
      }
    }
    
    return handleMouseDown(x, y, button);
  }
  
  public final boolean mouseUp(int x, int y, int button) {
    _selectButton = -1;
    
    if(_selectControl != null) {
      _selectControl.handleMouseUp(x - getAllX(_selectControl), y - getAllY(_selectControl), button);
      _selectControl = null;
      return true;
    }
    
    return handleMouseUp(x, y, button);
  }
  
  public final boolean mouseMove(int x, int y) {
    _mouseX = x;
    _mouseY = y;
    
    if(_selectControl != null) {
      _selectControl.handleMouseMove(x - getAllX(_selectControl), y - getAllY(_selectControl), _selectButton);
    } else {
      drawSelect();
      
      int[] pixel = _context.getPixel(x, y);
      
      if(pixel[0] != 0 || pixel[1] != 0 || pixel[2] != 0) {
        _selectControl = getSelectControl(pixel);
        
        if(_selectControl != _selectControlMove) {
          if(_selectControlMove != null) _selectControlMove.handleMouseLeave();
          if(_selectControl     != null) _selectControl.handleMouseEnter();
          _selectControlMove = _selectControl;
        }
        
        if(_selectControl != null) {
          _selectControl.handleMouseMove(x - getAllX(_selectControl), y - getAllY(_selectControl), _selectButton);
          _selectControl = null;
        }
      } else {
        if(_selectControlMove != null) {
          _selectControlMove.handleMouseLeave();
          _selectControlMove = null;
        }
      }
    }
    
    return handleMouseMove(x, y, _selectButton);
  }
  
  public final boolean mouseWheel(int delta) {
    drawSelect();
    
    int[] pixel = _context.getPixel(_mouseX, _mouseY);
    
    if(pixel[0] != 0 || pixel[1] != 0  || pixel[2] != 0) {
      _selectControl = getSelectControl(pixel);
      
      if(_selectControl != null) {
        _selectControl.handleMouseWheel(delta);
        _selectControl = null;
      }
    }
    
    return handleMouseWheel(delta);
  }
  
  public final boolean keyDown(int key) {
    if(key == Keyboard.KEY_F12) {
      _forceSelect = !_forceSelect;
      
      if(_forceSelect) {
        System.out.println("Switching GUI render mode to select");
      } else {
        System.out.println("Switching GUI render mode to normal");
      }
    }
    
    if(_focus != null) {
      _keyDownControl = _focus;
      _focus.handleKeyDown(key);
    }
    
    return handleKeyDown(key);
  }
  
  public final boolean keyUp(int key) {
    if(_keyDownControl != null) {
      _keyDownControl.handleKeyUp(key);
    }
    
    return handleKeyUp(key);
  }
  
  public final boolean charDown(char key) {
    if(_focus != null) {
      _focus.handleCharDown(key);
    }
    
    return handleCharDown(key);
  }
  
  public boolean handleMouseDown (int x, int y, int button) { return false; }
  public boolean handleMouseUp   (int x, int y, int button) { return false; }
  public boolean handleMouseMove (int x, int y, int button) { return false; }
  public boolean handleMouseWheel(int delta)                { return false; }
  public boolean handleKeyDown   (int key)  { return false; }
  public boolean handleKeyUp     (int key)  { return false; }
  public boolean handleCharDown  (char key) { return false; }
}