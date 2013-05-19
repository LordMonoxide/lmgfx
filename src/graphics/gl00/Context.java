package graphics.gl00;

import java.nio.ByteBuffer;

import graphics.shared.fonts.Fonts;
import graphics.shared.gui.GUIs;
import graphics.shared.textures.Textures;
import graphics.util.Logger;
import graphics.util.Time;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Controller;
import org.lwjgl.input.Controllers;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

public abstract class Context {
  protected static Context  _context;
  protected static Matrix   _matrix;
  protected static Textures _textures;
  protected static Fonts    _fonts;
  protected static Class<? extends Vertex>   _vertex;
  protected static Class<? extends Drawable> _drawable;
  protected static Class<? extends Scalable> _scalable;
  
  private static Game  _game;
  
  public static final Context  getContext()  { return _context;  }
  public static final Matrix   getMatrix()   { return _matrix;   }
  public static final Textures getTextures() { return _textures; }
  public static final Fonts    getFonts()    { return _fonts;    }
  public static final Game     getGame()     { return _game;     }
  
  public static final Vertex newVertex() {
    try {
      return _vertex.newInstance();
    } catch(Exception e) {
      e.printStackTrace();
      return null;
    }
  }
  
  public static final Drawable newDrawable() {
    try {
      return _drawable.newInstance();
    } catch(Exception e) {
      e.printStackTrace();
      return null;
    }
  }
  
  public static final Scalable newScalable() {
    try {
      return _scalable.newInstance();
    } catch(Exception e) {
      e.printStackTrace();
      return null;
    }
  }
  
  protected GUIs _gui = new GUIs();
  protected ContextLogic _logic = new ContextLogic(_gui);
  
  private float _cameraX, _cameraY;
  
  private int _mouseX = 0;
  private int _mouseY = 0;
  
  private int _w = 1280, _h = 720;
  private int _fpsTarget = 60;
  
  private float[] _backColour = {1, 1, 1, 1};
  private boolean _backColourUpdate;
  
  private int[] _selectColour = {1, 0, 0, 255};
  
  private boolean _running;
  
  private double _fpsTimeout = Time.HzToTicks(1);
  private double _fpsTimer;
  private    int _fpsCount;
  private    int _fps;
  
  public  float   getCameraX()    { return _cameraX;    }
  public  float   getCameraY()    { return _cameraY;    }
  public    int   getFPS()        { return _fps;        }
  public    int   getLogicFPS()   { return _logic.getFPS(); }
  public String   getTitle()      { return Display.getTitle(); }
  public boolean  getResizable()  { return Display.isResizable(); }
  public    int   getW()          { return _w;          }
  public    int   getH()          { return _h;          }
  public    int   getFPSTarget()  { return _fpsTarget;  }
  public  float[] getBackColour() { return _backColour; }
  public void setCameraX(float cameraX) { _cameraX = cameraX; }
  public void setCameraY(float cameraY) { _cameraY = cameraY; }
  public void setTitle(String title)    { Display.setTitle(title); }
  public void setResizable(boolean resizable) { Display.setResizable(resizable); }
  public void setW(int w)               { setWH(w, _h);     }
  public void setH(int h)               { setWH(_w, h);     }
  public void setFPSTarget(int fps)     { _fpsTarget = fps; }
  public void setBackColour(float[] c)  { _backColour = c; _backColourUpdate = true; }
  
  public void setWH(int w, int h) {
    _w = w;
    _h = h;
    _matrix.setProjection(_w, _h);
    GL11.glViewport(0, 0, _w, _h);
    _gui.resize();
  }
  
  public GUIs GUI() { return _gui; }
  
  protected abstract void createDisplay() throws LWJGLException;
  protected abstract void createInstances();
  
  public boolean create(Game game) {
    if(!Display.isCreated()) {
      try {
        Display.setInitialBackground(_backColour[0], _backColour[1], _backColour[2]);
        Display.setDisplayMode(new DisplayMode(_w, _h));
        createDisplay();
      } catch(LWJGLException e) {
        e.printStackTrace();
        return false;
      }
    }
    
    System.out.println("Creating context " + Display.getTitle());
    System.out.println("Display adapter: " + Display.getAdapter());
    System.out.println("Driver version:  " + Display.getVersion());
    System.out.println("OpenGL version:  " + GL11.glGetString(GL11.GL_VERSION));
    
    GL11.glEnable(GL11.GL_BLEND);
    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    GL11.glViewport(0, 0, _w, _h);
    
    createInstances();
    createGamepads();
    
    _matrix.setProjection(_w, _h);
    
    _game = game;
    _game.init();
    
    _running = true;
    
    return true;
  }
  
