/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.purap.rules;

import static org.kuali.kfs.KFSConstants.GL_DEBIT_CODE;

import java.util.List;

import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.UniversalUserService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.PurapParameterConstants;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.PurapConstants.PurapDocTypeCodes;
import org.kuali.module.purap.bo.PurchaseOrderItem;
import org.kuali.module.purap.bo.PurApItem;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.module.purap.service.PurapGeneralLedgerService;


public class PurchaseOrderAmendmentDocumentRule extends PurchaseOrderDocumentRule {

    @Override
    public boolean processValidation(PurchasingAccountsPayableDocument purapDocument) {
        boolean valid = super.processValidation(purapDocument);
        // Check that the user is in purchasing workgroup.
        String initiatorNetworkId = purapDocument.getDocumentHeader().getWorkflowDocument().getInitiatorNetworkId();
        UniversalUserService uus = SpringContext.getBean(UniversalUserService.class);
        UniversalUser user = null;
        try {
            user = uus.getUniversalUserByAuthenticationUserId(initiatorNetworkId);
            String purchasingGroup = SpringContext.getBean(KualiConfigurationService.class).getParameterValue(KFSConstants.PURAP_NAMESPACE, KFSConstants.Components.DOCUMENT, PurapParameterConstants.Workgroups.WORKGROUP_PURCHASING);
            if (!uus.isMember(user, purchasingGroup)) {
                valid = false;
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURAP_DOC_ID , KFSKeyConstants.AUTHORIZATION_ERROR_DOCUMENT, initiatorNetworkId, "amend", PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_DOCUMENT);
            }
        }
        catch (UserNotFoundException ue) {
            valid = false;
        }
        valid &= validateContainsAtLeastOneActiveItem(purapDocument);
        return valid;
    }
    
    private boolean validateContainsAtLeastOneActiveItem(PurchasingAccountsPayableDocument purapDocument) {
        List<PurApItem> items = purapDocument.getItems();
        for (PurApItem item : items) {
            if (((PurchaseOrderItem)item).isItemActiveIndicator() && (!((PurchaseOrderItem)item).isEmpty() && item.getItemType().isItemTypeAboveTheLineIndicator())) {
                return true;
            }
        }
        String documentType = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getDocumentEntry(purapDocument.getDocumentHeader().getWorkflowDocument().getDocumentType()).getLabel();
        
        GlobalVariables.getErrorMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_ITEM_REQUIRED, documentType);
        return false;
    }

    @Override
    protected void customizeExplicitGeneralLedgerPendingEntry(AccountingDocument accountingDocument, AccountingLine accountingLine, GeneralLedgerPendingEntry explicitEntry) {
        super.customizeExplicitGeneralLedgerPendingEntry(accountingDocument, accountingLine, explicitEntry);
        PurchaseOrderDocument po = (PurchaseOrderDocument)accountingDocument;

        SpringContext.getBean(PurapGeneralLedgerService.class).customizeGeneralLedgerPendingEntry(po, 
                accountingLine, explicitEntry, po.getPurapDocumentIdentifier(), GL_DEBIT_CODE, PurapDocTypeCodes.PO_DOCUMENT, true);

        explicitEntry.setFinancialDocumentTypeCode(PurapDocTypeCodes.PO_AMENDMENT_DOCUMENT);  //don't think i should have to override this, but default isn't getting the right PO doc
    }

}
