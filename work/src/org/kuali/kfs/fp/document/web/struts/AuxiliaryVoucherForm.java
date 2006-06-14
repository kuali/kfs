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

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.servlet.http.HttpServletRequest;

import org.kuali.module.financial.document.AuxiliaryVoucherDocument;

import static org.kuali.Constants.AuxiliaryVoucher.ACCRUAL_DOC_TYPE;
import static org.kuali.Constants.AuxiliaryVoucher.ADJUSTMENT_DOC_TYPE;
import static org.kuali.Constants.AuxiliaryVoucher.RECODE_DOC_TYPE;

/**
 * This class...
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class AuxiliaryVoucherForm extends VoucherForm {
	private static final Calendar calendar = new GregorianCalendar();
    
    public AuxiliaryVoucherForm() {
        super();
        setDocument(new AuxiliaryVoucherDocument());
    }
    
    /**
     * Overrides the parent to call super.populate and then to call the two methods that are specific to loading the two select
     * lists on the page.  In addition, this also makes sure that the credit and debit amounts are filled in for situations where 
     * validation errors occur and the page reposts.
     * 
     * @see org.kuali.core.web.struts.pojo.PojoForm#populate(javax.servlet.http.HttpServletRequest)
     */
    public void populate(HttpServletRequest request) {
        // populate the drop downs
        super.populate(request);
		calendar.setTime(new Timestamp(System.currentTimeMillis()));
        populateReversalDateForRendering();
    }

    /**
     * @return Returns the serviceBillingDocument.
     */
    public AuxiliaryVoucherDocument getAuxiliaryVoucherDocument() {
        return (AuxiliaryVoucherDocument) getDocument();
    }

    /**
     * @param serviceBillingDocument The serviceBillingDocument to set.
     */ 
    public void setAuxiliaryVoucherDocument(AuxiliaryVoucherDocument auxiliaryVoucherDocument) {
        setDocument(auxiliaryVoucherDocument);
    }    

	/**
	 * Handles special case display rules for displaying Reversal Date at UI layer
	 */
	public void populateReversalDateForRendering() {
		System.out.println("Doc type = " + getAuxiliaryVoucherDocument().getTypeCode());
		Long now = System.currentTimeMillis();
		calendar.setTimeInMillis(now);
		if (getAuxiliaryVoucherDocument().getTypeCode().equals(ACCRUAL_DOC_TYPE) &&
			(getAuxiliaryVoucherDocument().getReversalDate() == null || 
			 calendar.after(getAuxiliaryVoucherDocument().getReversalDate()))) {
			getAuxiliaryVoucherDocument().setReversalDate(new Timestamp(now));
		}
		else if (getAuxiliaryVoucherDocument().getTypeCode().equals(ADJUSTMENT_DOC_TYPE)) {
			getAuxiliaryVoucherDocument().setReversalDate(null);
		}
		else if (getAuxiliaryVoucherDocument().getTypeCode().equals(RECODE_DOC_TYPE)) {
			getAuxiliaryVoucherDocument()
				.setReversalDate(getDocument().getDocumentHeader().getWorkflowDocument().getCreateDate());
		}
		System.out.println("Reversal Date is now: " + getAuxiliaryVoucherDocument().getReversalDate());
	}
	
    /**
     * This method returns the reversal date in the format MMM d, yyyy.
     *
     * @return String
     */
    public String getFormattedReversalDate() {
        return formatReversalDate(getAuxiliaryVoucherDocument().getReversalDate());
    }
}

