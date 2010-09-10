package org.wololo.viper.resources;

import org.restlet.representation.Representation;
import org.restlet.resource.ServerResource;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class AuthRedirectServerResource extends ServerResource {

	User user;
	
	@Override
	public Representation handle() {
		UserService userService = UserServiceFactory.getUserService();
        user = userService.getCurrentUser();
		
		if (user != null) {
        	return super.handle();
        }
        else
        {
        	String url = userService.createLoginURL(this.getRequest().getClientInfo().getAddress());
        	this.getResponse().redirectPermanent(url);
        	return null;
        }
	}
}
