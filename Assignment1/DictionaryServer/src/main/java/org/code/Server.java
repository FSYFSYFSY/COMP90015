package org.code;
import java.net.*;
import java.io.*;
import java.util.Set;
import java.util.concurrent.*;
import java.net.InetAddress;

//Hanjie Liu: 1667156
public class Server {

    private static dictionary dict = new dictionary();
    public static BlockingQueue<Task> taskQueue = new LinkedBlockingQueue<>();
    public static String path = null;
    public static int port = 8888;

    public static void main(String[] args) throws IOException, InterruptedException {
        if(args.length == 2) {
            port = Integer.parseInt(args[0]);
            path = args[1];
        }
        else if (args.length == 1) {
            try {
                port = Integer.parseInt(args[0]);
            }catch(NumberFormatException e){
                System.out.println("The command should be : java -jar DictionaryServer.jar <port> (<dictionary-file>)");
                System.exit(1);
            }
        }
        else{
            System.out.println("The command should be : java -jar DictionaryServer.jar <port> (<dictionary-file>)");
            System.exit(1);
        }
        try {
            dict.set(path);
        } catch (Exception e) {
            System.out.println("No such a json file in the path");
            System.exit(1);
        }

        InetAddress localhost = InetAddress.getLocalHost();
        System.out.println("Host address: " + localhost.getHostAddress());

        ServerSocket serverSocket = new ServerSocket(port);


        new Thread(new TaskProcessor()).start();

        while(true){
            Socket clientSocket = serverSocket.accept();
            System.out.println("New client connected: " + clientSocket.getRemoteSocketAddress());
            new Thread(new ClientHandler(clientSocket)).start();
        }
    }

    static class Task {
        String request;
        PrintWriter out;
        public Task(String request, PrintWriter out) {
            this.request = request;
            this.out = out;
        }
    }

    static class TaskProcessor implements Runnable {
        public void run() {
            while (true) {
                try {
                    Task task = taskQueue.take();
                    processTask(task);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        private void processTask(Task task) {
            String Request = task.request;
            String[] requestParts = Request.split(":");
            String command = requestParts[0];
            String response = "";
            try {
                switch (command) {
                    case "add": {
                        String key = requestParts[1];
                        String value = requestParts[2];
                        response = dict.add(key, value);
                        break;
                    }
                    case "add_exist": {
                        String key = requestParts[1];
                        String value = requestParts[2];
                        response = dict.add_exist(key, value);
                        break;
                    }
                    case "remove": {
                        String key = requestParts[1];
                        response = dict.remove(key);
                        break;
                    }
                    case "update": {
                        String key = requestParts[1];
                        String value = requestParts[2];
                        String new_value = requestParts[3];
                        response = dict.update(key, value, new_value);
                        break;
                    }
                    case "query": {
                        String key = requestParts[1];
                        response = dict.query(key);
                        break;
                    }
                    default:
                        response = "Invalid action command!";
                        break;
                }
            } catch (Exception e) {
                response = "Please fillout all blank textfield for your request";
            }

            task.out.println(response);
            System.out.println(response);

        }
    }

    static class ClientHandler implements Runnable {
        private Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try (
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            ) {
                String request;
                while ((request = in.readLine()) != null) {
                    taskQueue.put(new Task(request, out));
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            } finally {
                try {
                    System.out.println("Client disconnected: " + socket.getRemoteSocketAddress());
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}