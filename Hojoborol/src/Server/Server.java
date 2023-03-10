package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.util.Map;

import javax.swing.JOptionPane;

public class Server implements Runnable {
    private int port;
    public static ArrayList<Socket> userList = null;
    public static Vector<String> userName = null;    // thread security
    public static Map<String, Socket> map = null;
    public static ServerSocket ss = null;
    public static boolean flag = true;

    public Server(int port) throws IOException {
        this.port = port;
    }

    public void run() {
        Socket s = null;
        userList = new ArrayList<Socket>();   //Contains user ports
        userName = new Vector<String>();      //contain users
        map = new HashMap<String, Socket>();   //name to socket one on one map

        System.out.println("Server Started!");

        try {
            ss = new ServerSocket(port);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        while (flag) {
            try {
                s = ss.accept();
                userList.add(s);
                new Thread(new ServerReceive(s, userList, userName, map)).start();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(ServerScreen.window, "Server closed！");
            }
        }
    }
}