package de.johanneswirth.tac.gameserver;

import de.johanneswirth.tac.common.ContainerFilter;
import de.johanneswirth.tac.common.ServiceUtils;
import de.johanneswirth.tac.common.Utils;
import de.johanneswirth.tac.gameserver.data.NoSQLDatabase;
import de.johanneswirth.tac.gameserver.services.GameStartService;
import de.johanneswirth.tac.gameserver.services.GameService;
import de.johanneswirth.tac.gameserver.services.SimulateService;
import io.dropwizard.Application;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.discovery.DiscoveryBundle;
import io.dropwizard.discovery.DiscoveryFactory;
import io.dropwizard.jdbi3.JdbiFactory;
import io.dropwizard.jersey.jackson.JsonProcessingExceptionMapper;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.jdbi.v3.core.Jdbi;

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
        bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(bootstrap.getConfigurationSourceProvider(),
                        new EnvironmentVariableSubstitutor(true)
                )
        );
        bootstrap.addBundle(new MigrationsBundle<GameServerConfiguration>() {
            @Override
            public DataSourceFactory getDataSourceFactory(GameServerConfiguration configuration) {
                return configuration.getDataSourceFactory();
            }
        });
        bootstrap.addBundle(discoveryBundle);
    }

    @Override
    public void run(GameServerConfiguration configuration, Environment environment) {
        final JdbiFactory factory = new JdbiFactory();
        final Jdbi jdbi = factory.build(environment, configuration.getDataSourceFactory(), "mysql");
        NoSQLDatabase database = new NoSQLDatabase(configuration.getNosqlHost(), configuration.getNosqlStore());
        environment.lifecycle().manage(database);
        environment.jersey().register(new GameService(database, jdbi));
        environment.jersey().register(new SimulateService(database, jdbi));
        environment.jersey().register(new GameStartService(database, jdbi));

        final Client client = new JerseyClientBuilder(environment).using(configuration.getJerseyClientConfiguration())
                .build(getName());
        ServiceUtils.init(discoveryBundle, client, environment);
        environment.jersey().register(new ContainerFilter());
        Utils.init(configuration.getPublicKey());
        environment.jersey().register(new JsonProcessingExceptionMapper(true));
    }
}
