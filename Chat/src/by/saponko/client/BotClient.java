package by.saponko.client;

import by.saponko.ConsoleHelper;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class BotClient extends Client {
    @Override
    protected SocketThread getSocketThread() {
        return new BotSocketThread();
    }

    @Override
    protected boolean shouldSendTextFromConsole() {
        return false;
    }

    @Override
    protected String getUserName() {
        return "date_bot_" +(int) (Math.random() * 100);
    }

    public static void main(String[] args) {
        BotClient botClient = new BotClient();
        botClient.run();
    }

    public class BotSocketThread extends SocketThread {
        @Override
        protected void clientMainLoop() throws IOException, ClassNotFoundException {
            sendTextMessage("Привет чатику. Я бот. Понимаю команды: дата, день, месяц, год, время, час, минуты, секунды.");
            super.clientMainLoop();
        }

        @Override
        protected void processIncomingMessage(String message) {
            ConsoleHelper.writeMessage(message);
            if (message != null && !message.isEmpty() && message.contains(":")) {

                String[] str = message.split(": ");

                switch (str[1]) {
                    case "дата":
                        sendTextMessage("Информация для " + str[0] + ": " + new SimpleDateFormat("d.MM.YYYY").format(Calendar.getInstance().getTime()));
                        break;
                    case "день":
                        sendTextMessage("Информация для " + str[0] + ": " + new SimpleDateFormat("d").format(Calendar.getInstance().getTime()));
                        break;
                    case "месяц":
                        sendTextMessage("Информация для " + str[0] + ": " + new SimpleDateFormat("MMMM").format(Calendar.getInstance().getTime()));
                        break;
                    case "год":
                        sendTextMessage("Информация для " + str[0] + ": " + new SimpleDateFormat("YYYY").format(Calendar.getInstance().getTime()));
                        break;
                    case "время":
                        sendTextMessage("Информация для " + str[0] + ": " + new SimpleDateFormat("H:mm:ss").format(Calendar.getInstance().getTime()));
                        break;
                    case "час":
                        sendTextMessage("Информация для " + str[0] + ": " + new SimpleDateFormat("H").format(Calendar.getInstance().getTime()));
                        break;
                    case "минуты":
                        sendTextMessage("Информация для " + str[0] + ": " + new SimpleDateFormat("m").format(Calendar.getInstance().getTime()));
                        break;
                    case "секунды":
                        sendTextMessage("Информация для " + str[0] + ": " + new SimpleDateFormat("s").format(Calendar.getInstance().getTime()));
                        break;
                }

            }

        }
    }
}

