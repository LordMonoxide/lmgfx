package graphics.gl32;

import graphics.shared.textures.Texture;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

public class Drawable extends graphics.gl00.Drawable {
  protected Matrix _matrix = (Matrix)super._matrix; // DID SOMEBODY ORDER A MATRIX?
  
  private int _vaID;
  private int _vbID;
  private int _ibID;
  private int _indices;
  
  protected Matrix4f _trans;
  protected Shader _shader;
  
  public Drawable() {
    updateLoc();
    
    _shader = Shaders.getBox();
    _vertex = Vertex.createQuad(_loc, _tex, _col);
    
    // Generate vertex array and buffers
    _vaID = GL30.glGenVertexArrays();
    _vbID = GL15.glGenBuffers();
    _ibID = GL15.glGenBuffers();
    
    // Set up index buffer
    byte[] indices = {
        0, 1, 2, 3
    };
    
    _indices = indices.length;
    
    ByteBuffer indicesBuffer = BufferUtils.createByteBuffer(_indices);
    indicesBuffer.put(indices);
    indicesBuffer.flip();
    
    GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, _ibID);
    GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);
    GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
    
    GL30.glBindVertexArray(_vaID);
    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, _vbID);
    GL20.glVertexAttribPointer(0, Vertex.locCount, GL11.GL_FLOAT, false, Vertex.stride, Vertex.locOffset);
    GL20.glVertexAttribPointer(1, Vertex.colCount, GL11.GL_FLOAT, false, Vertex.stride, Vertex.colOffset);
    GL20.glVertexAttribPointer(2, Vertex.texCount, GL11.GL_FLOAT, false, Vertex.stride, Vertex.texOffset);
    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    GL30.glBindVertexArray(0);
  }
  
  public void createQuad() {
    _renderMode = GL11.GL_TRIANGLE_STRIP;
    _vertex = Vertex.createQuad(_loc, _tex, _col);
    updateBuffer();
  }
  
  public void createBorder() {
    _renderMode = GL11.GL_LINE_STRIP;
    _vertex = Vertex.createBorder(_loc, _col);
    updateBuffer();
  }
  
  public void createLine() {
    _renderMode = GL11.GL_LINE;
    _vertex = Vertex.createLine(new float[] {_loc[0], _loc[1]}, new float[] {_loc[2], _loc[3]}, _col);
    updateBuffer();
  }
  
  public void setTexture(Texture texture) {
    _texture = texture;
    
    if(_texture != null) {
      _shader = Shaders.getDefault();
      setWH(texture.getW(), texture.getH());
      setTWH(texture.getW(), texture.getH());
    } else {
      _shader = Shaders.getBox();
    }
  }
  
  public void setX(float x) {
    super.setX(x);
    updateLoc();
  }
  
  public void setY(float y) {
    super.setY(y);
    updateLoc();
  }
  
  public void setXY(float x, float y) {
    super.setXY(x, y);
    updateLoc();
  }
  
  public void setW(float w) {
    super.setW(w);
    updateSize();
  }
  
  public void setH(float h) {
    super.setH(h);
    updateSize();
  }
  
  public void setWH(float w, float h) {
    super.setWH(w, h);
    updateSize();
  }
  
  public void setXYWH(float x, float y, float w, float h) {
    super.setXYWH(x, y, w, h);
    updateSize();
  }
  
  public void setTX(float x) {
    super.setTX(x);
    updateTex();
  }
  
  public void setTY(float y) {
    super.setTY(y);
    updateTex();
  }
  
  public void setTW(float w) {
    super.setTW(w);
    updateTex();
  }
  
  public void setTH(float h) {
    super.setTH(h);
    updateTex();
  }
  
  public void setTXY(float x, float y) {
    super.setTXY(x, y);
    updateTex();
  }
  
  public void setTWH(float w, float h) {
    super.setTWH(w, h);
    updateTex();
  }
  
  public void setTXYWH(float x, float y, float w, float h) {
    super.setTXYWH(x, y, w, h);
    updateTex();
  }
  
  public void setColour(float c[]) {
    super.setColour(c);
    updateCol();
  }
  
  protected void updateLoc() {
    _trans = Matrix.Translation(_loc[0], _loc[1]);
  }
  
  private void updateSize() {
    _vertex[1].setLoc(_loc[2],      0f);
    _vertex[2].setLoc(     0f, _loc[3]);
    _vertex[3].setLoc(_loc[2], _loc[3]);
  }
  
  private void updateTex() {
    float[] tex = new float[4];
    
    if(_texture != null) {
      tex[0] = _tex[0] / _texture.getW();
      tex[1] = _tex[1] / _texture.getH();
      tex[2] = (_tex[0] + _tex[2]) / _texture.getW();
      tex[3] = (_tex[1] + _tex[3]) / _texture.getH();
    }
    
    _vertex[0].setTex(tex[0], tex[1]);
    _vertex[1].setTex(tex[2], tex[1]);
    _vertex[2].setTex(tex[0], tex[3]);
    _vertex[3].setTex(tex[2], tex[3]);
  }
  
  private void updateCol() {
    for(int i = 0; i < _vertex.length; i++) {
      _vertex[i].setCol(_col);
    }
  }
  
  private void updateBuffer() {
    FloatBuffer buffer = BufferUtils.createFloatBuffer(_vertex.length * Vertex.allCount);
    
    for(int i = 0; i < _vertex.length; i++) {
      buffer.put(_vertex[i].getLoc());
      buffer.put(_vertex[i].getCol());
      buffer.put(_vertex[i].getTex());
    }
    
    buffer.flip();
    
    // Vertex buffer
    GL30.glBindVertexArray(_vaID);
    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, _vbID);
    GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    GL30.glBindVertexArray(0);
  }
  
  public void draw() {
    if(_vertex == null || !_visible) return;
    
    _shader.use(_matrix.getProjection(), _matrix.getWorld(), _trans);
    
    if(_texture != null)
      _texture.use();
    
    GL30.glBindVertexArray(_vaID);
    GL20.glEnableVertexAttribArray(0);
    GL20.glEnableVertexAttribArray(1);
    GL20.glEnableVertexAttribArray(2);
    
    // Bind to the index buffer that has all the information about the order of the vertices
    GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, _ibID);
    
    // Draw the vertices
    GL11.glDrawElements(_renderMode, _indices, GL11.GL_UNSIGNED_BYTE, 0);
    
    // Put everything back to default (deselect)
    GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
    GL20.glDisableVertexAttribArray(0);
    GL20.glDisableVertexAttribArray(1);
    GL20.glDisableVertexAttribArray(2);
    GL30.glBindVertexArray(0);
  }
}