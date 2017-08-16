package mk.ukim.finki.wbs.csparqlweb.controller;

import eu.larkc.csparql.core.engine.CsparqlEngine;
import eu.larkc.csparql.core.engine.CsparqlQueryResultProxy;
import mk.ukim.finki.wbs.csparqlweb.model.Query;
import mk.ukim.finki.wbs.csparqlweb.model.User;
import mk.ukim.finki.wbs.csparqlweb.observer.QueryObserver;
import mk.ukim.finki.wbs.csparqlweb.repository.QueryRepository;
import mk.ukim.finki.wbs.csparqlweb.repository.UserRepository;
import mk.ukim.finki.wbs.csparqlweb.stream.BasicStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.text.ParseException;

@Controller
public class UserQueryController {

    private static Logger logger = LoggerFactory.getLogger(UserQueryController.class);

    final CsparqlEngine engine;
    final BasicStream rdfStream;
    final QueryRepository queryRepository;
    final UserRepository userRepository;

    @Autowired
    public UserQueryController(CsparqlEngine engine, BasicStream rdfStream, QueryRepository queryRepository, UserRepository userRepository) {
        this.engine = engine;
        this.rdfStream = rdfStream;
        this.queryRepository = queryRepository;
        this.userRepository = userRepository;
    }

    @RequestMapping(value = "/api/user/{username}", method = RequestMethod.GET)
    public ResponseEntity<User> getUser(@PathVariable("username") String username) {
        User user = userRepository.findByUsername(username);

        if (null == user) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @RequestMapping(value = "/api/token", method = RequestMethod.POST)
    public ResponseEntity<Void> refreshToken(@RequestBody User user, UriComponentsBuilder uriBuilder) {

        if (null == userRepository.findByUsername(user.getUsername())) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        User u = userRepository.findByUsername(user.getUsername());
        u.setToken(user.getToken());

        userRepository.save(u);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/api/user", method = RequestMethod.POST)
    public ResponseEntity<Void> createUser(@RequestBody User user, UriComponentsBuilder uriBuilder) throws IOException {

        if (null != userRepository.findByUsername(user.getUsername())) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        userRepository.save(user);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(uriBuilder.path("/user/{username}").buildAndExpand(user.getUsername()).toUri());

        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/api/user/{username}/query", method = RequestMethod.POST)
    public ResponseEntity<Void> createQuery(@PathVariable String username, @RequestBody Query query, UriComponentsBuilder uriBuilder) {

        User user = userRepository.findByUsername(username);
        if (null == user) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (null != queryRepository.findByDefinitionAndUser(query.getDefinition(), user)) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }


        query.setUser(user);
        queryRepository.save(query);

        CsparqlQueryResultProxy c = null;

        try {
            c = engine.registerQuery(query.getDefinition(), false);
            logger.debug("Query: {}", query.getDefinition());
            logger.debug("Query Start Time: {}", System.currentTimeMillis());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (null != c) {
            c.addObserver(new QueryObserver(user.getToken()));
        }

        return new ResponseEntity<>(HttpStatus.CREATED);

    }

}
