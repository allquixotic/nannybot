# NannyBot

### A Discord Bot for managing an RP Guild

NannyBot is (will be) a collection of several different functionalities for managing an RP guild in Discord.

The plan is to integrate several of my other prior projects into NannyBot so that the Discord API only has one connection with my server, instead of many.

The first function of NannyBot is to keep track of a certain type of player status checks called "boops".

Documentation will be added over time. For now, to use, you must create a config.json file. Example:

```json
{
  "discordSecret": "your discord secret key",
  "dbDir": "~/nannybotData",
  "serverWhitelist": ["The name of your Discord server"],
  "channelWhitelist": ["some channel name"]
}
```

The whitelist parameters are *optional*. Omitting them means your bot will respond to commands on every server/channel it is invited to.

The Discord Secret must come from the Discord developer dashboard, where you must register a new bot.

The dbDir must be a readable and writable directory; it can be an absolute or relative path. Do *not* pass the name of a *file* in dbDir.