package org.wololo.viper.resources;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.jdo.JDOException;
import javax.jdo.PersistenceManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.InputRepresentation;
import org.restlet.representation.OutputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.wololo.viper.PMF;
import org.wololo.viper.pojos.Highscore;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.Transform;

public class HighscoreResource extends ServerResource {

	private class PictureRepresentation extends OutputRepresentation {

		Blob blob;

		PictureRepresentation(Blob blob) {
			super(MediaType.IMAGE_PNG);
			this.blob = blob;
		}

		@Override
		public void write(OutputStream arg0) throws IOException {
			byte[] bytes = blob.getBytes();

			arg0.write(bytes);
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

	@Get("png")
	public Representation getPng() {
		Highscore highscore = getHighscore(key);

		Blob blob = highscore.getPicture();

		if (blob == null) {
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
		}

		return new PictureRepresentation(blob);
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
	public JsonRepresentation update(JsonRepresentation jsonRepresentation)
			throws Throwable {
		PersistenceManager pm = PMF.get().getPersistenceManager();

		try {
			pm.currentTransaction().begin();

			Highscore highscore = pm.getObjectById(Highscore.class, key);

			// TODO: do the updating

			JSONObject response = new JSONObject();
			response.append("success", true);

			pm.currentTransaction().commit();

			return new JsonRepresentation(response);
		} catch (Throwable e) {
			pm.currentTransaction().rollback();
			throw e;
		} finally {
			pm.close();
		}
	}

	@Post("png")
	public JsonRepresentation postPicture(
			InputRepresentation inputRepresentation) throws Throwable {
		PersistenceManager pm = PMF.get().getPersistenceManager();

		try {
			pm.currentTransaction().begin();

			Highscore highscore = pm.getObjectById(Highscore.class, key);

			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			inputRepresentation.write(byteArrayOutputStream);

			ImagesService imagesService = ImagesServiceFactory
					.getImagesService();

			Image image = ImagesServiceFactory.makeImage(byteArrayOutputStream
					.toByteArray());
			Transform resize = ImagesServiceFactory.makeResize(200, 200);

			Image newImage = imagesService.applyTransform(resize, image);

			byte[] newImageData = newImage.getImageData();

			Blob blob = new Blob(newImageData);

			highscore.setPicture(blob);

			JSONObject response = new JSONObject();
			response.append("success", true);

			pm.currentTransaction().commit();

			return new JsonRepresentation(response);
		} catch (Throwable e) {
			pm.currentTransaction().rollback();
			throw e;
		} finally {
			pm.close();
		}
	}
}
