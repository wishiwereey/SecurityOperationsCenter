package network;
import visitor.Visitor;
public interface NetworkNode {
    String getName();
    <R> R accept(Visitor<R> visitor);
}