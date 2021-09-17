package com.example.demo;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class ClassReader2 {

    public static void main(String[] args) throws IOException {
        DefaultResourceLoader defaultResourceLoader = new DefaultResourceLoader();
        Resource resource = defaultResourceLoader.getResource(
                "jar:file:/app/demo-0.0.1-SNAPSHOT.jar!/BOOT-INF/classes/com/example/demo/HealthConfig.class");
        InputStream inputStream = resource.getInputStream();
        System.out.println(inputStream.getClass().getName());
        readStream(inputStream, true);
    }

    // The following code is licensed differently than the standard LICENSE file for this repository indicates.

    // ASM: a very small and fast Java bytecode manipulation framework
    // Copyright (c) 2000-2011 INRIA, France Telecom
    // All rights reserved.
    //
    // Redistribution and use in source and binary forms, with or without
    // modification, are permitted provided that the following conditions
    // are met:
    // 1. Redistributions of source code must retain the above copyright
    //    notice, this list of conditions and the following disclaimer.
    // 2. Redistributions in binary form must reproduce the above copyright
    //    notice, this list of conditions and the following disclaimer in the
    //    documentation and/or other materials provided with the distribution.
    // 3. Neither the name of the copyright holders nor the names of its
    //    contributors may be used to endorse or promote products derived from
    //    this software without specific prior written permission.
    //
    // THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
    // AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
    // IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
    // ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
    // LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
    // CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
    // SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
    // INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
    // CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
    // ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
    // THE POSSIBILITY OF SUCH DAMAGE.

    /**
     * Code copied from:
     * https://github.com/spring-projects/spring-framework/blob/d29d4d45aa31dedb176370cad1ca05bfb83846a0/spring-core/src/main/java/org/springframework/asm/ClassReader.java
     * Modifications made to the original code:
     * - added System.out.println statements to understand what's happening.
     * The code has been used here for the purpose of creating reproduction steps for the issue.
     */
    private static final int MAX_BUFFER_SIZE = 1024 * 1024;
    private static final int INPUT_STREAM_DATA_CHUNK_SIZE = 4096;

    private static byte[] readStream(final InputStream inputStream, final boolean close)
            throws IOException {
        if (inputStream == null) {
            throw new IOException("Class not found");
        }
        int bufferSize = calculateBufferSize(inputStream);
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            byte[] data = new byte[bufferSize];
            int bytesRead;
            int readCount = 0;
            while ((bytesRead = inputStream.read(data, 0, bufferSize)) != -1) {
                System.out.println("readCount: " + readCount + ", bytesRead: " + bytesRead + ", data:" + Arrays.toString(data));
                outputStream.write(data, 0, bytesRead);
                readCount++;
            }
            System.out.println("readCount: " + readCount + ", bytesRead: " + bytesRead + ", data:" + Arrays.toString(data));
            outputStream.flush();
            if (readCount == 1) {
                return data;
            }
            return outputStream.toByteArray();
        } finally {
            if (close) {
                inputStream.close();
            }
        }
    }

    private static int calculateBufferSize(final InputStream inputStream) throws IOException {
        int expectedLength = inputStream.available();
        /*
         * Some implementations can return 0 while holding available data
         * (e.g. new FileInputStream("/proc/a_file"))
         * Also in some pathological cases a very small number might be returned,
         * and in this case we use default size
         */
        if (expectedLength < 256) {
            return INPUT_STREAM_DATA_CHUNK_SIZE;
        }
        return Math.min(expectedLength, MAX_BUFFER_SIZE);
    }
}
