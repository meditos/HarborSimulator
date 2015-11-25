/** Ship class definition -- correspondes to OWL class Ship */
package harborsimulator;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Objects;
import javafx.collections.ObservableList;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

public class Ship {
    /** Ship identifier */
    private String id;
    
    /** Ship position */
    private Position pos;
    
    /** Ship speed vector */
    private Position speedVector;   
    
    /** Ship speed vector (module) */
    private double modS;
    
    /** Ship speed vector (angle) */
    private double angS;   
    
    /** Ship length */
    private double length;
    
    /** Ship min allowed distance */
    //private double minDistance;
    
    /** Ship current geometry */
    private Geometry geometry;
    
    /** Ship trajectory */
    private ArrayList<Ship> trajectory;
    
    private XYChart.Series serie;
    
    private double[] cumulatedBeliefs;
    
    private double lastTime;
    
    /** Constructor 
      * @param id Ship identifier */
    public Ship(String id, Date date, double lat, double lon, double latS, double lonS, double modS, double angS, double length/*, double minDistance*/) {
        this.id = id;
        this.pos = new Position(lat, lon, date);
        this.speedVector = new Position(latS, lonS, date);
        this.modS = modS;
        this.angS = angS;
        this.length = length;
        //this.minDistance = minDistance;        
        this.geometry = new GeometryFactory().createPoint(new Coordinate(lon, lat));   
        this.trajectory = new ArrayList<>();
        
        this.serie= new XYChart.Series();
        this.serie.setName(id);
        this.serie.getData().add(new XYChart.Data(0,0));
        
        this.cumulatedBeliefs = new double[3];
        //Set the starting points.
        this.cumulatedBeliefs[0]=0;
        this.cumulatedBeliefs[1]=0;
        this.cumulatedBeliefs[2]=1;
        this.lastTime=0;
    } 
    
    /** Copy constructor */
    public Ship(Ship s) {
        this(s.id, s.pos.d, s.pos.lat, s.pos.lon, s.speedVector.lat, s.speedVector.lon, s.modS, s.angS, s.length/*, s.minDistance*/);
    }
    
    /** Update ship transient properties */
    public void updateShipProperties(Date date, double lat, double lon, double latS, double lonS, double modS, double angS, double length) {
        this.trajectory.add(new Ship(this));
        this.pos = new Position(lat, lon, date);
        this.speedVector = new Position(latS, lonS, date);
        this.modS = modS;
        this.angS = angS;
        this.length = length;
        this.geometry = new GeometryFactory().createPoint(new Coordinate(lon, lat));
    }
    
