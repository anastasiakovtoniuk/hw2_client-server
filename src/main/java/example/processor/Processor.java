package example.processor;

import example.crypto.Decryptor;
import example.crypto.Encryptor;
import example.model.Message;
import example.network.Sender;
import example.storage.InventoryService;
import java.net.InetAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class Processor {
    private final BlockingQueue<byte[]> inputQueue = new LinkedBlockingQueue<>();
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final Decryptor decryptor;
    private final Encryptor encryptor;
    private final Sender sender;
    private final InventoryService inventoryService;
    private volatile boolean running;

    public Processor(Decryptor decryptor, Encryptor encryptor,
                     Sender sender, InventoryService inventoryService) {
        this.decryptor = decryptor;
        this.encryptor = encryptor;
        this.sender = sender;
        this.inventoryService = inventoryService;
    }

    public void start() {
        running = true;
        for (int i = 0; i < Runtime.getRuntime().availableProcessors(); i++) {
            executor.execute(this::processMessages);
        }
    }

    public void stop() {
        running = false;
        executor.shutdownNow();
    }

    public void addMessage(byte[] data) {
        inputQueue.add(data);
    }

    private void processMessages() {
        while (running) {
            try {
                byte[] data = inputQueue.take();
                Message message = decryptor.decrypt(data);
                String response = inventoryService.process(message);
                byte[] encrypted = encryptor.encrypt(response);
                sender.sendMessage(encrypted, InetAddress.getLoopbackAddress());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}