package org.wololo.viper.resources;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class DefaultResource extends AuthServerResource {
	@Get
	public String represent() {
		
		return "Hello " + user.getNickname() + ", welcome to Viper Services.";
	}
}
