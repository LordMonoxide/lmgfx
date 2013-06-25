package graphics.shared.gui;

import java.util.concurrent.ConcurrentLinkedDeque;

public class GUIs {
  protected ConcurrentLinkedDeque<GUI> _gui = new ConcurrentLinkedDeque<GUI>();
  
  public void push(GUI gui) {
    _gui.push(gui);
  }
  
  public void pop() {
    _gui.pop();
  }
  
  public void pop(GUI gui) {
    _gui.remove(gui);
  }
  
  public void clear() {
    _gui.clear();
  }
  
  public void destroy() {
    for(GUI gui : _gui) {
      gui.destroy();
    }
    
    clear();
  }
  
  public void draw() {
    GUI[] g = _gui.toArray(new GUI[0]);
    
    for(int i = _gui.size(); --i >= 0;) {
      if(g[i]._loaded) g[i].drawGUI();
    }
  }
  
  public void logic() {
    for(GUI gui : _gui) {
      if(gui._loaded) if(gui.logicGUI()) break;
    }
  }
  
  public void resize() {
    for(GUI gui : _gui) {
      if(gui._loaded) gui.resize();
    }
  }
  
  public void mouseMove(int x, int y) {
    for(GUI gui : _gui) {
      if(gui._loaded) if(gui.mouseMove(x, y)) break;
    }
  }
  
  public void mouseDown(int x, int y, int button) {
    for(GUI gui : _gui) {
      if(gui._loaded) if(gui.mouseDown(x, y, button)) break;
    }
  }
  
  public void mouseUp(int x, int y, int button) {
    for(GUI gui : _gui) {
      if(gui._loaded) if(gui.mouseUp(x, y, button)) break;
    }
  }
  
  public void mouseWheel(int delta) {
    for(GUI gui : _gui) {
      if(gui._loaded) if(gui.mouseWheel(delta)) break;
    }
  }
  
  public void keyDown(int key) {
    for(GUI gui : _gui) {
      if(gui._loaded) if(gui.keyDown(key)) break;
    }
  }
  
  public void keyUp(int key) {
    for(GUI gui : _gui) {
      if(gui._loaded) if(gui.keyUp(key)) break;
    }
  }
  
  public void charDown(char c) {
    for(GUI gui : _gui) {
      if(gui._loaded) if(gui.charDown(c)) break;
    }
  }
}