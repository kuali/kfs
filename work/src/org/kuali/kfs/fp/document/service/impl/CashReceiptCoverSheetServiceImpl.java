/*
 * Copyright (c) 2004, 2005 The National Association of College and University 
 * Business Officers, Cornell University, Trustees of Indiana University, 
 * Michigan State University Board of Trustees, Trustees of San Joaquin Delta 
 * College, University of Hawai'i, The Arizona Board of Regents on behalf of the 
 * University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); 
 * By obtaining, using and/or copying this Original Work, you agree that you 
 * have read, understand, and will comply with the terms and conditions of the 
 * Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,  DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 * THE SOFTWARE.
 */

package org.kuali.module.financial.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.kuali.module.financial.document.CashReceiptDocument;
import org.kuali.module.financial.rules.CashReceiptDocumentRule;
import org.kuali.module.financial.service.CashReceiptCoverSheetService;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;

/**
 * Implementation of service for handling creation of the cover sheet of the 
 * <code>{@link CashReceiptDocument}</code>
 * 
 * @author Leo Przybylski
 */
public class CashReceiptCoverSheetServiceImpl 
    implements CashReceiptCoverSheetService {
    private static Log LOG = LogFactory.getLog(CashReceiptCoverSheetService.class);
    
    public static final String CR_COVERSHEET_TEMPLATE_RELATIVE_DIR = "templates/financial";
    public static final String CR_COVERSHEET_TEMPLATE_NM = "CashReceiptCoverSheetTemplate.pdf";

    private static final String DOCUMENT_NUMBER_FIELD = "DocumentNumber";
    private static final String INITIATOR_FIELD       = "Initiator";
    private static final String CREATED_DATE_FIELD    = "CreatedDate";
    private static final String AMOUNT_FIELD          = "Amount";
    private static final String ORG_DOC_NUMBER_FIELD  = "OrgDocNumber";
    private static final String CAMPUS_FIELD          = "Campus";
    private static final String DEPOSIT_DATE_FIELD    = "DepositDate";
    private static final String DESCRIPTION_FIELD     = "Description";
    private static final String EXPLANATION_FIELD     = "Explanation";
    private static final String CHECKS_FIELD          = "Checks";
    private static final String CURRENCY_FIELD        = "Currency";
    private static final String COIN_FIELD            = "Coin";
    private static final String CREDIT_CARD_FIELD     = "CreditCard";
    private static final String ADV_DEPOSIT_FIELD     = "AdvancedDeposit";
    private static final String CHANGE_OUT_FIELD      = "ChangeOut";
    private static final String REVIV_FUND_OUT_FIELD  = "RevivFundOut";
    
    /**
     * Generate a cover sheet for the <code>{@link CashReceiptDocument}</code>.
     * An <code>{@link OutputStream}</code> is written to for the
     * coversheet.
     * 
     * @param document
     * @param OutputStream
     * @exception DocumentException
     * @exception IOException
     * @see org.kuali.core.module.financial.service.CashReceiptCoverSheetServiceImpl#generateCoverSheet( org.kuali.module.financial.documentCashReceiptDocument )
     */
    public void generateCoverSheet( CashReceiptDocument document,
                                    OutputStream outputStream ) 
        throws DocumentException, IOException {
        String templateDirectory = CR_COVERSHEET_TEMPLATE_RELATIVE_DIR;
        String templateName = CR_COVERSHEET_TEMPLATE_NM;
        
        if( new CashReceiptDocumentRule().isCoverSheetPrintable( document ) ) {
            try {
                PdfReader reader = 
                    new PdfReader(templateDirectory 
                                  + File.separator + templateName);

                // populate form with document values
                PdfStamper stamper = new PdfStamper(reader, outputStream);

                AcroFields populatedCoverSheet = stamper.getAcroFields();
                // populatedCoverSheet.setField("attachment", attachment);

                stamper.setFormFlattening(true);
                stamper.close();
            }
            catch (DocumentException e) {
                LOG.error("Error creating coversheet for: " 
                          + document.getFinancialDocumentNumber() 
                          + ". ::" + e);
                throw e;
            }
            catch (IOException e) {
                LOG.error("Error creating coversheet for: " 
                          + document.getFinancialDocumentNumber() 
                          + ". ::" + e);
                throw e;
            }
        }
    }
}
