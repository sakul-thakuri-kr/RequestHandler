package ServerFactory;

import org.eclipse.jetty.util.thread.ThreadPool;
import spark.ExceptionMapper;
import spark.embeddedserver.EmbeddedServer;
import spark.embeddedserver.EmbeddedServerFactory;
import spark.embeddedserver.jetty.JettyHandler;
import spark.http.matching.MatcherFilter;
import spark.route.Routes;
import spark.staticfiles.StaticFilesConfiguration;

public class CustomServerFactory implements EmbeddedServerFactory {
    private ThreadPool threadPool;
    private boolean httpOnly = true;

    public CustomServerFactory() {
    }

    @Override
    public EmbeddedServer create(Routes routes, StaticFilesConfiguration staticFilesConfiguration, ExceptionMapper exceptionMapper, boolean b) {
        MatcherFilter matcherFilter = new MatcherFilter(routes, staticFilesConfiguration, exceptionMapper, false, b);
        matcherFilter.init(null);

        JettyHandler handler = new JettyHandler(matcherFilter);
        handler.getSessionCookieConfig().setHttpOnly(this.httpOnly);
        return (new CustomEmbeddedServer(handler)).withThreadPool(this.threadPool);
    }

    public CustomServerFactory withThreadPool(ThreadPool threadPool) {
        this.threadPool = threadPool;
        return this;
    }

    public CustomServerFactory withHttpOnly(boolean httpOnly) {
        this.httpOnly = httpOnly;
        return this;
    }
}
