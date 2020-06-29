package HttpMapper;

import ServerFactory.CustomServerFactory;
import com.google.gson.Gson;
import spark.embeddedserver.EmbeddedServers;

import static spark.Spark.*;

public class Mapper {

    public Mapper () {
        EmbeddedServers.add(EmbeddedServers.Identifiers.JETTY, new CustomServerFactory());
    }

    public Mapper (int portNo) {
        port(portNo);
        EmbeddedServers.add(EmbeddedServers.Identifiers.JETTY, new CustomServerFactory());
    }

    public static void getk(String path, Funct funct) {
        Gson gson = new Gson();
        get(path, (request, response) -> {
            Object obj = funct.execute(request, response);
            String json = gson.toJson(obj);
            response.type("application/json");
            return json;
        });
    }

    public static void postk (String path, Funct funct) {
        Gson gson = new Gson();
        post(path, (request, response) -> {
            Object obj = funct.execute(request, response);
            String json = gson.toJson(obj);
            response.type("application/json");
            return json;
        });
    }
}
