import java.io.IOException;
import java.util.Scanner;

public class Menu {

    public static void menu() throws IOException {
        System.out.println("####################################");
        System.out.println("# 1: Show connected peers.         #");
        System.out.println("# 2: Search a file in the network. #");
        System.out.println("####################################");

        System.out.println("Opção:");

        Scanner sc = new Scanner(System.in);
        switch (sc.nextInt()) {
            case 1:
                //Network.getInstance().sendSimpleMessage(Variables.QUERY,"test");
                break;
        }

        Scanner s = new Scanner(System.in);
        if (s.nextLine().equals("m")) {
            Menu.menu();
        } else {
            System.out.println("(Type m to access the menu)");
        }
    }


}
