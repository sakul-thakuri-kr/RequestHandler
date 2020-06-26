package HttpMapper;

import ServerFactory.CustomServerFactory;
import com.google.gson.Gson;
import spark.embeddedserver.EmbeddedServers;

import static spark.Spark.get;

public class GetMapper {

    public GetMapper () {}

    public GetMapper (int port) {
        EmbeddedServers.add(EmbeddedServers.Identifiers.JETTY, new CustomServerFactory(port));
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
}
