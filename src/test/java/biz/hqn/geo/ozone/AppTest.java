package biz.hqn.geo.ozone;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.locationtech.jts.geom.*;
import org.locationtech.spatial4j.context.SpatialContext;
import org.locationtech.spatial4j.context.jts.JtsSpatialContext;
import org.locationtech.spatial4j.context.jts.JtsSpatialContextFactory;
import org.locationtech.spatial4j.shape.Point;
import org.locationtech.spatial4j.shape.Shape;
import org.locationtech.spatial4j.shape.ShapeFactory;
import org.locationtech.spatial4j.shape.jts.JtsShapeFactory;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.TreeSet;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class AppTest {
    @Test
    public void shouldAnswerWithTrue(){
        assertTrue(true);
    }

    @Test
    public void spatial4jShapeTest(){
        JtsSpatialContextFactory jtsSpatialContextFactory = new JtsSpatialContextFactory();
        JtsSpatialContext jtsSpatialContext = jtsSpatialContextFactory.newSpatialContext();
        JtsShapeFactory jtsShapeFactory = jtsSpatialContext.getShapeFactory();
        ShapeFactory.PolygonBuilder polygonBuilder = jtsShapeFactory.polygon();
// note due to it being a builder one needs to chain the points.
        Shape geo = polygonBuilder
                .pointXY(56.165615, 38.859550)
                .pointXY(56.166374, 38.879435)
                .pointXY(56.159917, 38.888566)
                .pointXY(56.152676, 38.861939)
                .pointXY(56.165615, 38.859550)
                .pointXY(56.165615, 38.859550)
                .build();
        Point pt = jtsShapeFactory.pointXY(56.161936, 38.870545);

        System.out.println(geo);
        SpatialContext ctx = geo.getContext();
        System.out.println("\tctx:" + ctx);
        System.out.println("\tisgeo:" + ctx.isGeo());
        System.out.println("\tfmts:" + ctx.getFormats().getReaders());
        System.out.println("\tdcalc:" + ctx.getDistCalc());

        assertTrue(geo.relate(pt).intersects());
    }

    @Test
    public void jtsPointInPolygonTest(){

        GeometryFactory gf = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), 4326);
        // create around abbey polygon
        int numPoints = 5;
        Coordinate[] points = new Coordinate[numPoints];

        points[0] = new Coordinate(56.165615, 38.859550, 0);
        points[1] = new Coordinate(56.166374, 38.879435, 0);
        points[2] = new Coordinate(56.159917, 38.888566, 0);
        points[3] = new Coordinate(56.152676, 38.861939, 0);
        points[4] = new Coordinate(56.165615, 38.859550, 0);

        LinearRing jtsRing = gf.createLinearRing(points);
        Polygon poly = gf.createPolygon(jtsRing, null);

        Geometry geo = poly.getBoundary();
        System.out.println(geo);
        System.out.println("\ttype=" + geo.getGeometryType());
        System.out.println("\tsrid=" + geo.getSRID());
        System.out.println("\tnumGeoms=" + geo.getNumGeometries());
        System.out.println("\tarea=" + geo.getArea());
        System.out.println("\tprecModel=" + geo.getPrecisionModel());
        System.out.println("\tdim=" + geo.getDimension());
        System.out.println("\tn-points=" + geo.getNumPoints());
        // Check
        //abbey
        assertTrue(poly.contains(gf.createPoint(new Coordinate(56.161936, 38.870545))));
        //somewhere
        assertFalse(poly.contains(gf.createPoint(new Coordinate(58.161936, 39.870545))));
    }

    @Test
    public void jtsPointOnRoadTest(){
        GeometryFactory gf = new GeometryFactory();
// create polygon
        int numPoints = 4;
        Coordinate[] points = new Coordinate[numPoints];
// set points
        points[0] = new Coordinate(56.282620, 39.385932, 0);
        points[1] = new Coordinate(56.282590, 39.383529, 0);
        points[2] = new Coordinate(56.282524, 39.378250, 0);
        points[3] = new Coordinate(56.282405, 39.374956, 0);

        Geometry jtsRoad = gf.createLineString(points).getEnvelope();
        System.out.println(jtsRoad);
        // abbey
        System.out.println(jtsRoad.contains(gf.createPoint(new Coordinate(56.282560, 39.3805))));
        // anywhere
        System.out.println(jtsRoad.contains(gf.createPoint(new Coordinate(58.282560, 37.3805))));

    }

    @Test
    public void treesetest(){
        final TreeSet<TheClass> set = new TreeSet<>();
        set.add(new TheClass(3, Instant.now().minus(1, ChronoUnit.HOURS)));
        set.add(new TheClass(2, Instant.now().minus(2, ChronoUnit.HOURS)));
        set.add(new TheClass(0, Instant.now().minus(3, ChronoUnit.HOURS), true));
        System.out.println(set);
    }

    private static class TheClass implements Comparable<TheClass> {
        final boolean loopback;
        final private long id;
        final private Instant dt;

        public TheClass(long id, Instant dt){
            this(id, dt, false);
        }

        public TheClass(long id, Instant dt, boolean loopback){
            this.id = id;
            this.dt = dt;
            this.loopback = loopback;
        }

        public long getId(){
            return id;
        }

        public Instant getDt(){
            return dt;
        }

        public boolean isLoopback(){
            return loopback;
        }

        @Override
        public String toString(){
            return "[id=" + getId() + ", dt=" + getDt() + ", loop=" + isLoopback() + "]";
        }

        @Override
        public int compareTo(TheClass theClass){
            int res = isLoopback() ? 1 : 0;
            if (res == 0) {
                res = Long.compare(getId(), theClass.getId());
                if (res == 0) {
                    res = getDt().compareTo(theClass.getDt());
                }
            }
            return res;
        }
    }
}
