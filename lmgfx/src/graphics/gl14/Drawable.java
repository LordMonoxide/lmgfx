package graphics.gl14;

import org.lwjgl.opengl.GL11;

public class Drawable extends graphics.gl00.Drawable {
  public void createQuad() {
    _renderMode = GL11.GL_TRIANGLE_STRIP;
    _vertex = Vertex.createQuad(new float[] {0, 0, _loc[2], _loc[3]}, _tex, _col);
  }
  
  public void createBorder() {
    _renderMode = GL11.GL_LINE_STRIP;
    _vertex = Vertex.createBorder(new float[] {0, 0, _loc[2], _loc[3]}, _col);
  }
  
  public void createLine() {
    _renderMode = GL11.GL_LINE;
    _vertex = Vertex.createLine(new float[] {_loc[0], _loc[1]}, new float[] {_loc[2], _loc[3]}, _col);
  }
  
  public void draw() {
    if(_vertex == null || !_visible) return;
    
    _matrix.push();
    _matrix.translate(_loc[0], _loc[1]);
    
    if(_texture != null) {
      GL11.glEnable(GL11.GL_TEXTURE_2D);
      _texture.use();
    } else {
      GL11.glDisable(GL11.GL_TEXTURE_2D);
    }
    
    GL11.glBegin(_renderMode);
    
    for(int i = 0; i < _vertex.length; i++) {
      if(_vertex[i] == null) continue;
      _vertex[i].use();
    }
    
    GL11.glEnd();
    
    _matrix.pop();
  }
}