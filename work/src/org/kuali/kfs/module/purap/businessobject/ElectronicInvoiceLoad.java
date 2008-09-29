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

/**
 * This class has been copied from EPIC
 * 
 * @author delyea
 */
public class ElectronicInvoiceLoad {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ElectronicInvoiceLoad.class);

    private Map invoiceLoadSummaries;
    private Map rejectFilesToMove;
    private List partialFailureRejects;
    private List completeFailureRejects;

    public ElectronicInvoiceLoad() {
        invoiceLoadSummaries = new HashMap();
        rejectFilesToMove = new HashMap();
        partialFailureRejects = new ArrayList();
        completeFailureRejects = new ArrayList();
    }

    public void insertInvoiceLoadSummary(ElectronicInvoiceLoadSummary eils) {
        invoiceLoadSummaries.put(eils.getVendorDunsNumber(), eils);
    }

    public void addRejectFileToMove(File file, String directory) {
        rejectFilesToMove.put(file, directory);
    }

    public void addInvoiceReject(ElectronicInvoiceRejectDocument eir,
                                 boolean isCompleteFailure) {
        if (isCompleteFailure){
            completeFailureRejects.add(eir);
        }else{
            partialFailureRejects.add(eir);
        }
    }

    public List getPartialFailureRejects() {
        return partialFailureRejects;
    }
    
    public List getCompleteFailureRejects() {
        return completeFailureRejects;
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

}
