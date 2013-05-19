package graphics.gl00;

public class Vertex {
  protected float[] _loc = {0, 0};
  protected float[] _tex = {0, 0};
  protected float[] _col = {0, 0, 0, 0};
  
  public static final Vertex[] newVertices(int count) {
    Vertex[] v = new Vertex[count];
    
    for(int i = 0; i < v.length; i++) {
      v[i] = Context.newVertex();
    }
    
    return v;
  }
  
  public static final Vertex[] createQuad(float[] loc, float[] tex, float[] col) {
    Vertex[] v = newVertices(4);
    v[0].set(new float[] {loc[0]         , loc[1]         }, new float[] {tex[0]         , tex[1]         }, col);
    v[1].set(new float[] {loc[0] + loc[2], loc[1]         }, new float[] {tex[0] + tex[2], tex[1]         }, col);
    v[2].set(new float[] {loc[0]         , loc[1] + loc[3]}, new float[] {tex[0]         , tex[1] + tex[3]}, col);
    v[3].set(new float[] {loc[0] + loc[2], loc[1] + loc[3]}, new float[] {tex[0] + tex[2], tex[1] + tex[3]}, col);
    return v;
  }
  
  public static final Vertex[] createBorder(float[] loc, float[] col) {
    Vertex[] v = newVertices(5);
    v[0].set(new float[] {loc[0]         , loc[1] + 1     }, new float[] {0, 0}, col);
    v[1].set(new float[] {loc[0] + loc[2], loc[1] + 1     }, new float[] {0, 0}, col);
    v[2].set(new float[] {loc[0] + loc[2], loc[1] + loc[3] - 1}, new float[] {0, 0}, col);
    v[3].set(new float[] {loc[0] + 1     , loc[1] + loc[3] - 1}, new float[] {0, 0}, col);
    v[4].set(new float[] {loc[0] + 1     , loc[1] + 1     }, new float[] {0, 0}, col);
    return v;
  }
  
  public static final Vertex[] createLine(float[] loc1, float[] loc2, float[] col) {
    Vertex[] v = newVertices(2);
    v[0].set(loc1, new float[] {0, 0}, col);
    v[1].set(loc2, new float[] {0, 0}, col);
    return v;
  }
  
  protected Vertex() { }
  protected Vertex(float[] loc, float[] tex, float[] col) {
    set(loc, tex, col);
  }
  
  public void set(float[] loc, float[] tex, float[] col) {
    _loc = loc;
    _tex = tex;
    _col = col;
    
    if(_col == null) {
      _col = new float[4];
    }
  }
  
  public void use() { }
  
  public float[] getLoc() { return _loc; }
  public float[] getTex() { return _tex; }
  public float[] getCol() { return _col; }
  public void setLoc(float[] loc) { _loc = loc; }
  public void setTex(float[] tex) { _tex = tex; }
  public void setCol(float[] col) { _col = col; }
  
  public void setLoc(float x, float y) {
    _loc[0] = x;
    _loc[1] = y;
  }
  
  public void setTex(float x, float y) {
    _tex[0] = x;
    _tex[0] = y;
  }
}