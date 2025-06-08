package example.storage;

import example.model.Message;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class InventoryService {
    private final ConcurrentHashMap<String, Integer> inventory = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Double> prices = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, ReentrantLock> locks = new ConcurrentHashMap<>();

    public String process(Message message) {
        String itemName = message.getItemName();
        ReentrantLock lock = locks.computeIfAbsent(itemName, k -> new ReentrantLock());

        lock.lock();
        try {
            return switch (message.getCommand()) {
                case "GET_QUANTITY" -> getQuantity(itemName);
                case "WRITE_OFF" -> writeOff(itemName, message.getQuantity());
                case "ACCEPT" -> accept(itemName, message.getQuantity());
                case "ADD_ITEM" -> addItem(itemName, message.getCategory());
                case "SET_PRICE" -> setPrice(itemName, message.getPrice());
                case "ADD_CATEGORY" -> "OK";
                default -> "Unknown command";
            };
        } finally {
            lock.unlock();
        }
    }

    private String getQuantity(String itemName) {
        return "Quantity: " + inventory.getOrDefault(itemName, 0);
    }

    private String writeOff(String itemName, int quantity) {
        int current = inventory.getOrDefault(itemName, 0);
        if (current < quantity) return "Error: Insufficient stock";
        inventory.put(itemName, current - quantity);
        return "OK";
    }

    private String accept(String itemName, int quantity) {
        inventory.put(itemName, inventory.getOrDefault(itemName, 0) + quantity);
        return "OK";
    }

    private String addItem(String itemName, String category) {
        inventory.putIfAbsent(itemName, 0);
        return "OK";
    }

    private String setPrice(String itemName, double price) {
        prices.put(itemName, price);
        return "OK";
    }
}