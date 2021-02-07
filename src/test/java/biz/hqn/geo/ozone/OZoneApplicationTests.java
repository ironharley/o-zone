package biz.hqn.geo.ozone;

import biz.hqn.geo.ozone.integration.entity.pg.repos.ObjRepo;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class OZoneApplicationTests {

    @Autowired
    private ObjRepo or;

    @Test
    public void contextLoad() throws JsonProcessingException {
    }

}
