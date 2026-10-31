package visitor;

import network.ContainerNode;
import network.NetworkNode;

import java.util.function.Consumer;

public final class TreeTraversal {

    private TreeTraversal() {
    }

    public static void walk(
            NetworkNode root,
            Consumer<NetworkNode> action
    ) {
        action.accept(root);

        if (root instanceof ContainerNode container) {
            for (NetworkNode child : container.getChildren()) {
                walk(child, action);
            }
        }
    }
}