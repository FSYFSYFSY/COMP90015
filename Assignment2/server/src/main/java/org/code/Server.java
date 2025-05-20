package org.code;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

import javax.swing.SwingUtilities;

public class Server {
    public static void main(String[] args) {
        String hostName = "localhost";
        String serviceName = "whiteboard";
        int port;

        if (args.length == 2) {
            hostName = args[0];
            port = Integer.parseInt(args[1]);
        } else {
            hostName = "localhost";
            port = 1099;
        }
        try {
            LocateRegistry.createRegistry(port);

            WhiteBoardService service = new WhiteBoardServiceImpl();
            Naming.rebind("rmi://" + hostName + ":" + port + "/" + serviceName, service);

            System.out.println("WhiteBoard Server started...");

            String finalHostName = hostName;
            String finalServiceName = serviceName;
            SwingUtilities.invokeLater(() -> {
                try {
                    Client.main(new String[]{finalHostName, String.valueOf(port), "manager"});
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}