package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;

public class ServerReceive implements Runnable {
    private Socket socket;
    private ArrayList<Socket> userList;
    private Vector<String> userName;
    private Map<String, Socket> map;


    public ServerReceive(Socket s, ArrayList<Socket> userList, Vector<String> userName, Map<String, Socket> map) {
        this.socket = s;
        this.userList = userList;
        this.userName = userName;
        this.map = map;
    }

    public void run() {
        try {
            BufferedReader brIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (true) {
                String s = brIn.readLine();
                String[] strs = s.split(",,");
                String info = strs[0];  //judge the kind of info
                String line = strs[1];
                //System.out.println(line);
                String name = "";
                if (strs.length == 3)
                    name = strs[2];

                if (info.equals("1")) {   // 1 for New message request
                    ServerScreen.console.append("New message ---- " + line + "\r\n");
                    ServerScreen.console.setCaretPosition(ServerScreen.console.getText().length());
                    new ServerSend(userList, line, "1", "");
                } else if (info.equals("2")) {  // 2 for login

                    if (!userName.contains(line)) {
                        ServerScreen.console.append("New login requested ---- " + line + "\r\n");
                        ServerScreen.console.setCaretPosition(ServerScreen.console.getText().length());
                        userName.add(line);
                        map.put(line, socket);
                        ServerScreen.user.setListData(userName);
                        new ServerSend(userList, userName, "2", line);
                    } else {
                        ServerScreen.console.append("Duplicate Login ---- " + line + "\r\n");
                        ServerScreen.console.setCaretPosition(ServerScreen.console.getText().length());
                        userName.remove(socket);
                        new ServerSend(socket, "", "4");
                    }
                } else if (info.equals("3")) {  // 3 for exit
                    ServerScreen.console.append("Exit ---->> " + line + "\r\n");
                    ServerScreen.console.setCaretPosition(ServerScreen.console.getText().length());
                    userName.remove(line);
                    userList.remove(socket);
                    map.remove(line);
                    ServerScreen.user.setListData(userName);
                    new ServerSend(userList, userName, "3", line);
                    socket.close();
                    break;  // info break
                } else if (info.equals("4")) {   // 4 for private msg
                   ServerScreen.console.append("New private message ---- " + line + "\r\n");
                   ServerScreen.console.setCaretPosition(ServerScreen.console.getText().length());
                    if (map.containsKey(name))
                        new ServerSend(map.get(name), line, "6");
                    else
                        new ServerSend(socket, "", "7");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}