import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException, ParseException {
        System.out.println("(Type m to access the menu)");
        Network p2p = Network.getInstance();

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
