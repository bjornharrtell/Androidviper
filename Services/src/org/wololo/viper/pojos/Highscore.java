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
	
	public int getScore() {
		return score;
	}
	
	public Date getDate() {
		return date;
	}
}