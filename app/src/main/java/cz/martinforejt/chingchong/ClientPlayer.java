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

    static final int MESSAGE_CONNECT = 1;
    static final int MESSAGE_DATA = 2;
    static final int MESSAGE_END = 3;

    private String ipAddress = "";
    private boolean isConnect = false;
    private boolean asyncRunning = false;

    public ClientPlayer(String name, String rivalName, String ipAddress) {
        super(name, rivalName);
        hisTurn(false);
        this.ipAddress = ipAddress;
    }

    /**
     *
     */
    public void connect() {
        Async mAsync = new Async(ipAddress, ServerPlayer.socketServerPORT);
        mAsync.execute(MESSAGE_CONNECT);
        isConnect = false;
    }

    /**
     *
     */
    public void sendData() {
        Async mAsync = new Async(ipAddress, ServerPlayer.socketServerPORT);
        mAsync.execute(MESSAGE_DATA);
    }

    /**
     *
     */
    public void haveData() {
        int visibleThumbs = rival.getShowsThumbs() + this.getShowsThumbs();

        if (rival.isHisTurn()) {
            if (visibleThumbs == rival.getChongs()) rival.setThumbs(rival.getThumbs() - 1);
        } else if (this.isHisTurn) {
            if (visibleThumbs == this.getChongs()) this.setThumbs(this.getThumbs() - 1);
        }

        rival.hasData(true);
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

                String message = "";
                if (type[0] == MESSAGE_CONNECT) message = createConnectMessage();
                if (type[0] == MESSAGE_DATA) message = createDataMessage();
                if (type[0] == MESSAGE_END) message = createEndMessage();

                String sendMessage = message + "\n";
                bw.write(sendMessage);
                bw.flush();

                try {
                    Thread.sleep(200);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                InputStream inputStream = socket.getInputStream();

                BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder total = new StringBuilder();
                String line;
                while ((line = r.readLine()) != null) {
                    total.append(line).append('\n');
                }

                response = total.toString().trim();

                os.close();
                inputStream.close();

            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "IOException: " + e.toString();
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.d("EXECUTE", response);

            int messageType = getMessageType(response);

            switch (messageType) {
                case MESSAGE_CONNECT:
                    isConnect = true;
                    break;
                case MESSAGE_DATA:
                    consumeData(response);
                    haveData();
                    break;
                case MESSAGE_END:
                    break;
            }

            asyncRunning = false;

            super.onPostExecute(result);
        }
    }

    /**
     * @param message string
     * @return int
     */
    private int getMessageType(String message) {
        if (message.equals("connect")) return MESSAGE_CONNECT;
        else if (message.equals("end")) return MESSAGE_END;
        else {
            String[] data = message.split(";");
            if (data[0].equals("data")) return MESSAGE_DATA;
        }
        return 0;
    }

    /**
     * @param message string
     */
    private void consumeData(String message) {
        String[] data = message.split(";");

        ClientPlayer.this.rival.setShowsThumbs(Integer.valueOf(data[1]));
        ClientPlayer.this.rival.setChongs(Integer.valueOf(data[2]));
        ClientPlayer.this.rival.setThumbs(Integer.valueOf(data[3]));
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
    private String createEndMessage() {
        return "end";
    }

    /**
     * @return String
     */
    private String createDataMessage() {

        String message = "data";

        message += ";" + String.valueOf(ClientPlayer.this.getShowsThumbs());
        message += ";" + String.valueOf(ClientPlayer.this.getChongs());
        message += ";" + String.valueOf(ClientPlayer.this.getThumbs());

        Log.d("DATA", message);

        return message;
    }

    /**
     * @return bool
     */
    public boolean isConnect() {
        return isConnect;
    }

    /**
     * @return bool
     */
    public boolean isAsyncRunning() {
        return asyncRunning;
    }

    /**
     *
     */
    public void onDestroy() {

    }
}
