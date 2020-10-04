package biz.hqn.geo.ozone.config;

import biz.hqn.geo.ozone.entity.IBoost;
import biz.hqn.geo.ozone.entity.IZone;
import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheWriteSynchronizationMode;
import org.apache.ignite.configuration.*;
import org.apache.ignite.spi.communication.tcp.TcpCommunicationSpi;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.apache.ignite.ssl.SslContextFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Configuration
@Slf4j
public class IgniteSvcConfiguration {

    private static final String DATA_CONFIG_NAME = "__DataRegionConfiguration";
    @Value("${debug:false}")
    private boolean debug;
    @Value("${application.ignite.instance_name:o-zone}")
    private String igniteInstanceName;
    @Value("${application.ignite.persistence_file_path:~/tmp/ozone-cache}")
    private String ignitePersistenceFilePath;
    @Value("${application.ignite.connector_port:56656}")
    private int igniteConnectorPort;
    @Value("${application.ignite.client_port:23568}")
    private int igniteClientPort;
    @Value("${application.ignite.enable_file_persistence:true}")
    private boolean enableFilePersistence;
    @Value("${application.ignite.network_auth_required:true}")
    private boolean network_auth_required;
    @Value("${application.ignite.network_ssl_required:false}")
    private boolean network_ssl_required;
    @Value("${application.ignite.local_cluster:true}")
    private boolean local_cluster;
    @Value("${application.ignite.server_address:127.0.0.1}")
    private String igniteServerAddress;
    @Value("${application.ignite.server_port_range:47500..47509}")
    private String igniteServerPortRange;
    @Value("${application.ignite.local-address:0.0.0.0}")
    private String igniteLocalServerAddress;
    @Value("${application.ignite.local-port:12343}")
    private int igniteLocalServerPort;
    @Value("${application.ignite.local-queue-size:100}")
    private int igniteLocalQueueSize;
    @Value("${application.ignite.cluster_mode:false}")
    private boolean igniteClusterModeEnabled;
    @Value("${application.ignite.base_multiplexor:1024}")
    private long igniteBaseMuxor;
    @Value("${application.ignite.page_init_counter:4}")
    private int ignitePgInitCounter;
    @Value("${application.ignite.region_initial_size:128}")
    private long igniteRegInitSz;
    @Value("${application.ignite.region_max_size:4096}")
    private long igniteRegMaxSz;
    // optionals
    @Value("${application.ignite.optional.metrics_log_freq:0}")
    private int ignitionOptMetrLogFq;
    @Value("${application.ignite.optional.query_thread_pool_size:2}")
    private int ignitionOptQueryTps;
    @Value("${application.ignite.optional.data_streamer_thread_pool_size:1}")
    private int ignitionOptDataStrTps;
    @Value("${application.ignite.optional.management_thread_pool_size:2}")
    private int ignitionOptMgmtTps;
    @Value("${application.ignite.optional.public_thread_pool_size:2}")
    private int ignitionOptPubTps;
    @Value("${application.ignite.optional.system_thread_pool_size:2}")
    private int ignitionOptSysTps;
    @Value("${application.ignite.optional.rebalance_thread_pool_size:1}")
    private int ignitionOptRebTps;
    @Value("${application.ignite.optional.async_callback_thread_pool_size:2}")
    private int ignitionOptAsyncCnTps;
    @Value("${application.ignite.optional.peer_classloading_enable:false}")
    private boolean ignitionOptPeerClEn;
    @Value("${application.ignite.optional.failure_detection_timeout_min:60}")
    private int failure_detection_timeout_min;
    @Value("${application.ignite.optional.system_worker_blocked_timeout_min:60}")
    private int system_worker_blocked_timeout_min;
    @Value("${application.ignite.optional.client_failure_detection_timeout_min:60}")
    private int client_failure_detection_timeout_min;
    @Value("${application.ignite.optional.long_query_warning_timeout_min:60}")
    private int long_query_warning_timeout_min;
    @Value("${application.ignite.optional.storage_subpath:storage}")
    private String ignitionOptStorageSubp;
    @Value("${application.ignite.optional.wal_arch_subpath:walArchive}")
    private String ignitionOptWalArchSubp;
    @Value("${application.ignite.optional.wal_subpath:walStore}")
    private String ignitionOptWalSubp;
    @Value("${application.ignite.optional.metrics-enabled:false}")
    private boolean metrics_enabled;


