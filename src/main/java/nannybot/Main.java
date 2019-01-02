package nannybot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.devtools.common.options.OptionsParser;
import lombok.Getter;
import lombok.extern.java.Log;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Log
public class Main extends ListenerAdapter {
	@Getter
	private final Config c;
	@Getter
	private final CliOptions cli;
	@Getter
	private final JDA jda;
	@Getter
	private final DB db;
	public static Main m;
	private final ExecutorService pool;
	public static final List<MessageProcessor> parsers = new CopyOnWriteArrayList<MessageProcessor>();
	
	public Main(String[] args) throws Exception {
		cli = getCommandLineOptions(args);
		c = getConfig();
		pool = Executors.newWorkStealingPool();
		jda = new JDABuilder(c.getDiscordSecret()).addEventListener(this).build();
		if(c == null) {
			throw new RuntimeException("No config found.");
		}
		db = new DB();
	}
	
	public static void main(String[] args) throws Exception {
		m = new Main(args);
		m.getJda().awaitReady();
	}
	
	private CliOptions getCommandLineOptions(String[] args) {
		OptionsParser parser = OptionsParser.newOptionsParser(CliOptions.class);
		parser.parseAndExitUponError(args);
		CliOptions options = parser.getOptions(CliOptions.class);
		if(Strings.isNullOrEmpty(options.config) || options.help) {
			System.out.println(parser.describeOptions(Collections.<String, String>emptyMap(),
                    OptionsParser.HelpVerbosity.LONG));
			if(Strings.isNullOrEmpty(options.config))
				throw new RuntimeException("No config file specified!");
		}
		return options;
	}
	
	private Config getConfig() throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		Config _c = mapper.readValue(Files.readString(new File(cli.getConfig()).toPath()), Config.class);
		if(_c.getChannelWhitelist() == null) { 
			_c.setChannelWhitelist(new ArrayList<String>());
		}
		if(_c.getServerWhitelist() == null) {
			_c.setServerWhitelist(new ArrayList<String>());
		}
		for(String chan : new ArrayList<String>(_c.getChannelWhitelist())) {
			if(chan.startsWith("#")) {
				_c.getChannelWhitelist().remove(chan);
				_c.getChannelWhitelist().add(chan.replaceFirst("#", ""));
			}
		}
		return _c;
	}

	@Override
    public void onMessageReceived(final MessageReceivedEvent event)
    {
        if (event.isFromType(ChannelType.TEXT))
        {
        	String gn = event.getGuild().getName();
        	String cn = event.getChannel().getName();
        	User user = event.getAuthor();
        	String message = event.getMessage().getContentDisplay();
        	if(cn.startsWith("#")) {
        		cn = cn.replaceFirst("#", "");
        	}
        	
        	if((c.serverWhitelist.size() == 0 || c.serverWhitelist.contains(gn)) && (c.channelWhitelist.size() == 0 || c.channelWhitelist.contains(cn))) {
        		pool.execute(() -> parseMessage(event));
        	}
        }
    }
	
	private void parseMessage(MessageReceivedEvent event) {
		for(var i : parsers) {
			MessageProcessor.Response r = i.processMessage(event);
			if(r.isMatched() && r.isError()) {
				log.severe("Error parsing message!");
			}
			if(r.isStop()) {
				return;
			}
		}
	}

}
