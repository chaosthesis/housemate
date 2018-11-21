package housemate.src.knowledge.engine;

import java.time.Instant;

public class Triple {
    /**
     * Private unique non mutable identifier for the Triple. Of the form:
     * subject.identifier + “ “ + predicate.identifer + “ “ + object.identifier.
     */
    private String identifier;

    /**
     * Unix time stamp when triple was created
     */
    private long createDate;

    /**
     * Private non mutable association to the associated Subject instance.
     */
    private Node subject;

    /**
     * Private non mutable association to the associated Predicate instance.
     */
    private Predicate predicate;

    /**
     * Private non mutable association to the associated Object instance.
     */
    private Node object;

    public Triple(Node subject, Predicate predicate, Node object) {
        identifier = subject.getIdentifier() + " " + predicate.getIdentifier() + " " + object.getIdentifier() + ".";
        createDate = Instant.now().getEpochSecond();
        
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
    }

    public Triple(String query) {
        identifier = query;
        createDate = Instant.now().getEpochSecond();
        
        String[] triples = query.split("[\\s* .\\s*]");
        subject = new Node(triples[0]);
        predicate = new Predicate(triples[1]);
        object = new Node(triples[2]);
    }

    /**
     * @return the Triple identifier.
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * @return the Triple creation date.
     */
    public long getCreateDate() {
        return createDate;
    }

    /**
     * @return the Subject of the Triple.
     */
    public Node getSubject() {
        return subject;
    }

    /**
     * @return the Object of the Triple.
     */
    public Predicate getPredicate() {
        return predicate;
    }

    /**
     * @return the Subject of the Triple.
     */
    public Node getObject() {
        return object;
    }
}