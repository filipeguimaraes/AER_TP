import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

class Server {

    int port;
    int pedidos;
    DatagramSocket serverSocket;

    public Server(int port) throws SocketException {
        this.port = port;
        this.pedidos = 1;
        this.serverSocket = new DatagramSocket(port);
    }

    public static void main(String[] args) throws IOException {
        Server s = new Server(1234);
        s.start();
        //s.broadcastResponder();
    }


    public void start() throws IOException {


        while (true) {

            byte[] receiveData = new byte[1024];
            byte[] sendData;

            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            System.out.println("Esperando por datagrama UDP na porta " + port);
            serverSocket.receive(receivePacket);
            System.out.print("Datagrama UDP [" + pedidos + "] recebido...");

            String sentence = new String(receivePacket.getData());
            System.out.println(sentence);

            InetAddress IPAddress = receivePacket.getAddress();

            int portAux = receivePacket.getPort();

            sendData = sentence.getBytes();

            DatagramPacket sendPacket = new DatagramPacket(sendData,
                    sendData.length, IPAddress, portAux);

            System.out.print("Enviando " + sentence + "...");

            serverSocket.send(sendPacket);
            System.out.println("OK\n");
            pedidos++;
        }
    }
}