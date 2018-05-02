package mk.ukim.finki.wbs.csparqlweb.observer;

import eu.larkc.csparql.common.RDFTable;
import eu.larkc.csparql.common.RDFTuple;
import eu.larkc.csparql.core.ResultFormatter;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Iterator;
import java.util.Observable;
import java.util.Properties;

public class KafkaQueryObserver  extends ResultFormatter {

    private String topic;
    private Properties props;
    private Producer<String, String> producer;

    public KafkaQueryObserver(String topic,Producer<String, String> producer) {
        this.topic = topic;
        this.producer = producer;
    }

    @Override
    public void update(Observable o, Object arg) {

        RDFTable q = (RDFTable)arg;
        System.out.println();
        System.out.println("-------" + q.size() + " results at SystemTime=[" + System.currentTimeMillis() + "]--------");
        Iterator var4 = q.iterator();

        int i = 1;
        while(var4.hasNext()) {
            RDFTuple t = (RDFTuple)var4.next();
            producer.send(new ProducerRecord<String, String>(topic, Integer.toString(i), t.toString()));
            //System.out.println(t.toString());
            i++;
        }


    }
}
