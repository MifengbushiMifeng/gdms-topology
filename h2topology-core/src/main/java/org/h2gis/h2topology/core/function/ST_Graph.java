/**
 * H2Topology is a library dedicated to graph analysis. It is based on the
 * JGraphT library available at <http://www.jgrapht.org/>. It enables computing
 * and processing large graphs using spatial and alphanumeric indexes.
 *
 * This version is developed at French IRSTV Institute as part of the EvalPDU
 * project, funded by the French Agence Nationale de la Recherche (ANR) under
 * contract ANR-08-VILL-0005-01 and GEBD project funded by the French Ministry
 * of Ecology and Sustainable Development.
 *
 * H2Topology is distributed under GPL 3 license. It is produced by the
 * "Atelier SIG" team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR
 * 2488.
 *
 * Copyright (C) 2009-2013 IRSTV (FR CNRS 2488)
 *
 * H2Topology is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * H2Topology is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * H2Topology. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://wwwc.orbisgis.org/> or contact
 * directly: info_at_orbisgis.org
 */
package org.h2gis.h2topology.core.function;

import org.h2gis.h2spatialapi.AbstractFunction;
import org.orbisgis.sputilities.SFSUtilities;
import org.orbisgis.sputilities.SpatialResultSet;

import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Constructs a mathematical graph based on the data contained in the input
 * table.
 * <p/>
 * CREATE ALIAS IF NOT EXISTS ST_GRAPH FOR "org.h2gis.h2topology.core.function.ST_Graph.test";
 *
 * @author Erwan Bocher
 * @author Adam Gouge
 */
public class ST_Graph extends AbstractFunction {

    public static void test(Connection connection, String inputTable) throws SQLException {

        Connection sConn = SFSUtilities.wrapConnection(connection);

        SpatialResultSet input = sConn.createStatement()
                .executeQuery("SELECT * FROM " + inputTable)
                .unwrap(SpatialResultSet.class);

        initNodesAndEdgesTables(sConn.createStatement(), input.getMetaData(), inputTable);

//        while (input.next()) {
//            // TODO: Make sure the table contains a geometry column.
//            Geometry geometry = input.getGeometry();
//            System.out.println(geometry.toString());
//        }
//        st.close();
    }

    private static void initNodesAndEdgesTables(
            Statement st,
            ResultSetMetaData metaData,
            String inputTable) throws SQLException {
        // TODO: Check if these tables already exist.
        // TODO: Make sure the tables do not contain columns with these names.

        // NODES TABLE
        st.execute("CREATE TABLE " + inputTable + "_nodes " +
                "(the_geom POINT NOT NULL, " +
                "id INTEGER NOT NULL)");

        // EDGES TABLE
        StringBuilder sb = new StringBuilder(1024);
        sb.append("CREATE TABLE " + inputTable + "_edges (");
        // Recover the old metadata
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            sb.append(metaData.getColumnLabel(i))
                    .append(" ")
                    .append(metaData.getColumnTypeName(i));
            int precision = metaData.getPrecision(i);
            if (precision != 0) {
                sb.append("( ").append(precision).append(" )");
            }
            sb.append(", ");
        }
        // Add the new metadata.
        sb.append("gid INTEGER NOT NULL, " +
                "start_node INTEGER NOT NULL, " +
                "end_node INTEGER NOT NULL)");
        st.execute(sb.toString());
    }
}
