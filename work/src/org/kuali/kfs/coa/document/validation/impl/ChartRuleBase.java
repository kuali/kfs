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
package org.kuali.module.chart.rules;

import org.apache.commons.lang.StringUtils;
import org.kuali.KeyConstants;
import org.kuali.core.document.Document;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.Maintainable;
import org.kuali.core.rule.RouteDocumentRule;
import org.kuali.core.rule.SaveDocumentRule;
import org.kuali.core.util.GlobalVariables;
import org.kuali.module.chart.bo.Account;

public class ChartRuleBase implements RouteDocumentRule, SaveDocumentRule {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ChartRuleBase.class);

    /**
     * This routes the document
     * 
     * @see org.kuali.core.rule.RouteDocumentRule#processRouteDocument(org.kuali.core.document.Document)
     */
    public boolean processRouteDocument(Document document) {
        MaintenanceDocument maintenanceDocument = (MaintenanceDocument) document;

        if (isDocumentValidForRouting(maintenanceDocument)) {
            return processCustomSaveDocumentBusinessRules((MaintenanceDocument) document);
        }
        else {
            return false;
        }
    }

    /**
     * This saves the document
     * 
     * @see org.kuali.core.rule.SaveDocumentRule#processSaveDocument(org.kuali.core.document.Document)
     */
    public boolean processSaveDocument(Document document) {
        MaintenanceDocument maintenanceDocument = (MaintenanceDocument) document;

        if (isDocumentValidForSave(maintenanceDocument)) {
            return processCustomSaveDocumentBusinessRules((MaintenanceDocument) document);
        }
        else {
            return false;
        }
    }

    /**
     * This method should be overridden to provide custom rules for processing document saving
     * 
     * @param document
     * @return
     */
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        return true;
    }

    /**
     * 
     * This method should be overridden to provide custom rules for processing document routing
     * 
     * @param document
     * @return
     */
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        return true;
    }

    // Document Validation Helper Methods
    /**
     * Performs common validation for Maintenance Document saves.
     * 
     * @param maintenanceDocument
     * @return boolean True if the document is valid for saving, false otherwise.
     */
    protected boolean isDocumentValidForSave(MaintenanceDocument maintenanceDocument) {
        if (null == maintenanceDocument) {
            return false;
        }

        boolean valid = true;

        // do common checks here

        return valid;
    }

    /**
     * This method performs common validation for Maintenance Document routes.
     * 
     * @param maintenanceDocument
     * @return boolean True if the document is valid for routing, false otherwise.
     */
    protected boolean isDocumentValidForRouting(MaintenanceDocument maintenanceDocument) {
        boolean success = true;

        success &= validateDocument((Document) maintenanceDocument);
        success &= validateMaintenanceDocument(maintenanceDocument);

        return success;
    }

    private boolean validateDocument(Document document) {
        boolean success = true;

        String documentHeaderId = document.getFinancialDocumentNumber();
        if (documentHeaderId == null) {
            GlobalVariables.getErrorMap().putError("documentHeaderId", KeyConstants.ERROR_REQUIRED);
            success = false;
        }

        return success;
    }


    private boolean validateMaintenanceDocument(MaintenanceDocument maintenanceDocument) {
        boolean success = true;

        GlobalVariables.getErrorMap().addToErrorPath("newMaintainableObject");
        Maintainable newMaintainable = maintenanceDocument.getNewMaintainableObject();
        if (newMaintainable == null) {
            GlobalVariables.getErrorMap().putError("", KeyConstants.ERROR_REQUIRED, "Account");
            success = false;
        }
        else {
            Account newAccount = (Account) newMaintainable.getBusinessObject();
            if (StringUtils.isBlank(newAccount.getAccountName())) {
                GlobalVariables.getErrorMap().putError("accountNumber", KeyConstants.ERROR_REQUIRED, "Account Number");
                success = false;
            }
        }
        GlobalVariables.getErrorMap().removeFromErrorPath("newMaintainableObject");

        if (maintenanceDocument.isOldBusinessObjectInDocument()) {
            GlobalVariables.getErrorMap().addToErrorPath("oldMaintainableObject");
            Maintainable oldMaintainable = maintenanceDocument.getOldMaintainableObject();
            if (oldMaintainable == null) {
                GlobalVariables.getErrorMap().putError("", KeyConstants.ERROR_REQUIRED, "Account");
                success = false;
            }
            else {
                Account oldAccount = (Account) oldMaintainable.getBusinessObject();
                if (StringUtils.isBlank(oldAccount.getAccountName())) {
                    GlobalVariables.getErrorMap().putError("accountNumber", KeyConstants.ERROR_REQUIRED, "Account Number");
                    success = false;
                }
            }
            GlobalVariables.getErrorMap().removeFromErrorPath("oldMaintainableObject");
        }

        return success;
    }

}
