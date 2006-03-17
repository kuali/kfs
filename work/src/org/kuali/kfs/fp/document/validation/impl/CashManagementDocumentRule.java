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

import java.util.Iterator;

import org.apache.log4j.Logger;
import org.kuali.core.bo.user.KualiUser;
import org.kuali.core.document.Document;
import org.kuali.core.rule.DocumentRuleBase;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.financial.bo.CashDrawer;
import org.kuali.module.financial.bo.Deposit;
import org.kuali.module.financial.bo.DepositCashReceiptControl;
import org.kuali.module.financial.document.CashManagementDocument;
import org.kuali.module.financial.document.CashReceiptDocument;
import org.kuali.Constants;

import edu.iu.uis.eden.EdenConstants;

/**
 * Business rule(s) applicable to Cash Management Document.
 * 
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class CashManagementDocumentRule extends DocumentRuleBase {
    private static final Logger LOG = Logger.getLogger(CashManagementDocumentRule.class);
    
    /**
     * Overrides to validate that the person saving the document is the initiator, validates that the cash drawer is open 
     * for initial creation, validates that the cash drawer for the specfic verification unit is closed for subsequent 
     * saves, and validates that the associate cash receipts are still verified. 
     * 
     * @see org.kuali.core.rule.DocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.core.document.Document)
     */
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        boolean isValid = super.processCustomSaveDocumentBusinessRules(document);
        
        CashManagementDocument cmd = (CashManagementDocument) document;
        
        // verify user is initiator
        verifyUserIsDocumentInitiator(cmd);
        
        // verify the cash drawer for the verification unit is closed for post-initialized saves
        verifyCashDrawerForVerificationUnitIsClosedForPostInitiationSaves(cmd);
        
        // verify that CRs are still verified
        verifyAssociatedCashReceiptsAreVerified(cmd);
        
        return isValid;
    }

    /**
     * This method double checks that the CRs that this cash management document's deposit, are 
     * still verified.
     * 
     * @param cmd
     */
    private void verifyAssociatedCashReceiptsAreVerified(CashManagementDocument cmd) {
        Iterator deposits = cmd.getDeposits().iterator();
        while(deposits.hasNext()) {
            Deposit deposit = (Deposit) deposits.next();
            Iterator depositCashReceiptControls = deposit.getDepositCashReceiptControl().iterator();
            while(depositCashReceiptControls.hasNext()) {
                DepositCashReceiptControl depositCashReceiptControl = (DepositCashReceiptControl) depositCashReceiptControls.next();
                CashReceiptDocument cashReceipt = depositCashReceiptControl.getCashReceiptHeader().getCashReceiptDocument();
                if(!cashReceipt.getDocumentHeader().getFinancialDocumentStatusCode().
                        equals(Constants.CashReceiptConstants.DOCUMENT_STATUS_CD_CASH_RECEIPT_VERIFIED)) {
                    throw new IllegalStateException("Cash receipt document number " + 
                            cashReceipt.getFinancialDocumentNumber() + " is not in a verified state.  It must be in " +
                                    "order for the deposit/cash management document that it is associated to be submitted.");
                }
            }
        }
    }

    /**
     * This method checks to make sure that the current system user is the person 
     * that initiated this document in the first place.
     * 
     * @param cmd
     */
    private void verifyUserIsDocumentInitiator(CashManagementDocument cmd) {
        KualiUser currentUser = GlobalVariables.getUserSession().getKualiUser();
        if(cmd.getDocumentHeader() != null && cmd.getDocumentHeader().getWorkflowDocument() != null) {
            String cmdInitiatorNetworkId = cmd.getDocumentHeader().getWorkflowDocument().getInitiatorNetworkId();
            if(!cmdInitiatorNetworkId.equals(currentUser.getPersonUserIdentifier())) {
                throw new IllegalStateException("The current user (" + currentUser.getPersonUserIdentifier() + 
                        ") is not the individual (" + cmdInitiatorNetworkId + ") that initiated this document.");
            }
        }
    }
    
    /**
     * This method checks to make sure that the cash drawer is closed for the associated verification unit, 
     * for post initiation saves.
     * 
     * @param cmd
     */
    private void verifyCashDrawerForVerificationUnitIsClosedForPostInitiationSaves(CashManagementDocument cmd) {
        if(cmd.getDocumentHeader() != null && 
                cmd.getDocumentHeader().getWorkflowDocument() != null && 
                cmd.getDocumentHeader().getWorkflowDocument().getRouteHeader() != null) {
            String workflowRouteStatusCode = cmd.getDocumentHeader().getWorkflowDocument().getRouteHeader().getDocRouteStatus();
            if(EdenConstants.ROUTE_HEADER_SAVED_CD.equals(workflowRouteStatusCode)) {
                // now verify that the associated cash drawer is closed
                CashDrawer cd = SpringServiceLocator.getCashDrawerService().getByWorkgroupName(cmd.getWorkgroupName());
                if(cd.isOpen()) {
                    throw new IllegalStateException("The cash drawer for verification unit \"" + cd.getWorkgroupName() + 
                        "\" is open.  It should be closed when a cash management document for that verification unit is open and being saved.");
                }
            }
        }
    }
    
    /**
     * Overrides parent to validate that the contained deposit business objects contain valid data 
     * according to the data dictionary set up.
     * 
     * @see org.kuali.core.rule.DocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.core.document.Document)
     */
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        boolean isValid = super.processCustomRouteDocumentBusinessRules(document);
        
        // iterate over all deposits contained within this cash management document and validate that 
        // each is complete according to its DD file
        CashManagementDocument cmd = (CashManagementDocument) document;
        Iterator deposits = cmd.getDeposits().iterator();
        while(deposits.hasNext()) {
            Deposit deposit = (Deposit) deposits.next();
            isValid &= validateDeposit(deposit);
        }
        
        return isValid;
    }
    
    /**
     * This method validates a deposit business object using the data dictionary service.
     * 
     * @param deposit
     * @return boolean
     */
    private boolean validateDeposit(Deposit deposit) {
        // validate the specific deposit coming in
        SpringServiceLocator.getDictionaryValidationService().validateBusinessObject(deposit);
        
        return GlobalVariables.getErrorMap().isEmpty();
    }
}