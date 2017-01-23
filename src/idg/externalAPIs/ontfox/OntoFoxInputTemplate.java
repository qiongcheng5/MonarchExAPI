package idg.externalAPIs.ontfox;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.LinkedList;

import idg.util.Common;

/**
 * Template
 * @author Qiong Cheng
 Sample:
 
 [URI of the OWL(RDF/XML) output file]


[Source ontology]
HP

[Low level source term URIs]
http://purl.obolibrary.org/obo/HP_0002133

[Top level source term URIs and target direct superclass URIs]
http://purl.obolibrary.org/obo/HP_0000001

[Source term retrieval setting]
includeAllIntermediates
# includeNoIntermediates
# includeComputedIntermediates

[Source annotation URIs]
includeAllAnnotationProperties
includeAllAxiomsRecursively
# The default is no annotation to be assigned. Use includeAllAnnotationProperties to include all 
# annotations. Use includeAllAxioms to include all annotations and other related axioms. Use 
# includeAllAxiomsRecursively to include all axioms for the specified terms and the related terms 
# recursively. 

[Source annotation URIs to be excluded]


 */
public class OntoFoxInputTemplate {
	
	LinkedList<String> list_LowLevelSourceTerm;
	LinkedList<String> list_TopLevelSourceTerm;
	LinkedList<String> list_SourceTermRetrievalSetting;
	LinkedList<String> list_SourceAnnotationURIs;
	
	static String OUTPUT_URI_SECTION_HEADER = "[URI of the OWL(RDF/XML) output file]";
	static String SOURCE_ONTOLOGY_HEADER = "[Source ontology]";
	static String LOW_LEVEL_SOURCE_TERM_URIS_HEADER = "[Low level source term URIs]";
	static String TOP_LEVEL_SOURCE_TERM_URIS_HEADER = "[Top level source term URIs and target direct superclass URIs]";
	static String SOURCE_TERM_RETRIEVAL_SETTING_HEADER = "[Source term retrieval setting]";
	static String SOURCE_ANNOTATION_URIS_HEADER = "[Source annotation URIs]";
	static String EXCLUDED_SOURCE_ANNOTATION_URIS_HEADER = "[Source annotation URIs to be excluded]";
	
	public OntoFoxInputTemplate(LinkedList<String> lowLevelTerms){
		this.list_LowLevelSourceTerm = lowLevelTerms;
		this.list_TopLevelSourceTerm = new LinkedList<String>();
		this.list_TopLevelSourceTerm.add(Common.HUMAN_PHENOTYPE_ONTOLOGY_ROOT_TERM);
		
		this.list_SourceTermRetrievalSetting = new LinkedList<String>();
		this.list_SourceTermRetrievalSetting.add(Common.ONTOFOX_INCLUDING_INTERMEDIATES);
		
		this.list_SourceAnnotationURIs = new LinkedList<String>();
		this.list_SourceAnnotationURIs.add(Common.ONTOFOX_INCLUDING_ANNOTATION_PROPS);
		this.list_SourceAnnotationURIs.add(Common.ONTOFOX_INCLUDING_RECURSIVE_AXIOMS_PROPS);
	}
	
	public OntoFoxInputTemplate(LinkedList<String> lowLevelTerms, 
			LinkedList<String> topLevelTerms){
		this.list_LowLevelSourceTerm = lowLevelTerms;
		this.list_TopLevelSourceTerm = topLevelTerms;
		
		this.list_SourceTermRetrievalSetting = new LinkedList<String>();
		this.list_SourceTermRetrievalSetting.add(Common.ONTOFOX_INCLUDING_INTERMEDIATES);
		
		this.list_SourceAnnotationURIs = new LinkedList<String>();
		this.list_SourceAnnotationURIs.add(Common.ONTOFOX_INCLUDING_ANNOTATION_PROPS);
		this.list_SourceAnnotationURIs.add(Common.ONTOFOX_INCLUDING_RECURSIVE_AXIOMS_PROPS);
	}
	
	public OntoFoxInputTemplate(LinkedList<String> lowLevelTerms, 
			LinkedList<String> topLevelTerms, 
			LinkedList<String>  retrievalSettings, 
			LinkedList<String> annotationURIs){
		this.list_LowLevelSourceTerm = lowLevelTerms;
		this.list_TopLevelSourceTerm = topLevelTerms;
		this.list_SourceTermRetrievalSetting = retrievalSettings;
		this.list_SourceAnnotationURIs = annotationURIs;
	}
	
	public void writeFile(String filePathString) throws FileNotFoundException{
		if (filePathString == null) return;
		File f = new File(filePathString);
		if(f.isDirectory()) { 
		    System.out.println("[OntoFoxInputTemplate] writeFile: parameter is a director and file path is needed.");
		    return;
		}
		
		if (f.exists()){
			System.out.println("[OntoFoxInputTemplate] writeFile: [warning] " + filePathString + " is to be rewritten.");
		}
		
		PrintWriter outputFile = new PrintWriter(filePathString);
		outputFile.println(OUTPUT_URI_SECTION_HEADER);
		outputFile.println();
		
		outputFile.println(SOURCE_ONTOLOGY_HEADER);
		outputFile.println(Common.HUMAN_PHENOTYPE_ONTOLOGY);
		
		outputFile.println(LOW_LEVEL_SOURCE_TERM_URIS_HEADER);
		for (String item : this.list_LowLevelSourceTerm)
			outputFile.println(Common.HUMAN_PHENOTYPE_ONTOLOGY_URL_MAIN + "/" + item);
		
		outputFile.println(TOP_LEVEL_SOURCE_TERM_URIS_HEADER);
		for (String item : this.list_TopLevelSourceTerm)
			outputFile.println(Common.HUMAN_PHENOTYPE_ONTOLOGY_URL_MAIN + "/" + item);
		
		outputFile.println(SOURCE_TERM_RETRIEVAL_SETTING_HEADER);
		for (String item : this.list_SourceTermRetrievalSetting)
			outputFile.println(item);
		
		outputFile.println(SOURCE_ANNOTATION_URIS_HEADER);
		for (String item : this.list_SourceAnnotationURIs)
			outputFile.println(item);
		
		outputFile.println(EXCLUDED_SOURCE_ANNOTATION_URIS_HEADER);
		outputFile.println();
		
		outputFile.close();
	}
	
}
