package edu.arizona.kfs.gl.document;

import edu.arizona.kfs.gl.businessobject.GlobalTransactionEdit;
import edu.arizona.kfs.gl.businessobject.GlobalTransactionEditDetail;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemMaintainable;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.service.BusinessObjectService;

public class GlobalTransactionEditMaintainableImpl extends FinancialSystemMaintainable {
    protected BusinessObjectService businessObjectService;

    @Override
    public void doRouteStatusChange(DocumentHeader documentHeader) {
        super.doRouteStatusChange(documentHeader);
        WorkflowDocument workflowDocument = documentHeader.getWorkflowDocument();
        
        businessObjectService = SpringContext.getBean(BusinessObjectService.class);

        if (workflowDocument.isProcessed()) {
            GlobalTransactionEdit globalTransactionEdit = (GlobalTransactionEdit)getBusinessObject();

            //loop through and make sure the children state match the parent state
            for(GlobalTransactionEditDetail gteDetail : globalTransactionEdit.getGlobalTransactionEditDetails()) {
                gteDetail.setActive(globalTransactionEdit.isActive());
                businessObjectService.save(gteDetail);
            }
        }
    }

}
