package mk.ukim.finki.wbs.csparqlweb.controller;

import eu.larkc.csparql.core.engine.CsparqlEngine;
import eu.larkc.csparql.core.engine.CsparqlQueryResultProxy;
import mk.ukim.finki.wbs.csparqlweb.model.Event;
import mk.ukim.finki.wbs.csparqlweb.model.Query;
import mk.ukim.finki.wbs.csparqlweb.model.User;
import mk.ukim.finki.wbs.csparqlweb.observer.KafkaQueryObserver;
import mk.ukim.finki.wbs.csparqlweb.stream.BasicStream;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.common.protocol.types.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.util.UriComponentsBuilder;

import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Controller
public class KafkaUserQueryController {

    private static Logger logger = LoggerFactory.getLogger(KafkaUserQueryController.class);

    final CsparqlEngine engine;
    final BasicStream rdfStream;

    @Autowired
    public KafkaUserQueryController(CsparqlEngine e, BasicStream s) {
        engine = e;
        rdfStream = s;
    }


    @RequestMapping(value = "/api/kafka/registerQuery/{topic}", method = RequestMethod.POST)
    public ResponseEntity<Void> registerQuery(@PathVariable String topic ,@RequestBody Query query, UriComponentsBuilder uriBuilder) {

        CsparqlQueryResultProxy c = null;

        //Create the topic
        Properties config = new Properties();
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        AdminClient admin = AdminClient.create(config);

        Map<String, String> configs = new HashMap<>();
        int partitions = 1;
        short replication = 1;

        admin.createTopics(Arrays.asList(new NewTopic(topic, partitions, replication).configs(configs)));

        try {
            c = engine.registerQuery(query.getDefinition(), false);
            logger.debug("Query: {}", query.getDefinition());
            logger.debug("Query Start Time: {}", System.currentTimeMillis());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (null != c) {
           // queryObserver.setUser(user);
           Properties props = new Properties();
            props.put("bootstrap.servers", "localhost:9092");
            props.put("acks", "all");
            props.put("retries", 0);
            props.put("batch.size", 16384);
            props.put("linger.ms", 1);
            props.put("buffer.memory", 33554432);
            props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            Producer<String, String> producer = new KafkaProducer<>(props);
            c.addObserver(new KafkaQueryObserver(topic,producer));
        }

        return new ResponseEntity<>(HttpStatus.CREATED);

    }



    //Test method !!
    //The server starts on port 8090 !!
    @RequestMapping(value="/api/kafka/test", method= RequestMethod.GET)
    public ResponseEntity<Event> createEvent() {

        String topic = "resultTopic";

        String query = "REGISTER QUERY test1 AS "
                + "PREFIX ex: <http://example.org/comment/> "
                + "SELECT ?s ?o "
                + "FROM STREAM <http://myexample.org/stream> [RANGE 5s STEP 4s] "
                + "WHERE { ?s ex:browserUsed ?o }";

        CsparqlQueryResultProxy c = null;

        //Create the topic
        Properties config = new Properties();
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        AdminClient admin = AdminClient.create(config);

        Map<String, String> configs = new HashMap<>();
        int partitions = 1;
        short replication = 1;

        admin.createTopics(Arrays.asList(new NewTopic(topic, partitions, replication).configs(configs)));

        try {
            c = engine.registerQuery(query, false);
            logger.debug("Query: {}", query);
            logger.debug("Query Start Time: {}", System.currentTimeMillis());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (null != c) {
            // queryObserver.setUser(user);
            Properties props = new Properties();
            props.put("bootstrap.servers", "localhost:9092");
            props.put("acks", "all");
            props.put("retries", 0);
            props.put("batch.size", 16384);
            props.put("linger.ms", 1);
            props.put("buffer.memory", 33554432);
            props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            Producer<String, String> producer = new KafkaProducer<>(props);
            c.addObserver(new KafkaQueryObserver(topic,producer));
        }


        return new ResponseEntity<>(HttpStatus.CREATED);
    }

}
