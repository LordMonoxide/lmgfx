package graphics.shared.textures;

import graphics.util.Time;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;

public class Textures {
  private static Textures _instance = new Textures();
  public static Textures getInstance() { return _instance; }
  
  private HashMap<String, Texture> _textures = new HashMap<String, Texture>();
  private PNG _png = new PNG();
  
  private int _lock;
  
  public int loaded() {
    return _textures.size();
  }
  
  private Textures() { }
  
  public Texture getTexture(String name, int w, int h, ByteBuffer data) {
    double t = Time.getTime();
    
    while(_lock != 0) try { Thread.sleep(1); } catch(Exception e) { }
    if(_textures.containsKey(name)) {
      return _textures.get(name);
    }
    
    _lock++;
    Texture texture = new Texture(name, w, h, data);
    _textures.put(name, texture);
    _lock--;
    
    System.out.println("Texture \"" + name + "\" (" + w + "x" + h +") loaded. (" + (Time.getTime() - t) + ")");
    
    return texture;
  }
  
  public Texture getTexture(String file) {
    double t = Time.getTime();
    
    while(_lock != 0) try { Thread.sleep(1); } catch(Exception e) { }
    if(_textures.containsKey(file)) {
      return _textures.get(file);
    }
    
    _lock++;
    System.out.println("Texture \"" + file + "\" loading to memory...");
    
    ByteBuffer data = null;
    
    try {
      data = _png.load(file);
    } catch(FileNotFoundException e) {
      System.err.println("Couldn't find texture \"" + file + "\"");
      return null;
    } catch(IOException e) {
      e.printStackTrace();
      return null;
    }
    
    Texture texture = new Texture(file, _png.getW(), _png.getH(), data);
    _textures.put(file, texture);
    
    _lock--;
    
    System.out.println("Texture \"" + file + "\" (" + _png.getW() + "x" + _png.getH() +") loaded. (" + (Time.getTime() - t) + ")");
    
    return texture;
  }
  
  public void destroy() {
    for(Texture t : _textures.values()) {
      t.destroy();
    }
    
    _textures.clear();
  }
}