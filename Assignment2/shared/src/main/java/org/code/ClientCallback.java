package org.code;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ClientCallback extends Remote {

    void updateBoard(List<Shape> lines) throws RemoteException;
}
