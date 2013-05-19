package graphics.shared.textures;

import graphics.util.Logger;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;

public class Texture {
  private String _name;
  private int _id;
  private int _w, _h;
  private ByteBuffer _data;
  
  protected Texture() { }
  protected Texture(String name, int w, int h, ByteBuffer data) {
    _name = name;
    _w = w;
    _h = h;
    _data = data;
    
    Logger.addRef(Logger.LOG_TEXTURE, _name);
  }
  
  public int getID() { return _id; }
  public int getW()  { return _w; }
  public int getH()  { return _h; }
  
  public void load() {
    if(_id != 0) return;
    _id = GL11.glGenTextures();
    GL11.glBindTexture(GL11.GL_TEXTURE_2D, _id);
    GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, _w, _h, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, _data);
    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
    _data = null;
    System.out.println("Loaded requested texture ID " + _id);
  }
  
  public void update(int x, int y, int w, int h, ByteBuffer data) {
    GL11.glBindTexture(GL11.GL_TEXTURE_2D, _id);
    GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, x, y, w, h, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data);
  }
  
  public void use() {
    GL11.glBindTexture(GL11.GL_TEXTURE_2D, _id);
  }
  
  public void destroy() {
    GL11.glDeleteTextures(_id);
    Logger.removeRef(Logger.LOG_TEXTURE, _name);
  }
}