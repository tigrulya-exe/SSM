package org.smartdata.hdfs;

import org.apache.hadoop.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Consumer;

public class SmartIOUtils {

  /**
   * Copy of {@link IOUtils#copyBytes(InputStream, OutputStream, long, boolean)}
   * with custom buffer size support
   */
  public static void copyBytes(
      InputStream in,
      OutputStream out,
      int bufferSize,
      long count,
      boolean closeStreams) throws IOException {

  }

  /**
   * Copy of {@link IOUtils#copyBytes(InputStream, OutputStream, long, boolean)}
   * with custom buffer size support
   */
  public static void copyBytes(
      InputStream in,
      OutputStream out,
      int bufferSize,
      long count,
      boolean closeStreams) throws IOException {
    byte[] buf = new byte[bufferSize];
    long bytesRemaining = count;
    int bytesRead;

    try {
      while (bytesRemaining > 0) {
        int bytesToRead = (int)
            (bytesRemaining < buf.length ? bytesRemaining : buf.length);

        bytesRead = in.read(buf, 0, bytesToRead);
        if (bytesRead == -1) {
          break;
        }

        out.write(buf, 0, bytesRead);
        bytesRemaining -= bytesRead;
      }
      if (closeStreams) {
        out.close();
        out = null;
        in.close();
        in = null;
      }
    } finally {
      if (closeStreams) {
        IOUtils.closeStream(out);
        IOUtils.closeStream(in);
      }
    }
  }

}
