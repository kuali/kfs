package edu.arizona.kfs.module.tax.service;

import java.io.File;
import java.io.OutputStream;
import java.util.List;

import edu.arizona.kfs.module.tax.businessobject.Payee;
import edu.arizona.kfs.module.tax.businessobject.Payer;

public interface TaxForm1099GeneratorService {

    /**
     * Perform logic for the BatchPayeeFormStep
     */
    public boolean generateBatchPayeeForms(int year, List<Payee> list);

    /**
     * Creates a 1099 Form in a PDF file into the specified Output Stream with the given data.
     */
    public boolean createPdfFile(Integer taxYear, int pageNum, OutputStream os, Payer payer, List<Payee> payees);

    /**
     * Retrieve the File object to represent where Tax documents are staged.
     */
    public File getTaxYearFolder(int year);

}
