package mk.ukim.finki.wbs.csparqlweb.repository;

import mk.ukim.finki.wbs.csparqlweb.model.Query;
import mk.ukim.finki.wbs.csparqlweb.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QueryRepository extends JpaRepository<Query, Long> {
    Query findByDefinitionAndUser(String definition, User user);
}
