package org.smartdata.hdfs;


import lombok.Builder.Default;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.apache.hadoop.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Consumer;

@RequiredArgsConstructor
@Builder
public class StreamCopyHandler {
  private final InputStream inputStream;

  private final OutputStream outputStream;

  @Default
  private final int bufferSize = 4096;

  @Default
  private final long offset = 0;

  @Default
  private final long count = -1;

  @Default
  private final boolean closeStreams = false;

  @Default
  private final Consumer<Float> progressConsumer = progress -> {};

  public static Builder of(InputStream inputStream, OutputStream outputStream) {
    return new Builder()
        .inputStream(inputStream)
        .outputStream(outputStream);
  }

  public void runCopy() throws IOException {
    byte[] buf = new byte[bufferSize];
    long bytesRemaining = count;
    int bytesRead;

    try {
      maybeSkip();

      while (bytesRemaining > 0) {
        int bytesToRead = (int) Math.min(bytesRemaining, bufferSize);

        bytesRead = inputStream.read(buf, 0, bytesToRead);
        if (bytesRead == -1) {
          break;
        }

        outputStream.write(buf, 0, bytesRead);
        bytesRemaining -= bytesRead;

        progressConsumer.accept(((float) (count - bytesRemaining)) / count);
      }
    } finally {
      if (closeStreams) {
        IOUtils.closeStream(outputStream);
        IOUtils.closeStream(inputStream);
      }
    }
  }

  private void maybeSkip() throws IOException {
    if (offset != 0) {
      inputStream.skip(offset);
    }
  }
}
