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
package org.kuali.module.financial.rules;

import java.sql.Timestamp;

import org.kuali.Constants;
import org.kuali.KeyConstants;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.util.GlobalVariables;
import org.kuali.module.financial.document.PreEncumbranceDocument;


/**
 * Business rule(s) applicable to PreEncumbrance documents.
 *
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class PreEncumbranceDocumentRule extends TransactionalDocumentRuleBase {

    /**
     * Pre Encumbrance document specific business rule checks for the "route document" event.
     *
     * @param document
     *
     * @return boolean True if the rules checks passed, false otherwise.
     */
    protected boolean processCustomRouteDocumentBusinessRules(TransactionalDocument document) {
        return isReversalDateValidForRouting((PreEncumbranceDocument) document);
    }

    /**
     * If a PreEncumbrance document has a reversal date, it must not be earlier than the current date to route.
     *
     * @param preEncumbranceDocument
     *
     * @return boolean True if this document does not have a reversal date earlier than the current date, false otherwise.
     */
    private boolean isReversalDateValidForRouting(PreEncumbranceDocument preEncumbranceDocument) {
        Timestamp reversalDate = preEncumbranceDocument.getReversalDate();
        return TransactionalDocumentRuleUtil.isValidReversalDate(reversalDate, "document.reversalDate");
    }

    

    /**
     * PreEncumbrance documents require at least one accounting line in either section for routing.
     *
     * @param transactionalDocument
     *
     * @return boolean True if the number of accounting lines are valid for routing, false otherwise.
     */
    protected boolean isAccountingLinesRequiredNumberForRoutingMet(TransactionalDocument transactionalDocument) {
        if (0 == transactionalDocument.getSourceAccountingLines().size()
            && 0 == transactionalDocument.getTargetAccountingLines().size())
        {
            GlobalVariables.getErrorMap().put(
                Constants.ACCOUNTING_LINE_ERRORS,
                KeyConstants.ERROR_DOCUMENT_NO_ACCOUNTING_LINES);
            return false;
        }
        else {
            return true;
        }
    }
}
