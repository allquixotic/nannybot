package nannybot;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.java.Log;

import java.util.List;

@Data @Builder @Log
public class Config {
	@NonNull
	String discordSecret;
	List<String> serverWhitelist;
	List<String> channelWhitelist;
	@NonNull
	String dbDir;
	
	String googleToken;
	String googleSecret;
	String docId;
	String sheetName;
}
