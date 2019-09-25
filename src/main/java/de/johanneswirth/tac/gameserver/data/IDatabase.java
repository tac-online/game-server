package de.johanneswirth.tac.gameserver.data;

import de.johanneswirth.tac.gameserver.entities.game.Game;

public interface IDatabase {

    void saveGame(Game game, long id);

    Game loadGame(long id);

    void deleteGame(long id);
}

