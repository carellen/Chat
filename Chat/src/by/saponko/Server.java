package by.saponko;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {

    private static Map<String, Connection> connectionMap = new ConcurrentHashMap<>();
    public static void main(String[] args) {

        try (ServerSocket serverSocket = new ServerSocket(ConsoleHelper.readInt())){
            System.out.println("Сервер запущен.");
            Socket socket = null;
            while (true) {
                socket = serverSocket.accept();
                new Handler(socket).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void sendBroadcastMessage(Message message) {

        for (Map.Entry<String, Connection> m : connectionMap.entrySet()
                ) {
            try {
                m.getValue().send(message);
            } catch (IOException e) {
                System.out.println("Сообщение не отправлено!");
            }
        }
    }

    private static class Handler extends Thread {
        private Socket socket;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        private String serverHandshake(Connection connection) throws IOException, ClassNotFoundException {
            while (true) {
                connection.send(new Message(MessageType.NAME_REQUEST));
                Message ans = connection.receive();
                if (!ans.getType().equals(MessageType.USER_NAME)) {
                    continue;
                }
                else if (ans.getType() == MessageType.USER_NAME && !(ans.getData().isEmpty())){
                    if (!connectionMap.containsKey(ans.getData())) {
                        connectionMap.put(ans.getData(), connection);
                        connection.send(new Message(MessageType.NAME_ACCEPTED));
                        return ans.getData();
                    }
                }

            }
        }

        private void sendListOfUsers(Connection connection, String userName) throws IOException {
            for (String n : connectionMap.keySet()
                    ) {
                if (!n.equals(userName)) {
                    connection.send(new Message(MessageType.USER_ADDED, n));
                }
            }
        }

        private void serverMainLoop(Connection connection, String userName) throws IOException, ClassNotFoundException {
            while(true) {
                Message in = connection.receive();
                if (in.getType() == MessageType.TEXT) {
                    sendBroadcastMessage(new Message(MessageType.TEXT, userName + ": " + in.getData()));
                }
                else {
                    connection.send(new Message(MessageType.TEXT, "Неверный формат сообщения!"));
                }
            }
        }

        @Override
        public void run() {
            System.out.println("Соединение установлено: " + socket.getRemoteSocketAddress());
            String userName = null;
            try (Connection newConnection = new Connection(socket)){
                userName = serverHandshake(newConnection);
                sendBroadcastMessage(new Message(MessageType.USER_ADDED, userName));
                sendListOfUsers(newConnection, userName);
                serverMainLoop(newConnection, userName);


            } catch (ClassNotFoundException e) {
                System.out.println("Произошла ошибка при обмене данными с удаленным адресом.");
            } catch (IOException e) {
                System.out.println("Произошла ошибка при обмене данными с удаленным адресом.");

            } finally {
                if (userName != null) {
                    connectionMap.remove(userName);
                    sendBroadcastMessage(new Message(MessageType.USER_REMOVED, userName));
                }
                System.out.println("Соединение с удаленным адресом закрыто");
            }
        }
    }
}
