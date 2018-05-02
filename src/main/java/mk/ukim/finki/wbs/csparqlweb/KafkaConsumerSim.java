package mk.ukim.finki.wbs.csparqlweb;

import eu.larkc.csparql.core.engine.ConsoleFormatter;
import eu.larkc.csparql.core.engine.CsparqlEngine;
import eu.larkc.csparql.core.engine.CsparqlQueryResultProxy;
import mk.ukim.finki.wbs.csparqlweb.model.EventSim;
import mk.ukim.finki.wbs.csparqlweb.stream.BasicStream;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Properties;
import java.util.regex.Pattern;


@Component
public class KafkaConsumerSim implements CommandLineRunner {



    final CsparqlEngine engine;
    final BasicStream rdfStream;

    @Autowired
    public KafkaConsumerSim(CsparqlEngine e, BasicStream stream) {
        engine = e;
        rdfStream = stream;
    }

    @Override
    public void run(String... strings) throws Exception {

        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("group.id", "test");
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList("test"));


        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(100);
            for (ConsumerRecord<String, String> record : records) {
                //System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
                String[] quad = record.value().split(Pattern.quote("|"));
                EventSim event = new EventSim(quad[0],quad[1],quad[2],Long.parseLong(quad[3]));
               // System.out.println(event);
                rdfStream.put(event.convertToRdfQuadruple());
            }

        }
    }
}
