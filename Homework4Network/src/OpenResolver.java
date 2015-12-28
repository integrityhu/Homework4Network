import java.io.*;
import java.net.*;
import java.util.*;

import javax.naming.*;
import javax.naming.directory.*;

/*
 * Ifj. Dravecz Tibor
 * 
 */
public class OpenResolver {

    private static int port = 53;
    private static String certificateDNS = "dns1.hu";

    public static boolean isOpenResolver(String dns, String domainToResolve) throws SocketTimeoutException, UnknownHostException, CommunicationException, NamingException {
        String resolvedDomain = resolveDomain(dns, domainToResolve);
        if (resolvedDomain.equals(dns)) {
            String resolvedDNS = resolveDomain(certificateDNS,dns);
            return resolvedDomain.equals(resolvedDNS);
        } else {
            return false;
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static String resolveDomain(String dns, String domainToResolve) throws SocketTimeoutException, CommunicationException, NamingException {
        Hashtable env = new Hashtable();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.dns.DnsContextFactory");
        env.put(Context.PROVIDER_URL, "dns://" + dns + "/.");
        DirContext ictx = new InitialDirContext(env);
        Attributes attr = ictx.getAttributes(domainToResolve, new String[] { "A" });
        Attribute attrib = attr.get("A");
        String result = (String) attrib.get(0);
        return result; 
    }

    public static boolean isServerAvailable(InetAddress inetAddress) throws IOException {
        return scanTCP(inetAddress) && scanUDP(inetAddress);
    }

    public static boolean scanUDP(InetAddress inetAddress) throws IOException {
        byte[] bytes = new byte[1024];
        DatagramSocket serverSocket = new DatagramSocket(9876);
        DatagramPacket sendPacket = new DatagramPacket(bytes, bytes.length, inetAddress, port);
        serverSocket.send(sendPacket);
        serverSocket.close();
        return true;
    }

    public static boolean scanTCP(InetAddress inetAddress) throws IOException {
            Socket socket = new Socket(inetAddress, port);
            socket.close();
            return true;
    }
    
    public static void action(String[] args) {
        // a tesztelendő DNS
        String dns = args[0];
        // egy domain név amely feloldásával teszteli a program a DNS-t
        String domainToResolve = args[1];
        try {
            InetAddress dnsInetAddress = InetAddress.getByName(dns);
            System.out.println("DNS inetAddress: " + dnsInetAddress);
            boolean isAvailable = OpenResolver.isServerAvailable(dnsInetAddress);
            if (isAvailable) {
                if (OpenResolver.isOpenResolver(dns, domainToResolve)) {
                    System.out.println("Open resolver found.");
                } else {
                    System.out.println(dns + " is not an open resolver.");
                }
            } else {
                System.out.println("Server does not available!");
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Missing argument!");
        } catch (UnknownHostException e) {
            System.out.println("Bad argument!");
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
        } catch (CommunicationException e) {
            e.printStackTrace();
        } catch (NamingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
