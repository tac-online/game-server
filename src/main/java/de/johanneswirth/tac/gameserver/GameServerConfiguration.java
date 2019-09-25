package de.johanneswirth.tac.gameserver;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.client.JerseyClientConfiguration;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.discovery.DiscoveryFactory;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class GameServerConfiguration extends Configuration {

    @NotEmpty
    private String publicKey;

    @NotEmpty
    private String nosqlHost;
    @NotEmpty
    private String nosqlStore;

    @JsonProperty
    public String getPublicKey() {
        return publicKey;
    }

    @JsonProperty
    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    @JsonProperty
    public String getNosqlHost() {
        return nosqlHost;
    }

    @JsonProperty
    public void setNosqlHost(String nosqlHost) {
        this.nosqlHost = nosqlHost;
    }

    @JsonProperty
    public String getNosqlStore() {
        return nosqlStore;
    }

    @JsonProperty
    public void setNosqlStore(String nosqlStore) {
        this.nosqlStore = nosqlStore;
    }

    @Valid
    @NotNull
    private DiscoveryFactory discovery = new DiscoveryFactory();

    @JsonProperty("discovery")
    public DiscoveryFactory getDiscoveryFactory() {
        return discovery;
    }

    @JsonProperty("discovery")
    public void setDiscoveryFactory(DiscoveryFactory discoveryFactory) {
        this.discovery = discoveryFactory;
    }

    @Valid
    @NotNull
    private JerseyClientConfiguration jerseyClient = new JerseyClientConfiguration();

    @JsonProperty("jerseyClient")
    public JerseyClientConfiguration getJerseyClientConfiguration() {
        return jerseyClient;
    }

    @JsonProperty("jerseyClient")
    public void setJerseyClientConfiguration(JerseyClientConfiguration jerseyClient) {
        this.jerseyClient = jerseyClient;
    }

    @Valid
    @NotNull
    private DataSourceFactory database = new DataSourceFactory();

    @JsonProperty("database")
    public void setDataSourceFactory(DataSourceFactory factory) {
        this.database = factory;
    }

    @JsonProperty("database")
    public DataSourceFactory getDataSourceFactory() {
        return database;
    }
}