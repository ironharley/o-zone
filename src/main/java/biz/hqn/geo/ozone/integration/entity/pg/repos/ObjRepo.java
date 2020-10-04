package biz.hqn.geo.ozone.integration.entity.pg.repos;

import biz.hqn.geo.ozone.integration.entity.pg.Obj;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ObjRepo extends JpaRepository<Obj, Long> {
}
