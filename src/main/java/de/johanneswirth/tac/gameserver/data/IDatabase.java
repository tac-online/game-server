package de.johanneswirth.tac.gameserver.data;

import de.johanneswirth.tac.gameserver.entities.game.Game;

public interface IDatabase {

    void saveGame(Game game, String name);

    Game loadGame(String name);
}