  private void createGamepads() {
    try {
      Controllers.create();
      
      for(int i = 0; i < Controllers.getControllerCount(); i++) {
        Controller c = Controllers.getController(i);
        
        if(!c.getName().equals("USB Receiver")) {
          System.out.println("Found controller: " + c.getName());
        }
      }
    } catch(LWJGLException e) {
      e.printStackTrace();
    }
  }
  
  protected void cleanup() {
    
  }
  
  public void destroy() {
    if(_running) {
      _running = false;
    } else {
      Display.destroy();
    }
  }
  
  public void run() {
    _fps = _fpsTarget;
    _fpsTimeout = Time.HzToTicks(1);
    _fpsTimer = Time.getTime() + _fpsTimeout;
    
    _logic.start();
    
    while(_running) {
      check();
      draw();
      mouse();
      
      if(_fpsTimer <= Time.getTime()) {
        _fpsTimer += _fpsTimeout;
        _fps = _fpsCount;
        _fpsCount = 0;
      }
      
      _fpsCount++;
    }
    
    _logic.stop();
    _game.destroy();
    
    while(!_logic._finished) {
      try {
        Thread.sleep(1);
      } catch(InterruptedException e) { }
    }
    
    _gui.destroy();
    _textures.destroy();
    
    cleanup();
    Controllers.destroy();
    Logger.printRefs();
    Display.destroy();
  }

  protected void check() {
    if(Display.isCloseRequested()) {
      _running = false;
    }
    
    if(Display.wasResized()) {
      System.out.println("Window resized!");
      setWH(Display.getWidth(), Display.getHeight());
    }
    
    if(_backColourUpdate) {
      GL11.glClearColor(_backColour[0], _backColour[1], _backColour[2], _backColour[3]);
      _backColourUpdate = false;
    }
    
    _textures.check();
  }
  
  public void clear() {
    GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
  }
  
  public void clear(float[] c) {
    GL11.glClearColor(c[0], c[1], c[2], c[3]);
    clear();
    GL11.glClearColor(_backColour[0], _backColour[1], _backColour[2], _backColour[3]);
  }
  
  protected void draw() {
    clear();
    
    _matrix.push();
    _matrix.translate(_cameraX, _cameraY);
    
    _gui.draw();
    
    _matrix.pop();
    
    Display.update();
    Display.sync(_fpsTarget);
  }
  
  protected void mouse() {
    if(_mouseX != Mouse.getX() || _mouseY != _h - Mouse.getY()) {
      _mouseX = Mouse.getX();
      _mouseY = _h - Mouse.getY();
      _gui.mouseMove(_mouseX, _mouseY);
    }
    
    if(Mouse.next()) {
      if(Mouse.getEventButton() != -1) {
        if(Mouse.getEventButtonState()) {
          _gui.mouseDown(_mouseX, _mouseY, Mouse.getEventButton());
        } else {
          _gui.mouseUp(_mouseX, _mouseY, Mouse.getEventButton());
        }
      }
      
      if(Mouse.getEventDWheel() != 0) {
        _gui.mouseWheel(Mouse.getEventDWheel());
      }
    }
  }
  
  public int[] getPixel(int x, int y) {
    ByteBuffer pixels = BufferUtils.createByteBuffer(3);
    GL11.glReadPixels(x, _h - y, 1, 1, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, pixels);
    byte[] b = new byte[] {pixels.get(0), pixels.get(1), pixels.get(2)};
    return new int[] {b[0] >= 0 ? b[0] : 256 + b[0], b[1] >= 0 ? b[1] : 256 + b[1], b[2] >= 0 ? b[2] : 256 + b[2]};
  }
  
  public int[] getNextSelectColour() {
    int[] colour = {_selectColour[0], _selectColour[1], _selectColour[2], _selectColour[3]};
    
    _selectColour[0]++;
    if(_selectColour[0] == 255) {
      _selectColour[0] = 0;
      _selectColour[1]++;
      if(_selectColour[1] == 255) {
        _selectColour[1] = 0;
        _selectColour[2]++;
        if(_selectColour[2] == 255) {
          System.err.println("Somehow we ran out of selcolours");
        }
      }
    }
    
    return colour;
  }
}