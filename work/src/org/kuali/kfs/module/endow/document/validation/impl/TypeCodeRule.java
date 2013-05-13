/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.document.validation.impl;

import java.util.List;

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowKeyConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.AutomatedCashInvestmentModel;
import org.kuali.kfs.module.endow.businessobject.FeeMethod;
import org.kuali.kfs.module.endow.businessobject.TypeCode;
import org.kuali.kfs.module.endow.businessobject.TypeFeeMethod;
import org.kuali.kfs.module.endow.document.service.FeeMethodService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.rules.rule.event.ApproveDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;
import org.kuali.rice.krad.util.ObjectUtils;

public class TypeCodeRule extends MaintenanceDocumentRuleBase {
    
    /**
     * @see org.kuali.rice.kns.rules.MaintenanceDocumentRule#processRouteDocument(org.kuali.rice.krad.document.Document)
     */
    @Override
    public boolean processRouteDocument(Document document) {
       boolean isValid = true; 
       isValid &= super.processRouteDocument(document);
       MessageMap errorMap = GlobalVariables.getMessageMap();
       isValid &= errorMap.hasNoErrors();
       
       if (!isValid) {
           return isValid;
       }
    
       return isValid;
    }
    
    /**
     * @see org.kuali.rice.kns.rules.MaintenanceDocumentRule#processApproveDocument(ApproveDocumentEvent)
     */
    @Override
    public boolean processApproveDocument(ApproveDocumentEvent approveEvent) {
        boolean isValid = true; 
        isValid &= super.processApproveDocument(approveEvent);
        MessageMap errorMap = GlobalVariables.getMessageMap();
        isValid &= errorMap.hasNoErrors();
        
        if (!isValid) {
            return isValid;
        }
        
        Document document = (Document) approveEvent.getDocument();
        isValid &= validateIncomeACIModelID(document);
        
        return isValid;
    }    

    /**
     * This method will validate if default income ACI model ID
     * @param document
     * @return true if default income ACI model ID' ipIndicator is I then
     * default principal ACI model ID's ipIndicator is P else return false
     */
    private boolean validateIncomeACIModelID(Document document) {
        boolean rulesPassed = true;
        
        MaintenanceDocument maintenanceDocument = (MaintenanceDocument) document;
        TypeCode typeCode = (TypeCode) maintenanceDocument.getNewMaintainableObject().getBusinessObject();
        
        AutomatedCashInvestmentModel IncomeACIModelId = (AutomatedCashInvestmentModel) typeCode.getAutomatedCashInvestmentModelForIncomeACIModelId();
        String incomeIndicator = IncomeACIModelId.getIpIndicator();
        
        if (EndowConstants.TypeRestrictionPresetValueCodes.INCOME_TYPE_RESTRICTION_CODE.equalsIgnoreCase(incomeIndicator)) {
            AutomatedCashInvestmentModel principalACIModelID = (AutomatedCashInvestmentModel) typeCode.getAutomatedCashInvestmentModelForPrincipalACIModelId();
            String principalIndicator = principalACIModelID.getIpIndicator();
            if (!EndowConstants.TypeRestrictionPresetValueCodes.PERMANENT_TYPE_RESTRICTION_CODE.equalsIgnoreCase(principalIndicator)) {
                GlobalVariables.getMessageMap().putError(EndowPropertyConstants.TYPE_INCOME_ACI_MODEL_ID, EndowKeyConstants.TypeCodeConstants.ERROR_INVALID_PRINCIPAL_ACI_MODEL_ID);
                return false;
            }
        }
        
        return rulesPassed;
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processAddCollectionLineBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument,
     *      java.lang.String, org.kuali.rice.krad.bo.PersistableBusinessObject)
     */
    @Override
    public boolean processAddCollectionLineBusinessRules(MaintenanceDocument document, String collectionName, PersistableBusinessObject bo) {
        boolean isValid = true;
        isValid &= super.processCustomAddCollectionLineBusinessRules(document, collectionName, bo);
        MessageMap errorMap = GlobalVariables.getMessageMap();
        isValid &= errorMap.hasNoErrors();
        
        if (!isValid) {
            return isValid;
        }
        
        TypeCode typeCode = (TypeCode) document.getNewMaintainableObject().getBusinessObject();
        TypeFeeMethod typeFeeMethod = (TypeFeeMethod) bo;

        bo.refreshReferenceObject(EndowPropertyConstants.FEE_METHOD);
        
        if (isEmptyFeeMethodCode(bo)) {
            return false;
        }
        if (duplicateFeeMethodCodeEntered(typeCode, bo)) {
            return false;
        }
        if (!validateFeeMethodCode(bo)) {
            isValid = false;
        }
        
        return isValid;
    }
    
