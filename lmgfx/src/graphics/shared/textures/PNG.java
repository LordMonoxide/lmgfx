package graphics.shared.textures;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;

public class PNG {
  private int _w, _h;
  
  public int getW() { return _w; }
  public int getH() { return _h; }
  
  public ByteBuffer load(String file) throws IOException {
    File f = new File("../gfx/textures/" + file);
    if(!f.exists()) throw new FileNotFoundException();
    InputStream in = null;
    
    try {
      in = new FileInputStream(f);
      PNGDecoder png = new PNGDecoder(in);
      
      _w = png.getWidth();
      _h = png.getHeight();
      
      ByteBuffer data = ByteBuffer.allocateDirect(4 * _w * _h);
      png.decode(data, _w * 4, Format.RGBA);
      data.flip();
      return data;
    } catch(IOException e) {
      throw e;
    } finally {
      in.close();
    }
  }
}