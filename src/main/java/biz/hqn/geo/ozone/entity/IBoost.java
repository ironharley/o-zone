package biz.hqn.geo.ozone.entity;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class IBoost {

    private Long id;
    private String wkt;
}
