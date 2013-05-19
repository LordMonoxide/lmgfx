package graphics.gl00;

import java.util.LinkedList;

import graphics.shared.gui.GUIs;
import graphics.util.Time;

import org.lwjgl.input.Controller;
import org.lwjgl.input.Controllers;
import org.lwjgl.input.Keyboard;

public class ContextLogic implements Runnable {
  protected Thread _thread;
  
  protected GUIs _gui;
  protected LinkedList<LogicCallback> _cb = new LinkedList<LogicCallback>();
  
  protected boolean _running;
  protected boolean _paused;
  protected boolean _finished;
  
  protected boolean[] _keyDown = new boolean[256];
  protected boolean[] _buttonDown = new boolean[256];
  protected int _povX, _povY;
  
  private double _fpsTimer;
  private double _fpsTimerInterval;
  private int _fpsCount;
  private int _fps;
  
  protected ContextLogic(GUIs gui) {
    _gui = gui;
  }
  
  public int getFPS() { return _fps; }
  
  public void start() {
    if(_thread != null) return;
    _running = true;
    _thread = new Thread(this);
    _thread.start();
    
    System.out.println("Logic thread started.");
  }
  
  public void stop() {
    _running = false;
  }
  
  public boolean getPaused() {
    return _paused;
  }
  
  public void setPaused(boolean paused) {
    _paused = paused;
  }
  
  public void addCallback(LogicCallback lc) {
    _cb.add(lc);
  }
  
  public void removeCallback(LogicCallback lc) {
    _cb.remove(lc);
  }
  
  public void run() {
    _fps = 120;
    double logicTimeout = Time.HzToTicks(_fps);
    double logicTimer   = Time.getTime();
    
    _fpsTimerInterval = Time.HzToTicks(1);
    _fpsTimer = Time.getTime() + _fpsTimerInterval;
    
    double inputTimeout = Time.HzToTicks(60);
    double inputTimer   = Time.getTime();
    
    while(_running) {
      if(!_paused) {
        if(inputTimer <= Time.getTime()) {
          inputTimer += inputTimeout;
          keyboard();
          controller();
        }
        
        if(logicTimer <= Time.getTime()) {
          logicTimer += logicTimeout;
          
          _gui.logic();
          
          for(LogicCallback lc : _cb) {
            lc.callback();
          }
          
          _fpsCount++;
        }
        
        if(_fpsTimer <= Time.getTime()) {
          _fpsTimer = Time.getTime() + _fpsTimerInterval;
          _fps = _fpsCount;
          _fpsCount = 0;
        }
      }
      
      try {
        Thread.sleep(1);
      } catch(InterruptedException e) { }
    }
    
    _finished = true;
    
    System.out.println("Logic thread finished.");
  }
  
  protected void keyboard() {
    if(Keyboard.next()) {
      if(Keyboard.getEventKeyState()) {
        if(!_keyDown[Keyboard.getEventKey()]) {
          _keyDown[Keyboard.getEventKey()] = true;
          _gui.keyDown(Keyboard.getEventKey());
        }
        
        if(Keyboard.getEventCharacter() != 0) {
          switch(Keyboard.getEventCharacter()) {
            case  8: case  9:
            case 13: case 27:
              break;
              
            default:
              _gui.charDown(Keyboard.getEventCharacter());
          }
        }
      } else {
        _keyDown[Keyboard.getEventKey()] = false;
        _gui.keyUp(Keyboard.getEventKey());
      }
    }
  }
  
  protected void controller() {
    if(Controllers.next()) {
      Controller c = Controllers.getEventSource();
      c.setXAxisDeadZone(0.2f);
      c.setYAxisDeadZone(0.2f);
      c.setRXAxisDeadZone(0.2f);
      c.setRYAxisDeadZone(0.2f);
      
      if(Controllers.isEventAxis()) {
        double al = Math.toDegrees(Math.atan2(c.getYAxisValue(),  c.getXAxisValue()));
        double ar = Math.toDegrees(Math.atan2(c.getRYAxisValue(), c.getRXAxisValue()));
        
        _gui.axisLeft (al, c.getXAxisValue(), c.getYAxisValue());
        _gui.axisRight(ar, c.getXAxisValue(), c.getYAxisValue());
      }
      
      if(Controllers.isEventButton()) {
        for(int i = 0; i < c.getButtonCount(); i++) {
          if(c.isButtonPressed(i)) {
            if(!_buttonDown[i]) {
              _buttonDown[i] = true;
              _gui.buttonDown(i);
            }
          } else {
            if(_buttonDown[i]) {
              _buttonDown[i] = false;
              _gui.buttonUp(i);
            }
          }
        }
      }
      
      if(Controllers.isEventPovX()) {
        int povX = (int)c.getPovX();
        switch(povX) {
          case 0:
            _gui.buttonUp(_povX == -1 ? 1002 : 1003);
            break;
            
          case -1:
            _gui.buttonDown(1002);
            break;
            
          case 1:
            _gui.buttonDown(1003);
            break;
        }
        
        _povX = povX;
      }
      
      if(Controllers.isEventPovY()) {
        int povY = (int)c.getPovY();
        
        switch(povY) {
          case 0:
            _gui.buttonUp(_povY == -1 ? 1000 : 1001);
            break;
            
          case -1:
            _gui.buttonDown(1000);
            break;
            
          case 1:
            _gui.buttonDown(1001);
            break;
        }
        
        _povY = povY;
      }
    }
  }
  
  public static interface LogicCallback {
    public void callback();
  }
}