package org.telldus;

import java.io.IOException;
import java.io.InputStream;

public class Util {

  /*
   * Read bytes from the stream until we got an "i1s" that indicates the end of the message.
   * Probably not the most efficient way to do it, but will do for now...
   */
  public static String getMessage(InputStream is) throws IOException {
    byte[] buffer = new byte[1024];
    int read;
    int state = 0;
    int idx = 0;
    while((read = is.read()) != -1) {
      buffer[idx++] = (byte)read;
      switch(state) {
        case 0:
          if((byte)read == 'i') {
            state++;
          } else {
            state = 0;
          }
        break;
        case 1:
          if((byte)read == '1') {
            state++;
          } else {
            state = 0;
          }
        break;
        case 2:
          if ((byte) read == 's') {
            return new String(buffer, 0, idx);
          } else {
            state = 0;
          }
      }
    }
    return null;
	}
}
