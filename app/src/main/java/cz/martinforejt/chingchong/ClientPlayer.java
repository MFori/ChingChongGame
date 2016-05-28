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

    public ClientPlayer(String name, String rivalName){
        super(name, rivalName);
    }

    public void connect() {
        mAsync a = new mAsync("192.168.0.100", 8080);
        a.execute();
    }

    public class mAsync extends AsyncTask<Void, Void, Void> {

        String response = "";
        String dstAddress = "";
        int dstPort;

        public mAsync(String dstAddress, int dstPort) {
            this.dstAddress = dstAddress;
            this.dstPort = dstPort;
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            Socket socket = null;

            try {
                socket = new Socket(dstAddress, dstPort);

                //Send the message to the server
                OutputStream os = socket.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(os);
                BufferedWriter bw = new BufferedWriter(osw);


                String number = "connect";

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
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO HAVE DATA
            Log.d("EXECUTE",response);
            super.onPostExecute(result);
        }
    }

    public void sendData(){

    }

    public void haveData(){

    }

}
