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
package org.kuali.module.purap.web.struts.action;

import java.io.ByteArrayOutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.Document;
import org.kuali.core.exceptions.DocumentInitiationAuthorizationException;
import org.kuali.core.exceptions.GroupNotFoundException;
import org.kuali.core.service.DocumentService;
import org.kuali.core.service.KualiGroupService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.web.struts.action.KualiAction;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.purap.PurapParameterConstants;
import org.kuali.module.purap.bo.PurchaseOrderVendorQuote;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.service.PurchaseOrderService;

/**
 * Struts Action for printing Purap documents outside of a document action
 */
public class PrintAction extends KualiAction {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PrintAction.class);

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // do security check
        // get current user and check to see if they 

        //get parameters
        String poDocNumber = request.getParameter("poDocNumber");
        Integer vendorQuoteId = new Integer(request.getParameter("vendorQuoteId"));
        if (StringUtils.isEmpty(poDocNumber) || StringUtils.isEmpty(poDocNumber)) {
            throw new RuntimeException();
        }
        // doc service - get this doc
        // get the vendor quote
        // call the print service
        PurchaseOrderDocument po = (PurchaseOrderDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(poDocNumber);
        UniversalUser curUser = GlobalVariables.getUserSession().getUniversalUser();
        if (!hasPrintAuthorization(po, curUser)) {
            throw new DocumentInitiationAuthorizationException(KFSKeyConstants.AUTHORIZATION_ERROR_DOCUMENT, new String[]{curUser.getPersonUserIdentifier(), "print", "Purchase Order"});
        }

        
        PurchaseOrderVendorQuote poVendorQuote = null;
        for (PurchaseOrderVendorQuote vendorQuote : po.getPurchaseOrderVendorQuotes()) {
            if (vendorQuote.getPurchaseOrderVendorQuoteIdentifier().equals(vendorQuoteId)) {
                poVendorQuote = vendorQuote;
                break;
            }
        }
        if (poVendorQuote==null) {
            throw new RuntimeException();
        }
        ByteArrayOutputStream baosPDF = new ByteArrayOutputStream();
        poVendorQuote.setTransmitPrintDisplayed(false);
        try {
            StringBuffer sbFilename = new StringBuffer();
            sbFilename.append("PURAP_PO_QUOTE_");
            sbFilename.append(po.getPurapDocumentIdentifier());
            sbFilename.append("_");
            sbFilename.append(System.currentTimeMillis());
            sbFilename.append(".pdf");

            boolean success = SpringContext.getBean(PurchaseOrderService.class).printPurchaseOrderQuotePDF(po, poVendorQuote, baosPDF);

            if (!success) {
                poVendorQuote.setTransmitPrintDisplayed(true);
                if (baosPDF != null) {
                    baosPDF.reset();
                }
                return mapping.findForward(KFSConstants.MAPPING_BASIC);
            }
            response.setHeader("Cache-Control", "max-age=30");
            response.setContentType("application/pdf");
            StringBuffer sbContentDispValue = new StringBuffer();
            // sbContentDispValue.append("inline");
            sbContentDispValue.append("attachment");
            sbContentDispValue.append("; filename=");
            sbContentDispValue.append(sbFilename);

            response.setHeader("Content-disposition", sbContentDispValue.toString());

            response.setContentLength(baosPDF.size());

            ServletOutputStream sos;

            sos = response.getOutputStream();

            baosPDF.writeTo(sos);

            sos.flush();

        }
        finally {
            if (baosPDF != null) {
                baosPDF.reset();
            }
        }

        return null;
    }
    
    private boolean hasPrintAuthorization(Document document, UniversalUser user) {
        String authorizedWorkgroup = SpringContext.getBean(ParameterService.class).getParameterValue(PurchaseOrderDocument.class, PurapParameterConstants.Workgroups.PURAP_DOCUMENT_PO_INITIATE_ACTION);
        try {
            return SpringContext.getBean(KualiGroupService.class).getByGroupName(authorizedWorkgroup).hasMember(user);
        }
        catch (GroupNotFoundException e) {
            throw new RuntimeException("Workgroup " + authorizedWorkgroup + " not found", e);
        }
    }

}