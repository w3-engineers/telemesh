package com.w3engineers.appshare.util.lib;

import android.support.annotation.NonNull;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.TimeZone;

/*
 * ============================================================================
 * Copyright (C) 2018 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

public class InstantServer {

    private int port;
    private static InstantServer instantServer = new InstantServer();
    /**
     * Response status type
     */
    private String filePath;

    /**
     * Response mime type
     */

    private static final String
            MIME_PLAINTEXT = "text/plain",
            MIME_DEFAULT_BINARY = "application/octet-stream";

    @NonNull
    public synchronized static InstantServer getInstance() {
        return instantServer;
    }

    private InstantServer() {

    }

    /**
     * Initiating and starting server after calling this constructor
     */
//    InstantServer(int port, String filePath) {
//        startServer();
//    }

    @NonNull
    public InstantServer setPort(int port) {
        this.port = port;
        return this;
    }

    @NonNull
    public InstantServer setFilePath(@NonNull String filePath) {
        this.filePath = filePath;
        return this;
    }

    /**
     * Purpose of this callback is
     * How amount of data has been passed
     */
    public interface PercentCallback {
        void showPercent(int percent);
    }

    /** A server socket for network communication */
    private ServerSocket serverSocket;
    private Thread thread;

    /**
     * The purpose of this API is socket initialize
     * and ready for accepting any type of request
     * which through using POST or GET method
     * then start this server
     */
    public void startServer(){
        try {
            final ServerSocket serverSocket = new ServerSocket(port);
            final String fileLocation = filePath;
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (true) {
                            Socket socket = serverSocket.accept();
                            new HTTPRequestSession(socket, fileLocation);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            thread.setDaemon(true);
            thread.start();

            this.serverSocket = serverSocket;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * When our purpose is done then we stop this server
     * and this time close our server socket
     */
    public void stopServer() {
        try {
            serverSocket.close();
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles one session, i.e. parses the HTTP request
     * and returns the local Response.
     */
    private class HTTPRequestSession implements Runnable {

        private Socket socket;
        private Properties methods, header, parameters;
        private SimpleDateFormat simpleDateFormat;

        /**
         * Response status type
         */
        private String HTTP_OK = "200 OK", HTTP_RANGE_NOT_SATISFIABLE = "416 Requested Range Not Satisfiable",
                HTTP_PARTIALCONTENT = "206 Partial Content",
                HTTP_NOTMODIFIED = "304 Not Modified", filePath;

        /**
         * Response mime type
         */
        private static final String
                MIME_HTML = "text/html",
                MIME_CSS = "text/css",
                MIME_PNG = "image/png",
                MIME_ICO = "image/x-icon";

        /**
         * Response file type
         */
        private static final String
                FILE_TYPE_CSS = ".css",
                FILE_TYPE_PNG = ".png",
                FILE_TYPE_JPG = ".jpg",
                FILE_TYPE_ICO = ".ico",
                TYPE_STRING = ".str";

        /**
         * @param socket
         * When server got any type of information
         * then it starts its session
         */
        HTTPRequestSession(Socket socket, String filePath) {
            this.filePath = filePath;
            this.socket = socket;
            Thread thread = new Thread(this);
            thread.setDaemon(true);
            thread.start();
        }

        /**
         * Reset previous session data and clear all info
         * re-initiate it again
         */
        private void reset() {
            if (methods != null)
                methods.clear();
            if (header != null)
                header.clear();
            if (parameters != null)
                parameters.clear();
        }

        /**
         * Initiate all properties and
         * initiate time format for getting request from client
         */
        private void init() {

            methods = new Properties();
            header = new Properties();
            parameters = new Properties();

            simpleDateFormat = new SimpleDateFormat("E, d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        }

        @Override
        public void run() {
            try {
                InputStream inputStream = socket.getInputStream();
                if (inputStream == null)
                    return;

                // Read the first 8192 bytes.
                // The full header should fit in here.
                // Default header limit is 8KB.

                int bufferSize = 8192;
                byte[] buffer = new byte[bufferSize];
                int reqLength = inputStream.read(buffer, 0, bufferSize);
                if (reqLength <= 0) return;

                String URI = "uri";

                reset();
                init();

                // Create a BufferedReader for parsing the header data.
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buffer, 0, reqLength);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(byteArrayInputStream));

                decodeHeaderData(bufferedReader);

                Response response = serveFile(methods.getProperty(URI), header);

                if (response != null) {
                    sendResponse(response);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * Prepare and serve any type of response after getting any request
         * @param uri -> serve any response based on requested uri
         * @param header -> header is needed when serve a media or apk content
         * @return -> the desire response based on client request
         */
        private Response serveFile(String uri, Properties header) {

            Response response = null;
            try {

                if (uri.contains("TeleMesh")) {
                    response = prepareFile(header);
                } else {
                    if (uri.contains(FILE_TYPE_CSS)) {
                        response = new Response(HTTP_OK, MIME_CSS, InAppShareWebController.getInAppShareWebController().getWebSupportFile(uri));
                    } else if (uri.contains(FILE_TYPE_PNG)) {
                        response = new Response(HTTP_OK, MIME_PNG, InAppShareWebController.getInAppShareWebController().getWebSupportFile(uri));
                    } else if (uri.contains(FILE_TYPE_JPG)) {
                        response = new Response(HTTP_OK, FILE_TYPE_JPG, InAppShareWebController.getInAppShareWebController().getWebSupportFile(uri));
                    } else if (uri.contains(FILE_TYPE_ICO)) {
                        response = new Response(HTTP_OK, MIME_ICO, InAppShareWebController.getInAppShareWebController().getWebSupportFile(uri));
                    } else if (uri.contains(TYPE_STRING)) {
                        response = new Response(HTTP_OK, MIME_PNG, InAppShareWebController.getInAppShareWebController().getWebSupportFile(uri));
                    } else {
                        InputStream inputStream = InAppShareWebController.getInAppShareWebController().getWebFile();
                        response = new Response(HTTP_OK, MIME_HTML, inputStream);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return response;
        }

        /**
         * Prepare a file response when client request to download a file
         * @param header -> Adding header params for preparing response file
         * @return a response which contains the input stream of selected file,
         * status and mime type also
         */
        private Response prepareFile(Properties header) {
            Response response = null;
            String mime = MIME_DEFAULT_BINARY;

            try {
                File f = new File(filePath);

                // Calculate etag
                String etag = Integer.toHexString((f.getAbsolutePath() + f.lastModified() + "" + f.length()).hashCode());

                // Support (simple) skipping:
                long startFrom = 0;
                long endAt = -1;
                String range = header.getProperty("range");
                if (range != null) {
                    if (range.startsWith("bytes=")) {
                        range = range.substring("bytes=".length());
                        int minus = range.indexOf('-');
                        try {
                            if (minus > 0) {
                                startFrom = Long.parseLong(range.substring(0, minus));
                                endAt = Long.parseLong(range.substring(minus + 1));
                            }
                        } catch (NumberFormatException nfe) {
                            nfe.printStackTrace();
                        }
                    }
                }

                // Change return code and add Content-Range header when skipping is requested
                long fileLen = f.length();
                if (range != null && startFrom >= 0) {
                    if (startFrom >= fileLen) {
                        response = new Response(HTTP_RANGE_NOT_SATISFIABLE, MIME_PLAINTEXT, "");
                        response.addHeader("Content-Range", "bytes 0-0/" + fileLen);
                        response.addHeader("ETag", etag);
                    } else {
                        if (endAt < 0)
                            endAt = fileLen - 1;
                        long newLen = endAt - startFrom + 1;
                        if (newLen < 0) newLen = 0;

                        final long dataLen = newLen;
                        FileInputStream fis = new FileInputStream(f) {
                            public int available() throws IOException {
                                return (int) dataLen;
                            }
                        };
                        fis.skip(startFrom);

                        response = new Response(HTTP_PARTIALCONTENT, mime, fis);
                        response.addHeader("Content-Length", "" + dataLen);
                        response.addHeader("Content-Range", "bytes " + startFrom + "-" + endAt + "/" + fileLen);
                        response.addHeader("ETag", etag);
                    }
                } else {
                    if (etag.equals(header.getProperty("if-none-match")))
                        response = new Response(HTTP_NOTMODIFIED, mime, "");
                    else {
                        response = new Response(HTTP_OK, mime, new FileInputStream(f));
                        response.addHeader("Content-Length", "" + fileLen);
                        response.addHeader("ETag", etag);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (response != null) {
                response.addHeader("Accept-Ranges", "bytes");
            }

            return response;
        }

        /**
         * Purpose of this api send a response which already prepared by URI and property
         * @param response -> Sends given local Response to the socket.
         */
        private void sendResponse(Response response) {

            String status = response.status;
            String mimeType = response.mimeType;
            InputStream inputStream = response.inputStream;

            int theBufferSize = 16 * 1024;

            try {
                if (status == null)
                    throw new Error("sendResponse(): MessageBase can't be null.");

                OutputStream out = socket.getOutputStream();
                PrintWriter pw = new PrintWriter(out);
                pw.print("HTTP/1.0 " + status + " \r\n");

                if (mimeType != null)
                    pw.print("Content-Type: " + mimeType + "\r\n");

                if (header == null || header.getProperty("Date") == null)
                    pw.print("Date: " + simpleDateFormat.format(new Date()) + "\r\n");

                if (header != null) {
                    Enumeration e = header.keys();
                    while (e.hasMoreElements()) {
                        String key = (String) e.nextElement();
                        String value = header.getProperty(key);
                        pw.print(key + ": " + value + "\r\n");
                    }
                }

                pw.print("\r\n");
                pw.flush();

                if (inputStream != null) {
                    int pending = inputStream.available();    // This is to support partial sends, see serveFile()
                    byte[] buff = new byte[theBufferSize];
                    long lengthOfFile = inputStream.available(), total = 0;
                    while (pending > 0) {
                        int read = inputStream.read(buff, 0, ((pending > theBufferSize) ? theBufferSize : pending));
                        if (read <= 0) break;
                        out.write(buff, 0, read);
                        pending -= read;

                        total += read;
                        int responsePercentage = (int) ((total * 100) / lengthOfFile);
                        // show percent using a callback
                    }
                }
                out.flush();
                out.close();
                if (inputStream != null)
                    inputStream.close();

            } catch (IOException ioe) {
                try {
                    socket.close();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }

        /**
         * Decodes the sent headers and loads the data into
         * java Properties' key - value pairs
         **/
        private void decodeHeaderData(BufferedReader bufferedReader) {

            try {
                String readLine = bufferedReader.readLine();
                StringTokenizer stringTokenizer = new StringTokenizer(readLine);

                if (!stringTokenizer.hasMoreTokens())
                    return;

                String URI = "uri";
                String METHOD = "method";

                // Parse method and uri from request
                methods.setProperty(METHOD, stringTokenizer.nextToken());
                methods.setProperty(URI, stringTokenizer.nextToken());

                // Decode the header into parameters and header java properties
                String line = bufferedReader.readLine();
                while (line != null && line.trim().length() > 0) {
                    int p = line.indexOf(':');
                    if (p >= 0)
                        header.put(line.substring(0, p).trim().toLowerCase(Locale.getDefault()),
                                line.substring(p + 1).trim());
                    line = bufferedReader.readLine();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * HTTP local Response.
     * Return one of these from serve().
     * Preparing any response and
     * set all info in this response obj
     */
    private class Response {
        String status, mimeType;
        InputStream inputStream;
        Properties header = new Properties();

        /**
         * Inject any header for preparing response then use this api
         */
        void addHeader(String key, String value) {
            header.put(key, value);
        }

        /**
         * Basic constructor for sending file response
         */
        Response(String status, String mimeType, InputStream inputStream) {
            this.status = status;
            this.mimeType = mimeType;
            this.inputStream = inputStream;
        }

        /**
         * Convenience method that makes an InputStream out of given text.
         */
        Response(String status, String mimeType, String txt) {
            this.status = status;
            this.mimeType = mimeType;
            try {
                this.inputStream = new ByteArrayInputStream(txt.getBytes("UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }



}
