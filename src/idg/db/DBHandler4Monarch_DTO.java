/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package idg.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.Vector;

import idg.QualityBasedAssociationInMonarch;
import idg.db.dao.PairwiseObjects;
import idg.db.dao.TargetObj;
import idg.externalAPIs.monarch.MonarchScore;
import idg.util.Common;
import idg.util.ConfigFile;
import idg.util.DBSource;

import java.io.PrintWriter;

/**
 * @author Qiong Cheng
 * 
 */
public class DBHandler4Monarch_DTO {
    Connection mDTOConn = null;
    Connection dtoConn = null;
    final static String MONARCH_RELATIONSHIP_TABLE = "tbl_monarch_associations";
    ResultSet objRs;
    String tableName;
    //private PrintWriter log;
	
    public DBHandler4Monarch_DTO() throws SQLException{
        mDTOConn = DBSource.getInstance().getConnection(ConfigFile.getProperty(Common.Monarch_DTO_DataScheme_ItemName_in_Configuration));
    }
    
    public DBHandler4Monarch_DTO(Connection conn) throws SQLException{
        mDTOConn = conn;
    }
    
    public void release()throws SQLException{
        DBSource.getInstance().freeConnection(mDTOConn);
    }

    public synchronized LinkedList<TargetObj> getDTOProteins() throws SQLException{
        //String sTableName = "tbl_dto_proteins";//"view_dto_proteins_m2";//tbl_dto_proteins" ;
        LinkedList list = null ;
        Statement objSt = null ;
        ResultSet rs = null ;

        //String strsql = " select gene_symbol,uniprot_id, tdl, idg_family from " + sTableName +";" ;
        String strsql = "SELECT " + 
                    "tbl_dto_uniprotid2genesymbol.gene_id, " + 
                    "tbl_dto_proteins.gene_symbol, " + 
                    "tbl_dto_proteins.uniprot_id, " + 
                    "tbl_dto_proteins.tdl, " + 
                    "tbl_dto_proteins.idg_family " + 
                "FROM " + 
                    "tbl_dto_proteins, tbl_dto_uniprotid2genesymbol " + 
                "WHERE tbl_dto_proteins.uniprot_id = tbl_dto_uniprotid2genesymbol.uniprot_id;";

        if (Common.isDebug) System.out.println(strsql) ;

        objSt = mDTOConn.createStatement( ) ;
        rs = objSt.executeQuery( strsql) ;
        while ( rs.next() ) {
            if ( list == null )
                list = new LinkedList();
             list.add(new TargetObj( rs.getLong( "gene_id" ),
                    rs.getString( "gene_symbol" ),
                    rs.getString( "uniprot_id" ),
                    rs.getString( "tdl" ), 
                    rs.getString("idg_family")) ) ;
        }

        rs.close() ;
        rs = null ;
        objSt.close() ;
        objSt = null ;

        return list ;
    }
    
    public synchronized LinkedList<TargetObj> getTargetsFromDisGeNET() throws SQLException{
        LinkedList list = null ;
        Statement objSt = null ;
        ResultSet rs = null ;

        String strsql = "SELECT gene_id, gene_symbol, Uniprot_ID FROM tbl_disgenet_proteins;\n";
        if (Common.isDebug) System.out.println(strsql) ;

        objSt = mDTOConn.createStatement( ) ;
        rs = objSt.executeQuery( strsql) ;
        while ( rs.next() ) {
            if ( list == null )
                list = new LinkedList();
             list.add(new TargetObj( rs.getLong( "gene_id" ),
                    rs.getString( "gene_symbol" ),
                    rs.getString( "uniprot_id" ),
                    "NON", 
                    "NON") ) ;
        }

        rs.close() ;
        rs = null ;
        objSt.close() ;
        objSt = null ;

        return list ;
    }
    
    public synchronized LinkedList<Long> getCommonGeneIDsOfDTOMonarch() throws SQLException{
        LinkedList list = null ;
        Statement objSt = null ;
        ResultSet rs = null ;

        String strsql = "select distinct Gene_ID from tbl_dto_hpo_disease_mappings order by CONVERT(Gene_ID, DECIMAL);\n"; //tbl_hpo_gene_disease_phenotype_mapping;\n";
        if (Common.isDebug) System.out.println(strsql) ;

        objSt = mDTOConn.createStatement( ) ;
        rs = objSt.executeQuery( strsql) ;
        while ( rs.next() ) {
            if ( list == null )
                list = new LinkedList();
             list.add(rs.getLong( "gene_id" ));
        }

        rs.close() ;
        rs = null ;
        objSt.close() ;
        objSt = null ;

        return list ;
    }
    
    /*
    Changes are in need.
    */
    public synchronized boolean insertEntity(int entityType, Object entityObj) {
        LinkedList list = null ;
        Statement objSt = null ;
        boolean result = false ;

        try{
            String strsql = "SELECT gene_id, gene_symbol, Uniprot_ID FROM tbl_disgenet_proteins;\n";
            if (Common.isDebug) System.out.println(strsql) ;

            objSt = mDTOConn.createStatement( ) ;
            objSt.executeUpdate(strsql) ;

            objSt.close() ;
            result = true;
        } catch (SQLException ex){
            ex.printStackTrace();
        }

        return result;
    }
	
