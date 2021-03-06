package mk.ukim.finki.wbs.csparqlweb;

import com.fasterxml.jackson.databind.BeanProperty;
import eu.larkc.csparql.cep.api.RdfStream;
import eu.larkc.csparql.core.engine.CsparqlEngine;
import eu.larkc.csparql.core.engine.CsparqlEngineImpl;
import eu.larkc.csparql.core.engine.CsparqlQueryResultProxy;
import mk.ukim.finki.wbs.csparqlweb.observer.QueryObserver;
import mk.ukim.finki.wbs.csparqlweb.stream.BasicStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.annotation.PostConstruct;

@SpringBootApplication
@EnableWebMvc
public class Application {

    protected final Logger logger = LoggerFactory
            .getLogger(Application.class);

    @Autowired
    CsparqlEngine engine;

    @Autowired
    BasicStream stream;

    @Bean
    public CsparqlEngine getCsparqlEngine() {
        return new CsparqlEngineImpl();
    }

    @Bean
    public BasicStream getStream() {
        return new BasicStream("http://myexample.org/stream");
    }

    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    public QueryObserver getQueryObserver() {
        return new QueryObserver();
    }

    @PostConstruct
    public void initializeCsparqlEngine() {
        logger.info("Initializing Csparql Engine");
        engine.initialize(true);
        engine.registerStream(stream);
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
