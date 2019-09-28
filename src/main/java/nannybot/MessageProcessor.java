package nannybot;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Value;
import lombok.extern.java.Log;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

@Log
public abstract class MessageProcessor {

	@Getter
	protected Pattern rx;
	
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

	public final void messageAwooPing(MessageReceivedEvent mre, String message) {
		mre.getTextChannel().sendMessage(new MessageBuilder().append(mre.getAuthor()).append(" ").append(message).build()).queue();
	}

	public final void messageAwooPingPaged(MessageReceivedEvent mre, String startMessage, String message, int maxMessages) {
		boolean smine = Strings.isNullOrEmpty(startMessage), mine = Strings.isNullOrEmpty(message);
		int smlen = smine ? 0 : startMessage.length(), mlen = mine ? 0 : message.length();
		int maxMessageLength = 1900;
		Iterable<String> result = Splitter.fixedLength(maxMessageLength).split(mine ? "" : message);
		String[] parts = Iterables.toArray(result, String.class);
		int i = 1;
		if(smlen + mlen <= maxMessageLength) {
			messageAwooPing(mre, startMessage + message);
		}
		else {
			messageAwooPing(mre, startMessage);
			for(String vring : parts) {
				if(i >= maxMessages) {
					messageAwooPing(mre, "OK, that's enough. I'm not going to spam more messages than that!");
					break;
				}
				messageAwooPing(mre, vring);
				i++;
				try {Thread.sleep(((i+1) * 500));}catch(InterruptedException ie){}
			}
		}
	}
}
