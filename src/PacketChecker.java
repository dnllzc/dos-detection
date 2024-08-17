import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

public class PacketChecker {

    public static LinkedList<Attributes> attributesList = Attributes.attributesList;
    public static LinkedList<Queues> queues = Queues.queues;
    public static String sources = "";
    public static int indexAttribute;
    public static boolean newSource = true;
    public static int average = 0;
    public static int deviationQueue = -2;

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
            checkSource(a.source, indexAttribute, newSource);
            //System.out.println(a.source);
        }

        // Get the time of the packet
        checkTime();

    }


    public static void checkDeviation() {
        // Check if there is an unusual deviation in the number of packets of plus minus 30%
        int sum = 0;
        for (int i = 0; i < Queues.queues.size(); i++) {
            sum += Queues.queues.get(i).numOfRecords;
        }
        if (!Queues.queues.isEmpty()) {
            average = sum / Queues.queues.size();
            for (int i = 0; i < Queues.queues.size(); i++) {
                if (Queues.queues.get(i).numOfRecords > average * 1.3) {
                    System.out.println("Warning: Unusual deviation in the number of packets");
                    System.out.println("In queue " + Queues.queues.get(i).source + " there are " + Queues.queues.get(i).numOfRecords + " packets");
                    System.out.println("Average number of packets is " + average);
                    System.out.println("Expected number of packets is between " + average * 0.7 + " and " + average * 1.3);
                    deviationQueue = i;
                }
                else {
                    deviationQueue = -1;
                }
            }
        }
        else {
            deviationQueue = -2;
        }
    }

    public static void checkSource(String source, int indexAttribute, boolean newSource) {
        //String[] args = {};
        //MPI.Init(detection.mpiArgs);

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

        //MPI.Finalize();
    }

    public static void checkTime() {
        //String[] args = {};
        //MPI.Init(detection.mpiArgs);
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
            //System.out.println("Debug Log: Current time: " + stringTime);
            //System.out.println("Debug Log: Packet time: " + time);
            String[] currTime = stringTime.split(":");
            String[] packetTime = time.split(":");

            int minutesCurr = Integer.parseInt(currTime[1]);
            int minutesPacket = Integer.parseInt(packetTime[1]);

            //System.out.println("Debug Log: Current minutes: " + minutesCurr);
            //System.out.println("Debug Log: Packet minutes: " + minutesPacket);

            if (minutesCurr - minutesPacket >= 2) {
                // Remove record from Attributes list with the same recordId
                for (int j = 0; j < attributesList.size(); j++) {
                    if (attributesList.get(j).recordId == recordIndexInQueue) {
                        attributesList.remove(j);
                        Attributes.numOfRecords--;
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

}
