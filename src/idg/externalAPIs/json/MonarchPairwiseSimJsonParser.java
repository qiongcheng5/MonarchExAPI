package idg.externalAPIs.json;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.SecureRandom;
import java.util.LinkedList;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import idg.externalAPIs.json.obj.PhenotypeSimRankObj;
import idg.externalAPIs.monarch.MonarchScore;
import idg.util.Common;

public class MonarchPairwiseSimJsonParser {
	static JSONParser parser =  new JSONParser();
	
	public  MonarchPairwiseSimJsonParser(){
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
	

	public double getCombinedScore(StringBuffer phenoSimStr) throws ParseException, Exception{
		JSONObject objJSON = (JSONObject)parser.parse(phenoSimStr.substring(0));
		
		if (objJSON == null)
				throw new Exception("No JSON node object!");
			
		if (objJSON instanceof java.util.HashMap){
			
			JSONArray arrNodes = (JSONArray) objJSON.get("b");
			for (int count = 0; arrNodes!=null && count< arrNodes.size(); count++){
				if (Common.isDebug) 
					System.out.println(count + " - " + ((JSONObject) (arrNodes.get(count))).toString());
				JSONObject subArrNode = (JSONObject) (arrNodes.get(count));
				if (subArrNode == null)
					throw new Exception("No JSON node object!");
				
				if (subArrNode instanceof java.util.HashMap){
					JSONObject subsubArrNodes = (JSONObject) subArrNode.get("score");
					return Double.parseDouble(subsubArrNodes.get("score").toString());
				}
			}
		}

		return -1;
	}
	
	public MonarchScore getPhenotypeSimilarityScore(StringBuffer phenoSimStr, double scoreInMonarch) throws ParseException, Exception{
		LinkedList<PhenotypeSimRankObj> ranklist4Subject = new LinkedList<PhenotypeSimRankObj>();
		LinkedList<PhenotypeSimRankObj> ranklist4Object = new LinkedList<PhenotypeSimRankObj>();
		LinkedList<PhenotypeSimRankObj> ranklist4Model = new LinkedList<PhenotypeSimRankObj>();
		double icscore = 0;
		String phenotypeID = "", phenotypeLabel = "";
		
		JSONObject objJSON = (JSONObject)parser.parse(phenoSimStr.substring(0));
		
		if (objJSON == null)
				throw new Exception("No JSON node object!");
			
		if (objJSON instanceof java.util.HashMap){
			
			JSONArray arrNodes = (JSONArray) objJSON.get("b");
			for (int count = 0; arrNodes!=null && count< arrNodes.size(); count++){
				if (Common.isDebug) 
					System.out.println(count + " - " + ((JSONObject) (arrNodes.get(count))).toString());
				
				JSONObject subArrNode = (JSONObject) (arrNodes.get(count));
				if (subArrNode == null)
					throw new Exception("No JSON node object!");
				
				if (subArrNode instanceof java.util.HashMap){
					JSONArray subsubArrNodes = (JSONArray) subArrNode.get("matches");
					for (int i = 0; i < subsubArrNodes.size(); i++){
						if (Common.isDebug) 
							System.out.println(i + " - " + ((JSONObject) (subsubArrNodes.get(i))).toString());
					
						JSONObject rankObj = (JSONObject) (subsubArrNodes.get(i));
						JSONObject rank4Subject = (JSONObject) (rankObj.get("a"));
						ranklist4Subject.add(new PhenotypeSimRankObj(Double.parseDouble(rank4Subject.get("IC").toString()),
								rank4Subject.get("id").toString(), rank4Subject.get("label").toString()));
						
						JSONObject rank4Object = (JSONObject) (rankObj.get("b"));
						ranklist4Object.add(new PhenotypeSimRankObj(Double.parseDouble(rank4Object.get("IC").toString()),
								rank4Object.get("id").toString(), rank4Object.get("label").toString()));
						
						JSONObject rank4Model = (JSONObject) (rankObj.get("lcs"));
						ranklist4Model.add(new PhenotypeSimRankObj(Double.parseDouble(rank4Model.get("IC").toString()),
								rank4Model.get("id").toString(), rank4Model.get("label").toString()));
					}
				}
			}
		}

		return calPhenoDigmScore(ranklist4Subject, ranklist4Object, 
				ranklist4Model, scoreInMonarch);
		
	}
	
	private MonarchScore calPhenoDigmScore(LinkedList<PhenotypeSimRankObj> ranklist4Subject,
			LinkedList<PhenotypeSimRankObj> ranklist4Object,
			LinkedList<PhenotypeSimRankObj> ranklist4Model, 
			double scoreInMonarch){
		
		if ((ranklist4Subject == null || (ranklist4Subject != null && ranklist4Subject.size() <= 0)) ||
			(ranklist4Object == null || (ranklist4Object != null && ranklist4Object.size() <= 0))	||
			(ranklist4Model == null || (ranklist4Model != null && ranklist4Model.size() <= 0))){
			return null;
		}
		
		double maxic4Subject = 0, maxic4Object = 0, maxic4Model = 0, 
				maxscore = 0, avgscore = 0, icsum4Subject = 0, icsum4Object = 0, ic = 0,
				icsum4Model = 0, avgic4Model = 0, maxPercentageScore = 0, 
				avgPercentageScore = 0, combinedPercentageScore = 0 ;
		for (PhenotypeSimRankObj subject : ranklist4Subject){
			maxic4Subject = maxic4Subject < subject.getICScore() ? subject.getICScore() : maxic4Subject;
			icsum4Subject += subject.getICScore(); 
		}
		
		for (PhenotypeSimRankObj object : ranklist4Object){
			maxic4Object = maxic4Object < object.getICScore() ? object.getICScore() : maxic4Object;
			icsum4Object += object.getICScore();
		}
		
		for (PhenotypeSimRankObj model : ranklist4Model){
			maxic4Model = maxic4Model < model.getICScore() ? model.getICScore() : maxic4Model;
			icsum4Model += model.getICScore();
		}
		
		// maxscore
		maxscore = maxic4Subject >= maxic4Object? maxic4Subject : maxic4Object ;
		//maxscore = maxic4Model >= maxic4Object? maxic4Model : maxic4Object ;
		
		// avgscore
		avgscore = (icsum4Subject + icsum4Object) / (ranklist4Subject.size() + ranklist4Object.size());
		//avgscore = (icsum4Object + icsum4Model) / (ranklist4Object.size()+ ranklist4Model.size());
		avgic4Model = icsum4Model / ranklist4Model.size();
		//avgic4Model = icsum4Subject / ranklist4Subject.size();
		
		//maxPercentageScore 
		maxPercentageScore = maxic4Model / maxscore * 100; //maxscore / maxic4Model * 100;
		avgPercentageScore = avgic4Model / avgscore * 100; //avgscore / avgic4Model * 100;
		
		combinedPercentageScore = (maxPercentageScore + avgPercentageScore) / 2;
			
		return new MonarchScore(maxscore, avgscore, maxPercentageScore,
				avgPercentageScore, combinedPercentageScore, scoreInMonarch);
		
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String theUrl = "https://monarchinitiative.org/compare/NCBIGene:6622/OMIM:270400";
		MonarchPairwiseSimJsonParser mParser = new MonarchPairwiseSimJsonParser();

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
		    MonarchScore scoreObj = new MonarchScore();
		    while ((inputLine = in.readLine()) != null){
		        //System.out.println("theUrl = " + theUrl + "\nLineNum: " + lineNum + "\nInputLine:\n" + inputLine);
		    		sb.append(inputLine);
		    }
	    
			double score = mParser.getCombinedScore(sb);
			MonarchScore mscoreObj = mParser.getPhenotypeSimilarityScore(sb, score);
			System.out.println(mscoreObj.toString());
		} catch (Exception e){
			e.printStackTrace();
		}
	}
}
