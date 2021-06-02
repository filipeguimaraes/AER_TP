package dtn;

public class FileNDN {
    private String name;
    private byte[] dados;

    public FileNDN(String name, byte[] dados) {
        this.name = name;
        this.dados = dados;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getDados() {
        return dados;
    }

    public void setDados(byte[] dados) {
        this.dados = dados;
    }
}
