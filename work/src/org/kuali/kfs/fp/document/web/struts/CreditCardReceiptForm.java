/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.financial.web.struts.form;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.kuali.core.util.SpringServiceLocator;
import org.kuali.core.web.struts.form.KualiTransactionalDocumentFormBase;
import org.kuali.module.financial.bo.AdvanceDepositDetail;
import org.kuali.module.financial.bo.CreditCardDetail;
import org.kuali.module.financial.document.CreditCardReceiptDocument;

/**
 * This class is the struts form for Credit Card Receipt document.
 * 
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class CreditCardReceiptForm extends KualiTransactionalDocumentFormBase {
    private CreditCardDetail newCreditCardReceipt;

    /**
     * Constructs a CreditCardReceiptForm.java.
     */
    public CreditCardReceiptForm() {
        super();
        setDocument(new CreditCardReceiptDocument());
        setNewCreditCardReceipt(new CreditCardDetail());
    }

    /**
     * @return CreditCardReceiptDocument
     */
    public CreditCardReceiptDocument getCreditCardReceiptDocument() {
        return (CreditCardReceiptDocument) getDocument();
    }

    /**
     * @return CreditCardDetail
     */
    public CreditCardDetail getNewCreditCardReceipt() {
        return newCreditCardReceipt;
    }

    /**
     * @param newCreditCardReceipt
     */
    public void setNewCreditCardReceipt(CreditCardDetail newCreditCardReceipt) {
        this.newCreditCardReceipt = newCreditCardReceipt;
    }
    
    /**
     * Overrides the parent to call super.populate and then tells each line to check the associated data dictionary
     * and modify the values entered to follow all the attributes set for the values of the accounting line.
     * 
     * @see org.kuali.core.web.struts.form.KualiTransactionalDocumentFormBase#populate(javax.servlet.http.HttpServletRequest)
     */
    @Override
    public void populate(HttpServletRequest request) {
        super.populate(request);

        //
        // now run through all of the accounting lines and make sure they've been uppercased and populated appropriately
        SpringServiceLocator.getBusinessObjectDictionaryService().performForceUppercase(getNewCreditCardReceipt());
        
        List<CreditCardDetail> creditCardReceipts = getCreditCardReceiptDocument().getCreditCardReceipts();
        for (CreditCardDetail detail : creditCardReceipts) {
            SpringServiceLocator.getBusinessObjectDictionaryService().performForceUppercase(detail);
       }

    }
}