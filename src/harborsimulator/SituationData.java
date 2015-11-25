package harborsimulator;

import org.w3c.dom.Element;
import org.semanticweb.owlapi.model.*;

/**
 * 
 */
public class SituationData {
    
    public XMLArea insideOf;
    public XMLArea alignedTo;
    public XMLArea nonAlignedTo;
    public int recommendedTowingBoatNumber;
    public int currentTowingBoatNumber;
    public String towingDistanceId;
    public double towingDistance;
    public String tooCloseToFacility;
    public double distanceToFacility;
    public double allowedDistanceToFacility;
    public String nonAlignedShipId;
    public double nonAlignedShipAngle;
    public String distanceToTowShipId;
    public double distanceToTowShip;
    public String behaviour;
    
    public SituationData(String behaviour){
        this.behaviour = behaviour.substring(behaviour.indexOf('#')+1, behaviour.length()-1);
    }
    
    public double calculateSpeedHeuristic(double speed){
        if(behaviour.equals("SpeedViolation")){
            //El valor resultante de la resta crece con la degradacion de la situacion por lo tanto es necesario restar 1.
            double expvalue = 1-Math.exp(-0.05*Math.abs(speed-insideOf.module));
//            double logvalue = Math.log(Math.abs(insideOf.module-speed));
//            double expvaluewithfactor = Math.exp(-0.2*Math.abs(insideOf.module-speed));
//            System.out.println("Logaritmo: " + logvalue);
            System.out.println("SpeedViolation");
            System.out.println("Exponencial: " + expvalue);
//            System.out.println("Exponencial con factor: " + expvaluewithfactor);
            return expvalue;
        }else if(behaviour.equals("SpeedCompliance")){
            return 0;
        }
        return 0;
    }
    
    //AllowedAngle=10
    //La primera resta deberia ser >= 10
    public double calculateNavigationDirectionHeuristic(double angle){
        if(behaviour.equals("NavigationDirectionViolation")){
            double resta = Math.abs(angle-nonAlignedTo.angle);
            //Si la resta es > 180 le restamos 360 para obtener el número de grados de diferencia entre los 2 angulos.
            if(resta>180){
                resta = Math.abs(resta-360);
            }
            double angvalue=0;
            double k=0.05;
            if(resta<=90){
                double restaRadianes=Math.toRadians(resta);
                angvalue=(Math.sin(restaRadianes)/2);
                angvalue=k*angvalue;
            }else if (resta>90 && resta<=180){
                double restaRadianes=Math.toRadians(resta);
                angvalue=(-Math.cos(restaRadianes)/2)+0.5;
                angvalue=k*angvalue;
            }else{
                System.out.println("Error in the TowingAlignmentViolation calculus.");
            }
            System.out.println("GRADOS: " + resta);
            //ROGOVA
            //double angvalue = 1+(-1)*Math.cos(resta)/2;//allowedAngle
            System.out.println("NavigationDirectionViolation");
            System.out.println("Angulo: " + angvalue);
            System.out.println("El siguiente valor deberia ser >= 10: " + resta);
            return angvalue;
        }else if(behaviour.equals("NavigationDirectionCompliance")){
            return 0;
        }
        return 0;
    }
    
    //Extraer RecommendedTowingDistance= Entre 10 y 20. 15 +- 5. 
    //En este caso vamos a utilizar 20 porque no va a haber barcos demasiado proximos.
    //towingAllowedDistance=20
    public double calculateTowingDistanceHeuristic(){
        if(behaviour.equals("TowingDistanceViolation")){
            //El valor resultante de la resta crece con la degradacion de la situacion por lo tanto es necesario restar 1.
            double distvalue = 1-Math.exp(-0.05*Math.abs(towingDistance-20));//towingAllowedDistance
            System.out.println("TowingDistanceViolation");
            System.out.println("TowingDistance: " + distvalue);
            System.out.println("El siguiente valor deberia ser >= 20: " + towingDistance);
            return distvalue;
        }else if(behaviour.equals("TowingDistanceCompliance")){
            return 0;
        }
        return 0;
    }
    
    //La resta deberia ser >= 5.
    //allowedDistanceToTow=5 incluido.
    public double calculateDistanceToTowHeuristic(){
        if(behaviour.equals("DistanceToTowViolation")){
            //El valor resultante de la resta crece con la degradacion de la situacion por lo tanto es necesario restar 1.
            double distvalue = 1-Math.exp(-0.05*Math.abs(distanceToTowShip-5));
            System.out.println("DistanceToTowViolation");
            System.out.println("DistanceToTow: " + distvalue);
            System.out.println("El siguiente valor deberia ser <= 5: " + distanceToTowShip);
            return distvalue;
        }else if(behaviour.equals("DistanceToTowCompliance")){
            return 0;
        }
        return 0;
    }
    
    //La primera resta deberia ser >= 10.
    //towingAllowedAngle=10 incluido.
    public double calculateTowingAlignmentHeuristic(double angle){
        if(behaviour.equals("TowingAlignmentViolation")){
            //Aqui hay que tener en cuenta los angulos que estamos tratando a la hora de hacer la resta.
            //Mientras la resta sea <= 180 no hay problema.
            double resta=Math.abs(angle-nonAlignedShipAngle);
            //Si la resta es > 180 le restamos 360 para obtener el número de grados de diferencia entre los 2 angulos.
            if(resta>180){
                resta = Math.abs(resta-360);
            }
            double angvalue=0;
            double k=0.05;
            if(resta<=90){
                double restaRadianes=Math.toRadians(resta);
                angvalue=(Math.sin(restaRadianes)/2);
                angvalue=1-Math.exp(-k*angvalue);
            }else if (resta>90 && resta<=180){
                double restaRadianes=Math.toRadians(resta);
                angvalue=(-Math.cos(restaRadianes)/2)+0.5;
                angvalue=1-Math.exp(-k*angvalue);
            }else{
                System.out.println("Error in the TowingAlignmentViolation calculus.");
            }
            //ROGOVA
            //double angvalue = 1+(-1)*Math.cos(resta)/2;//towingAllowedAngle
            System.out.println("TowingAlignmentViolation");
            System.out.println("TowingAlignment: " + angvalue);
            System.out.println("El siguiente valor deberia ser >= 10: " + resta);
            return angvalue;
        }else if(behaviour.equals("TowingAlignmentCompliance")){
            return 0;
        }
        return 0;
    }
    
