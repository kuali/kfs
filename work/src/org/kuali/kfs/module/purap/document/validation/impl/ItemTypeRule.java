/*
 * Copyright 2007-2008 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.purap.document.validation.impl;

import java.util.Collection;

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.businessobject.ItemType;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.module.purap.document.VendorCreditMemoDocument;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.kns.service.BusinessObjectDictionaryService;
import org.kuali.rice.kns.service.ParameterService;

/* 
 * 
*/
public class ItemTypeRule extends MaintenanceDocumentRuleBase {

    protected boolean processCustomApproveDocumentBusinessRules(MaintenanceDocument document) {
        LOG.info("processCustomApproveDocumentBusinessRules called");
        this.setupConvenienceObjects();
        boolean success = this.checkForSystemParametersExistence();
        return success && super.processCustomApproveDocumentBusinessRules(document);
    }

    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        LOG.info("processCustomRouteDocumentBusinessRules called");
        this.setupConvenienceObjects();
        boolean success = this.checkForSystemParametersExistence() && this.checkIndicators();
        return success && super.processCustomRouteDocumentBusinessRules(document);
    }

    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        LOG.info("processCustomSaveDocumentBusinessRules called");
        this.setupConvenienceObjects();
        boolean success = this.checkForSystemParametersExistence();
        return success && super.processCustomSaveDocumentBusinessRules(document);
    }

    protected boolean checkForSystemParametersExistence() {
        LOG.info("checkForSystemParametersExistence called");
        boolean success = true;
        Collection<String> cmAdditionalCharges = SpringContext.getBean(ParameterService.class).getParameterValues(VendorCreditMemoDocument.class, PurapConstants.BELOW_THE_LINES_PARAMETER);
        Collection<String> preqAdditionalCharges = SpringContext.getBean(ParameterService.class).getParameterValues(PaymentRequestDocument.class, PurapConstants.BELOW_THE_LINES_PARAMETER);
        Collection<String> poAdditionalCharges = SpringContext.getBean(ParameterService.class).getParameterValues(PurchaseOrderDocument.class, PurapConstants.BELOW_THE_LINES_PARAMETER);
        Collection<String> reqAdditionalCharges = SpringContext.getBean(ParameterService.class).getParameterValues(RequisitionDocument.class, PurapConstants.BELOW_THE_LINES_PARAMETER);
        Collection<String> defaultNonQuantityItemTypes = SpringContext.getBean(ParameterService.class).getParameterValues( KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.DEFAULT_NON_QUANTITY_ITEM_TYPE);
        Collection<String> defaultQuantityItemTypes = SpringContext.getBean(ParameterService.class).getParameterValues(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.DEFAULT_QUANTITY_ITEM_TYPE);
        Collection<String> cmAllowNegative = SpringContext.getBean(ParameterService.class).getParameterValues(VendorCreditMemoDocument.class, PurapConstants.ITEM_ALLOWS_NEGATIVE);
        Collection<String> preqAllowNegative = SpringContext.getBean(ParameterService.class).getParameterValues(PaymentRequestDocument.class, PurapConstants.ITEM_ALLOWS_NEGATIVE);
        Collection<String> poAllowNegative = SpringContext.getBean(ParameterService.class).getParameterValues(PurchaseOrderDocument.class, PurapConstants.ITEM_ALLOWS_NEGATIVE);
        Collection<String> reqAllowNegative = SpringContext.getBean(ParameterService.class).getParameterValues(RequisitionDocument.class, PurapConstants.ITEM_ALLOWS_NEGATIVE);
        Collection<String> cmAllowPositive = SpringContext.getBean(ParameterService.class).getParameterValues(VendorCreditMemoDocument.class, PurapConstants.ITEM_ALLOWS_POSITIVE);
        Collection<String> preqAllowPositive = SpringContext.getBean(ParameterService.class).getParameterValues(PaymentRequestDocument.class, PurapConstants.ITEM_ALLOWS_POSITIVE);
        Collection<String> poAllowPositive = SpringContext.getBean(ParameterService.class).getParameterValues(PurchaseOrderDocument.class, PurapConstants.ITEM_ALLOWS_POSITIVE);
        Collection<String> reqAllowPositive = SpringContext.getBean(ParameterService.class).getParameterValues(RequisitionDocument.class, PurapConstants.ITEM_ALLOWS_POSITIVE);
        Collection<String> cmAllowZero = SpringContext.getBean(ParameterService.class).getParameterValues(VendorCreditMemoDocument.class, PurapConstants.ITEM_ALLOWS_ZERO);
        Collection<String> preqAllowZero = SpringContext.getBean(ParameterService.class).getParameterValues(PaymentRequestDocument.class, PurapConstants.ITEM_ALLOWS_ZERO);
        Collection<String> poAllowZero = SpringContext.getBean(ParameterService.class).getParameterValues(PurchaseOrderDocument.class, PurapConstants.ITEM_ALLOWS_ZERO);
        Collection<String> reqAllowZero = SpringContext.getBean(ParameterService.class).getParameterValues(RequisitionDocument.class, PurapConstants.ITEM_ALLOWS_ZERO);
        Collection<String> cmRequireDescription = SpringContext.getBean(ParameterService.class).getParameterValues(VendorCreditMemoDocument.class, PurapConstants.ITEM_REQUIRES_USER_ENTERED_DESCRIPTION);
        Collection<String> preqRequireDescription = SpringContext.getBean(ParameterService.class).getParameterValues(PaymentRequestDocument.class, PurapConstants.ITEM_REQUIRES_USER_ENTERED_DESCRIPTION);
        Collection<String> poRequireDescription = SpringContext.getBean(ParameterService.class).getParameterValues(PurchaseOrderDocument.class, PurapConstants.ITEM_REQUIRES_USER_ENTERED_DESCRIPTION);
        Collection<String> reqRequireDescription = SpringContext.getBean(ParameterService.class).getParameterValues(RequisitionDocument.class, PurapConstants.ITEM_REQUIRES_USER_ENTERED_DESCRIPTION);
        Collection<String> preqRestrictingAccountEdit = SpringContext.getBean(ParameterService.class).getParameterValues(PaymentRequestDocument.class, PurapParameterConstants.PURAP_ITEM_TYPES_RESTRICTING_ACCOUNT_EDIT);
        Collection<String> poRestrictingAccountEdit = SpringContext.getBean(ParameterService.class).getParameterValues(PurchaseOrderDocument.class, PurapParameterConstants.PURAP_ITEM_TYPES_RESTRICTING_ACCOUNT_EDIT);
        Collection<String> reqRestrictingAccountEdit = SpringContext.getBean(ParameterService.class).getParameterValues(RequisitionDocument.class, PurapParameterConstants.PURAP_ITEM_TYPES_RESTRICTING_ACCOUNT_EDIT);
        
        ItemType newBo = (ItemType)getNewBo();
        ItemType oldBo= (ItemType)getOldBo();
       
        if ((!newBo.isActive() && oldBo.isActive()) &&
             (cmAdditionalCharges.contains(newBo.getItemTypeCode()) || 
              preqAdditionalCharges.contains(newBo.getItemTypeCode()) ||
              poAdditionalCharges.contains(newBo.getItemTypeCode()) ||
              reqAdditionalCharges.contains(newBo.getItemTypeCode()) ||
              defaultNonQuantityItemTypes.contains(newBo.getItemTypeCode()) ||
              defaultQuantityItemTypes.contains(newBo.getItemTypeCode()) ||
              cmAllowNegative.contains(newBo.getItemTypeCode()) ||
              preqAllowNegative.contains(newBo.getItemTypeCode()) ||
              poAllowNegative.contains(newBo.getItemTypeCode()) ||
              reqAllowNegative.contains(newBo.getItemTypeCode()) ||
              cmAllowPositive.contains(newBo.getItemTypeCode()) ||
              preqAllowPositive.contains(newBo.getItemTypeCode()) ||
              poAllowPositive.contains(newBo.getItemTypeCode()) ||
              reqAllowPositive.contains(newBo.getItemTypeCode()) ||
              cmAllowZero.contains(newBo.getItemTypeCode()) ||
              preqAllowZero.contains(newBo.getItemTypeCode()) ||
              poAllowZero.contains(newBo.getItemTypeCode()) ||
              reqAllowZero.contains(newBo.getItemTypeCode()) ||
              cmRequireDescription.contains(newBo.getItemTypeCode()) ||
              preqRequireDescription.contains(newBo.getItemTypeCode()) ||
              poRequireDescription.contains(newBo.getItemTypeCode()) ||
              reqRequireDescription.contains(newBo.getItemTypeCode()) ||
              preqRestrictingAccountEdit.contains(newBo.getItemTypeCode()) ||
              poRestrictingAccountEdit.contains(newBo.getItemTypeCode()) ||
              reqRestrictingAccountEdit.contains(newBo.getItemTypeCode()) )
            ) {
            success = false;
            String documentLabel = SpringContext.getBean(BusinessObjectDictionaryService.class).getMaintainableLabel(newBo.getClass()); 
            putGlobalError(KFSKeyConstants.ERROR_CANNOT_INACTIVATE_USED_IN_SYSTEM_PARAMETERS, documentLabel);
        }
        
        return success;
    }
    
    protected boolean checkIndicators() {
        
        boolean checkResult = true;
        ItemType newBo = (ItemType)getNewBo();
                
        // Both Quantity Based General Ledger Indicator and Additional Charge Indicator cannot be Yes at the same time
        if (newBo.isActive() && newBo.isAdditionalChargeIndicator() && newBo.isQuantityBasedGeneralLedgerIndicator()) {
            putFieldError(PurapPropertyConstants.ITEM_TYPE_QUANTITY_BASED, PurapKeyConstants.ERROR_ITEM_TYPE_QUANTITY_BASED_NOT_ALLOWED_WITH_ADDITIONAL_CHARGE);
            checkResult = false;
        }
        
        return checkResult;
    }
}
