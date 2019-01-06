package nannybot;

import com.google.common.base.Strings;
import lombok.extern.java.Log;
import nannybot.model.Boop;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import javax.inject.Singleton;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Pattern;

@Log @Singleton
public class BoopInfoMessageProcessor extends MessageProcessor {

	private static final Pattern bmrx = Pattern.compile("^\\s*!boopinfo\\s+(@)?(\\S+)\\s*$", Pattern.CASE_INSENSITIVE);

	public BoopInfoMessageProcessor() {
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
				String handle = matcher.group(2);
				List<Boop> boops = Main.m.getDb().getBoopsByName(handle);
				StringBuilder sb = new StringBuilder();
				for(Boop b : boops) {
					sb.append(b.toString()).append(",\n");
				}
				messageAwooPing(mre, "Boops for @" + handle + ": \n" + sb.toString());
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