    public double calculateTowingNumberHeuristic(double length){
        if(behaviour.equals("TowingNumberViolation")){
            int alpha = 1;
            double base = Math.E;
            int beta = 2;
            int lambda = 2; //Math.pow(2,recommendedTowingBoatNumber)
            //int exponente = beta*Math.abs(currentTowingBoatNumber-recommendedTowingBoatNumber);
            int exponente = (int)(5*(1+Math.abs(185-length))); 
            //Diferencia entre la longitud que tiene y la que deberia tener un barco que tuviese ese numero de tugboats.
            System.out.println("EXPONENTE: " + exponente);
            System.out.println("DENOMINADOR: " + exponente*(1+Math.pow(base,-exponente)));
            System.out.println("DENNY: " + (1+Math.pow(base,-exponente)));
            //recommendedTowingBoatNumber-currentTowingBoatNumber
            //Segun se degrada la situacion el exponente es mayor, por lo tanto restamos 1.
            //double numbervalue = 1-(lambda/(1+(1*Math.pow(base,exponente))));
            //double numbervalue=Math.pow(recommendedTowingBoatNumber,exponente)/Math.pow(recommendedTowingBoatNumber,recommendedTowingBoatNumber);
            double numbervalue=0.5/exponente*(1+Math.pow(base,-exponente));
            
            //numbervalue=numbervalue*0.15;
            System.out.println("TowingNumberViolation");
            System.out.println("TowingNumber: " + numbervalue);
            System.out.println("CurrentTowingBoatNumber: " +currentTowingBoatNumber);
            return numbervalue;
        }else if(behaviour.equals("TowingNumberCompliance")){
            return 0;
        }
        return 0;
    }
    
    public double calculateDistanceToFacilityHeuristic(){
        if(behaviour.equals("FacilityPerimeterViolation")){
            //El valor resultante de la resta crece con la degradacion de la situacion por lo tanto es necesario restar 1.
            double distvalue = 1-Math.exp(-0.05*Math.abs(distanceToFacility-allowedDistanceToFacility));
            System.out.println("FacilityPerimeterViolation");
            System.out.println("FacilityDistance: " + distvalue);
            System.out.println("AllowedDistance: " + allowedDistanceToFacility); 
            System.out.println("El siguiente valor deberia ser <= 25: " + distanceToFacility);
            return distvalue;
        }else if(behaviour.equals("FacilityPerimeterCompliance")){
            return 0;
        }
        return 0;
    }
    
    public void printXML(Element track, DomXML xmlfile){
        if(behaviour.equals("SpeedViolation")||behaviour.equals("SpeedCompliance")){
            Element areaData = xmlfile.appendElement(track, "speedarea");
            areaData.setAttribute("id", insideOf.id);
            areaData.setAttribute("module", insideOf.module+"");
        }else if(behaviour.equals("NavigationDirectionCompliance")){
            Element areaData = xmlfile.appendElement(track, "alignmentarea");
            areaData.setAttribute("id", alignedTo.id);
            areaData.setAttribute("angle", alignedTo.angle+"");
        }else if(behaviour.equals("NavigationDirectionViolation")){
            Element areaData = xmlfile.appendElement(track, "nonalignmentarea");
            areaData.setAttribute("id", nonAlignedTo.id);
            areaData.setAttribute("angle", nonAlignedTo.angle+"");
        }else if(behaviour.equals("TowingNumberViolation")||behaviour.equals("TowingNumberCompliance")){
            Element towingBoatNumber = xmlfile.appendElement(track, "towingboatnumber");
            towingBoatNumber.setAttribute("recommendedtowingboatnumber", recommendedTowingBoatNumber+"");
            towingBoatNumber.setAttribute("currenttowingboatnumber", currentTowingBoatNumber+"");
        }else if(behaviour.equals("TowingDistanceViolation")||behaviour.equals("TowingDistanceCompliance")){
            Element towingDist = xmlfile.appendElement(track, "towingdistance");
            towingDist.setAttribute("id", towingDistanceId+"");
            towingDist.setAttribute("distance", towingDistance+"");
        }else if(behaviour.equals("DistanceToTowViolation")){
            Element distanceToTow = xmlfile.appendElement(track, "distancetotow");
            distanceToTow.setAttribute("id", distanceToTowShipId+"");
            distanceToTow.setAttribute("distance", distanceToTowShip+"");
        }else if(behaviour.equals("TowingAlignmentViolation")){
            Element towingAlignment = xmlfile.appendElement(track, "towingalignment");
            towingAlignment.setAttribute("id", nonAlignedShipId+"");
            towingAlignment.setAttribute("angle", nonAlignedShipAngle+"");
        }else if(behaviour.equals("FacilityPerimeterViolation")){
            Element facility = xmlfile.appendElement(track, "dangerousfacility");
            facility.setAttribute("id", tooCloseToFacility);
            facility.setAttribute("distance", distanceToFacility+"");
        }
    }
}
