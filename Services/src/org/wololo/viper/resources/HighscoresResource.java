package org.wololo.viper.resources;

import java.util.Date;

import javax.jdo.Extent;
import javax.jdo.PersistenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import org.wololo.viper.PMF;
import org.wololo.viper.pojos.Highscore;

public class HighscoresResource extends ServerResource {

	@Get
	public JsonRepresentation represent() throws JSONException {
		PersistenceManager pm = PMF.get().getPersistenceManager();

		// JSONObject response = new JSONObject();
		JSONArray highscores = new JSONArray();
		// response.append("highscores", highscores);

		try {
			Extent<Highscore> extent = pm.getExtent(Highscore.class, false);
			int count = 0;
			for (Highscore entity : extent) {
				highscores.put(entity.toJSONObject());
				count++;
			}
			extent.closeAll();

		} finally {
			pm.close();
		}

		return new JsonRepresentation(highscores);
	}

	@Post
	public JsonRepresentation create() throws JSONException {
		Highscore highscore = new Highscore("Test", 2, new Date());

		PersistenceManager pm = PMF.get().getPersistenceManager();
		pm.makePersistent(highscore);

		JSONObject response = new JSONObject();
		response.append("success", true);

		return new JsonRepresentation(response);
	}
}
