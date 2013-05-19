package graphics.gl00;

import graphics.shared.textures.Texture;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;

public class Canvas {
  private Context _context = Context._context;
  private Matrix _matrix = Context._matrix;
  private Texture _texture;
  
  private int _id;
  
  public Canvas(String name, int w, int h) {
    IntBuffer buffer = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder()).asIntBuffer();
    EXTFramebufferObject.glGenFramebuffersEXT(buffer);
    
    _id = buffer.get();
    _texture = Context.getTextures().getTexture(name, w, h, null, true);
    
    bind();
    EXTFramebufferObject.glFramebufferTexture2DEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, EXTFramebufferObject.GL_COLOR_ATTACHMENT0_EXT, GL11.GL_TEXTURE_2D, _texture.getID(), 0);
    unbind();
  }
  
  public Texture getTexture() {
    return _texture;
  }
  
  public void clear() {
    GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
  }
  
  public void bind() {
    EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, _id);
    GL11.glPushAttrib(GL11.GL_VIEWPORT_BIT);
    GL11.glViewport(0, 0, _texture.getW(), _texture.getH());
    _matrix.setProjection(_texture.getW(), _texture.getH(), true);
    _matrix.push();
    _matrix.reset();
    clear();
  }
  
  public void unbind() {
    GL11.glPopAttrib();
    _matrix.pop();
    _matrix.setProjection(_context.getW(), _context.getH(), false);
    EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, 0);
  }
}