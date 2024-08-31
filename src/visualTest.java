import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class visualTest {
    static JPanel[][] queuePanels = new JPanel[4][10];
    static Color[] colors = {Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK};

    static int numRecords = 0;
    static int numQueues = 4;
    static int[] queueRecords = {0, 0, 0, 0};
    static String sources = "";
    static int average = 0;
    static int deviation = 0;

    static JLabel currTimeLabel;
    static JLabel numRecordsLabel;
    static JLabel numQueuesLabel;
    static JLabel queue1RecordsLabel;
    static JLabel queue2RecordsLabel;
    static JLabel queue3RecordsLabel;
    static JLabel queue4RecordsLabel;
    static JLabel sourcesLabel;
    static JLabel averageLabel;
    static JLabel deviationLabel;
    static JLabel lowerBandLabel;
    static JLabel upperBandLabel;
    static JLabel expectedHourLabel;

    public static void execute() {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1280, 720);
        frame.setLayout(null);

        // Create the panel for queues
        JPanel queuesPanel = new JPanel();
        queuesPanel.setBounds(40, 40, 800, 640);
        queuesPanel.setLayout(new GridLayout(1, 4, 10, 0)); // 1 row, 4 columns, 10px gap between queues
        queuesPanel.setVisible(true);

        for (int i = 0; i < 4; i++) {
            JPanel queuePanel = new JPanel();
            queuePanel.setLayout(new GridLayout(10, 1, 0, 5)); // each queue 10 divisions
            queuePanel.setBorder(BorderFactory.createTitledBorder("Queue #" + (i + 1)));
            for (int j = 0; j < 10; j++) {
                queuePanels[i][j] = new JPanel();
                queuePanels[i][j].setBackground(colors[j]);
                queuePanel.add(queuePanels[i][j]);
            }
            queuesPanel.add(queuePanel);
        }

        JPanel infoPanel = new JPanel();
        infoPanel.setBounds(860, 40, 380, 640);
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Queue Information"));

        currTimeLabel = new JLabel("Current time: ");
        numRecordsLabel = new JLabel("No. of records: " + numRecords);
        numQueuesLabel = new JLabel("No. of queues: " + numQueues);
        queue1RecordsLabel = new JLabel("Q#1 records: " + queueRecords[0]);
        queue2RecordsLabel = new JLabel("Q#2 records: " + queueRecords[1]);
        queue3RecordsLabel = new JLabel("Q#3 records: " + queueRecords[2]);
        queue4RecordsLabel = new JLabel("Q#4 records: " + queueRecords[3]);
        sourcesLabel = new JLabel("Sources: " + sources);
        averageLabel = new JLabel("Average: " + average);
        deviationLabel = new JLabel("Deviation: ");
        lowerBandLabel = new JLabel("Lower band: ");
        upperBandLabel = new JLabel("Upper band: ");
        expectedHourLabel = new JLabel("Expected: ");

        currTimeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        numRecordsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        numQueuesLabel.setFont(new Font("Arial", Font.BOLD, 14));
        queue1RecordsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        queue2RecordsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        queue3RecordsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        queue4RecordsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        sourcesLabel.setFont(new Font("Arial", Font.BOLD, 14));
        averageLabel.setFont(new Font("Arial", Font.BOLD, 14));
        deviationLabel.setFont(new Font("Arial", Font.BOLD, 14));
        lowerBandLabel.setFont(new Font("Arial", Font.BOLD, 14));
        upperBandLabel.setFont(new Font("Arial", Font.BOLD, 14));
        expectedHourLabel.setFont(new Font("Arial", Font.BOLD, 14));

        infoPanel.add(currTimeLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 50)));
        infoPanel.add(numRecordsLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        infoPanel.add(numQueuesLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        infoPanel.add(queue1RecordsLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        infoPanel.add(queue2RecordsLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        infoPanel.add(queue3RecordsLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        infoPanel.add(queue4RecordsLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        infoPanel.add(sourcesLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        infoPanel.add(averageLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        infoPanel.add(deviationLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        infoPanel.add(lowerBandLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        infoPanel.add(upperBandLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        infoPanel.add(expectedHourLabel);

        frame.add(queuesPanel);
        frame.add(infoPanel);
        frame.setVisible(true);

        Thread updateThread = new Thread() {
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000);
                        updateLabels();
                        updateQueuePanels();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        updateThread.start();

    }

    private static void updateLabels() {

        numRecords = Attributes.numOfRecords;
        numQueues = Queues.numQueues;
        for (int i = 0; i < numQueues; i++) {
            queueRecords[i] = Queues.queues.get(i).numOfRecords;
        }
        sources = PacketChecker.sources;
        average = PacketChecker.average;
        deviation = PacketChecker.deviationQueue;

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String stringTime = sdf.format(new Date());
        currTimeLabel.setText("Current time: " + stringTime);
        numRecordsLabel.setText("No. of records: " + numRecords);
        numQueuesLabel.setText("No. of queues: " + numQueues);
        queue1RecordsLabel.setText("Q#1 records: " + queueRecords[0]);
        queue2RecordsLabel.setText("Q#2 records: " + queueRecords[1]);
        queue3RecordsLabel.setText("Q#3 records: " + queueRecords[2]);
        queue4RecordsLabel.setText("Q#4 records: " + queueRecords[3]);
        sourcesLabel.setText("Sources: " + sources);
        averageLabel.setText("Average: " + average);
        switch(deviation) {
            case -2:
                deviationLabel.setText("Deviation: No data");
                break;
            case -1:
                deviationLabel.setText("Deviation: " + PacketChecker.stDeviation);
                break;
            case 0:
                deviationLabel.setText("Deviation: Queue 1, inspect");
                break;
            case 1:
                deviationLabel.setText("Deviation: Queue 2, inspect");
                break;
            case 2:
                deviationLabel.setText("Deviation: Queue 3, inspect");
                break;
            case 3:
                deviationLabel.setText("Deviation: Queue 4, inspect");
                break;
        }

        if (BollingerBands.bands.length != 0) {
            int hour = Integer.parseInt(stringTime.substring(0, 2));
            int allTimeLow = BollingerBands.bands[0][hour][4];
            int allTimeHigh = BollingerBands.bands[0][hour][0];
            int allTimeAverage = BollingerBands.bands[0][hour][2];
            for (int i = 0; i < BollingerBands.bands.length; i++) {
                if (BollingerBands.bands[i][hour][4] < allTimeLow) {
                    allTimeLow = BollingerBands.bands[i][hour][4];
                }
                if (BollingerBands.bands[i][hour][0] > allTimeHigh) {
                    allTimeHigh = BollingerBands.bands[i][hour][0];
                }
                allTimeAverage += BollingerBands.bands[i][hour][2];
            }
            allTimeAverage /= BollingerBands.bands.length;

            lowerBandLabel.setText("Lower band: " + allTimeLow);
            upperBandLabel.setText("Upper band: " + allTimeHigh);
            expectedHourLabel.setText("Expected at this hour: " + allTimeAverage);
        } else {
            lowerBandLabel.setText("Lower band: No data");
            upperBandLabel.setText("Upper band: No data");
            expectedHourLabel.setText("Expected: No data");
        }
    }

    private static void updateQueuePanels() {
        int maxQueueSize = 0;
        for (int i = 0; i < numQueues; i++) {
            maxQueueSize = Math.max(maxQueueSize, queueRecords[i]);
        }
        if (maxQueueSize < 1000) maxQueueSize = 1000;

        for (int i = 0; i < numQueues; i++) {
            int fillPercentage = (int) ((double) queueRecords[i] / maxQueueSize * 100);
            int fillPanels = fillPercentage / 10;

            for (int j = 9; j >= 0; j--) {
                if (j >= 10 - fillPanels) {
                    if (fillPercentage <= 50) {
                        queuePanels[i][j].setBackground(new Color(5, 171, 5));
                    }
                    else if (fillPercentage <= 80) {
                        queuePanels[i][j].setBackground(new Color(224, 206, 2));
                    }
                    else {
                        queuePanels[i][j].setBackground(new Color(205, 0, 0));
                    }
                }
                else {
                    queuePanels[i][j].setBackground(Color.BLACK);
                }
            }
        }
    }
}
