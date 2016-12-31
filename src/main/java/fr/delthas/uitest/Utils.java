package fr.delthas.uitest;

import org.lwjgl.BufferUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;

final class Utils {
  private Utils() {
    throw new IllegalStateException("This class cannot be instantiated!");
  }

  static BufferedInputStream getResource(String path) throws IOException {
    InputStream is = Utils.class.getResourceAsStream(path.startsWith("/") ? path : "/" + path);
    if (is == null && Files.isRegularFile(Paths.get(path))) {
      is = Files.newInputStream(Paths.get(path));
    }
    if (is != null) {
      return new BufferedInputStream(is);
    }
    throw new IOException("Resource " + path + " not found.");
  }

  private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
    ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
    buffer.flip();
    newBuffer.put(buffer);
    return newBuffer;
  }

  static ByteBuffer getResourceBuffer(String path) throws IOException {
    return getResourceBuffer(path, 1024);
  }

  static ByteBuffer getBuffer(InputStream is) throws IOException {
    return getBuffer(is, 1024);
  }

  static ByteBuffer getBuffer(InputStream is, int bufferSize) throws IOException {
    ByteBuffer buffer;
    try (InputStream source = new BufferedInputStream(is); ReadableByteChannel rbc = Channels.newChannel(source)) {
      buffer = BufferUtils.createByteBuffer(bufferSize);
      while (true) {
        int bytes = rbc.read(buffer);
        if (bytes == -1) {
          break;
        }
        if (buffer.remaining() == 0) {
          buffer = resizeBuffer(buffer, buffer.capacity() * 2);
        }
      }
    }
    buffer.flip();
    return buffer;
  }

  static ByteBuffer getResourceBuffer(String path, int bufferSize) throws IOException {
    ByteBuffer buffer;
    try (InputStream source = getResource(path); ReadableByteChannel rbc = Channels.newChannel(source)) {
      buffer = BufferUtils.createByteBuffer(bufferSize);
      while (true) {
        int bytes = rbc.read(buffer);
        if (bytes == -1) {
          break;
        }
        if (buffer.remaining() == 0) {
          buffer = resizeBuffer(buffer, buffer.capacity() * 2);
        }
      }
    }
    buffer.flip();
    return buffer;
  }
}
