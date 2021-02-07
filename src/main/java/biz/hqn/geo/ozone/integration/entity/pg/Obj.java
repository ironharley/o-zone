package biz.hqn.geo.ozone.integration.entity.pg;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import com.vividsolutions.jts.geom.Geometry;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

@Entity
@Table(schema = "mon", name = "obj")
@Getter
@ToString
public class Obj implements Serializable, Comparable<Obj> {

    @Id
    @Column(name = "id_obj")
    private long id_obj;
    @Column(columnDefinition = "Geometry")
    private Geometry geom;
    @Column
    private String s_name;
    @Column
    private String s_comment;
    @Column
    private Long id_account;
    @Column
    private Long geom_buffer_m;
    @Column
    private Integer is_active;

    @Override
    public int compareTo(@NotNull Obj obj){
        return Long.compare(getId_obj(), obj.getId_obj());
    }
}
