package biz.hqn.geo.ozone.entity;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.WKTReader;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Builder
@Data
@ToString
@Slf4j
public class IZone implements Comparable<IZone> {

    private Long id;
    private String wkt;
    private String s_name;
    private String s_comment;
    private Long id_account;

    public Optional<Geometry> geometry(){
        final AtomicReference<Geometry> res = new AtomicReference<>(null);
        Optional.ofNullable(this.wkt).ifPresent(wkt -> {
            try {
                res.set(new WKTReader().read(wkt));
            } catch (Exception e) {
                log.error("{}", e.getMessage(), e);
            }
        });
        return res.get() == null ? Optional.empty() : Optional.of(res.get());
    }

    @Override
    public int compareTo(@NotNull IZone iZone){
        return Long.compare(getId(), iZone.getId());
    }
}
