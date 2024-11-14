package com.takeshi.config.security;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * TakeshiHttpResponseWrapper
 *
 * @author 七濑武【Nanase Takeshi】
 */
public class TakeshiHttpResponseWrapper extends HttpServletResponseWrapper {

    /**
     * ByteArrayOutputStream
     */
    private final ByteArrayOutputStream buffer;

    /**
     * ServletOutputStream
     */
    private final ServletOutputStream outputStream;

    /**
     * PrintWriter
     */
    private final PrintWriter writer;

    /**
     * 包装response
     *
     * @param response response
     * @throws IOException IOException
     */
    public TakeshiHttpResponseWrapper(HttpServletResponse response) throws IOException {
        super(response);
        buffer = new ByteArrayOutputStream();
        outputStream = new WrapperServletOutputStream(buffer);
        writer = new PrintWriter(new OutputStreamWriter(buffer, this.getCharacterEncoding()));
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return outputStream;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return writer;
    }

    @Override
    public void flushBuffer() throws IOException {
        if (writer != null) {
            writer.flush();
        }
        if (outputStream != null) {
            outputStream.flush();
        }
    }

    @Override
    public void resetBuffer() {
        buffer.reset();
    }

    /**
     * 获取内容
     *
     * @return 内容
     * @throws IOException IOException
     */
    public byte[] getResponseData() throws IOException {
        flushBuffer();
        return buffer.toByteArray();
    }

    private static class WrapperServletOutputStream extends ServletOutputStream {

        private final ByteArrayOutputStream outputStream;

        public WrapperServletOutputStream(ByteArrayOutputStream outputStream) {
            this.outputStream = outputStream;
        }

        @Override
        public void write(int b) throws IOException {
            outputStream.write(b);
        }

        @Override
        public void write(byte[] b) throws IOException {
            outputStream.write(b);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            outputStream.write(b, off, len);
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {
        }

    }

}
