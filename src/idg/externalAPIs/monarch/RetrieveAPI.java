/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package idg.externalAPIs.monarch;

import static idg.wwwapi.GetURL.createAFile;

import java.io.*;
import java.net.*;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Vector;
import java.util.LinkedList;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import idg.QualityBasedAssociationInMonarch;
import idg.db.DBHandler4Monarch_DTO;
import idg.db.dao.PairwiseObjects;
import idg.externalAPIs.json.MonarchPairwiseSimJsonParser;
import idg.util.Common;
import idg.wwwapi.GetURL;

/**
 * The class provides a general operation of retrieving data from API access
 * 
 * @author Qiong Cheng
 */
public class RetrieveAPI {
    
    /*
    API example: https://monarchinitiative.org/gene/NCBIGene:2629
    
    https://solr.monarchinitiative.org/solr/golr/select?defType=edismax&qt=standard&indent=on&wt=csv&rows=100000&start=0&fl=subject,subject_label,subject_taxon,subject_taxon_label,relation,relation_label,object,object_label,evidence,evidence_label,source,is_defined_by,qualifier&facet=true&facet.mincount=1&facet.sort=count&json.nl=arrarr&facet.limit=25&facet.method=enum&csv.encapsulator=&csv.separator=%09&csv.header=true&csv.mv.separator=%7C&fq=subject_closure:%22NCBIGene:2629%22&fq=object_category:%22phenotype%22&facet.field=subject_taxon_label&q=*:*
    
    */
    
