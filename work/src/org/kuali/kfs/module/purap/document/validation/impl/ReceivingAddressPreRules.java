/*
 * Copyright 2008 The Kuali Foundation
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

import org.kuali.kfs.coa.document.validation.impl.MaintenancePreRulesBase;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.businessobject.ReceivingAddress;
import org.kuali.kfs.module.purap.document.service.ReceivingAddressService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Business Prerules applicable to ReceivingAddressMaintenanceDocument. 
 * These prerules check whether the maintenance action to a Receiving Address business objects abide certain constraint rules.
 */
public class ReceivingAddressPreRules extends MaintenancePreRulesBase {

    /**
     * Checks whether the maintenance action to a Receiving Address business objects abide to the contraint that, 
     * there is one and only one active default receiving address for each chart or chart/org at any given time, 
     * if there exists at least one active receiving address for this chart or chart/org. 
     * If the contraint would be broken as a result of the update, the method will present an error or warning 
     * to the user; and if proceed, enforce the rule by updating the related receiving address as well.
     * Note: this method relies on the condition that the current status of the DB satisfies the constraints.
     * 
     * @see org.kuali.kfs.coa.document.validation.impl.MaintenancePreRulesBase#doCustomPreRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean doCustomPreRules( MaintenanceDocument document ) {                
        ReceivingAddress raOld = ( ReceivingAddress )document.getOldMaintainableObject().getBusinessObject();
        ReceivingAddress raNew = ( ReceivingAddress )document.getNewMaintainableObject().getBusinessObject();

        /* The fields that affect the rule are the default and active indicators. 
         * According to the create/copy/edit action, and various combinations of updates to these two fields, 
         * including unchanged (No->NO or Yes->Yes), set (No->Yes), unset (Yes->No), the rule checking will 
         * proceed respectively. The following boolean variables indicates the various updates and combinations.
         */
        boolean isNew = document.isNew();
        boolean isEdit = document.isEdit();
        boolean wasActive = isEdit && raOld.isActive();        
        boolean wasDefault = isEdit && raOld.isDefaultIndicator();
        boolean isActive = raNew.isActive();        
        boolean isDefault = raNew.isDefaultIndicator();
        boolean stayActive = wasActive && isActive;
        boolean stayDefault = wasDefault && isDefault; 
        boolean setActive = (isNew || !wasActive) && isActive;
        boolean unsetActive = wasActive && !isActive;
        boolean setDefault = (isNew || !wasDefault) && isDefault;
        boolean unsetDefault = wasDefault && !isDefault;        
        
        /* Check whether there're other active RA exist.
         * We only need to search within the same chart/org group, since we don't allow editting on chart/org.
         * However, we need to exclude the current address being edited if it is not a new one and was active.
         */ 
        /*
        Map criteria = new HashMap();
        criteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, raNew.getChartOfAccountsCode());
        criteria.put(KFSPropertyConstants.ORGANIZATION_CODE, raNew.getOrganizationCode());
        //criteria.put(PurapPropertyConstants.RCVNG_ADDR_DFLT_IND, true);        
        criteria.put(PurapPropertyConstants.RCVNG_ADDR_ACTIVE, true);        
        int count = SpringContext.getBean(BusinessObjectService.class).countMatching(ReceivingAddress.class, criteria);
        */
        int count = SpringContext.getBean(ReceivingAddressService.class).countActiveByChartOrg(raNew.getChartOfAccountsCode(),raNew.getOrganizationCode());      
        boolean existOther = wasActive ? (count>1) : (count>0);
            
        /* Case 1 - adding the first active address: 
         * Force it to be the default one.
         */ 
        if ( setActive && !isDefault && !existOther ) {
            raNew.setDefaultIndicator( true );
        }
        /* Case 2 - switching the default address from another one to this one: 
         * Give warning; if proceed, will unset the other default address in post-processing.
         */
        else if ( (stayActive && setDefault) || (setActive && isDefault && existOther) ) {
            if (!super.askOrAnalyzeYesNoQuestion(PurapConstants.CONFIRM_CHANGE_DFLT_RVNG_ADDR, PurapConstants.CONFIRM_CHANGE_DFLT_RVNG_ADDR_TXT) ) {
                abortRulesCheck();
            }
        }
        /* Case 3 - unsetting the default address that's still active:
         * Give error: Can't unset the default address; you must set another default address to replace this one. 
         */
        else if ( stayActive && unsetDefault ) {
            putFieldError(PurapPropertyConstants.RECEIVING_ADDRESS_DEFAULT_INDICATOR, PurapKeyConstants.ERROR_RCVNG_ADDR_UNSET_DFLT);
            abortRulesCheck();
            return false;
        }
        /* Case 4 - deactivating the default address while there're still other active ones:
         * Give error: Can't deactivate the default address when there're still other active ones;
         * you must set another default address first.
         */
        else if ( unsetActive && wasDefault && existOther ) {            
            putFieldError(PurapPropertyConstants.BO_ACTIVE, PurapKeyConstants.ERROR_RCVNG_ADDR_DEACTIVATE_DFLT);
            abortRulesCheck();
            return false;
        }         
        /* Other cases are harmless, i.e. won't break the constraint, so we can proceed without doing anything extra.
         */
        
        return true;
    }
 
    /**
     * Convenience method to add a property-specific error to the global errors list, with the correct prefix
     * added to the property name so that it will display correctly on maintenance documents.
     * 
     * @param propertyName Property name of the element that is associated with the error, to mark the field as errored in the UI.
     * @param errorConstant - Error Constant that can be mapped to a resource for the actual text message.
     * 
     */
    protected void putFieldError(String propertyName, String errorConstant) {
        GlobalVariables.getMessageMap().putErrorWithoutFullErrorPath(MaintenanceDocumentRuleBase.MAINTAINABLE_ERROR_PREFIX+propertyName, errorConstant);
    }
    
}
