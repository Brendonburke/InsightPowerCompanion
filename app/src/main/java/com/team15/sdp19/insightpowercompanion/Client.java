package com.team15.sdp19.insightpowercompanion;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Client
{
    // initialize socket and input output streams
    private Socket socket            = null;
    private DataInputStream  input   = null;
    private DataOutputStream out     = null;

    // constructor to put ip address and port

    public static void main(String args[]) throws IOException {
        Socket sock = new Socket("127.0.0.1", 10568);
        System.out.println("Connected");
        while (true) {
            InputStream in = sock.getInputStream();
            System.out.println("here");
            String out = streamToString(in);
            System.out.println(out);
        }
    }
    private static String streamToString(InputStream is){
        System.out.println("test");
        Scanner s = new Scanner(is,"UTF-8").useDelimiter("\\A");
        System.out.println("test again");
        return  s.nextLine();
    }
}
  /*  public static byte [] toByteArray(InputStream in) throws IOException{
        ByteArrayOutputStream o = new ByteArrayOutputStream();

        byte [] buffer = new byte[1024];
        int len;
        while ((len = in.read(buffer)) != -1){
            o.write(buffer,0,len);
        }
        return o.toByteArray();
    }
    public static double toDouble(byte [] bytes){
        return ByteBuffer.wrap(bytes).getDouble();
    }
} */
