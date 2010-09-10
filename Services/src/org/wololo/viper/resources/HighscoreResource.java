package org.wololo.viper.resources;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.jdo.JDOException;
import javax.jdo.PersistenceManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.MediaType;
import org.restlet.data.Preference;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.InputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
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

public class HighscoreResource extends AuthServerResource {

	long key;
	String ext; 

	public HighscoreResource() {
	}

	public void doInit() {
		String key = (String)this.getRequest().getAttributes().get("key");
		
		if (key.matches("((-|\\+)?[0-9]+(\\.[0-9]+)?)+")) {
			this.key = Long.parseLong(key);
		} else {
			String[] split = key.split("\\.");
			
			this.key = Long.parseLong(split[0]);
			this.ext = split[1];
			
			if (this.ext.equals("png")) {
				List<Preference<MediaType>> temp = new ArrayList<Preference<MediaType>>();
				temp.add(new Preference<MediaType>(MediaType.IMAGE_PNG));
				this.getRequest().getClientInfo().setAcceptedMediaTypes(temp);
			}
		}
	}

	@Get("json")
	public JsonRepresentation getJson() throws Exception {
		PersistenceManager pm = PMF.get().getPersistenceManager();

		try {
			Highscore highscore = pm.getObjectById(Highscore.class, key);
			JSONObject response = highscore.toJSONObject();
			response.put("success", true);
			return new JsonRepresentation(response);
		} finally {
			pm.close();
		}
	}

	@Get
	public Representation getDefault() {
		return getPng();
	}
	
	@Get("png")
	public BlobRepresentation getPng() {
		PersistenceManager pm = PMF.get().getPersistenceManager();

		try {
			Highscore highscore = pm.getObjectById(Highscore.class, key);
			Blob blob = highscore.getPicture();

			if (blob == null) {
				throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
			}

			return new BlobRepresentation(blob);

		} finally {
			pm.close();
		}
	}

	@Post("json")
	public JsonRepresentation update(JsonRepresentation jsonRepresentation)
			throws Exception {
		PersistenceManager pm = PMF.get().getPersistenceManager();

		try {
			pm.currentTransaction().begin();

			Highscore highscore = pm.getObjectById(Highscore.class, key);

			// TODO: do the updating
			pm.currentTransaction().commit();

			JSONObject response = new JSONObject();
			response.append("success", true);
			return new JsonRepresentation(response);
		} catch (Exception e) {
			pm.currentTransaction().rollback();
			throw e;
		} finally {
			pm.close();
		}
	}

	@Post("png")
	public JsonRepresentation postPicture(
			InputRepresentation inputRepresentation) throws Exception {

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

			pm.currentTransaction().commit();

			JSONObject response = new JSONObject();
			response.append("success", true);
			return new JsonRepresentation(response);
		} catch (Exception e) {
			pm.currentTransaction().rollback();
			throw e;
		} finally {
			pm.close();
		}
	}
}