    @PostConstruct
    public void init(){
    }

    @PreDestroy
    public void close(){
    }

    @Bean
    public Boolean clusterMode(){
        return this.igniteClusterModeEnabled;
    }

    @Bean("igniteConfiguration")
    public IgniteConfiguration igniteConfiguration(){

        // cache configuration
        final List<CacheConfiguration> cc_list = new LinkedList<>();
        cc_list.add(
                new CacheConfiguration<>()
                        .setCopyOnRead(false)
                        .setBackups(0)
                        .setAtomicityMode(CacheAtomicityMode.ATOMIC)
                        .setName(CacheNames.Boost.name())
                        .setIndexedTypes(Long.class, IBoost.class)
                        .setStatisticsEnabled(metrics_enabled)
                        .setDataRegionName(DATA_CONFIG_NAME)
                        .setWriteSynchronizationMode(CacheWriteSynchronizationMode.FULL_SYNC)
        );

        // это уже отправленные алярмы.
        // чтобы не отправлять чаще чем нужно, после контрольного времени давятся
        cc_list.add(
                new CacheConfiguration<>()
                        .setCopyOnRead(false)
                        .setBackups(0)
                        .setAtomicityMode(CacheAtomicityMode.ATOMIC)
                        .setName(CacheNames.Zones.name())
                        .setIndexedTypes(Long.class, IZone.class)
                        .setDataRegionName(DATA_CONFIG_NAME)
                        .setStatisticsEnabled(metrics_enabled)
                        .setWriteSynchronizationMode(CacheWriteSynchronizationMode.FULL_ASYNC)
        );


        final IgniteConfiguration igniteConfiguration = new IgniteConfiguration()
                .setWorkDirectory(this.ignitePersistenceFilePath)
                .setClientMode(false);

        long dr_init = this.igniteRegInitSz *
                this.igniteBaseMuxor *
                this.igniteBaseMuxor;
        long dr_max = this.igniteRegMaxSz *
                this.igniteBaseMuxor *
                this.igniteBaseMuxor;
        if (debug)
            log.warn("### {}: init {}, max {}", DATA_CONFIG_NAME, dr_init, dr_max);

        // durable file memory persistence
        if (this.enableFilePersistence) {
            DataStorageConfiguration dsc = new DataStorageConfiguration()
                    .setStoragePath(ignitePersistenceFilePath +
                            File.separator +
                            this.ignitionOptStorageSubp)
                    .setWalArchivePath(ignitePersistenceFilePath +
                            File.separator +
                            this.ignitionOptWalArchSubp)
                    .setWalPath(ignitePersistenceFilePath +
                            File.separator +
                            this.ignitionOptWalSubp)
                    .setPageSize((int) (this.ignitePgInitCounter *
                            this.igniteBaseMuxor))
                    .setDataRegionConfigurations(
                            new DataRegionConfiguration()
                                    .setName(DATA_CONFIG_NAME)
                                    .setInitialSize(dr_init)
                                    .setMaxSize(dr_max)
                                    .setMetricsEnabled(metrics_enabled)
                                    .setCheckpointPageBufferSize(this.ignitePgInitCounter * 2 *
                                            this.igniteBaseMuxor *
                                            this.igniteBaseMuxor *
                                            this.igniteBaseMuxor)
                                    .setPersistenceEnabled(this.enableFilePersistence));
            igniteConfiguration
                    .setDataStorageConfiguration(dsc)
                    .setConsistentId(this.igniteInstanceName + "_fsys");
        }
        // cluster tcp configuration
        final TcpDiscoverySpi tcpDiscoverySpi = new TcpDiscoverySpi();
        if (this.igniteClusterModeEnabled) {

            if (this.local_cluster) {
                tcpDiscoverySpi
                        .setIpFinder(new TcpDiscoveryVmIpFinder(true));
            } else {
                tcpDiscoverySpi
                        .setIpFinder(new TcpDiscoveryVmIpFinder()
                                .setAddresses(   // need to be changed when it come to real cluster
                                        Collections.singletonList(
                                                String.format("%s:%s",
                                                        this.igniteServerAddress,
                                                        this.igniteServerPortRange
                                                )
                                        )
                                )
                        );

            }
        }


        if (this.network_ssl_required) {
            SslContextFactory ssl_ctx = new SslContextFactory();
            ssl_ctx.setTrustManagers(SslContextFactory.getDisabledTrustManager());
            ssl_ctx.setTrustStorePassword(new char[]{0, 0, 0, 0});
            ssl_ctx.setTrustStoreType(SslContextFactory.DFLT_STORE_TYPE);

            ssl_ctx.setKeyStoreFilePath(this.ignitePersistenceFilePath + "/ksf");
            ssl_ctx.setKeyStorePassword(new char[]{0, 0, 0, 0});
            ssl_ctx.setKeyStoreType(SslContextFactory.DFLT_STORE_TYPE);
            ssl_ctx.setKeyAlgorithm(SslContextFactory.DFLT_KEY_ALGORITHM);

            ssl_ctx.setProtocol(SslContextFactory.DFLT_SSL_PROTOCOL);
            igniteConfiguration.setSslContextFactory(ssl_ctx);
        }
        // common ignite configuration
        return igniteConfiguration
                .setClientConnectorConfiguration(
                        new ClientConnectorConfiguration()
                                .setHost("127.0.0.1")
                                .setPort(this.igniteClientPort)
                )
                .setAuthenticationEnabled(this.network_auth_required)
                .setMetricsLogFrequency(this.ignitionOptMetrLogFq)
                .setQueryThreadPoolSize(this.ignitionOptQueryTps)
                .setDataStreamerThreadPoolSize(this.ignitionOptDataStrTps)
                .setManagementThreadPoolSize(this.ignitionOptMgmtTps)
                .setPublicThreadPoolSize(this.ignitionOptPubTps)
                .setSystemThreadPoolSize(this.ignitionOptSysTps)
                .setRebalanceThreadPoolSize(this.ignitionOptRebTps)
                .setAsyncCallbackPoolSize(this.ignitionOptAsyncCnTps)
                .setPeerClassLoadingEnabled(this.ignitionOptPeerClEn)
                .setIgniteInstanceName(this.igniteInstanceName)
                .setFailureDetectionTimeout(this.failure_detection_timeout_min * 60 * 1000)
                .setSystemWorkerBlockedTimeout(this.system_worker_blocked_timeout_min * 60 * 1000)
                .setClientFailureDetectionTimeout(this.client_failure_detection_timeout_min * 60 * 1000)
                .setLongQueryWarningTimeout(this.long_query_warning_timeout_min * 60 * 1000)
                .setConnectorConfiguration(
                        new ConnectorConfiguration()
                                .setSslClientAuth(network_ssl_required)
                                .setSslEnabled(network_ssl_required)
                                .setPort(this.igniteConnectorPort))
                .setBinaryConfiguration(new BinaryConfiguration().setCompactFooter(false))
                .setDiscoverySpi(tcpDiscoverySpi)
                .setCommunicationSpi(new TcpCommunicationSpi()
                        .setMessageQueueLimit(this.igniteLocalQueueSize)
                        .setLocalAddress(this.igniteLocalServerAddress)
                        .setLocalPort(this.igniteLocalServerPort))
                .setCacheConfiguration(cc_list.toArray(new CacheConfiguration[0]));
    }


    public enum CacheNames {
        Zones,
        Boost
    }

}
