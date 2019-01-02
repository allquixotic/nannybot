package nannybot;

import io.jsondb.JsonDBTemplate;
import nannybot.model.Boop;

import java.io.File;
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


}
