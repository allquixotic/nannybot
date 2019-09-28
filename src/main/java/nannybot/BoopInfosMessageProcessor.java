package nannybot;

import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Singleton;

import lombok.extern.java.Log;
import nannybot.model.Boop;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

@Log @Singleton
public class BoopInfosMessageProcessor extends MessageProcessor {

	private static final Pattern bmrx = Pattern.compile("^\\s*!boopinfos\\s+(.+?)\\s*$", Pattern.CASE_INSENSITIVE);
	private static final Pattern hrx = Pattern.compile("^\\s*@?(\\S+)\\s*$", Pattern.CASE_INSENSITIVE);

	public BoopInfosMessageProcessor() {
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
				String[] handles = matcher.group(1).split("\\s+");
				if(handles.length > 15) {
					messageAwooPing(mre, "Please limit your !boopinfos request to no more than 15 handles at a time.");
				}
				else {
					StringBuilder sb = new StringBuilder();
					for(String handle : handles) {
						Matcher m = hrx.matcher(handle);
						if(!m.matches()) continue;
						String rhandle = m.group(1);
						List<Boop> beeps = Main.m.getDb().getBoopsByName(rhandle);
						if(beeps != null) for(Boop b : beeps) {
							sb.append(b.toString()).append(",\n");
						}
					}
					messageAwooPingPaged(mre, "Requested boops: \n", sb.toString(), 5);
				}
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