    /** Creates a new instance of DownloadFasta */
    public RetrieveAPI() {
    	
        // Create a new trust manager that trust all certificates
        TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                public void checkClientTrusted(
                    java.security.cert.X509Certificate[] certs, String authType) {
                }
                public void checkServerTrusted(
                    java.security.cert.X509Certificate[] certs, String authType) {
                }
            }
        };
        
        // Activate the new trust manager
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            SecureRandom x = new java.security.SecureRandom();
            sc.init(null, trustAllCerts, x);
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
        }
        
    }
    
    public void getAllGenePhenotypeAssocitions(){
        // database access
        
        
        // Iteratively retrieving gene-phenotype associations
        
    }
    
    public void getAllGenePhenotypeAssocitionsOfDTOMonarchOverlap(DBHandler4Monarch_DTO dbhandler) throws IOException{
        try{
            // database access
            LinkedList<Long> geneIDs = dbhandler.getCommonGeneIDsOfDTOMonarch();

            // Iteratively retrieving gene-phenotype associations
            int count = 0;
            for (Long geneID : geneIDs){
                System.out.println("[RetrieveAPI:getAllGenePhenotypeAssocitionsOfDTOMonarchOverlap] " + count + " : " + geneID.longValue());
                retrieve(Common.Gene_Phenotype_Relationship_Quality, dbhandler, geneID.longValue());
                count++;
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
        
    }
    
    public void getAllGeneDiseaseAssocitionsOfDTOMonarchOverlap(DBHandler4Monarch_DTO dbhandler) throws IOException{
        try{
            // database access
            LinkedList<Long> geneIDs = dbhandler.getCommonGeneIDsOfDTOMonarch();

            // Iteratively retrieving gene-phenotype associations
            int count = 0;
            for (Long geneID : geneIDs){
                System.out.println("[RetrieveAPI:getAllGeneDiseaseAssocitionsOfDTOMonarchOverlap] " + count + " : " + geneID.longValue());
                retrieve(Common.Gene_Disease_Relationship_Quality, dbhandler, geneID.longValue());
                count++;
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
        
    }
    
    /** This method does the actual GET   */
    public void retrieveRelationship(String theUrl, DBHandler4Monarch_DTO dbhandler, int relType) throws IOException
    {
        try {
            URL gotoUrl = new URL(theUrl);
            System.out.println(theUrl);
            InputStreamReader isr = new InputStreamReader(gotoUrl.openStream());
            BufferedReader in = new BufferedReader(isr);

            StringBuffer sb = new StringBuffer();
            String inputLine;
            boolean isFirst = true;
            
            //grab the contents at the URL
            while ((inputLine = in.readLine()) != null){
                if (isFirst){
                    isFirst = false;
                    continue;
                }
                String items[] = inputLine.split("\t");
                switch (relType){
                    case 1: //Gene_Phenotype_Relationship_Quality
                        QualityBasedAssociationInMonarch relobj = new QualityBasedAssociationInMonarch(
                                items[0], items[1], items[2], items[3],
                                "G2P", "Gene2PhenotypeRel", items[4],
                                items[5].length() <= 256 ? items[5]:items[5].substring(0, 255),
                                items[8], 
                                items[9].length() <= 1124 ? items[9]:items[9].substring(0, 1123), 
                                	items[10], items[11],
                                items[12]);
                        dbhandler.insertMonarchQualityBasedAssociation(relobj);
                        break;
                    case 2: //Gene_Disease_Relationship_Quality
                    	QualityBasedAssociationInMonarch diseaseRel = new QualityBasedAssociationInMonarch(
                                items[0], items[1], items[2], items[3],
                                "G2D", "Gene2DiseaseRel", items[4], 
                                items[5].length() <= 256 ? items[5]:items[5].substring(0, 255),
                                items[8],
                                items[9].length() <= 1124 ? items[9]:items[9].substring(0, 1123), 
                                items[10], items[11],
                                items[12]);
                        dbhandler.insertMonarchQualityBasedAssociation(diseaseRel);
                    		break;
                    default:
                }
            }
            isr.close();
        }
        catch (MalformedURLException mue) {
            mue.printStackTrace();
        }
        catch (IOException ioe) {
            throw ioe;
        }
    }    

    public void retrieve(int relType, DBHandler4Monarch_DTO dbhandler, long geneID) throws Exception{
        String URL = "";
	    	switch (relType){
	        case 1: //Gene_Phenotype_Relationship_Quality
	        		URL="https://solr.monarchinitiative.org/solr/golr/select?defType=edismax&qt=standard&indent=on&wt=csv&rows=100000&start=0&fl=subject,subject_label,subject_taxon,subject_taxon_label,object,object_label,relation,relation_label,evidence,evidence_label,source,is_defined_by,qualifier&facet=true&facet.mincount=1&facet.sort=count&json.nl=arrarr&facet.limit=25&facet.method=enum&csv.encapsulator=%22&csv.separator=%09&csv.header=true&csv.mv.separator=%7C&fq=object_category:%22phenotype%22&fq=subject_closure:%22NCBIGene:" + geneID + "%22&facet.field=subject_taxon_label&q=*:*";
	            break;
	        case 2: //Gene_Disease_Relationship_Quality
		        	URL="https://solr.monarchinitiative.org/solr/golr/select?defType=edismax&qt=standard&indent=on&wt=csv&rows=100000&start=0&fl=subject,subject_label,subject_taxon,subject_taxon_label,object,object_label,relation,relation_label,evidence,evidence_label,source,is_defined_by,qualifier&facet=true&facet.mincount=1&facet.sort=count&json.nl=arrarr&facet.limit=25&facet.method=enum&csv.encapsulator=%22&csv.separator=%09&csv.header=true&csv.mv.separator=%7C&fq=object_category:%22disease%22&fq=subject_category:%22gene%22&fq=subject_closure:%22NCBIGene:" + geneID + "%22&facet.field=subject_taxon_label&q=*:*";
		        default:
	    }
        
        retrieveRelationship(URL, dbhandler , relType);

    }
    
    public MonarchScore retrievePhenotypeBasedSim(MonarchPairwiseSimJsonParser mParser, PairwiseObjects pair, boolean isSubject2Object) throws Exception{
        String theUrl = "";
        MonarchScore mscoreObj = null;
        
        if (isSubject2Object)
        	    theUrl = "https://monarchinitiative.org/compare/" + 
        				pair.getSubject() + "/" + pair.getObject();
        else
        		theUrl = "https://monarchinitiative.org/compare/" + 
    				pair.getObject() + "/" + pair.getSubject();
        
        //https://monarchinitiative.org/compare/NCBIGene:6622/OMIM:270400
        try {
            URL gotoUrl = new URL(theUrl);
            
            InputStreamReader isr = new InputStreamReader(gotoUrl.openStream());
            BufferedReader in = new BufferedReader(isr);

            StringBuffer sb = new StringBuffer();
            String inputLine;
            boolean isFirst = true;
            
            //grab the contents at the URL
            int lineNum = 1;
            JSONParser parser =  new JSONParser();
            while ((inputLine = in.readLine()) != null){
            		sb.append(inputLine);
            }
            
            double score = mParser.getCombinedScore(sb);
			mscoreObj = mParser.getPhenotypeSimilarityScore(sb, score);
			if (Common.isDebug)
				if (mscoreObj != null) System.out.println(mscoreObj.toShortString());
			
            isr.close();
			return mscoreObj;
        }
        catch (MalformedURLException mue) {
            //mue.printStackTrace();
            if (Common.isDebug) System.out.println(mue.getMessage());
        }
        catch (IOException ioe) {
            //ioe.printStackTrace();
        		if (Common.isDebug) System.out.println(ioe.getMessage());
        }
	    return null;
    }
}

