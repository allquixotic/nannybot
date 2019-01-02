package nannybot;

import lombok.*;
import lombok.extern.java.Log;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log
public abstract class MessageProcessor {

	@Getter
	protected Pattern rx; 
	
	protected MessageProcessor() {
		Main.parsers.add(this);
	}
	
	protected final Matcher matches(MessageReceivedEvent mre) {
		var t = rx.matcher(mre.getMessage().getContentDisplay());
		return (t.matches() ? t : null);
	}
	
	public abstract Response processMessage(MessageReceivedEvent mre);
	
	@Data @Value @Builder
	static class Response {
		boolean matched;
		boolean error;
		boolean stop;
		MessageReceivedEvent mre;
	}
}
