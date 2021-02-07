package biz.hqn.geo.ozone.entity;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import biz.hqn.geo.ozone.integration.entity.pg.Obj;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ZoneHelper {


    public static Set<IZone> ext2zone(List<Obj> list){
        Set<IZone> res = new TreeSet<>();
        for (Obj o : list) {
            if (o.getGeom() != null)
                res.add(IZone
                        .builder()
                        .id(o.getId_obj())
                        .s_name(o.getS_name())
                        .wkt(o.getGeom().toText())
                        .build());
        }
        return res;
    }

}
