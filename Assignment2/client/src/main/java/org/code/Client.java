package org.code;

import org.code.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.rmi.Naming;
import java.util.ArrayList;
import java.util.List;

public class Client {
    private ClientCallback callback;
    private WhiteBoardService service;
    private DrawingPanel canvas;
    private final String[] ShapeOptions = {"Line", "Rect", "Oval", "Triangle", "FreeDraw"};
    private String selectedShape;
    private int currentColor;
    static String hostName = "localhost";
    static String serviceName ="Fsy";

    public static void main(String[] args) {

        if(args.length == 2){
            hostName = args[0];
            serviceName = args[1];
        }

        SwingUtilities.invokeLater(() -> {
            try {
                new Client().start();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "启动失败: " + e.getMessage());
            }
        });
    }
        try{
        WhiteBoardService service = new WhiteBoardServiceImpl();
        Naming.rebind("rmi://"+hostName+"/"+serviceName, service);
    }catch(Exception e){
        e.printStackTrace();
    }

    public void start() throws Exception {
        // 初始化 RMI 连接
        service = (WhiteBoardService) Naming.lookup("rmi://"+hostName+"/"+serviceName);

        // 初始化 callback
        callback = new ClientCallbackImpl();

        // 注册 callback 到服务器
        service.registerClient(callback);

        // 初始化 UI
        JFrame frame = new JFrame("Whiteboard Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 800);
        frame.setLayout(new BorderLayout());

        // 创建 DrawingPanel 并传入远程服务
        canvas = new DrawingPanel(service);
        frame.add(canvas, BorderLayout.CENTER);

        // shape 选择栏
        JPanel shapePanel = new JPanel();
        for (String s : shapeOptions) {
            JButton btn = new JButton(s);
            btn.addActionListener(e -> canvas.setSelectedShape(s));
            shapePanel.add(btn);
        }
        frame.add(shapePanel, BorderLayout.SOUTH);

        // 颜色选择栏
        JPanel colorPanel = new JPanel();
        for (Color c : palette) {
            JButton btn = new JButton();
            btn.setBackground(c);
            btn.setPreferredSize(new Dimension(30, 30));
            btn.addActionListener(e -> canvas.setCurrentColor(c.getRGB()));
            colorPanel.add(btn);
        }
        frame.add(colorPanel, BorderLayout.NORTH);

        frame.setVisible(true);
    }

    /**
     * 客户端回调实现类，用于接收服务端的更新
     */
    private class ClientCallbackImpl extends UnicastRemoteObject implements ClientCallback {
        protected ClientCallbackImpl() throws RemoteException {
            super();
        }

        @Override
        public void updateBoard(java.util.List<Shape> shapes) throws RemoteException {
            canvas.setShapes(shapes);
            canvas.repaint();
        }
    }
}

