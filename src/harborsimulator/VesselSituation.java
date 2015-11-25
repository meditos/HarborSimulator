package harborsimulator;

import java.util.ArrayList;
import java.util.HashMap;
import org.w3c.dom.*;

public class VesselSituation {
    
    public String uri;
    public String id;
    public double length;
    public double xPosition;
    public double yPosition;
    public double latspeed;
    public double lonspeed;
    public double speedangle;
    public double speedmod;
    public double adistance;
    public String boatType;
    public ArrayList<SituationData> superClasses;
     
    public VesselSituation(String uri, String id, double length, double xPosition, double yPosition, double latspeed, double lonspeed, double speedangle, double speedmod, double adistance){
        this.uri = uri;
        this.id = id;
        this.length = length;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.latspeed = latspeed;
        this.lonspeed = lonspeed;
        this.speedangle = speedangle;
        this.speedmod = speedmod;
        this.adistance = adistance;
        superClasses = new ArrayList();
    }
    
    public void calculateHeuristics(ArrayList<Double> values){
        for(SituationData sc:superClasses){
            if(sc.behaviour.equals("SpeedViolation")||sc.behaviour.equals("SpeedCompliance")){
                values.add(sc.calculateSpeedHeuristic(speedmod));
            }else if(sc.behaviour.equals("NavigationDirectionViolation")||sc.behaviour.equals("NavigationDirectionCompliance")){
                values.add(sc.calculateNavigationDirectionHeuristic(speedangle));
            }else if(sc.behaviour.equals("TowingNumberViolation")||sc.behaviour.equals("TowingNumberCompliance")){
                values.add(sc.calculateTowingNumberHeuristic(this.length));
            }else if(sc.behaviour.equals("TowingDistanceViolation")||sc.behaviour.equals("TowingDistanceCompliance")){
                //values.add(sc.calculateTowingDistanceHeuristic()); //NO VALE
            }else if(sc.behaviour.equals("DistanceToTowViolation")||sc.behaviour.equals("DistanceToTowCompliance")){
                //values.add(sc.calculateDistanceToTowHeuristic()); //NO VALE
            }else if(sc.behaviour.equals("TowingAlignmentViolation")||sc.behaviour.equals("TowingAlignmentCompliance")){
                values.add(sc.calculateTowingAlignmentHeuristic(speedangle));
            }else if(sc.behaviour.equals("FacilityPerimeterViolation")||sc.behaviour.equals("FacilityPerimeterCompliance")){
                values.add(sc.calculateDistanceToFacilityHeuristic());
            }
        }
    }
    
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(" >> ID >> ");
        s.append(id);
        s.append(" >> Length >> ");
        s.append(length);
        s.append(" >> X,Y position >> ");
        s.append(xPosition+","+yPosition);
        s.append(" >>Angle,Mod Speed >> ");
        s.append(speedangle+","+speedmod);
//        s.append(" >> SuperClasses >> ");
//        for(String sc : superClasses) {
//            s.append(sc.substring(sc.indexOf('#') + 1, sc.length() - 1) + " ");        
//        }
        return s.toString();
    }
    
    public void printXML(DomXML xmlfile, Element timestamp){
        for(SituationData s : this.superClasses){
            Element rViolation = xmlfile.appendElement(timestamp, "restrictionviolation");
            rViolation.setAttribute("type", s.behaviour);
            Element track = xmlfile.appendElement(rViolation, "track");
            track.setAttribute("track_id", id);
            track.setAttribute("boattype", boatType);
            Element position = xmlfile.appendElement(track, "position");
            position.setAttribute("latitude", xPosition+"");
            position.setAttribute("longitude", yPosition+"");
            Element speed = xmlfile.appendElement(track, "speed");
            speed.setAttribute("latspeed", latspeed+"");
            speed.setAttribute("lonspeed", lonspeed+"");
            speed.setAttribute("module", speedmod+"");
            speed.setAttribute("angle", speedangle+"");
            s.printXML(track, xmlfile);
        }
    }
}
