#!/bin/sh
/tac-game-server/bin/tac-game-server db migrate config.yml
/tac-game-server/bin/tac-game-server db status config.yml
/tac-game-server/bin/tac-game-server server config.yml

