package example.network;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class FakeReceiver implements Receiver {
    private final BlockingQueue<byte[]> queue = new LinkedBlockingQueue<>();
    private volatile boolean running;
    private Thread generatorThread;

    @Override
    public void start() {
        running = true;
        generatorThread = new Thread(() -> {
            Random random = new Random();
            while (running) {
                try {
                    String command = getRandomCommand();
                    String itemName = "item" + random.nextInt(10);
                    int quantity = random.nextInt(100);
                    double price = random.nextDouble() * 100;
                    String category = "category" + random.nextInt(5);

                    String messageStr = String.format("%s:%s:%d:%.2f:%s",
                            command, itemName, quantity, price, category);
                    queue.put(messageStr.getBytes());

                    Thread.sleep(random.nextInt(100) + 50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        generatorThread.start();
    }

    @Override
    public void stop() {
        running = false;
        if (generatorThread != null) {
            generatorThread.interrupt();
        }
    }

    public byte[] receive() throws InterruptedException {
        return queue.take();
    }

    private String getRandomCommand() {
        String[] commands = {
                "GET_QUANTITY", "WRITE_OFF", "ACCEPT",
                "ADD_CATEGORY", "ADD_ITEM", "SET_PRICE"
        };
        return commands[new Random().nextInt(commands.length)];
    }
}