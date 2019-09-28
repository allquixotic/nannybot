package nannybot;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;

import lombok.extern.java.Log;
import nannybot.model.Boop;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import javax.inject.Singleton;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Pattern;

@Log @Singleton
public class BoopsMessageProcessor extends MessageProcessor {

	private static final Pattern bmrx = Pattern.compile("^\\s*!boops\\s*(\\d+)?\\s*$", Pattern.CASE_INSENSITIVE);

	public BoopsMessageProcessor() {
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
				String sNumDays = matcher.group(1);
				int numDays = Strings.isNullOrEmpty(sNumDays) ? 7 : Integer.parseInt(sNumDays);
				List<Boop> boops = Main.m.getDb().getBoopsWithinDays(numDays);
				StringBuilder sb = new StringBuilder();
				for(Boop b : boops) {
					sb.append(b.toString()).append(",\n");
				}
				String k = "Boops within the past " + numDays + " days: \n";
				messageAwooPingPaged(mre, k, sb.toString(), 5);
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
