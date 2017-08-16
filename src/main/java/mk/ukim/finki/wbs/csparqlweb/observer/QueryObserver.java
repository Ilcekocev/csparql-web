package mk.ukim.finki.wbs.csparqlweb.observer;

import com.google.gson.JsonObject;
import eu.larkc.csparql.common.RDFTable;
import eu.larkc.csparql.common.RDFTuple;
import eu.larkc.csparql.core.ResultFormatter;
import mk.ukim.finki.wbs.csparqlweb.model.User;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.io.IOException;
import java.util.Iterator;
import java.util.Observable;

public class QueryObserver extends ResultFormatter {

    private String token;

    public QueryObserver(String token) {
        this.token = token;
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

        okhttp3.MediaType JSON = okhttp3.MediaType.parse("application/json");
        OkHttpClient client = new OkHttpClient();

        JsonObject data = new JsonObject();
        JsonObject message = new JsonObject();
        message.addProperty("title", "New notification");
        message.addProperty("body", "Hello World!");
        data.addProperty("to", token);
        data.add("notification", message);
        System.out.println("Push notification to user " + token + " here.");
        okhttp3.RequestBody body = okhttp3.RequestBody.create(JSON, data.toString());
        Request request = new Request.Builder()
                .header("Authorization", "key=AAAAnyHg1qM:APA91bGl2qE6RHE9Hjcp8AvLOxfp-kzHZjEjGjIPdBQBn-dEIPNrq8GvCuZ0p9LvzxbNo0uCBIG9dwUDhpvc1cIVXLdmUwzztOrWKbWuVcEeNrTH2G7IalgayBAdvZWPOwtukL8--fFX")
                .url("https://fcm.googleapis.com/fcm/send")
                .post(body)
                .build();
        try {
            okhttp3.Response response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println();
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}