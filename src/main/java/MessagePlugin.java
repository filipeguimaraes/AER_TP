import java.io.Serializable;

public class MessagePlugin implements Serializable {
    Integer type;
    String options;

    public MessagePlugin(Integer type, String options) {
        this.type = type;
        this.options = options;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }
}
