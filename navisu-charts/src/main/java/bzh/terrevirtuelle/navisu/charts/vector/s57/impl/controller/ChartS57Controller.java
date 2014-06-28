/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bzh.terrevirtuelle.navisu.charts.vector.s57.impl.controller;

import bzh.terrevirtuelle.navisu.charts.vector.s57.impl.controller.loader.DEPARE_ShapefileLoader;
import bzh.terrevirtuelle.navisu.charts.vector.s57.impl.controller.loader.DEPCNT_ShapefileLoader;
import bzh.terrevirtuelle.navisu.charts.vector.s57.impl.controller.loader.BCNCAR_ShapefileLoader;
import bzh.terrevirtuelle.navisu.charts.vector.s57.impl.controller.loader.BCNISD_ShapefileLoader;
import bzh.terrevirtuelle.navisu.charts.vector.s57.impl.controller.loader.BCNLAT_ShapefileLoader;
import bzh.terrevirtuelle.navisu.charts.vector.s57.impl.controller.loader.LIGHTS_ShapefileLoader;
import bzh.terrevirtuelle.navisu.charts.vector.s57.impl.controller.loader.M_NSYS_ShapefileLoader;
import bzh.terrevirtuelle.navisu.charts.vector.s57.impl.controller.loader.OBSTRN_CNT_ShapefileLoader;
import bzh.terrevirtuelle.navisu.charts.vector.s57.impl.controller.loader.OBSTRN_ShapefileLoader;
import bzh.terrevirtuelle.navisu.charts.vector.s57.impl.controller.loader.UWTROC_ShapefileLoader;
import bzh.terrevirtuelle.navisu.charts.vector.s57.impl.controller.loader.WRECKS_CNT_ShapefileLoader;
import bzh.terrevirtuelle.navisu.charts.vector.s57.impl.controller.loader.WRECKS_ShapefileLoader;
import bzh.terrevirtuelle.navisu.charts.vector.s57.impl.view.Lights;
import bzh.terrevirtuelle.navisu.core.view.geoview.worldwind.impl.GeoWorldWindViewImpl;
import bzh.terrevirtuelle.navisu.domain.charts.vector.s57.S57Object;
import bzh.terrevirtuelle.navisu.domain.charts.vector.s57.geo.BeaconCardinal;
import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.event.SelectEvent;
import gov.nasa.worldwind.layers.AirspaceLayer;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwindx.examples.util.ShapefileLoader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Serge Morvan
 * @date 11/05/2014 12:49
 */
public class ChartS57Controller {

    private static final ChartS57Controller INSTANCE;
    protected String path;
    private File file;
    //  private File fileDepare;
    private Map<String, String> acronyms;
    private Map<String, Map<Long, S57Object>> geos;
    private final List<Layer> layers;
    private final List<AirspaceLayer> airspaceLayers;
    private ShapefileLoader loader;
    protected WorldWindow wwd;

    static {
        INSTANCE = new ChartS57Controller();
    }

    public ChartS57Controller() { wwd = GeoWorldWindViewImpl.getWW();
        
        initAcronymsMap();
        layers = new ArrayList<>();
        airspaceLayers = new ArrayList<>();
    }

