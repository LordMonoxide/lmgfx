package graphics.gl32;

import graphics.shared.textures.Texture;

public class Scalable extends graphics.gl00.Scalable {
  private float _borderL;
  private float _borderT;
  private float _borderR;
  private float _borderB;
  private float _borderL2;
  private float _borderT2;
  private float _borderR2;
  private float _borderB2;
  private float _borderH2;
  private float _borderV2;
  private float _tw, _th, _ts;
  
  private Drawable[] _d = new Drawable[9];
  
  public Scalable() {
    for(int i = 0; i < _d.length; i++) {
      _d[i] = new Drawable();
    }
    
    setSize(
        new float[] {32, 32, 32, 32},
        new float[] {21, 21, 21, 21},
        96, 96, 1
    );
  }
  
  public void setW(float w) {
    _loc[2] = w;
    updateVertices();
  }
  
  public void setH(float h) {
    _loc[3] = h;
    updateVertices();
  }
  
  public void setWH(float w, float h) {
    _loc[2] = w;
    _loc[3] = h;
    updateVertices();
  }
  
  public void setXYWH(float x, float y, float w, float h) {
    _loc[0] = x;
    _loc[1] = y;
    _loc[2] = w;
    _loc[3] = h;
    updateVertices();
    //updateLoc();
  }
  
  public void setXYWH(float[] loc) {
    _loc = loc;
    updateVertices();
    //updateLoc();
  }
  
  public void setColour(float[] c) {
    for(int i = 0; i < _d.length; i++) {
      _d[i].setColour(c);
    }
  }
  
  public void setTexture(Texture texture) {
    _texture = texture;
    
    for(int i = 0; i < _d.length; i++) {
      _d[i].setTexture(_texture);
    }
    
    if(_texture != null) {
      setWH(texture.getW(), texture.getH());
    }
    
    updateVertices();
  }
  
  public void setSize(float[] s1, float[] s2, float tw, float th, float ts) {
    _borderL = s1[0];
    _borderT = s1[1];
    _borderR = s1[2];
    _borderB = s1[3];
    _borderL2 = s2[0];
    _borderT2 = s2[1];
    _borderR2 = s2[2];
    _borderB2 = s2[3];
    _borderH2 = _borderL2 + _borderR2;
    _borderV2 = _borderT2 + _borderB2;
  }
  
  public void updateVertices() {
    float[][] border = new float[][] {
        {0, 0, _borderL, _borderT},
         {_borderL2, 0, _loc[2] - _borderH2, _borderT},
         {_loc[2] - _borderR2, 0, _borderR, _borderT},
        {0, _borderT2, _borderL, _loc[3] - _borderV2},
         {_borderL2, _borderT2, _loc[2] - _borderH2, _loc[3] - _borderV2},
         {_loc[2] - _borderR2, _borderT2, _borderR, _loc[3] - _borderV2},
        {0, _loc[3] - _borderB2, _borderR, _borderB},
         {_borderL2, _loc[3] - _borderB2, _loc[2] - _borderH2, _borderB},
         {_loc[2] - _borderR2, _loc[3] - _borderB2, _borderR, _borderB}
    };
    
    float[][] borderS = new float[][] {
        { 0,  0, _borderL, _borderT}, {_borderL,  0, _ts, _borderT}, {_tw - _borderR,  0, _borderR, _borderT},
        { 0, _borderT, _borderL, _ts}, {_borderL, _borderT, _ts, _ts}, {_tw - _borderR, _borderT, _borderR, _ts},
        { 0, _th - _borderB, _borderL, _borderB}, {_borderL, _th - _borderB, _ts, _borderB}, {_tw - _borderR, _th - _borderB, _borderR, _borderB}
    };
    
    for(int i = 0; i < _d.length; i++) {
      _d[i].setXYWH(border[i][0] + _loc[0], border[i][1] + _loc[1], border[i][2], border[i][3]);
      _d[i].setTXYWH(borderS[i][0], borderS[i][1], borderS[i][2], borderS[i][3]);
      _d[i].createQuad();
    }
  }
  
  public void draw() {
    if(!_visible) return;
    
    for(int i = 0; i < _d.length; i++) {
      _d[i].draw();
    }
  }

  @Override
  public void createQuad() {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void createBorder() {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void createLine() {
    // TODO Auto-generated method stub
    
  }
}