    public synchronized boolean dropTable4GenePhenotypeAssociations() {
        LinkedList list = null ;
        Statement objSt = null ;
        boolean result = false ;
        String strsql = "";

        try{
            strsql = "DROP TABLE IF EXISTS `monarch_dto`.`" + MONARCH_RELATIONSHIP_TABLE + "` ;\n";
            if (Common.isDebug) System.out.println(strsql) ;

            objSt = mDTOConn.createStatement( ) ;
            objSt.executeUpdate(strsql) ;

            objSt.close() ;
            result = true;
        } catch (SQLException ex){
            System.out.println("Fail to execute " + strsql) ;
            ex.printStackTrace();
        }

        return result;
    }
    
    public synchronized boolean creatTable4GenePhenotypeAssociations() {
        LinkedList list = null ;
        Statement objSt = null ;
        boolean result = false ;
        String strsql = "";

        try{
            strsql = "CREATE TABLE IF NOT EXISTS `monarch_dto`.`" + MONARCH_RELATIONSHIP_TABLE + "` (\n" +
                    "  `Subject_Gene_ID` VARCHAR(20) NOT NULL COMMENT '',\n" +
                    "  `Subject_Label` VARCHAR(20) NOT NULL COMMENT '',\n" +
                    "  `Subject_Taxon` VARCHAR(20) NOT NULL COMMENT '',\n" +
                    "  `subject_taxon_label` VARCHAR(45) NOT NULL COMMENT '',\n" +
                    "  `Relation` VARCHAR(20) NOT NULL COMMENT '',\n" +
                    "  `Relation_Label` VARCHAR(20) NOT NULL COMMENT '',\n" +
                    "  `Object` VARCHAR(20) NOT NULL COMMENT '',\n" +
                    "  `Object_Label` VARCHAR(256) NOT NULL COMMENT '',\n" +
                    "  `Evidence` VARCHAR(1024) NOT NULL COMMENT '',\n" +
                    "  `Evidence_Label` VARCHAR(1124) NOT NULL COMMENT '',\n" +
                    //"  `SourceRef` VARCHAR(256) NOT NULL COMMENT '',\n" +
                    //"  `Is_Defined_By` VARCHAR(256) NOT NULL COMMENT '',\n" +
                    "  `Qualifier` VARCHAR(20) NOT NULL COMMENT '',  \n" +
                    "  `Max_Score_S2O` FLOAT, \n" +
                    "  `Avg_Score_S2O` FLOAT, \n" +
                    "  `Max_Percentage_Score_S2O` FLOAT, \n" +
                    "  `Avg_Percentage_Score_S2O` FLOAT, \n" +
                    "  `Combined_Percentage_Score_S2O` FLOAT, \n" +
                    "  `Combined_Score_In_Monarch_S2O` FLOAT, \n" +
                    "  `Max_Score_O2S` FLOAT, \n" +
                    "  `Avg_Score_O2S` FLOAT, \n" +
                    "  `Max_Percentage_Score_O2S` FLOAT, \n" +
                    "  `Avg_Percentage_Score_O2S` FLOAT, \n" +
                    "  `Combined_Percentage_Score_O2S` FLOAT, \n" +
                    "  `Combined_Score_In_Monarch_O2S` FLOAT \n" +
                    "  )\n";
            //         + "SELECT gene_id, gene_symbol, Uniprot_ID FROM tbl_disgenet_proteins;\n";
            if (Common.isDebug) System.out.println(strsql) ;

            objSt = mDTOConn.createStatement( ) ;
            objSt.executeUpdate(strsql) ;

            objSt.close() ;
            result = true;
        } catch (SQLException ex){
            System.out.println("Fail to execute " + strsql) ;
            ex.printStackTrace();
        }

        return result;
    }
    
    public synchronized boolean insertMonarchQualityBasedAssociation(QualityBasedAssociationInMonarch entityObj) {
        LinkedList list = null ;
        Statement objSt = null ;
        boolean result = false ;

        try{
            String strsql = "INSERT INTO " + MONARCH_RELATIONSHIP_TABLE + " ("
                    + "Subject_Gene_ID, Subject_Label, Subject_Taxon, "
                    + "subject_taxon_label, Relation, Relation_Label, "
                    + "Object, Object_Label,Evidence, Evidence_Label, "
                    //+ "SourceRef, Is_Defined_By, "
                    + "Qualifier ) VALUES (\""
                    + entityObj.subject + "\",\""
                    + entityObj.subject_label + "\",\""
                    + entityObj.subject_taxon + "\",\""
                    + entityObj.subject_taxon_label + "\",\""
                    + entityObj.relation + "\",\""
                    + entityObj.relation_label + "\",\""
                    + entityObj.object + "\",\""
                    + entityObj.object_label + "\",\""
                    + entityObj.evidence + "\",\""
                    + entityObj.evidence_label + "\",\""
                   // + entityObj.source + "\",\""
                   // + entityObj.is_defined_by + "\",\""
                    + entityObj.qualifier
                    + "\")";
            if (Common.isDebug) System.out.println(strsql) ;

            objSt = mDTOConn.createStatement( ) ;
            objSt.executeUpdate(strsql) ;

            objSt.close() ;
            result = true;
        } catch (SQLException ex){
            ex.printStackTrace();
        }

        return result;
    }
    
