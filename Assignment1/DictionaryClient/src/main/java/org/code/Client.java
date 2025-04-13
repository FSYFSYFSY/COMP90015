package org.code;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.*;

//Hanjie Liu: 1667156

public class Client {
    private JFrame frame;
    private JList<String> actionList;
    private JTextField keyField;
    private JTextField valueField;
    private JTextField newValueField;
    private JButton submitButton;
    private JTextArea responseArea;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private DefaultListModel<String> actionModel;
    public static String address = "localhost";
    public static int port = 8888;

    // Available actions
    private static final String[] actions = {"add", "add_exist", "remove", "query", "update"};

    public static void main(String[] args) {
        if(args.length == 2) {
            port = Integer.parseInt(args[1]);
            address = args[0];
        }
        else if (args.length == 1) {
            try {
                port = Integer.parseInt(args[1]);
            }catch(NumberFormatException e){
                System.out.println("The command should be : java –jar DictionaryClient.jar <server-address> <server-port>");
                System.exit(1);
            }
        }
        else{
            System.out.println("The command should be : java –jar DictionaryClient.jar <server-address> <server-port>");
            System.exit(1);
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    new Client(address, port).initialize();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public Client(String serverAddress, int serverPort) throws IOException {
        this.socket = new Socket(serverAddress, serverPort);
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void initialize() {
        frame = new JFrame("Dictionary Client");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setSize(400, 500);
        frame.setLayout(new BorderLayout());

        actionModel = new DefaultListModel<>();
        for (String action : actions) {
            actionModel.addElement(action);
        }

        actionList = new JList<>(actionModel);
        actionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        actionList.addListSelectionListener(e -> updateInputFields());

        keyField = new JTextField(20);
        valueField = new JTextField(20);
        newValueField = new JTextField(20);
        submitButton = new JButton("Submit");
        submitButton.setEnabled(false);  // Initially disabled

        responseArea = new JTextArea(10, 30);
        responseArea.setEditable(false);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(5, 2));
        inputPanel.add(new JLabel("Key:"));
        inputPanel.add(keyField);
        inputPanel.add(new JLabel("Value:"));
        inputPanel.add(valueField);
        inputPanel.add(new JLabel("New Value:"));
        inputPanel.add(newValueField);

        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(new BorderLayout());
        actionPanel.add(new JScrollPane(actionList), BorderLayout.CENTER);
        actionPanel.add(submitButton, BorderLayout.SOUTH);

        frame.add(inputPanel, BorderLayout.CENTER);
        frame.add(actionPanel, BorderLayout.WEST);
        frame.add(new JScrollPane(responseArea), BorderLayout.SOUTH);

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                try {
                    if (out != null) out.close();
                    if (in != null) in.close();
                    if (socket != null && !socket.isClosed()) {
                        socket.close();
                        System.out.println("Disconnected from server.");
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                } finally {
                    frame.dispose();
                    System.exit(0);
                }
            }
        });

        submitButton.addActionListener(e -> sendRequest());
        frame.setVisible(true);

    }

    private void updateInputFields() {
        String selectedAction = actionList.getSelectedValue();
        keyField.setText("");
        valueField.setText("");
        newValueField.setText("");

        keyField.setEnabled(false);
        valueField.setEnabled(false);
        newValueField.setEnabled(false);
        submitButton.setEnabled(false);

        switch (selectedAction) {
            case "add":
            case "add_exist":
                keyField.setEnabled(true);
                valueField.setEnabled(true);
                submitButton.setEnabled(true);
                break;
            case "remove":
            case "query":
                keyField.setEnabled(true);
                submitButton.setEnabled(true);
                break;
            case "update":
                keyField.setEnabled(true);
                valueField.setEnabled(true);
                newValueField.setEnabled(true);
                submitButton.setEnabled(true);
                break;
        }
    }

    private void sendRequest() {
        String selectedAction = actionList.getSelectedValue();
        String key = keyField.getText().toLowerCase();
        String value = valueField.getText().toLowerCase();
        String newValue = newValueField.getText().toLowerCase();

        submitButton.setEnabled(false);

        String request = "none";
        switch (selectedAction) {
            case "add":
            case "add_exist":
                if(key != "" && value != "") {
                    request = selectedAction + ":" + key + ":" + value;
                }
                break;
            case "remove":
            case "query":
                if(key != "") {
                    request = selectedAction + ":" + key;
                }
                break;
            case "update":
                if(key != "" && value != "" && newValue != "") {
                    request = selectedAction + ":" + key + ":" + value + ":" + newValue;
                }
                break;
        }
        out.println(request);
        request = "none";
        try {
            String response = in.readLine();
            responseArea.setText(response);
            submitButton.setEnabled(true);
        } catch (IOException e) {
            responseArea.setText("Cannot communicat with the server.");
            submitButton.setEnabled(true);
            e.printStackTrace();
        }
    }
}
