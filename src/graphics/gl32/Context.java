package graphics.gl32;

import graphics.shared.fonts.Fonts;
import graphics.shared.textures.Textures;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.PixelFormat;

public class Context extends graphics.gl00.Context {
  protected void createDisplay() throws LWJGLException {
    ContextAttribs contextAttribs = new ContextAttribs(3, 2).withProfileCore(true).withForwardCompatible(true);
    PixelFormat pixelFormat = new PixelFormat();
    Display.create(pixelFormat, contextAttribs);
  }
  
  protected void createInstances() {
    _context  = this;
    _matrix   = new Matrix();
    _vertex   = Vertex.class;
    _drawable = Drawable.class;
    //_scalable = Scalable.class;
    _textures = Textures.getInstance();
    _fonts    = Fonts.getInstance();
  }
  
  protected void cleanup() {
    Shaders.destroy();
  }
}