package idg.externalAPIs.monarch;

import java.io.IOException;
import java.util.LinkedList;

import idg.QualityBasedAssociationInMonarch;
import idg.db.DBHandler4Monarch_DTO;
import idg.db.dao.PairwiseObjects;
import idg.externalAPIs.json.MonarchPairwiseSimJsonParser;
import idg.util.Common;

/**
 * @author Qiong Cheng
 * 
 */
/*
 * Given the json response of monarch comparasion from the link : 
 * //https://monarchinitiative.org/compare/NCBIGene:6622/OMIM:270400
 * 
 * we will calculate the following variables and save back to the DB
  `Max_Score` FLOAT,
  `Avg_Score` FLOAT,
  `Max_Percentage_Score` FLOAT,
  `Avg_Percentage_Score` FLOAT,
  `Combined_Percentage_Score` FLOAT,
  `Combined_Score_In_Monarch` FLOAT
  
 */
public class MonarchSimilarityMeasures {
	
	public static void retrievePairwiseSimMeasures(DBHandler4Monarch_DTO dbhandler, int relType, RetrieveAPI retrieveapi) throws IOException{
        try{
            // database access
            LinkedList<PairwiseObjects> pairs = dbhandler.getMonarchAssociationPairs(relType);
            MonarchPairwiseSimJsonParser mParser = new MonarchPairwiseSimJsonParser();

            // Iteratively retrieving gene-phenotype associations
            int count = 0;
            MonarchScore score4Sub2Obj, score4Obj2Sub;
            for (PairwiseObjects pair : pairs){
                //if (Common.isDebug)
                		System.out.println("[RetrieveAPI:retrieveGeneDiseaseSimMeasures] " + count + " : " + pair.toString());
                
                score4Sub2Obj = retrieveapi.retrievePhenotypeBasedSim(mParser, pair, true);
                
                score4Obj2Sub = retrieveapi.retrievePhenotypeBasedSim(mParser, pair, false);
                
                dbhandler.updateMonarchAssociationScore(pair, score4Sub2Obj, score4Obj2Sub);
                count++;
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
        
    }
}
