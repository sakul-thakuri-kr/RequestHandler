package HttpMapper;

import com.google.gson.Gson;

import static spark.Spark.get;
import static spark.Spark.post;

public class Mapper {

    public Mapper () {}

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
