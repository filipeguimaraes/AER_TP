package dtn;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Estrutura que guarda os ficheiro que o nó contem bem como os ficheiros que recebeu.
 */
public class Cache {
    private final ReentrantLock lock = new ReentrantLock();
    private final Map<String, FileNDN> files; //<nomeficheiro,ficheiro>

    public Cache() {
        this.files = new HashMap<>();
    }


    public void addFile(FileNDN file) {
        try {
            lock();
            if (!files.containsKey(file.getName())) {
                files.put(file.getName(), file);
            }
        } finally {
            unlock();
        }
    }

    public void addFiles(List<File> files) throws IOException {
        try {
            lock();
            for (File file : files) {
                FileNDN fileNDN = new FileNDN(file.getName(), Files.readAllBytes(file.toPath()));
                this.files.put(fileNDN.getName(), fileNDN);
            }
        } finally {
            unlock();
        }

    }

    public boolean containsFile(FileNDN file) {
        return files.containsKey(file.getName());
    }

    public Map<String, FileNDN> getFiles() {
        return files;
    }

    public void lock() {
        lock.lock();
    }

    public void unlock() {
        lock.unlock();
    }

}
