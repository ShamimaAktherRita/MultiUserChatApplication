package user;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.Socket;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class UserScreen {
    public static JFrame window;
    public static JButton connect, exit;
    public static JTextArea textMessage;
    public static Socket socket = null;
    public static JList<String> user;

    JTextField userName, port, message, msgPriv;
    JButton privateMsg, send;

    //main
    public static void main(String[] args) {
        new UserScreen();
    }

    public UserScreen() {
        init();
    }

    public void init() {
        window = new JFrame("Chat");
        window.setLayout(null);
        window.setBounds(400, 400, 530, 420);
        window.setResizable(true);

        JLabel label_userName = new JLabel("User:");
        label_userName.setBounds(10, 28, 70, 30);
        window.add(label_userName);

        userName = new JTextField();
        userName.setBounds(80, 28, 70, 30);
        window.add(userName);

        JLabel label_porta = new JLabel("port:");
        label_porta.setBounds(180, 28, 50, 30);
        window.add(label_porta);

        port = new JTextField();
        port.setBounds(230, 28, 50, 30);
        window.add(port);

        connect = new JButton("Log in");
        connect.setBounds(300, 28, 90, 30);
        window.add(connect);

        exit = new JButton("Exit");
        exit.setBounds(400, 28, 90, 30);
        window.add(exit);

        textMessage = new JTextArea();
        textMessage.setBounds(10, 70, 340, 250);
        textMessage.setEditable(false);

        textMessage.setLineWrap(true);
        textMessage.setWrapStyleWord(true);

        JLabel label_text = new JLabel("Message Area");
        label_text.setBounds(100, 58, 200, 50);
        window.add(label_text);

        JScrollPane paneText = new JScrollPane(textMessage);
        paneText.setBounds(10, 90, 360, 240);
        window.add(paneText);

        JLabel label_userList = new JLabel("User Lists");
        label_userList.setBounds(380, 60, 200, 50);
        window.add(label_userList);

        user = new JList<String>();
        JScrollPane paneUser = new JScrollPane(user);
        paneUser.setBounds(375, 90, 140, 240);
        window.add(paneUser);

        JLabel label_Alert = new JLabel("Type Msg for the group");
        label_Alert.setBounds(10, 320, 180, 50);
        window.add(label_Alert);

        message = new JTextField();
        message.setBounds(10, 355, 188, 30);
        message.setText(null);
        window.add(message);

        JLabel label_Warning = new JLabel("Add user for private msg");
        label_Warning.setBounds(272, 320, 250, 50);
        window.add(label_Warning);

        msgPriv = new JTextField();
        msgPriv.setBounds(272, 355, 100, 30);
        window.add(msgPriv);

        privateMsg = new JButton("Private Msg");
        privateMsg.setBounds(376, 355, 140, 30);
        window.add(privateMsg);

        send = new JButton("Group");
        send.setBounds(190, 355, 77, 30);
        window.add(send);

        myEvent();  // add connected/listening to port
        window.setVisible(true);
    }

    public void myEvent() {
        window.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (socket != null && socket.isConnected()) {
                    try {
                        new userSend(socket, getuserName(), "3", "");
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                System.exit(0);
            }
        });

        exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (socket == null) {
                    JOptionPane.showMessageDialog(window, "The connection was closed!");
                } else if (socket != null && socket.isConnected()) {
                    try {
                        new userSend(socket, getuserName(), "3", "");
                        connect.setText("Login");
                        exit.setText("Exit!");
                        socket.close();
                        socket = null;
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });

        connect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (socket != null && socket.isConnected()) {
                    JOptionPane.showMessageDialog(window, "Connected!");
                } else {
                    String ipString = "127.0.0.1";
                    String portaClinet = port.getText();
                    String name = userName.getText();

                    if ("".equals(name) || "".equals(portaClinet)) {
                        JOptionPane.showMessageDialog(window, "User or port cannot be empty!");
                    } else {
                        try {
                            int portas = Integer.parseInt(portaClinet);
                            socket = new Socket(ipString, portas);
                            connect.setText("Enter");
                            exit.setText("Exit");
                            new userSend(socket, getuserName(), "2", "");
                            new Thread(new userReceive(socket)).start();
                        } catch (Exception e2) {
                            JOptionPane.showMessageDialog(window, "failed to connect, check ip and port");
                        }
                    }
                }
            }
        });

        privateMsg.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMsgmsgPriv();
            }
        });

        send.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMsg();
            }
        });
    }

    public void sendMsg() {
        String messages = message.getText();
        if ("".equals(messages)) {
            JOptionPane.showMessageDialog(window, "There's nothing to send");
        } else if (socket == null || !socket.isConnected()) {
            JOptionPane.showMessageDialog(window, "No connection");
        } else {
            try {
                new userSend(socket, getuserName() + ": " + messages, "1", "");
                message.setText(null);
            } catch (IOException e1) {
                JOptionPane.showMessageDialog(window, "failed to send!");
            }
        }
    }

    public void sendMsgmsgPriv() {
        String messages = message.getText();
        if ("".equals(messages)) {
            JOptionPane.showMessageDialog(window, "There's nothing to send!");
        } else if (socket == null || !socket.isConnected()) {
            JOptionPane.showMessageDialog(window, "No connection");
        } else {
            try {
                new userSend(socket, getuserName() + ": " + messages, "4", getmsgPrivate());
                UserScreen.textMessage.append(getuserName() + ": " + messages + "\r\n");
                message.setText(null);
            }catch(IOException e1) {
                JOptionPane.showMessageDialog(window, "Private message not sent!");
            }
        }
    }

    public String getuserName() {
        return userName.getText();
    }

    public String getmsgPrivate() {
        return msgPriv.getText();
    }
}