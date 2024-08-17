import mpi.MPI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class detection{

    public static LinkedList<Attributes> attributesList = Attributes.attributesList;
    public static LinkedList<Queues> queues = Queues.queues;
    public static boolean responsePacket = false;
    public static BlockingQueue<String[]> packetQueue = new LinkedBlockingQueue<>();
    public static String[] mpiArgs;
    public static boolean visualization = false;

    public static void main(String[] args) throws IOException, InterruptedException {
        //String[] commands = {"bash", "-c", "tcpdump", "-i", "any", "port", "8080", "and", "'(tcp-syn|tcp-ack)!=0'"};
        //String cmd = "/home/dnllzc/Desktop/DOS/src/tcpdump.sh";
        mpiArgs = args;
        MPI.Init(mpiArgs);

        int rank = MPI.COMM_WORLD.Rank();

        // Define the command to be executed
        String cmd2 = "tcpdump -i any port 8080 and '(tcp-syn|tcp-ack)!=0'";

        //ProcessBuilder pb = new ProcessBuilder(cmd2);
        //Process p = pb.start();
        if (rank == 0 && !visualization) {
            visualTest.execute();
            visualization = true;
        }

        // Process to execute the command
        ProcessBuilder pb2 = new ProcessBuilder();
        pb2.command("bash", "-c", cmd2);
        Process p;
        p = pb2.start();



        // Thread to sort packets to queues
        Thread packetSort = new Thread(new Runnable() {
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
        packetSort.start();

        // Thread to check the deviation of the queues
        Thread deviationCheck = new Thread(new Runnable() {
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
        deviationCheck.start();

        Thread packetProcess = new Thread(new Runnable() {
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
        packetProcess.start();

        Thread tdOutput = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        // Read the output of the command
                        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            //System.out.println(line);
                            String[] attributes = line.split(" ");
                            packetQueue.put(attributes);
                        }
                    }
                    catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        if (rank == 0) {
            tdOutput.start();
        }

        Thread allLogs = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        DebugPrints();
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        if (rank == 0) {
            allLogs.start();
        }


        if (rank == 0) {
            p.waitFor(1, TimeUnit.MILLISECONDS);
        }

        MPI.Finalize();
    }

    public static void processPacket(String[] attributes) {
        for(int i=0; i<attributes.length; i++) {
            if (attributes[i].equals("IP")) {
                responsePacket = attributes[i + 1].contains(".http-alt");
                //System.out.println("Response packet: " + responsePacket + " " + attributes[i + 1]);
                break;
            }
        }

        if (responsePacket) {
            //System.out.println("Response packet detected");
        }
        else {
            getAttributes(attributes);
        }
    }

    // Parse the attributes from the command output
    private static void getAttributes(String[] attributes) {
        for (int i=0; i<attributes.length; i++) {
            if (attributes[i].equals("IP")) {
                String tempSource = attributes[i + 1];
                String[] actualSource = tempSource.split("\\.");
                attributes[i + 1] = actualSource[0];
                break;
            }
        }
        DistributedQueues.distributePackets(attributes);
    }

    public static void DebugPrints() {
        System.out.println("----------------------");
        System.out.println("Debug prints");
        System.out.println("----------------------");
        System.out.println("Debug Log: No. records: " + Attributes.numOfRecords);
        System.out.println("Debug Log: No. queues: " + Queues.queues.size());
        System.out.println("Debug Log: No. queues alt: " + Queues.numQueues);
        if (!Queues.queues.isEmpty()) {
            for (int i = 0; i < Queues.queues.size(); i++) {
                System.out.println("Debug Log: Q#" + i + " records: " + Queues.queues.get(i).recordIds.split(",").length);
                System.out.println("Debug Log: Q#" + i + " records alt: " + Queues.queues.get(i).numOfRecords);
            }
        }
        else {
            System.out.println("Debug Log: No queues available");
        }
        System.out.println("Debug Log: Sources: " + PacketChecker.sources);
        System.out.println("Debug Log: Index attribute: " + PacketChecker.indexAttribute);
    }

}