package de.johanneswirth.tac.gameserver;

import de.johanneswirth.tac.common.AuthenticationRequired;
import de.johanneswirth.tac.common.ServiceUtils;
import de.johanneswirth.tac.gameserver.data.NoSQLDatabase;
import de.johanneswirth.tac.gameserver.services.GameStartService;
import de.johanneswirth.tac.gameserver.services.InterfaceVersionService;
import de.johanneswirth.tac.gameserver.services.GameService;
import de.johanneswirth.tac.gameserver.services.SimulateService;
import io.dropwizard.Application;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.discovery.DiscoveryBundle;
import io.dropwizard.discovery.DiscoveryFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import javax.ws.rs.client.Client;

public class GameServerApp extends Application<GameServerConfiguration> {

    private final DiscoveryBundle<GameServerConfiguration> discoveryBundle = new DiscoveryBundle<GameServerConfiguration>() {
        @Override
        public DiscoveryFactory getDiscoveryFactory(GameServerConfiguration configuration) {
            return configuration.getDiscoveryFactory();
        }

    };

    public static void main(String[] args) throws Exception {
        new GameServerApp().run(args);
    }

    public void initialize(Bootstrap<GameServerConfiguration> bootstrap) {
        bootstrap.addBundle(discoveryBundle);
    }

    @Override
    public void run(GameServerConfiguration configuration, Environment environment) {
        NoSQLDatabase database = new NoSQLDatabase(configuration.getNosqlHost(), configuration.getNosqlStore());
        environment.lifecycle().manage(database);
        environment.jersey().register(InterfaceVersionService.class);
        environment.jersey().register(new GameService(database));
        environment.jersey().register(new GameStartService(database));
        environment.jersey().register(new SimulateService(database));
        final Client client = new JerseyClientBuilder(environment).using(configuration.getJerseyClientConfiguration())
                .build(getName());
        ServiceUtils.init(discoveryBundle, client, environment);
        environment.jersey().register(new AuthenticationRequired(configuration.getPublicKey()));
    }
}
