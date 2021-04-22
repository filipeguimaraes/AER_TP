import java.io.IOException;
import java.util.Scanner;

public class Menu {

    public static void menu() throws IOException {
        System.out.println("(Type m to access the menu)");
        Scanner s = new Scanner(System.in);
        if (!s.nextLine().equals("m")) {
            Menu.menu();
        } else {
            System.out.println("####################################");
            System.out.println("# 1: Show connected peers.         #");
            System.out.println("# 2: Show disconnected peers.      #");
            System.out.println("# 3: Search a file in the network. #");
            System.out.println("####################################");

            System.out.println("Option:");

            Scanner sc = new Scanner(System.in);
            switch (sc.nextInt()) {
                case 1:
                    Network.getInstance().printConnectedPeers();
                    break;
                case 2:
                    Network.getInstance().printDisconnectedPeers();
                    break;
                case 3:
                    System.out.println("Query: ");
                    Scanner s3 = new Scanner(System.in);
                    Network.getInstance().sendSearch(s3.nextLine());
                    break;
                default:
                    System.out.println("Invalid option!");
            }
        }

        menu();
    }


}
