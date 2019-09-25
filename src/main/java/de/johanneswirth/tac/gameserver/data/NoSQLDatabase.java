package de.johanneswirth.tac.gameserver.data;

import de.johanneswirth.tac.gameserver.entities.game.Game;
import io.dropwizard.lifecycle.Managed;
import oracle.kv.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


public class NoSQLDatabase implements IDatabase, Managed {

    private static final Logger LOGGER = LoggerFactory.getLogger(NoSQLDatabase.class);

    private KVStoreConfig config;
    private KVStore store;
    private String host;
    private String storeName;

    public NoSQLDatabase(String host, String store) {
        this.host = host;
        this.storeName = store;
    }

    @Override
    public void saveGame(Game game, long id) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(out);
            oos.writeObject(game);
            store.put(Key.createKey("games", id + ""), Value.createValue(out.toByteArray()));
        } catch (Exception e) {
            LOGGER.error("", e);
        }
    }

    @Override
    public Game loadGame(long id) {
        try {
            Value val = store.get(Key.createKey("games", id + "")).getValue();
            ByteArrayInputStream in = new ByteArrayInputStream(val.getValue());
            ObjectInputStream ois = new ObjectInputStream(in);
            return (Game) ois.readObject();
        } catch (Exception e) {
            LOGGER.error("", e);
            return null;
        }
    }

    @Override
    public void deleteGame(long id) {
        store.delete(Key.createKey("games", id + ""));
    }

    @Override
    public void start() throws Exception {
        config = new KVStoreConfig(storeName, host);
        store = KVStoreFactory.getStore(config);
    }

    @Override
    public void stop() throws Exception {
        store.close();
    }
}
