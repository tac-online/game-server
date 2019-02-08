package de.johanneswirth.tac.gameserver.data;

import de.johanneswirth.tac.gameserver.entities.game.Game;
import io.dropwizard.lifecycle.Managed;
import oracle.kv.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;

import static de.johanneswirth.tac.common.Utils.LOGGER;


public class NoSQLDatabase implements IDatabase, Managed {

    private static final String HOST = "localhost:5000";
    private static final String STORE = "kvstore_tac";
    private KVStoreConfig config;
    private KVStore store;
    private String host;
    private String storeName;

    public NoSQLDatabase(String host, String store) {
        this.host = host;
        this.storeName = store;
    }

    @Override
    public void saveGame(Game game, String name) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(out);
            oos.writeObject(game);
            store.put(Key.createKey("games", name), Value.createValue(out.toByteArray()));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "", e);
        }
    }

    @Override
    public Game loadGame(String name) {
        try {
            Value val = store.get(Key.createKey("games", name)).getValue();
            ByteArrayInputStream in = new ByteArrayInputStream(val.getValue());
            ObjectInputStream ois = new ObjectInputStream(in);
            return (Game) ois.readObject();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "", e);
            return null;
        }
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
