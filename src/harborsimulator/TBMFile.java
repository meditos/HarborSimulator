package harborsimulator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

/**
 * 
 */
public class TBMFile {
    
    private File fichero; 
    private FileWriter fstream;
    private BufferedWriter out;         
    
    public TBMFile(){
        try{
            // Create file 
            fichero= new File ("out.batch.txt");
            fstream = new FileWriter(fichero);
            out = new BufferedWriter(fstream);
        }catch (Exception e){
            System.err.println("Error: " + e.getMessage());
        }    
        writeInfrastructre();
    }
    
    private void writeInfrastructre(){
        /*try{
            out.write("TBMLAB BEGIN % instructions start here");
            writeFrame();
            writeStructure();
            writeAttribute();
            writeVariable();
        }catch (Exception e){
            System.err.println("Error: " + e.getMessage());
        }*/
    }
    
    public void writeFrame()throws Exception{
        out.write("\nFRAME        % creating a Logical frame for threat"
        + "\nFSHN = FT    % its short name is FT"
        + "\nFFLN = Logical % its full name is Logical"
        + "\nFTYP = Logical % type for frame is logical"
        + "\nFCRD = 2     % cardinality is 2 (default is F,T)"
        + "\nELEM = threat,nothreat %In this case it should be threat,not threat");
        
        out.write("\nFRAME        % creating a Logical frame for ships"//¿NECESARIO?
        + "\nFSHN = FSH    % its short name is FSH"
        + "\nFFLN = Logical % its full name is Logical"
        + "\nFTYP = Logical % type for frame is logical"
        + "\nFCRD = 4     % cardinality is 4"
        + "\nELEM = s1, s2, s3, s4");
        
        out.write("\nFRAME        % creating a Logical frame for speed"
        + "\nFSHN = FS    % its short name is FS"
        + "\nFFLN = Logical % its full name is Logical"
        + "\nFTYP = Logical % type for frame is logical"
        + "\nFCRD = 2     % cardinality is 2 (default is F,T)"
        + "\nELEM = violation, compliance %In this case it should be violation, compliance");
        
    }
    
    public void writeStructure()throws Exception{
        out.write("\nSTRUCTURE    % creating a structure for threats"
        + "\nSSHN = SFT"
        + "\nSFLN = Logical"
        + "\nSMRF = FT    % based on frame FT");
        
        out.write("\nSTRUCTURE    % creating a structure for ships"
        + "\nSSHN = SFSH"
        + "\nSFLN = Logical"
        + "\nSMRF = FSH    % based on frame FS");
        
        out.write("\nSTRUCTURE    % creating a structure for speeds"
        + "\nSSHN = SFS"
        + "\nSFLN = Logical"
        + "\nSMRF = FS    % based on frame FS");
    }
    
    public void writeAttribute()throws Exception{
        //Esto todavia habria que apañarlo
        out.write("\nATTRIBUTE    % creating an attribute"
        + "\nASHN = t"
        + "\nAFLN = threat"
        + "\nASTR = SFT   % based on SFS structure");
        //INDEX??
    }
    
    public void writeVariable()throws Exception{
        out.write("\nVARIABLE     % creating a variable"
        + "\nVSHN = SFT"
        + "\nVFLN = threat"
        + "\nVATT = SFT     % based on SFT attribute"
        + "\nVFRM = FT    % based on FT frame");
    }
    
    public void writeLine(String line){
        try{
            out.write(line);
        }catch (Exception e){
            System.err.println("Error: " + e.getMessage());
        }    
    }
       
    public void saveTBMFile(File f){
        try{
            //Al salvar debe de escribirse una linea al final del fichero.
            out.write("\nTBMLAB END");
            //Close the output stream
            out.close();
        }catch (Exception e){
            System.err.println("Error: " + e.getMessage());
        }    
        fichero.renameTo(f);
    }
    
}
