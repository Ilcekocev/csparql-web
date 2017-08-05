package mk.ukim.finki.wbs.csparqlweb.stream;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.impl.PropertyImpl;
import com.hp.hpl.jena.rdf.model.impl.ResourceImpl;
import eu.larkc.csparql.cep.api.RdfQuadruple;
import eu.larkc.csparql.cep.api.RdfStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringWriter;

public class BasicStream extends RdfStream {

    /**
     * The logger.
     */
    protected final Logger logger = LoggerFactory
            .getLogger(BasicStream.class);

    public BasicStream(final String iri) {
        super(iri);
    }

    public synchronized void addEvent(String subject, String predicate, String object) {
        logger.info("Adding event to Csparql Engine");
        RdfQuadruple q = new RdfQuadruple(subject, predicate, object, System.currentTimeMillis());
        this.put(q);
    }

}