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

    private boolean waitingForData = false;

    Thread socketServerThread = null;

    ServerSocket serverSocket;

    /**
     * @param name String
     * @param rivalName String
     */
    public ServerPlayer(String name, String rivalName){
        super(name, rivalName);
        hisTurn(true);
        restartServer();
    }

    public void restartServer() {
        if(socketServerThread != null) {
            socketServerThread.interrupt();
            socketServerThread = null;
        }

        socketServerThread = new Thread(new SocketServerThread());
        socketServerThread.start();
    }

    public void sendData(){
        waitingForData = true;
    }

    public void haveData(){
        int visibleThumbs = rival.getShowsThumbs() + this.getShowsThumbs();

        if (rival.isHisTurn()) {
            if (visibleThumbs == rival.getChongs()) rival.setThumbs(rival.getThumbs() - 1);
        } else if (this.isHisTurn) {
            if (visibleThumbs == this.getChongs()) this.setThumbs(this.getThumbs() - 1);
        }

        rival.hasData(true);
        waitingForData = false;
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

                    switch (messageType){
                        case MESSAGE_CONNECT:
                            CreateGameFragment.getInstance().startGame();
                            break;
                        case MESSAGE_DATA:
                            consumeData(message);
                            break;
                        case MESSAGE_END:
                            break;
                    }

                    if(messageType == MESSAGE_DATA && !waitingForData) continue;

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
                        msgReply = createConnectMessage();
                        break;
                    case MESSAGE_DATA:
                        msgReply = createDataMessage();
                        break;
                    case MESSAGE_END:
                        msgReply = "end";
                        break;
                }

                Log.d("MESSAGE", msgReply);

                printStream.print(msgReply);
                printStream.close();

                haveData();

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    private void consumeData(String message) {
        String[] data = message.split(";");

        ServerPlayer.this.rival.setShowsThumbs(Integer.valueOf(data[1]));
        ServerPlayer.this.rival.setChongs(Integer.valueOf(data[2]));
        ServerPlayer.this.rival.setThumbs(Integer.valueOf(data[3]));
    }

    /**
     * @return String
     */
    private String createConnectMessage() {
        return "connect";
    }

    /**
     * @return String
     */
    private String createDataMessage() {

        String message = "data";

        message += ";" + String.valueOf(ServerPlayer.this.getShowsThumbs());
        message += ";" + String.valueOf(ServerPlayer.this.getChongs());
        message += ";" + String.valueOf(ServerPlayer.this.getThumbs());

        Log.d("DATA", message);

        return message;
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
