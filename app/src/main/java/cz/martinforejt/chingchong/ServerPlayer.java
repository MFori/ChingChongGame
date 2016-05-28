package cz.martinforejt.chingchong;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by Martin Forejt on 16.05.2016.
 * forejt.martin97@gmail.com
 */
public class ServerPlayer extends Player {

    static final int socketServerPORT = 8080;

    static final int MESSAGE_CONNECT = 1;
    static final int MESSAGE_DATA = 2;
    static final int MESSAGE_END = 3;

    ServerSocket serverSocket;

    public ServerPlayer(String name, String rivalName){
        super(name, rivalName);
        Thread socketServerThread = new Thread(new SocketServerThread());
        socketServerThread.start();
    }

    public void sendData(){

    }

    public void haveData(){

    }

    private class SocketServerThread extends Thread {

        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(socketServerPORT);

                while (true) {
                    Socket socket = serverSocket.accept();
                    InputStream is = socket.getInputStream();
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader br = new BufferedReader(isr);
                    String message = br.readLine();

                    String message1 = message + "#" + " from "
                            + socket.getInetAddress() + ":"
                            + socket.getPort() + "\n";

                    int messageType = 0;
                    if(message.equals("connect")) messageType = MESSAGE_CONNECT;

                    SocketServerReplyThread socketServerReplyThread = new SocketServerReplyThread(socket, messageType);
                    socketServerReplyThread.run();

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private class SocketServerReplyThread extends Thread {

        private Socket hostThreadSocket;
        private int message;

        SocketServerReplyThread(Socket socket, int message) {
            hostThreadSocket = socket;
            this.message = message;
        }

        @Override
        public void run() {
            OutputStream outputStream;
            String msgReply = "Hello from Server, you are #";

            try {
                outputStream = hostThreadSocket.getOutputStream();
                PrintStream printStream = new PrintStream(outputStream);

                switch (message){
                    case MESSAGE_CONNECT:
                        msgReply = "connect";
                        Log.d("CONNECT", "YES");
                        break;
                    case MESSAGE_DATA:
                        break;
                    case MESSAGE_END:
                        break;
                }

                printStream.print(msgReply);
                printStream.close();

                //String message = "replayed: " + msgReply + "\n";

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    public int getPort() {
        return socketServerPORT;
    }

    /**
     *
     * @return
     */
    public String getIpAddress() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces
                        .nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface
                        .getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress
                            .nextElement();

                    if (inetAddress.isSiteLocalAddress()) {
                        ip += inetAddress.getHostAddress();
                    }
                }
            }

        } catch (SocketException e) {
            e.printStackTrace();
        }
        return ip;
    }

    public void onDestroy() {
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

}
