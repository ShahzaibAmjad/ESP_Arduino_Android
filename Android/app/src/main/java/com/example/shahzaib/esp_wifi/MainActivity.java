package com.example.shahzaib.esp_wifi;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {

    Button btn1, btn2;
    public static String baseIpAdress = "192.168.4.1";
    public static String portNumber = "80";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        btn1 = (Button) findViewById(R.id.button1);
        btn2 = (Button) findViewById(R.id.button2);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    if(baseIpAdress.length()>0 && portNumber.length()>0) {
                        new HttpRequestAsyncTask(
                                baseIpAdress, portNumber, "ctrl"+"1"
                        ).execute();
                    }
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(baseIpAdress.length()>0 && portNumber.length()>0) {
                    new HttpRequestAsyncTask(
                            baseIpAdress, portNumber, "ctrl"+"2"
                    ).execute();
                }
            }
        });
    }

    public String sendRequest(String baseIpAdress, String portNumber, String parameterName) {
        String serverResponse = "ERROR";

        try {

            DefaultHttpClient httpclient = new DefaultHttpClient(); // create an HTTP client
            HttpParams httpParameters = new BasicHttpParams();
            int timeoutConnection = 250;
            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
            int timeoutSocket = 250;
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
            httpclient.setParams(httpParameters);
            URI website = new URI("http://"+baseIpAdress+":"+portNumber+"/?"+parameterName);
            HttpGet getRequest = new HttpGet(); // create an HTTP GET object
            getRequest.setURI(website); // set the URL of the GET request
            HttpResponse response = httpclient.execute(getRequest); // execute the request
            //Toast.makeText(getBaseContext(), "ok", Toast.LENGTH_LONG).show();
            //httpclient.getConnectionManager().shutdown();
//            InputStream content = null;
//            content = response.getEntity().getContent();
//            BufferedReader in = new BufferedReader(new InputStreamReader(
//                    content
//            ));
//            serverResponse = in.readLine();
//            Toast.makeText(getBaseContext(), serverResponse, Toast.LENGTH_LONG).show();
//            // Close the connection
//            content.close();

        } catch (ClientProtocolException e) {
            // HTTP error
            serverResponse = e.getMessage();
            e.printStackTrace();
        } catch (IOException e) {
            // IO error
            serverResponse = e.getMessage();
            e.printStackTrace();
        } catch (URISyntaxException e) {
            // URL syntax error
            serverResponse = e.getMessage();
            e.printStackTrace();
        }
        // return the server's reply/response text
        return "";
    }


    /**
     * An AsyncTask is needed to execute HTTP requests in the background so that they do not
     * block the user interface.
     */
    private class HttpRequestAsyncTask extends AsyncTask<Void, Void, Void> {

        private String baseIpAdress, portNumber; //,requestReply;
        private String parameter;

        public HttpRequestAsyncTask(String baseIpAdress, String portNumber, String parameter)
        {

            this.baseIpAdress = baseIpAdress;
            this.portNumber = portNumber;
            this.parameter = parameter;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            sendRequest(baseIpAdress,portNumber, parameter);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

        }

        @Override
        protected void onPreExecute() {

        }
    }

    class ServerAsyncTask extends AsyncTask<Socket, Void, String> {
        //Background task which serve for the client
        @Override
        protected String doInBackground(Socket... params) {
            String result = null;
            //Get the accepted socket object
            Socket mySocket = params[0];
            try {
                //Get the data input stream comming from the client
                InputStream is = mySocket.getInputStream();
                //Get the output stream to the client
                PrintWriter out = new PrintWriter(
                        mySocket.getOutputStream(), true);
                //Write data to the data output stream
                out.println("Received");
                //Buffer the data input stream
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(is));
                //Read the contents of the data buffer
                result = br.readLine();
                //Close the client connection
                mySocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }


    }

    class ClientAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String result = null;
            try {
                //Create a client socket and define internet address and the port of the server
                Socket socket = new Socket(params[0],
                        Integer.parseInt(params[1]));
                //Get the input stream of the client socket
                InputStream is = socket.getInputStream();
                //Get the output stream of the client socket
                PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
                //Write data to the output stream of the client socket
                out.println(params[2]);
                //Buffer the data coming from the input stream
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(is));
                //Read data in the input buffer
                result = br.readLine();
                //Close the client socket
                socket.close();
            } catch (NumberFormatException e) {
                e.printStackTrace();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }
        @Override
        protected void onPostExecute(String s) {
            //Write server message to the text view
            //Toast.makeText(getBaseContext(), s, Toast.LENGTH_LONG).show();
        }
    }


}
