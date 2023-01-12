package Server;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class ServerScreen {
    public static JFrame window;
    public static int ports;
    public static JTextArea console;
    public static JList<String> user;

    JButton start, exit, send;
    JTextField serverName, serverPort, message;

    //main
    public static void main(String[] args) {
        new ServerScreen();
    }

    public ServerScreen() {
        init();
    }

    public void init() {   // server layout
        window = new JFrame("Server");
        window.setLayout(null);
        window.setBounds(200, 200, 500, 350);
        window.setResizable(false);

        JLabel labelnameServer = new JLabel("Server:");
        labelnameServer.setBounds(10, 8, 80, 30);
        window.add(labelnameServer);

        serverName = new JTextField();
        serverName.setBounds(80, 8, 60, 30);
        window.add(serverName);

        JLabel label_port = new JLabel("Port:");
        label_port.setBounds(150, 8, 60, 30);
        window.add(label_port);

        serverPort = new JTextField();
        serverPort.setBounds(200, 8, 70, 30);
        window.add(serverPort);

        start = new JButton("Start");
        start.setBounds(280, 8, 90, 30);
        window.add(start);

        exit = new JButton("Exit");
        exit.setBounds(380, 8, 110, 30);
        window.add(exit);

        console = new JTextArea();
        console.setBounds(10, 70, 340, 320);
        console.setEditable(false);  // cannot be edited

        console.setLineWrap(true);  // automatic content line feed
        console.setWrapStyleWord(true);

        JLabel label_text = new JLabel("Server Panel");
        label_text.setBounds(100, 47, 190, 30);
        window.add(label_text);

        JScrollPane paneText = new JScrollPane(console);
        paneText.setBounds(10, 70, 340, 220);
        window.add(paneText);

        JLabel label_userList = new JLabel("User List");
        label_userList.setBounds(357, 47, 180, 30);
        window.add(label_userList);

        user = new JList<String>();
        JScrollPane paneUser = new JScrollPane(user);
        paneUser.setBounds(355, 70, 130, 220);
        window.add(paneUser);

        message = new JTextField();
        message.setBounds(0, 0, 0, 0);
        window.add(message);

        send = new JButton("Send");
        send.setBounds(0, 0, 0, 0);
        window.add(send);

        myEvent();  // add listeners
        window.setVisible(true);
    }

    public void myEvent() {
        window.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (Server.userList != null && Server.userList.size() != 0) {
                    try {
                        new ServerSend(Server.userList, "", "5", "");
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                System.exit(0); // exit from the window
            }
        });

        exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (Server.ss == null || Server.ss.isClosed()) {
                    JOptionPane.showMessageDialog(window, "Server has been closed!");
                } else {
                    if (Server.userList != null && Server.userList.size() != 0) {
                        try {
                            new ServerSend(Server.userList, "", "5", "");
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                    try {
                        start.setText("Start");
                        exit.setText("Close");
                        Server.ss.close();
                        Server.ss = null;
                        Server.userList = null;
                        Server.flag = false;   // important
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });

        start.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (Server.ss != null && !Server.ss.isClosed()) {
                    JOptionPane.showMessageDialog(window, "The server has started!");
                } else {
                    ports = getPort();
                    if (ports != 0) {
                        try {
                            Server.flag = true;
                            new Thread(new Server(ports)).start(); // start thread server

                            start.setText("Start");
                            exit.setText("Close");
                        } catch (IOException e1) {
                            JOptionPane.showMessageDialog(window, "Failed to run");
                        }
                    }
                }
            }
        });

        message.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMsg();
                }
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
            JOptionPane.showMessageDialog(window, "There's nothing to send!");
        } else if (Server.userList == null || Server.userList.size() == 0) {
            JOptionPane.showMessageDialog(window, "There is no connection!");
        } else {
            try {
                new ServerSend(Server.userList, getServerName() + ": " + messages, "1", "");
                message.setText(null);
            } catch (IOException e1) {
                JOptionPane.showMessageDialog(window, "Failed to send!");
            }
        }
    }

    public int getPort() {
        String port = serverPort.getText();
        String name = serverName.getText();
        if ("".equals(port) || "".equals(name)) {
            JOptionPane.showMessageDialog(window, "No port or username found!");
            return 0;
        } else {
            return Integer.parseInt(port);
        }
    }

    public String getServerName() {
        return serverName.getText();
    }
}