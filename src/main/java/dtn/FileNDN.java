package dtn;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileNDN implements Serializable {
    private String name;
    private byte[] dados;

    public FileNDN(String name, byte[] dados) {
        this.name = name;
        this.dados = dados;
    }

    public String getName() {
        return name;
    }

    public void savefile() throws IOException {
        Path relativePath = Paths.get("");
        Path path = Paths.get(relativePath.toAbsolutePath().toString()+"/"+name+".txt");
        Files.write(path, this.dados);
        System.out.println("File saved in "+path);
    }
}
