package nannybot;

import com.google.common.base.Strings;
import lombok.extern.java.Log;
import nannybot.model.Boop;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import javax.inject.Singleton;
import java.util.Date;
import java.util.logging.Level;
import java.util.regex.Pattern;

@Log @Singleton
public class BoopMessageProcessor extends MessageProcessor {

	private static final Pattern bmrx = Pattern.compile("^\\s*!boop\\s+(@)?(\\S+)\\s+(.*?)\\s*$", Pattern.CASE_INSENSITIVE);

	public BoopMessageProcessor() {
		rx = bmrx;
	}

	@Override
	public Response processMessage(final MessageReceivedEvent mre) {
		var matcher = matches(mre);
		if(matcher != null) {
			var retval = Response.builder();
			retval.matched(true);
			retval.stop(true);
			retval.error(false);

			try {
				boolean hasAt = Strings.isNullOrEmpty(matcher.group(1));
				String handle = matcher.group(2);
				String detail = matcher.group(3);
				Boop beep = Boop.builder().when(new Date()).who(handle).detail(detail).by(mre.getAuthor().getName()).build();
				log.info("Saving " + beep.toString());
				Main.m.getDb().save(beep);
				mre.getTextChannel().sendMessage("Stored boop: " + beep.toString()).queue();
				log.info("Stored boop: " + beep.toString());
			}
			catch(Exception e) {
				log.log(Level.SEVERE, "An exception was thrown", e);
				retval.error(true);
			}

			return retval.build();
		}
		else {
			return Response.builder().error(false).matched(false).stop(false).build();
		}
	}
	
}
