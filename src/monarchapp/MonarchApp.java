/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monarchapp;

import java.sql.Connection;
import java.util.LinkedList;

import idg.db.DBHandler4Monarch_DTO;
import idg.externalAPIs.monarch.MonarchSimilarityMeasures;
import idg.externalAPIs.monarch.RetrieveAPI;
import idg.externalAPIs.ontfox.RetrieveSubontology;
import idg.util.Common;
import idg.util.ConfigFile;
import idg.util.DBSource;

/**
 *
 * @author Qiong Cheng
 */
public class MonarchApp {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try{
        	 
        		System.setProperty("user.dir", ConfigFile.getBaseDir()); 
        		
            Connection conn = DBSource.getInstance().getConnection(ConfigFile.getProperty(Common.Monarch_DTO_DataScheme_ItemName_in_Configuration));
            DBHandler4Monarch_DTO db= new DBHandler4Monarch_DTO(conn);
            
            if (Common.isDebugDB) 
            		db.dropTable4GenePhenotypeAssociations();
            if (Common.isDebugDB) 
            		db.creatTable4GenePhenotypeAssociations();
            
            RetrieveAPI retrieveapi = new RetrieveAPI();
            if (Common.isDebugDB) 
            		retrieveapi.getAllGenePhenotypeAssocitionsOfDTOMonarchOverlap(db);

            if (Common.isDebugDB) 
            		retrieveapi.getAllGeneDiseaseAssocitionsOfDTOMonarchOverlap(db);
            
            	if (Common.isDebugDB) 
            		MonarchSimilarityMeasures.retrievePairwiseSimMeasures(db, 
            				Common.Gene_Phenotype_Relationship_Quality, retrieveapi);
            
            if (Common.isDebugDB) 
            		MonarchSimilarityMeasures.retrievePairwiseSimMeasures(db, 
            				Common.Gene_Disease_Relationship_Quality, retrieveapi);
            
            if (Common.isDebugDB) 
            		RetrieveSubontology.retrieve(db, ConfigFile.getBaseDir()+"input.txt", ConfigFile.getBaseDir()+"ontfox_out.xml"); //"C:\\Users\\Qiong\\chunting\\projects\\MonarchApp\\prj\\data\\

            //LinkedList list1 = db.getDTOProteins();
            //System.out.println(list1.size());
            
            //LinkedList list2 = db.getTargetsFromDisGeNET();
            //System.out.println(list2.size());
            
            db.release();
            DBSource.getInstance().release();
    	}catch(Exception ex){
            ex.printStackTrace();
    	}
    }
    
}
