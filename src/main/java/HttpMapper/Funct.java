package HttpMapper;

import spark.Request;
import spark.Response;

public interface Funct {
    public Object execute(Request request, Response response);
}
