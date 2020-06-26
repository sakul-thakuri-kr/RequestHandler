package ServerFactory;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.embeddedserver.EmbeddedServer;
import spark.embeddedserver.jetty.SocketConnectorFactory;
import spark.embeddedserver.jetty.websocket.WebSocketHandlerWrapper;
import spark.embeddedserver.jetty.websocket.WebSocketServletContextHandlerFactory;
import spark.ssl.SslStores;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CustomEmbeddedServer implements EmbeddedServer {
    private static int port = 8080;
    private static final String NAME = "Spark";
    private final Handler handler;
    private Server server;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private Map<String, WebSocketHandlerWrapper> webSocketHandlers;
    private Optional<Integer> webSocketIdleTimeoutMillis;
    private ThreadPool threadPool = null;

    public CustomEmbeddedServer(Handler handler, int port) {
        this.handler = handler;
        this.port = port;
        System.out.println(port);
    }

    @Override
    public void configureWebSockets(Map<String, WebSocketHandlerWrapper> webSocketHandlers,
                                    Optional<Integer> webSocketIdleTimeoutMillis) {

        this.webSocketHandlers = webSocketHandlers;
        this.webSocketIdleTimeoutMillis = webSocketIdleTimeoutMillis;
    }

    @Override
    public int ignite(String host,
                       int port,
                      SslStores sslStores,
                      int maxThreads,
                      int minThreads,
                      int threadIdleTimeoutMillis) throws Exception {

        boolean hasCustomizedConnectors = false;

        if (port == 0) {
            try (ServerSocket s = new ServerSocket(0)) {
                port = s.getLocalPort();
            } catch (IOException e) {
                logger.error("Could not get first available port (port set to 0), using default: {}", CustomEmbeddedServer.port);
                port = CustomEmbeddedServer.port;
            }
        }

        // Create instance of jetty server with either default or supplied queued thread pool
        if (this.threadPool == null) {
            if (maxThreads > 0) {
                int min = minThreads > 0 ? minThreads : 200;
                int idleTimeout = threadIdleTimeoutMillis > 0 ? threadIdleTimeoutMillis : '\uea60';
                server = new Server(new QueuedThreadPool(maxThreads, min, idleTimeout));
            } else {
                server = new Server();
            }
        } else {
            this.server = new Server(threadPool);
        }

        ServerConnector connector;

        if (sslStores == null) {
            connector = SocketConnectorFactory.createSocketConnector(server, host, port);
        } else {
            connector = SocketConnectorFactory.createSecureSocketConnector(server, host, port, sslStores);
        }

        Connector previousConnectors[] = server.getConnectors();
        server = connector.getServer();
        if (previousConnectors.length != 0) {
            server.setConnectors(previousConnectors);
            hasCustomizedConnectors = true;
        } else {
            server.setConnectors(new Connector[]{connector});
        }

        ServletContextHandler webSocketServletContextHandler =
                WebSocketServletContextHandlerFactory.create(webSocketHandlers, webSocketIdleTimeoutMillis);

        // Handle web socket routes
        if (webSocketServletContextHandler == null) {
            server.setHandler(handler);
        } else {
            List<Handler> handlersInList = new ArrayList<>();
            handlersInList.add(handler);

            // WebSocket handler must be the last one
            if (webSocketServletContextHandler != null) {
                handlersInList.add(webSocketServletContextHandler);
            }

            HandlerList handlers = new HandlerList();
            handlers.setHandlers(handlersInList.toArray(new Handler[handlersInList.size()]));
            server.setHandler(handlers);
        }

        logger.info("== {} has ignited ...", NAME);
        if (hasCustomizedConnectors) {
            logger.info(">> Listening on Custom Server ports!");
        } else {
            logger.info(">> Listening on {}:{}", host, port);
        }
        System.out.println(":::::::::::_____-:::::::");
        server.start();
        System.out.println("::::::::::::::::::");
        return port;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void join() throws InterruptedException {
        server.join();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void extinguish() {
        logger.info(">>> {} shutting down ...", NAME);
        try {
            if (server != null) {
                server.stop();
            }
        } catch (Exception e) {
            logger.error("stop failed", e);
            System.exit(100); // NOSONAR
        }
        logger.info("done");
    }

    @Override
    public int activeThreadCount() {
        if (server == null) {
            return 0;
        }
        return server.getThreadPool().getThreads() - server.getThreadPool().getIdleThreads();
    }

    /**
     * Sets optional thread pool for jetty server.  This is useful for overriding the default thread pool
     * behaviour for example io.dropwizard.metrics.jetty9.InstrumentedQueuedThreadPool.
     *
     * @param threadPool thread pool
     * @return Builder pattern - returns this instance
     */
    public CustomEmbeddedServer withThreadPool(ThreadPool threadPool) {
        this.threadPool = threadPool;
        return this;
    }
}
