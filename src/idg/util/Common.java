package idg.util;

/**
 * @author Qiong Cheng
 * 
 * Constants or common methods are to define here.
 */

public final class Common {
	public static boolean isDebug=false;
	public static boolean isDebugDB=false;
	
	
    // DB-associated configuration items
    public static String DBServiceItemInConfiguration = "Service";
    public static String Monarch_DTO_DataScheme_ItemName_in_Configuration = "monarch_dto_service";
    public static String DTO_DataScheme_ItemName_in_Configuration = "dto_service";
			
    public static int Gene_Phenotype_Relationship_Quality = 1;
    public static int Gene_Disease_Relationship_Quality = 2;
    public static int Gene_Phenotype_Relationship_Quantity = 3;
    public static int Gene_Disease_Relationship_Quantity = 4;    
    
    // External ontology
    public static String HUMAN_PHENOTYPE_ONTOLOGY = "HP";
    public static String HUMAN_PHENOTYPE_ONTOLOGY_URL_MAIN = "http://purl.obolibrary.org/obo";
    public static String HUMAN_PHENOTYPE_ONTOLOGY_ROOT_TERM = "HP_0000001";
    
    public static String ONTOFOX_INCLUDING_INTERMEDIATES = "includeAllIntermediates";
    public static String ONTOFOX_INCLUDING_COMPUTED_INTERMEDIATES = "includeComputedIntermediates";
    public static String ONTOFOX_INCLUDING_NoINTERMEDIATES = "includeNoIntermediates";
    
    public static String ONTOFOX_INCLUDING_ANNOTATION_PROPS = "includeAllAnnotationProperties";
    public static String ONTOFOX_INCLUDING_RECURSIVE_AXIOMS_PROPS = "includeAllAxiomsRecursively";
    
	public static Double logitFunction(double prob){
            return new Double(Math.log(prob)-Math.log(1-prob));
	}
}
