package cz.martinforejt.chingchong;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Martin Forejt on 16.05.2016.
 * forejt.martin97@gmail.com
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
        this.ipAddress = ipAddress;
    }

    public void connect() {
        Async mAsync = new Async(ipAddress, ServerPlayer.socketServerPORT);
        mAsync.execute(MESSAGE_CONNECT);
        isConnect = false;
    }

    public void sendData() {
        Async mAsync = new Async(ipAddress, ServerPlayer.socketServerPORT);
        mAsync.execute(MESSAGE_DATA);
    }

    public void haveData() {

    }

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

            for (int i = 0; i < type.length; i++) {

                Socket socket = null;

                try {
                    socket = new Socket(dstAddress, dstPort);

                    //Send the message to the server
                    OutputStream os = socket.getOutputStream();
                    OutputStreamWriter osw = new OutputStreamWriter(os);
                    BufferedWriter bw = new BufferedWriter(osw);

                    String number = "";
                    if (type[0] == MESSAGE_CONNECT) number = "connect";
                    if (type[0] == MESSAGE_DATA) number = "data;123;4";
                    if (type[0] == MESSAGE_END) number = "end";

                    String sendMessage = number + "\n";
                    bw.write(sendMessage);
                    bw.flush();

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(
                            1024);
                    byte[] buffer = new byte[1024];

                    int bytesRead;
                    InputStream inputStream = socket.getInputStream();

			/*
             * notice: inputStream.read() will block if no data return
			 */
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        byteArrayOutputStream.write(buffer, 0, bytesRead);
                        response += byteArrayOutputStream.toString("UTF-8");
                    }

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
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO HAVE DATA
            Log.d("EXECUTE", response);
            if(response.equals("connect")) isConnect = true;

            asyncRunning = false;

            super.onPostExecute(result);
        }
    }

    public boolean isConnect() {
        return  isConnect;
    }

    public boolean isAsyncRunning() {
        return asyncRunning;
    }
}
