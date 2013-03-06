/*
 * Copyright 2006-2008 The Kuali Foundation
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
package org.kuali.kfs.module.purap.document.web.struts;

import java.io.ByteArrayOutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderVendorQuote;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.authorization.DocumentInitiationException;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.document.authorization.DocumentAuthorizer;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.kns.web.struts.action.KualiAction;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Struts Action for printing Purap documents outside of a document action
 */
public class PrintAction extends KualiAction {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PrintAction.class);

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        //get parameters
        String poDocNumber = request.getParameter("poDocNumber");
        Integer vendorQuoteId = new Integer(request.getParameter("vendorQuoteId"));
        if (StringUtils.isEmpty(poDocNumber) || StringUtils.isEmpty(poDocNumber)) {
            throw new RuntimeException();
        }

        // doc service - get this doc
        PurchaseOrderDocument po = (PurchaseOrderDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(poDocNumber);
        DocumentAuthorizer documentAuthorizer = SpringContext.getBean(DocumentHelperService.class).getDocumentAuthorizer(po);

        if (!(documentAuthorizer.canInitiate(KFSConstants.FinancialDocumentTypeCodes.PURCHASE_ORDER, GlobalVariables.getUserSession().getPerson()) ||
                documentAuthorizer.canInitiate(KFSConstants.FinancialDocumentTypeCodes.CONTRACT_MANAGER_ASSIGNMENT, GlobalVariables.getUserSession().getPerson()))) {
            throw new DocumentInitiationException(KFSKeyConstants.AUTHORIZATION_ERROR_DOCUMENT, new String[]{GlobalVariables.getUserSession().getPerson().getPrincipalName(), "print", "Purchase Order"});
        }

        // get the vendor quote
        PurchaseOrderVendorQuote poVendorQuote = null;
        for (PurchaseOrderVendorQuote vendorQuote : po.getPurchaseOrderVendorQuotes()) {
            if (vendorQuote.getPurchaseOrderVendorQuoteIdentifier().equals(vendorQuoteId)) {
                poVendorQuote = vendorQuote;
                break;
            }
        }

        if (poVendorQuote == null) {
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

            // call the print service
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

}

