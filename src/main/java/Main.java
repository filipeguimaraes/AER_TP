import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {
        System.out.println("(Type m to access the menu)");
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
        Scanner sc= new Scanner(System.in);
        if (sc.nextLine().contains("m")){
            menu();
        }

    }


    public static void menu() throws IOException {
        System.out.println("###################");
        System.out.println("1: Send a request.");
        System.out.println("###################");

        System.out.println("Opcção:");

        Scanner sc= new Scanner(System.in);
        switch (sc.nextInt()){
            case 1:
                Network.getInstance().sendQuery("test");
        }
        Scanner sc= new Scanner(System.in);
        if (sc.nextLine().contains("m")){
            menu();
        }
    }

}
