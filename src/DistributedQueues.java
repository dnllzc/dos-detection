import mpi.MPI;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;



public class DistributedQueues {

    public static LinkedList<Attributes> attributesList = Attributes.attributesList;

    public static void distributePackets(String[] attributes) {
        // Define the attributes
        String time = attributes[0];
        String source = "";
        String actualSource = "";
        String flags = "";
        int size = 0;
        int length = 0;
        int recordId = Attributes.numOfRecords;
        boolean isHttpAlt = false;

        // Get the attributes
        label:
        for (int i = 0; i < attributes.length; i++) {
            switch (attributes[i]) {
                case "IP":
                    source = attributes[i + 1];
                    actualSource = source.split("\\.")[0];
                    isHttpAlt = source.split("\\.")[1].equals("http-alt");
                    break;
                case "Flags":
                    flags = attributes[i + 1].substring(0, attributes[i + 1].length() - 1);
                    break;
                case "win":
                    size = Integer.parseInt(attributes[i + 1].substring(0, attributes[i + 1].length() - 1));
                    break;
                case "length":
                    if (attributes[i + 1].matches("[0-9]+")) {
                        length = Integer.parseInt(attributes[i + 1]);
                    } else {
                        length = Integer.parseInt(attributes[i + 1].substring(0, attributes[i + 1].length() - 1));
                    }
                    Attributes.numOfRecords++;
                    break label;
            }
        }

        // Add the attributes to the list
        if (!isHttpAlt) {
            attributesList.add(new Attributes(recordId, time, actualSource, flags, size, length));
        }
    }

    public static void checkSource(String source, int indexAttribute, boolean newSource) {
        String[] args = {};
        MPI.Init(args);

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

        MPI.Finalize();
    }

    public static void checkTime() {
        String[] args = {};
        MPI.Init(args);
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
