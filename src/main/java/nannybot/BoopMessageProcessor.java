package nannybot;

import com.google.common.base.Strings;
import lombok.extern.java.Log;
import nannybot.model.Boop;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.Date;
import java.util.logging.Level;
import java.util.regex.Pattern;

@Log
public class BoopMessageProcessor extends MessageProcessor {

	private static final Pattern bmrx = Pattern.compile("^\\s*!boop\\s+(@)?(\\S+)\\s+(.*?)\\s*$", Pattern.CASE_INSENSITIVE);

	public BoopMessageProcessor() {
		super();
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
				Boop beep = Boop.builder().when(new Date()).who(handle).detail(detail).build();
				Main.m.getDb().save(beep);
				mre.getTextChannel().sendMessage("Stored boop: " + beep.toString());
			}
			catch(Exception e) {
				log.log(Level.SEVERE, "An exception was thrown", e);
			}

			return retval.build();
		}
		else {
			return Response.builder().error(false).matched(false).stop(false).build();
		}
	}
	
}
