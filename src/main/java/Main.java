import dtn.DTN;
import org.json.simple.parser.ParseException;
import p2p.P2P;
import services.DisconnectedPeers;
import services.MulticastSearch;
import services.PingPeers;
import services.Receiver;

import java.io.File;
import java.io.IOException;

public class Main {

    /**
     * Ponto de partida da aplicação
     *
     * @param args Caminho para o ficheiro de configuração (opcional).
     * @throws IOException
     * @throws ParseException Caminho para o ficheiro incorreto
     */
    public static void main(String[] args) throws IOException, ParseException {
        P2P p2p = P2P.getInstance();

        if (args.length == 1) {
            File file = new File(args[0]);
            DTN.getInstance(file.getName().split("/.")[0]);
            p2p.loadPeersFromConfig(args[0]);
            p2p.loadFilesFromConfig(args[0]);

            //Inicialização dos serviços
            Receiver.getInstance().init();
            MulticastSearch.getInstance().init();
            DisconnectedPeers.getInstance().init();
            PingPeers.getInstance().init();

            Menu.menu();
        } else {
            System.out.println("Ficheiro de configuração não especificado.");
        }


    }


}
