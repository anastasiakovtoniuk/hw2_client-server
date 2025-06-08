package example.network;

import java.net.InetAddress;

public class Sender {
    public void sendMessage(byte[] message, InetAddress target) {
        System.out.println("Response sent: " + new String(message));
    }
}