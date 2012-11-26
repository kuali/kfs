/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.document.web.struts;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.businessobject.AccountingDocumentRelationship;
import org.kuali.kfs.module.tem.businessobject.TemSourceAccountingLine;
import org.kuali.kfs.module.tem.document.TravelDocumentBase;
import org.kuali.kfs.module.tem.document.service.AccountingDocumentRelationshipService;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;

/**
 * This class...
 */
public class TravelDisbursementVoucherAction extends org.kuali.kfs.fp.document.web.struts.DisbursementVoucherAction {


    @Override
    public ActionForward docHandler(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward forward = super.docHandler(mapping, form, request, response);
        TravelDisbursementVoucherForm travelDisbursementVoucherForm = (TravelDisbursementVoucherForm) form;
        if (travelDisbursementVoucherForm.getTemDocID() != null) {
            TravelDocumentBase document = (TravelDocumentBase) getDocumentService().getByDocumentHeaderId(travelDisbursementVoucherForm.getTemDocID().toString());
            DisbursementVoucherDocument disbursementVoucherDocument = (DisbursementVoucherDocument) travelDisbursementVoucherForm.getDocument();
            document.populateVendorPayment(disbursementVoucherDocument);
            if (document.getTemProfile() == null) {
                document.setProfileId(document.getTemProfileId());
            }
            if (document.getTemProfile() != null) {
                travelDisbursementVoucherForm.getNewSourceLine().setChartOfAccountsCode(document.getTemProfile().getDefaultChartCode());
                travelDisbursementVoucherForm.getNewSourceLine().setAccountNumber(document.getTemProfile().getDefaultAccount());

                SourceAccountingLine line = new SourceAccountingLine();
                disbursementVoucherDocument.addSourceAccountingLine(line);
            }
            if (document.getSourceAccountingLines() != null) {
                List<TemSourceAccountingLine> newList = new ArrayList<TemSourceAccountingLine>();
                int sequence = 1;
                for (TemSourceAccountingLine line : (List<TemSourceAccountingLine>)document.getSourceAccountingLines()){
                    if (!line.getCardType().equals(TemConstants.TRAVEL_TYPE_CTS)){
                        line.setSequenceNumber(new Integer(sequence));
                        sequence++;
                        newList.add(line);
                    }
                }
                disbursementVoucherDocument.setSourceAccountingLines(newList);
            }

            String relationDescription = document.getDocumentHeader().getWorkflowDocument().getDocumentTypeName() + " - DV";
            SpringContext.getBean(AccountingDocumentRelationshipService.class).save(new AccountingDocumentRelationship(document.getDocumentNumber(), travelDisbursementVoucherForm.getDocument().getDocumentNumber(), relationDescription));
        }
        return forward;
    }

}
