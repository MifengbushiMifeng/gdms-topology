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

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.indexes.IndexException;
import org.gdms.driver.DriverException;
import org.gdms.sql.function.FunctionException;
import org.junit.Test;

/**
 * Tests creating the 2D Graph under all possible configurations (weighted or
 * unweighted; directed, reversed or undirected).
 *
 * @author Adam Gouge
 */
public class Graph2DTest extends GraphCreatorTest {

    private static final String LENGTH = "length";
    private static final String NO_WEIGHT = null;

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean printsGraph() {
        return true;
    }

    @Test
    public void unweightedDirected() throws NoSuchTableException,
            DataSourceCreationException, DriverException, FunctionException,
            IndexException {
        System.out.println("\n***** 2D Unweighted Directed *****");
        testGraphCreatorDirected(get2DGraphDataSource(), NO_WEIGHT);
    }

    @Test
    public void unweightedReversed() throws NoSuchTableException,
            DataSourceCreationException, DriverException, FunctionException,
            IndexException {
        System.out.println("\n***** 2D Unweighted Reversed *****");
        testGraphCreatorReversed(get2DGraphDataSource(), NO_WEIGHT);
    }

    @Test
    public void unweightedUndirected() throws NoSuchTableException,
            DataSourceCreationException, DriverException, FunctionException,
            IndexException {
        System.out.println("\n***** 2D Unweighted Undirected *****");
        testGraphCreatorUndirected(get2DGraphDataSource(), NO_WEIGHT);
    }

    @Test
    public void weightedDirected() throws NoSuchTableException,
            DataSourceCreationException, DriverException, FunctionException,
            IndexException {
        System.out.println("\n***** 2D Weighted Directed *****");
        testGraphCreatorDirected(get2DGraphDataSource(), LENGTH);
    }

    @Test
    public void weightedReversed() throws NoSuchTableException,
            DataSourceCreationException, DriverException, FunctionException,
            IndexException {
        System.out.println("\n***** 2D Weighted Reversed *****");
        testGraphCreatorReversed(get2DGraphDataSource(), LENGTH);
    }

    @Test
    public void weightedUndirected() throws NoSuchTableException,
            DataSourceCreationException, DriverException, FunctionException,
            IndexException {
        System.out.println("\n***** 2D Weighted Undirected *****");
        testGraphCreatorUndirected(get2DGraphDataSource(), LENGTH);
    }

    /**
     * Gets the data source for the 2D graph.
     *
     * @return The data source for the 2D graph.
     *
     * @throws NoSuchTableException
     * @throws DataSourceCreationException
     * @throws DriverException
     */
    public DataSource get2DGraphDataSource() throws
            NoSuchTableException,
            DataSourceCreationException,
            DriverException {
        DataSource ds = dsf.getDataSource(GRAPH2D_EDGES);
        ds.open();
        return ds;
    }
}
