package harborsimulator;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.*;
import javafx.scene.chart.XYChart;
import org.semanticweb.owlapi.model.*;
import org.w3c.dom.*;


public class Process {
    /** Referal class (to print on the screen) */
    private HarborSimulator app;
    
    /** Input file reader */
    private Scanner in;   
    
    /** Geometrical model (includes areas and ships) */
    private GeometricalModel gm;
    
    /** Semantic model (ontology) */
    private SemanticModel sm;
    
    /** KML file text strings */
    ArrayList<String> kmlLines;
    
    /** DomXML object*/
    private DomXML xmlfile;
    
    /** DomXML object*/
    private TBMFile tbmfile;
    
    /** Constructor 
     *  @param file  Data file name         
     *  @param onto  Ontology file name  
     *  @param app   Application where painting will be done */
    public Process(IRI onto, HarborSimulator app) throws CannotCreateProcessException {
        this.app = app;
        
        // > Create semantic model
        try {
            sm = new SemanticModel(onto);        
        } catch(CannotSetupOntologyException e) {
            app.log(e.getMessage());
            throw new CannotCreateProcessException();
        }
        app.log("Ontology loaded and reasoner set");
                
        // > Create geometrical model
        gm = new GeometricalModel(sm, app);        
        app.log("Geometrical model is set");
        
        
        // > Clear areas
        app.clearAreas();       
        // > Paint areas
        Iterator<Area> it_a = gm.iteratorAreas();
        while(it_a.hasNext()) {
            Area a = it_a.next();
            Geometry g = a.getGeometry();
            // extract points to geometry @todo Encapsulate list of points
            double [] lat_array = new double[g.getNumPoints()];
            double [] lng_array = new double[g.getNumPoints()];
            Coordinate [] c_array = g.getCoordinates();
            
            for(int i=0; i<c_array.length; i++) {            
                lat_array[i] = c_array[i].y;                
                lng_array[i] = c_array[i].x;                            
            }
            
            app.paintArea(a.getId(), lat_array, lng_array, a.getDescription());  // @todo Extend information printed on the screen about the area
        }
        
        // > Clear facilities
        app.clearFacilities();       
        // > Paint facilities
        Iterator<Facility> it_f = gm.iteratorFacilities();
        while(it_f.hasNext()) {
            Facility f = it_f.next();
            Geometry g = f.getGeometry();
            // extract points to geometry @todo Encapsulate list of points
            double [] lat_array = new double[g.getNumPoints()];
            double [] lng_array = new double[g.getNumPoints()];
            Coordinate [] c_array = g.getCoordinates();
            
            for(int i=0; i<c_array.length; i++) {            
                lat_array[i] = c_array[i].y;                
                lng_array[i] = c_array[i].x;                            
            }
            app.paintFacility(f.getId(), lat_array, lng_array, f.getDescription());  // @todo Extend information printed on the screen about the area
        }
        
        // > Clear ships
        app.clearShips();
        // > Paint ships
        app.updatePoints(gm.iteratorShips(), 0);
//        Iterator<Ship> it_s = gm.iteratorShips();
//        while(it_s.hasNext()) {
//            Ship s = it_s.next();
//            app.paintPoint(s.getId(), 0, s.getLat(), s.getLon(), s.getModS()>0, s.getModS(), s.getLatS(), s.getLonS(), s.getLength());
//        }
        
        // > Initialize KML file
        kmlLines = new ArrayList<>();     
        
        // > Initialize XML file
        xmlfile = new DomXML();
        xmlfile.createRoot();
        
        // > Initialize TBM file
        tbmfile = new TBMFile();
    }
    
    /** Open data file 
        @param file Data file name */
    public void openDataFile(String file, HarborSimulator app) throws CannotCreateProcessException {
        // Load data file
        File is_data = new File(file);
        
        if(is_data == null)
            throw new CannotCreateProcessException("Cannot open data file. [" + file + "].");
        else {
            try {
                in = new Scanner(is_data);
            } catch(FileNotFoundException e) {
                throw new CannotCreateProcessException("Cannot open data file. [" + file + "].\n" + e.getMessage());
            }
            app.showMessage("Data file successfully opened!");
            app.log("Opened: " + file);
            this.app = app;
        }
    }
    
