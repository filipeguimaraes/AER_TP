import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException, ParseException {
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

        if(args.length==1){
            p2p.loadPeersFromConfig(args[0]);
        }

        Scanner sc = new Scanner(System.in);
        if (sc.nextLine().equals("m")) {
            Menu.menu();
        } else {
            System.out.println("(Type m to access the menu)");
        }

    }


}
