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
package org.kuali.module.financial.document;

import org.kuali.Constants;
import org.kuali.core.document.TransactionalDocumentBase;



/**
 * This is the business object that represents the IndirectCostAdjustmentDocument in Kuali. This 
 * is a transactional document that will eventually post transactions to the G/L.  It 
 * integrates with workflow and also contains two groupings of accounting lines: 
 * Expense and target.  Expense is the expense and target is the income lines. * 
 * Indirect Cost Adjustment Document
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class IndirectCostAdjustmentDocument extends TransactionalDocumentBase {
	private static final long serialVersionUID = -7550467058420297523L;
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(IndirectCostAdjustmentDocument.class);
	
	/**
	 * Initializes the array lists and some basic info.
	 */
	public IndirectCostAdjustmentDocument() {
		super();
	}


	// workflow related methods
    /**
     * Overrides the base implementation to return "Grant".
     */
    public String getSourceAccountingLinesSectionTitle() {
        return Constants.GRANT;
    }

    /**
     * Overrides the base implementation to return "ICR".
     */
    public String getTargetAccountingLinesSectionTitle() {
        return Constants.ICR;
    }
}
