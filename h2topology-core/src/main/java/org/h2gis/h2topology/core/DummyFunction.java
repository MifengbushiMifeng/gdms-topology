package org.h2gis.h2topology.core;

import com.vividsolutions.jts.geom.Geometry;
import org.h2gis.h2spatialapi.DeterministicScalarFunction;

/**
 * Function sample. Return the area of the parameter.
 *
 * To test this in the H2 web interface, do the following: <code>
 * CREATE ALIAS IF NOT EXISTS MY_FN FOR "org.h2gis.h2topology.core.DummyFunction.test";
 * CALL MY_FN('POLYGON((0 0,10 0,10 10,0 10,0 0))');</code>
 * The answer is 100.
 */
public class DummyFunction extends DeterministicScalarFunction {
    /**
     * Default constructor.
     */
    public DummyFunction() {
        addProperty(PROP_REMARKS, "Function sample, Return the area of the parameter.");
    }

    @Override
    public String getJavaStaticMethod() {
        return "test";
    }

   /**
    * Function sample. Return the same value as parameters.
    */
    public static double test(Geometry geometry) {
        return geometry.getArea();
    }
}
