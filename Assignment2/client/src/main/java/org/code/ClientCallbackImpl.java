package org.code;

import javax.swing.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class ClientCallbackImpl extends UnicastRemoteObject implements ClientCallback {
    private final Client client;

    public ClientCallbackImpl(Client client) throws RemoteException {
        super();
        this.client = client;

    }

    @Override
    public void updateBoard(List<Shape> shapes) throws RemoteException {
        client.getCanvas().setShapes(shapes);
        client.getCanvas().repaint();
    }

    @Override
    public void updateClients (List<String> clients) throws RemoteException{
        client.setclients(clients);
    }

    @Override
    public String getclientname() throws RemoteException{
        return client.getClientname();
    }

    @Override
    public void receiveMessage(String sender, String message) throws RemoteException {
        client.showMessage(sender + ": " + message);
    }

    @Override
    public boolean confirmNewClient(String ClientName) throws RemoteException {
        if (ClientName.equals("manager")) return true;

        int result = JOptionPane.showConfirmDialog(
                null,
                "Do you approve '" + ClientName + "' joining?",
                "New connection request",
                JOptionPane.YES_NO_OPTION
        );

        return result == JOptionPane.YES_OPTION;
    }

    @Override
    public void kicked() throws RemoteException {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(null,
                    "You were kicked out by manager (or server closed)",
                    "Disconnected",
                    JOptionPane.WARNING_MESSAGE);
            System.exit(0);
        });
    }
}