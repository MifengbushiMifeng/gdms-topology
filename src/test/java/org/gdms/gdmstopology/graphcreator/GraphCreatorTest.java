/**
 * GDMS-Topology is a library dedicated to graph analysis. It is based on the
 * JGraphT library available at <http://www.jgrapht.org/>. It enables computing
 * and processing large graphs using spatial and alphanumeric indexes.
 *
 * This version is developed at French IRSTV Institute as part of the EvalPDU
 * project, funded by the French Agence Nationale de la Recherche (ANR) under
 * contract ANR-08-VILL-0005-01 and GEBD project funded by the French Ministry
 * of Ecology and Sustainable Development.
 *
 * GDMS-Topology is distributed under GPL 3 license. It is produced by the
 * "Atelier SIG" team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR
 * 2488.
 *
 * Copyright (C) 2009-2013 IRSTV (FR CNRS 2488)
 *
 * GDMS-Topology is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * GDMS-Topology is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * GDMS-Topology. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://wwwc.orbisgis.org/> or contact
 * directly: info_at_orbisgis.org
 */
package org.gdms.gdmstopology.graphcreator;

import com.graphhopper.sna.model.Edge;
import java.util.Iterator;
import java.util.Set;
import org.jgrapht.Graph;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.indexes.IndexException;
import org.gdms.driver.DriverException;
import org.gdms.gdmstopology.TopologySetupTest;
import org.gdms.gdmstopology.model.GraphSchema;
import org.gdms.sql.function.FunctionException;
import org.jgrapht.UndirectedGraph;

/**
 * Tests {@link GraphCreator}.
 *
 * @author Adam Gouge
 */
public abstract class GraphCreatorTest extends TopologySetupTest {

    /**
     * Instantiates an appropriate {@link GraphCreator} and prepares the graph.
     *
     * @param ds               The data source.
     * @param orientation      The orientation.
     * @param weightColumnName The weight column name.
     *
     * @throws NoSuchTableException
     * @throws DataSourceCreationException
     * @throws DriverException
     * @throws FunctionException
     */
    public void testGraphCreator(DataSource ds,
                                 int orientation,
                                 String weightColumnName,
                                 boolean printGraph)
            throws
            NoSuchTableException,
            DataSourceCreationException,
            DriverException,
            FunctionException,
            IndexException {

        GraphCreator creator = (weightColumnName == null)
                ? new UnweightedGraphCreator(ds, orientation)
                : new WeightedGraphCreator(ds, orientation, weightColumnName);
        Graph graph = creator.prepareGraph();

        if (printGraph) {
            printEdges(graph);
        }
    }

    /**
     * Indicates whether the graph should be printed.
     */
    protected abstract boolean printsGraph();

    /**
     * Prints all edges of the graph.
     *
     * @param graph The graph.
     */
    private void printEdges(Graph graph) {
        Set<Edge> edgeSet = graph.edgeSet();
        Iterator<Edge> iterator = edgeSet.iterator();
        while (iterator.hasNext()) {
            Edge edge = iterator.next();
            String edgeString = graph.getEdgeSource(edge).toString() + " ";
            if (graph instanceof UndirectedGraph) {
                edgeString += "<";
            }
            edgeString += "--> " + graph.getEdgeTarget(edge)
                    + " (" + graph.getEdgeWeight(edge) + ")";
            System.out.println(edgeString);
        }
        System.out.println("");
    }

    /**
     * Tests creating a directed graph.
     *
     * @param ds               The data source.
     * @param weightColumnName The weight column name.
     *
     * @throws NoSuchTableException
     * @throws DataSourceCreationException
     * @throws DriverException
     * @throws FunctionException
     */
    public void testGraphCreatorDirected(DataSource ds,
                                         String weightColumnName) throws
            NoSuchTableException,
            DataSourceCreationException,
            DriverException,
            FunctionException,
            IndexException {
        testGraphCreator(ds, GraphSchema.DIRECT, weightColumnName, printsGraph());
    }

    /**
     * Tests creating a directed graph with edges reversed.
     *
     * @param ds               The data source.
     * @param weightColumnName The weight column name.
     *
     * @throws NoSuchTableException
     * @throws DataSourceCreationException
     * @throws DriverException
     * @throws FunctionException
     */
    public void testGraphCreatorReversed(DataSource ds,
                                         String weightColumnName) throws
            NoSuchTableException,
            DataSourceCreationException,
            DriverException,
            FunctionException,
            IndexException {
        testGraphCreator(ds, GraphSchema.DIRECT_REVERSED, weightColumnName,
                         printsGraph());
    }

    /**
     * Tests creating an undirected graph.
     *
     * @param ds               The data source.
     * @param weightColumnName The weight column name.
     *
     * @throws NoSuchTableException
     * @throws DataSourceCreationException
     * @throws DriverException
     * @throws FunctionException
     */
    public void testGraphCreatorUndirected(DataSource ds,
                                           String weightColumnName) throws
            NoSuchTableException,
            DataSourceCreationException,
            DriverException,
            FunctionException,
            IndexException {
        testGraphCreator(ds, GraphSchema.UNDIRECT, weightColumnName,
                         printsGraph());
    }
}
