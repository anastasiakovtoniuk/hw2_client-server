package example.model;

public class Message {
    private final String command;
    private final String itemName;
    private final int quantity;
    private final double price;
    private final String category;

    public Message(String command, String itemName, int quantity, double price, String category) {
        this.command = command;
        this.itemName = itemName;
        this.quantity = quantity;
        this.price = price;
        this.category = category;
    }


    public String getCommand() { return command; }
    public String getItemName() { return itemName; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public String getCategory() { return category; }
}