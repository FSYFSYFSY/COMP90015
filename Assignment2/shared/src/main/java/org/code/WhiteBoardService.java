package org.code;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface WhiteBoardService extends Remote {
    void drawShape(Shape shape) throws RemoteException;
    void eraseAt(int x, int y) throws RemoteException;
    void clearBoard() throws RemoteException;

    List<Shape> getCurrentBoard() throws RemoteException;

    void registerClient(ClientCallback client) throws RemoteException;
    void unregisterClient(ClientCallback client) throws RemoteException;
}