package Starter;

import ServerFactory.CustomServerFactory;
import spark.embeddedserver.EmbeddedServers;
public class ServerStarter {

    public static void setUpServer () {
        EmbeddedServers.add(EmbeddedServers.Identifiers.JETTY, new CustomServerFactory());
    }
}
