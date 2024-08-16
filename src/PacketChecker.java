import java.util.LinkedList;

public class PacketChecker {

    public static LinkedList<Attributes> attributesList = Attributes.attributesList;
    public static LinkedList<Queues> queues = Queues.queues;
    public static String sources = "";
    public static int indexAttribute;
    public static boolean newSource = true;

    // Check all attributes of the packet
    public static void checkAll() {
        for (Attributes a : attributesList) {

            // Check if the source is new
            if (!sources.contains(a.source)) {
                sources += a.source + " ";
                newSource = true;
            }
            else {
                newSource = false;
            }

            // Save the index of the attribute
            indexAttribute = attributesList.indexOf(a);
            checkSource(a.source);
            //System.out.println(a.source);
        }

        // Get the time of the packet
        checkTime();

    }

    // Check the source of the packet
    public static void checkSource(String source) {
        DistributedQueues.checkSource(source, indexAttribute, newSource);
    }

    // Check the time of the packet
    public static void checkTime() {
        DistributedQueues.checkTime();
    }


    public static void checkDeviation() {
        // Check if there is an unusual deviation in the number of packets of plus minus 30%
        int sum = 0;
        for (int i = 0; i < Queues.queues.size(); i++) {
            sum += Queues.queues.get(i).numOfRecords;
        }
        if (!Queues.queues.isEmpty()) {
            int average = sum / Queues.queues.size();
            for (int i = 0; i < Queues.queues.size(); i++) {
                if (Queues.queues.get(i).numOfRecords > average * 1.3 || Queues.queues.get(i).numOfRecords < average * 0.7) {
                    System.out.println("Warning: Unusual deviation in the number of packets");
                    System.out.println("In queue " + Queues.queues.get(i).source + " there are " + Queues.queues.get(i).numOfRecords + " packets");
                    System.out.println("Average number of packets is " + average);
                    System.out.println("Expected number of packets is between " + average * 0.7 + " and " + average * 1.3);
                }
            }
        }
    }

}
