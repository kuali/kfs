package org.kuali.module.financial.service;


import java.io.IOException;
import java.io.OutputStream;

import org.kuali.module.financial.document.DisbursementVoucherDocument;

import com.lowagie.text.DocumentException;

/**
 * Service used for manipulating disbursement voucher cover sheets.
 * 
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 * @version $Id: DisbursementVoucherCoverSheetService.java,v 1.1 2006-02-06 01:30:20 maynalem Exp $
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
    public void generateDisbursementVoucherCoverSheet(String templateDirectory, String templateName,
            DisbursementVoucherDocument document, OutputStream outputStream) throws DocumentException, IOException;
}
