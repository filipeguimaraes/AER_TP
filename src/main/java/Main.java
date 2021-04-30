import network.Network;
import services.*;
import org.json.simple.parser.ParseException;

import java.io.IOException;

public class Main {

    /**
     * Ponto de partida da aplicação
     * @param args Caminho para o ficheiro de configuração (opcional).
     * @throws IOException
     * @throws ParseException Caminho para o ficheiro incorreto
     */
    public static void main(String[] args) throws IOException, ParseException {
        Network p2p = Network.getInstance();

        if(args.length==1){
            p2p.loadPeersFromConfig(args[0]);
            p2p.loadFilesFromConfig(args[0]);
        }

        //Inicialização dos serviços
        Receiver.getInstance().init();
        MulticastSearch.getInstance().init();
        DisconnectedPeers.getInstance().init();
        PingPeers.getInstance().init();

        Menu.menu();

    }


}
