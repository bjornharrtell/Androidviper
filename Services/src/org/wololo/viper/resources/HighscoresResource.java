package org.wololo.viper.resources;

import java.util.Date;
import java.util.List;

import javax.jdo.JDOException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

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

	@Get("json")
	public JsonRepresentation represent() throws JSONException {
		PersistenceManager pm = PMF.get().getPersistenceManager();

		JSONObject response = new JSONObject();

		try {
			Query query = pm.newQuery(Highscore.class);
			query.setOrdering("score desc");
			JSONArray highscoresResult = new JSONArray();
			List<Highscore> highscores = (List<Highscore>) query.execute();

			int count = 0;
			for (Highscore highscore : highscores) {
				if (count > 10) {
					pm.deletePersistent(highscore);
				} else {
					highscoresResult.put(highscore.toJSONObject());
				}
				count++;
			}
			query.closeAll();

			response.put("success", true);
			response.put("highscores", highscoresResult);

			return new JsonRepresentation(response);

		} catch (JDOException e) {
			response.put("success", false);
			return new JsonRepresentation(response);
		} finally {
			pm.close();
		}
	}

	@Post("json")
	public JsonRepresentation create(JsonRepresentation jsonRepresentation)
			throws JSONException {
		JSONObject jsonObject = jsonRepresentation.getJsonObject();

		Highscore highscore = new Highscore(jsonObject.getString("name"),
				jsonObject.getInt("score"), new Date(), null);

		PersistenceManager pm = PMF.get().getPersistenceManager();
		pm.makePersistent(highscore);

		JSONObject response = new JSONObject();
		response.put("success", true);

		return new JsonRepresentation(response);
	}
}
