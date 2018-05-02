package mk.ukim.finki.wbs.csparqlweb;

import eu.larkc.csparql.core.engine.ConsoleFormatter;
import eu.larkc.csparql.core.engine.CsparqlEngine;
import eu.larkc.csparql.core.engine.CsparqlQueryResultProxy;
import mk.ukim.finki.wbs.csparqlweb.model.Event;
import mk.ukim.finki.wbs.csparqlweb.model.EventSim;
import mk.ukim.finki.wbs.csparqlweb.observer.QueryObserver;
import mk.ukim.finki.wbs.csparqlweb.repository.QueryRepository;
import mk.ukim.finki.wbs.csparqlweb.repository.UserRepository;
import mk.ukim.finki.wbs.csparqlweb.stream.BasicStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;



public class Simulation implements CommandLineRunner {


    final CsparqlEngine engine;
    final BasicStream rdfStream;
  //  private QueryObserver queryObserver;

    @Autowired
    public Simulation(CsparqlEngine engine, BasicStream rdfStream) {
        this.engine = engine;
        this.rdfStream = rdfStream;
       // this.queryObse
    }

    @Override
    public void run(String... strings) throws Exception {

        CsparqlQueryResultProxy c = null;


        String query = "REGISTER QUERY test1 AS "
                + "PREFIX ex: <http://example.org/post/> "
                + "SELECT ?s ?o "
                + "FROM STREAM <http://myexample.org/stream> [RANGE 5s STEP 4s] "
                + "WHERE { ?s ex:browserUsed ?o }";

        try {
            c = engine.registerQuery(query, false);
           // logger.debug("Query: {}", query.getDefinition());
           // logger.debug("Query Start Time: {}", System.currentTimeMillis());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (null != c) {
            c.addObserver(new ConsoleFormatter());
        }

        //stream.put(event.convertToRdfQuadruple());


        String fileloc = "/home/ilce/hadoop-2.6.0/test_data/data.csv";
        List<EventSim> list = new ArrayList<>();
        FileReader fileReader = new FileReader(fileloc);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            String[] quad = line.split(Pattern.quote("|"));
            EventSim event = new EventSim(quad[0],quad[1],quad[2],Long.parseLong(quad[3]));
            list.add(event);
        }

        list.sort(Comparator.comparing(EventSim::getTimestamp));

        long timestamp = list.get(0).getTimestamp();
        for (EventSim e : list) {
           // System.out.println(e);
            rdfStream.put(e.convertToRdfQuadruple());
            long currTime = e.getTimestamp();
            double wait = (double) currTime - timestamp;
            if (wait > 500) {
                wait = Math.ceil(wait/86400);
            }
            timestamp = currTime;
            Thread.sleep((long)wait);
        }


    }
}
