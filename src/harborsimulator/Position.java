/** Position class (latitude and longitude) */
package harborsimulator;

import java.util.Date;

public class Position {

    /** Longitude */
    public double lat;
    
    /** Latitude */
    public double lon;
    
    /** Timestamp */
    public Date d;
    
    /** Constructor 
      * @param lat Latitude 
      * @param lon Longitude
      * @param date   Time value */
    public Position(double lat, double lon, Date date) {
        this.lat = lat;
        this.lon = lon;
        this.d   = date;
    }
    
    /** Copy constructor */
    public Position(Position p) {
        this(p.lat, p.lon, p.d);
    }
    
}
