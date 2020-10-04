package biz.hqn.geo.ozone.cache.impl;

import biz.hqn.geo.ozone.cache.Kernel;
import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteState;
import org.apache.ignite.Ignition;
import org.apache.ignite.IgnitionListener;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

@Component("Kernel")
@Slf4j
public class KernelImpl implements Kernel, IgnitionListener {
    private final IgniteConfiguration igniteConfiguration;

    private final ReentrantLock lock = new ReentrantLock();
    private Ignite ignite;

    @Value("${application.ignite.cluster_mode:false}")
    private boolean clusterMode;

    public KernelImpl(final IgniteConfiguration igniteConfiguration){
        this.igniteConfiguration = igniteConfiguration;
    }

    @PostConstruct
    public void init(){
        this.__start_ignite().ifPresent(ignite -> {
            //todo smth
        });
    }


    @PreDestroy
    public void close(){
        this.ignite().ifPresent(ignite -> {
            this.__stop_ignite();
        });
    }

    @Override
    public Optional<Ignite> ignite(){
        return this.lock.isLocked()
                ? Optional.empty()
                : Optional.of(this.ignite);
    }

    private Optional<Ignite> __start_ignite(){
        this.ignite = Ignition.start(this.igniteConfiguration);
        this.ignite.cluster().active(this.clusterMode);
        if (this.clusterMode) {
            this.ignite.cluster().setBaselineTopology(this.ignite.cluster().forServers().nodes());
        }
        Ignition.addListener(this);
        return this.ignite();
    }

    private void __stop_ignite(){
        try {
            log.warn("Ignite drove to stop");
            this.lock.lock();
            Ignition.stop(this.ignite.name(), false);
        } catch (Exception e) {
            log.error("{}", e.getMessage(), e);
        }
    }

    @Override
    public void onStateChange(@Nullable String s, IgniteState igniteState){
        log.info("Ignite state changed: {}/{}", s, igniteState);
    }
}
