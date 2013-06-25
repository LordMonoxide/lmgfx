package graphics.shared.textures;

import graphics.gl00.Context;
import graphics.util.Logger;

import java.nio.ByteBuffer;
import java.util.LinkedList;

import org.lwjgl.opengl.GL11;

public class Texture {
  private String _name;
  private int _id;
  private int _w, _h;
  private ByteBuffer _data;
  
  private Events _events = new Events(this);
  private boolean _loaded, _updated;
  
  protected Texture(String name, int w, int h, ByteBuffer data) {
    _name = name;
    _w = w;
    _h = h;
    _data = data;
    
    Context.getContext().addLoadCallback(new Context.Loader.Callback() {
      public void load() {
        _id = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, _id);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, _w, _h, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, _data);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        _data = null;
        System.out.println("Loaded requested texture ID " + _id);
        _loaded = true;
        
        _events.raiseLoad();
      }
    }, true);
    
    Logger.addRef(Logger.LOG_TEXTURE, _name);
  }
  
  public int getID() { return _id; }
  public int getW()  { return _w; }
  public int getH()  { return _h; }
  
  public Events events() { return _events; }
  public boolean loaded() { return _loaded; }
  public boolean updated() { return _updated; }
  
  public void update(final int x, final int y, final int w, final int h, final ByteBuffer data) {
    _updated = false;
    
    Context.getContext().addLoadCallback(new Context.Loader.Callback() {
      public void load() {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, _id);
        GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, x, y, w, h, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data);
        _updated = true;
        
        _events.raiseUpdate();
      }
    }, true);
  }
  
  public void use() {
    GL11.glBindTexture(GL11.GL_TEXTURE_2D, _id);
  }
  
  public void destroy() {
    GL11.glDeleteTextures(_id);
    Logger.removeRef(Logger.LOG_TEXTURE, _name);
  }
  
  public static class Events {
    private LinkedList<Load>   _load   = new LinkedList<Load>();
    private LinkedList<Update> _update = new LinkedList<Update>();
    
    private Texture _this;
    
    public Events(Texture data) {
      _this = data;
    }
    
    public void addLoadHandler(Load e) {
      e.__events = this;
      _load.add(e);
      
      if(_this._loaded) {
        raiseLoad();
      }
    }
    
    public void addUpdateHandler(Update e) {
      e.__events = this;
      _update.add(e);
      
      if(_this._loaded) {
        raiseUpdate();
      }
    }
    
    public void raiseLoad() {
      for(Load e : _load) e.load();
    }
    
    public void raiseUpdate() {
      for(Update e : _update) e.update();
    }
    
    public static abstract class Load   extends Event { public abstract void load  (); }
    public static abstract class Update extends Event { public abstract void update(); }
    
    public static class Event {
      protected Events __events;
      
      public void remove() {
        __events._load.remove(this);
      }
    }
  }
}