package housemate.src.knowledge.engine;

import java.time.Instant;

public class Predicate {
    /**
     * Private unique non mutable identifier for the Predicate. Predicate
     * identifiers are case insensitive.
     */
    private String identifier;

    /**
     * Unix time stamp when predicate was created.
     */
    private long createDate;

    /**
     * @return the Predicate identifier.
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * @return the Predicate creation date.
     */
    public long getCreateDate() {
        return createDate;
    }

    public Predicate(String identifier) {
        this.identifier = identifier;
        this.createDate = Instant.now().getEpochSecond();
    }
}