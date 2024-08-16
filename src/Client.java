import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.Random;

public class Client extends Thread{
    static DatagramPacket sendPacket;
    static byte[] sendData;
    private static int count=0;
    public static Random rand;

    public static void main(String[] args) throws IOException {
        rand = new Random();
        // Create a Datagram Socket
        DatagramSocket clientSocket = new DatagramSocket();
        // Set client timeout to be 1 second
        clientSocket.setSoTimeout(1000);

        int availableCores = Runtime.getRuntime().availableProcessors()/2;
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter number of packets to send: ");
        int numPackets = sc.nextInt();

        while(count!=numPackets) {
            for(int i=0; i< availableCores; i++) {
                Thread thread = new Thread(() -> {
                    try {
                        // Create a DatagramPacket to send to the server
                        sendData = new byte[1024];
                        InetAddress IPAddress = InetAddress.getByName("localhost");
                        int randomNum = rand.nextInt(1000);
                        int packet = rand.nextInt(1, 999999999);
                        String sentence = Integer.toString(randomNum);
                        int leftLimit = 97; // letter 'a'
                        int rightLimit = 122; // letter 'z'
                        int targetStringLength = rand.nextInt(10);
                        Random random = new Random();
                        StringBuilder buffer = new StringBuilder(targetStringLength);
                        for (int k = 0; k < targetStringLength; k++) {
                            int randomLimitedInt = leftLimit + (int)
                                    (random.nextFloat() * (rightLimit - leftLimit + 1));
                            buffer.append((char) randomLimitedInt);
                        }
                        String generatedString = buffer.toString();
                        sendData = generatedString.getBytes();
                        sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 5001);
                        clientSocket.send(sendPacket);
                        System.out.println("Sent packet to server");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                thread.start();
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            count++;
        }

        clientSocket.close();
        System.out.println("Sent " + count + " packets to server");

    }
}