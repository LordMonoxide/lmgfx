package graphics.shared.textures;

import graphics.util.Time;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.LinkedList;

public class Textures {
  private static Textures _instance = new Textures();
  
  public static Textures getInstance() {
    return _instance;
  }
  
  private HashMap<String, Texture> _textures = new HashMap<String, Texture>();
  private LinkedList<Texture>      _texturesToLoad = new LinkedList<Texture>();
  private PNG _png = new PNG();
  
  public int loaded() {
    return _textures.size();
  }
  
  public int loading() {
    return _texturesToLoad.size();
  }
  
  private Textures() { }
  
  public void check() {
    Texture t = _texturesToLoad.poll();
    if(t != null) {
      t.load();
    }
  }
  
  public Texture getTexture(String name, int w, int h, ByteBuffer data) { return getTexture(name, w, h, data, false); }
  public Texture getTexture(String name, int w, int h, ByteBuffer data, boolean forceLoad) {
    double t = Time.getTime();
    
    if(_textures.containsKey(name)) {
      return _textures.get(name);
    }
    
    Texture texture = new Texture(name, w, h, data);
    
    if(!forceLoad) {
      _texturesToLoad.offer(texture);
    } else {
      texture.load();
    }
    
    _textures.put(name, texture);
    
    System.out.println("Texture \"" + name + "\" (" + w + "x" + h +") loaded. (" + (Time.getTime() - t) + ")");
    
    return texture;
  }
  
  public Texture getTexture(String file) { return getTexture(file, false); }
  public Texture getTexture(String file, boolean forceLoad) {
    double t = Time.getTime();
    
    if(_textures.containsKey(file)) {
      return _textures.get(file);
    }
    
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
    
    if(!forceLoad) {
      _texturesToLoad.offer(texture);
    } else {
      texture.load();
    }
    
    _textures.put(file, texture);
    
    System.out.println("Texture \"" + file + "\" (" + _png.getW() + "x" + _png.getH() +") loaded. (" + (Time.getTime() - t) + ")");
    
    return texture;
  }
  
  public void destroy() {
    _texturesToLoad.clear();
    
    for(Texture t : _textures.values()) {
      t.destroy();
    }
    
    _textures.clear();
  }
}