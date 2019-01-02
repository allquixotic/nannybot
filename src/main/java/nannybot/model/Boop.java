package nannybot.model;


import io.jsondb.annotation.Document;
import io.jsondb.annotation.Id;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data @Builder @NoArgsConstructor
@Document(collection = "boops", schemaVersion= "1.0")
public class Boop implements Serializable {
    @Id
    private Date when;
    private String who;
    private String detail;
}
