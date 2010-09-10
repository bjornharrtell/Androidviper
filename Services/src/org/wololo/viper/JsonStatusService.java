package org.wololo.viper;

import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.service.StatusService;

/**
 * StatusService extended to represent in JSON instead of the default HTML
 */
public class JsonStatusService extends StatusService {

	public Representation getRepresentation(Status status, Request request, Response response) {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("success", false);
			jsonObject.put("exception", status.getThrowable().toString() + ": " + status.getThrowable().getMessage());
		} catch (JSONException e) {
			super.getRepresentation(status, request, response);
		}

		return new JsonRepresentation(jsonObject);
	}
}
