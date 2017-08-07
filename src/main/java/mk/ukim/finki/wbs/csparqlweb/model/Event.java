package mk.ukim.finki.wbs.csparqlweb.model;

import eu.larkc.csparql.cep.api.RdfQuadruple;

import javax.persistence.Entity;

public class Event {

    private String subject;
    private String predicate;
    private String object;

    protected Event() {}

    public Event(String subject, String predicate, String object) {
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
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
}
