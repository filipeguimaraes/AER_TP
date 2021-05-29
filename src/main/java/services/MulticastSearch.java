package services;

import network.Message;
import network.Variables;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class MulticastSearch {

    private static MulticastSearch instance = null;
    private boolean flag;

    private MulticastSearch() {
        this.flag = true;
    }

    public static MulticastSearch getInstance() {
        if (instance == null) {
            instance = new MulticastSearch();
        }
        return instance;
    }


    /**
     * Serviço que obtém peers diretamente conectados usando o multicast.
     */
    public void obtainPeersOnMulticast() {
        new Thread(() -> {
            while (true) {
                try {
                    List<InetAddress> addrs = obtainValidAddresses(InetAddress.getByName(Variables.MULTICAST_ADDRESS));

                    Message hello = new Message(Variables.HELLO, null);
                    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    final ObjectOutputStream oos = new ObjectOutputStream(baos);
                    oos.writeObject(hello);

                    final byte[] data = baos.toByteArray();
                    MulticastSocket ms = new MulticastSocket();
                    DatagramPacket dp = new DatagramPacket(
                            data,
                            data.length,
                            InetAddress.getByName(Variables.MULTICAST_ADDRESS),
                            Variables.MULTICAST_PORT);

                    for (InetAddress addr : addrs) {
                        ms.setInterface(addr);
                        ms.send(dp);

                    }
                    ms.close();
                    Thread.sleep(Variables.HELLO_TIME);
                } catch (Exception e) {
                    System.out.println("obtainPeersOnMulticast: " + e.getMessage());
                }
            }
        }).start();

    }

    /**
     * Obter os endereços multicast no nodo local.
     *
     * @param group MULTICAST ADDRESS
     * @return Lista de endereços na rede
     */
    public List<InetAddress> obtainValidAddresses(InetAddress group) throws SocketException {
        List<InetAddress> result = new ArrayList<>();

        //verify if group is a multicast address
        if (group == null || !group.isMulticastAddress()) return result;

        //obtain the network interfaces list
        Enumeration<NetworkInterface> ifs = NetworkInterface.getNetworkInterfaces();
        while (ifs.hasMoreElements()) {
            NetworkInterface ni = ifs.nextElement();
            //ignoring loopback, inactive interfaces and the interfaces that do not support multicast
            if (ni.isLoopback() || !ni.isUp() || !ni.supportsMulticast()) {
                continue;
            }
            Enumeration<InetAddress> addresses = ni.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress addr = addresses.nextElement();
                //including addresses of the same type of group address
                if (group.getClass() != addr.getClass()) continue;
                if ((group.isMCLinkLocal() && addr.isLinkLocalAddress())
                        || (!group.isMCLinkLocal() && !addr.isLinkLocalAddress())) {
                    result.add(addr);
                }
            }
        }

        return result;
    }

    public void init() {
        this.flag = true;
        obtainPeersOnMulticast();
    }

    public void stop() {
        this.flag = false;
    }

    public String getState() {
        return this.flag ? "ON" : "OFF";
    }


}
