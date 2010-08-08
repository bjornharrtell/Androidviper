package org.wololo.viper.resources;

import java.util.Date;

import javax.jdo.PersistenceManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import org.wololo.viper.PMF;
import org.wololo.viper.pojos.Highscore;

public class HighscoreResource extends ServerResource {
	
	@Get
	public void dummy() throws JSONException {
		create();
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
