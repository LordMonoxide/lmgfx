package graphics.gl32;

import graphics.util.Logger;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;

public class Shader {
  private String _name;
  private int _prog;
  private int _vsh;
  private int _fsh;
  
  private int _proj;
  private int _world;
  private int _trans;
  
  private FloatBuffer _matBuffer = BufferUtils.createFloatBuffer(16);
  
  protected Shader(String name, int prog, int vsh, int fsh, int proj, int world, int trans) {
    _name = name;
    _prog = prog;
    _vsh = vsh;
    _fsh = fsh;
    _proj = proj;
    _world = world;
    _trans = trans;
    
    Logger.addRef(Logger.LOG_SHADER, _name);
  }
  
  public void use(Matrix4f proj, Matrix4f world, Matrix4f trans) {
    GL20.glUseProgram(_prog);
    
    proj.store(_matBuffer);
    _matBuffer.flip();
    GL20.glUniformMatrix4(_proj, false, _matBuffer);
    
    world.store(_matBuffer);
    _matBuffer.flip();
    GL20.glUniformMatrix4(_world, false, _matBuffer);
    
    trans.store(_matBuffer);
    _matBuffer.flip();
    GL20.glUniformMatrix4(_trans, false, _matBuffer);
  }
  
  public void destroy() {
    GL20.glDetachShader(_prog, _vsh);
    GL20.glDetachShader(_prog, _fsh);
    GL20.glDeleteProgram(_prog);
    Logger.removeRef(Logger.LOG_SHADER, _name);
  }
}