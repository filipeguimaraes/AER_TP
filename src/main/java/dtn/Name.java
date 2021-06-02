package dtn;

import java.util.List;

public class Name {
    private List<String> path;
    private String name;

    public Name(List<String> path, String name) {
        this.path = path;
        this.name = name;
    }


    public List<String> getPath() {
        return path;
    }

    public void setPath(List<String> path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
