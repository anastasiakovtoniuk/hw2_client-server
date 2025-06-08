package example;

import example.crypto.SimpleDecryptor;
import example.crypto.SimpleEncryptor;
import example.network.FakeReceiver;
import example.network.Receiver;
import example.network.Sender;
import example.processor.Processor;
import example.storage.InventoryService;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Receiver receiver = new FakeReceiver();
        Sender sender = new Sender();
        SimpleDecryptor decryptor = new SimpleDecryptor();
        SimpleEncryptor encryptor = new SimpleEncryptor();
        InventoryService inventoryService = new InventoryService();

        Processor processor = new Processor(
                decryptor, encryptor, sender, inventoryService
        );

        processor.start();
        receiver.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            receiver.stop();
            processor.stop();
            System.out.println("System shutdown");
        }));


        while (true) {
            byte[] data = ((FakeReceiver) receiver).receive();
            processor.addMessage(data);
            Thread.sleep(10);
        }
    }
}