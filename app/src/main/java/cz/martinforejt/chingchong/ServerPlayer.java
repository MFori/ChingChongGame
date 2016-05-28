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

    /**
     * @param name String
     * @param rivalName String
     */
    public ServerPlayer(String name, String rivalName){
        super(name, rivalName);
        Thread socketServerThread = new Thread(new SocketServerThread());
        socketServerThread.start();
    }

    public void sendData(){

    }

    public void haveData(){

    }

    /**
     *
     */
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

                    int messageType = getMessageType(message);

                    SocketServerReplyThread socketServerReplyThread = new SocketServerReplyThread(socket, messageType);
                    socketServerReplyThread.run();

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * Read message type from message
         * @param message String
         * @return int
         */
        private int getMessageType(String message) {
            if(message.equals("connect")) return MESSAGE_CONNECT;
            else if(message.equals("end")) return MESSAGE_END;
            else {
                String[] data = message.split(";");
                if(data[0].equals("data")) return MESSAGE_DATA;
            }
            return 0;
        }

    }

    /**
     *
     */
    private class SocketServerReplyThread extends Thread {

        private Socket hostThreadSocket;
        private int message;

        /**
         * @param socket Socket
         * @param message int
         */
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
                        break;
                    case MESSAGE_DATA:
                        msgReply = "data";
                        break;
                    case MESSAGE_END:
                        msgReply = "end";
                        break;
                }

                Log.d("MESSAGE", msgReply);

                printStream.print(msgReply);
                printStream.close();

                //String message = "replayed: " + msgReply + "\n";

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    /**
     * Return server socket port (always 8080)
     * @return int
     */
    public int getPort() {
        return socketServerPORT;
    }

    /**
     * Return server socket ip address
     * @return String
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

    /**
     * Close server socket waiting...
     */
    public void onDestroy() {
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
