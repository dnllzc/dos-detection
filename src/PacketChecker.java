import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

public class PacketChecker {

    public static LinkedList<Attributes> attributesList = Attributes.attributesList;
    public static LinkedList<Queues> queues = Queues.queues;
    public static String sources = "";
    public static int indexAttribute;
    public static boolean newSource = true;

    // Check all attributes of the packet
    public static void checkAll() {
        System.out.println("----------------------");
        System.out.println("Debug prints");
        System.out.println("----------------------");
        for (Attributes a : attributesList) {
            // Check if the source is new
            if (!sources.contains(a.source)) {
                sources = sources + a.source + " ";
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
        DebugPrints();

    }

    // Check the source of the packet
    public static void checkSource(String source) {
        // If the queues are empty, add the first one
        if (Queues.queues.isEmpty()) {
            Queues.queues.add(new Queues(attributesList.get(indexAttribute)));
            Queues.numQueues++;
            Queues.queues.get(0).recordIds += attributesList.get(indexAttribute).recordId;
            Queues.queues.get(0).numOfRecords++;
            newSource = false;
        }
        // If the source is new, add a new queue
        // else, add the record to the existing queue
        for (int i = 0; i < Queues.numQueues; i++) {
            if (newSource) {
                Queues.queues.add(new Queues(attributesList.get(indexAttribute)));
                Queues.numQueues++;
                newSource = false;
                Queues.queues.get(i).recordIds += "," + attributesList.get(indexAttribute).recordId;
                Queues.queues.get(i).numOfRecords++;
                break;
            }
            else {
                for (int j = 0; j < Queues.queues.size(); j++) {
                    if (Queues.queues.get(j).source.equals(source)) {
                        if (Queues.queues.get(j).recordIds.contains(Integer.toString(attributesList.get(indexAttribute).recordId))) {
                            break;
                        }
                        Queues.queues.get(j).recordIds += "," + attributesList.get(indexAttribute).recordId;
                        Queues.queues.get(j).numOfRecords++;
                        //System.out.println("TEMP DEBUG: NUMBER OF RECORDS: " + Queues.queues.get(j).numOfRecords);
                        //System.out.println("TEMP DEBUG: NUMBER OF RECORDS 2: " + Queues.queues.get(j).recordIds.split(",").length);
                        break;
                    }
                }
                break;
            }

        }
    }

    // Check the time of the packet
    public static void checkTime() {
        for (int i = 0; i < Queues.queues.size(); i++) {
            String[] recordsQueues = Queues.queues.get(i).recordIds.split(",");
            if (Queues.queues.get(i).numOfRecords < 1) {
                break;
            }
            int recordIndexInQueue = Integer.parseInt(recordsQueues[Queues.queues.get(i).numOfRecords - 1]);
            String time = attributesList.get(recordIndexInQueue).time;
            // Remove all entries in queue that are older than 2 minutes
            // 20:45:17.000000 - time format
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSSSSS");
            String stringTime = sdf.format(new Date());
            System.out.println("Debug Log: Current time: " + stringTime);
            System.out.println("Debug Log: Packet time: " + time);
            String[] currTime = stringTime.split(":");
            String[] packetTime = time.split(":");

            int minutesCurr = Integer.parseInt(currTime[1]);
            int minutesPacket = Integer.parseInt(packetTime[1]);

            System.out.println("Debug Log: Current minutes: " + minutesCurr);
            System.out.println("Debug Log: Packet minutes: " + minutesPacket);

            if (minutesCurr - minutesPacket > 2) {
                // Remove record from Attributes list with the same recordId
                for (int j = 0; j < attributesList.size(); j++) {
                    if (attributesList.get(j).recordId == recordIndexInQueue) {
                        attributesList.remove(j);
                        break;
                    }
                }
                // Remove record from Queues list with the same recordId
                // and shift all records to the left
                for (int j = 0; j < recordsQueues.length; j++) {
                    if (Integer.parseInt(recordsQueues[j]) == recordIndexInQueue) {
                        for (int k = j; k < recordsQueues.length - 1; k++) {
                            recordsQueues[k] = recordsQueues[k + 1];
                        }
                        break;
                    }
                }
            }
        }
    }

    // Just some debugging prints
    public static void DebugPrints() {
        System.out.println("Debug Log: No. records: " + Attributes.numOfRecords);
        System.out.println("Debug Log: No. queues: " + Queues.queues.size());
        System.out.println("Debug Log: No. queues alt: " + Queues.numQueues);
        if (!Queues.queues.isEmpty()) {
            System.out.println("Debug Log: Q#0 records: " + Queues.queues.get(0).recordIds.split(",").length);
            System.out.println("Debug Log: Q#0 records alt: " + Queues.queues.get(0).numOfRecords);
        }
        else {
            System.out.println("Debug Log: No queues available");
        }
        System.out.println("Debug Log: Sources: " + sources);
        System.out.println("Debug Log: Index attribute: " + indexAttribute);
    }

//        public static boolean checkFlags(String flags) {
//            return false;
//        }
//
//        public static boolean checkSize(int size) {
//            if (size > 65535) {
//                return true;
//            }
//            return false;
//        }
//
//        public static boolean checkLength(int length) {
//            if (length > 512) {
//                return true;
//            }
//            return false;
//        }

}
