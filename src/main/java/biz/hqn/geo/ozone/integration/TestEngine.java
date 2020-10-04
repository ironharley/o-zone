package biz.hqn.geo.ozone.integration;

import biz.hqn.geo.ozone.entity.ZoneHelper;
import biz.hqn.geo.ozone.integration.entity.pg.Obj;
import biz.hqn.geo.ozone.integration.entity.pg.repos.ObjRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

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
            log.info("{} ", ZoneHelper.ext2zone(objs));

        } catch (Exception e) {
            log.error("{}", e.getMessage(), e);
        }
    }
}
