package nannybot;

import io.jsondb.JsonDBTemplate;
import nannybot.model.Boop;

import java.io.File;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DB {
    private final JsonDBTemplate templ;

    public DB() {
        String dbdir = Main.m.getC().dbDir;
        File f = new File(dbdir);
        if(f.exists() && !f.isDirectory()) {
            throw new IllegalArgumentException("dbDir must be a directory, not a file!");
        }
        if(!f.exists() && !f.mkdirs()) {
                throw new IllegalArgumentException("Can't create directory " + dbdir);
        }
        templ = new JsonDBTemplate(dbdir, "nannybot.model");
        if(!templ.collectionExists(Boop.class)) {
            templ.createCollection(Boop.class);
        }
    }

    public void save(Boop b) {
        templ.insert(b);
    }

    public List<Boop> getBoopsByName(String handle) {
        return templ.find(String.format("/.[who='%s']", handle), Boop.class);
    }

    public List<Boop> getBoopsWithinDays(int days) {
        if(days < 1) {
            throw new IllegalArgumentException("Tried to get boops within " + days + " days, which makes no sense!");
        }
        List<Boop> orig = templ.getCollection(Boop.class);
        List<Boop> retval = new ArrayList<>();
        for(Boop b : orig) {
            long delta = Math.abs(ChronoUnit.DAYS.between(b.getWhen().toInstant(), new Date().toInstant()));
            if(delta >= 0L && delta <= 7L) {
                retval.add(b);
            }
        }
        return retval;
    }


}