    private void initAcronymsMap() {
        acronyms = new HashMap<>();
        String tmp;
        String[] tmpTab;
        try {
            BufferedReader input = new BufferedReader(
                    new FileReader("properties/s57AcronymClasses.txt"));
            while ((tmp = input.readLine()) != null) {
                tmpTab = tmp.split(",");
                acronyms.put(tmpTab[0], tmpTab[1]);
            }
        } catch (IOException ex) {
            Logger.getLogger(ChartS57Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static ChartS57Controller getInstance() {
        return INSTANCE;
    }

    public final void init(String path) {
        this.path = path;
        file = new File(path);
        initGeosMap();
    }

    public List<AirspaceLayer> getAirspaceLayers() {
        return airspaceLayers;
    }

    public List<Layer> getLayers() {
        return layers;
    }

    private void initGeosMap() {
        geos = new HashMap<>();

        File[] listOfFiles;
        File tmp;
        if (file.isDirectory()) {
            listOfFiles = file.listFiles();
            for (File f : listOfFiles) {
                String s = f.getName();
                if (s.equals("DEPARE.shp")) {
                    loader = new DEPARE_ShapefileLoader();
                    tmp = new File(path + "/DEPARE.shp");
                    List<Layer> la = loader.createLayersFromSource(tmp);
                    la.stream().forEach((l) -> {
                        l.setName("DEPARE");
                    });
                    layers.addAll(la);
                }
                if (s.equals("DEPCNT.shp")) {
                    loader = new DEPCNT_ShapefileLoader();
                    tmp = new File(path + "/DEPCNT.shp");
                    List<Layer> la = loader.createLayersFromSource(tmp);
                    la.stream().forEach((l) -> {
                        l.setName("DEPCNT");
                    });
                    layers.addAll(la);
                }
                /*
                 if (s.equals("M_COVR.shp")) {
                 loader = new M_COVR_ShapefileLoader();
                 tmp = new File(path + "/M_COVR.shp");
                 List<Layer> la = loader.createLayersFromSource(tmp);
                 la.stream().forEach((l) -> {
                 l.setName("M_COVR");
                 });
                 layers.addAll(la);
                 }
                 */
                if (s.equals("BCNCAR.shp")) {
                    loader = new BCNCAR_ShapefileLoader();
                    tmp = new File(path + "/BCNCAR.shp");
                    List<Layer> la = loader.createLayersFromSource(tmp);
                    la.stream().forEach((l) -> {
                        l.setName("BCNCAR");
                    });
                    layers.addAll(la);

                    List<BeaconCardinal> beacons = ((BCNCAR_ShapefileLoader) loader).getBeacons();
                    beacons.stream().forEach((b) -> {
                        // System.out.println("b : " + b);
                    });

                }
                if (s.equals("BCNLAT.shp")) {
                    loader = new BCNLAT_ShapefileLoader();
                    tmp = new File(path + "/BCNLAT.shp");
                    List<Layer> la = loader.createLayersFromSource(tmp);
                    la.stream().forEach((l) -> {
                        l.setName("BCNLAT");
                    });
                    layers.addAll(la);

                }
                if (s.equals("BCNISD.shp")) {
                    loader = new BCNISD_ShapefileLoader();
                    tmp = new File(path + "/BCNISD.shp");
                    List<Layer> la = loader.createLayersFromSource(tmp);
                    la.stream().forEach((l) -> {
                        l.setName("BCNISD");
                    });
                    layers.addAll(la);
                }
                if (s.equals("M_NSYS.shp")) {
                    loader = new M_NSYS_ShapefileLoader();
                    tmp = new File(path + "/M_NSYS.shp");
                    List<Layer> la = loader.createLayersFromSource(tmp);
                    la.stream().forEach((l) -> {
                        l.setName("M_NSYS");
                    });
                    //layers.addAll(la);
                    // M_NSYS est utile pour la définition du systeme de ref.
                }
                if (s.equals("WRECKS.shp")) {
                    loader = new WRECKS_CNT_ShapefileLoader();
                    tmp = new File(path + "/WRECKS.shp");
                    List<Layer> la = loader.createLayersFromSource(tmp);
                    la.stream().forEach((l) -> {
                        l.setName("WRECKS");
                    });
                    layers.addAll(la);
                    loader = new WRECKS_ShapefileLoader();
                    tmp = new File(path + "/WRECKS.shp");
                    la = loader.createLayersFromSource(tmp);
                    la.stream().forEach((l) -> {
                        l.setName("WRECKS");
                    });
                    layers.addAll(la);
                }
                if (s.equals("OBSTRN.shp")) {
                    loader = new OBSTRN_CNT_ShapefileLoader();
                    tmp = new File(path + "/OBSTRN.shp");
                    List<Layer> la = loader.createLayersFromSource(tmp);
                    la.stream().forEach((l) -> {
                        l.setName("OBSTRN");
                    });
                    layers.addAll(la);

                    loader = new OBSTRN_ShapefileLoader();
                    tmp = new File(path + "/OBSTRN.shp");
                    la = loader.createLayersFromSource(tmp);
                    la.stream().forEach((l) -> {
                        l.setName("OBSTRN");
                    });
                    layers.addAll(la);
                }
                if (s.equals("UWTROC.shp")) {
                    loader = new UWTROC_ShapefileLoader();
                    tmp = new File(path + "/UWTROC.shp");
                    List<Layer> la = loader.createLayersFromSource(tmp);
                    la.stream().forEach((l) -> {
                        l.setName("UWTROC");
                    });
                    layers.addAll(la);
                }
                if (s.equals("LIGHTS.shp")) {

                    loader = new LIGHTS_ShapefileLoader();
                    tmp = new File(path + "/LIGHTS.shp");
                    loader.createLayersFromSource(tmp);
                    AirspaceLayer la = ((LIGHTS_ShapefileLoader) loader).getAirspaceLayer();
                    la.setName("LIGHTS");
                    airspaceLayers.add(la);

                    /*
                     loader = new PointTemplate_ShapefileLoader();
                     tmp = new File(path + "/LIGHTS.shp");
                     List<Layer> la = loader.createLayersFromSource(tmp);
                     la.stream().forEach((l) -> {
                     l.setName("LIGHTS");
                     });
                     layers.addAll(la);
                     */
                    wwd.addSelectListener((SelectEvent event) -> {
                        if (event.isLeftClick()
                                && event.getTopObject().getClass().getInterfaces()[0].equals(Lights.class)) {
                            System.out.println(Arrays.toString(((Lights)event.getTopObject()).getRadii()));
                        }
                    });
                }
                if (s.contains(".shp")) {
                    geos.put(s.replace(".shp", ""), new HashMap<>());
                }

            }
        }
    }

    public List<Layer> makeShapefileLayers(File f) {

//        loader = null;
//        fileDepare = null;
        //       File index = new File("data/shp");
//        for(File f: index.listFiles()) f.delete();
        return null;
    }

}
