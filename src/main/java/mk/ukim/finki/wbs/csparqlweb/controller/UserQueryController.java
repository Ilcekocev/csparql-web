package mk.ukim.finki.wbs.csparqlweb.controller;

import eu.larkc.csparql.core.engine.ConsoleFormatter;
import eu.larkc.csparql.core.engine.CsparqlEngine;
import eu.larkc.csparql.core.engine.CsparqlQueryResultProxy;
import mk.ukim.finki.wbs.csparqlweb.observer.QueryObserver;
import mk.ukim.finki.wbs.csparqlweb.stream.BasicStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.ParseException;

@Controller
public class UserQueryController {

    private static Logger logger = LoggerFactory.getLogger(UserQueryController.class);


    @Autowired
    CsparqlEngine engine;

    @Autowired
    BasicStream rdfStream;

    @RequestMapping(path="/query", method=RequestMethod.GET)
    @ResponseBody
    public String test() {
        String query = "REGISTER QUERY Testing AS "
                + "PREFIX ex: <http://myexample.org/> "
                + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> "
                + "SELECT ?s ?p ?o "
                + "FROM STREAM <http://myexample.org/stream> [RANGE 5s STEP 1s] "
                + "WHERE {" +
                " ?s ?p ?o . " +
                "}";

        CsparqlQueryResultProxy c1 = null;
        try {
            c1 = engine.registerQuery(query, false);
            logger.debug("Query: {}", query);
            logger.debug("Query Start Time : {}", System.currentTimeMillis());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (null != c1) {
            c1.addObserver(new QueryObserver());
        }

        rdfStream.addEvent("Test", "pred", "obj");

        return "Hi!";
    }

}
