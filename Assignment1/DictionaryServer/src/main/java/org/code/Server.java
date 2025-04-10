package org.code;
import java.net.*;
import java.io.*;
import java.util.Set;
import java.util.concurrent.*;

public class Server {

    private static dictionary dict = new dictionary();
    public static BlockingQueue<Task> taskQueue = new LinkedBlockingQueue<>();

    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length != 2) {
            System.out.println("The command should be : java -jar DictionaryServer.jar <port> <dictionary-file>");
            System.exit(1);
        }

        int port = Integer.parseInt(args[0]);
        String path = args[1];

        try {
            dict.set(path);
        } catch (Exception e) {
            e.printStackTrace();
        }

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
                switch(command) {
                    case "add": {
                        String key = requestParts[1];
                        String value = requestParts[2];
                        dict.add(key, value);
                        response = "Dictionary Added " + key + " : " + value;
                        break;
                    }
                    case "add_exist": {
                        String key = requestParts[1];
                        String value = requestParts[2];
                        dict.add(key, value);
                        response = "Dictionary exist word Added " + key + " : " + value;
                        break;
                    }
                    case "remove": {
                        String key = requestParts[1];
                        String result = dict.remove(key);
                        response = "Dictionary removed status: " + result;
                        break;
                    }
                    case "update": {
                        String key = requestParts[1];
                        String value = requestParts[2];
                        String new_value = requestParts[3];
                        String result = dict.update(key, value, new_value);
                        response = "Dictionary word Updated status: " + result;
                        break;
                    }
                    case "query": {
                        String key = requestParts[1];
                        Set<String> query_result = dict.query(key);
                        response = query_result.toString();
                        break;
                    }
                    default:
                        response = "Invalid command!";
                        break;
                }
            } catch(Exception e) {
                response = "Error processing task: " + e.getMessage();
            }

            task.out.println(response);
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
                try { socket.close(); } catch (IOException e) {}
            }
        }
    }
}