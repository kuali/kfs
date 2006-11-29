/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/coa/document/validation/impl/ChartRuleBase.java,v $
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

        String documentHeaderId = document.getDocumentNumber();
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
