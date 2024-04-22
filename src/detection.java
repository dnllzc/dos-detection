import java.util.*;
import java.io.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class detection{

    public static LinkedList<Attributes> attributesList = Attributes.attributesList;
    public static LinkedList<Queues> queues = Queues.queues;
    public static boolean responsePacket = false;
    public static BlockingQueue<String[]> packetQueue = new LinkedBlockingQueue<>();

    public static void main(String[] args) throws IOException, InterruptedException {
        //String[] commands = {"bash", "-c", "tcpdump", "-i", "any", "port", "8080", "and", "'(tcp-syn|tcp-ack)!=0'"};
        //String cmd = "/home/dnllzc/Desktop/DOS/src/tcpdump.sh";

        // Define the command to be executed
        String cmd2 = "tcpdump -i any port 8080 and '(tcp-syn|tcp-ack)!=0'";

        //ProcessBuilder pb = new ProcessBuilder(cmd2);
        //Process p = pb.start();

        // Process to execute the command
        ProcessBuilder pb2 = new ProcessBuilder();
        pb2.command("bash", "-c", cmd2);
        Process p = pb2.start();

        // Thread to sort packets to queues
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        PacketChecker.checkAll();
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        t.start();

        // Thread to check the deviation of the queues
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        PacketChecker.checkDeviation();
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        t2.start();

        Thread t3 = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        String[] packet = packetQueue.take();
                        processPacket(packet);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        t3.start();

        // Read the output of the command
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            //System.out.println(line);
            String[] attributes = line.split(" ");
            packetQueue.put(attributes);
        }
        p.waitFor(1, TimeUnit.MILLISECONDS);

    }

    public static void processPacket(String[] attributes) {
        for(int i=0; i<attributes.length; i++) {
            if (attributes[i].equals("IP")) {
                responsePacket = attributes[i + 1].equals(".http-alt");
                String actualSource = attributes[i + 1].split("\\.")[0];
                attributes[i + 1] = actualSource;
            }
        }

        if (responsePacket) {
            System.out.println("Response packet detected");
        }
        else {
            getAttributes(attributes);
        }
    }

    // Parse the attributes from the command output
    private static void getAttributes(String[] attributes) {
        DistributedQueues.distributePackets(attributes);
    }

    public static void printAttributes(Attributes a) {
        String format = "Record ID:" + a.recordId + "\nTime: " + a.time + "\nSource: " + a.source + "\nFlags: " + a.flags + "\nSize: " + a.size + "\nLength: " + a.length;
        System.out.println(format);
    }

}