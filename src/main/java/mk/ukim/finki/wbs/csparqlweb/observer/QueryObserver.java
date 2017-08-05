package mk.ukim.finki.wbs.csparqlweb.observer;

import eu.larkc.csparql.common.RDFTable;
import eu.larkc.csparql.common.RDFTuple;
import eu.larkc.csparql.core.ResultFormatter;

import java.util.Iterator;
import java.util.Observable;

public class QueryObserver extends ResultFormatter {
    public QueryObserver() {
    }

    public void update(Observable o, Object arg) {
        RDFTable q = (RDFTable)arg;
        System.out.println();
        System.out.println("-------" + q.size() + " results at SystemTime=[" + System.currentTimeMillis() + "]--------");
        Iterator var4 = q.iterator();

        while(var4.hasNext()) {
            RDFTuple t = (RDFTuple)var4.next();
            System.out.println(t.toString());
        }

        System.out.println("Push notification to user here.");
        System.out.println();
    }
}