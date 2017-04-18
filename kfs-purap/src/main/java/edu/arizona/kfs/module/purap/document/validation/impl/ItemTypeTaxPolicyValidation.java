package edu.arizona.kfs.module.purap.document.validation.impl;

import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.util.GlobalVariables;

import edu.arizona.kfs.module.purap.PurapKeyConstants;
import edu.arizona.kfs.module.purap.PurapPropertyConstants;

public class ItemTypeTaxPolicyValidation extends org.kuali.kfs.module.purap.document.validation.impl.ItemTypeTaxPolicyValidation {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ItemTypeTaxPolicyValidation.class);
    
    
    @Override
    public boolean validate(AttributedDocumentEvent event) {
        boolean isValid = true;
        PurchasingAccountsPayableDocument purchasingDocument = (PurchasingAccountsPayableDocument) event.getDocument();

        boolean salesTaxInd = SpringContext.getBean(ParameterService.class).getParameterValueAsBoolean(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.ENABLE_SALES_TAX_IND);
        boolean useTaxIndicator = purchasingDocument.isUseTaxIndicator();
        
        //if sales tax or use tax is enabled, attempt tax check
        if((salesTaxInd || useTaxIndicator)) {
            
            GlobalVariables.getMessageMap().clearErrorPath();
            GlobalVariables.getMessageMap().addToErrorPath(PurapPropertyConstants.REQUISITION_ITEMS_ERROR_PATH);
            for ( PurApItem item : purchasingDocument.getItems() ) {
                if (((edu.arizona.kfs.module.purap.document.service.PurapService)purapService).isItemTypeConflictWithTaxPolicy(purchasingDocument, item)) {
                    String itemIdentifier = item.getItemIdentifierString();
                    
                    GlobalVariables.getMessageMap().putError(PurapPropertyConstants.FIELD_NAME_ITEM_TYPE_CODE, PurapKeyConstants.ERROR_INVALID_ITEM_TYPE_FOR_ACCOUNT_TAX_POLICY, itemIdentifier );
                    isValid &= false;
                }
            }
            GlobalVariables.getMessageMap().clearErrorPath();
        }
        
        return isValid;
    }
}
