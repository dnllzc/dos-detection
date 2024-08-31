import java.text.SimpleDateFormat;
import java.util.Date;

public class BollingerBands {
    public static String time;
    public static int numOfQueues;
    public static int[][] packetsQueue = new int[4][60];
    public static int[][][] bands = new int[20][24][5];
    private static int dayCounter = 0;

    public static void Start() {
        minuteUpdate.start();
        hourUpdate.start();
    }

    public static void updateQueues() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSSSSS");
        String stringTime = sdf.format(new Date());
        int minute = Integer.parseInt(stringTime.substring(3, 5));
        numOfQueues = Queues.numQueues;
        if (numOfQueues == 0) return;
        for (int i = 0; i < numOfQueues; i++) {
            packetsQueue[i][minute] = Queues.queues.get(i).numOfRecords;
        }
    }

    public static void updateBands() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSSSSS");
        String stringTime = sdf.format(new Date());
        int hour = Integer.parseInt(stringTime.substring(0, 2));
        int sum = 0;
        for (int i = 0; i < dayCounter; i++) {
            for (int j = 0; j < 60; j++) {
                sum += packetsQueue[i][j];
            }
        }
        int average = sum / dayCounter;
        int sumSquared = 0;
        for (int i = 0; i < dayCounter; i++) {
            for (int j = 0; j < 60; j++) {
                sumSquared += (packetsQueue[i][j] - average) * (packetsQueue[i][j] - average);
            }
        }
        int standardDeviation = (int) Math.sqrt(sumSquared / dayCounter);
        bands[dayCounter][hour][0] = average + 2 * standardDeviation;
        bands[dayCounter][hour][1] = average + standardDeviation;
        bands[dayCounter][hour][2] = average;
        bands[dayCounter][hour][3] = average - standardDeviation;
        bands[dayCounter][hour][4] = average - 2 * standardDeviation;
        if (dayCounter == 20) {
            for (int i = 0; i < 19; i++) {
                bands[i] = bands[i + 1];
            }
        }
        else if (hour == 0){
            dayCounter++;
        }
    }

    static Thread minuteUpdate = new Thread() {
        public void run() {
            while (true) {
                try {
                    Thread.sleep(1000);
                    updateQueues();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    static Thread hourUpdate = new Thread() {
        public void run() {
            while (true) {
                try {
                    Thread.sleep(3600000);
                    updateBands();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };


}
