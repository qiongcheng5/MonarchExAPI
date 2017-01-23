/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package idg.db.dao;

/**
 *
 * @author Qiong Cheng
 */
public class TargetObj {
    
    // Add the getter/setter methods later
    public long GeneID;
    public String GeneSymbol;
    public String UniprotID;
    public String TDL;
    public String IDGFamily;
    
    public TargetObj(long geneid, String genesym, String uniprotid, String tdl, String idgfamily ){
        this.UniprotID = uniprotid;
        this.GeneID = geneid;
        this.GeneSymbol = genesym;
        this.TDL = tdl;
        this.IDGFamily = idgfamily;
    }
    
}
