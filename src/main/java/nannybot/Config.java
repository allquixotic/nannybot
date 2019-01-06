package nannybot;

import lombok.*;
import lombok.extern.java.Log;

import java.util.List;

@Data @Builder @Log @NoArgsConstructor @AllArgsConstructor
public class Config {
	@NonNull
	String discordSecret;
	List<String> serverWhitelist;
	List<String> channelWhitelist;
	@NonNull
	String dbDir;

	String googleApplicationName;
	String googleTokensDirectoryPath;
	String googleCredentialsFilePath;
	String sheetId;
	String sheetRange;
	String boophelp;
}
