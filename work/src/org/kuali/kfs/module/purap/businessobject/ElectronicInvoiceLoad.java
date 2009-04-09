/*
 * Created on Mar 9, 2005
 *
 */
package org.kuali.kfs.module.purap.businessobject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.purap.document.ElectronicInvoiceRejectDocument;

public class ElectronicInvoiceLoad {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ElectronicInvoiceLoad.class);

    private Map invoiceLoadSummaries;
    private Map rejectFilesToMove;
    private List rejectDocumentList;

    public ElectronicInvoiceLoad() {
        invoiceLoadSummaries = new HashMap();
        rejectFilesToMove = new HashMap();
        rejectDocumentList = new ArrayList();
    }

    public void insertInvoiceLoadSummary(ElectronicInvoiceLoadSummary eils) {
        invoiceLoadSummaries.put(eils.getVendorDunsNumber(), eils);
    }

    public void addRejectFileToMove(File file, String directory) {
        rejectFilesToMove.put(file, directory);
    }

    public void addInvoiceReject(ElectronicInvoiceRejectDocument eir) {
        rejectDocumentList.add(eir);
    }

    public List getRejectDocuments() {
        return rejectDocumentList;
    }

    public Map getInvoiceLoadSummaries() {
        return invoiceLoadSummaries;
    }

    public void setInvoiceLoadSummaries(Map invoiceLoadSummaries) {
        this.invoiceLoadSummaries = invoiceLoadSummaries;
    }

    public Map getRejectFilesToMove() {
        return rejectFilesToMove;
    }
    
    public boolean containsRejects(){
        return !rejectDocumentList.isEmpty();
    }

}
