/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.fp.document.web.struts;

import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.fp.businessobject.AdvanceDepositDetail;
import org.kuali.kfs.fp.document.AdvanceDepositDocument;
import org.kuali.kfs.fp.document.validation.impl.AdvanceDepositDocumentRuleUtil;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * This is the action class for the Advance Deposit document.
 */
public class AdvanceDepositAction extends CapitalAccountingLinesActionBase {

    /**
     * Adds handling for advance deposit detail amount updates.
     * 
     * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        AdvanceDepositForm adForm = (AdvanceDepositForm) form;

        if ( adForm != null && adForm.hasDocumentId()) {
            AdvanceDepositDocument adDoc = adForm.getAdvanceDepositDocument();

            adDoc.setTotalAdvanceDepositAmount(calculateAdvanceDepositTotal(adDoc)); // recalc b/c changes to the amounts could
            // have happened
        }

        // proceed as usual
        return super.execute(mapping, form, request, response);
    }

    /* NOTE
     * The following method was originally added to fix the NPE caused by the reference to AdvanceDepositDocument inside AdvanceDepositDetail. 
     * Now it's commented out as we are fixing the NPE in a better way, by removing the nested reference to AdvanceDepositDocument, 
     * which is not used anymore.
     */
    /**
     * Overridden to ensure that the nested AdvanceDepositDocuments in AdvanceDepositDetails are populated.
     * @see org.kuali.rice.kns.web.struts.action.KualiTransactionalDocumentActionBase#copy(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     *
    @Override
    public ActionForward copy(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        AdvanceDepositForm adForm = (AdvanceDepositForm) form;
        AdvanceDepositDocument adDoc = adForm.getAdvanceDepositDocument();
        List<AdvanceDepositDetail> advanceDeposits = adDoc.getAdvanceDeposits();
        
        for (AdvanceDepositDetail advanceDeposit: advanceDeposits) {
            /* somehow ObjectUtils.isNull(nestAdDoc) returns false here
            AdvanceDepositDocument nestAdDoc = advanceDeposit.getAdvanceDepositDocument();
            if (ObjectUtils.isNull(nestAdDoc)) {
                nestAdDoc = null;
            }
            *
            // need to populate the referred document, otherwise there will be NPE when copying
            advanceDeposit.setAdvanceDepositDocument(adDoc);
        }
        return super.copy(mapping, form, request, response);
    }
    */
    
    /**
     * Adds a AdvanceDepositDetail instance created from the current "new advanceDeposit" line to the document
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward addAdvanceDeposit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        AdvanceDepositForm adForm = (AdvanceDepositForm) form;
        AdvanceDepositDocument adDoc = adForm.getAdvanceDepositDocument();

        AdvanceDepositDetail newAdvanceDeposit = adForm.getNewAdvanceDeposit();
        adDoc.prepareNewAdvanceDeposit(newAdvanceDeposit);

        // advanceDeposit business rules
        boolean rulePassed = validateNewAdvanceDeposit(newAdvanceDeposit);
        if (rulePassed) {
            // add advanceDeposit
            adDoc.addAdvanceDeposit(newAdvanceDeposit);

            // clear the used advanceDeposit
            AdvanceDepositDetail advanceDepositDetail = new AdvanceDepositDetail();
            advanceDepositDetail.setDefaultBankCode();
            adForm.setNewAdvanceDeposit(advanceDepositDetail);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Deletes the selected advanceDeposit (line) from the document
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward deleteAdvanceDeposit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        AdvanceDepositForm adForm = (AdvanceDepositForm) form;
        AdvanceDepositDocument adDoc = adForm.getAdvanceDepositDocument();

        int deleteIndex = getLineToDelete(request);
        // delete advanceDeposit
        adDoc.removeAdvanceDeposit(deleteIndex);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This method validates a new advance deposit detail record.
     * 
     * @param advanceDeposit
     * @return boolean
     */
    protected boolean validateNewAdvanceDeposit(AdvanceDepositDetail advanceDeposit) {
        GlobalVariables.getMessageMap().addToErrorPath(KFSPropertyConstants.NEW_ADVANCE_DEPOSIT);
        boolean isValid = AdvanceDepositDocumentRuleUtil.validateAdvanceDeposit(advanceDeposit);
        GlobalVariables.getMessageMap().removeFromErrorPath(KFSPropertyConstants.NEW_ADVANCE_DEPOSIT);
        return isValid;
    }

    /**
     * Recalculates the advance deposit total since user could have changed it during their update.
     * 
     * @param advanceDepositDocument
     */
    protected KualiDecimal calculateAdvanceDepositTotal(AdvanceDepositDocument advanceDepositDocument) {
        KualiDecimal total = KualiDecimal.ZERO;
        Iterator<AdvanceDepositDetail> deposits = advanceDepositDocument.getAdvanceDeposits().iterator();
        while (deposits.hasNext()) {
            AdvanceDepositDetail deposit = deposits.next();
            total = total.add(deposit.getFinancialDocumentAdvanceDepositAmount());
        }
        return total;
    }
}
