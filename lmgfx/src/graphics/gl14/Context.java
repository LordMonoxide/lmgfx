package graphics.gl14;

import graphics.shared.fonts.Fonts;
import graphics.shared.textures.Textures;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.PixelFormat;

public class Context extends graphics.gl00.Context {
  protected void createDisplay() throws LWJGLException {
    ContextAttribs attribs = new ContextAttribs(1, 4);
    PixelFormat format = new PixelFormat();
    Display.create(format, attribs);
  }
  
  protected void createInstances() {
    _context  = this;
    _matrix   = new Matrix();
    _vertex   = Vertex.class;
    _drawable = Drawable.class;
    _scalable = Scalable.class;
    _textures = Textures.getInstance();
    _fonts    = Fonts.getInstance();
  }
}