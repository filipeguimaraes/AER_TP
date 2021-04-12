import java.net.SocketException;
import java.net.UnknownHostException;

public class Main {

    public static void main(String[] args) throws SocketException, UnknownHostException {
        Rede p2p = new Rede();
        new Thread(() -> {
            while (true) {
                try {
                    p2p.refreshPeers();
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }).start();
    }

}
