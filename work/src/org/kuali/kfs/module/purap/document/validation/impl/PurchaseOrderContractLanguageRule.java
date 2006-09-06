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
package org.kuali.module.purap.rules;

import java.util.HashMap;
import java.util.Map;

import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.bo.PurchaseOrderContractLanguage;

public class PurchaseOrderContractLanguageRule extends MaintenanceDocumentRuleBase {
    
    private PurchaseOrderContractLanguage newContractLanguage;
    private BusinessObjectService boService;

    /**
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#setupConvenienceObjects()
     */
    @Override
    public void setupConvenienceObjects() {
        // setup newDelegateChange convenience objects, make sure all possible sub-objects are populated
        newContractLanguage = (PurchaseOrderContractLanguage) super.getNewBo();
        boService = (BusinessObjectService) super.getBoService();
        super.setupConvenienceObjects();
    }
    
    protected boolean processCustomApproveDocumentBusinessRules(MaintenanceDocument document) {
        LOG.info("processCustomApproveDocumentBusinessRules called");
        this.setupConvenienceObjects();
        boolean success = this.checkForDuplicate();
        return success && super.processCustomApproveDocumentBusinessRules(document);
    }

    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        LOG.info("processCustomRouteDocumentBusinessRules called");
        this.setupConvenienceObjects();
        boolean success = this.checkForDuplicate();
        return success && super.processCustomRouteDocumentBusinessRules(document);
    }

    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        LOG.info("processCustomSaveDocumentBusinessRules called");
        this.setupConvenienceObjects();
        boolean success = this.checkForDuplicate();
        return success && super.processCustomSaveDocumentBusinessRules(document);
    }
    
    protected boolean checkForDuplicate() {
        LOG.info("checkForDuplicate called");
        boolean success = true;
        Map fieldValues = new HashMap();
        fieldValues.put("campusCode", newContractLanguage.getCampusCode());
        fieldValues.put("purchaseOrderContractLanguageDescription", newContractLanguage.getPurchaseOrderContractLanguageDescription());
        fieldValues.put("contractLanguageCreateDate", newContractLanguage.getContractLanguageCreateDate());
        if (boService.countMatching(newContractLanguage.getClass(), fieldValues) != 0) {
            success &= false;
            putGlobalError(PurapKeyConstants.PURAP_GENERAL_POTENTIAL_DUPLICATE);
        }
        return success;
    }
}
