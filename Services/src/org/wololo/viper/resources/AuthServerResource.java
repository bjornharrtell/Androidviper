package org.wololo.viper.resources;

import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ServerResource;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class AuthServerResource extends ServerResource {

	User user;
	UserService userService = UserServiceFactory.getUserService();
	
	@Override
	public Representation doHandle(Variant variant) {
		
        user = userService.getCurrentUser();
		
		if (user != null) {
        	return super.doHandle(variant);
        }
        else
        {
        	JsonRepresentation jsonRepresentation = new JsonRepresentation(createAuthError());
        	return jsonRepresentation;
        }
	}
	
	JSONObject createAuthError() {
		JSONObject jsonObject = new JSONObject();
    	try {
			jsonObject.put("success", false);
			Reference reference = this.getRequest().getResourceRef();
			jsonObject.put("authurl", userService.createLoginURL(reference.getHostIdentifier() + reference.getPath()));
			setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
		} catch (JSONException e) {
		}
    	return jsonObject;
	}
	
}
