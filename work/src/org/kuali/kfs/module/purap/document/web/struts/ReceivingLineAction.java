/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.module.purap.web.struts.action;

import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.util.UrlFactory;
import org.kuali.core.web.struts.form.KualiDocumentFormBase;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.document.ReceivingLineDocument;
import org.kuali.module.purap.service.PurchaseOrderService;
import org.kuali.module.purap.service.ReceivingService;
import org.kuali.module.purap.service.impl.ReceivingServiceImpl;
import org.kuali.module.purap.web.struts.form.PurchaseOrderForm;
import org.kuali.module.purap.web.struts.form.ReceivingLineForm;

import edu.iu.uis.eden.exception.WorkflowException;

public class ReceivingLineAction extends ReceivingBaseAction {

    protected void createDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {       
        
        super.createDocument(kualiDocumentFormBase);
                                                  
        ReceivingLineForm rlf = (ReceivingLineForm)kualiDocumentFormBase;
        ReceivingLineDocument rlDoc = (ReceivingLineDocument)rlf.getDocument();
        String poDocId = rlf.getPurchaseOrderDocId();
        
        ReceivingService receivingService = new ReceivingServiceImpl();
        
        //populate Receiving Line Document from Purchase Order
        SpringContext.getBean(ReceivingService.class).populateReceivingLine(rlDoc, poDocId);
        
    }

    public ActionForward createReceivingCorrection(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ReceivingLineForm rlForm = (ReceivingLineForm) form;
        ReceivingLineDocument document = (ReceivingLineDocument) rlForm.getDocument();        
        
        String basePath = getBasePath(request);
        String methodToCallDocHandler = "docHandler";
        String methodToCallReceivingCorrection = "initiate";
                        
        //set parameters
        Properties parameters = new Properties();
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, methodToCallDocHandler);
        parameters.put(KFSConstants.PARAMETER_COMMAND, methodToCallReceivingCorrection);
        parameters.put(KFSConstants.DOCUMENT_TYPE_NAME, "ReceivingCorrectionDocument");        
        parameters.put("receivingLineDocId", document.getDocumentNumber() );
        
        //create url
        String receivingCorrectionUrl = UrlFactory.parameterizeUrl(basePath + "/" + "purapReceivingCorrection.do", parameters);
        
        //create forward
        ActionForward forward = new ActionForward(receivingCorrectionUrl, true);
        
        return forward;
        
    }

}
