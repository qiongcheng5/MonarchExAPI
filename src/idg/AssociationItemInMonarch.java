package idg;

import idg.externalAPIs.monarch.MonarchScore;

/**
 * @author Qiong Cheng
 * 
 */
public class AssociationItemInMonarch {
	public QualityBasedAssociationInMonarch association;
	public MonarchScore score4Sub2Obj;
	public MonarchScore score4Obj2Sub;
	
	public AssociationItemInMonarch(QualityBasedAssociationInMonarch association){
		this.association = new QualityBasedAssociationInMonarch(association);
	}
	
	public AssociationItemInMonarch(MonarchScore score4Sub2Obj, MonarchScore score4Obj2Sub){
		this.score4Sub2Obj = new MonarchScore(score4Sub2Obj);
		this.score4Obj2Sub = new MonarchScore(score4Obj2Sub);
	}
	
	public AssociationItemInMonarch(QualityBasedAssociationInMonarch association,
			MonarchScore score4Sub2Obj, MonarchScore score4Obj2Sub){
		this.association = new QualityBasedAssociationInMonarch(association);
		this.score4Sub2Obj = new MonarchScore(score4Sub2Obj);
		this.score4Obj2Sub = new MonarchScore(score4Obj2Sub);
	}
}
