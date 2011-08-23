package org.gdms.gdmstopology.function;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.DimensionConstraint;
import org.gdms.data.types.GeometryConstraint;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DiskBufferDriver;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.gdmstopology.model.DWMultigraphDataSource;
import org.gdms.gdmstopology.model.GraphEdge;
import org.gdms.gdmstopology.model.GraphSchema;
import org.gdms.gdmstopology.model.WMultigraphDataSource;
import org.gdms.gdmstopology.process.GraphAnalysis;
import org.gdms.sql.customQuery.CustomQuery;
import org.gdms.sql.customQuery.TableDefinition;
import org.gdms.sql.function.Argument;
import org.gdms.sql.function.Arguments;
import org.orbisgis.progress.IProgressMonitor;

/**
 *
 * @author ebocher
 */
public class ST_ShortestPath implements CustomQuery {

        @Override
        public ObjectDriver evaluate(DataSourceFactory dsf, DataSource[] tables, Value[] values, IProgressMonitor pm) throws ExecutionException {
                int source = values[0].getAsInt();
                int target = values[1].getAsInt();

                SpatialDataSourceDecorator sdsEdges = new SpatialDataSourceDecorator(tables[0]);

                if (values.length == 3) {
                        if (values[2].getAsBoolean()) {
                                return computeWMPath(dsf, sdsEdges, source, target, pm);
                        } else {
                                return computeDWMPath(dsf, sdsEdges, source, target, pm);
                        }

                } else {
                        return computeDWMPath(dsf, sdsEdges, source, target, pm);
                }


        }

        @Override
        public String getName() {
                return "ST_ShortestPath";
        }

        @Override
        public String getDescription() {
                return "Return the shortest path beetwen two vertexes using the Dijkstra algorithm.";
        }

        @Override
        public String getSqlOrder() {
                return "SELECT ST_ShortestPath(12, 10 [,true]) from data;";
        }

        @Override
        public Metadata getMetadata(Metadata[] tables) throws DriverException {
                Metadata md = new DefaultMetadata(
                        new Type[]{TypeFactory.createType(Type.GEOMETRY), TypeFactory.createType(Type.INT),
                                TypeFactory.createType(Type.INT), TypeFactory.createType(Type.INT), TypeFactory.createType(Type.DOUBLE)},
                        new String[]{"the_geom", GraphSchema.ID, GraphSchema.START_NODE, GraphSchema.END_NODE, GraphSchema.WEIGTH});
                return md;
        }

        @Override
        public TableDefinition[] getTablesDefinitions() {
                return new TableDefinition[]{TableDefinition.GEOMETRY};
        }

        @Override
        public Arguments[] getFunctionArguments() {
                return new Arguments[]{new Arguments(Argument.INT, Argument.INT), new Arguments(Argument.INT, Argument.INT, Argument.BOOLEAN)};
        }

        private ObjectDriver computeDWMPath(DataSourceFactory dsf, SpatialDataSourceDecorator sds, Integer source, Integer target, IProgressMonitor pm) {
                DWMultigraphDataSource dWMultigraphDataSource = new DWMultigraphDataSource(sds, pm);
                try {
                        dWMultigraphDataSource.open();
                        List<GraphEdge> result = GraphAnalysis.getShortestPath(dWMultigraphDataSource, source, target);
                        if (result != null) {
                                DiskBufferDriver diskBufferDriver = new DiskBufferDriver(dsf, getMetadata(null));
                                int k = 0;
                                for (GraphEdge graphEdge : result) {
                                        Geometry geometry = dWMultigraphDataSource.getGeometry(graphEdge);
                                        diskBufferDriver.addValues(new Value[]{ValueFactory.createValue(geometry),
                                                        ValueFactory.createValue(k++),
                                                        ValueFactory.createValue(graphEdge.getSource()),
                                                        ValueFactory.createValue(graphEdge.getTarget()),
                                                        ValueFactory.createValue(graphEdge.getWeight())});
                                }
                                diskBufferDriver.writingFinished();
                                return diskBufferDriver;
                        }
                        dWMultigraphDataSource.close();

                } catch (DriverException ex) {
                        Logger.getLogger(ST_ShortestPath.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
        }

        private ObjectDriver computeWMPath(DataSourceFactory dsf, SpatialDataSourceDecorator sds, int source, int target, IProgressMonitor pm) {
                WMultigraphDataSource wMultigraphDataSource = new WMultigraphDataSource(sds, pm);
                try {
                        wMultigraphDataSource.open();
                        List<GraphEdge> result = GraphAnalysis.getShortestPath(wMultigraphDataSource, source, target);
                        if (result != null) {
                                DiskBufferDriver diskBufferDriver = new DiskBufferDriver(dsf, getMetadata(null));
                                int k = 0;
                                for (GraphEdge graphEdge : result) {
                                        Geometry geometry = wMultigraphDataSource.getGeometry(graphEdge);
                                        diskBufferDriver.addValues(new Value[]{ValueFactory.createValue(geometry),
                                                        ValueFactory.createValue(k++),
                                                        ValueFactory.createValue(graphEdge.getSource()),
                                                        ValueFactory.createValue(graphEdge.getTarget()),
                                                        ValueFactory.createValue(graphEdge.getWeight())});

                                }
                                diskBufferDriver.writingFinished();
                                return diskBufferDriver;
                        }
                        wMultigraphDataSource.close();

                } catch (DriverException ex) {
                        Logger.getLogger(ST_ShortestPath.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
        }
}