package in.ac.iiitv.CS;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.net.UnknownHostException;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ClientActivity extends AppCompatActivity {

    TextView textResponse;
    EditText editTextAddress, editTextPort;
    Button buttonConnect, buttonClear;
    Button buttonSend;
    Button mHealth;
    EditText welcomeMsg;
    MyClientTask myClientTask;
    Button mMatrixMultiplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        mMatrixMultiplication = (Button) findViewById(R.id.matrix_multiplication);
        editTextAddress = (EditText) findViewById(R.id.address);
        editTextPort = (EditText) findViewById(R.id.port);
        buttonConnect = (Button) findViewById(R.id.connect);
        buttonClear = (Button) findViewById(R.id.clear);
        textResponse = (TextView) findViewById(R.id.response);
        buttonSend = (Button) findViewById(R.id.send);
        welcomeMsg = (EditText)findViewById(R.id.welcomemsg);
        mHealth = (Button) findViewById(R.id.health);
        buttonConnect.setOnClickListener(buttonConnectOnClickListener);
        buttonSend.setOnClickListener(sendOnClickListener);
        mMatrixMultiplication.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final String finalTMsg = "matrix," + welcomeMsg.getText().toString();
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            myClientTask.dataOutputStream.writeUTF(finalTMsg);
//                            final String msg = myClientTask.dataInputStream.readUTF();
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
////                                    textResponse.setText(msg);
//                                }
//                            });

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
            }
        });
        mHealth.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final String finalTMsg = "health#007$";
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            myClientTask.dataOutputStream.writeUTF(finalTMsg);
//                            final String msg = myClientTask.dataInputStream.readUTF();
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
////                                    textResponse.setText(msg);
//                                }
//                            });

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
            }
        });
        buttonClear.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                textResponse.setText("");
            }
        });
        Log.e("asf",readUsage()+"");
    }

    OnClickListener sendOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            String tMsg = welcomeMsg.getText().toString();
            if(tMsg.equals("")){
                tMsg = "welcome";
                Toast.makeText(ClientActivity.this, "No Welcome Msg sent", Toast.LENGTH_SHORT).show();
            }

            final String finalTMsg = tMsg;
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        myClientTask.dataOutputStream.writeUTF(finalTMsg);
                        if (finalTMsg.equals("QUIT")){runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.e("dsfasd","dfasdfasdf");
                                buttonConnect.setVisibility(View.VISIBLE);
                                mHealth.setVisibility(View.GONE);
                                buttonSend.setVisibility(View.GONE);
                                myClientTask.valid = false;
                            }
                        });
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();


        }
    };

    OnClickListener buttonConnectOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View arg0) {
            textResponse.setText("");
            String tMsg = welcomeMsg.getText().toString();
            if(tMsg.equals("")){
                tMsg = "welcome";
                Toast.makeText(ClientActivity.this, "No Welcome Msg sent", Toast.LENGTH_SHORT).show();
            }
            myClientTask = new MyClientTask(editTextAddress
                    .getText().toString(), Integer.parseInt(editTextPort
                    .getText().toString()),
                    tMsg);
            myClientTask.execute();
        }
    };

    public class MyClientTask extends AsyncTask<Void, Void, Void> {

        String dstAddress;
        int dstPort;
        String response = "";
        String msgToServer;
        DataOutputStream dataOutputStream;
        DataInputStream dataInputStream;
        Socket socket;
        boolean valid;

        MyClientTask(String addr, int port, String msgTo) {
            dstAddress = addr;
            dstPort = port;
            msgToServer = msgTo;
            socket = null;
            valid = true;

        }
            @Override
        protected Void doInBackground(Void... arg0) {
                try {
                    socket = new Socket(dstAddress, dstPort);
                    dataOutputStream = new DataOutputStream(
                            socket.getOutputStream());
                    dataInputStream = new DataInputStream(socket.getInputStream());

                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

//            Socket socket = null;
//            DataOutputStream dataOutputStream = null;
//            DataInputStream dataInputStream = null;

            try {
//                socket = new Socket(dstAddress, dstPort);
//                dataOutputStream = new DataOutputStream(
//                        socket.getOutputStream());
//                dataInputStream = new DataInputStream(socket.getInputStream());

                if(msgToServer != null){
                    dataOutputStream.writeUTF(msgToServer);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("he","asdf");
                        buttonConnect.setVisibility(View.GONE);
//            }
                        buttonSend.setVisibility(View.VISIBLE);
                        mHealth.setVisibility(View.VISIBLE);
                        textResponse.setText(response);
                    }
                });
                while (valid){
                    response = dataInputStream.readUTF();
                    if (!response.isEmpty()){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                textResponse.setText(response);
                            }
                        });
                    }
                }

            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "IOException: " + e.toString();
            } /*finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                if (dataOutputStream != null) {
                    try {
                        dataOutputStream.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                if (dataInputStream != null) {
                    try {
                        dataInputStream.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }*/
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
//            if (buttonConnect.getText().toString().equals("Connect...")){
//                buttonConnect.setVisibility(View.GONE);
////            }
//            buttonSend.setVisibility(View.VISIBLE);
//            mHealth.setVisibility(View.VISIBLE);
//            textResponse.setText(response);
            super.onPostExecute(result);
        }


    }
    private float readUsage() {
        try {
            RandomAccessFile reader = new RandomAccessFile("/proc/stat", "r");
            String load = reader.readLine();

            String[] toks = load.split(" +");  // Split on one or more spaces

            long idle1 = Long.parseLong(toks[4]);
            long cpu1 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[5])
                    + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

            try {
                Thread.sleep(360);
            } catch (Exception e) {}

            reader.seek(0);
            load = reader.readLine();
            reader.close();

            toks = load.split(" +");

            long idle2 = Long.parseLong(toks[4]);
            long cpu2 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[5])
                    + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

            return (float)(cpu2 - cpu1) / ((cpu2 + idle2) - (cpu1 + idle1));

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return 0;
    }

}