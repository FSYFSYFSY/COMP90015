package org.code;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;


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

    // Available actions
    private static final String[] actions = {"add", "increase", "remove", "query", "update"};

    public static void main(String[] args) {
        // Check if we received server address and port
        /*
        if (args.length != 2) {
            System.out.println("The command should be: java -jar DictionaryClient.jar <server-address> <server-port>");
            System.exit(1);
        }
        */

        String serverAddress = "localhost";
        int serverPort = 8888;
        //String serverAddress = args[0];  // Server address
        //int serverPort = Integer.parseInt(args[1]);  // Server port

        SwingUtilities.invokeLater(() -> {
            try {
                new Client(serverAddress, serverPort).initialize();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    // Constructor to initialize server address and port
    public Client(String serverAddress, int serverPort) throws IOException {
        // Initialize socket connection to the server
        this.socket = new Socket(serverAddress, serverPort);
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    // Initialize the GUI components
    public void initialize() {
        frame = new JFrame("Dictionary Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 500);
        frame.setLayout(new BorderLayout());

        // Create the action list model and list
        actionModel = new DefaultListModel<>();
        for (String action : actions) {
            actionModel.addElement(action);
        }

        actionList = new JList<>(actionModel);
        actionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        actionList.addListSelectionListener(e -> updateInputFields());

        // Create the input fields and buttons
        keyField = new JTextField(20);
        valueField = new JTextField(20);
        newValueField = new JTextField(20);
        submitButton = new JButton("Submit");
        submitButton.setEnabled(false);  // Initially disabled

        // Create text area to display server response
        responseArea = new JTextArea(10, 30);
        responseArea.setEditable(false);

        // Panel for input fields
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(5, 2));
        inputPanel.add(new JLabel("Key:"));
        inputPanel.add(keyField);
        inputPanel.add(new JLabel("Value:"));
        inputPanel.add(valueField);
        inputPanel.add(new JLabel("New Value:"));
        inputPanel.add(newValueField);

        // Panel for actions and buttons
        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(new BorderLayout());
        actionPanel.add(new JScrollPane(actionList), BorderLayout.CENTER);
        actionPanel.add(submitButton, BorderLayout.SOUTH);

        // Add components to the frame
        frame.add(inputPanel, BorderLayout.CENTER);
        frame.add(actionPanel, BorderLayout.WEST);
        frame.add(new JScrollPane(responseArea), BorderLayout.SOUTH);

        // Submit button action
        submitButton.addActionListener(e -> sendRequest());

        // Show the frame
        frame.setVisible(true);
    }

    // Update input fields based on selected action
    private void updateInputFields() {
        String selectedAction = actionList.getSelectedValue();
        keyField.setText("");
        valueField.setText("");
        newValueField.setText("");

        // Disable all input fields initially
        keyField.setEnabled(false);
        valueField.setEnabled(false);
        newValueField.setEnabled(false);
        submitButton.setEnabled(false);

        switch (selectedAction) {
            case "add":
            case "increase":
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

    // Send the request to the server
    private void sendRequest() {
        String selectedAction = actionList.getSelectedValue();
        String key = keyField.getText();
        String value = valueField.getText();
        String newValue = newValueField.getText();

        // Disable the submit button until the response is received
        submitButton.setEnabled(false);

        // Prepare request based on selected action
        String request = "";
        switch (selectedAction) {
            case "add":
            case "increase":
                request = selectedAction + ":" + key + ":" + value;
                break;
            case "remove":
            case "query":
                request = selectedAction + ":" + key;
                break;
            case "update":
                request = selectedAction + ":" + key + ":" + value + ":" + newValue;
                break;
        }

        // Send the request to the server
        out.println(request);

        // Wait for server's response and update the UI
        try {
            String response = in.readLine();
            responseArea.setText(response);
            submitButton.setEnabled(true);  // Re-enable the submit button after response
        } catch (IOException e) {
            responseArea.setText("Error communicating with server.");
            submitButton.setEnabled(true);  // Re-enable the submit button if there's an error
            e.printStackTrace();
        }
    }
}
