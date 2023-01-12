package user;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class userReceive implements Runnable {
    private Socket s;

    public userReceive(Socket s) {
        this.s = s;
    }

    @Override
    public void run() {
        try {
            BufferedReader brIn = new BufferedReader(new InputStreamReader(s.getInputStream()));
            while (true) {
                String s = brIn.readLine();
                String[] strs = s.split("\\.");
                String info = strs[0];     
                String name = "", line = "";
                if (strs.length == 2)
                    line = strs[1];
                else if (strs.length == 3) {
                    line = strs[1];
                    name = strs[2];
                }

                if (info.equals("1")) {  // 1 for msg
                    UserScreen.textMessage.append(line + "\r\n");
                    UserScreen.textMessage.setCaretPosition(UserScreen.textMessage.getText().length());
                } else if (info.equals("2") || info.equals("3")) { // 2 for enter, 3 for exit
                    if (info.equals("2")) {
                       UserScreen.textMessage.append("(Alert) " + name + " entered!" + "\r\n");
                       UserScreen.textMessage.setCaretPosition(UserScreen.textMessage.getText().length());
                    } else {
                        UserScreen.textMessage.append("(Alert) " + name + " left!" + "\r\n");
                        UserScreen.textMessage.setCaretPosition(UserScreen.textMessage.getText().length());
                    }
                    String list = line.substring(1, line.length() - 1);
                    String[] data = list.split(",");
                    UserScreen.user.clearSelection();
                    UserScreen.user.setListData(data);
                } else if (info.equals("4")) {  // 4 for alerts
                    UserScreen.connect.setText("login");
                    UserScreen.exit.setText("exit");
                   UserScreen.socket.close();
                    UserScreen.socket = null;
                    JOptionPane.showMessageDialog(UserScreen.window, "Someone is already using this username");
                    break;
                } else if (info.equals("5")) {   // 5 for close the server
                    UserScreen.connect.setText("login");
                    UserScreen.exit.setText("exit");
                    UserScreen.socket.close();
                    UserScreen.socket = null;
                    break;
                } else if (info.equals("6")) {  // 6 for private msg
                    UserScreen.textMessage.append("(Private Message) " + line + "\r\n");
                    UserScreen.textMessage.setCaretPosition(UserScreen.textMessage.getText().length());
                } else if (info.equals("7")) {
                    JOptionPane.showMessageDialog(UserScreen.window, "This user is not online");
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(UserScreen.window, "The user has left");
        }
    }
}