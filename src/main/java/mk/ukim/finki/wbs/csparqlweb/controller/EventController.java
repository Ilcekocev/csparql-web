package mk.ukim.finki.wbs.csparqlweb.controller;

import mk.ukim.finki.wbs.csparqlweb.model.Event;
import mk.ukim.finki.wbs.csparqlweb.stream.BasicStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class EventController {

    private static Logger logger = LoggerFactory.getLogger(EventController.class);

    final BasicStream stream;

    @Autowired
    public EventController(BasicStream stream) {
        this.stream = stream;
    }

    @RequestMapping(value="/api/event", method= RequestMethod.POST)
    public ResponseEntity<Event> createEvent(@RequestBody Event event) {
        stream.put(event.convertToRdfQuadruple());
        return new ResponseEntity<>(event, HttpStatus.CREATED);
    }

}
