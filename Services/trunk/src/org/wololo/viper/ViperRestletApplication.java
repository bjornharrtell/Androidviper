package org.wololo.viper;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;
import org.wololo.viper.resources.Highscore;
 
public class ViperRestletApplication extends Application {
 
    /**
     * Creates a root Restlet that will receive all incoming calls.
     */
    @Override
    public Restlet createInboundRoot() {
        // Create a router Restlet that routes each call to a
        // new instance of HelloWorldResource.
        Router router = new Router(getContext());
 
        // Defines only one route
        router.attachDefault(Highscore.class);
 
        return router;
    }
}