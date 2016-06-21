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
 * class ServerPlayer
 */
public class ServerPlayer extends Player {

    static final int socketServerPORT = 8080;

    // Message types:
    static final int MESSAGE_CONNECT = 1;
    static final int MESSAGE_DATA = 2;
    static final int MESSAGE_END = 3;
    static final int MESSAGE_REMATCH = 4;
    static final int MESSAGE_PAUSE = 5;

    private boolean waitingForData = false;
    private boolean isRunning = false;
    private boolean wantRematch = false;

    private String clientIp = null;
    private boolean clientWantRematch = false;
    private boolean clientPaused = false;

    Thread socketServerThread = null;

    ServerSocket serverSocket;

    /**
     * @param name      String
     * @param rivalName String
     */
    public ServerPlayer(String name, String rivalName) {
        super(name, rivalName);
        hisTurn(false);
        rival.hisTurn(true);
        restartServer();
    }

    /**
     * Restart server socket and thread
     */
    public void restartServer() {
        stopServer();
        startServer();
    }

    /**
     * Start server thread
     * Open server socket
     */
    public void startServer() {
        isRunning = true;
        socketServerThread = new Thread(new SocketServerThread());
        socketServerThread.start();
    }

    /**
     * Close socket, stop thread
     */
    public void stopServer() {
        isRunning = false;
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        serverSocket = null;
        if (socketServerThread != null) {
            socketServerThread.interrupt();
            socketServerThread = null;
        }
    }

    /**
     * Open waiting for game data from client
     */
    public void sendData() {
        waitingForData = true;
    }

    /**
     * Calculate received data
     */
    public void haveData() {
        int visibleThumbs = rival.getShowsThumbs() + this.getShowsThumbs();

        Log.d("VISIBLE", String.valueOf(visibleThumbs));
        Log.d("VISIBLE", String.valueOf(getChongs()));
        if (rival.isHisTurn()) {
            Log.d("VISIBLE", "RIVAL");
            if (visibleThumbs == rival.getChongs()) rival.setThumbs(rival.getThumbs() - 1);
        } else if (this.isHisTurn) {
            Log.d("VISIBLE", "JA");
            if (visibleThumbs == this.getChongs()) {
                Log.d("VISIBLE", "ROVNA");
                this.setThumbs(this.getThumbs() - 1);
            }
        }

        rival.hasData(true);
        waitingForData = false;
        clientPaused = false;
    }

    /**
     * Main server Thread
     */
    private class SocketServerThread extends Thread {

        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(socketServerPORT);

                while (isRunning) {

                    Socket socket = serverSocket.accept();

                    InputStream is = socket.getInputStream();
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader br = new BufferedReader(isr);
                    String message = br.readLine();

                    if (clientIp == null || clientIp.equals(socket.getInetAddress().toString())) {
                        int messageType = getMessageType(message);

                        switch (messageType) {
                            case MESSAGE_CONNECT:
                                consumeConnect(message, socket);
                                break;
                            case MESSAGE_DATA:
                                consumeData(message);
                                break;
                            case MESSAGE_END:
                                break;
                            case MESSAGE_REMATCH:
                                clientWantRematch = true;
                                //wantRematch = false;
                                break;
                            case MESSAGE_PAUSE:
                                clientPaused = true;
                                break;
                        }

                        if (messageType != MESSAGE_CONNECT && clientIp == null) continue;
                        if (messageType == MESSAGE_PAUSE) continue;

                        SocketServerReplyThread socketServerReplyThread = new SocketServerReplyThread(socket, messageType);
                        socketServerReplyThread.run();
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * Read message type from message
         *
         * @param message String
         * @return int
         */
        private int getMessageType(String message) {
            String[] data = message.split(";");
            switch (data[0]) {
                case "connect":
                    return MESSAGE_CONNECT;
                case "end":
                    return MESSAGE_END;
                case "data":
                    return MESSAGE_DATA;
                case "rematch":
                    return MESSAGE_REMATCH;
                case "pause":
                    return MESSAGE_PAUSE;
            }
            return 0;
        }

    }

    /**
     * Thread for replying to client
     */
    private class SocketServerReplyThread extends Thread {

        private Socket hostThreadSocket;
        private int message;

        /**
         * @param socket  Socket
         * @param message int
         */
        SocketServerReplyThread(Socket socket, int message) {
            hostThreadSocket = socket;
            this.message = message;
        }

        @Override
        public void run() {
            OutputStream outputStream;
            String msgReply = "";

            try {
                outputStream = hostThreadSocket.getOutputStream();
                PrintStream printStream = new PrintStream(outputStream);

                switch (message) {
                    case MESSAGE_CONNECT:
                        msgReply = createConnectMessage();
                        break;
                    case MESSAGE_DATA:
                        if (!waitingForData) msgReply = "error";
                        else msgReply = createDataMessage();
                        break;
                    case MESSAGE_END:
                        msgReply = "end";
                        break;
                    case MESSAGE_REMATCH:
                        if (wantRematch) {
                            msgReply = "rematch";
                            ResultFragment.getInstance().rematch();
                            wantRematch = false;
                        } else msgReply = "";
                        break;
                }

                Log.d("MESSAGE", msgReply);

                printStream.print(msgReply);
                printStream.close();

                if(!rival.hasData()) haveData();

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    /**
     * Consume game data from client
     *
     * @param message String
     */
    private void consumeData(String message) {
        String[] data = message.split(";");

        Log.d("CONSUME", message);

        ServerPlayer.this.rival.setShowsThumbs(Integer.valueOf(data[1]));
        ServerPlayer.this.rival.setChongs(Integer.valueOf(data[2]));
        ServerPlayer.this.rival.setThumbs(Integer.valueOf(data[3]));
    }

    /**
     * Consume connect data from client
     *
     * @param message String
     */
    private void consumeConnect(String message, Socket socket) {
        if (clientIp == null) clientIp = socket.getInetAddress().toString();
        CreateGameFragment.getInstance().startGame();

        String[] data = message.split(";");

        rival.setName(data[1]);
    }

    /**
     * Create message for connect reply
     *
     * @return String
     */
    private String createConnectMessage() {
        String message = "connect";

        message += ";";
        message += this.getName();

        return message;
    }

    /**
     * Create message for data reply
     *
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
     * Check if client send rematch request
     *
     * @return bool
     */
    public boolean clientWantRematch() {
        return clientWantRematch;
    }

    /**
     * @param wantRematch bool
     * @return ServerPlayer
     */
    public ServerPlayer wantRematch(boolean wantRematch) {
        this.wantRematch = wantRematch;
        return this;
    }

    /**
     * Check if client is paused
     *
     * @return bool
     */
    public boolean isClientPaused() {
        return clientPaused;
    }

    /**
     * Return server socket port (always 8080)
     *
     * @return int
     */
    public int getPort() {
        return socketServerPORT;
    }

    /**
     * Return server socket ip address
     *
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
        Log.d("SERVER", "DESTROY");
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        stopServer();
    }

    /**
     * Player on pause
     */
    public void onPause() {
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        stopServer();
    }

    /**
     * Player on resume
     */
    public void onResume() {
        startServer();
    }

}
