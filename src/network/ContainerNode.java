package network;
import java.util.List;
public interface ContainerNode extends NetworkNode {
    List<NetworkNode> getChildren();
}