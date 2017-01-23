package idg.db.dao;

/**
 * @author Qiong Cheng
 * 
 */
public class PairwiseObjects {
	String subject, object;
	public PairwiseObjects(String subject, String object){
		this.subject = subject;
		this.object = object;
	}
	
	public String getSubject(){
		return this.subject;
	}
	
	public String getObject(){
		return this.object;
	}
	
	public void setObject(String object){
		this.object = object;
	}
	
	public void setSubject(String subject){
		this.subject = subject;
	}
	
	public String toString(){
		return "Pair: {" + this.subject + ", " + this.object + "}";
	}
}
