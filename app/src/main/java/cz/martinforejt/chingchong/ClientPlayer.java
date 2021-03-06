package cz.martinforejt.chingchong;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Martin Forejt on 16.05.2016.
 * forejt.martin97@gmail.com
 * class ClientPlayer
 */
public class ClientPlayer extends Player {

    // Messages types
    static final int MESSAGE_CONNECT = 1;
    static final int MESSAGE_DATA = 2;
    static final int MESSAGE_END = 3;
    static final int MESSAGE_REMATCH = 4;
    static final int MESSAGE_PAUSE = 5;
    static final int MESSAGE_ERROR = 6;

    // Server ip Address
    private String ipAddress = "";
    private boolean isConnect = false;
    private boolean asyncRunning = false;
    private boolean isError = false;

    private boolean canConsumeData = false;

    private Async connAsync = null;

    /**
     * @param name      String
     * @param rivalName String
     * @param ipAddress String - server ip address
     */
    public ClientPlayer(String name, String rivalName, String ipAddress) {
        super(name, rivalName);
        hisTurn(true);
        rival.hisTurn(false);
        this.ipAddress = ipAddress;
    }

    /**
     * Change ip address of server
     *
     * @param ipAddress String
     * @return ClientPlayer
     */
    public ClientPlayer changeIp(String ipAddress) {
        this.ipAddress = ipAddress;
        return this;
    }

    /**
     * Connect to Server (server player)
     */
    public void connect() {
        connAsync = new Async(ipAddress, ServerPlayer.socketServerPORT);
        connAsync.execute(MESSAGE_CONNECT);
        isConnect = false;
        //asyncRunning = true;
    }

    public void stopConnect() {
        if (connAsync != null) connAsync.cancel(true);
    }

    /**
     * Send data to server
     */
    public void sendData() {
        Async mAsync = new Async(ipAddress, ServerPlayer.socketServerPORT);
        mAsync.execute(MESSAGE_DATA);
        isError = false;
    }

    public void prepareData() {
        canConsumeData = true;
    }

    /**
     * Send rematch request to server
     */
    public void rematch() {
        Async mAsync = new Async(ipAddress, ServerPlayer.socketServerPORT);
        mAsync.execute(MESSAGE_REMATCH);
        isConnect = false;
    }

    /**
     * Send pause message to server
     */
    public void sendPause() {
        Async mAsync = new Async(ipAddress, ServerPlayer.socketServerPORT);
        mAsync.execute(MESSAGE_PAUSE);
    }

    /**
     * Calculate and consume data from server
     */
    public void haveData() {
        if(canConsumeData) {
            Log.d("CONSUME", "cs");
            canConsumeData = false;

            int visibleThumbs = rival.getShowsThumbs() + this.getShowsThumbs();

            if (rival.isHisTurn()) {
                if (visibleThumbs == rival.getChongs()) rival.setThumbs(rival.getThumbs() - 1);
            } else if (this.isHisTurn) {
                if (visibleThumbs == this.getChongs()) this.setThumbs(this.getThumbs() - 1);
            }

            rival.hasData(true);
        }
    }

    /**
     * class Async
     */
    public class Async extends AsyncTask<Integer, Void, Void> {

        String response = "";
        String dstAddress = "";
        int dstPort;

        public Async(String dstAddress, int dstPort) {
            this.dstAddress = dstAddress;
            this.dstPort = dstPort;
        }

        @Override
        protected Void doInBackground(Integer... type) {

            asyncRunning = true;

            Socket socket = null;

            try {
                socket = new Socket(dstAddress, dstPort);

                //Send the message to the server
                OutputStream os = socket.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(os);
                BufferedWriter bw = new BufferedWriter(osw);

                String message;
                if (type[0] == MESSAGE_CONNECT) message = createConnectMessage();
                else if (type[0] == MESSAGE_DATA) message = createDataMessage();
                else if (type[0] == MESSAGE_END) message = createEndMessage();
                else if (type[0] == MESSAGE_REMATCH) message = createRematchMessage();
                else if (type[0] == MESSAGE_PAUSE) message = createPauseMessage();
                else return null;

                String sendMessage = message + "\n";
                bw.write(sendMessage);
                bw.flush();

                //if(type[0] == MESSAGE_CONNECT || type[0] == MESSAGE_REMATCH) {
                try {
                    Thread.sleep(150);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //}

                // Get reply from server
                InputStream inputStream = socket.getInputStream();

                BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder total = new StringBuilder();
                String line;
                while ((line = r.readLine()) != null) {
                    total.append(line).append('\n');
                }

                // final message
                response = total.toString().trim();

                os.close();
                inputStream.close();

            } catch (Exception e) {
                e.printStackTrace();

                response = "error";
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.d("Execute: ", response);

            int messageType = getMessageType(response);

            switch (messageType) {
                case MESSAGE_CONNECT:
                    isConnect = true;
                    consumeConnect(response);
                case MESSAGE_REMATCH:
                    isConnect = true;
                    break;
                case MESSAGE_DATA:
                    consumeData(response);
                    haveData();
                    break;
                case MESSAGE_END:
                    break;
                case MESSAGE_ERROR:
                    isError = true;
                    break;
            }

            super.onPostExecute(result);

            asyncRunning = false;
        }
    }

    /**
     * Return type of message
     *
     * @param message string
     * @return int - type
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
            case "error":
                return MESSAGE_ERROR;
        }
        return 0;
    }

    /**
     * Save data from server to rival object
     *
     * @param message string - data
     *                Data structure:
     *                " data ; rivalShowsThumbs ; rivalChongs ; rivalAvailableThumbs "
     */
    private void consumeData(String message) {
        String[] data = message.split(";");

        ClientPlayer.this.rival.setShowsThumbs(Integer.valueOf(data[1]));
        ClientPlayer.this.rival.setChongs(Integer.valueOf(data[2]));
        ClientPlayer.this.rival.setThumbs(Integer.valueOf(data[3]));
    }

    /**
     * @param message String
     */
    private void consumeConnect(String message) {
        String[] data = message.split(";");

        rival.setName(data[1]);
    }

    /**
     * Return the connect message
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
     * Return the end message
     *
     * @return String
     */
    private String createEndMessage() {
        return "end";
    }

    /**
     * Return the data message
     *
     * @return String
     */
    private String createDataMessage() {

        String message = "data";

        message += ";" + String.valueOf(ClientPlayer.this.getShowsThumbs());
        message += ";" + String.valueOf(ClientPlayer.this.getChongs());
        message += ";" + String.valueOf(ClientPlayer.this.getThumbs());

        Log.d("Data(send): ", message);

        return message;
    }

    /**
     * Return rematch message
     *
     * @return String
     */
    private String createRematchMessage() {
        return "rematch";
    }

    /**
     * Return pause message
     *
     * @return String
     */
    private String createPauseMessage() {
        return "pause";
    }

    /**
     * Check if is connect to server ( server player )
     *
     * @return bool
     */
    public boolean isConnect() {
        return isConnect;
    }

    /**
     * Check for error
     *
     * @return bool
     */
    public boolean isError() {
        return isError;
    }

    /**
     * Check if asyncTask is running
     *
     * @return bool
     */
    public boolean isAsyncRunning() {
        return asyncRunning;
    }

    /**
     * Required destroy method
     */
    public void onDestroy() {
        onPause();
    }

    /**
     *
     */
    public void onPause() {
        sendPause();
    }

    /**
     *
     */
    public void onResume() {

    }
}
