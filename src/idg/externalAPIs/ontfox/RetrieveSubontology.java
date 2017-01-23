package idg.externalAPIs.ontfox;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import idg.db.DBHandler4Monarch_DTO;
import idg.util.Common;

/**
 * @author Qiong Cheng
 * 
 */
public class RetrieveSubontology {
	
	
	public static void retrieve(DBHandler4Monarch_DTO dbhandler, String ontofoxInputFileName, String fileName2SaveSubontologies) throws IOException{
		try{
            // database access
            LinkedList<String> phenotypeIDs = dbhandler.getInvolvedPhenotypesOfDTOMonarch();
            
            // build the OntFox input file 
            OntoFoxInputTemplate inputTemplate = new OntoFoxInputTemplate(phenotypeIDs);
            inputTemplate.writeFile(ontofoxInputFileName);

            // save subontologies
            Process p = Runtime.getRuntime().exec("curl -s -F file=@" + ontofoxInputFileName + " -o " + fileName2SaveSubontologies + " http://ontofox.hegroup.org/service.php");
            File result = new File(fileName2SaveSubontologies);
            
            int count = 1;
            while (! result.exists()){
            		Thread.sleep(10000); // sleep for 10 seconds
            		if (count++ >= 12)
            			break;
            }
            
        } catch (Exception ex){
            ex.printStackTrace();
        }
	}
}
