import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

class Client {
    DatagramSocket clientSocket;
    InetAddress IPAddressServer;
    int port;


    public Client(String IPAddressServer, int port) throws SocketException, UnknownHostException {
        this.clientSocket = new DatagramSocket();
        this.IPAddressServer = InetAddress.getByName(IPAddressServer);
        this.port = port;
    }

    public static void main(String[] args) throws IOException {
        Client c = new Client("localhost", 1234);
        c.broadcastCall();
        /*
        while(true) {
            c.send();
        }
        */

        //c.close();
    }

    public void send() throws IOException {
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        byte[] sendData;
        byte[] receiveData = new byte[1024];

        System.out.println("Digite o texto a ser enviado ao servidor: ");
        String sentence = inFromUser.readLine();
        sendData = sentence.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData,
                sendData.length,
                IPAddressServer,
                port);

        System.out.println("Enviando pacote UDP para " + IPAddressServer.getHostAddress() + ":" + port);
        clientSocket.send(sendPacket);

        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

        clientSocket.receive(receivePacket);
        System.out.println("Pacote UDP recebido...");

        String modifiedSentence = new String(receivePacket.getData());

        System.out.println("Texto recebido do servidor:" + modifiedSentence);
    }

    public void broadcastCall(){
        try {
            DatagramSocket c = new DatagramSocket();
            c.setBroadcast(true);

            byte[] sendData = "SOMEBODY_LISTEN".getBytes();

            //ff02::1 //ff02::5
            DatagramPacket sendPacket = new DatagramPacket(sendData,
                                                           sendData.length,
                                                           InetAddress.getByName("ff02::1"),
                                                        1234);

            c.send(sendPacket);
            System.out.println("> Searching for nodes...");

            byte[] recvBuf = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
            c.receive(receivePacket);


            System.out.println("> Broadcast response from server: " + receivePacket.getAddress().getHostAddress());

            /*
             *  NOW you have the server IP in receivePacket.getAddress()
             */

            //Close the port!
            c.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void close() {
        clientSocket.close();
        System.out.println("Socket cliente fechado!");

    }


}