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
    private List electronicInvoiceRejects;

    public ElectronicInvoiceLoad() {
        invoiceLoadSummaries = new HashMap();
        rejectFilesToMove = new HashMap();
        electronicInvoiceRejects = new ArrayList();
    }

    public void insertInvoiceLoadSummary(ElectronicInvoiceLoadSummary eils) {
        this.invoiceLoadSummaries.put(eils.getVendorDunsNumber(), eils);
    }

    public void insertRejectFileToMove(File file, String directory) {
        this.rejectFilesToMove.put(file, directory);
    }

    public void addInvoiceReject(ElectronicInvoiceRejectDocument eir) {
        this.electronicInvoiceRejects.add(eir);
    }

    public List getElectronicInvoiceRejects() {
        return electronicInvoiceRejects;
    }

    public void setElectronicInvoiceRejects(List electronicInvoiceRejects) {
        this.electronicInvoiceRejects = electronicInvoiceRejects;
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

    public void setRejectFilesToMove(Map rejectFilesToMove) {
        this.rejectFilesToMove = rejectFilesToMove;
    }
}
