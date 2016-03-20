# SimpleRandomTeleport
A Teleport plugin for Bukkit. Overworld, Nether and The End. [1.8.8] and [1.9]


Features:

- Random Teleport in Overworld, Nether and the End, different algorithms for each.

- Cooldown between teleports, it can be overriden with a permissions.

- Configuration per world:
	- Radius X and Z and center block
	- Dangerous blocks list and Danger Area (Radius) players will not be teleported near those blocks
	- No Cliff Area, players will not be teleported near cliffs (Radius)
	- If a world hasn't config, teleporting in that world will be disabled

- Languages: Spanish and English

- Permissions:
	- randomteleport.tp  Needed to let players teleport
	- randomteleport.nodelay  Override cooldown time
	- randomteleport.reload Use reload command

- Commands:
	- /rtp tp  Teleports player to a random location
	- /rtp help  Shows help menu
	- /rtp reload  Reload Config