    public synchronized boolean updateMonarchAssociationScore(PairwiseObjects pair, 
    				MonarchScore score4Sub2Obj, 
    				MonarchScore score4Obj2Sub) {
        LinkedList list = null ;
        Statement objSt = null ;
        boolean result = false ;

        if (score4Sub2Obj == null || score4Obj2Sub == null || pair == null)
        		return false;
        
        try{
            String strsql = "SET SQL_SAFE_UPDATES = 0; " ;
            objSt = mDTOConn.createStatement( ) ;
            objSt.executeUpdate(strsql) ;
            
            strsql = "UPDATE " + MONARCH_RELATIONSHIP_TABLE 
            		+ " SET "
            		+ " Max_Score_S2O = " + score4Sub2Obj.getMaxScore() + ","
            		+ " Avg_Score_S2O = " + score4Sub2Obj.getAvgScore() + ","
                + " Max_Percentage_Score_S2O = " + score4Sub2Obj.getMaxPercentageScore() + ","
                + " Avg_Percentage_Score_S2O = " + score4Sub2Obj.getAvgPercentageScore() + ","
                + " Combined_Percentage_Score_S2O = " + score4Sub2Obj.getCombinedPercentageScore() + ","
                + " Combined_Score_In_Monarch_S2O = " + score4Sub2Obj.getCombinedScoreInMonarch() + ","   
            		+ " Max_Score_O2S = " + score4Obj2Sub.getMaxScore() + ","
            		+ " Avg_Score_O2S = " + score4Obj2Sub.getAvgScore() + ","
                + " Max_Percentage_Score_O2S = " + score4Obj2Sub.getMaxPercentageScore() + ","
                + " Avg_Percentage_Score_O2S = " + score4Obj2Sub.getAvgPercentageScore() + ","
                + " Combined_Percentage_Score_O2S = " + score4Obj2Sub.getCombinedPercentageScore() + ","
                + " Combined_Score_In_Monarch_O2S = " + score4Obj2Sub.getCombinedScoreInMonarch() + " "  
            		+ " WHERE Object=\"" + pair.getObject()
            		+ "\" AND Subject_Gene_ID = \"" + pair.getSubject() + "\";";

            if (Common.isDebug) 
            		System.out.println(strsql) ;

            objSt.executeUpdate(strsql) ;

            objSt.close() ;
            result = true;
        } catch (SQLException ex){
            ex.printStackTrace();
        }

        return result;
    }


    public synchronized LinkedList<String> getInvolvedPhenotypesOfDTOMonarch() throws SQLException{
        LinkedList list = null ;
        Statement objSt = null ;
        ResultSet rs = null ;

        String strsql = "SELECT  distinct replace(object, ':', '_') FROM " + MONARCH_RELATIONSHIP_TABLE + "\n"; 
        if (Common.isDebug) System.out.println(strsql) ;

        objSt = mDTOConn.createStatement( ) ;
        rs = objSt.executeQuery( strsql) ;
        while ( rs.next() ) {
            if ( list == null )
                list = new LinkedList();
             list.add(rs.getString(1));
        }

        rs.close() ;
        rs = null ;
        objSt.close() ;
        objSt = null ;

        return list ;
    }
    
    public synchronized LinkedList<PairwiseObjects> getMonarchAssociationPairs(int relType) throws SQLException{
        LinkedList list = null ;
        Statement objSt = null ;
        ResultSet rs = null ;

        String strsql = null;
	    	switch (relType){
	        case 1: //Gene_Phenotype_Relationship_Quality
		        	strsql = "SELECT Subject_Gene_ID, Object FROM " 
		            		+ MONARCH_RELATIONSHIP_TABLE + " where Relation=\"G2P\";";
	            break;
	        case 2: //Gene_Disease_Relationship_Quality
		        	strsql = "SELECT Subject_Gene_ID, Object FROM " 
		            		+ MONARCH_RELATIONSHIP_TABLE + " where Relation=\"G2D\";";
		    default:
	    }

        if (Common.isDebug) System.out.println(strsql) ;

        objSt = mDTOConn.createStatement( ) ;
        rs = objSt.executeQuery( strsql) ;
        while ( rs.next() ) {
            if ( list == null )
                list = new LinkedList();
             list.add(new PairwiseObjects( rs.getString( "Subject_Gene_ID" ),
                    rs.getString( "Object" ) )) ;
        }

        rs.close() ;
        rs = null ;
        objSt.close() ;
        objSt = null ;

        return list ;
    }
}
