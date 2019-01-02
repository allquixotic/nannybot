package nannybot;

import com.google.devtools.common.options.Option;
import com.google.devtools.common.options.OptionsBase;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;

@Log
public class CliOptions extends OptionsBase {
	@Option(name = "help", abbrev = 'h', help = "Prints usage info.", defaultValue = "false")
	@Getter @Setter
	public boolean help;
	
	@Option(name = "config", abbrev = 'c', help = "Path to config file.", defaultValue="./config.json")
	@Getter @Setter
	public String config;
}
