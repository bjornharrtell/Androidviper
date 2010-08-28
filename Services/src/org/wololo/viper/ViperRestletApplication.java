package org.wololo.viper;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;
import org.wololo.viper.resources.DefaultResource;
import org.wololo.viper.resources.HighscoreResource;
import org.wololo.viper.resources.HighscoresResource;

public class ViperRestletApplication extends Application {

	public ViperRestletApplication() {
		setStatusService(new JsonStatusService());
	}
	
	@Override
	public Restlet createInboundRoot() {
		Router router = new Router(getContext());

		router.attachDefault(DefaultResource.class);

		router.attach("/highscores", HighscoresResource.class);
		router.attach("/highscores/{key}", HighscoreResource.class);
		return router;
	}
}