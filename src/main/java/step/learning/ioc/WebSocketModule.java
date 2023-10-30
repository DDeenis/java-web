package step.learning.ioc;

import com.google.inject.AbstractModule;
import step.learning.ws.WebSocketConfigurator;

public class WebSocketModule extends AbstractModule {
    @Override
    protected void configure() {
        requestStaticInjection(WebSocketConfigurator.class);
    }
}
