package graphics.gl32;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

public class Drawable extends graphics.gl00.Drawable {
  private Matrix _matrix = (Matrix)super._matrix;
  
  private int _vaID;
  private int _vbID;
  
  private Matrix4f _trans;
  private Shader _shader;
  
  public Drawable() {
    Context.getContext().addLoadCallback(new Context.Loader.Callback() {
      public void load() {
        // Generate vertex array and buffers
        _vaID = GL30.glGenVertexArrays();
        _vbID = GL15.glGenBuffers();
        
        GL30.glBindVertexArray(_vaID);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, _vbID);
        GL20.glVertexAttribPointer(0, Vertex.locCount, GL11.GL_FLOAT, false, Vertex.stride, Vertex.locOffset);
        GL20.glVertexAttribPointer(1, Vertex.colCount, GL11.GL_FLOAT, false, Vertex.stride, Vertex.colOffset);
        GL20.glVertexAttribPointer(2, Vertex.texCount, GL11.GL_FLOAT, false, Vertex.stride, Vertex.texOffset);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);
      }
    }, true);
  }
  
  public void createQuad() {
    _renderMode = GL11.GL_TRIANGLE_STRIP;
    _vertex = Vertex.createQuad(new float[] {0, 0, _loc[2], _loc[3]}, _tex, _col);
    updateBuffer();
  }
  
  public void createBorder() {
    _renderMode = GL11.GL_LINE_STRIP;
    _vertex = Vertex.createBorder(new float[] {0, 0, _loc[2], _loc[3]}, _col);
    updateBuffer();
  }
  
  public void createLine() {
    _renderMode = GL11.GL_LINE;
    _vertex = Vertex.createLine(new float[] {_loc[0], _loc[1]}, new float[] {_loc[2], _loc[3]}, _col);
    updateBuffer();
  }
  
  private void updateLoc() {
    _trans = _matrix.getTranslation(_loc[0], _loc[1]);
    _vertex[1].setLoc(_loc[2],      0f);
    _vertex[2].setLoc(     0f, _loc[3]);
    _vertex[3].setLoc(_loc[2], _loc[3]);
  }
  
  private void updateTex() {
    if(_texture != null) {
      float[] tex = new float[4];
      tex[0] = _tex[0] / _texture.getW();
      tex[1] = _tex[1] / _texture.getH();
      tex[2] = (_tex[0] + _tex[2]) / _texture.getW();
      tex[3] = (_tex[1] + _tex[3]) / _texture.getH();
      
      _vertex[0].setTex(tex[0], tex[1]);
      _vertex[1].setTex(tex[2], tex[1]);
      _vertex[2].setTex(tex[0], tex[3]);
      _vertex[3].setTex(tex[2], tex[3]);
    }
  }
  
  private void updateCol() {
    for(int i = 0; i < _vertex.length; i++) {
      _vertex[i].setCol(_col);
    }
  }
  
  private void updateBuffer() {
    updateLoc();
    updateTex();
    updateCol();
    
    final FloatBuffer buffer = BufferUtils.createFloatBuffer(_vertex.length * Vertex.allCount);
    
    for(int i = 0; i < _vertex.length; i++) {
      buffer.put(_vertex[i].getLoc());
      buffer.put(_vertex[i].getCol());
      buffer.put(_vertex[i].getTex());
    }
    
    buffer.flip();
    
    Context.getContext().addLoadCallback(new Context.Loader.Callback() {
      public void load() {
        _shader = _texture == null ? Shaders.getBox() : Shaders.getDefault();
        
        GL30.glBindVertexArray(_vaID);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, _vbID);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);
      }
    }, true);
  }
  
  public void draw() {
    if(_vertex == null) return;
    if(_shader == null) return;
    if(!_visible) return;
    
    _shader.use(_matrix.getProjection(), _matrix.getWorld(), _trans);
    
    if(_texture != null) _texture.use();
    
    GL30.glBindVertexArray(_vaID);
    GL20.glEnableVertexAttribArray(0);
    GL20.glEnableVertexAttribArray(1);
    GL20.glEnableVertexAttribArray(2);
    
    GL11.glDrawArrays(_renderMode, 0, _vertex.length);
    
    GL20.glDisableVertexAttribArray(0);
    GL20.glDisableVertexAttribArray(1);
    GL20.glDisableVertexAttribArray(2);
    GL30.glBindVertexArray(0);
  }
}