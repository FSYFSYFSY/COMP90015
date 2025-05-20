package org.code;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;


public class Client {
    private ClientCallback callback;
    private WhiteBoardService service;
    private DrawingPanel canvas;
    private String selectedShape;
    private int currentColor;
    static String hostName = "localhost";
    static String serviceName ="whiteboard";
    static int port = 1099;
    private static String clientname = "client";
    public static List<String> clients = new ArrayList<>();
    public static DefaultListModel<String> clientListModel = new DefaultListModel<>();;
    private JTextArea chatArea;
    private JTextField inputField;
    private FileMenuBar fileMenu;

    private final String[] ShapeOptions = {"Line2","Line4","Line8", "Rectangle", "Oval", "Triangle", "FreeDraw2","FreeDraw4","FreeDraw8","Erase2","Erase4","Erase8","Text"};

    private static final Color[] COLOR_PALETTE = {
            Color.BLACK, Color.WHITE, Color.RED, Color.GREEN,
            Color.BLUE, Color.YELLOW, Color.ORANGE, Color.PINK,
            Color.CYAN, Color.MAGENTA, Color.GRAY, Color.LIGHT_GRAY,
            new Color(128, 0, 0),
            new Color(0, 128, 0),
            new Color(0, 0, 128),
            new Color(128, 128, 0)
    };

    public static void main(String[] args) {

        if(args.length == 3){
            hostName = args[0];
            port = Integer.parseInt(args[1]);
            clientname = args[2];
        }

        SwingUtilities.invokeLater(() -> {
            try {
                new Client().start();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "start fail: " + e.getMessage());
            }
        });
    }

    public void start() throws Exception {
        service = (WhiteBoardService) Naming.lookup("rmi://" + hostName + ":" + port + "/" + serviceName);

        if (!service.isClientNameAvailable(clientname)) {
            JOptionPane.showMessageDialog(null, "Name: '" + clientname + "' has already been created！");
            System.exit(0);
        }

        JFrame frame = new JFrame("Whiteboard Client");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    service.unregisterClient(callback);
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    frame.dispose();
                    System.exit(0);
                }
            }
        });
        frame.setSize(1200, 800);
        frame.setLayout(new BorderLayout());

        canvas = new DrawingPanel(service);
        frame.add(canvas, BorderLayout.CENTER);

        FileMenuBar fileMenu = new FileMenuBar(service, clientname);
        frame.setJMenuBar(fileMenu);

        callback = new ClientCallbackImpl(this);
        service.registerClient(callback);

        List<String> existingClients = service.getClientList();
        clientListModel.clear();
        for (String c : existingClients) {
            clientListModel.addElement(c);
        }

        frame.setTitle("WhiteBoard - Client: " + clientname);

        JList<String> clientListBox = new JList<>(clientListModel);
        JScrollPane scrollPane = new JScrollPane(clientListBox);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Connected Clients"));
        scrollPane.setPreferredSize(new Dimension(150, 200));

        JButton kickButton = new JButton("Kick");
        kickButton.setEnabled("manager".equals(clientname)); // 只有 manager 可以点击

        kickButton.addActionListener(e -> {
            String selectedClient = clientListBox.getSelectedValue();
            if (selectedClient == null) {
                JOptionPane.showMessageDialog(frame, "Please Pick a client", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if ("manager".equals(selectedClient)) {
                JOptionPane.showMessageDialog(frame, "You cannot kick the manager", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(frame,
                    "Are you sure to kick " + selectedClient,
                    "Kick confirm",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    service.kickClient(selectedClient);
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "kick failed：" + ex.getMessage());
                }
            }
        });
        JPanel clientPanel = new JPanel(new BorderLayout());
        clientPanel.add(scrollPane, BorderLayout.CENTER);
        clientPanel.add(kickButton, BorderLayout.SOUTH);
        frame.add(clientPanel, BorderLayout.WEST);

        JPanel shapePanel = new JPanel();
        for (String s : ShapeOptions) {
            JButton btn = new JButton(s);
            btn.addActionListener(e -> canvas.SetSelectedShape(s));
            shapePanel.add(btn);
        }
        frame.add(shapePanel, BorderLayout.SOUTH);

        JPanel colorPanel = new JPanel();
        for (Color c : COLOR_PALETTE) {
            JButton btn = new JButton();
            btn.setBackground(c);
            btn.setPreferredSize(new Dimension(30, 30));
            btn.addActionListener(e -> canvas.setCurrentColor(c.getRGB()));
            colorPanel.add(btn);
        }
        frame.add(colorPanel, BorderLayout.NORTH);

        //Chat
        JPanel chatPanel = new JPanel(new BorderLayout());
        chatPanel.setPreferredSize(new Dimension(300, 800));

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        JScrollPane chatScroll = new JScrollPane(chatArea);
        chatPanel.add(chatScroll, BorderLayout.CENTER);

        inputField = new JTextField();
        inputField.addActionListener(e -> {
            String message = inputField.getText().trim();
            if (!message.isEmpty()) {
                try {
                    service.broadcastMessage(clientname, message);
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }
                inputField.setText("");
            }
            else{
                message = "";
            }
        });
        chatPanel.add(inputField, BorderLayout.SOUTH);

        frame.add(chatPanel, BorderLayout.EAST);

        frame.setVisible(true);
    }


    public DrawingPanel getCanvas() {
        return this.canvas;
    }

    public String getClientname() {
        return this.clientname;
    }

    public void setclients(List<String> newClients) {
        clients = newClients;
        clientListModel.clear();
        for (String name : newClients) {
            clientListModel.addElement(name);
        }
    }
    public void showMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            chatArea.append(message + "\n");
        });
    }
}

