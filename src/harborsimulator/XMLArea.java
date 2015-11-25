package harborsimulator;


public class XMLArea {
    /** Identifier */
    public String id;
    
    /** Navigation angle */
    public double angle;
    
    /** Navigation module */
    public double module;
    
    public XMLArea(String id, double angle, double module){
        this.id=id;
        this.angle=angle;
        this.module=module;
    }
}
