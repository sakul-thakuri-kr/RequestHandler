package Starter;

import HttpMapper.GetMapper;
import ServerFactory.CustomServerFactory;
import spark.embeddedserver.EmbeddedServers;

import java.util.HashMap;
import java.util.Map;
public class Main {

    public static void main(String[] args) {

      Map<Integer, String> map = new HashMap<>();

      GetMapper getMapper = new GetMapper(8080);
      getMapper.getk("/home/:name", (request, response) -> {
          map.put(1, request.params(":name"));
          return map;
      });
    }
}

