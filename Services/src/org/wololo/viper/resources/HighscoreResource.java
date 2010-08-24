package org.wololo.viper.resources;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import javax.jdo.JDOException;
import javax.jdo.PersistenceManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.MediaType;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.OutputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import org.wololo.viper.PMF;
import org.wololo.viper.pojos.Highscore;

import com.google.appengine.api.datastore.Blob;

public class HighscoreResource extends ServerResource {

	private class PictureRepresentation extends OutputRepresentation {

		Blob blob;

		PictureRepresentation(Blob blob) {
			super(MediaType.IMAGE_PNG);
			this.blob = blob;
		}

		@Override
		public void write(OutputStream arg0) throws IOException {
			arg0.write(blob.getBytes());
		}
	}

	long key;

	public HighscoreResource() {
	}

	public void doInit() {
		key = Long.valueOf((String) this.getRequest().getAttributes()
				.get("key"));
	}

	@Get("json")
	public JsonRepresentation getJson() throws JSONException {
		JSONObject response = new JSONObject();
		response.put("success", false);

		try {
			Highscore highscore = getHighscore(key);
			response = highscore.toJSONObject();
			response.put("success", true);
		} catch (JDOException e) {
			response.put("exception", e.getMessage());
		}

		return new JsonRepresentation(response);
	}
	
	@Get("txt")
	public String getString() throws JSONException {
		return "moo";
	}

	@Get("png")
	public Representation getPng() {
		Highscore highscore = getHighscore(key);

		return new PictureRepresentation(highscore.getPicture());
	}

	private Highscore getHighscore(long key) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			Highscore highscore = pm.getObjectById(Highscore.class, key);
			return highscore;
		} finally {
			pm.close();
		}
	}

	@Post("json")
	public JsonRepresentation create() throws JSONException {
		Highscore highscore = new Highscore(
				"Test" + System.currentTimeMillis(), 2, new Date(), null);

		PersistenceManager pm = PMF.get().getPersistenceManager();
		pm.makePersistent(highscore);

		JSONObject response = new JSONObject();
		response.append("success", true);

		return new JsonRepresentation(response);
	}
}
