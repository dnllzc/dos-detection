import org.xml.sax.helpers.AttributesImpl;

import java.util.LinkedList;

public class Queues {

    public String time;
    public String source;
    public String recordIds = "";
    public int numOfRecords;

    public int sourceCounter = 0;

    public static LinkedList<Queues> queues = new LinkedList<>();
    public int maxQueues = 10;
    public static int numQueues = 0;

    public Queues(Attributes a) {
        // Each queue has assigned source to it
        // amd am array of records that are associated with that source
        this.source = a.source;
        this.recordIds = Integer.toString(a.recordId);
    }

}
