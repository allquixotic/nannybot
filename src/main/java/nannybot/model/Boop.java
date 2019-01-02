package nannybot.model;


import io.jsondb.annotation.Document;
import io.jsondb.annotation.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@Document(collection = "boops", schemaVersion= "1.0")
public class Boop implements Serializable {
    @Id
    private Date when;
    private String who;
    private String detail;
    private String by;

    @Override
    public String toString() {
        return String.format("On %s, %s booped @%s; details: %s", when, by, who, detail);
    }
}
