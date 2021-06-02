import p2p.P2P;

import java.io.IOException;
import java.util.Scanner;

public class Menu {

    /**
     * Imprime o menu e recebe o input do utilizador.
     *
     * @throws IOException Exceção a chamar os respetivos métodos.
     */
    public static void menu() throws IOException {
        System.out.println("(Type m to access the menu)");
        Scanner s = new Scanner(System.in);
        if (!s.nextLine().equals("m")) {
            Menu.menu();
        } else {
            clearScreen();
            System.out.println("########################################");
            System.out.println("#                                      #");
            System.out.println("#   1: Show connected peers.           #");
            System.out.println("#   2: Show disconnected peers.        #");
            System.out.println("#   3: Search a file in the network.   #");
            System.out.println("#   4: List files on cache.            #");
            System.out.println("#                                      #");
            System.out.println("########################################");

            System.out.println("Option:");

            Scanner sc = new Scanner(System.in);
            switch (sc.nextLine()) {
                case "1":
                    clearScreen();
                    P2P.getInstance().printConnectedPeers();
                    break;
                case "2":
                    clearScreen();
                    P2P.getInstance().printDisconnectedPeers();
                    break;
                case "3":
                    clearScreen();
                    System.out.println("Query: ");
                    Scanner s3 = new Scanner(System.in);
                    P2P.getInstance().sendSearch(s3.nextLine());
                    break;
                case "4":
                    clearScreen();
                    P2P.getInstance().printFilesKnown();
                    System.out.println("Path: ");
                    Scanner s4 = new Scanner(System.in);
                    P2P.getInstance().sendRequestFile(s4.nextLine());
                    break;
                default:
                    clearScreen();
                    System.out.println("Invalid option!");
                    break;
            }
        }
        menu();
    }

    /**
     * Limpa o terminal.
     */
    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }


}
