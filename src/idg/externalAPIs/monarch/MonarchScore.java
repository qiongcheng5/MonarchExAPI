package idg.externalAPIs.monarch;

/**
 * @author Qiong Cheng
 * 
 */
public class MonarchScore {
	double maxScore;
	double avgScore;
	double maxPercentageScore;
	double avgPercentageScore;
	double combinedPercentageScore;
	double combinedScoreInMonarch;
	
	public MonarchScore(double maxScore, 
			double avgScore,
			double maxPercentageScore,
			double avgPercentageScore,
			double combinedPercentageScore,
			double combinedScoreInMonarch){
		this.maxScore = maxScore;
		this.avgScore = avgScore;
		this.maxPercentageScore = maxPercentageScore;
		this.avgPercentageScore = avgPercentageScore;
		this.combinedPercentageScore = combinedPercentageScore;
		this.combinedScoreInMonarch = combinedScoreInMonarch;
	}
	
	public MonarchScore(MonarchScore that){
		this.maxScore = that.getMaxScore();
		this.avgScore = that.getAvgScore();
		this.maxPercentageScore = that.getMaxPercentageScore();
		this.avgPercentageScore = that.getAvgPercentageScore();
		this.combinedPercentageScore = that.getCombinedPercentageScore();
		this.combinedScoreInMonarch = that.getCombinedScoreInMonarch();
	}
	
	public MonarchScore(){
	}
	
	public double getMaxScore(){
		return this.maxScore;
	}
	
	public double getAvgScore(){
		return this.avgScore;
	}
	
	public double getMaxPercentageScore(){
		return this.maxPercentageScore;
	}
	
	public double getCombinedPercentageScore(){
		return this.combinedPercentageScore;
	}
	
	public double getCombinedScoreInMonarch(){
		return this.combinedScoreInMonarch;
	}
	
	public double getAvgPercentageScore(){
		return this.avgPercentageScore;
	}
	
	public void setMaxScore(double maxScore){
		this.maxScore = maxScore;
	}
	
	public void setAvgScore(double avgScore){
		this.avgScore = avgScore;
	}
	
	public void setMaxPercentageScore(double maxPercentageScore){
		this.maxPercentageScore = maxPercentageScore;
	}
	
	public void setCombinedPercentageScore(double combinedPercentageScore){
		this.combinedPercentageScore = combinedPercentageScore;
	}
	
	public void setCombinedScoreInMonarch(double combinedScoreInMonarch){
		this.combinedScoreInMonarch = combinedScoreInMonarch;
	}
	
	public void setAvgPercentageScore(double avgPercentageScore){
		this.avgPercentageScore = avgPercentageScore;
	}
	
	public String toString(){
		return "maxscore = " + this.getMaxScore() + "\n"
					+ "avgscore = " + this.getAvgScore() + "\n"
					+ "maxPercentageScore = " + this.getMaxPercentageScore() + "\n"
					+ "avgPercentageScore = " + this.getAvgPercentageScore() + "\n"
					+ "combinedPercentageScore = " + this.getCombinedPercentageScore() + "\n"
					+ "combinedScoreInMonarch = " + this.getCombinedScoreInMonarch();
	}
	
	public String toShortString(){
		return "combinedPercentageScore = " + this.getCombinedPercentageScore() + " : "
					+ "combinedScoreInMonarch = " + this.getCombinedScoreInMonarch();
	}
}
