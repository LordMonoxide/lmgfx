package graphics.shared.gui.controls;

import graphics.shared.gui.Control;
import graphics.shared.gui.GUI;
import graphics.shared.textures.Texture;

public class Picture extends Control<Control.Events> {
  public Picture(GUI gui) {
    this(gui, false);
  }
  
  public Picture(GUI gui, boolean register) {
    super(gui, register);
    _events = new Control.Events(this);
    _acceptsFocus = false;
  }
  
  protected void resize() {
    _background.setWH(_loc[2], _loc[3]);
    _background.createQuad();
  }
  
  public Texture getTexture() {
    return _background.getTexture();
  }
  
  public void setTexture(Texture texture) {
    _background.setTexture(texture);
    
    if(texture != null) {
      _background.setColour(new float[] {1, 1, 1, 1});
      setWH(_background.getW(), _background.getH());
    } else {
      _background.setColour(null);
      _background.createQuad();
    }
  }
}