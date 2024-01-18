import org.xml.sax.helpers.AttributesImpl;

import java.util.LinkedList;

public class PacketChecker {

    private static LinkedList<Attributes> attributesList = Attributes.attributesList;
    public static String sources;

    public static boolean checkPacket(Attributes a) {
        sources = sources + a.source + " ";
        boolean susSource = checkSource(a.source);
        boolean susFlags = checkFlags(a.flags);
        boolean susSize = checkSize(a.size);
        boolean susLength = checkLength(a.length);

        if (susSource || susFlags || susSize || susLength) {
            System.out.println("\n" + "--------------------" + "\nMalicious packet detected!\n" + "--------------------" + "\n");
            return true;
        }

        return false;

    }

    public static boolean checkSource(String source) {
//        for (int i = 0; i < attributesList.size(); i++) {
//            for (int j = 0; j < attributesList.size(); j++) {
//                if (((attributesList.get(i)).source).equals((attributesList.get(j)).source)) {
//                    attributesList.get(i).sourceCounter++;
//                }
//            }
//        }
//        for (int i = 0; i < attributesList.size(); i++) {
//            if ((attributesList.get(i)).sourceCounter > 100) {
//                return true;
//            }
//        }

        String[] sources = PacketChecker.sources.split(" ");
        int counter = 0;

        for (int i = 0; i < sources.length; i++) {
            if (source.equals(sources[i])) {
                counter++;
            }
        }
        if (counter > 100) {
            return true;
        }

        return false;
    }

    public static boolean checkFlags(String flags) {
        return false;
    }

    public static boolean checkSize(int size) {
//        for (int i = 0; i < attributesList.size(); i++) {
//            if ((attributesList.get(i)).size > 65535) {
//                return true;
//            }
//        }
        if (size > 65535) {
            return true;
        }
        return false;
    }

    public static boolean checkLength(int length) {
//        for (int i = 0; i < attributesList.size(); i++) {
//            if ((attributesList.get(i)).length > 512) {
//                return true;
//            }
//        }
        if (length > 512) {
            return true;
        }
        return false;
    }

}
