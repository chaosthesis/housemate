package housemate.src.knowledge.engine;

import java.time.Instant;

public class Node {
    /**
     * Private unique non mutable identifier for the Node. Node identifiers are case
     * insensitive.
     */
    private String identifier;

    /**
     * Unix time stamp when node was created.
     */
    private long createDate;

    /**
     * @return the Node identifier.
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * @return the Node creation date.
     */
    public long getCreateDate() {
        return createDate;
    }

    public Node(String identifier) {
        this.identifier = identifier;
        this.createDate = Instant.now().getEpochSecond();
    }
}