package biz.hqn.geo.ozone.integration;

import java.util.List;
import javax.annotation.PostConstruct;
import biz.hqn.geo.ozone.integration.entity.pg.Obj;
import biz.hqn.geo.ozone.integration.entity.pg.repos.ObjRepo;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.io.geojson.GeoJsonWriter;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@Component
@DependsOn("Kernel")
@Slf4j
public class TestEngine {
    private final ObjRepo objRepo;

    public TestEngine(final ObjRepo objRepo){
        this.objRepo = objRepo;
    }

    @PostConstruct
    public void init(){
        try {
            List<Obj> objs = this.objRepo.findAll();
            GeometryFactory gf = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), 4326);
            GeoJsonWriter gjw = new GeoJsonWriter();
            //log.info("{} ",gjw.write(objs.get(0).getGeom()));
            //log.info("{} ", ZoneHelper.ext2zone(objs));

        } catch (Exception e) {
            log.error("{}", e.getMessage(), e);
        }
    }
}
