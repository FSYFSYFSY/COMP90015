package org.code;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.lang.reflect.Type;
import java.rmi.RemoteException;
import java.util.List;


public class FileMenuBar extends JMenuBar {
    private final WhiteBoardService service;
    private final String clientName;

    public FileMenuBar(WhiteBoardService service, String clientName) {
        this.service = service;
        this.clientName = clientName;

        boolean isManager = "manager".equals(clientName);

        JMenu fileMenu = new JMenu("File");

        JMenuItem newItem = new JMenuItem("New");
        JMenuItem openItem = new JMenuItem("Open");
        JMenuItem saveItem = new JMenuItem("Save");

        newItem.setEnabled(isManager);
        openItem.setEnabled(isManager);
        saveItem.setEnabled(isManager);

        newItem.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(null, "Clear current canvas?", "New Drawing", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                try {
                    service.clearBoard();
                } catch (RemoteException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        //Load from .dat file
        openItem.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
                    List<Shape> localShapes = (List<Shape>) in.readObject();

                    service.loadShapesFromFile(localShapes);
                } catch (IOException | ClassNotFoundException er) {
                    er.printStackTrace();
                }
            }
        });

        //Please manually name it as a .dat file when you save it
        saveItem.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
                    List<Shape> shapes = service.getCurrentBoard();
                    out.writeObject(shapes);

                    service.saveShapesToFile(shapes);
                } catch (IOException er) {
                    er.printStackTrace();
                }
            }
        });


        fileMenu.add(newItem);
        fileMenu.add(openItem);
        fileMenu.add(saveItem);

        this.add(fileMenu);
    }
}
