package visitor;

import network.*;
import java.util.stream.Collectors;

public final class JsonExportVisitor implements Visitor<String> {

    public String export(NetworkNode root) {
        return root.accept(this);
    }

    private String escape(String text) {
        StringBuilder result = new StringBuilder("\"");

        for (char character : text.toCharArray()) {
            switch (character) {
                case '"' -> result.append("\\\"");
                case '\\' -> result.append("\\\\");
                case '\n' -> result.append("\\n");
                case '\r' -> result.append("\\r");
                case '\t' -> result.append("\\t");
                default -> {
                    if (character < 32) {
                        result.append(
                                String.format("\\u%04x", (int) character)
                        );
                    } else {
                        result.append(character);
                    }
                }
            }
        }

        return result.append('"').toString();
    }

    private String leaf(String type, String name) {
        return "{\"type\":" + escape(type)
                + ",\"name\":" + escape(name) + "}";
    }

    private String container(
            String type,
            ContainerNode node
    ) {
        String children = node.getChildren()
                .stream()
                .map(child -> child.accept(this))
                .collect(Collectors.joining(","));

        return "{\"type\":" + escape(type)
                + ",\"name\":" + escape(node.getName())
                + ",\"children\":[" + children + "]}";
    }

    @Override
    public String visit(Network network) {
        return container("Network", network);
    }

    @Override
    public String visit(Segment segment) {
        return container("Segment", segment);
    }

    @Override
    public String visit(Server server) {
        return leaf("Server", server.getName());
    }

    @Override
    public String visit(Workstation workstation) {
        return leaf("Workstation", workstation.getName());
    }

    @Override
    public String visit(Sensor sensor) {
        return leaf("Sensor", sensor.getName());
    }

    @Override
    public String visit(LogRecord logRecord) {
        return leaf("LogRecord", logRecord.getName());
    }

    @Override
    public String visit(Firewall firewall) {
        return leaf("Firewall", firewall.getName());
    }
}