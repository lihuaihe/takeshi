package com.takeshi.config.security;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 保存过滤器里面的流
 *
 * @author 725
 */
public class TakeshiHttpRequestWrapper extends HttpServletRequestWrapper {

    /**
     * 用于将流保存下来
     */
    private final byte[] bodyByte;

    /**
     * 包装request
     *
     * @param request request
     * @throws IOException IOException
     */
    public TakeshiHttpRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        bodyByte = request.getInputStream().readAllBytes();
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bodyByte);
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {

            }

            @Override
            public int read() throws IOException {
                return byteArrayInputStream.read();
            }
        };
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(this.getInputStream()));
    }

}
