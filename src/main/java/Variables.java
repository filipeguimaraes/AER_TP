public class Variables {
    //Endereços e Portas
    public static final String MULTICAST_ADDRESS = "FF15::1";
    public static final int MULTICAST_PORT = 9999;

    //Tempos
    public static final int HELLO_TIME = 5000;
    public static final int DEAD_TIME = 7000;

    //TIPOS DE MENSAGENS
    public static final int HELLO = 1;
    public static final int QUERY = 2;
    public static final int PING = 3;
    public static final int PEERS = 4;
    public static final int QUERY_RESPONSE = 5;
    public static final int REQUEST = 6;

    //Estado do peer
    public static final int ON = 1;
    public static final int OFF =0;
}
