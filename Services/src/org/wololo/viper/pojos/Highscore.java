package org.wololo.viper.pojos;

import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.appengine.api.datastore.Blob;
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
    
    @Persistent
    private Blob picture;

    public Highscore(String name, int score, Date date, Blob picture) {
        this.name = name;
        this.score = score;
        this.date = date;
        this.picture = picture;
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
	
	public Blob getPicture() {
		return picture;
	}
	
	public JSONObject toJSONObject() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		
		jsonObject.put("id", key.getId());
		jsonObject.put("name", name);
		jsonObject.put("score", score);
		jsonObject.put("date", date.getTime());
		
		return jsonObject;
	}
}