    /** Proceed reading the next data register */
    public void next(HarborSimulator app) throws BadDataFileFormatException {                        
        int line = -1;
        double t=-1;        // t will be interpreted as time in millis after epoch
        for(int i=0; i <4; i++){
            if(in.hasNext() ) {

                // >> Read data
                line++;

                String track_id="?";
                double lat=-1;
                double lng=-1;
                boolean hasSpeed=false;
                double speedLat=-1;
                double speedLng=-1;
                double speedMod=-1;
                double speedAng=-1;
                double length=-1;

                try {
                    track_id = "s"+in.next();                
                    t = in.nextDouble();
                    app.log("Processing [" + t + "]");
                    lat = in.nextDouble();
                    lng = in.nextDouble();
                    hasSpeed = in.nextInt()==1;
                    speedLat = in.nextDouble();
                    speedLng = in.nextDouble();
                    speedMod = in.nextDouble();
                    speedAng = in.nextDouble();
                    length   = in.nextDouble();
                } catch(InputMismatchException e) {
                    throw new BadDataFileFormatException("Bad data file format in line " + line + ".\n" + e.getMessage());
                }

                // >> Update GM and SM models
                gm.updateModels(t, track_id, lat, lng, length, speedLat, speedLng, speedMod, speedAng);
            }
        }                
        // >> Paint point
        //app.paintPoint(track_id, t, lat, lng, hasSpeed, speedMod, speedLat, speedLng, length);

        // >> Paint markers according to vessel situations
        // - Retrieve vessel states
        ArrayList<ExpectedSituation> 
                violation=new ArrayList<>(), 
                compliance=new ArrayList<>(), 
                unknown=new ArrayList<>();

        gm.retrieveSituationInfo(violation, compliance, unknown);                        

        // paint violation
        /*for(ExpectedSituation e: violation) {
            String uri = e.uri;
            String id = uri.toString().substring(uri.toString().indexOf('#') + 1, uri.toString().length() - 1);

            Iterator<Ship> it = gm.iteratorShips();
            while(it.hasNext()) {
                Ship s = it.next();

                if(s.getId().equals(id)) {
                    // Found ship that corresponds to situation e
                    app.paintMarker(s, 0);
                }
            }                                
        }

        // paint compliance
        for(ExpectedSituation e: compliance) {
            String uri = e.uri;
            String id = uri.toString().substring(uri.toString().indexOf('#') + 1, uri.toString().length() - 1);

            Iterator<Ship> it = gm.iteratorShips();
            while(it.hasNext()) {
                Ship s = it.next();

                if(s.getId().equals(id)) {
                    // Found ship that corresponds to situation e
                    app.paintMarker(s, 2);
                }
            }                                
        }

        // paint compliance and violation
        ArrayList<ExpectedSituation> compliance_violation = new ArrayList<>();
        for(ExpectedSituation v: violation) {
            for(ExpectedSituation c: compliance)
                if(v.uri.equals(c.uri))
                    compliance_violation.add(v);
        }
        for(ExpectedSituation e: compliance_violation) {
            String uri = e.uri;
            String id = uri.toString().substring(uri.toString().indexOf('#') + 1, uri.toString().length() - 1);

            Iterator<Ship> it = gm.iteratorShips();
            while(it.hasNext()) {
                Ship s = it.next();

                if(s.getId().equals(id)) {
                    // Found ship that corresponds to situation e
                    app.paintMarker(s, 1);
                }
            }                                
        }

        // paint unknown
        for(ExpectedSituation e: unknown) {
            String uri = e.uri;
            String id = uri.toString().substring(uri.toString().indexOf('#') + 1, uri.toString().length() - 1);

            Iterator<Ship> it = gm.iteratorShips();
            while(it.hasNext()) {
                Ship s = it.next();

                if(s.getId().equals(id)) {
                    // Found ship that corresponds to situation e
                    app.paintMarker(s, 3);
                }
            }                                
        }*/

        app.log("Violations: " + Arrays.toString(violation.toArray()));
        app.log("Compliance: " + Arrays.toString(compliance.toArray()));            
        app.log("Unknown: " + Arrays.toString(unknown.toArray()));  


        // >> Update KML file @todo: set different icons for each ship
        /* kmlLines.add(
                "<Placemark>"+
                "    <TimeStamp>" + 
                "      <when>" + getDateKML(t) + "</when>" + 
                "    </TimeStamp>" + 
                "    <Point>" +
                "      <coordinates>" + lng + "," + lat + "</coordinates>" +
                "    </Point>" +
                "</Placemark>"
            ); */

        // >> Update the list of ships in the canvas.
        app.updatePoints(gm.iteratorShips(), t);

        ArrayList<VesselSituation> 
                violation1=new ArrayList<>(), 
                compliance1=new ArrayList<>(), 
                unknown1=new ArrayList<>();
        sm.retrieveVesselSituation(violation1, compliance1, unknown1, gm);

        // >> Update the beliefs
        updateBeliefs(t,violation1, compliance1, unknown1);

        // >> Update XML file
        //updateXMLFile(t,violation1, compliance1, unknown1);

         // >> Update TBM file
        //updateTBMFile(t);

    }
    
