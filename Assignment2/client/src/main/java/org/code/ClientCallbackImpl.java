package org.code;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class ClientCallbackImpl extends UnicastRemoteObject implements ClientCallback {
    private final DrawingPanel drawingPanel;

    public ClientCallbackImpl(DrawingPanel drawingPanel) throws RemoteException {
        super();
        this.drawingPanel = drawingPanel;
    }

    @Override
    public void updateBoard(List<Shape> shapes) throws RemoteException {
        // 将新的图形列表传给客户端的画布
        drawingPanel.setShapes(shapes);
        drawingPanel.repaint();  // 触发重绘
    }
}