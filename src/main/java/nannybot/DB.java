package nannybot;

import java.io.File;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.text.similarity.LevenshteinDistance;

import io.jsondb.JsonDBTemplate;
import lombok.extern.java.Log;
import nannybot.model.Boop;

@Log
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

    public void nuke(String handle) {
        List<Boop> ugh = templ.find(String.format("/.", handle), Boop.class);
        List<Boop> toRemove = ugh.parallelStream().filter(beep -> handle.equalsIgnoreCase(beep.getWho())).collect(Collectors.toList());
        toRemove.forEach(beep -> templ.remove(beep, Boop.class));
    }

    public List<Boop> getBoopsByName(String handle) {
        List<Boop> ugh = templ.find(String.format("/.", handle), Boop.class);
        return ugh.parallelStream().filter(beep -> LevenshteinDistance.getDefaultInstance().apply(handle.toLowerCase(), beep.getWho().toLowerCase()) <= 1).collect(Collectors.toList());
    }

    public List<Boop> getBoopsWithinDays(int days) {
        if(days < 1) {
            throw new IllegalArgumentException("Tried to get boops within " + days + " days, which makes no sense!");
        }
        List<Boop> orig = templ.getCollection(Boop.class);
        List<Boop> retval = new ArrayList<>();
        final LocalDate rightnow = new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        //log.log(Level.INFO, "Right now: " + rightnow.toString());
        for(Boop b : orig) {
            long delta = Math.abs(ChronoUnit.DAYS.between(b.getWhen().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), rightnow));
            //log.log(Level.INFO, "For " + b.getWhen().toString() + ", Delta: " + delta);
            if(delta >= 0L && delta <= (long) days) {
                retval.add(b);
            }
        }
        return retval;
    }


}
