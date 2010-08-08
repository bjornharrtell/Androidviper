package org.wololo.viper.pojos;

import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class Highscore {
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;

    @Persistent
    private String name;

    @Persistent
    private int score;
    
    @Persistent
    private Date date;

    public Highscore(String name, int score, Date date) {
        this.name = name;
        this.score = score;
        this.date = date;
    }

	public String getName() {
		return name;
	}
	
	public JSONObject toJSONObject() throws JSONException {
		JSONObject json = new JSONObject();
		json.put("name", name);
		json.put("score", score);
		json.put("date", date);
		
		return json;
	}
	
	public JsonRepresentation toJsonRepresentation() throws JSONException {
		return new JsonRepresentation(toJSONObject());
	}
}