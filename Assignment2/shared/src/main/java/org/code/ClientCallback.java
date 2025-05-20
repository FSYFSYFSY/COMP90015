package org.code;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ClientCallback extends Remote {

    void updateBoard(List<Shape> lines) throws RemoteException;
    void updateClients(List<String> clients) throws RemoteException;

    String getclientname() throws RemoteException;

    boolean confirmNewClient(String ClientName) throws RemoteException;

    void kicked() throws RemoteException;

    void receiveMessage(String sender, String message) throws RemoteException;
}
