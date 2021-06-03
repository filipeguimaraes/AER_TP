package dtn;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

/**
 * Modulação do ficheiro que circula pela rede NDN. Caso seja um interest só contem o nome.
 */
public class FileNDN implements Serializable {
    private final String name;
    private final byte[] dados;

    public FileNDN(String name, byte[] dados) {
        this.name = name;
        this.dados = dados;
    }

    public String getName() {
        return name;
    }

    /**
     * No ultimo salto guarda o ficheiro em disco.
     *
     * @throws IOException Caso não consiga passar o byte[] para ficheiro.
     */
    public void saveFile() throws IOException {
        Path relativePath = Paths.get("");
        Path path = Paths.get(relativePath.toAbsolutePath() + "/" + LocalDateTime.now() + "-" + name);
        Files.write(path, this.dados);
        System.out.println("File saved in " + path);
    }
}
