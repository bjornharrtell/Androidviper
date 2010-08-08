package org.wololo.viper;

import com.google.appengine.api.datastore.Key;

import java.util.Date;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class HighscoreEntry {
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;

    @Persistent
    private String name;

    @Persistent
    private int score;
    
    @Persistent
    private Date date;

    public HighscoreEntry(String name, int score, Date date) {
        this.name = name;
        this.score = score;
        this.date = date;
    }

	public String getName() {
		return name;
	}
}