package mk.ukim.finki.wbs.csparqlweb.model;

import eu.larkc.csparql.cep.api.RdfQuadruple;

public class EventSim {

    private String subject;
    private String predicate;
    private String object;
    private long timestamp;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    protected EventSim() {}

    public EventSim(String subject, String predicate, String object, long timestamp) {
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
        this.timestamp = timestamp;
    }


    public RdfQuadruple convertToRdfQuadruple() {
        return new RdfQuadruple(this.subject, this.predicate, this.object, System.currentTimeMillis());
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getPredicate() {
        return predicate;
    }

    public void setPredicate(String predicate) {
        this.predicate = predicate;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    @Override
    public String toString() {
        return String.format("%s %s %s %d",subject,predicate,object,timestamp);
    }
}
