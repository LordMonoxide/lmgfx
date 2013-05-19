package main;

import graphics.gl00.Context;
import graphics.gl00.Game;

public class Main {
  public static void main(String[] args) {
    Context c = new graphics.gl14.Context();
    if(c.create(new Game() {
      public void init() {
        
      }
      
      public void destroy() {
        
      }
    })) {
      c.run();
    }
  }
}