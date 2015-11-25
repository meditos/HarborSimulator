package harborsimulator;

import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import java.io.File;
import java.util.*;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

/*Actualiza toda la informacion relativa a las ontologias
 Esta clase nos va a servir para recuperar los estados de los Vessels
 retrieveVesselStates*/
public class SemanticModel {
    
    /** Ontology manager */
    private OWLOntologyManager manager;
    /** Ontology model */
    private OWLOntology ont;
    /** Harbor ontology base */
    private String harborOntologyPrefix = "http://giaa.inf.uc3m.es/harbor/hs/harbor.owl#";    
    /** Reasoner */
    private OWLReasoner reasoner;
    /** Reasoner configuration */
    private OWLReasonerConfiguration reasonerConfig;
    /** Reasoner factory */
    private OWLReasonerFactory reasonerFactory;    
    
    /** Ontology working parameters */
    OWLDataFactory dataFactory;
    private PrefixManager pm;
    private OWLClass            vesselClass;
    private OWLClass            largePowerDrivenVesselClass;
    private OWLClass            smallBoatClass;
    private OWLClass            speedClass;
    private OWLClass            positionClass;
    private OWLClass            distanceClass;
    private OWLClass            boatNumberClass;
    private OWLClass            lengthClass;
    private OWLObjectProperty   lengthProperty;
    private OWLDataProperty     lenProperty;
    private OWLObjectProperty   positionProperty;
    private OWLDataProperty     latProperty;
    private OWLDataProperty     lngProperty;
    private OWLObjectProperty   speedProperty;
    private OWLDataProperty     angProperty;
    private OWLDataProperty     modProperty;
    private OWLObjectProperty   allowedDistanceProperty;
    private OWLObjectProperty   possibleTowingDistanceProperty;
    private OWLObjectProperty   recommendedTowingBoatNumberProperty;
    private OWLObjectProperty   recommendedTowingDistanceProperty;
    private OWLObjectProperty   currentTowingBoatNumberProperty;
    private OWLDataProperty     dProperty;
    private OWLDataProperty     numProperty;  
    private OWLClass            areaClass;    
    private OWLObjectProperty   pointProperty;
    private OWLDataProperty     dirProperty;
    private OWLClass            facilityClass;
    private OWLAnnotationProperty commentProperty;                
    private OWLObjectProperty   insideOfProperty;
    private OWLObjectProperty   closeToProperty;
    private OWLObjectProperty   isTowedByProperty;
    private OWLObjectProperty   isWellTowedByProperty;
    private OWLObjectProperty   isBadTowedByProperty;
    private OWLObjectProperty   towingProperty;
    private OWLObjectProperty   wellTowingProperty;
    private OWLObjectProperty   badTowingProperty;
    private OWLObjectProperty   closeToFacilityProperty;
    private OWLObjectProperty   alignedToProperty;
    private OWLObjectProperty   nonAlignedToProperty;
    private OWLObjectProperty   tooCloseToTowProperty;
    private OWLObjectProperty   correctDistanceToTowProperty;
    private OWLObjectProperty   nonAlignedToTowProperty;
    private OWLObjectProperty   alignedToTowProperty;
    private OWLClass            restrictionViolation;
    private OWLClass            restrictionCompliance;

    
    public Date zeroTime = new Date();;
    
    public SemanticModel(IRI onto) throws CannotSetupOntologyException {                                    
        // >> Load ontology
        try {
            manager = OWLManager.createOWLOntologyManager();                        
                        
            // > Load ontology
            ont = manager.loadOntologyFromOntologyDocument(onto);
            
            // >> Initialize parameters
            dataFactory      = manager.getOWLDataFactory();
            pm               = new DefaultPrefixManager(harborOntologyPrefix);
            vesselClass      = dataFactory.getOWLClass(":Vessel", pm);
            largePowerDrivenVesselClass = dataFactory.getOWLClass(":LargePowerDrivenVessel", pm);
            smallBoatClass   = dataFactory.getOWLClass(":SmallBoat", pm);
            lengthProperty   = dataFactory.getOWLObjectProperty(":length", pm);            
            positionProperty = dataFactory.getOWLObjectProperty(":position", pm);            
            speedProperty    = dataFactory.getOWLObjectProperty(":speed", pm);
            allowedDistanceProperty = dataFactory.getOWLObjectProperty(":allowedDistance", pm);
            possibleTowingDistanceProperty = dataFactory.getOWLObjectProperty(":possibleTowingDistance", pm);
            speedClass       = dataFactory.getOWLClass(":Speed", pm);
            angProperty      = dataFactory.getOWLDataProperty(":ang", pm);
            modProperty      = dataFactory.getOWLDataProperty(":mod", pm);            
            latProperty      = dataFactory.getOWLDataProperty(":lat", pm);
            lngProperty      = dataFactory.getOWLDataProperty(":lng", pm);            
            positionClass    = dataFactory.getOWLClass(":Position", pm);            
            lengthClass      = dataFactory.getOWLClass(":Length", pm);
            lenProperty      = dataFactory.getOWLDataProperty(":len", pm);
            distanceClass    = dataFactory.getOWLClass(":Distance", pm);
            boatNumberClass = dataFactory.getOWLClass(":BoatNumber", pm);;
            recommendedTowingBoatNumberProperty = dataFactory.getOWLObjectProperty(":recommendedTowingBoatNumber", pm);
            currentTowingBoatNumberProperty = dataFactory.getOWLObjectProperty(":currentTowingBoatNumber", pm);
            recommendedTowingDistanceProperty = dataFactory.getOWLObjectProperty(":recommendedTowingDistance", pm);
            dProperty        = dataFactory.getOWLDataProperty(":d", pm);
            numProperty      = dataFactory.getOWLDataProperty(":num", pm);
            
            areaClass        = dataFactory.getOWLClass(":Area", pm);
            pointProperty    = dataFactory.getOWLObjectProperty(":delimitedBy", pm);
            dirProperty      = dataFactory.getOWLDataProperty(":dir", pm);
            
            facilityClass    = dataFactory.getOWLClass(":Facility", pm);
            
            commentProperty  = dataFactory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_COMMENT.getIRI());  
            
            insideOfProperty = dataFactory.getOWLObjectProperty(":insideOf", pm);
            closeToProperty  = dataFactory.getOWLObjectProperty(":closeTo", pm);
            isTowedByProperty  = dataFactory.getOWLObjectProperty(":isTowedBy", pm);
            isWellTowedByProperty  = dataFactory.getOWLObjectProperty(":isWellTowedBy", pm);
            isBadTowedByProperty  = dataFactory.getOWLObjectProperty(":isBadTowedBy", pm);
            towingProperty  = dataFactory.getOWLObjectProperty(":towing", pm);
            wellTowingProperty  = dataFactory.getOWLObjectProperty(":wellTowing", pm);
            badTowingProperty  = dataFactory.getOWLObjectProperty(":badTowing", pm);
            closeToFacilityProperty  = dataFactory.getOWLObjectProperty(":closeToFacility", pm);
            alignedToProperty = dataFactory.getOWLObjectProperty(":alignedTo", pm);
            nonAlignedToProperty = dataFactory.getOWLObjectProperty(":nonAlignedTo", pm);
            tooCloseToTowProperty = dataFactory.getOWLObjectProperty(":tooCloseToTow", pm);
            correctDistanceToTowProperty = dataFactory.getOWLObjectProperty(":correctDistanceToTow", pm);
            nonAlignedToTowProperty = dataFactory.getOWLObjectProperty(":nonAlignedToTow", pm);
            alignedToTowProperty = dataFactory.getOWLObjectProperty(":alignedToTow", pm);

            restrictionViolation = dataFactory.getOWLClass(":RestrictionViolation", pm);
            restrictionCompliance = dataFactory.getOWLClass(":RestrictionCompliance", pm);

        } catch(OWLOntologyCreationException e) {
            throw new CannotSetupOntologyException(e.getMessage());
        
        } catch(Exception e) {
            throw new CannotSetupOntologyException(e.getMessage());
        }         
        