    public void updateBeliefs(double t, ArrayList<VesselSituation> violations, ArrayList<VesselSituation> compliance, ArrayList<VesselSituation> unknown){
        if(t>lastTime){
            double[][]matrixDS = new double[3][3];
            double[]situationBeliefs;
            //En cada paso de tiempo generamos incertidumbre. Movemos el 10% de la certeza a la incertidumbre.
            double cp=cumulatedBeliefs[0]/10;
            double ncp=cumulatedBeliefs[1]/10;
            
            cumulatedBeliefs[0]=cumulatedBeliefs[0]-cp;
            cumulatedBeliefs[1]=cumulatedBeliefs[1]-ncp;
            cumulatedBeliefs[2]=cumulatedBeliefs[2]+cp+ncp;
            //Recorrido de las violaciones de seguridad de los barcos.
            if(!shipWithoutViolations(violations)){
                for(VesselSituation sit : violations){ 
                    ArrayList<Double> beliefs=new ArrayList();
                    if(sit.id.equals(this.id)){
                    sit.calculateHeuristics(beliefs);
                        //Recorremos los valores devueltos por las heuristicas y los fusionamos.
                        for(Double d: beliefs){
                            situationBeliefs = new double[3];
                            situationBeliefs[0]=d;
                            situationBeliefs[2]=1-d;
                            for(int i = 0; i<situationBeliefs.length; i ++){
                                for(int j=0; j <cumulatedBeliefs.length; j++){
                                    matrixDS[i][j]= situationBeliefs[i]*cumulatedBeliefs[j];
                                }
                            }
                            double threat=matrixDS[0][0]+matrixDS[0][2]+matrixDS[2][0];
                            double notThreat=matrixDS[1][1]+matrixDS[1][2]+matrixDS[2][1];//En principio deberia ser siempre 0.
                            double both=matrixDS[2][2];
                            double normalizador = 1-(matrixDS[0][1]+matrixDS[1][0]);
                            //Sustituimos los cumulatedBeliefs
                            double threatNormalizado=threat/normalizador;
                            double notThreatNormalizado=notThreat/normalizador;
                            double bothNormalizado = both/normalizador;
                            cumulatedBeliefs[0]=threatNormalizado;
                            cumulatedBeliefs[1]=notThreatNormalizado;
                            cumulatedBeliefs[2]=bothNormalizado;
                            double suma = threatNormalizado+notThreatNormalizado+bothNormalizado;
                            System.out.println("SHIP: " + this.id);
                            System.out.println("ASCENSO NORMALIZADO - Threat: " + threatNormalizado + " Not threat: " + notThreatNormalizado + " BOTH: " + bothNormalizado);
                            System.out.println("Suma: " + suma);
                        }
                    }
                }
            }else if(shipWithoutViolations(violations)){/* && cumulatedBeliefs[0]>0*/
                situationBeliefs = new double[3];
                double d = 0.3;
                situationBeliefs[1]=d;
                situationBeliefs[2]=1-d;
                for(int i = 0; i<situationBeliefs.length; i ++){
                    for(int j=0; j <cumulatedBeliefs.length; j++){
                        matrixDS[i][j]= situationBeliefs[i]*cumulatedBeliefs[j];
                    }
                }
                double threat=matrixDS[0][0]+matrixDS[0][2]+matrixDS[2][0];
                double notThreat=matrixDS[1][1]+matrixDS[1][2]+matrixDS[2][1];//En principio deberia ser siempre 0.
                double both=matrixDS[2][2];
                double normalizador = 1-(matrixDS[0][1]+matrixDS[1][0]);
                //Sustituimos los cumulatedBeliefs
                double threatNormalizado=threat/normalizador;
                double notThreatNormalizado=notThreat/normalizador;
                double bothNormalizado = both/normalizador;
                cumulatedBeliefs[0]=threatNormalizado;
                cumulatedBeliefs[1]=notThreatNormalizado;
                cumulatedBeliefs[2]=bothNormalizado;
                double suma = threatNormalizado+notThreatNormalizado+bothNormalizado;
                System.out.println("SHIP: " + this.id);
                System.out.println("DESCENSO NORMALIZADO - Threat: " + threatNormalizado + " Not threat: " + notThreatNormalizado + " BOTH: " + bothNormalizado);
                System.out.println("Suma: " + suma);
            }
            updateChart(t, cumulatedBeliefs[0]);
            lastTime=t;
        }
    }
    
    public boolean shipWithoutViolations(ArrayList<VesselSituation>  violation){
        boolean withoutViolations=true;
        for(VesselSituation sit:violation){
            if(sit.id.equals(id)){
                ArrayList<SituationData> sd = sit.superClasses;
                if(sd.size()>0){
                    withoutViolations=false;
                }
            }
        }
        return withoutViolations;
    }
    
    /** Get area id 
      * @return Ship id */
    public String getId() {
        return id;
    }
    
    /** Get geometry method 
      * @return Ship JTS geometry */
    public Geometry getGeometry() {
        return geometry;
    }
    
    /** Get latitude method
      * @return Latitude value */
    public double getLat() {
        return pos.lat;
    }
    
    /** Get longitude method
      * @return Longitude value */
    public double getLon() {
        return pos.lon;
    }
    
    /** Get speed method
      * @return Speed module value */
    public double getModS() {
        return modS;
    }
    
    /** Get speed angle method
      * @return Speed module value */
    public double getAngS() {
        return angS;
    }
    
    /** Get latitude of the speed vector method
      * @return Latitude value of the speed vector */
    public double getLatS() {
        return speedVector.lat;
    }
    
    /** Get longitude of the speed vector method
      * @return Longitude value of the speed vector */
    public double getLonS() {
        return speedVector.lon;
    }
    
    /** Get length method
      * @return Length */
    public double getLength() {
        return length;
    }
    
    /** Get minimum allowed distance method
      * @return Length */
    public double getAllowedDistance() {
        return 100;
        //return minDistance;
    }
    
    public ArrayList<Ship> getTrajectory(){
        return trajectory;
    }
    
    public Position getPosition(){
        return pos;
    }
    
    /** Get date of current situation 
      *@return Date of current situation */
    public Date getDate() {
        return this.pos.d;
    }
    
    public XYChart.Series getSerie(){
        return serie;
    }
    
    /** Ship comparison method */
    @Override
    public boolean equals(Object other) {
        if(other == null) return false;
        if(other == this) return true;
        if(!(other instanceof Ship)) return false;
        
        Ship otherShip = (Ship) other;
        if(otherShip.id == null ? id == null : otherShip.id.equals(id))
            return true;
        else
            return false;
    }

    /** Autogenerated hashCode function */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.id);
        return hash;
    }
    
    public void initializeChart(AreaChart chart){
        chart.getData().add(serie);
    }
    
    public void updateChart(double time, double threatmass){
        this.serie.getData().add(new XYChart.Data(time,threatmass));
    }
}
