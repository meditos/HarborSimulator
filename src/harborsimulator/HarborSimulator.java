/* Harbor Simulator 0.1 */ 
package harborsimulator;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.Axis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.*;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.semanticweb.owlapi.model.IRI;


public class HarborSimulator extends Application {
    
    private String  data_path = "";
    private boolean data_load = false; 
    private String  onto_path = "";
    private String  dump_path = "";
    private boolean onto_load = false; 
    private String  kml_path  = "";
    
    //private WebView mapWebView;
    private Canvas canvasForTrajectories;
    private Canvas canvasForFacilities;
    private Canvas canvasForAreas;
    private Canvas canvasForShips;
    private GraphicsContext gcTrajectories;
    private GraphicsContext gcFacilities;
    private GraphicsContext gcAreas;
    private GraphicsContext gcShips;
    private AreaChart lc;
    private NumberAxis xAxis;
    
    private WebEngine messagesEngine;
    private ArrayList<String> logMessages;
    
    private ArrayList<URL> shipIconsURLs;
    private Process p;
    
    private Stage ps;
    
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) {
        ps = primaryStage;
        primaryStage.setTitle("Test harbor simulator");
        
        //Create a canvas
        canvasForTrajectories = new Canvas(800,500);
        canvasForTrajectories.autosize();
        gcTrajectories = canvasForTrajectories.getGraphicsContext2D();
        gcTrajectories.setLineWidth(0.5);
        gcTrajectories.clearRect(0, 0, canvasForTrajectories.getWidth(), canvasForTrajectories.getHeight());
        gcTrajectories.strokeRect(1, 1, canvasForTrajectories.getWidth()-2, canvasForTrajectories.getHeight()-2);
        
        canvasForFacilities = new Canvas(800,500);
        canvasForFacilities.autosize();
        gcFacilities = canvasForFacilities.getGraphicsContext2D();
        gcFacilities.clearRect(0, 0, canvasForFacilities.getWidth(), canvasForFacilities.getHeight());
        gcFacilities.setStroke(Color.BLACK);
        gcFacilities.strokeRect(1, 1, canvasForFacilities.getWidth()-2, canvasForFacilities.getHeight()-2);
        gcFacilities.setFill(new Color(Color.RED.getRed(), Color.RED.getGreen(), Color.RED.getBlue(), 0.6));
        
        
        canvasForAreas = new Canvas(800,500);
        canvasForAreas.autosize();
        gcAreas = canvasForAreas.getGraphicsContext2D();
        gcAreas.clearRect(0, 0, canvasForAreas.getWidth(), canvasForAreas.getHeight());
        gcAreas.setStroke(new Color( 0, 0, Color.BLUE.getBlue(), 0.7));
        gcAreas.strokeRect(1, 1, canvasForAreas.getWidth()-2, canvasForAreas.getHeight()-2);
        gcAreas.setFill(new Color( 0, 0, Color.BLUE.getBlue(), 0.1));
        
        canvasForShips = new Canvas(800,500);
        canvasForShips.autosize();
        gcShips = canvasForShips.getGraphicsContext2D();
        gcShips.clearRect(0, 0, canvasForShips.getWidth(), canvasForShips.getHeight());
        gcShips.setStroke(Color.RED);
        gcShips.strokeRect(1, 1, canvasForShips.getWidth()-2, canvasForShips.getHeight()-2);
        gcShips.setFill(Color.RED);
        
        xAxis = new NumberAxis("Time", 0, 5, 1);
        xAxis.setAutoRanging(true);
        NumberAxis yAxis = new NumberAxis("Belief", 0, 1, 0.25);
        lc = new AreaChart(xAxis, yAxis);
        
        lc.getStylesheets().add("css/Chart.css");
        
        // >> Create menu bar        
        MenuBar menuBar = new MenuBar();
        final Menu menuFile = new Menu("File");
        final Menu menuScene = new Menu("Scene");
        menuScene.setDisable(true);
        final Menu menuMap = new Menu("Map");
        menuMap.setDisable(true);
        menuMap.getStyleClass().add("map-toolbar");

        // -- Menu File 
        final MenuItem loadOntology, loadData, exit;
        loadOntology = new MenuItem("Load ontology...");
        loadData = new MenuItem("Load data...");    
        exit = new MenuItem("Exit");

        // - Load ontology
        final HarborSimulator hs = this;
        loadOntology.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                File f = showOpenFile();
                if (f != null) {
                    try {
                        onto_path = f.toURI().getPath();
                        IRI onto = IRI.create(f.toURI());
                        
                        clearAreas();
                        clearShips();
                        p = new Process(onto, hs);
                        
                        loadData.setDisable(false);   
                        menuScene.setDisable(true);
                    } catch(Exception e) {
                        log(e.getMessage());
                    }
                }
            }
        });
        
        // - Load data file
        loadData.setDisable(true);
        loadData.setOnAction(new EventHandler<ActionEvent>() { 
            @Override
            public void handle(ActionEvent t) {
                
                // Set data file                
                File f = showOpenFile();
                if (f != null) {
                    data_path = f.getAbsolutePath();
                    
                    try {
                        p.openDataFile(data_path, hs);
                        menuScene.setDisable(false);
                    } catch(CannotCreateProcessException e) {
                        log(e.getMessage());
                    }                                    
                }
            }
        });
                
        
        // - Exit
        exit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                Platform.exit();
            }
        });
        
        menuFile.getItems().addAll(loadOntology, loadData, new SeparatorMenuItem(), exit);                
        
        // -- Menu Scene
        
        // - Next piece of data
        final MenuItem next;
        next = new MenuItem("Next");
        next.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                try {
                    p.next(hs);
                } catch (BadDataFileFormatException e) {
                    log(e.getMessage());
                }
            }
        });
        next.setAccelerator(KeyCombination.keyCombination("Ctrl+N"));
        
        // - Dump ontology
        final MenuItem dump;
        dump = new MenuItem("Dump ontology...");
        dump.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                File f = showSaveFile();
                if (f != null) {                                        
                    try {
                        p.dump(f);                        
                    } catch(CannotWriteOntologyException e) {
                        log(e.getMessage());
                    }                                    
                }                                
            }
        });        
        
        menuScene.getItems().addAll(next, new SeparatorMenuItem() , dump);
        
        menuBar.getMenus().addAll(menuFile, menuScene); // , menuMap);        
        
        // >> Create info tab
        ToolBar toolBar = new ToolBar();
        Button clear = new Button("Clear");
        clear.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                clearLog();
            }
        });
        Button save = new Button("Save");
        save.setVisible(false);
        
        toolBar.getItems().addAll(clear, save);
        
        BorderPane infoPane = new BorderPane();        
        WebView messages = new WebView();
        messagesEngine = messages.getEngine();       
        logMessages = new ArrayList();
        
        infoPane.setTop(toolBar);
        infoPane.setCenter(messages);       
        
        
        //>> Create a pane
        
        //AnchorPane, GridPane, HBox, StackPane, TilePane, VBox
        StackPane canvas = new StackPane();
        //canvas.setPrefSize(800,500);
        canvas.getChildren().addAll(canvasForAreas,canvasForFacilities,canvasForTrajectories,canvasForShips);
        
        // >> Create horizontal split pane
        SplitPane animations = new SplitPane();
        animations.setOrientation(Orientation.HORIZONTAL);
        animations.setDividerPositions(0.615, 1);
        //animations.getItems().addAll(canvas, lc);
        animations.getItems().addAll(canvas, lc);
        
        
        // >> Create vertical split pane
        SplitPane contents = new SplitPane();        
        contents.setOrientation(Orientation.VERTICAL);
        contents.setDividerPositions(0.77, 1);
        contents.getItems().addAll(animations, infoPane);
        
        // >> Create root panel
        BorderPane root = new BorderPane();   
        root.setCenter(contents);
        root.setTop(menuBar);
                  
        // >> Start scene
        Scene scene = new Scene(root, 1300, 680, Color.web("#666970"));
        primaryStage.setScene(scene);
        log("Application started");   
        primaryStage.setResizable(true);
        primaryStage.show();
    } 
    
    /** Log information 
        @param msg Message to log 
        @param format Format of the message @todo */
    public void log(String msg, String... format) {
        
        // Escape message!
        msg.replaceAll("<", "&lt");
        msg.replaceAll(">", "&gt");        
        
        logMessages.add(0, "[" + Calendar.getInstance().getTime() + "] " + msg);
        
        StringBuilder m = new StringBuilder();
        m.append(
            "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" " +
            "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\"> " +
            "<html xmlns=\"http://www.w3.org/1999/xhtml\"> " +
            "<head>" +
                "<title>Log pane</title>" +
                "<style type=\"text/css\">" +
                    "body { "+
                        "padding-left: 1em;"+
                        "font-family: 'Courier New', Courier, monospace;" +
                        "font-size: small; " +
                        "color: gray;"+
                        "background-color: #ffffff; "+
                    "}" +
                    "p {" +
                        "margin-top: 0.2em; "+
                        "margin-bottom: 0.2em; "+
                    "}" +
                "</style>" +       
            "</head>" +
            "<body>"
        );
        
        for(String s : logMessages) {
            m.append("<p>");            
            m.append(s);
            m.append("</p>");
        }
        
        m.append("</body>");
        
        messagesEngine.loadContent(m.toString());
  
    }
    
    /** Clear log */
    public void clearLog() {
        logMessages.clear();
        
        StringBuilder m = new StringBuilder();
        m.append(
            "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" " +
            "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\"> " +
            "<html xmlns=\"http://www.w3.org/1999/xhtml\"> " +
            "<head>" +
                "<title>Log pane</title>" +                       
            "</head>" +
            "<body>" +
            "</body>"
        );
        messagesEngine.loadContent(m.toString());
    }
    
    /** Show message window 
        @param message Message to print */
    public void showMessage(String message) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.setScene(new Scene(VBoxBuilder.create().
                children(new Text(message)).
                alignment(Pos.CENTER).padding(new Insets(5)).build()));
        dialogStage.show();
    }
    
    /** Show open file dialog */
    public File showOpenFile() {
        FileChooser fc = new FileChooser();
        try {
            fc.setInitialDirectory(
                new File(HarborSimulator.class.getResource("/files").toURI()));
        } catch(Exception e) {
            // do nothing
        }
        File f = fc.showOpenDialog(ps); 
        return f;
    }
    
     /** Show open file dialog */
     public File showSaveFile() {
        FileChooser fc = new FileChooser();
        try {
            fc.setInitialDirectory(
                new File(HarborSimulator.class.getResource("/files").toURI()));
        } catch(Exception e) {
            // do nothing
        }
        return fc.showSaveDialog(ps);
    }
     
     
    /** Clear all facilities in map */
    public void clearFacilities() {
        gcFacilities.clearRect(0, 0, canvasForFacilities.getWidth(), canvasForFacilities.getHeight());
        
    }
    
    /** Clear all areas in map */
    public void clearAreas() {
        gcAreas.clearRect(0, 0, canvasForAreas.getWidth(), canvasForAreas.getHeight());
    }
    
    /** Clear all ships in map */
    public void clearShips() {
        gcShips.clearRect(0, 0, canvasForShips.getWidth(), canvasForShips.getHeight());
    }
    
   /** Paint facility on map 
     *  @param id Facility identifier
     *  @param lat Latitudes array
     *  @param lon Longitudes array 
     *  @param description Facility description*/
    public void paintFacility(String id, double [] lat, double [] lon, String description) {
        if(lat.length==lon.length){
            gcFacilities.strokePolygon(lon, lat, lat.length);
            gcFacilities.fillPolygon(lon, lat, lat.length);
            gcFacilities.strokeText(getAbbreviature(id), lon[0], lat[0]);
            //      THESE CODE SORTS THE POINTS OF A GEOMETRY
//            double longitudes [] = new double[lon.length];
//            double latitudes [] = new double[lat.length];
//            ArrayList<Double> lonArray = new ArrayList<>();
//            ArrayList<Double> latArray = new ArrayList<>();
//            for(int i=0; i<lat.length; i++){
//                boolean notRepeated = true;
//                for(int j=i; j<lat.length; j++){
//                    if(j!=i && lon[i]==lon[j] && lat[i]==lat[j]){
//                        notRepeated=false;
//                    }
//                }
//                if(notRepeated){
//                    lonArray.add(lon[i]);
//                    latArray.add(lat[i]);
//                }
//            }
//            double previousLon = -1;
//            double previousLat = -1;
//            int arraySize=lonArray.size();
//            //Iterator<Double> lonIt = lonArray.iterator();
//            //Iterator<Double> latIt = latArray.iterator();
//            for(int i=0;i<arraySize; i++){
//                int value=0;
//                if(previousLon!=-1 && previousLat!=-1){
//                    double distance = -1;
//                    int j=0;
//                    while(j<lonArray.size()){
//                        Double longitud = lonArray.get(j);//lonIt.next();
//                        Double latitud = latArray.get(j);//latIt.next();
//                        double currentDistance=Math.sqrt(Math.pow((longitud-previousLon),2) + Math.pow((latitud-previousLat),2));
//                        if(distance==-1){
//                            distance=currentDistance;
//                            value=j;//lonArray.indexOf(longitud);
//                        }else if(currentDistance!=0 && distance>currentDistance){
//                            distance=currentDistance;
//                            value=j;//lonArray.indexOf(longitud);
//                        }
//                        j++;
//                    }
//                }
//                previousLon=lonArray.get(value);
//                previousLat=latArray.get(value);
//                longitudes[i]= lonArray.get(value);
//                latitudes[i]= latArray.get(value);
//                lonArray.remove(value);
//                latArray.remove(value);
//            }
//            longitudes[longitudes.length-1]=longitudes[0];
//            latitudes[latitudes.length-1]=latitudes[0];
        }else{
            System.out.println("Bad facility definition");
        } 
    }
    
    /** Paint area on map 
     *  About loading KML in Google Maps: http://stackoverflow.com/questions/6092110/google-maps-api-and-kml-file-localhost-development-options/
     *  @param id Area identifier
     *  @param lat Latitudes array
     *  @param lon Longitudes array 
     *  @param description Area description*/
    public void paintArea(String id, double [] lat, double [] lon, String description) {
        if(lat.length==lon.length){
            //RELLENAR EL POLIGONO CON COLORES TRANSPARENTES
            gcAreas.strokeText(getAbbreviature(id), lon[0], lat[0]);
            gcAreas.fillPolygon(lon, lat, lat.length);
            gcAreas.strokePolygon(lon, lat, lat.length);
        }else{
            System.out.println("Bad area definition");
        }                
    }
    
    public String getAbbreviature(String id){
        String text="";
        if(id.equals("generalCargoChannelToNorth")){
            text = "GCN";
            gcAreas.setStroke(new Color( 0, 0, Color.BLUE.getBlue(), 0.7));
            gcAreas.setFill(new Color( 0, 0, Color.BLUE.getBlue(), 0.1));
        }else if(id.equals("generalCargoChannelToNorth1")){
            text= "GCN1";
            gcAreas.setStroke(new Color( 0, 0, Color.BLUE.getBlue(), 0.7));
            gcAreas.setFill(new Color( 0, 0, Color.BLUE.getBlue(), 0.1));
        }else if(id.equals("generalCargoChannelToNorth2")){
            text= "GCN2";
            gcAreas.setStroke(new Color( 0, 0, Color.BLUE.getBlue(), 0.7));
            gcAreas.setFill(new Color( 0, 0, Color.BLUE.getBlue(), 0.1));
        }else if(id.equals("generalCargoChannelToSouth")){
            text= "GCS";
            gcAreas.setStroke(new Color( 0, 0, Color.BLUE.getBlue(), 0.7));
            gcAreas.setFill(new Color( 0, 0, Color.BLUE.getBlue(), 0.1));
        }else if(id.equals("generalCargoChannelToSouth1")){
            text= "GCS1";
            gcAreas.setStroke(new Color( 0, 0, Color.BLUE.getBlue(), 0.7));
            gcAreas.setFill(new Color( 0, 0, Color.BLUE.getBlue(), 0.1));
        }else if(id.equals("generalCargoChannelToSouth2")){
            text= "GCS2";
            gcAreas.setStroke(new Color( 0, 0, Color.BLUE.getBlue(), 0.7));
            gcAreas.setFill(new Color( 0, 0, Color.BLUE.getBlue(), 0.1));
        }else if(id.equals("generalCargoChannelEast")){
            text= "GCE";
            gcAreas.setStroke(new Color( 0, 0, Color.BLUE.getBlue(), 0.7));
            gcAreas.setFill(new Color( 0, 0, Color.BLUE.getBlue(), 0.1));
        }else if(id.equals("generalCargoChannelWest")){
            text= "GCW";
            gcAreas.setStroke(new Color( 0, 0, Color.BLUE.getBlue(), 0.7));
            gcAreas.setFill(new Color( 0, 0, Color.BLUE.getBlue(), 0.1));
        }else if(id.equals("harborShipsEastWest")){
            text= "HSEW";
            gcAreas.setStroke(new Color(Color.GREY.getRed(), Color.GREY.getGreen(), Color.GREY.getBlue(), 0.7));
            gcAreas.setFill(new Color(Color.SILVER.getRed(), Color.SILVER.getGreen(), Color.SILVER.getBlue(), 0.1));
        }else if(id.equals("harborShipsNorthSouth1")){
            text= "HSNS1";
            gcAreas.setStroke(new Color(Color.GREY.getRed(), Color.GREY.getGreen(), Color.GREY.getBlue(), 0.7));
            gcAreas.setFill(new Color(Color.SILVER.getRed(), Color.SILVER.getGreen(), Color.SILVER.getBlue(), 0.1));
        }else if(id.equals("harborShipsNorthSouth2")){
            text= "HSNS2";
            gcAreas.setStroke(new Color(Color.GREY.getRed(), Color.GREY.getGreen(), Color.GREY.getBlue(), 0.7));
            gcAreas.setFill(new Color(Color.SILVER.getRed(), Color.SILVER.getGreen(), Color.SILVER.getBlue(), 0.1));
        }else if(id.equals("smallBoatsEastWest")){
            text= "SBEW";
            gcAreas.setStroke(new Color(Color.GREEN.getRed(), Color.GREEN.getGreen(), Color.GREEN.getBlue(), 0.7));
            gcAreas.setFill(new Color(Color.GREEN.getRed(), Color.GREEN.getGreen(), Color.GREEN.getBlue(), 0.1));
        }else if(id.equals("smallBoatsNorthSouth1")){
            text= "SBNS1";
            gcAreas.setStroke(new Color(Color.GREEN.getRed(), Color.GREEN.getGreen(), Color.GREEN.getBlue(), 0.7));
            gcAreas.setFill(new Color(Color.GREEN.getRed(), Color.GREEN.getGreen(), Color.GREEN.getBlue(), 0.1));
        }else if(id.equals("smallBoatsNorthSouth2")){
            text= "SBNS2";
            gcAreas.setStroke(new Color(Color.GREEN.getRed(), Color.GREEN.getGreen(), Color.GREEN.getBlue(), 0.7));
            gcAreas.setFill(new Color(Color.GREEN.getRed(), Color.GREEN.getGreen(), Color.GREEN.getBlue(), 0.1));
        }else if(id.equals("specialContainerChannelToEast1")){
            text= "SCE1";
            gcAreas.setStroke(new Color(Color.ORANGE.getRed(), Color.ORANGE.getGreen(), Color.ORANGE.getBlue(), 0.7));
            gcAreas.setFill(new Color(Color.ORANGE.getRed(), Color.ORANGE.getGreen(), Color.ORANGE.getBlue(), 0.1));
        }else if(id.equals("specialContainerChannelToEast2")){
            text= "SCE2";
            gcAreas.setStroke(new Color(Color.ORANGE.getRed(), Color.ORANGE.getGreen(), Color.ORANGE.getBlue(), 0.7));
            gcAreas.setFill(new Color(Color.ORANGE.getRed(), Color.ORANGE.getGreen(), Color.ORANGE.getBlue(), 0.1));
        }else if(id.equals("specialContainerChannelToEast3")){
            text= "SCE3";
            gcAreas.setStroke(new Color(Color.ORANGE.getRed(), Color.ORANGE.getGreen(), Color.ORANGE.getBlue(), 0.7));
            gcAreas.setFill(new Color(Color.ORANGE.getRed(), Color.ORANGE.getGreen(), Color.ORANGE.getBlue(), 0.1));
        }else if(id.equals("specialContainerChannelToWest1")){
            text= "SCW1";
            gcAreas.setStroke(new Color(Color.ORANGE.getRed(), Color.ORANGE.getGreen(), Color.ORANGE.getBlue(), 0.7));
            gcAreas.setFill(new Color(Color.ORANGE.getRed(), Color.ORANGE.getGreen(), Color.ORANGE.getBlue(), 0.1));
        }else if(id.equals("specialContainerChannelToWest2")){
            text= "SCW2";
            gcAreas.setStroke(new Color(Color.ORANGE.getRed(), Color.ORANGE.getGreen(), Color.ORANGE.getBlue(), 0.7));
            gcAreas.setFill(new Color(Color.ORANGE.getRed(), Color.ORANGE.getGreen(), Color.ORANGE.getBlue(), 0.1));
        }else if(id.equals("specialContainerChannelToWest3")){
            text= "SCW3";
            gcAreas.setStroke(new Color(Color.ORANGE.getRed(), Color.ORANGE.getGreen(), Color.ORANGE.getBlue(), 0.7));
            gcAreas.setFill(new Color(Color.ORANGE.getRed(), Color.ORANGE.getGreen(), Color.ORANGE.getBlue(), 0.1));
        }else if(id.equals("FuelTank")){
            text= "LFT";
        }
        return text;
    }
    
    public void updatePoints(Iterator<Ship> ships, double time){
        clearShips();
        while(ships.hasNext()){
            Ship s = ships.next();
            int size=4;
            if(s.getLength()>=170){
                gcShips.setStroke(new Color(Color.RED.getRed(), Color.RED.getGreen(), Color.RED.getBlue(), 0.4));
                //gcShips.setFill(new Color(Color.YELLOW.getRed(), Color.YELLOW.getGreen(), Color.YELLOW.getBlue(), 0.2));
                gcShips.strokeOval(s.getLon()-30, s.getLat()-30, 60, 60);
                //gcShips.fillOval(s.getLon()-40, s.getLat()-40, 80, 80);
                gcShips.setStroke(new Color(Color.GREEN.getRed(), Color.GREEN.getGreen(), Color.GREEN.getBlue(), 0.4));
                //gcShips.setFill(new Color(Color.TURQUOISE.getRed(), Color.TURQUOISE.getGreen(), Color.TURQUOISE.getBlue(), 0.2));
                gcShips.strokeOval(s.getLon()-20, s.getLat()-20, 40, 40);
                //gcShips.fillOval(s.getLon()-20, s.getLat()-20, 40, 40);
            }
            switch (s.getId()) {
                case "s1":
                    gcShips.setStroke(Color.RED.saturate());
                    gcShips.setFill(Color.RED.saturate());
                    //gcShips.fillOval(s.getLon()-(size/2), s.getLat()-(size/2), size, size);
                    int npoints = 3;
                    double longitudes [] = new double[npoints];
                    double latitudes[] = new double[npoints];
                    longitudes[0] = s.getLon()-2;//IZQUIERDA
                    longitudes[1] = s.getLon();//ARRIBA
                    longitudes[2] = s.getLon()+2;//DERECHA
                    latitudes[0] = s.getLat()+2;//IZQUIERDA
                    latitudes[1] = s.getLat()-2;//ARRIBA
                    latitudes[2] = s.getLat()+2;//DERECHA
                    gcShips.fillPolygon(longitudes, latitudes, npoints);
                    gcShips.strokePolygon(longitudes, latitudes, npoints);
                    gcTrajectories.setStroke(Color.RED.saturate());
                    gcTrajectories.setFill(Color.RED.saturate());
                    break;
                case "s2":
                    gcShips.setStroke(Color.NAVY.saturate());
                    gcShips.setFill(Color.NAVY.saturate());
                    gcShips.fillOval(s.getLon()-(size/2), s.getLat()-(size/2), size, size);
                    gcShips.strokeOval(s.getLon()-(size/2), s.getLat()-(size/2), size, size);
                    gcTrajectories.setStroke(Color.NAVY.saturate());
                    gcTrajectories.setFill(Color.NAVY.saturate());
                    break;
                case "s3":
                    gcShips.setStroke(Color.DARKGREEN.saturate());
                    gcShips.setFill(Color.DARKGREEN.saturate());
                    npoints = 8;
                    longitudes = new double[npoints];
                    latitudes = new double[npoints];
                    longitudes[0] = s.getLon()-3;//IZQUIERDA ARRIBA
                    longitudes[1] = s.getLon();//CENTRO
                    longitudes[2] = s.getLon()+3;//DERECHA ARRIBA
                    longitudes[3] = s.getLon();//CENTRO
                    longitudes[4] = s.getLon()+3;//DERECHA ABAJO
                    longitudes[5] = s.getLon();//CENTRO
                    longitudes[6] = s.getLon()-3;//IZQUIERDA ABAJO
                    longitudes[7] = s.getLon();//CENTRO
                    
                    latitudes[0] = s.getLat()-3;//IZQUIERDA ARRIBA
                    latitudes[1] = s.getLat();//CENTRO
                    latitudes[2] = s.getLat()-3;//DERECHA ARRIBA
                    latitudes[3] = s.getLat();//CENTRO
                    latitudes[4] = s.getLat()+3;//DERECHA ABAJO
                    latitudes[5] = s.getLat();//CENTRO
                    latitudes[6] = s.getLat()+3;//IZQUIERDA ABAJO
                    latitudes[7] = s.getLat();//CENTRO
                    gcShips.fillPolygon(longitudes, latitudes, npoints);
                    gcShips.strokePolygon(longitudes, latitudes, npoints);
                    //gcShips.fillOval(s.getLon()-(size/2), s.getLat()-(size/2), size, size);
                    gcTrajectories.setStroke(Color.FORESTGREEN.saturate());
                    gcTrajectories.setFill(Color.FORESTGREEN.saturate());
                    break;
                case "s4":
                    gcShips.setStroke(Color.PURPLE.saturate());
                    gcShips.setFill(Color.PURPLE.saturate());
                    npoints = 4;
                    longitudes = new double[npoints];
                    latitudes = new double[npoints];
                    longitudes[0] = s.getLon()-3;//IZQUIERDA
                    longitudes[1] = s.getLon();//ARRIBA
                    longitudes[2] = s.getLon()+3;//DERECHA
                    longitudes[3] = s.getLon();//ABAJO
                    latitudes[0] = s.getLat();//IZQUIERDA
                    latitudes[1] = s.getLat()-3;//ARRIBA
                    latitudes[2] = s.getLat();//DERECHA
                    latitudes[3] = s.getLat()+3;//ABAJO
                    gcShips.fillPolygon(longitudes, latitudes, npoints);
                    gcShips.strokePolygon(longitudes, latitudes, npoints);
                    //gcShips.fillOval(s.getLon()-(size/2), s.getLat()-(size/2), size, size);
                    gcTrajectories.setStroke(Color.PURPLE.saturate());
                    gcTrajectories.setFill(Color.PURPLE.saturate());
                    break;
                default:
                    gcShips.setStroke(Color.BLACK);
                    gcShips.setFill(Color.BLACK.saturate());
                    gcShips.fillOval(s.getLon()-(size/2), s.getLat()-(size/2), size, size);
                    gcTrajectories.setStroke(Color.BLACK);
                    gcTrajectories.setFill(Color.BLACK);
                    break;
            }
            //gcShips.fillOval(s.getLon()-(size/2), s.getLat()-(size/2), size, size);
            
            //fillRect(s.getLon()-(size/2), s.getLat()-(size/2), size, size);
            //gcShips.strokeOval(s.getLon()-(size/2), s.getLat()-(size/2), size, size);
//            
//            double desviacionTextoLat=0;
//            double desviacionTextoLon=0;
//            double angulo = s.getAngS();
//            if(angulo>45 && angulo<=135){
//                desviacionTextoLon=-5;
//                desviacionTextoLat=-10;
//            }else if(angulo>135 && angulo<=225){
//                desviacionTextoLon=-20;
//                desviacionTextoLat=0;
//            }else if(angulo>225 && angulo<=315){
//                desviacionTextoLon=-5;
//                desviacionTextoLat=10;
//            }else if(angulo>315 && angulo<45){
//                desviacionTextoLon=0;
//                desviacionTextoLat=0;
//            }
            //gcShips.fillText(s.getId(), s.getLon()+desviacionTextoLon, s.getLat()+desviacionTextoLat); //Colocar el texto donde mas convenga.
            //Image image1 = new Image("files/boat-red.png", false);
            //gcShips.drawImage(image1, s.getLon(), s.getLat());
            if(s.getLength()<=15){
                gcShips.strokeOval(s.getLon()-5, s.getLat()-5, 10, 10);
            }
            //AMPLIAR IMAGEN!!
            ArrayList<Ship> trajectory = s.getTrajectory();
            if(!trajectory.isEmpty()){
                Ship previousShip = trajectory.get(trajectory.size()-1);
                gcTrajectories.strokeLine(previousShip.getLon(), previousShip.getLat(), s.getLon(), s.getLat());
            }
//            Image image1 = new Image("/files/boat-red.png", true);
//            gcShips.drawImage(image1, 300, 300);
        }
    }
    
    /** Paint point (x, y) on the screen
     *  @param id Track id
     *  @param lat  latitude
     *  @param lng  longitude
     *  @param time time
     *  @param speed has speed?
     *  @param lat2 end latitude
     *  @param lng2 end longitude
     *  @param length length
     */
    public void paintPoint(String id, double time, double lat, double lng, boolean speed, double speedMod, double lat2, double lng2, double length) {
        if(!speed) {
            lat2 = lat;
            lng2 = lng;
        }
        //gc.fillOval(500,50,5,5);
        //System.out.println("LAT: " + lat + "LON: " + lng);
        gcShips.fillRect(lng, lat, length/5, length/2);
        //Image image1 = new Image("files/boat-green.png", true);
        //gcShips.drawImage(image1, 300, 300);                       
    }
    
    
    public void paintMarker(Ship s, int type) {
        String scriptCall =
                "document.paintMarker("
                + "'" + s.getId() + "', "
                + "'" + shipIconsURLs.get(type) + "'"
                + ")";
        //mapWebView.getEngine().executeScript(scriptCall);
    }
    
    public AreaChart getChart(){
        return lc;
    }
    
    
    public Axis getXAxis(){
        return xAxis;
    }
}
