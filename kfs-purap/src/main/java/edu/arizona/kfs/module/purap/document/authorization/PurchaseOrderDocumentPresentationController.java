package edu.arizona.kfs.module.purap.document.authorization;

import java.util.Set;

import org.kuali.kfs.module.purap.PurapAuthorizationConstants.PurchaseOrderEditMode;
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;

public class PurchaseOrderDocumentPresentationController extends org.kuali.kfs.module.purap.document.authorization.PurchaseOrderDocumentPresentationController {
    
    @Override
    public Set<String> getEditModes(Document document) {
        Set<String> editModes = super.getEditModes(document);
        
        //make clear tax button always available like REQS
        boolean salesTaxInd = SpringContext.getBean(ParameterService.class).getParameterValueAsBoolean(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.ENABLE_SALES_TAX_IND);
        if (salesTaxInd) {
            editModes.add(PurchaseOrderEditMode.CLEAR_ALL_TAXES);
        }
        
        return editModes;
    }
}