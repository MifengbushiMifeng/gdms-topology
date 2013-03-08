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
import org.gdms.data.values.Value;
import org.gdms.driver.DataSet;
import org.jgrapht.DirectedGraph;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.WeightedGraph;

/**
 * Creates a weighted graph with a specified orientation from the given
 * {@link DataSet}.
 *
 * @author Adam Gouge
 */
public class WeightedGraphCreator extends GraphCreator {

    /**
     * The name of the weight column.
     */
    private final String weightColumnName;

    /**
     * Constructs a new {@link WeightedGraphCreator}.
     *
     * @param dataSet     The data set.
     * @param orientation The orientation.
     *
     */
    public WeightedGraphCreator(DataSet dataSet, int orientation,
                                String weightColumnName) {
        super(dataSet, orientation);
        this.weightColumnName = weightColumnName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getWeightColumnName() {
        return weightColumnName;
    }

    /**
     * Loads weighted edges.
     *
     * @param scanner The scanner that will parse the csv file.
     * @param graph   The graph to which the edges will be added.
     * @param reverse {@code true} iff the edge orientation should be reversed.
     */
    private void loadWeightedEdges(WeightedGraph graph,
                                   int startNodeIndex,
                                   int endNodeIndex,
                                   int weightFieldIndex) {
        Iterator<Value[]> iterator = dataSet.iterator();
        while (iterator.hasNext()) {
            Value[] row = iterator.next();
            int startNode = row[startNodeIndex].getAsInt();
            int endNode = row[endNodeIndex].getAsInt();
            double weight = row[weightFieldIndex].getAsDouble();
            // Add the nodes to the graph.
            graph.addVertex(startNode);
            graph.addVertex(endNode);
            // Add the unweighted edge to the graph and set the edge weight.
            graph.setEdgeWeight(graph.addEdge(startNode, endNode), weight);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadDirectedEdges(DirectedGraph graph,
                                     int startNodeIndex,
                                     int endNodeIndex,
                                     int weightFieldIndex) {
        loadWeightedEdges((WeightedGraph) graph,
                          startNodeIndex, endNodeIndex, weightFieldIndex);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadUndirectedEdges(UndirectedGraph graph,
                                       int startNodeIndex,
                                       int endNodeIndex,
                                       int weightFieldIndex) {
        loadWeightedEdges((WeightedGraph) graph,
                          startNodeIndex, endNodeIndex, weightFieldIndex);
    }
}
