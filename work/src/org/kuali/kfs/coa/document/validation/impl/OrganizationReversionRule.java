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

import java.util.List;

import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.core.util.GlobalVariables;
import org.kuali.module.chart.bo.OrganizationReversion;
import org.kuali.module.chart.bo.OrganizationReversionDetail;

/**
 * 
 * This class implements the business rules specific to the Organization Reversion Maintenance Document.
 * 
 * @author jkeller
 * 
 */
public class OrganizationReversionRule extends MaintenanceDocumentRuleBase {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OrganizationReversionRule.class);

    OrganizationReversion oldOrgReversion;
    OrganizationReversion newOrgReversion;

    /**
     * 
     * No-Args Constructor for an OrganizationReversionRule.
     * 
     */
    public OrganizationReversionRule() {

    }

    /**
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     */
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {

        boolean success = true;

        // make sure its a valid ExtendedAttribute MaintenanceDocument
        if (!isCorrectMaintenanceClass(document, OrganizationReversion.class)) {
            throw new IllegalArgumentException("Maintenance Document passed in was of the incorrect type.  Expected " + "'" + OrganizationReversion.class.toString() + "', received " + "'" + document.getOldMaintainableObject().getBoClass().toString() + "'.");
        }

        // get the real business object
        newOrgReversion = (OrganizationReversion) document.getNewMaintainableObject().getBusinessObject();

        // add check to validate document recursively to get to the collection attributes
        success &= validateDetailBusinessObjects(newOrgReversion);

        return success;
    }

    /**
     * 
     * Tests each option attached to the main business object and validates its properties.
     * 
     * @param extendedAttribute
     * @return
     */
    private boolean validateDetailBusinessObjects(OrganizationReversion orgReversion) {
        GlobalVariables.getErrorMap().addToErrorPath("document.newMaintainableObject");
        List<OrganizationReversionDetail> details = orgReversion.getOrganizationReversionDetail();
        int index = 0;
        int originalErrorCount = GlobalVariables.getErrorMap().getErrorCount();
        for (OrganizationReversionDetail dtl : details) {
            String errorPath = "organizationReversionDetail[" + index + "]";
            GlobalVariables.getErrorMap().addToErrorPath(errorPath);
            getDictionaryValidationService().validateBusinessObject(dtl);
            GlobalVariables.getErrorMap().removeFromErrorPath(errorPath);
            index++;
        }
        GlobalVariables.getErrorMap().removeFromErrorPath("document.newMaintainableObject");
        return GlobalVariables.getErrorMap().getErrorCount() > originalErrorCount;
    }

    /*
     * TODO: Upgrade this to use the new AddLineRule/AddLineEvent system which will allow us to process each attribute as it is
     * added
     */

}
