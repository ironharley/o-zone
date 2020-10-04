package biz.hqn.geo.ozone.cache;

import org.apache.ignite.Ignite;

import java.util.Optional;

public interface Kernel {
    Optional<Ignite> ignite();
}
