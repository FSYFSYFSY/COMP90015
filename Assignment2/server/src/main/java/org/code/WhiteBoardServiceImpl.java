package org.code;

import java.io.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class WhiteBoardServiceImpl extends UnicastRemoteObject implements WhiteBoardService {
    private List<Shape> shapes;
    private List<ClientCallback> clients;
    private List<String> clientnames;
    private List<String> Chatmessages;

    public WhiteBoardServiceImpl() throws RemoteException {
        super();
        shapes = new ArrayList<>();
        clients = new ArrayList<>();
        clientnames = new ArrayList<>();
    }

    @Override
    public synchronized void drawShape(Shape shape) throws RemoteException {
        shapes.add(shape);
        broadcastUpdate();
    }

    @Override
    public void clearBoard() throws RemoteException {
        shapes.clear();
        broadcastUpdate();
    }

    @Override
    public List<Shape> getCurrentBoard() throws RemoteException {
        return new ArrayList<>(shapes);
    }
    @Override
    public List<String> getClientList() throws RemoteException {
        return new ArrayList<>(clientnames);
    }

    @Override
    public synchronized boolean isClientNameAvailable(String name) throws RemoteException {
        return !clientnames.contains(name);
    }

    @Override
    public synchronized void broadcastMessage(String sender, String message) throws RemoteException {
        for (ClientCallback client : clients) {
            String formatted = sender + ": " + message;
            client.receiveMessage(sender, message);
        }
    }

    @Override
    public void kickClient(String clientName) throws RemoteException {
        ClientCallback targetClient = null;

        synchronized (clients) {
            for (ClientCallback client : clients) {
                try {
                    if (client.getclientname().equals(clientName)) {
                        targetClient = client;
                        break;
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        if (targetClient != null) {
            try {
                targetClient.kicked();
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            unregisterClient(targetClient);
        }
    }

    @Override
    public synchronized void registerClient(ClientCallback callback) throws RemoteException {
        String newClientName = callback.getclientname();

        for (ClientCallback existing : clients) {
            try {
                if ("manager".equals(existing.getclientname())) {
                    boolean allow = existing.confirmNewClient(newClientName);
                    if (!allow) {
                        callback.kicked();
                        return;
                    }
                    break;
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        clients.add(callback);
        clientnames.add(newClientName);
        broadcastUpdate();
    }

    @Override
    public void unregisterClient(ClientCallback client) throws RemoteException {
        clients.remove(client);
        clientnames.remove(client.getclientname());
        broadcastUpdate();
    }

    // save it to .dat file
    @Override
    public void saveShapesToFile(List<Shape> shapeList) throws RemoteException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("shapes_backup.dat"))) {
            out.writeObject(shapeList);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RemoteException("Save failed: " + e.getMessage());
        }
    }

    //Load from .dat file
    @Override
    public void loadShapesFromFile(List<Shape> loadedShapes) throws RemoteException {
        synchronized (shapes) {
            shapes.clear();
            shapes.addAll(loadedShapes);
        }
        broadcastUpdate();
    }

    private void broadcastUpdate() {
    Iterator<ClientCallback> iter = clients.iterator();
    while (iter.hasNext()) {
        try {
            ClientCallback client = iter.next();
            client.updateBoard(getCurrentBoard());
            client.updateClients(clientnames);
        } catch (RemoteException e) {
            iter.remove();
        }
    }

    }
}