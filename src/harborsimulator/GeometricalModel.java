/** Geometrical model definition */
package harborsimulator;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

public class GeometricalModel {
    
    /** Application */
    private HarborSimulator app;
    
    /** Ships */
    private ArrayList<Ship> ships;
    
    /** Areas */
    private ArrayList<Area> areas;
    
    /** Facilities */
    private ArrayList<Facility> facilities;
    
    /** Semantic model */
    private SemanticModel sm;
    
    /** Constructor */
    public GeometricalModel(SemanticModel sm, HarborSimulator app) {
        this.app = app;
        this.sm  = sm;
        
        // > Load areas
        facilities = sm.getFacilities();
        
        // > Load areas
        areas = sm.getAreas();
        
        // -- Inform when no area is defined
        if(areas.isEmpty()) {
            app.showMessage("No area was found in the ontology. Simulator will not be effective. ");
            app.log("No areas were defined");
            // @todo Load KML file and update ontology with area data
        }
        
        // > Load ships
        ships = sm.getShips();        
    }
    
    /** Update ship data -- Updates information in the GM and the SM according
     *  to method parameters (transient properties)
     *  @param time Relative time as specified in the file
     *  @param track_id Id of the new track to update the model with
     *  @param lat Latitude
     *  @param lng Longitude
     *  @param length Length of the ship
     *  @param speedLat Latitude of the speed vector
     *  @param speedLng Longitude of the speed vector
     *  @param speedMod Module of the speed vector
     *  @param length Ship lenght
     */
    public void updateModels(double time, String track_id,
            double lat, double lng, double length,
            double speedLat, double speedLng, double speedMod, double speedAng) {                
        
        Date d = new Date(sm.zeroTime.getTime() + (long) time);
        
        // > Find ship id in ships array
        Ship shipToUpdate = null;
        for(Ship s : ships) {
            if(s.getId().equals(track_id)) {
                shipToUpdate = s;
                break;
            }
        }
        
        // > Update ship data and insert into ship array of the GM
        if(shipToUpdate != null) {
            shipToUpdate.updateShipProperties(d, lat, lng, speedLat, speedLng, speedMod, speedAng, length);
            sm.updateShip(shipToUpdate, time, speedMod>0, this);
        } else {
            shipToUpdate = new Ship(track_id, d, lat, lng, speedLat, speedLng, speedMod, speedAng, length/*, 100*/);  // @todo Min distance is automatically set
            //app.getChart();
            shipToUpdate.initializeChart(app.getChart());
            ships.add(shipToUpdate);
            sm.createShip(shipToUpdate, time, speedMod>0, this);
        }
        
    }
    
    /** Retrieve vessel situation information */
    public void retrieveSituationInfo(ArrayList<ExpectedSituation> violation, ArrayList<ExpectedSituation> compliance, ArrayList<ExpectedSituation> unknown) {
        sm.retrieveVesselStates(violation, compliance, unknown);
    }
    
    /** Get facilities iterator */
    public Iterator<Facility> iteratorFacilities() {
        return facilities.iterator();
    }
    
    /** Get areas iterator */
    public Iterator<Area> iteratorAreas() {
        return areas.iterator();
    }
    
    /** Get ships iterator */
    public Iterator<Ship> iteratorShips() {
        return ships.iterator();
    }
    
    public Ship getShip(String id){
        Iterator<Ship> itShips = ships.iterator();
        Ship sh1 = null;
        boolean terminado=false;
        while(itShips.hasNext()&&!terminado){
            Ship sh2 = itShips.next();
            if(sh2.getId().equals(id)){
                sh1=sh2;
                terminado=true;
            }
        }
        return sh1;
    }
    
    public Facility getFacility(String id){
        Iterator<Facility> itFacilities = facilities.iterator();
        Facility f1 = null;
        boolean terminado=false;
        while(itFacilities.hasNext()&&!terminado){
            Facility f2 = itFacilities.next();
            if(f2.getId().equals(id)){
                f1=f2;
                terminado=true;
            }
        }
        return f1;
    }
    
    /** Dump semantic model */
    public void dump(File file) throws CannotWriteOntologyException {
        try {
            sm.dump(file);        
        } catch(OWLOntologyStorageException e) {
            throw new CannotWriteOntologyException(e.getMessage());
        }
    }
    
    public ArrayList<Ship> getShips(){
        return ships;
    }
}
