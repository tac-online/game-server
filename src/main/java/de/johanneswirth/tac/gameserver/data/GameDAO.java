package de.johanneswirth.tac.gameserver.data;

import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;
import java.util.Optional;

public interface GameDAO {

    @SqlQuery("select id from games where player_id = :player_id")
    List<Long> getGames(@Bind("player_id") long playerID);

    @SqlUpdate("insert into games (player_id) values (:player_id)")
    @GetGeneratedKeys("id")
    long createNewGame(@Bind("player_id") long player_id);

    @SqlQuery("select player_id from games where id = :game_id and player_id = :player_id")
    Optional<Long> checkAccess(@Bind("player_id") long player_id, @Bind("game_id") long game_id);

    default boolean hasAccessOnGame(long player_id, long game_id) {
        return checkAccess(player_id, game_id).isPresent();
    }

    @SqlUpdate("delete from games where id = :game_id and player_id = :player_id")
    boolean deleteGame(@Bind("player_id") long player_id, @Bind("game_id") long game_id);
}
