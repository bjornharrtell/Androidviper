package org.wololo.viper.resources;

import java.util.Date;

import javax.jdo.Extent;
import javax.jdo.PersistenceManager;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.wololo.viper.PMF;
import org.wololo.viper.HighscoreEntry;

public class Highscore extends ServerResource {
	@Get
    public String represent() {
		
		HighscoreEntry highscoreEntry = new HighscoreEntry("Test", 2,
				new Date());
		
		PersistenceManager pm = PMF.get().getPersistenceManager();

		try {
			pm.makePersistent(highscoreEntry);

			Extent<HighscoreEntry> extent = pm.getExtent(HighscoreEntry.class,
					false);
			int count = 0;
			for (HighscoreEntry entity : extent) {
				entity.getName();
				count++;
			}
			extent.closeAll();

		} finally {
			pm.close();
		}
		
        return "hello, world (from the cloud!)";
    }
}
