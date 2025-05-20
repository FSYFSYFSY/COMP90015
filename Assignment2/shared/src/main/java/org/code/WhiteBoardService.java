package org.code;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface WhiteBoardService extends Remote {
    void drawShape(Shape shape) throws RemoteException;
    //void eraseAt(int x, int y) throws RemoteException;
    void clearBoard() throws RemoteException;

    boolean isClientNameAvailable(String name) throws RemoteException;

    void broadcastMessage(String sender, String message) throws RemoteException;

    List<Shape> getCurrentBoard() throws RemoteException;
    List<String> getClientList() throws RemoteException;

    void saveShapesToFile(List<Shape> shapeList) throws RemoteException;
    void loadShapesFromFile(List<Shape> loadedShapes) throws RemoteException;

    void kickClient(String clientName) throws RemoteException;


    void registerClient(ClientCallback client) throws RemoteException;
    void unregisterClient(ClientCallback client) throws RemoteException;
}