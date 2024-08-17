import java.util.LinkedList;



public class DistributedQueues {

    public static LinkedList<Attributes> attributesList = Attributes.attributesList;

    public static void distributePackets(String[] attributes) {
        // Define the attributes
        String time = attributes[0];
        String source = "";
        String flags = "";
        int size = 0;
        int length = 0;
        int recordId = Attributes.numOfRecords;
        boolean isHttpAlt = false;

        // Get the attributes
        for (int i = 0; i < attributes.length; i++) {
            switch (attributes[i]) {
                case "IP":
                    source = attributes[i + 1];
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
                    break;
            }
        }

        // Add the attributes to the list
        if (!isHttpAlt) {
            attributesList.add(new Attributes(recordId, time, source, flags, size, length));
        }
    }



}
