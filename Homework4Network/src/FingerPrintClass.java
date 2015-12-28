import java.io.IOException;

import org.apache.commons.net.finger.FingerClient;

/**
 * 
 */

/**
 * @author pzoli
 *
 */
public class FingerPrintClass {

    public static void action() {
        FingerClient finger;

        finger = new FingerClient();

        try {
          finger.connect("192.168.1.26");
          System.out.println(finger.query(true));
          finger.disconnect();
        } catch(IOException e) {
          System.err.println("Error I/O exception: " + e.getMessage());
          return;
        }
    }
}
