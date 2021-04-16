import java.io.Serializable;
import java.net.DatagramPacket;
import java.util.List;

public class Message implements Serializable {

    //Integer id;
    int type;
    String message;
    List<MessagePlugin> pluginList;


    public Message(Integer type, String message, List<MessagePlugin> pluginList) {
        this.type = type;
        this.message = message;
        this.pluginList = pluginList;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<MessagePlugin> getPluginList() {
        return pluginList;
    }

    public void setPluginList(List<MessagePlugin> pluginList) {
        this.pluginList = pluginList;
    }
}
