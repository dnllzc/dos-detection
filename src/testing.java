import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;

public class testing extends Thread {
    public static void main(String[] args) {
        try {
            // Create a DatagramSocket to listen for incoming packets
            DatagramSocket socket = new DatagramSocket(5001);

            int availableCores = Runtime.getRuntime().availableProcessors()/2;
            System.out.println("Available cores: " + availableCores);

            byte[] buffer = new byte[1024];

            while (true) {
                // Create a DatagramPacket to receive the incoming packet
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                // create Thread on all available cores
                for (int i = 0; i < availableCores; i++) {
                    Thread thread = new Thread(() -> {
                        try {
                            socket.receive(packet);

                            // Extract the information from the packet
                            String data = new String(packet.getData(), 0, packet.getLength());
                            String sourceAddress = packet.getAddress().getHostAddress();
                            int sourcePort = packet.getPort();

                            if (packet.getLength() > 5) {
                                // Suspicious packet so drop it
                                System.out.println("DROP | Suspicious packet received from " + sourceAddress + ":" + sourcePort);
                                System.out.println("Data: " + data + " | Size: " + packet.getLength());
                                System.out.println();
                            } else {
                            // Print the packet information
                                System.out.println("ACCEPT | Received packet from " + sourceAddress + ":" + sourcePort);
                                System.out.println("Data: " + data + " | Size: " + packet.getLength());
                                System.out.println();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                    thread.start();
                    thread.join();
                }



            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
