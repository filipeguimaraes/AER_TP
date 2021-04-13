import java.net.SocketException;
import java.net.UnknownHostException;

public class Main {

    public static void main(String[] args) throws SocketException, UnknownHostException {
        Network p2p = Network.getInstance();
        new Thread(() -> {
            while (true) {
                try {
                    p2p.sendHellos();
                    Thread.sleep(2000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }).start();
    }

}
