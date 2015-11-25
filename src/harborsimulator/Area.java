/** Area class definition */
package harborsimulator;

import com.vividsolutions.jts.geom.Geometry;

public class Area {
      
    /** Identifier */
    private String id;
    
    /** Associated geometry */
    private Geometry geometry;
    
    /** Navigation angle */
    private double angle;
    
    /** Description */
    private String description;
    
    /** Constructor 
      * @param id Area identifier 
      * @param g  Geometry 
      * @param a  Navigation angle 
      * @param desc Area description */
    public Area(String id, Geometry g, double a, String desc) {
        this.id        = id;
        this.geometry  = g;
        this.angle     = a;  
        this.description = desc;
    }
    
    /** Get area id 
      * @return Area id */
    public String getId() {
        return id;
    }
    
    /** Get geometry method 
      * @return Area JTS geometry */
    public Geometry getGeometry() {
        return geometry;
    }
    
    
    /** Get area navigation angle 
      * @return Area navigation angle */
    public double getNavigationAngle() {
        return angle;
    }
    
    /** Get area description 
      * @return Area description */
    public String getDescription() {
        return description;
    }
}