        // >> Initial setup reasoner
        reasonerFactory = PelletReasonerFactory.getInstance();
        reasonerConfig = new SimpleConfiguration();        
        reasoner = reasonerFactory.createReasoner(ont, reasonerConfig);        
    }
    
    public ArrayList<Facility> getFacilities() {
        // >> Load facility definitions into geometrical model
        ArrayList<Facility> facilitiesArray = new ArrayList<>();
        
        // -- Retrieve instances of Facility class (class names are manually assigned)        
        Set<OWLNamedIndividual> facilities = reasoner.getInstances(facilityClass, false).getFlattened();
        
        // -- Load facility information
        for(OWLNamedIndividual facility : facilities) {
            // area values
            String name;
            double minDistance = -1;
            String description = "?";
            Geometry geom = null;
            // Assign name
            String id = facility.toString().substring(facility.toString().indexOf('#') + 1, facility.toString().length() - 1);            
            
            // allowed distance
            double adist=0;
            for (OWLIndividual a : facility.getObjectPropertyValues(allowedDistanceProperty, ont)){
                for (OWLLiteral d : a.getDataPropertyValues(dProperty, ont)){
                    adist = d.parseDouble();
                }
            }
            
            // Assign area description (by default, only one is considered)
            Set<OWLAnnotation> annotations = facility.getAnnotations(ont, commentProperty);
            if(annotations.isEmpty())                
                description = "No description available";
            else 
                for(OWLAnnotation ann : annotations) {
                    OWLLiteral val = (OWLLiteral) ann.getValue();
                    // if(val.hasLang("en")
                    if(val.getLiteral() != null)
                        description = val.getLiteral();                                
                }   
            
            // Assign area geometries
            Set<OWLIndividual> points = facility.getObjectPropertyValues(pointProperty, ont);          
            ArrayList<Double> lat_arraylist = new ArrayList<>();
            ArrayList<Double> lng_arraylist = new ArrayList<>();
            ArrayList<Coordinate> points_asCoordinates = new ArrayList<>();

            List<OWLIndividual> sorted_points = new ArrayList<>(points);
            java.util.Collections.sort(sorted_points);  // beware: points must be in alphabetical order! (see: https://mailman.stanford.edu/pipermail/protege-owl/2010-January/013039.html)
            
            for(OWLIndividual point: sorted_points) {
                double lat, lng;
                Set<OWLLiteral> lats = point.getDataPropertyValues(latProperty, ont);
                if(lats.size() > 0) {
                    ArrayList<OWLLiteral> lata = new ArrayList(lats);
                    lat = lata.get(0).parseDouble();
                } else {
                    continue;
                }
                
                Set<OWLLiteral> lngs = point.getDataPropertyValues(lngProperty, ont);
                if(lngs.size() > 0) {
                    ArrayList<OWLLiteral> lnga = new ArrayList(lngs);
                    lng = lnga.get(0).parseDouble();
                } else {
                    continue;
                }
                lat_arraylist.add(lat);
                lng_arraylist.add(lng);
                
                points_asCoordinates.add(new Coordinate(lng, lat));                
            }
                        
            if(sorted_points.size() >= 3) {
                GeometryFactory gf = new GeometryFactory();
                points_asCoordinates.add(new Coordinate(points_asCoordinates.get(0)));
                LinearRing lr = gf.createLinearRing(points_asCoordinates.toArray(new Coordinate[0]));
                Geometry facility_asGeometry = gf.createPolygon(lr, null);
                geom = facility_asGeometry;
             }
            
            // Create ship and add to array
            Facility f = new Facility(id, geom, adist, description);
            facilitiesArray.add(f);
        }
        return facilitiesArray;
    }
    
    public ArrayList<Area> getAreas() {
        // >> Load area definitions into geometrical model
        ArrayList<Area> areasArray = new ArrayList<>();
       
        // -- Retrieve instances of Area class (class names are manually assigned)        
        Set<OWLNamedIndividual> areas = reasoner.getInstances(areaClass, false).getFlattened();   
        
        // -- Load areas information
        for(OWLNamedIndividual area : areas) {
            
            // area values
            String name;
            double navigationAngle = -1;
            double speedLimit = -1;
            String description = "?";
            Geometry geom = null;
            
            // Assign name
            name = area.toString().substring(area.toString().indexOf('#') + 1, area.toString().length() - 1);
            
            // get area navigation direction (only one is considered)
            Set<OWLLiteral> navigationDirections = area.getDataPropertyValues(dirProperty, ont);
            for(OWLLiteral dir : navigationDirections) {                
                navigationAngle = dir.parseDouble();
            }
            
            // get area max navigation speed (only one is considered)
            Set<OWLLiteral> speedLimits = area.getDataPropertyValues(modProperty, ont);
            for(OWLLiteral dir : speedLimits) {                
                speedLimit = dir.parseDouble();
            }

            
            // Assign area description (by default, only one is considered)
            Set<OWLAnnotation> annotations = area.getAnnotations(ont, commentProperty);
            if(annotations.isEmpty())                
                description = "No description available";
            else 
                for(OWLAnnotation ann : annotations) {
                    OWLLiteral val = (OWLLiteral) ann.getValue();
                    // if(val.hasLang("en")
                    if(val.getLiteral() != null)
                        description = val.getLiteral();                                
                }   
            
            // Assign area geometries
            Set<OWLIndividual> points = area.getObjectPropertyValues(pointProperty, ont);          
            ArrayList<Double> lat_arraylist = new ArrayList<>();
            ArrayList<Double> lng_arraylist = new ArrayList<>();
            ArrayList<Coordinate> points_asCoordinates = new ArrayList<>();

            List<OWLIndividual> sorted_points = new ArrayList<>(points);
            java.util.Collections.sort(sorted_points);  // beware: points must be in alphabetical order! (see: https://mailman.stanford.edu/pipermail/protege-owl/2010-January/013039.html)
            
            for(OWLIndividual point: sorted_points) {
                double lat, lng;
                Set<OWLLiteral> lats = point.getDataPropertyValues(latProperty, ont);
                if(lats.size() > 0) {
                    ArrayList<OWLLiteral> lata = new ArrayList(lats);
                    lat = lata.get(0).parseDouble();
                } else {
                    continue;
                }
                
                Set<OWLLiteral> lngs = point.getDataPropertyValues(lngProperty, ont);
                if(lngs.size() > 0) {
                    ArrayList<OWLLiteral> lnga = new ArrayList(lngs);
                    lng = lnga.get(0).parseDouble();
                } else {
                    continue;
                }
                lat_arraylist.add(lat);
                lng_arraylist.add(lng);
                
                points_asCoordinates.add(new Coordinate(lng, lat));                
            }
                        
            if(sorted_points.size() >= 3) {
                GeometryFactory gf = new GeometryFactory();
                points_asCoordinates.add(new Coordinate(points_asCoordinates.get(0)));
                LinearRing lr = gf.createLinearRing(points_asCoordinates.toArray(new Coordinate[0]));
                Geometry area_asGeometry = gf.createPolygon(lr, null);
                geom = area_asGeometry;
             }
            
             // Create area
             Area a = new Area(name, geom, navigationAngle, description);
             
             // Push area into array
             areasArray.add(a);             
        }
        
        return areasArray;        
    }
    
    public ArrayList<Ship> getShips() {
        // >> Load ship definitions into geometrical model
        ArrayList<Ship> shipsArray = new ArrayList<>();
       
        // -- Retrieve instances of Ship class (class names are manually assigned)        
        Set<OWLNamedIndividual> ships = reasoner.getInstances(vesselClass, false).getFlattened();   
        
        // -- Load ships information
        for(OWLNamedIndividual v : ships) {
                        
            // Assign name
            String id = v.toString().substring(v.toString().indexOf('#') + 1, v.toString().length() - 1);            
            
            // length
            double l = 0;
            for (OWLIndividual a : v.getObjectPropertyValues(lengthProperty, ont)) {
                for (OWLLiteral d : a.getDataPropertyValues(lenProperty, ont)) 
                    l = d.parseDouble();
            }
            
            // position
            double lat=0, lon=0;
            for (OWLIndividual a : v.getObjectPropertyValues(positionProperty, ont)) {
                for (OWLLiteral d : a.getDataPropertyValues(latProperty, ont)) 
                    lat = d.parseDouble();
                for (OWLLiteral d : a.getDataPropertyValues(lngProperty, ont)) 
                    lon = d.parseDouble();
            }
            
            // speed
            double ang=0, mod=0, latS=0, lonS=0;
            for (OWLIndividual a : v.getObjectPropertyValues(speedProperty, ont)) {
                for (OWLLiteral d : a.getDataPropertyValues(latProperty, ont)) 
                    latS = d.parseDouble();
                for (OWLLiteral d : a.getDataPropertyValues(lngProperty, ont)) 
                    lonS = d.parseDouble();
                for (OWLLiteral d : a.getDataPropertyValues(angProperty, ont)) 
                    ang = d.parseDouble();
                for (OWLLiteral d : a.getDataPropertyValues(modProperty, ont)) 
                    mod = d.parseDouble();
            }
            
            // allowed distance
            double adist=0;
            for (OWLIndividual a : v.getObjectPropertyValues(allowedDistanceProperty, ont)) {
                for (OWLLiteral d : a.getDataPropertyValues(dProperty, ont)) 
                    adist = d.parseDouble();
            }
            
            // Create ship and add to array
            Ship s = new Ship(id, zeroTime, lat, lon, latS, lonS, mod, ang, l/*, adist*/);
            shipsArray.add(s);
        }
        
        return shipsArray;
    }
    
    /** Create ship. */
    public void createShip(Ship s, double time, boolean hasSpeed, GeometricalModel gm) {
        //assignDefaultShipValues(s, time);  
        assignShipValues(s,time,hasSpeed);
        updateGeometricalRelationships(s,time,hasSpeed,gm);
    }
    
    /** Update ship. */
    public void updateShip(Ship s, double time, boolean hasSpeed, GeometricalModel gm) {
        assignShipValues(s,time,hasSpeed);
        updateGeometricalRelationships(s,time,hasSpeed,gm);
    }
    
    private void assignDefaultShipValues(Ship s, double time){
        //Assign values in the individuals of the ontology when they are created.
        //OWLNamedIndividual v = dataFactory.getOWLNamedIndividual(":" + s.getId(), pm);
        
        
    }
    
    private void assignShipValues(Ship s, double time, boolean hasSpeed){
        // >> Update ontology instances (remove + insert)
        // and transient properties 
        OWLNamedIndividual v = dataFactory.getOWLNamedIndividual(":" + s.getId(), pm); 
        
        // -- Insert class assertion
        OWLClassAssertionAxiom axiom_vessel = dataFactory.getOWLClassAssertionAxiom(vesselClass, v);
        manager.addAxiom(ont, axiom_vessel);        
                
        if(hasSpeed) {
            for (OWLIndividual a : v.getObjectPropertyValues(speedProperty, ont)) {                               
                for (OWLLiteral d : a.getDataPropertyValues(latProperty, ont)) {
                    OWLDataPropertyAssertionAxiom axiom_lat = dataFactory.getOWLDataPropertyAssertionAxiom(latProperty, a, d);
                    manager.removeAxiom(ont, axiom_lat);
                }
                for (OWLLiteral d : a.getDataPropertyValues(lngProperty, ont)) {
                    OWLDataPropertyAssertionAxiom axiom_lng = dataFactory.getOWLDataPropertyAssertionAxiom(lngProperty, a, d);
                    manager.removeAxiom(ont, axiom_lng);
                }
                for (OWLLiteral d : a.getDataPropertyValues(modProperty, ont)) {
                    OWLDataPropertyAssertionAxiom axiom_mod = dataFactory.getOWLDataPropertyAssertionAxiom(modProperty, a, d);
                    manager.removeAxiom(ont, axiom_mod);
                }
                for (OWLLiteral d : a.getDataPropertyValues(angProperty, ont)) {
                    OWLDataPropertyAssertionAxiom axiom_ang = dataFactory.getOWLDataPropertyAssertionAxiom(angProperty, a, d);
                    manager.removeAxiom(ont, axiom_ang);
                }

                OWLObjectPropertyAssertionAxiom axiom_speed = dataFactory.getOWLObjectPropertyAssertionAxiom(speedProperty, v, a);
                manager.removeAxiom(ont, axiom_speed);

                OWLClassAssertionAxiom axiom_isSpeed = dataFactory.getOWLClassAssertionAxiom(speedClass, a);
                manager.removeAxiom(ont, axiom_isSpeed);

                OWLDeclarationAxiom axiom_speed_dec = dataFactory.getOWLDeclarationAxiom(a.asOWLNamedIndividual());
                manager.removeAxiom(ont, axiom_speed_dec);
            }   // remove speed instance and associated axioms
        }

        for (OWLIndividual a : v.getObjectPropertyValues(positionProperty, ont)) {
            for (OWLLiteral d : a.getDataPropertyValues(latProperty, ont)) {
                OWLDataPropertyAssertionAxiom axiom_lat = dataFactory.getOWLDataPropertyAssertionAxiom(latProperty, a, d);
                manager.removeAxiom(ont, axiom_lat);
            }
            for (OWLLiteral d : a.getDataPropertyValues(lngProperty, ont)) {
                OWLDataPropertyAssertionAxiom axiom_lng = dataFactory.getOWLDataPropertyAssertionAxiom(lngProperty, a, d);
                manager.removeAxiom(ont, axiom_lng);
            }

            OWLObjectPropertyAssertionAxiom axiom_position = dataFactory.getOWLObjectPropertyAssertionAxiom(positionProperty, v, a);
            manager.removeAxiom(ont, axiom_position);

            OWLClassAssertionAxiom axiom_isPos = dataFactory.getOWLClassAssertionAxiom(positionClass, a);
            manager.removeAxiom(ont, axiom_isPos);

            OWLDeclarationAxiom axiom_position_dec = dataFactory.getOWLDeclarationAxiom(a.asOWLNamedIndividual());
            manager.removeAxiom(ont, axiom_position_dec);
        }   // remove position instance and associated axioms

        for (OWLIndividual a : v.getObjectPropertyValues(lengthProperty, ont)) {
            for (OWLLiteral d : a.getDataPropertyValues(lenProperty, ont)) {
                OWLDataPropertyAssertionAxiom axiom_len = dataFactory.getOWLDataPropertyAssertionAxiom(lenProperty, a, d);
                manager.removeAxiom(ont, axiom_len);
            }

            OWLObjectPropertyAssertionAxiom axiom_length = dataFactory.getOWLObjectPropertyAssertionAxiom(lengthProperty, v, a);
            manager.removeAxiom(ont, axiom_length);

            OWLClassAssertionAxiom axiom_isLen = dataFactory.getOWLClassAssertionAxiom(lengthClass, a);
            manager.removeAxiom(ont, axiom_isLen);

            OWLDeclarationAxiom axiom_length_dec = dataFactory.getOWLDeclarationAxiom(a.asOWLNamedIndividual());
            manager.removeAxiom(ont, axiom_length_dec);
        }   // remove length instance and associated axioms
        
        // -- PossibleTowingDistance
        for (OWLIndividual a : v.getObjectPropertyValues(possibleTowingDistanceProperty, ont)) {
            for (OWLLiteral d : a.getDataPropertyValues(dProperty, ont)) {
                OWLDataPropertyAssertionAxiom axiom_dist = dataFactory.getOWLDataPropertyAssertionAxiom(dProperty, a, d);
                manager.removeAxiom(ont, axiom_dist);
            }
            OWLObjectPropertyAssertionAxiom axiom_p_distance = dataFactory.getOWLObjectPropertyAssertionAxiom(possibleTowingDistanceProperty, v, a);
            manager.removeAxiom(ont, axiom_p_distance);

            OWLClassAssertionAxiom axiom_isDist = dataFactory.getOWLClassAssertionAxiom(distanceClass, a);
            manager.removeAxiom(ont, axiom_isDist);

            OWLDeclarationAxiom axiom_dist_dec = dataFactory.getOWLDeclarationAxiom(a.asOWLNamedIndividual());
            manager.removeAxiom(ont, axiom_dist_dec);
        }
        
        // -- RecommendedTowingBoatNumber
        for (OWLIndividual a : v.getObjectPropertyValues(recommendedTowingBoatNumberProperty, ont)) {
            for (OWLLiteral d : a.getDataPropertyValues(numProperty, ont)) {
                OWLDataPropertyAssertionAxiom axiom_num = dataFactory.getOWLDataPropertyAssertionAxiom(numProperty, a, d);
                manager.removeAxiom(ont, axiom_num);
            }
            OWLObjectPropertyAssertionAxiom axiom_b_number = dataFactory.getOWLObjectPropertyAssertionAxiom(recommendedTowingBoatNumberProperty, v, a);
            manager.removeAxiom(ont, axiom_b_number);

            OWLClassAssertionAxiom axiom_isNum = dataFactory.getOWLClassAssertionAxiom(boatNumberClass, a);
            manager.removeAxiom(ont, axiom_isNum);

            OWLDeclarationAxiom axiom_num_dec = dataFactory.getOWLDeclarationAxiom(a.asOWLNamedIndividual());
            manager.removeAxiom(ont, axiom_num_dec);
        }

        // -- RecommendedTowingDistance
        for (OWLIndividual a : v.getObjectPropertyValues(recommendedTowingDistanceProperty, ont)) {
            for (OWLLiteral d : a.getDataPropertyValues(dProperty, ont)) {
                OWLDataPropertyAssertionAxiom axiom_num = dataFactory.getOWLDataPropertyAssertionAxiom(dProperty, a, d);
                manager.removeAxiom(ont, axiom_num);
            }
            OWLObjectPropertyAssertionAxiom axiom_rt_distance = dataFactory.getOWLObjectPropertyAssertionAxiom(recommendedTowingDistanceProperty, v, a);
            manager.removeAxiom(ont, axiom_rt_distance);

            OWLClassAssertionAxiom axiom_isDist = dataFactory.getOWLClassAssertionAxiom(distanceClass, a);
            manager.removeAxiom(ont, axiom_isDist);

            OWLDeclarationAxiom axiom_dist_dec = dataFactory.getOWLDeclarationAxiom(a.asOWLNamedIndividual());
            manager.removeAxiom(ont, axiom_dist_dec);
        }

        
        // min allowed distance is not removed because it is a transient property

        // -- Insert speed (lat, lng, module, angle, length)
        if (hasSpeed) {
            OWLNamedIndividual newSpeed = dataFactory.getOWLNamedIndividual(":" + s.getId() + "_s_" + time, pm);
            OWLDataPropertyAssertionAxiom axiom_slat = dataFactory.getOWLDataPropertyAssertionAxiom(latProperty, newSpeed, s.getLatS());
            OWLDataPropertyAssertionAxiom axiom_slng = dataFactory.getOWLDataPropertyAssertionAxiom(lngProperty, newSpeed, s.getLonS());
            OWLDataPropertyAssertionAxiom axiom_smod = dataFactory.getOWLDataPropertyAssertionAxiom(modProperty, newSpeed, s.getModS());
            OWLDataPropertyAssertionAxiom axiom_sang = dataFactory.getOWLDataPropertyAssertionAxiom(angProperty, newSpeed, s.getAngS());
            OWLClassAssertionAxiom axiom_isSpeed = dataFactory.getOWLClassAssertionAxiom(speedClass, newSpeed);
            OWLObjectPropertyAssertionAxiom axiom_speed = dataFactory.getOWLObjectPropertyAssertionAxiom(speedProperty, v, newSpeed);

            manager.addAxiom(ont, axiom_slat);
            manager.addAxiom(ont, axiom_slng);
            manager.addAxiom(ont, axiom_smod);
            manager.addAxiom(ont, axiom_sang);
            manager.addAxiom(ont, axiom_isSpeed);
            manager.addAxiom(ont, axiom_speed);
        }
            
        // -- Insert position (lat and lng)
        OWLNamedIndividual newPos = dataFactory.getOWLNamedIndividual(":" + s.getId() + "_p_" + time, pm);
        OWLDataPropertyAssertionAxiom axiom_plat = dataFactory.getOWLDataPropertyAssertionAxiom(latProperty, newPos, s.getLat());
        OWLDataPropertyAssertionAxiom axiom_plng = dataFactory.getOWLDataPropertyAssertionAxiom(lngProperty, newPos, s.getLon());
        OWLClassAssertionAxiom axiom_isPos = dataFactory.getOWLClassAssertionAxiom(positionClass, newPos);
        OWLObjectPropertyAssertionAxiom axiom_pos = dataFactory.getOWLObjectPropertyAssertionAxiom(positionProperty, v, newPos);                

        manager.addAxiom(ont, axiom_plat);
        manager.addAxiom(ont, axiom_plng);
        manager.addAxiom(ont, axiom_isPos);
        manager.addAxiom(ont, axiom_pos);        

        // -- Insert length (len)
        OWLNamedIndividual newLen = dataFactory.getOWLNamedIndividual(":" + s.getId() + "_l_" + time, pm);
        OWLDataPropertyAssertionAxiom axiom_llen = dataFactory.getOWLDataPropertyAssertionAxiom(lenProperty, newLen, s.getLength());
        OWLClassAssertionAxiom axiom_isLen = dataFactory.getOWLClassAssertionAxiom(lengthClass, newLen);
        OWLObjectPropertyAssertionAxiom axiom_len = dataFactory.getOWLObjectPropertyAssertionAxiom(lengthProperty, v, newLen);

        manager.addAxiom(ont, axiom_llen);
        manager.addAxiom(ont, axiom_isLen);
        manager.addAxiom(ont, axiom_len);
        
        //Esta comparacion ralentiza la ejecucion. Se puede sustituir por s.getLength()>=170
        if(/*belongsToClass(v,largePowerDrivenVesselClass)*/s.getLength()>=170){ 
            // -- PossibleTowingDistance
            OWLNamedIndividual newPtd = dataFactory.getOWLNamedIndividual(":" + s.getId() + "_ptd_" + time, pm);    
            OWLDataPropertyAssertionAxiom axiom_ptd_distance = dataFactory.getOWLDataPropertyAssertionAxiom(dProperty, newPtd, 30.0);
            OWLClassAssertionAxiom axiom_isPtd = dataFactory.getOWLClassAssertionAxiom(distanceClass, newPtd);
            OWLObjectPropertyAssertionAxiom axiom_ptd = dataFactory.getOWLObjectPropertyAssertionAxiom(possibleTowingDistanceProperty, v, newPtd);
            manager.addAxiom(ont, axiom_ptd_distance);
            manager.addAxiom(ont, axiom_isPtd);
            manager.addAxiom(ont, axiom_ptd);

            // -- RecommendedTowingBoatNumber
            OWLNamedIndividual newRtbn = dataFactory.getOWLNamedIndividual(":" + s.getId() + "_rtbn_" + time, pm);
            OWLDataPropertyAssertionAxiom axiom_rtbn_number = dataFactory.getOWLDataPropertyAssertionAxiom(numProperty, newRtbn, 2);
            OWLClassAssertionAxiom axiom_isRtbn = dataFactory.getOWLClassAssertionAxiom(boatNumberClass, newRtbn);
            OWLObjectPropertyAssertionAxiom axiom_rtbn = dataFactory.getOWLObjectPropertyAssertionAxiom(recommendedTowingBoatNumberProperty, v, newRtbn);
            manager.addAxiom(ont, axiom_rtbn_number);
            manager.addAxiom(ont, axiom_isRtbn);
            manager.addAxiom(ont, axiom_rtbn);
            
            // -- RecommendedTowingDistance
            OWLNamedIndividual newRtd = dataFactory.getOWLNamedIndividual(":" + s.getId() + "_rtd_" + time, pm);
            OWLDataPropertyAssertionAxiom axiom_rtd_number = dataFactory.getOWLDataPropertyAssertionAxiom(dProperty, newRtd, 15.0);
            OWLClassAssertionAxiom axiom_isRtd = dataFactory.getOWLClassAssertionAxiom(distanceClass, newRtd);
            OWLObjectPropertyAssertionAxiom axiom_rtd = dataFactory.getOWLObjectPropertyAssertionAxiom(recommendedTowingDistanceProperty, v, newRtd);
            manager.addAxiom(ont, axiom_rtd_number);
            manager.addAxiom(ont, axiom_isRtd);
            manager.addAxiom(ont, axiom_rtd);
        }
    }
    
    public void updateGeometricalRelationships(Ship s, double time, boolean hasSpeed, GeometricalModel gm){ 
        // >> Update ontology geometrical and topological relations
        
        OWLNamedIndividual v = dataFactory.getOWLNamedIndividual(":" + s.getId(), pm); 
        Geometry p = s.getGeometry();
        
        // insideOf
        Iterator<Area> it_a = gm.iteratorAreas();
        while (it_a.hasNext()) {
            Area area = it_a.next();
            Geometry ga = area.getGeometry();
            String area_id = area.getId();
            if (ga.covers(p)) {
                OWLNamedIndividual a = dataFactory.getOWLNamedIndividual(":" + area_id, pm);
                OWLObjectPropertyAssertionAxiom axiom_insideOf = dataFactory.getOWLObjectPropertyAssertionAxiom(insideOfProperty, v, a);
                manager.addAxiom(ont, axiom_insideOf);
                // alignedTo
                if(hasSpeed){
                    double area_angle = area.getNavigationAngle();
                    double diff_angle = Math.abs(area_angle - s.getAngS());
                    if (diff_angle < 10) {  // @todo: change to have a difference value per area
                        //Create alignedTo axioms.
                        OWLObjectPropertyAssertionAxiom axiom_alignedTo = dataFactory.getOWLObjectPropertyAssertionAxiom(alignedToProperty, v, a);
                        manager.addAxiom(ont, axiom_alignedTo);
                        //Delete nonAlignedTo axioms.
                        OWLObjectPropertyAssertionAxiom axiom_nonAlignedTo = dataFactory.getOWLObjectPropertyAssertionAxiom(nonAlignedToProperty, v, a);
                        manager.removeAxiom(ont, axiom_nonAlignedTo);
                    } else {
                        //Create nonAlignedTo axioms.
                        OWLObjectPropertyAssertionAxiom axiom_nonAlignedTo = dataFactory.getOWLObjectPropertyAssertionAxiom(nonAlignedToProperty, v, a);
                        manager.addAxiom(ont, axiom_nonAlignedTo);
                        //Delete alignedTo axioms.
                        OWLObjectPropertyAssertionAxiom axiom_alignedTo = dataFactory.getOWLObjectPropertyAssertionAxiom(alignedToProperty, v, a);
                        manager.removeAxiom(ont, axiom_alignedTo);
                    }
                }
            } else {
                OWLNamedIndividual a = dataFactory.getOWLNamedIndividual(":" + area_id, pm);
                OWLObjectPropertyAssertionAxiom axiom_insideOf = dataFactory.getOWLObjectPropertyAssertionAxiom(insideOfProperty, v, a);
                manager.removeAxiom(ont, axiom_insideOf);
                //Delete alignedTo axioms.
                OWLObjectPropertyAssertionAxiom axiom_alignedTo = dataFactory.getOWLObjectPropertyAssertionAxiom(alignedToProperty, v, a);
                manager.removeAxiom(ont, axiom_alignedTo);
                //Delete nonAlignedTo axioms.
                OWLObjectPropertyAssertionAxiom axiom_nonAlignedTo = dataFactory.getOWLObjectPropertyAssertionAxiom(nonAlignedToProperty, v, a);
                manager.removeAxiom(ont, axiom_nonAlignedTo);
            }
        }
        
        // Several analysis possible towing boats, number of towing boats, wellTowing, badTowing.
        int currentTowingBoats=0;
        Iterator<Ship> it_s = gm.iteratorShips();
        while (it_s.hasNext()) {
            Ship sh = it_s.next();
            if (!sh.getId().equals(s.getId())) {
                String sh_id = sh.getId();
                Geometry g = sh.getGeometry();
                OWLNamedIndividual a = dataFactory.getOWLNamedIndividual(":" + sh_id, pm);
                double distBetweenShips = p.distance(g);
                //Possible towing boats.
                if(/*belongsToClass(v, largePowerDrivenVesselClass)*/s.getLength()>=170 && /*belongsToClass(a, smallBoatClass)*/ sh.getLength()<=15){
                    //Possible distance where we consider boats as towing boats.
                    double possibleDistance = -1.0;
                    for (OWLIndividual ptdp : v.getObjectPropertyValues(possibleTowingDistanceProperty, ont)) {
                        for (OWLLiteral d : ptdp.getDataPropertyValues(dProperty, ont)){ 
                            possibleDistance = d.parseDouble();
                        }    
                    }
                    double recommendedDistance = -1.0;
                    for (OWLIndividual rtdp : v.getObjectPropertyValues(recommendedTowingDistanceProperty, ont)) {
                        for (OWLLiteral d : rtdp.getDataPropertyValues(dProperty, ont)){ 
                            recommendedDistance = d.parseDouble();
                        }    
                    }
                    if(hasInstantiatedProperty(wellTowingProperty,a,v)){ 
                        OWLObjectPropertyAssertionAxiom axiom_isTowedBy = dataFactory.getOWLObjectPropertyAssertionAxiom(isWellTowedByProperty, v, a);
                        manager.removeAxiom(ont, axiom_isTowedBy);
                        OWLObjectPropertyAssertionAxiom axiom_towing = dataFactory.getOWLObjectPropertyAssertionAxiom(wellTowingProperty, a, v);
                        manager.removeAxiom(ont, axiom_towing);
                    }else if(hasInstantiatedProperty(badTowingProperty,a,v)){
                        OWLObjectPropertyAssertionAxiom axiom_isTowedBy = dataFactory.getOWLObjectPropertyAssertionAxiom(isBadTowedByProperty, v, a);
                        manager.removeAxiom(ont, axiom_isTowedBy);
                        OWLObjectPropertyAssertionAxiom axiom_towing = dataFactory.getOWLObjectPropertyAssertionAxiom(badTowingProperty, a, v);
                        manager.removeAxiom(ont, axiom_towing);
                    }
                    if (distBetweenShips <= possibleDistance) {
                        if(/*!hasInstantiatedProperty(wellTowingProperty,a,v) && distBetweenShips >= recommendedDistance-5 &&*/ distBetweenShips <= recommendedDistance+5){
                            OWLObjectPropertyAssertionAxiom axiom_isTowedBy = dataFactory.getOWLObjectPropertyAssertionAxiom(isWellTowedByProperty, v, a);
                            manager.addAxiom(ont, axiom_isTowedBy);
                            OWLObjectPropertyAssertionAxiom axiom_towing = dataFactory.getOWLObjectPropertyAssertionAxiom(wellTowingProperty, a, v);
                            manager.addAxiom(ont, axiom_towing);
                        }else if(/*!hasInstantiatedProperty(badTowingProperty,a,v)&&(distBetweenShips < recommendedDistance-5 ||*/ distBetweenShips > recommendedDistance+5){
                            OWLObjectPropertyAssertionAxiom axiom_isTowedBy = dataFactory.getOWLObjectPropertyAssertionAxiom(isBadTowedByProperty, v, a);
                            manager.addAxiom(ont, axiom_isTowedBy);
                            OWLObjectPropertyAssertionAxiom axiom_towing = dataFactory.getOWLObjectPropertyAssertionAxiom(badTowingProperty, a, v);
                            manager.addAxiom(ont, axiom_towing);
                        }
                        currentTowingBoats++;
                    }
                }else if(/*belongsToClass(v, smallBoatClass)*/ s.getLength()<=15 && /*belongsToClass(a, largePowerDrivenVesselClass)*/sh.getLength()>=170){
                    //Possible distance where we consider boats as towing boats.
                    double possibleDistance = -1.0;
                    for (OWLIndividual ptdp : a.getObjectPropertyValues(possibleTowingDistanceProperty, ont)) {
                        for (OWLLiteral d : ptdp.getDataPropertyValues(dProperty, ont)){ 
                            possibleDistance = d.parseDouble();
                        }    
                    }
                    double recommendedDistance = -1.0;
                    for (OWLIndividual rtdp : a.getObjectPropertyValues(recommendedTowingDistanceProperty, ont)) {
                        for (OWLLiteral d : rtdp.getDataPropertyValues(dProperty, ont)){ 
                            recommendedDistance = d.parseDouble();
                        }    
                    }
                    for (OWLIndividual b : a.getObjectPropertyValues(currentTowingBoatNumberProperty, ont)) {
                        for (OWLLiteral d : b.getDataPropertyValues(numProperty, ont)) {
                            currentTowingBoats = d.parseInteger(); 
                        }
                    }
                    if(hasInstantiatedProperty(wellTowingProperty,v,a)){    
                        OWLObjectPropertyAssertionAxiom axiom_isWellTowedBy = dataFactory.getOWLObjectPropertyAssertionAxiom(isWellTowedByProperty, a, v);
                        manager.removeAxiom(ont, axiom_isWellTowedBy);
                        OWLObjectPropertyAssertionAxiom axiom_wellTowing = dataFactory.getOWLObjectPropertyAssertionAxiom(wellTowingProperty, v, a);
                        manager.removeAxiom(ont, axiom_wellTowing);
                        //Decrease the number of towin
                        currentTowingBoats=currentTowingBoats-1;
                        changeNumberTowingBoats(a, sh_id, time, currentTowingBoats);
                    }else if(hasInstantiatedProperty(badTowingProperty,v,a)){
                        OWLObjectPropertyAssertionAxiom axiom_isBadTowedBy = dataFactory.getOWLObjectPropertyAssertionAxiom(isBadTowedByProperty, a, v);
                        manager.removeAxiom(ont, axiom_isBadTowedBy);
                        OWLObjectPropertyAssertionAxiom axiom_badTowing = dataFactory.getOWLObjectPropertyAssertionAxiom(badTowingProperty, v, a);
                        manager.removeAxiom(ont, axiom_badTowing);
                        //Decrease the number of towing boats 
                        currentTowingBoats=currentTowingBoats-1;
                        changeNumberTowingBoats(a, sh_id, time, currentTowingBoats);
                    }
                    if (distBetweenShips <= possibleDistance) {
                        if(/*!hasInstantiatedProperty(wellTowingProperty,v,a)&&(distBetweenShips >= recommendedDistance-5 &&*/ distBetweenShips <= recommendedDistance+5){
                            //Set the properties
                            OWLObjectPropertyAssertionAxiom axiom_isWellTowedBy = dataFactory.getOWLObjectPropertyAssertionAxiom(isWellTowedByProperty, a, v);
                            manager.addAxiom(ont, axiom_isWellTowedBy);
                            OWLObjectPropertyAssertionAxiom axiom_wellTowing = dataFactory.getOWLObjectPropertyAssertionAxiom(wellTowingProperty, v, a);
                            manager.addAxiom(ont, axiom_wellTowing);
                            currentTowingBoats=currentTowingBoats+1;
                            changeNumberTowingBoats(a, sh_id, time, currentTowingBoats);
                        }else if (/*!hasInstantiatedProperty(badTowingProperty,v,a)&&(distBetweenShips < recommendedDistance-5 ||*/ distBetweenShips > recommendedDistance+5){
                            //Set the properties
                            OWLObjectPropertyAssertionAxiom axiom_isBadTowedBy = dataFactory.getOWLObjectPropertyAssertionAxiom(isBadTowedByProperty, a, v);
                            manager.addAxiom(ont, axiom_isBadTowedBy);
                            OWLObjectPropertyAssertionAxiom axiom_badTowing = dataFactory.getOWLObjectPropertyAssertionAxiom(badTowingProperty, v, a);
                            manager.addAxiom(ont, axiom_badTowing);
                            //Increase the number of towing boats
                            currentTowingBoats=currentTowingBoats+1;
                            changeNumberTowingBoats(a, sh_id, time, currentTowingBoats);
                        }
                    }
                }else if(/*belongsToClass(v, smallBoatClass)*/ s.getLength()<=15 && /*belongsToClass(a, smallBoatClass)*/ sh.getLength()<=15){
                    //System.out.println("TIEMPO: " + time);
                    //System.out.println("Distancia entre los barcos " + s.getId() + " , " + sh.getId() + ": " + distBetweenShips);
                    if(distBetweenShips<=5){
                        OWLObjectPropertyAssertionAxiom axiom_tooCloseToTow = dataFactory.getOWLObjectPropertyAssertionAxiom(tooCloseToTowProperty, v, a);
                        manager.addAxiom(ont, axiom_tooCloseToTow);
                        OWLObjectPropertyAssertionAxiom axiom_tooCloseToTow2 = dataFactory.getOWLObjectPropertyAssertionAxiom(tooCloseToTowProperty, a, v);
                        manager.addAxiom(ont, axiom_tooCloseToTow2);
                        //OWLObjectPropertyAssertionAxiom axiom_correctDistanceToTow = dataFactory.getOWLObjectPropertyAssertionAxiom(correctDistanceToTowProperty, v, a);
                        //manager.removeAxiom(ont, axiom_correctDistanceToTow);
                    }else{
                        //OWLObjectPropertyAssertionAxiom axiom_correctDistanceToTow = dataFactory.getOWLObjectPropertyAssertionAxiom(correctDistanceToTowProperty, v, a);
                        //manager.addAxiom(ont, axiom_correctDistanceToTow);
                        OWLObjectPropertyAssertionAxiom axiom_tooCloseToTow = dataFactory.getOWLObjectPropertyAssertionAxiom(tooCloseToTowProperty, v, a);
                        manager.removeAxiom(ont, axiom_tooCloseToTow);
                        OWLObjectPropertyAssertionAxiom axiom_tooCloseToTow2 = dataFactory.getOWLObjectPropertyAssertionAxiom(tooCloseToTowProperty, a, v);
                        manager.removeAxiom(ont, axiom_tooCloseToTow2);
                    }
                    double diff_angle = Math.abs(sh.getAngS() - s.getAngS());
                    //System.out.println("Angulo entre los barcos " + s.getId() + ", " + sh.getId() + ": " + diff_angle);
                    if(diff_angle>=10){
                        OWLObjectPropertyAssertionAxiom axiom_nonAlignedToTow = dataFactory.getOWLObjectPropertyAssertionAxiom(nonAlignedToTowProperty, v, a);
                        manager.addAxiom(ont, axiom_nonAlignedToTow);
                        OWLObjectPropertyAssertionAxiom axiom_nonAlignedToTow2 = dataFactory.getOWLObjectPropertyAssertionAxiom(nonAlignedToTowProperty, a, v);
                        manager.addAxiom(ont, axiom_nonAlignedToTow2);
                        //OWLObjectPropertyAssertionAxiom axiom_alignedToTow = dataFactory.getOWLObjectPropertyAssertionAxiom(alignedToTowProperty, v, a);
                        //manager.removeAxiom(ont, axiom_alignedToTow);
                    }else{
                        //OWLObjectPropertyAssertionAxiom axiom_alignedToTow = dataFactory.getOWLObjectPropertyAssertionAxiom(alignedToTowProperty, v, a);
                        //manager.addAxiom(ont, axiom_alignedToTow);
                        OWLObjectPropertyAssertionAxiom axiom_nonAlignedToTow = dataFactory.getOWLObjectPropertyAssertionAxiom(nonAlignedToTowProperty, v, a);
                        manager.removeAxiom(ont, axiom_nonAlignedToTow);
                        OWLObjectPropertyAssertionAxiom axiom_nonAlignedToTow2 = dataFactory.getOWLObjectPropertyAssertionAxiom(nonAlignedToTowProperty, a, v);
                        manager.removeAxiom(ont, axiom_nonAlignedToTow2);
                    }
                }
                // Get maximum allowed distances                
//                if (p.distance(g) < s.getAllowedDistance() ) {  // @todo closeTo axiom does not reflects which ships attacks
//                    OWLObjectPropertyAssertionAxiom axiom_closeTo = dataFactory.getOWLObjectPropertyAssertionAxiom(closeToProperty, v, a);
//                    manager.addAxiom(ont, axiom_closeTo);
//                } else {
//                    OWLObjectPropertyAssertionAxiom axiom_closeTo = dataFactory.getOWLObjectPropertyAssertionAxiom(closeToProperty, v, a);
//                    manager.removeAxiom(ont, axiom_closeTo);
//                }
//                
//                if (g.distance(p) < sh.getAllowedDistance() ) {  // @todo closeTo axiom does not reflects which ships attacks
//                    OWLObjectPropertyAssertionAxiom axiom_closeTo = dataFactory.getOWLObjectPropertyAssertionAxiom(closeToProperty, a, v);
//                    manager.addAxiom(ont, axiom_closeTo);
//                } else {
//                    OWLObjectPropertyAssertionAxiom axiom_closeTo = dataFactory.getOWLObjectPropertyAssertionAxiom(closeToProperty, a, v);
//                    manager.removeAxiom(ont, axiom_closeTo);
//                }
            }
        }
        if(/*belongsToClass(v, largePowerDrivenVesselClass)*/s.getLength()>=170){
            //Set the new number of towing boats  
            changeNumberTowingBoats(v, s.getId(), time, currentTowingBoats);
        }
        
        // closeToFacility
        Iterator<Facility> it_f = gm.iteratorFacilities();
        while (it_f.hasNext()) {
            Facility f = it_f.next();

            String f_id = f.getId();
            Geometry g = f.getGeometry();
            
            // Get maximum allowed distance
            if (p.distance(g) < f.getMinDistance() ) {
                OWLNamedIndividual a = dataFactory.getOWLNamedIndividual(":" + f_id, pm);
                OWLObjectPropertyAssertionAxiom axiom_closeTo = dataFactory.getOWLObjectPropertyAssertionAxiom(closeToFacilityProperty, v, a);
                manager.addAxiom(ont, axiom_closeTo);
            } else {
                OWLNamedIndividual a = dataFactory.getOWLNamedIndividual(":" + f_id, pm);
                OWLObjectPropertyAssertionAxiom axiom_closeTo = dataFactory.getOWLObjectPropertyAssertionAxiom(closeToFacilityProperty, v, a);
                manager.removeAxiom(ont, axiom_closeTo);
            }
        }
    }
    
    private boolean hasInstantiatedProperty(OWLObjectPropertyExpression instantiatedP, OWLIndividual from, OWLIndividual to){
        boolean isInstantianted = false;
        Set<OWLIndividual> instances = from.getObjectPropertyValues(instantiatedP, ont);
        for(OWLIndividual tship : instances){
            if(tship.equals(to)){
                isInstantianted = true;
            }
        }
        return isInstantianted;
    }
    
    
    
    /* -- Change the number of towing boats for a vessel*/
    private void changeNumberTowingBoats(OWLIndividual vessel, String id, double time, int currentTowingBoats){
        //System.out.println("Current Towing Boats: " + currentTowingBoats);
        //Delete the old number of towing boats
        for (OWLIndividual b : vessel.getObjectPropertyValues(currentTowingBoatNumberProperty, ont)) {
            for (OWLLiteral d : b.getDataPropertyValues(numProperty, ont)) {
                OWLDataPropertyAssertionAxiom axiom_num = dataFactory.getOWLDataPropertyAssertionAxiom(numProperty, b, d);
                manager.removeAxiom(ont, axiom_num);
            }
            OWLObjectPropertyAssertionAxiom axiom_ctb_number = dataFactory.getOWLObjectPropertyAssertionAxiom(currentTowingBoatNumberProperty, vessel, b);
            manager.removeAxiom(ont, axiom_ctb_number);

            OWLClassAssertionAxiom axiom_isNum = dataFactory.getOWLClassAssertionAxiom(boatNumberClass, b);
            manager.removeAxiom(ont, axiom_isNum);

            OWLDeclarationAxiom axiom_ctb_num_dec = dataFactory.getOWLDeclarationAxiom(b.asOWLNamedIndividual());
            manager.removeAxiom(ont, axiom_ctb_num_dec);
        }
        //Assert the new number of towing boats
        OWLNamedIndividual newCtbn = dataFactory.getOWLNamedIndividual(":" + id + "_ctbn_" + time, pm);
        OWLDataPropertyAssertionAxiom axiom_ctbn_number = dataFactory.getOWLDataPropertyAssertionAxiom(numProperty, newCtbn, currentTowingBoats);        
        OWLClassAssertionAxiom axiom_isCtbn = dataFactory.getOWLClassAssertionAxiom(boatNumberClass, newCtbn);
        OWLObjectPropertyAssertionAxiom axiom_ctbn = dataFactory.getOWLObjectPropertyAssertionAxiom(currentTowingBoatNumberProperty, vessel, newCtbn);
        manager.addAxiom(ont, axiom_ctbn_number);
        manager.addAxiom(ont, axiom_isCtbn);
        manager.addAxiom(ont, axiom_ctbn);
    }
    
    
    /* -- Check if an individual belongs to a class*/
    private boolean belongsToClass(OWLIndividual individual, OWLClass checkingclass){
        // >> Reconnect reasoner
        reasonerFactory = PelletReasonerFactory.getInstance();
        reasonerConfig = new SimpleConfiguration();        
        reasoner = reasonerFactory.createReasoner(ont, reasonerConfig);   
        
        Set<OWLClass> classes = reasoner.getTypes(individual.asOWLNamedIndividual(), true).getFlattened();
        boolean belongsToClass=false;
        for (OWLClass c : classes) {
            if(c.equals(checkingclass)){
                belongsToClass=true;
            }
        }
        reasoner.dispose();
        return belongsToClass;
    }
    
    /** Dump ontology into file */
    public void dump(File f) throws OWLOntologyStorageException {                
        manager.saveOntology(ont, IRI.create(f));                  
    }
    
    /** Retrieve vessel states, according to expected situations 
      * @param violation  Empty array where violation vessels will be stored (must be allocated)
      * @param compliant  Empty array where compliant vessels will be stored (must be allocated)
      * @param unknown    Empty array where unknown vessels will be stored   (must be allocated)
      */
    public void retrieveVesselStates(ArrayList<ExpectedSituation> violation, ArrayList<ExpectedSituation> compliant, ArrayList<ExpectedSituation> unknown) {        
        
        // >> Reconnect reasoner
        reasonerFactory = PelletReasonerFactory.getInstance();
        reasonerConfig = new SimpleConfiguration();        
        reasoner = reasonerFactory.createReasoner(ont, reasonerConfig);        
  
        // >> Violations
        Set<OWLNamedIndividual> inViolation = reasoner.getInstances(restrictionViolation, false).getFlattened();
        for (OWLNamedIndividual i : inViolation) {
            ExpectedSituation situation = new ExpectedSituation(i.toString());
            for (OWLClass c : reasoner.getTypes(i, true).getFlattened()) {  // @todo: include just violation classes                                                
                situation.superClasses.add(c.toString());
            }
            violation.add(situation);
        }

        // >> Compliances
        Set<OWLNamedIndividual> inCompliance = reasoner.getInstances(restrictionCompliance, false).getFlattened();        
        for (OWLNamedIndividual i : inCompliance) {            
            ExpectedSituation situation = new ExpectedSituation(i.toString());
            for (OWLClass c : reasoner.getTypes(i, true).getFlattened()) {
                situation.superClasses.add(c.toString());                   // @todo: include just compliance classes 
            }
            compliant.add(situation);
        }
        
        // -- Unknown            
        Set<OWLNamedIndividual> inVessel = reasoner.getInstances(vesselClass, false).getFlattened();
        for (OWLNamedIndividual i : inVessel) {            
            if (!inViolation.contains(i) && !inCompliance.contains(i)) {
                ExpectedSituation situation = new ExpectedSituation(i.toString());
                        
                for (OWLClass c : reasoner.getTypes(i, true).getFlattened()) {
                    situation.superClasses.add(c.toString());
                }
                
                unknown.add(situation);
            }
        }
        reasoner.dispose();
    }
    
    
    /** Retrieve vessels situations 
      * @param vesselsSituation  Empty array where the situation of the vessels will be stored (must be allocated)
      */
    public void retrieveVesselSituation(ArrayList<VesselSituation> violation, ArrayList<VesselSituation> compliant, ArrayList<VesselSituation> unknown, GeometricalModel gm) {
        // >> Reconnect reasoner
        reasonerFactory = PelletReasonerFactory.getInstance();
        reasonerConfig = new SimpleConfiguration();        
        reasoner = reasonerFactory.createReasoner(ont, reasonerConfig);
        
        // >> Violations
        Set<OWLNamedIndividual> inViolation = reasoner.getInstances(restrictionViolation, false).getFlattened();        
        for (OWLNamedIndividual i : inViolation) {
            //System.out.println("VIOLATION:");
            VesselSituation vs = getDataFromShipIndividual(i, restrictionViolation, gm);
            violation.add(vs);
        }
        // >> Compliances
        Set<OWLNamedIndividual> inCompliance = reasoner.getInstances(restrictionCompliance, false).getFlattened();        
        for (OWLNamedIndividual i : inCompliance) {
            //System.out.println("COMPLIANT:");
            VesselSituation vs = getDataFromShipIndividual(i, restrictionCompliance, gm);
            compliant.add(vs);
        }
        
        // -- Unknown            
        Set<OWLNamedIndividual> inVessel = reasoner.getInstances(vesselClass, false).getFlattened();
        for (OWLNamedIndividual i : inVessel) {            
            if (!inViolation.contains(i) && !inCompliance.contains(i)) {
                //System.out.println("UNKNOWN:");
                VesselSituation vs = getDataFromShipIndividual(i, vesselClass, gm);
                unknown.add(vs);
            }
        }
        //Una vez tenemos toda la informacion de las heuristicas calculada, utilizamos la DISJUNCTIVE COMBINATION RULE.
    }
    
    public VesselSituation getDataFromShipIndividual(OWLNamedIndividual v, OWLClass superclass, GeometricalModel gm){
        
        // Assign name
        String id = v.toString().substring(v.toString().indexOf('#') + 1, v.toString().length() - 1);            
        //System.out.println("ID: " + id);
        // length
        double length = -1;
        for (OWLIndividual a : v.getObjectPropertyValues(lengthProperty, ont)) {
            for (OWLLiteral d : a.getDataPropertyValues(lenProperty, ont)) 
                //System.out.println("LENGTH: " + d.parseDouble());
                length = d.parseDouble();
        }

        // position
        double xpos = -1, ypos = -1;
        for (OWLIndividual a : v.getObjectPropertyValues(positionProperty, ont)) {
            for (OWLLiteral d : a.getDataPropertyValues(latProperty, ont)) 
                //System.out.println("LATITUDE: " + d.parseDouble());
                xpos = d.parseDouble();
            for (OWLLiteral d : a.getDataPropertyValues(lngProperty, ont)) 
                //System.out.println("LONGITUDE: " + d.parseDouble());
                ypos = d.parseDouble();
        }

        // speed
        double xspeed = -1, yspeed = -1, angle = -1, mod = -1;
        for (OWLIndividual a : v.getObjectPropertyValues(speedProperty, ont)) {
           for (OWLLiteral d : a.getDataPropertyValues(latProperty, ont)) 
                //System.out.println("LAT_SPEED: " + d.parseDouble());
                xspeed = d.parseDouble();
            for (OWLLiteral d : a.getDataPropertyValues(lngProperty, ont)) 
                //System.out.println("LON_SPEED: " + d.parseDouble());
                yspeed = d.parseDouble();
            for (OWLLiteral d : a.getDataPropertyValues(angProperty, ont)) 
                //System.out.println("ANGLE: " + d.parseDouble());
                angle = d.parseDouble();
            for (OWLLiteral d : a.getDataPropertyValues(modProperty, ont)) 
                //System.out.println("MOD: " + d.parseDouble());
                mod = d.parseDouble();
        }

        // allowed distance
        double adist = -1;
        for (OWLIndividual a : v.getObjectPropertyValues(allowedDistanceProperty, ont)) {
            for (OWLLiteral d : a.getDataPropertyValues(dProperty, ont)) 
                //System.out.println("Allowed distance: " + d.parseDouble());
                adist = d.parseDouble();
        }
        
        VesselSituation vs = new VesselSituation(v.toString(), id, length, xpos, ypos, xspeed, yspeed, angle, mod, adist);
        
        for (OWLClass c : reasoner.getTypes(v, true).getFlattened()) {  // @todo: include just violation classes
            Iterator<OWLClassExpression> itVesselSubclass = vesselClass.getSubClasses(ont).iterator();
            while(itVesselSubclass.hasNext()){
                OWLClassExpression checker = itVesselSubclass.next();
                if(checker.equals(c)){
                    //System.out.println("Vessel class: " + c.toString());
                    vs.boatType = c.toString();
                }
            }
        }
        
        for (OWLClass c : reasoner.getTypes(v, true).getFlattened()) {
            Iterator<OWLClassExpression> itSubclass = superclass.getSubClasses(ont).iterator();
            while(itSubclass.hasNext()){
                OWLClassExpression checker = itSubclass.next();
                if(checker.equals(c)){
                    ArrayList<SituationData> sd = getSituationData(v,c,vs,gm);
                    for(SituationData s: sd){
                        vs.superClasses.add(s);
                    }
                }
            }
        }
        return vs;
    }
    
    public ArrayList<SituationData> getSituationData(OWLNamedIndividual v, OWLClass c, VesselSituation vs, GeometricalModel gm){
        ArrayList<SituationData> sds = new ArrayList();
        /* En esta situacion tenemos las superclasses, el tipo de barco y todas sus caracteristicas.
        * Recorremos las superclases, hacemos match con el comportamiento, en ese mismo if
        * definimos la peticion para generamos los datos especificos que necesitaremos posteriormente.
        * 
        * Como puedes estar violando el alineamiento o la velocidad de 2 areas o el alineamiento o la 
        * distancia entre 2 remolcadoras o con el propio LargePowerdDrivenVessel a veces va a ser necesario 
        * enviar un array con varios objetos SituationData. Uno para cada violacion.
        * 
        * Para cada barco debemos tener sus violaciones y compliances.
        * Para cada violacion debemos tener una serie de datos concretos (estan definidos en ReflexionesRogovianas.txt).
        */
        OWLClass speedViolation = dataFactory.getOWLClass(":SpeedViolation", pm);
        OWLClass speedCompliance = dataFactory.getOWLClass(":SpeedCompliance", pm);
        OWLClass navigationDirectionViolation = dataFactory.getOWLClass(":NavigationDirectionViolation", pm);
        OWLClass navigationDirectionCompliance = dataFactory.getOWLClass(":NavigationDirectionCompliance", pm);
        OWLClass towingDistanceViolation = dataFactory.getOWLClass(":TowingDistanceViolation", pm);
        OWLClass towingDistanceCompliance = dataFactory.getOWLClass(":TowingDistanceCompliance", pm);
        OWLClass towingNumberViolation = dataFactory.getOWLClass(":TowingNumberViolation", pm);
        OWLClass towingNumberCompliance = dataFactory.getOWLClass(":TowingNumberCompliance", pm);
        OWLClass towingAlignmentViolation  = dataFactory.getOWLClass(":TowingAlignmentViolation", pm);
        OWLClass distanceToTowViolation = dataFactory.getOWLClass(":DistanceToTowViolation", pm);
        OWLClass facilityPerimeterViolation = dataFactory.getOWLClass(":FacilityPerimeterViolation", pm);
        
        
        //EXTRAER TODA LA INFO DE LAS ONTOLOGIAS
        if(c.toString().equals(speedViolation.toString())||c.toString().equals(speedCompliance.toString())){
            //Diferencia de la velocidad con respecto a la velocidad del area en la que se navega.
            //Area que en la que se produce la violacion y sus datos. (Solo ese area)
            //Se saca por la relacion insideOf
            Set<OWLIndividual> insideOf = v.getObjectPropertyValues(insideOfProperty, ont);
            for (OWLIndividual a : insideOf){
                OWLNamedIndividual ni = a.asOWLNamedIndividual();
                double areaAngle = -1;
                for (OWLLiteral d : ni.getDataPropertyValues(dirProperty, ont)){ 
                    areaAngle=d.parseDouble();
                }
                double areaModule = -1;
                for (OWLLiteral d : ni.getDataPropertyValues(modProperty, ont)){ 
                    areaModule=d.parseDouble();
                }
                if((areaModule>=vs.speedmod && c.toString().equals(speedCompliance.toString())) || (areaModule<vs.speedmod && c.toString().equals(speedViolation.toString()))){
                    SituationData sd = new SituationData(c.toString());
                    sd.insideOf = new XMLArea(ni.toString(), areaAngle, areaModule);
                    sds.add(sd);
                }
            }
        }else if(c.toString().equals(navigationDirectionViolation.toString())){
            //Diferencia de la direccion con respecto a la direccion del area en la que se navega.
            //Area que en la que se produce la violacion y sus datos. (Solo ese area)
            Set<OWLIndividual> nonAlignedTo = v.getObjectPropertyValues(nonAlignedToProperty, ont);
            for (OWLIndividual a : nonAlignedTo){
                OWLNamedIndividual ni = a.asOWLNamedIndividual();
                double areaAngle = -1;
                for (OWLLiteral d : ni.getDataPropertyValues(dirProperty, ont)){ 
                    areaAngle=d.parseDouble();
                }
                double areaModule = -1;
                for (OWLLiteral d : ni.getDataPropertyValues(modProperty, ont)){ 
                    areaModule=d.parseDouble();
                }
                
                SituationData sd = new SituationData(c.toString());
                sd.nonAlignedTo = new XMLArea(ni.toString(), areaAngle, areaModule);
                sds.add(sd);
                //Hay que crear un "situation data" que incluya los datos del area y el comportamiento.
                
            }
        }else if(c.toString().equals(navigationDirectionCompliance.toString())){
            //Diferencia de la direccion con respecto a la direccion del area en la que se navega.
            //Area que en la que se produce la violacion y sus datos. (Solo ese area)    
            Set<OWLIndividual> alignedTo = v.getObjectPropertyValues(alignedToProperty, ont);
            for (OWLIndividual a : alignedTo){
                OWLNamedIndividual ni = a.asOWLNamedIndividual();
                double areaAngle = -1;
                for (OWLLiteral d : ni.getDataPropertyValues(dirProperty, ont)){ 
                    areaAngle=d.parseDouble();
                }
                double areaModule = -1;
                for (OWLLiteral d : ni.getDataPropertyValues(modProperty, ont)){ 
                    areaModule=d.parseDouble();
                }
                
                SituationData sd = new SituationData(c.toString());
                sd.alignedTo = new XMLArea(ni.toString(), areaAngle, areaModule);
                sds.add(sd);
            }
        }else if(c.toString().equals(towingNumberViolation.toString())||c.toString().equals(towingNumberCompliance.toString())){
            //System.out.println("TowingNumberViolation o TowingNumberCompliance");
            //TowingNumberViolation (Solo LargePowerDrivenVessel)
            //Diferencia de los SmallBoats recomendados y los actuales.
	    int currentTowingBoats=-1;
            for (OWLIndividual b : v.getObjectPropertyValues(currentTowingBoatNumberProperty, ont)) {
                for (OWLLiteral d : b.getDataPropertyValues(numProperty, ont)) {
                    currentTowingBoats = d.parseInteger(); 
                }
            }
            int recommendedTowingBoats=-1;
            for (OWLIndividual b : v.getObjectPropertyValues(recommendedTowingBoatNumberProperty, ont)) {
                for (OWLLiteral d : b.getDataPropertyValues(numProperty, ont)) {
                    recommendedTowingBoats = d.parseInteger(); 
                }
            }
            SituationData sd = new SituationData(c.toString());
            sd.currentTowingBoatNumber = currentTowingBoats;
            sd.recommendedTowingBoatNumber = recommendedTowingBoats;
            sds.add(sd);
        }else if(c.toString().equals(towingDistanceViolation.toString())||c.toString().equals(towingDistanceCompliance.toString())){
            //System.out.println("TowingDistanceViolation o TowingDistanceCompliance");
            //TowingDistanceViolation (Puede que solo hagan falta los SmallBoats)
            //Distancia de los SmallBoats con respecto al LargePowerDrivenVessel.
            //Id del LargePowerDrivenVessel.
            String idShip1 = v.toString().substring(v.toString().indexOf('#') + 1, v.toString().length() - 1);
            Geometry p1 = getShipGeometry(idShip1,gm);
            Set<OWLIndividual> towedBySet = v.getObjectPropertyValues(badTowingProperty, ont);
            if(c.toString().equals(towingDistanceCompliance.toString())){
                towedBySet = v.getObjectPropertyValues(wellTowingProperty, ont);
            }
            for(OWLIndividual towedBy : towedBySet){
                String idShip2 = towedBy.toString().substring(v.toString().indexOf('#') + 1, v.toString().length() - 1);
                Geometry p2 = getShipGeometry(idShip2,gm);
                if(p1!=null&&p2!=null){
                    double distance=p1.distance(p2);
                    SituationData sd = new SituationData(c.toString());
                    sd.towingDistanceId = idShip2;
                    sd.towingDistance = distance;
                    sds.add(sd);
                }
            }
        }else if(c.toString().equals(towingAlignmentViolation.toString())){
            //System.out.println("TowingAlignmentViolation");
            //TowingAlignmentViolation (Solo entre SmallBoats)
            //Para cada SmallBoat que remolque se guarda el identificador y la diferencia del angulo entre los barcos.
            Set<OWLIndividual> nonAlignedToTow = v.getObjectPropertyValues(nonAlignedToTowProperty, ont);
            for (OWLIndividual nonAligned : nonAlignedToTow){
                Set<OWLIndividual> towedShips11 = nonAligned.getObjectPropertyValues(badTowingProperty, ont);
                Set<OWLIndividual> towedShips12 = nonAligned.getObjectPropertyValues(wellTowingProperty, ont);
                Set<OWLIndividual> towedShips21 = v.getObjectPropertyValues(badTowingProperty, ont);
                Set<OWLIndividual> towedShips22 = v.getObjectPropertyValues(wellTowingProperty, ont);
                for (OWLIndividual a : towedShips11){
                    for (OWLIndividual b : towedShips21){
                        if(a.equals(b)){
                            double angle=-1;
                            for (OWLIndividual s : nonAligned.getObjectPropertyValues(speedProperty, ont)) {
                                for (OWLLiteral r : s.getDataPropertyValues(angProperty, ont)) {
                                    angle = r.parseDouble();
                                    SituationData sd = new SituationData(c.toString());
                                    sd.nonAlignedShipId = nonAligned.toString();
                                    sd.nonAlignedShipAngle = angle;
                                    sds.add(sd);
                                }
                            }
                        }
                    }
                    for (OWLIndividual b : towedShips22){
                        if(a.equals(b)){
                            double angle=-1;
                            for (OWLIndividual s : nonAligned.getObjectPropertyValues(speedProperty, ont)) {
                                for (OWLLiteral r : s.getDataPropertyValues(angProperty, ont)) {
                                    angle = r.parseDouble();
                                    SituationData sd = new SituationData(c.toString());
                                    sd.nonAlignedShipId = nonAligned.toString();
                                    sd.nonAlignedShipAngle = angle;
                                    sds.add(sd);
                                }
                            }
                        }
                    }
                }
                for (OWLIndividual a : towedShips12){
                    for (OWLIndividual b : towedShips21){
                        if(a.equals(b)){
                            double angle=-1;
                            for (OWLIndividual s : nonAligned.getObjectPropertyValues(speedProperty, ont)) {
                                for (OWLLiteral r : s.getDataPropertyValues(angProperty, ont)) {
                                    angle = r.parseDouble();
                                    SituationData sd = new SituationData(c.toString());
                                    sd.nonAlignedShipId = nonAligned.toString();
                                    sd.nonAlignedShipAngle = angle;
                                    sds.add(sd);
                                }
                            }
                        }
                    }
                    for (OWLIndividual b : towedShips22){
                        if(a.equals(b)){
                            double angle=-1;
                            for (OWLIndividual s : nonAligned.getObjectPropertyValues(speedProperty, ont)) {
                                for (OWLLiteral r : s.getDataPropertyValues(angProperty, ont)) {
                                    angle = r.parseDouble();
                                    SituationData sd = new SituationData(c.toString());
                                    sd.nonAlignedShipId = nonAligned.toString();
                                    sd.nonAlignedShipAngle = angle;
                                    sds.add(sd);
                                }
                            }
                        }
                    }
                }
            }
        }else if(c.toString().equals(distanceToTowViolation.toString())){
            //DistanceToTowViolation (Solo entre SmallBoats)
            //Para cada SmallBoat que remolque se guarda el identificador y la distancia especifica entre ellos.
            //Vessel(?x), Vessel(?y), Vessel(?z), isTowedBy(?z, ?x), isTowedBy(?z, ?y), tooCloseToTow(?x, ?y) -> DistanceToTowViolation(?x), DistanceToTowViolation(?y)
            String idShip1 = v.toString().substring(v.toString().indexOf('#') + 1, v.toString().length() - 1);
            Geometry p1 = getShipGeometry(idShip1,gm);
            Set<OWLIndividual> tooCloseToTow = v.getObjectPropertyValues(tooCloseToTowProperty, ont);
            for (OWLIndividual tooCloseTo : tooCloseToTow){
                Set<OWLIndividual> towedBy1 = tooCloseTo.getObjectPropertyValues(badTowingProperty, ont);
                Set<OWLIndividual> towedBy2 = tooCloseTo.getObjectPropertyValues(wellTowingProperty, ont);
                Set<OWLIndividual> towedBySet1 = v.getObjectPropertyValues(badTowingProperty, ont);
                Set<OWLIndividual> towedBySet2 = v.getObjectPropertyValues(wellTowingProperty, ont);
                for (OWLIndividual a : towedBy1){
                    for (OWLIndividual b : towedBySet1){
                        if(a.equals(b)){
                            String idShip2 = tooCloseTo.toString().substring(v.toString().indexOf('#') + 1, v.toString().length() - 1);
                            Geometry p2 = getShipGeometry(idShip2,gm);
                            if(p1!=null&&p2!=null){
                                double distance=p1.distance(p2);
                                SituationData sd = new SituationData(c.toString());
                                sd.distanceToTowShipId = idShip2;
                                sd.distanceToTowShip = distance;
                                sds.add(sd);
                            }
                        }
                    }
                    for (OWLIndividual b : towedBySet2){
                        if(a.equals(b)){
                            String idShip2 = tooCloseTo.toString().substring(v.toString().indexOf('#') + 1, v.toString().length() - 1);
                            Geometry p2 = getShipGeometry(idShip2,gm);
                            if(p1!=null&&p2!=null){
                                double distance=p1.distance(p2);
                                SituationData sd = new SituationData(c.toString());
                                sd.distanceToTowShipId = idShip2;
                                sd.distanceToTowShip = distance;
                                sds.add(sd);
                            }
                        }
                    }
                }
                for (OWLIndividual a : towedBy2){
                    for (OWLIndividual b : towedBySet1){
                        if(a.equals(b)){
                            String idShip2 = tooCloseTo.toString().substring(v.toString().indexOf('#') + 1, v.toString().length() - 1);
                            Geometry p2 = getShipGeometry(idShip2,gm);
                            if(p1!=null&&p2!=null){
                                double distance=p1.distance(p2);
                                SituationData sd = new SituationData(c.toString());
                                sd.distanceToTowShipId = idShip2;
                                sd.distanceToTowShip = distance;
                                sds.add(sd);
                            }
                        }
                    }
                    for (OWLIndividual b : towedBySet2){
                        if(a.equals(b)){
                            String idShip2 = tooCloseTo.toString().substring(v.toString().indexOf('#') + 1, v.toString().length() - 1);
                            Geometry p2 = getShipGeometry(idShip2,gm);
                            if(p1!=null&&p2!=null){
                                double distance=p1.distance(p2);
                                SituationData sd = new SituationData(c.toString());
                                sd.distanceToTowShipId = idShip2;
                                sd.distanceToTowShip = distance;
                                sds.add(sd);
                            }
                        }
                    }
                }
            }
        }else if(c.toString().equals(facilityPerimeterViolation.toString())){
            //System.out.println("FacilityPerimeterViolation");
            //FacilityPerimeterViolation
            //Distancia entre el barco y la instalacion. Id de la instalacion.
            //DangerousFacility(?f), Vessel(?x), closeToFacility(?x, ?f) -> FacilityPerimeterViolation(?x)
            //Extraemos el id del barco.
            String idShip = v.toString().substring(v.toString().indexOf('#') + 1, v.toString().length() - 1);
            Geometry p = getShipGeometry(idShip,gm);
            Set<OWLIndividual> closeToFacility = v.getObjectPropertyValues(closeToFacilityProperty, ont);
            for (OWLIndividual facility : closeToFacility){
                //Extraemos el id de la instalacion.
                String idFacility = facility.toString().substring(facility.toString().indexOf('#') + 1, facility.toString().length() - 1);            
                double facilityDistance=-1;
                for (OWLIndividual a : facility.getObjectPropertyValues(allowedDistanceProperty, ont)){
                    for (OWLLiteral d : a.getDataPropertyValues(dProperty, ont)){
                        facilityDistance = d.parseDouble();
                    }
                }
                //Buscamos sus geometrias 
                Geometry g = getFacilityGeometry(idFacility,gm);
                //Calculamos su distancia.
                if(g!=null&&p!=null){
                    double distance=p.distance(g);
                    SituationData sd = new SituationData(c.toString());
                    sd.tooCloseToFacility = facility.toString();
                    sd.distanceToFacility = distance;
                    sd.allowedDistanceToFacility = facilityDistance;
                    sds.add(sd);
                }
            }
        }else{
            //System.out.println("Non recognized behaviour");
        }
        return sds;
    }
    
    public Geometry getFacilityGeometry(String facilityId, GeometricalModel gm){
        Iterator<Facility> it_f = gm.iteratorFacilities();
        Geometry g = null;
        boolean terminado=false;
        while (it_f.hasNext()&&!terminado) {
            Facility f = it_f.next();
            String f_id = f.getId();
            if(f_id.equals(facilityId)){
                terminado=true;
                g = f.getGeometry();
            }
        }
        return g;
    }
    
    public Geometry getShipGeometry(String shipId, GeometricalModel gm){
        Iterator<Ship> it_s = gm.iteratorShips();
        Geometry p = null;
        boolean terminado=false;
        while (it_s.hasNext()&&!terminado) {
            Ship s = it_s.next();
            String s_id = s.getId();
            if(s_id.equals(shipId)){
                terminado=true;
                p = s.getGeometry();
            }
        }
        return p;
    }
}