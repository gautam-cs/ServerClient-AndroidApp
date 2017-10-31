package in.ac.iiitv.CS;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

import android.app.ActivityManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.text.DecimalFormat;

import static java.lang.Math.pow;

public class ServerActivity extends AppCompatActivity {

    TextView info, infoip, msg;
    String message = "";
    String client_message = "";
    ServerSocket serverSocket;
    ArrayList<Socket> clientSockets;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);
        info = (TextView) findViewById(R.id.info);
        infoip = (TextView) findViewById(R.id.infoip);
        msg = (TextView) findViewById(R.id.msg);
        clientSockets = new ArrayList<>();
        infoip.setText(getIpAddress());

        Thread socketServerThread = new Thread(new SocketServerThread());
        socketServerThread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private class SocketServerThread extends Thread {

        static final int SocketServerPORT = 8080;
        int count = 0;

        @Override
        public void run() {
            Socket socket = null;
//            DataInputStream dataInputStream = null;
//            DataOutputStream dataOutputStream = null;

            try {
                serverSocket = new ServerSocket(SocketServerPORT);
                ServerActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        info.setText("I'm waiting here: "
                                + serverSocket.getLocalPort());
                    }
                });
                int i = 0;
                while (true) {
                    socket = serverSocket.accept();
                    clientSockets.add(socket);
                    IndividualClientThread individualClientThread = new IndividualClientThread(socket,i);
                    individualClientThread.start();
                    i++;
                }
            } catch (IOException e) {

            }
        }

    }

    public class IndividualClientThread extends Thread {
        Socket mySocket;
        int number;

        IndividualClientThread(Socket socket,int number) {
            mySocket = socket;
            this.number = number;
        }

        @Override
        public void run() {
            int count = 0;
            DataInputStream dataInputStream = null ;
            DataOutputStream dataOutputStream = null;
            try {
                dataInputStream = new DataInputStream(
                        mySocket.getInputStream());
                dataOutputStream = new DataOutputStream(
                        mySocket.getOutputStream());
                String messageFromClient = "";

                //If no message sent from client, this code will block the program
                messageFromClient = dataInputStream.readUTF();
                Log.e("dfsasdf",messageFromClient);
                while (!messageFromClient.equals("QUIT")){
                    Log.e("dfsasdf","asdf");
                    if (!messageFromClient.equals("")){
                        count++;
                        String s1 = "health#007$";
                        if (messageFromClient.equals(s1)) {
                            Thread socketServerThread = new Thread(new SocketServerThread());
                            socketServerThread.start();
                            Log.e("asf", readUsage() + "");
                            ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
                            assert activityManager != null;
                            activityManager.getMemoryInfo(mi);
                            Log.e("memory free", "" + mi.availMem);
                            Log.e("total memory", "" + mi.totalMem);
                            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                            Intent batteryStatus = getApplicationContext().registerReceiver(null, ifilter);
                            assert batteryStatus != null;
                            int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                            int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                            DecimalFormat df = new DecimalFormat("0.00");
                            float batteryPct = level / (float) scale;
                            Log.e("battery percentge", "" + batteryPct);
                            client_message = "SERVER HEALTH:" + "\n" + "CPU USAGE: " + df.format(readUsage() * 100) + "%" + "\n" + "FREE MEMORY: "
                                    + df.format(mi.availMem / pow(2, 30)) + " GB" + "\n" + "TOTAL MEMORY: " + df.format(mi.totalMem / pow(2, 30)) +
                                    " GB" + "\n" + "BATTEY PERCENTAGE: " + batteryPct * 100 + "%" + "\n";
                            message += "Client " + "#" + count + " requested for health status\n";
                        } else {
                            message += "#" + count + " from " + mySocket.getInetAddress()
                                    + ":" + mySocket.getPort() + "\n"
                                    + "Msg from client: " + messageFromClient + "\n";
                        }
                        ServerActivity.this.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                msg.setText(message);
                            }
                        });
                        String msgReply = client_message;
                        dataOutputStream.writeUTF(msgReply);
                    }
                    messageFromClient = dataInputStream.readUTF();
                }
                clientSockets.remove(mySocket);
                dataOutputStream.writeUTF("Diconnected from Server");
                for (Socket clientSocket:clientSockets
                     ) {
                    DataOutputStream dout = new DataOutputStream(clientSocket.getOutputStream());
                    dout.writeUTF(this.number+"th Client Disconnected");
                }
            }catch (IOException e){
                e.printStackTrace();
            } finally {if (mySocket != null) {
                try {
                    mySocket.close();
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

            if (dataOutputStream != null) {
                try {
                    dataOutputStream.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        }
    }

    private String getIpAddress() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            if (enumNetworkInterfaces == null)
                Log.e("f", "fadf");
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces
                        .nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface
                        .getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();

                    if (inetAddress.isSiteLocalAddress()) {
                        ip += "SiteLocalAddress: "
                                + inetAddress.getHostAddress() + "\n";
                    }

                }

            }

        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ip += "Something Wrong! " + e.toString() + "\n";
        }

        return ip;
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
            } catch (Exception e) {
            }

            reader.seek(0);
            load = reader.readLine();
            reader.close();

            toks = load.split(" +");

            long idle2 = Long.parseLong(toks[4]);
            long cpu2 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[5])
                    + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

            return (float) (cpu2 - cpu1) / ((cpu2 + idle2) - (cpu1 + idle1));

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return 0;
    }

}

