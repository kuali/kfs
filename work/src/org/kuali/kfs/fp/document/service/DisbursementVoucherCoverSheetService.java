package org.kuali.module.financial.service;


import java.io.IOException;
import java.io.OutputStream;

import org.kuali.module.financial.document.DisbursementVoucherDocument;

import com.lowagie.text.DocumentException;

/**
 * Service used for manipulating disbursement voucher cover sheets.
 * 
 * @author Kuali Financial Transactions Team ()
 */
public interface DisbursementVoucherCoverSheetService {
    /**
     * 
     * generates a disbursement voucher coversheet
     * 
     * @param templateName
     * @param document
     * @param outputStream
     */
    public void generateDisbursementVoucherCoverSheet(String templateDirectory, String templateName, DisbursementVoucherDocument document, OutputStream outputStream) throws DocumentException, IOException;
}
