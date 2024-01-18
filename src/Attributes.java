import java.util.LinkedList;

public class Attributes {

    public String time;
    public String source;
    public String flags;
    public int size;
    public int length;
    public boolean isMalicious;
    public int recordId = 0;
    public static int numOfRecords = 0;

    public int sourceCounter = 0;

    public static LinkedList<Attributes> attributesList = new LinkedList<>();
    public Attributes(int recordId, String time, String source, String flags, int size, int length) {
        this.recordId = recordId;
        this.time = time;
        this.source = source;
        this.flags = flags;
        this.size = size;
        this.length = length;
        this.isMalicious = PacketChecker.checkPacket(this);
    }
}
