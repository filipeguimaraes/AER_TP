import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException, ParseException {
        Network p2p = Network.getInstance();

        if(args.length==1){
            p2p.loadPeersFromConfig(args[0]);
            p2p.loadFilesFromConfig(args[0]);
        }

        Menu.menu();

    }


}
