import java.util.*;
import java.io.*;
import java.util.concurrent.TimeUnit;

public class detection{
    public static void main(String[] args) throws IOException, InterruptedException {
        //String[] commands = {"bash", "-c", "tcpdump", "-i", "any", "port", "8080", "and", "'(tcp-syn|tcp-ack)!=0'"};
        //String cmd = "/home/dnllzc/Desktop/DOS/src/tcpdump.sh";
        String cmd2 = "tcpdump -i any port 8080 and '(tcp-syn|tcp-ack)!=0'";

        //ProcessBuilder pb = new ProcessBuilder(cmd2);
        //Process p = pb.start();

        ProcessBuilder pb2 = new ProcessBuilder();
        pb2.command("bash", "-c", cmd2);
        Process p = pb2.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line = null;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
        p.waitFor(10, TimeUnit.MILLISECONDS);

    }
}