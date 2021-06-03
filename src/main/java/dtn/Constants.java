package dtn;

public class Constants {
    //Endereço e porta a serem utilizados
    public static final String MULTICAST_ADDRESS = "FF15::1";
    public static final int MULTICAST_PORT = 9998;

    //Parametros fixos de uma mensagem
    public static final int MAX_SIZE = 8192; //bytes
    public static final int TTL = 3; //saltos

    //Tipos de mensagem
    public static final int INTEREST = 1;
    public static final int POST = 2;
    public static final int CONFIRM = 3;
}
