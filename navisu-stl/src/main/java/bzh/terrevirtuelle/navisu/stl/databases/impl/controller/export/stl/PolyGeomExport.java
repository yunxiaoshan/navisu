/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bzh.terrevirtuelle.navisu.stl.databases.impl.controller.export.stl;

import java.util.Map;

/**
 *
 * @author serge
 */
public interface PolyGeomExport {

    void export(String geometries, Map<String, String> labels);
}