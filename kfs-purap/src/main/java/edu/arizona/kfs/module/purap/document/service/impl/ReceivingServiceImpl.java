package edu.arizona.kfs.module.purap.document.service.impl;

import java.util.List;

import org.joda.time.DateTime;
import org.kuali.kfs.module.purap.businessobject.LineItemReceivingView;
import org.kuali.kfs.module.purap.document.LineItemReceivingDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

public class ReceivingServiceImpl extends org.kuali.kfs.module.purap.document.service.impl.ReceivingServiceImpl {
	
    @Override
    public String getReceivingDeliveryCampusCode(PurchaseOrderDocument po) {
        String deliveryCampusCode = "";
        String latestDocumentNumber = "";

        List<LineItemReceivingView> rViews = null;
        WorkflowDocument workflowDocument = null;
        DateTime latestCreateDate = null;

        //get related views
        if(ObjectUtils.isNotNull(po.getRelatedViews()) ){
            rViews = po.getRelatedViews().getRelatedLineItemReceivingViews();
        }

        //if not empty, then grab the latest receiving view
        if(ObjectUtils.isNotNull(rViews) && rViews.isEmpty() == false){

            for(LineItemReceivingView rView : rViews){
            	boolean internalUserSession = false;
                try{
                	// allow method to run without a user session so it can be used
                    // by workflow processes
                	if (GlobalVariables.getUserSession() == null) {
                        internalUserSession = true;
                        GlobalVariables.setUserSession(new UserSession(KFSConstants.SYSTEM_USER));
                        GlobalVariables.clear();
                    }
                    workflowDocument = workflowDocumentService.loadWorkflowDocument(rView.getDocumentNumber(), GlobalVariables.getUserSession().getPerson());

                    //if latest create date is null or the latest is before the current, current is newer
                    if( ObjectUtils.isNull(latestCreateDate) || latestCreateDate.isBefore(workflowDocument.getDateCreated()) ){
                        latestCreateDate = workflowDocument.getDateCreated();
                        latestDocumentNumber = workflowDocument.getDocumentId().toString();
                    }
                }catch(WorkflowException we){
                    throw new RuntimeException(we);
                } finally {
                    // if a user session was established for this call, clear it out
                    if (internalUserSession) {
                        GlobalVariables.clear();
                        GlobalVariables.setUserSession(null);
                    }
                }
            }

            //if there is a create date, a latest workflow doc was found
            if( ObjectUtils.isNotNull(latestCreateDate)){
                try{
                    LineItemReceivingDocument rlDoc = (LineItemReceivingDocument)documentService.getByDocumentHeaderId(latestDocumentNumber);
                    deliveryCampusCode = rlDoc.getDeliveryCampusCode();
                }catch(WorkflowException we){
                    throw new RuntimeException(we);
                }
            }
        }

        return deliveryCampusCode;
    }
}