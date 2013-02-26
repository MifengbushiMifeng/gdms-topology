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
package org.gdms.gdmstopology.centrality;

import com.graphhopper.storage.Graph;
import com.graphhopper.storage.GraphStorage;
import com.graphhopper.storage.RAMDirectory;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.schema.Metadata;
import org.gdms.driver.DataSet;
import org.gdms.driver.DriverException;
import org.gdms.gdmstopology.model.GraphException;
import org.gdms.gdmstopology.model.GraphSchema;
import org.orbisgis.progress.ProgressMonitor;

/**
 *
 * @author Adam Gouge
 */
public abstract class GraphCreator {

    /**
     * Used to parse the data set.
     */
    protected final DataSourceFactory dsf;
    /**
     * The data set.
     */
    protected final DataSet dataSet;
    /**
     * Progress monitor.
     */
    protected final ProgressMonitor pm;
    /**
     * Orientation.
     */
    protected final int orientation;
    /**
     * An error message given when a user inputs an erroneous graph orientation.
     */
    public final static String GRAPH_TYPE_ERROR =
            "Please enter an appropriate graph orientation (1, 2 or 3).";
    /**
     * Used to allocate enough space for the GraphHopper graph.
     */
    // TODO: How big does this need to be?
    protected final static int ALLOCATE_GRAPH_SPACE = 10;

    /**
     * Constructs a new {@link GraphCreator}.
     *
     * @param dsf         The {@link DataSourceFactory} used to parse the data
     *                    set.
     * @param dataSet     The data set.
     * @param pm          The progress monitor used to track the progress of the
     *                    calculation.
     * @param orientation The orientation.
     *
     */
    public GraphCreator(DataSourceFactory dsf,
                        DataSet dataSet,
                        ProgressMonitor pm,
                        int orientation) {
        this.dsf = dsf;
        this.dataSet = dataSet;
        this.pm = pm;
        this.orientation = orientation;
    }

    /**
     * Prepares the graph according to the given orientation.
     *
     * @param orientation The orientation.
     *
     * @return The newly prepared graph.
     *
     * @throws DriverException
     * @throws GraphException
     */
    protected Graph prepareGraph(int orientation)
            throws DriverException,
            GraphException {

        // DATASET INFORMATION
        // Recover the edge Metadata.
        // TODO: Add a check to make sure the metadata was loaded correctly.
        Metadata edgeMetadata = dataSet.getMetadata();

        // Recover the indices of the start node and end node.
        int startNodeIndex = edgeMetadata.getFieldIndex(GraphSchema.START_NODE);
        int endNodeIndex = edgeMetadata.getFieldIndex(GraphSchema.END_NODE);

        // If the weight column name is not null, then recover the weight
        // field index. Otherwise, set it to -1.
        String weightColumnName = getWeightColumnName();
        int weightFieldIndex =
                (weightColumnName == null)
                ? -1
                : edgeMetadata.getFieldIndex(weightColumnName);

        // GRAPH CREATION
        // Initialize the graph.
        GraphStorage graph = new GraphStorage(new RAMDirectory());
        graph.createNew(ALLOCATE_GRAPH_SPACE);
        // Add the edges according to the given graph type.
        loadEdges(graph, orientation,
                  startNodeIndex, endNodeIndex, weightFieldIndex);
        return graph;
    }

    /**
     * Returns the weight column name, or {@code null} for unweighted graphs.
     *
     * @return The weight column name.
     */
    protected abstract String getWeightColumnName();

    /**
     * Loads the graph edges with the appropriate orientation.
     *
     * @param graph            The graph.
     * @param orientation      The orientation.
     * @param startNodeIndex   The start node index.
     * @param endNodeIndex     The end node index.
     * @param weightFieldIndex The weight field index.
     *
     * @throws GraphException
     */
    private void loadEdges(Graph graph, int orientation,
                           int startNodeIndex,
                           int endNodeIndex,
                           int weightFieldIndex) throws
            GraphException {
        if (orientation == GraphSchema.DIRECT) {
            loadDirectedEdges(
                    graph, startNodeIndex, endNodeIndex, weightFieldIndex);
        } else if (orientation == GraphSchema.DIRECT_REVERSED) {
            loadReversedEdges(
                    graph, startNodeIndex, endNodeIndex, weightFieldIndex);
        } else if (orientation == GraphSchema.UNDIRECT) {
            loadUndirectedEdges(
                    graph, startNodeIndex, endNodeIndex, weightFieldIndex);
        } else {
            throw new GraphException(GRAPH_TYPE_ERROR);
        }
    }

    /**
     * Loads directed edges; the weight is decided by the implementation of this
     * method.
     *
     * @param graph            The graph.
     * @param startNodeIndex   The start node index.
     * @param endNodeIndex     The end node index.
     * @param weightFieldIndex The weight field index.
     */
    protected abstract void loadDirectedEdges(Graph graph,
                                              int startNodeIndex,
                                              int endNodeIndex,
                                              int weightFieldIndex);

    /**
     * Loads directed edges with orientations reversed.
     *
     * @param graph            The graph.
     * @param startNodeIndex   The start node index.
     * @param endNodeIndex     The end node index.
     * @param weightFieldIndex The weight field index.
     */
    private void loadReversedEdges(Graph graph,
                                   int startNodeIndex,
                                   int endNodeIndex,
                                   int weightFieldIndex) {
        loadDirectedEdges(graph, endNodeIndex, startNodeIndex, weightFieldIndex);
    }

    /**
     * Loads undirected edges; the weight is decided by the implementation of
     * this method.
     *
     * @param graph            The graph.
     * @param startNodeIndex   The start node index.
     * @param endNodeIndex     The end node index.
     * @param weightFieldIndex The weight field index.
     */
    protected abstract void loadUndirectedEdges(Graph graph,
                                                int startNodeIndex,
                                                int endNodeIndex,
                                                int weightFieldIndex);
}
