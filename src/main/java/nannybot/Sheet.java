package nannybot;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import nannybot.model.Boop;

import javax.inject.Singleton;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Data
@Builder
@Singleton @AllArgsConstructor
public class Sheet {
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
    private final String spreadsheetId = Main.m.getC().getSheetId();
    private final String range = Main.m.getC().getSheetRange();

    private Sheets _service = null;

    private Sheets getService() {
        if(_service == null) {
            try {
                final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
                _service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                        .setApplicationName(Main.m.getC().getGoogleApplicationName())
                        .build();
            }
            catch(Exception e) {
                throw new RuntimeException(e);
            }
        }
        return _service;
    }

    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        return GoogleCredential.fromStream(new FileInputStream(Main.m.getC().getGoogleCredentialsFilePath()))
                .createScoped(Lists.newArrayList(SCOPES));
    }

    public Sheet() {

    }

    public void addBoop(Boop b) throws IOException {
        ValueRange appendBody = new ValueRange()
                .setValues(Arrays.asList(
                        Arrays.asList(Main.fmt.format(b.getWhen()), b.getBy(), "@" + b.getWho(), b.getDetail())));
        AppendValuesResponse appendResult = getService().spreadsheets().values()
                .append(spreadsheetId, range, appendBody)
                .setValueInputOption("USER_ENTERED")
                .setInsertDataOption("INSERT_ROWS")
                .setIncludeValuesInResponse(false)
                .execute();
    }
}