    /* Get current date with proper format for KML file */
    public String getDateKML(double t) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
        return sdf.format(cal.getTime());   // @todo: use time value in the file
    }

    /** Run from the beginning */
    /* public void run() {
        while(in.hasNext()) {
            this.next();            
        }
    } */
    
    /** Dump ontology file */
    public void dump(File file) throws CannotWriteOntologyException {
        gm.dump(new File(file.getPath()+".owl"));
        xmlfile.saveXMLFile(new File(file.getPath()+".xml"));
        //tbmfile.saveTBMFile(new File(file.getPath()+".batch.txt"));
    }
    
    /** Save KML file */
    public void saveKML(String file) {
        return; // @implement
    }
    
    public void updateXMLFile(double t, ArrayList<VesselSituation> violation, ArrayList<VesselSituation> compliance, ArrayList<VesselSituation> unknown){
        //Se supone que las masas individuales se calculan solas a partir de las heuristicas de Gala.
        //Solo hay que implementar la regla de combinacion disjuntiva para cada barco.
        Element timestamp = xmlfile.appendElement(xmlfile.getRoot(), "step");
        timestamp.setAttribute("time", t+"");
        for(VesselSituation sit : violation){
            sit.printXML(xmlfile, timestamp);
        }
        for(VesselSituation sit : compliance){
            sit.printXML(xmlfile, timestamp);
        }
        for(VesselSituation sit : unknown){
            sit.printXML(xmlfile, timestamp);
        }
    }
    
    public void updateBeliefs(double t, ArrayList<VesselSituation> violation, ArrayList<VesselSituation> compliance, ArrayList<VesselSituation> unknown){
        //PREPROCESADO PARA ALINEAMIENTO Y VELOCIDAD.
        Iterator<Ship> itShs = gm.iteratorShips();
        while(itShs.hasNext()){
            Ship sh = itShs.next();
            String id = sh.getId();
            for(VesselSituation sit : compliance){
                if(sit.id.equals(id)){
                    //Para todos los barcos con una compatibilidad de alineamiento o velocidad.
                    ArrayList<SituationData> goodBehaviors = sit.superClasses;
                    for(SituationData sdgb:goodBehaviors){
                        switch (sdgb.behaviour) {
                            case "SpeedCompliance":
                                for(VesselSituation sitViolation : violation){
                                    if(sitViolation.id.equals(id)){
                                        for(int i = 0; i < sitViolation.superClasses.size(); i++){
                                            if(sitViolation.superClasses.get(i).behaviour.equals("SpeedViolation")){
                                                sitViolation.superClasses.remove(i);
                                            }
                                        }
                                    }
                                }
                                break;
                            case "NavigationDirectionCompliance":
                                for(VesselSituation sitViolation : violation){
                                    if(sitViolation.id.equals(id)){
                                        for(int i = 0; i < sitViolation.superClasses.size(); i++){
                                            if(sitViolation.superClasses.get(i).behaviour.equals("NavigationDirectionViolation")){
                                                sitViolation.superClasses.remove(i);
                                            }
                                        }
                                    }
                                }
                                break;
                        }
                    }
                }
            }
        }

        itShs = gm.iteratorShips();
        while(itShs.hasNext()){
            Ship sh = itShs.next();
            sh.updateBeliefs(t, violation, compliance, unknown);
        }
    }
    
    public void updateTBMFile(double t){
        //Deberia actualizar las probabilidades teniendo en cuenta la nueva situacion y la traduccion de las heuristicas.
        
    }
    
    public GeometricalModel getGeometricalModel(){
        return gm;
    }
}