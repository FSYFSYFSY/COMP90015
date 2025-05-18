package org.code;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class WhiteBoardServiceImpl extends UnicastRemoteObject implements WhiteBoardService {
    private final List<Shape> shapes;
    private final List<ClientCallback> clients;

    public WhiteBoardServiceImpl() throws RemoteException {
        super();
        shapes = new ArrayList<>();
        clients = new ArrayList<>();
    }

    @Override
    public void drawShape(Shape shape) throws RemoteException {
        shapes.add(shape);
        broadcastUpdate();
    }

    @Override
    public void eraseAt(int x, int y) throws RemoteException {
        shapes.removeIf(s -> s.containsPoint(x, y));
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
    public void registerClient(ClientCallback client) throws RemoteException {
        clients.add(client);
        client.updateBoard(getCurrentBoard());
    }

    @Override
    public void unregisterClient(ClientCallback client) throws RemoteException {
        clients.remove(client);
    }

    private void broadcastUpdate() {
        Iterator<ClientCallback> iter = clients.iterator();
        while (iter.hasNext()) {
            try {
                ClientCallback client = iter.next();
                client.updateBoard(getCurrentBoard());
            } catch (RemoteException e) {
                iter.remove();
            }
        }
    }
}