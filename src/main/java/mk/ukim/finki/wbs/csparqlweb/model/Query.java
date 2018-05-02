package mk.ukim.finki.wbs.csparqlweb.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
public class Query {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(length=1024)
    private String definition;

    @JsonIgnore
    @ManyToOne
    private User user;

    protected Query() {
    }

    public Query(String definition) {
        this.definition = definition;
    }

    @Override
    public String toString() {
        return "Query{" +
                "id=" + id +
                ", definition='" + definition + '\'' +
                '}';
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }
}
