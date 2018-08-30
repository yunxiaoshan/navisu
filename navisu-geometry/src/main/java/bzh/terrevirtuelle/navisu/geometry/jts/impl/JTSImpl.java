/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bzh.terrevirtuelle.navisu.geometry.jts.impl;

import bzh.terrevirtuelle.navisu.domain.geometry.Point3D;
import bzh.terrevirtuelle.navisu.geometry.geodesy.GeodesyServices;
import bzh.terrevirtuelle.navisu.geometry.jts.JTS;
import bzh.terrevirtuelle.navisu.geometry.jts.JTSServices;
import bzh.terrevirtuelle.navisu.topology.TopologyServices;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.triangulate.DelaunayTriangulationBuilder;
import gov.nasa.worldwind.render.Path;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.capcaval.c3.component.ComponentState;
import org.capcaval.c3.component.annotation.UsedService;
import org.opensphere.geometry.algorithm.ConcaveHull;

/**
 *
 * @author serge
 */
public class JTSImpl
        implements JTS, JTSServices, ComponentState {

    @UsedService
    TopologyServices topologyServices;
    @UsedService
    GeodesyServices geodesyServices;

    @Override
    public List<Coordinate> toListCoordinates(List<Point3D> pts) {
        List<Coordinate> coordJTSs = new ArrayList<>();
        pts.stream().forEach((p) -> {
            coordJTSs.add(new Coordinate(p.getLongitude(), p.getLatitude(), p.getElevation()));
        });
        return coordJTSs;
    }

    @Override
    public Coordinate[] toTabCoordinates(List<Point3D> pts) {
        Coordinate[] tmp = new Coordinate[pts.size()];
        for (int i = 0; i < tmp.length; i++) {
            Point3D pt = pts.get(i);
            tmp[i] = new Coordinate(pt.getLongitude(), pt.getLatitude(), pt.getElevation());
        }
        return tmp;
    }

    @Override
    public Geometry getConcaveHull(List<Point3D> points, double threshold) {
        List<Coordinate> coordinatesJTS = toListCoordinates(points);
        Coordinate[] coord = new Coordinate[coordinatesJTS.size()];
        for (int c = 0; c < coord.length; c++) {
            coord[c] = coordinatesJTS.get(c);
        }
        MultiPoint geom = new GeometryFactory().createMultiPoint(coord);
        ConcaveHull ch = new ConcaveHull(geom, threshold);
        Geometry concaveHullTmp = ch.getConcaveHull();

        return concaveHullTmp;
    }

    @Override
    public Geometry getLineString(List<Point3D> points) {
        List<Coordinate> coordinatesJTS = toListCoordinates(points);
        Coordinate[] coord = new Coordinate[coordinatesJTS.size()];
        for (int c = 0; c < coord.length; c++) {
            coord[c] = coordinatesJTS.get(c);
        }
        LineString geom = new GeometryFactory().createLineString(coord);
        return geom;
    }

    @Override
    public boolean contains(Geometry geom, Point3D pt3D) {
        boolean result;
        Coordinate coord = new Coordinate(pt3D.getLongitude(), pt3D.getLatitude(), 100);
        GeometryFactory geometryFactory = new GeometryFactory();
        Point pt = geometryFactory.createPoint(coord);
        result = !geom.contains(pt);
        return result;
    }

    @Override
    public List<Point3D> merge(List<Point3D> pts0, List<Point3D> pts1) {
        List<Point3D> tmp = new ArrayList<>();
        Coordinate[] tab0 = toTabCoordinates(pts0);
        Coordinate[] tab1 = toTabCoordinates(pts1);
        GeometryFactory geomFactory = new GeometryFactory();
        MultiPoint multiPoint0 = geomFactory.createMultiPoint(tab0);
        multiPoint0.setSRID(4326);
        MultiPoint multiPoint1 = geomFactory.createMultiPoint(tab1);
        multiPoint1.setSRID(4326);
        Geometry geom = multiPoint0.union(multiPoint1);
        Coordinate[] tab3 = geom.getCoordinates();
        Set<Point3D> set = new HashSet<>();
        for (Coordinate c : tab3) {
            set.add(new Point3D(c.y, c.x, c.z));
        }
        tmp.addAll(set);
        return tmp;
    }

    @Override
    public Point3D[][] mergePointsToGrid(List<Point3D> points, Point3D[][] grid) {
        int line = grid[0].length;
        int col = grid[1].length;
        GeometryFactory geometryFactory = new GeometryFactory();

        // points to JTS Point
        List<Point> data = new ArrayList<>();
        for (Point3D p : points) {
            data.add(geometryFactory.createPoint(new Coordinate(p.getLongitude(), p.getLatitude(), p.getElevation())));
        }
        /*
        New grid
        gridCoord with JTS Point
         */
        Point3D[][] result = new Point3D[line][col];
        Point[][] gridCoord = new Point[line][col];
        for (int i = 0; i < line; i++) {
            for (int j = 0; j < col; j++) {
                result[i][j] = new Point3D(grid[i][j].getLatitude(), grid[i][j].getLongitude(), grid[i][j].getElevation());
                gridCoord[i][j] = geometryFactory.createPoint(new Coordinate(grid[i][j].getLongitude(), grid[i][j].getLatitude(), grid[i][j].getElevation()));
            }
        }

        List<Geometry> triangles = createDelaunayWithFilterOnArea(points, 1E-6);
       // System.out.println("triangles : " + triangles.size());
        double dA;
        double dB;
        double dC;
        double min;
        for (Geometry t : triangles) {
            for (int i = 0; i < line; i++) {
                for (int j = 0; j < col; j++) {
                    if (t.contains(gridCoord[i][j])) {
                        Coordinate[] coordinates = t.getCoordinates();
                      //  System.out.println(coordinates[0] + " " + coordinates[1] + " " + coordinates[3]);

                        dA = geodesyServices.getDistanceM(coordinates[0].y, coordinates[0].x,
                                gridCoord[i][j].getCoordinate().y, gridCoord[i][j].getCoordinate().x);
                        dB = geodesyServices.getDistanceM(coordinates[1].y, coordinates[1].x,
                                gridCoord[i][j].getCoordinate().y, gridCoord[i][j].getCoordinate().x);
                        dC = geodesyServices.getDistanceM(coordinates[2].y, coordinates[2].x,
                                gridCoord[i][j].getCoordinate().y, gridCoord[i][j].getCoordinate().x);

                        min = Double.MAX_VALUE;
                        if (min > dA) {
                            min = dA;
                            result[i][j].setElevation(coordinates[0].z);
                        }
                        if (min > dB) {
                            min = dB;
                            result[i][j].setElevation(coordinates[1].z);
                        }
                        if (min > dC) {
                            result[i][j].setElevation(coordinates[2].z);
                        }
                    }
                }
            }
        }

        return result;
    }

    @Override
    public List<Path> createDelaunayToPath(List<Point3D> pts) {
        Coordinate[] coordinateTab = toTabCoordinates(pts);
        MultiPoint points = new GeometryFactory().createMultiPoint(coordinateTab);
        ArrayList<Geometry> triangles = new ArrayList<>();
        DelaunayTriangulationBuilder triator = new DelaunayTriangulationBuilder();
        triator.setSites(points);
        Geometry tris = triator.getTriangles(new GeometryFactory());
        for (int i = 0; i < tris.getNumGeometries(); i++) {
            triangles.add(tris.getGeometryN(i));
        }
        List<Path> paths = topologyServices.wktPolygonsToWwjPaths(triangles);
        return paths;
    }

    @Override
    public List<Geometry> createDelaunay(List<Point3D> pts) {
        Coordinate[] coordinateTab = toTabCoordinates(pts);
        MultiPoint points = new GeometryFactory().createMultiPoint(coordinateTab);
        ArrayList<Geometry> triangles = new ArrayList<>();
        DelaunayTriangulationBuilder triator = new DelaunayTriangulationBuilder();
        triator.setSites(points);
        Geometry tris = triator.getTriangles(new GeometryFactory());
        for (int i = 0; i < tris.getNumGeometries(); i++) {
            triangles.add(tris.getGeometryN(i));
        }
        return triangles;
    }

    @Override
    public List<Path> createDelaunayToPath(Point3D[][] pts) {
        List<Point3D> data = new ArrayList<>();
        for (int i = 0; i < pts[0].length; i++) {
            for (int j = 0; j < pts[1].length; j++) {
                data.add(pts[i][j]);
            }
        }
        return createDelaunayToPath(data);
    }

    @Override
    public List<Path> createDelaunayToPath(List<Point3D> pts, double maxElevation) {
        List<Point3D> data = new ArrayList<>();
        for (Point3D p : pts) {
            data.add(new Point3D(p.getLatitude(), p.getLongitude(), maxElevation - p.getElevation()));
        }
        Coordinate[] coordinateTab = toTabCoordinates(data);
        MultiPoint points = new GeometryFactory().createMultiPoint(coordinateTab);
        ArrayList<Geometry> triangles = new ArrayList<>();
        DelaunayTriangulationBuilder triator = new DelaunayTriangulationBuilder();
        triator.setSites(points);
        Geometry tris = triator.getTriangles(new GeometryFactory());
        for (int i = 0; i < tris.getNumGeometries(); i++) {
            triangles.add(tris.getGeometryN(i));
        }
        List<Path> paths = topologyServices.wktPolygonsToWwjPaths(triangles);
        return paths;
    }

    @Override
    public List<Path> createDelaunayToPath(Point3D[][] pts, double maxElevation) {
        List<Point3D> data = new ArrayList<>();
        for (int i = 0; i < pts[0].length; i++) {
            for (int j = 0; j < pts[1].length; j++) {
                data.add(pts[i][j]);
            }
        }
        return createDelaunayToPath(data, maxElevation);
    }

    @Override
    public List<Path> createDelaunayWithFilterOnAreaToPath(List<Point3D> pts, double filter) {
        Coordinate[] coordinateTab = toTabCoordinates(pts);
        MultiPoint points = new GeometryFactory().createMultiPoint(coordinateTab);
        ArrayList<Geometry> triangles = new ArrayList<>();
        DelaunayTriangulationBuilder triator = new DelaunayTriangulationBuilder();
        triator.setSites(points);
        Geometry tris = triator.getTriangles(new GeometryFactory());
        for (int i = 0; i < tris.getNumGeometries(); i++) {
            triangles.add(tris.getGeometryN(i));
        }
        List<Path> paths = topologyServices.wktPolygonsToWwjPathsWithFilterOnArea(triangles, filter);
        return paths;
    }

    @Override
    public List<Geometry> createDelaunayWithFilterOnArea(List<Point3D> pts, double filter) {
        Coordinate[] coordinateTab = toTabCoordinates(pts);
        MultiPoint points = new GeometryFactory().createMultiPoint(coordinateTab);
        ArrayList<Geometry> triangles = new ArrayList<>();
        DelaunayTriangulationBuilder triator = new DelaunayTriangulationBuilder();
        triator.setSites(points);
        Geometry tris = triator.getTriangles(new GeometryFactory());
        for (int i = 0; i < tris.getNumGeometries(); i++) {
            triangles.add(tris.getGeometryN(i));
        }
        ArrayList<Geometry> result = new ArrayList<>();
        for (Geometry g : triangles) {
            if (g.getArea() < filter) {
                result.add(g);
            }
        }
        return result;
    }

    @Override
    public List<Path> createDelaunayWithFilterOnLengthToPath(List<Point3D> pts, double filter) {
        Coordinate[] coordinateTab = toTabCoordinates(pts);
        MultiPoint points = new GeometryFactory().createMultiPoint(coordinateTab);
        ArrayList<Geometry> triangles = new ArrayList<>();
        DelaunayTriangulationBuilder triator = new DelaunayTriangulationBuilder();
        triator.setSites(points);
        Geometry tris = triator.getTriangles(new GeometryFactory());
        for (int i = 0; i < tris.getNumGeometries(); i++) {
            triangles.add(tris.getGeometryN(i));
        }
        List<Path> paths = topologyServices.wktPolygonsToWwjPathsWithFilterOnLength(triangles, filter);
        return paths;
    }

    @Override
    public List<Path> createDelaunayWithFilterToPath(List<Point3D> pts, double filter, double maxElevation) {
        List<Point3D> bathy = new ArrayList<>();
        pts.forEach((p) -> {
            bathy.add(new Point3D(p.getLatitude(), p.getLongitude(), maxElevation - p.getElevation()));
        });
        Coordinate[] coordinateTab = toTabCoordinates(bathy);
        MultiPoint points = new GeometryFactory().createMultiPoint(coordinateTab);
        ArrayList<Geometry> triangles = new ArrayList<>();
        DelaunayTriangulationBuilder triator = new DelaunayTriangulationBuilder();
        triator.setSites(points);
        Geometry tris = triator.getTriangles(new GeometryFactory());
        for (int i = 0; i < tris.getNumGeometries(); i++) {
            triangles.add(tris.getGeometryN(i));
        }
        List<Path> paths = topologyServices.wktPolygonsToWwjPathsWithFilterOnArea(triangles, filter);
        return paths;
    }

    @Override
    public void componentInitiated() {
    }

    @Override
    public void componentStarted() {
    }

    @Override
    public void componentStopped() {
    }
}
