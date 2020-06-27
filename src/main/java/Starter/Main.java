package Starter;

import HttpMapper.Mapper;
import com.google.gson.Gson;
import static Starter.ServerStarter.*;
import static spark.Spark.port;

import java.util.HashMap;
import java.util.Map;
public class Main {

    public static void main(String[] args) {

        setUpServer();

        Mapper mapper = new Mapper();
        Map<Integer, String> map = new HashMap<>();
        mapper.getk("/home/:name", (request, response) -> {
            return map;
        });


        mapper.postk("/home", (request, response) -> {
            Map map1 = new Gson().fromJson(request.body(), HashMap.class);
            map.putAll(map1);
            response.status(200);
            return "Successful";
        });
    }
}

