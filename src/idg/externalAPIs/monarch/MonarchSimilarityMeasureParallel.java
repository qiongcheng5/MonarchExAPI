package idg.externalAPIs.monarch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.SecureRandom;
import java.util.LinkedList;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.json.simple.parser.JSONParser;

import idg.AssociationItemInMonarch;
import idg.QualityBasedAssociationInMonarch;
import idg.db.DBHandler4Monarch_DTO;
import idg.db.dao.PairwiseObjects;
import idg.externalAPIs.json.MonarchPairwiseSimJsonParser;
import idg.util.Common;

/**
 * @author Qiong Cheng
 * 
 */
public class MonarchSimilarityMeasureParallel {
	 /** Creates a new instance of DownloadFasta */
    public MonarchSimilarityMeasureParallel() {
    	
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
	
	public void retrieve(MonarchPairwiseSimJsonParser mParser, int relType, long geneID, 
			LinkedList<AssociationItemInMonarch> list, LinkedList<String> missingGenes, 
			LinkedList<String> missingAssociations) throws Exception{
		if (list == null)
			list = new LinkedList<AssociationItemInMonarch>();
        
		String URL = "";
        MonarchScore score4Sub2Obj, score4Obj2Sub;
	    	switch (relType){
	        case 1: //Gene_Phenotype_Relationship_Quality
	        		URL="https://solr.monarchinitiative.org/solr/golr/select?defType=edismax&qt=standard&indent=on&wt=csv&rows=100000&start=0&fl=subject,subject_label,subject_taxon,subject_taxon_label,relation,relation_label,object,object_label,evidence,evidence_label,source,is_defined_by,qualifier&facet=true&facet.mincount=1&facet.sort=count&json.nl=arrarr&facet.limit=25&facet.method=enum&csv.encapsulator=&csv.separator=%09&csv.header=true&csv.mv.separator=%7C&fq=subject_closure:%22NCBIGene:" + geneID + "%22&fq=object_category:%22phenotype%22&facet.field=subject_taxon_label&q=*:*";
	            break;
	        case 2: //Gene_Disease_Relationship_Quality
		        	URL="https://solr.monarchinitiative.org/solr/golr/select?defType=edismax&qt=standard&indent=on&wt=csv&rows=100000&start=0&fl=subject,subject_label,subject_taxon,subject_taxon_label,relation,relation_label,object,object_label,evidence,evidence_label,source,is_defined_by,qualifier&facet=true&facet.mincount=1&facet.sort=count&json.nl=arrarr&facet.limit=25&facet.method=enum&csv.encapsulator=&csv.separator=%09&csv.header=true&csv.mv.separator=%7C&fq=subject_closure:%22NCBIGene:" + geneID + "%22&fq=object_category:%22disease%22&fq=subject_category:%22gene%22&facet.field=subject_taxon_label&q=*:*";
		        default:
	    }
        
        	if (Common.isDebug)
        		System.out.println(URL);
	    
        	if (geneID == 55844)
        		System.out.println(URL);
        	
        URL gotoUrl = new URL(URL);
        InputStreamReader isr = new InputStreamReader(gotoUrl.openStream());
        BufferedReader in = new BufferedReader(isr);

        StringBuffer sb = new StringBuffer();
        String inputLine;
        boolean isFirst = true;
           
        //grab the contents at the URL
        int count = 0;
        while ((inputLine = in.readLine()) != null){
            if (isFirst){
                isFirst = false;
                continue;
            }
            
            count++;
            String items[] = inputLine.split("\t");
            QualityBasedAssociationInMonarch relobj = null;
            switch (relType){
                case 1: //Gene_Phenotype_Relationship_Quality
                    relobj = new QualityBasedAssociationInMonarch(
                            items[0], items[1], items[2], items[3],
                            "G2P", "Gene2PhenotypeRel", items[6],
                            items[7].length() <= 256 ? items[7]:items[7].substring(0, 255),
                            items[8], 
                            items[9].length() <= 1124 ? items[9]:items[9].substring(0, 1123), 
                            	items[10], items[11],
                            items[12]);
                    
                    //dbhandler.insertMonarchQualityBasedAssociation(relobj);
                    break;
                case 2: //Gene_Disease_Relationship_Quality
                		relobj = new QualityBasedAssociationInMonarch(
                            items[0], items[1], items[2], items[3],
                            "G2D", "Gene2DiseaseRel", items[6], 
                            items[7].length() <= 256 ? items[7]:items[7].substring(0, 255),
                            items[8],
                            items[9].length() <= 1124 ? items[9]:items[9].substring(0, 1123), 
                            items[10], items[11],
                            items[12]);

                		//dbhandler.insertMonarchQualityBasedAssociation(diseaseRel);
                		break;
                default:
            }

            PairwiseObjects pair = new PairwiseObjects(relobj.subject, relobj.object);
            
            score4Sub2Obj = retrievePhenotypeBasedSim(mParser, pair, true);
            score4Obj2Sub = retrievePhenotypeBasedSim(mParser, pair, false);
            
            if (score4Sub2Obj != null && score4Obj2Sub!= null)
            		list.add(new AssociationItemInMonarch(relobj, score4Sub2Obj, score4Sub2Obj));
            else{
            		list.add(new AssociationItemInMonarch(relobj));
            		missingAssociations.add(relobj.subject+ "\t" + relobj.object + "\t" + "https://monarchinitiative.org/compare/" + 
            				relobj.subject + "/" + relobj.object);
            }
            
        }
        
        if (count == 0) missingGenes.add("NCBIGene:" + geneID);
        
        isr.close();
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
        if (Common.isDebug)
            		System.out.println(theUrl);
        
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
            mue.printStackTrace();
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
	    return null;
    }
	
	public void retrievePairwiseSimMeasures(int relType, String geneListFile, 
			String outRelQualityFile, String missingGenesFile, String missingAssociationsFile) throws IOException{
		LinkedList<AssociationItemInMonarch> list = new LinkedList<AssociationItemInMonarch>();
		LinkedList<String> missingGenes = new LinkedList<String>();
		LinkedList<String> missingAssociations = new LinkedList<String>();

        MonarchPairwiseSimJsonParser mParser = new MonarchPairwiseSimJsonParser();
        
        try{
	        	FileReader fr = new FileReader(geneListFile);
	        	BufferedReader br = new BufferedReader(fr);
	        	br.readLine();
	        	String s;
	        int geneID = 0;
	        	while((s = br.readLine()) != null) {
	        		String[] items = s.split("\t");
	        		geneID = Integer.parseInt(items[7]);
	        		try{
	        			retrieve(mParser, relType, geneID, list, missingGenes, missingAssociations);
	        		} catch(Exception ex){
	        			missingGenes.add("NCBIGene:" + geneID);
	        			ex.printStackTrace();
	        		}
	        	}
	        	fr.close();	
            
	        	if (list.size() > 0){
	            	FileWriter writer = new FileWriter(outRelQualityFile);
		        	for (AssociationItemInMonarch item: list){
		        		writer.write(item.association.subject + '\t'
		        				+ item.association.subject_label + '\t'
		        				+ item.association.subject_taxon + '\t'
		        				+ item.association.subject_taxon_label + '\t'
		        				+ item.association.relation + '\t'
		        				+ item.association.relation_label + '\t'
		        				+ item.association.object + '\t'
		        				+ item.association.object_label + '\t'
		        				+ item.association.evidence + '\t'
		        				+ item.association.evidence_label + '\t'
		        				+ item.association.source + '\t'
		        				+ item.association.is_defined_by + '\t'
		        				+ item.association.qualifier + '\t'
		        				+ (item.score4Sub2Obj==null? "" : item.score4Sub2Obj.getMaxScore()) + '\t'
		        				+ (item.score4Sub2Obj==null? "" : item.score4Sub2Obj.getAvgScore()) + '\t'
		        				+ (item.score4Sub2Obj==null? "" : item.score4Sub2Obj.getMaxPercentageScore()) + '\t'
		        				+ (item.score4Sub2Obj==null? "" : item.score4Obj2Sub.getAvgPercentageScore()) + '\t'
		        				+ (item.score4Sub2Obj==null? "" : item.score4Sub2Obj.getCombinedPercentageScore()) + '\t'
		        				+ (item.score4Sub2Obj==null? "" : item.score4Sub2Obj.getCombinedScoreInMonarch()) + '\t'
		        				+ (item.score4Obj2Sub==null? "" : item.score4Obj2Sub.getMaxScore()) + '\t'
		        				+ (item.score4Obj2Sub==null? "" : item.score4Obj2Sub.getAvgScore()) + '\t'
		        				+ (item.score4Obj2Sub==null? "" : item.score4Obj2Sub.getMaxPercentageScore()) + '\t'
		        				+ (item.score4Obj2Sub==null? "" : item.score4Obj2Sub.getAvgPercentageScore()) + '\t'
		        				+ (item.score4Obj2Sub==null? "" : item.score4Obj2Sub.getCombinedPercentageScore()) + '\t'
		        				+ (item.score4Obj2Sub==null? "" : item.score4Obj2Sub.getCombinedScoreInMonarch()) + "\r\n");
		        	}
		        	writer.flush();
		        	writer.close();
	        	}
	        	
	        	if (missingGenes.size() > 0){
		        	FileWriter writerMissingGenes = new FileWriter(missingGenesFile);
		        	for (String item: missingGenes){
		        		writerMissingGenes.write(item  + "\r\n");
		        	}
		        	writerMissingGenes.flush();
		        	writerMissingGenes.close();
	        	}

	        	if (missingAssociations.size() > 0){
		        	FileWriter writerMissingAssociations = new FileWriter(missingAssociationsFile);
		        	for (String item: missingAssociations){
		        		writerMissingAssociations.write(item  + "\r\n");
		        	}
		        	writerMissingAssociations.flush();
		        	writerMissingAssociations.close();
	        	}
        } catch (Exception ex){
            ex.printStackTrace();
        }
        
    }
	
	public static void main(String[] args) {
		/*if (args.length < 5){
			System.out.println("Usage:");
			System.out.println("    java MonarchSimilarityMeasureParallel tcrd_proteome_part_infile association_outfile missing_gene_file missing_associations_file");
		}*/
		
		String geneListFile = args[0]; //"C:\\Users\\Qiong\\chunting\\projects\\MonarchApp\\MonarchApp\\sample\\sample.txt"; //args[1]
		String outRelQualityFile = args[1]; //"C:\\Users\\Qiong\\chunting\\projects\\MonarchApp\\MonarchApp\\sample\\sampleout.txt"; //args[2];
		String missingGenesFile = args[2]; //"C:\\Users\\Qiong\\chunting\\projects\\MonarchApp\\MonarchApp\\sample\\sampleout_missing_genes.txt";//args[3];
		String missingAssociationsFile = args[3]; //"C:\\Users\\Qiong\\chunting\\projects\\MonarchApp\\MonarchApp\\sample\\sampleout_missing_associations.txt";//args[4];
		int relationshipID = Integer.parseInt(args[4]); // 1: gene-disease; 2: gene-phenotype; 3: both
		
		try {
			MonarchSimilarityMeasureParallel paral = new MonarchSimilarityMeasureParallel();
			if (relationshipID % 2 == 1)
				paral.retrievePairwiseSimMeasures(Common.Gene_Disease_Relationship_Quality,
					geneListFile, outRelQualityFile, missingGenesFile, missingAssociationsFile);

			if (relationshipID / 2 == 1)
				paral.retrievePairwiseSimMeasures(Common.Gene_Phenotype_Relationship_Quality,
					geneListFile, outRelQualityFile, missingGenesFile, missingAssociationsFile);
		

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
