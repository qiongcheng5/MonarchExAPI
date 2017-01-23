package idg.externalAPIs.json.obj;

/**
 * @author Qiong Cheng
 * 
 */
public class PhenotypeSimRankObj {
	double ICScore;
	String phenotypeID;
	String phenotypeLabel;
	
	public PhenotypeSimRankObj(double ic, String phenotypeID, String phenotypeLabel){
		this.ICScore = ic;
		this.phenotypeID = phenotypeID;
		this.phenotypeLabel = phenotypeLabel;
	}
	public double getICScore(){
		return this.ICScore;
	}
	
	// TODO to finish
}
