package housemate.src.knowledge.engine;

import java.util.HashMap;
import java.util.Set;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Arrays;

public class KnowledgeGraph {
    private static KnowledgeGraph instance;

    /**
     * Private association for maintaining the active set of Nodes (i.e. Subjects
     * and/or Objects). Map key is the node identifier and value is the associated
     * Node. Node identifiers are case insensitive.
     */
    private Map<String, Node> nodeMap;

    /**
     * Private association for maintaining the active set of Predicates. Map key is
     * the predicate identifier and value is the associated Predicate. Predicate
     * identifiers are case insensitive.
     */
    private Map<String, Predicate> predicateMap;

    /**
     * Private association for maintaining the active set of Triples. Map key is the
     * Triple identifier and value is the associated Triple.
     */
    private Map<String, Triple> tripleMap;

    /**
     * Private association for maintaining a fast query lookup map. Map key is the
     * query string (e.g. “Bill ? ?”), and value is a Set of matching Triples.
     */
    private Map<String, Set<Triple>> queryMapSet;

    private KnowledgeGraph() {
        nodeMap = new HashMap<String, Node>();
        predicateMap = new HashMap<String, Predicate>();
        tripleMap = new HashMap<String, Triple>();
        queryMapSet = new HashMap<String, Set<Triple>>();
    }

    /**
     * Public method for adding a Triple to the KnowledgeGraph. The following
     * associations must be updated: nodeMap, tripleMap, queryMapSet, predicateMap
     * to reflect the added Triple. There should be one Triple instance per unique
     * Subject, Predicate, Object combination, so that Triples are not duplicated.
     * 
     * @param subject
     * @param predicate
     * @param object
     */
    public void importTriple(String subject, String predicate, String object) {
        Triple triple = getTriple(getNode(subject), getPredicate(predicate), getNode(object));
        List<String> queryToIndex = enumerateQuery(subject, predicate, object);
        for (String query : queryToIndex) {
            if (!queryMapSet.containsKey(query)) {
                queryMapSet.put(query, new HashSet<Triple>());
            }
            queryMapSet.get(query).add(triple);
        }
    }

    /**
     * Use the queryMapSet to determine the Triples that match the given Query. If
     * none are found return an empty Set.
     * 
     * @param subject
     * @param predicate
     * @param object
     * @return
     */
    public Set<Triple> executeQuery(String subject, String predicate, String object) {
        String query = subject + " " + predicate + " " + object;
        if (queryMapSet.containsKey(query)) {
            return queryMapSet.get(query);
        } else {
            return null;
        }
    }

    /**
     * This method returns a reference to the single static instance of the
     * KnowledgeGraph.
     * 
     * @return
     */
    public static KnowledgeGraph getInstance() {
        if (instance == null) {
            instance = new KnowledgeGraph();
        }
        return instance;
    }

    /**
     * Return a Node Instance for the given node identifier. Use the nodeMap to look
     * up the Node. If the Node does not exist, create it and add it to the nodeMap.
     * Node names are case insensitive.
     * 
     * @param identifier
     * @return
     */
    public Node getNode(String identifier) {
        if (nodeMap.containsKey(identifier)) {
            return nodeMap.get(identifier);
        } else {
            Node node = new Node(identifier);
            nodeMap.put(identifier, node);
            return node;
        }
    }

    /**
     * Return a Predicate instance for the given identifier. Use the predicateMap to
     * lookup the Predicate. If the Predicate does not exist, create it and add it
     * to the predicateMap. Predicate names are case insensitive.
     * 
     * @param identifier
     * @return
     */
    public Predicate getPredicate(String identifier) {
        if (predicateMap.containsKey(identifier)) {
            return predicateMap.get(identifier);
        } else {
            Predicate predicate = new Predicate(identifier);
            predicateMap.put(identifier, predicate);
            return predicate;
        }
    }

    /**
     * Return the Triple instance for the given Object, Predicate and Subject. Use
     * the tripleMap to lookup the Triple. If the Triple does not exist, create it
     * and add it to the tripleMap and update the queryMapSet.
     * 
     * @param subject
     * @param predicate
     * @param object
     * @return
     */
    public Triple getTriple(Node subject, Predicate predicate, Node object) {
        Triple triple = new Triple(subject, predicate, object);
        String tripleId = triple.getIdentifier();
        if (tripleMap.containsKey(tripleId)) {
            return tripleMap.get(tripleId);
        } else {
            tripleMap.put(tripleId, triple);
            return triple;
        }
    }

    /**
     * Return a list of queries that would return current knowledge upon requesting.
     * 
     * @param subject
     * @param predicate
     * @param object
     * @return
     */
    private List<String> enumerateQuery(String subject, String predicate, String object) {
        List<String> queryToIndex = new ArrayList<String>();
        queryToIndex.add(subject + " " + predicate + " " + object);
        queryToIndex.add(subject + " " + predicate + " ?");
        queryToIndex.add(subject + " ? " + object);
        queryToIndex.add("? " + predicate + " " + object);
        queryToIndex.add(subject + " ? ?");
        queryToIndex.add("? ? " + object);
        queryToIndex.add("? " + predicate + " ?");
        queryToIndex.add("? ? ?");
        return queryToIndex;
    }
}