    /**
     * This method checks to make sure that fee method code is not empty
     * @param bo bo to be mapped to object typeFeeMethod
     * @return isValid is true if fee method code is empty else return false
     */
    private boolean isEmptyFeeMethodCode(PersistableBusinessObject bo) {
        boolean isValid = false;
        
        TypeFeeMethod typeFeeMethod = (TypeFeeMethod) bo;  
        
        String feeMethodCode = typeFeeMethod.getFeeMethodCode();
        if (ObjectUtils.isNull(feeMethodCode)) {
            GlobalVariables.getMessageMap().putError(EndowPropertyConstants.FEE_METHOD_CODE, EndowKeyConstants.TypeFeeMethodCodeConstants.ERROR_FEE_METHOD_CODE_CANNOT_BE_BLANK);
            return true;
        }
        
        return isValid;
    }
    
    /**
     * This method checks to make sure that fee method code exists in Fee Method table
     * @param bo bo to be mapped to object typeFeeMethod
     * @return isValid is true if fee method is empty else return false
     */
    private boolean validateFeeMethodCode(PersistableBusinessObject bo) {
        boolean isValid = true;
        
        TypeFeeMethod typeFeeMethod = (TypeFeeMethod) bo;  
        String feeMethodCode = typeFeeMethod.getFeeMethodCode();
        
        FeeMethodService feeMethodService = SpringContext.getBean(FeeMethodService.class);
        FeeMethod feeMethod = feeMethodService.getByPrimaryKey(feeMethodCode);
        
        if (ObjectUtils.isNull(feeMethod)) {
            GlobalVariables.getMessageMap().putError(EndowPropertyConstants.FEE_METHOD_CODE, EndowKeyConstants.TypeFeeMethodCodeConstants.ERROR_INVALID_FEE_METHOD_CODE_ENTERED);
            return false;
        }
        
        return isValid;
    }
    
    /**
     * This method checks to make sure that fee method code is not a duplicate in the collection list
     * Compare the entered fee method code on the line to the fee method codes in the list to make sure
     * it is not a duplicate.
     * @param typeCode , bo
     * @return isDuplicate is true if fee method code in the list else return false
     */
    private boolean duplicateFeeMethodCodeEntered(TypeCode typeCode, PersistableBusinessObject bo) {
        boolean isDuplicate = false ;
        
        TypeFeeMethod typeFeeMethod = (TypeFeeMethod) bo;  
        String feeMethodCode = typeFeeMethod.getFeeMethodCode();
        
        List<TypeFeeMethod> typeFeeMethods = (List<TypeFeeMethod>) typeCode.getTypeFeeMethods();
        
        for (TypeFeeMethod typeFeeMethodRecord : typeFeeMethods) {
            if (typeFeeMethodRecord.getFeeMethodCode().equalsIgnoreCase(feeMethodCode)) {
                isDuplicate = true;
                GlobalVariables.getMessageMap().putError(EndowPropertyConstants.FEE_METHOD_CODE, EndowKeyConstants.TypeFeeMethodCodeConstants.ERROR_DUPLICATE_FEE_METHOD_CODE_ENTERED) ;
            }
        }
        
        return isDuplicate;
    }
}
