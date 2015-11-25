package harborsimulator;

import com.vividsolutions.jts.geom.Geometry;

/**
 *
 */
public class Facility {

    /** Identifier */
    private String id;
    
    /** Associated geometry */
    private Geometry geometry;
    
    /** Navigation angle */
    private double minDistance;
    
    /** Description */
    private String description;
    
    /** Constructor 
      * @param id Facility identifier 
      * @param g  Geometry 
      * @param d  Security distance 
      * @param desc Area description */
    public Facility(String id, Geometry g, double d, String desc) {
        this.id        = id;
        this.geometry  = g;
        this.minDistance     = d;  
        this.description = desc;
    }
    
    /** Get area id 
      * @return Facility id */
    public String getId() {
        return id;
    }
    
    /** Get geometry method 
      * @return Facility JTS geometry */
    public Geometry getGeometry() {
        return geometry;
    }
    
    
    /** Get facility minimum distance 
      * @return Minimun distance to the facility */
    public double getMinDistance() {
        return minDistance;
    }
    
    /** Get area description 
      * @return Facility description */
    public String getDescription() {
        return description;
    }
}