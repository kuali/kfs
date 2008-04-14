/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.labor.web.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.KFSConstants;
import org.kuali.module.chart.bo.codes.BalanceTyp;
import org.kuali.module.financial.web.struts.form.JournalVoucherForm;
import org.kuali.module.labor.LaborConstants;
import org.kuali.module.labor.bo.LaborLedgerPendingEntry;
import org.kuali.module.labor.bo.PositionData;

/**
 * Struts Action Form for the Labor Ledger Journal Voucher. This class piggy backs on all of the functionality in the
 * KualiTransactionalDocumentActionBase but is necessary for this document type. The Journal Voucher is unique in that it defines
 * several fields that aren't typically used by the other financial transaction processing eDocs (i.e. external system fields,
 * object type override, credit and debit amounts).
 */
public class JournalVoucherAction extends org.kuali.module.financial.web.struts.action.JournalVoucherAction {

    /**
     * @see org.kuali.core.web.struts.action.KualiAction#performLookup(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward performLookup(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        // parse out the business object name from our methodToCall parameter
        String fullParameter = (String) request.getAttribute(KFSConstants.METHOD_TO_CALL_ATTRIBUTE);
        String boClassName = StringUtils.substringBetween(fullParameter, KFSConstants.METHOD_TO_CALL_BOPARM_LEFT_DEL, KFSConstants.METHOD_TO_CALL_BOPARM_RIGHT_DEL);

        if (StringUtils.equals(boClassName, LaborLedgerPendingEntry.class.getName())) {
            String path = super.performLookup(mapping, form, request, response).getPath();
            path = path.replaceFirst(KFSConstants.LOOKUP_ACTION, LaborConstants.LONG_ROW_TABLE_INRUIRY_ACTION);
            return new ActionForward(path, true);
        }
        else if (StringUtils.equals(boClassName, PositionData.class.getName())) {
            String path = super.performLookup(mapping, form, request, response).getPath();
            path = path.replaceFirst(KFSConstants.LOOKUP_ACTION, KFSConstants.GL_MODIFIED_INQUIRY_ACTION);
            return new ActionForward(path, true);
        }
        else {
            return super.performLookup(mapping, form, request, response);
        }
    }

    /**
     * Labor JV allows reference fields on all encumbrance types. So only want to give message if a change is being made from a
     * encumbrance balance type to a nor (or vice-versa).
     * 
     * @see org.kuali.module.financial.web.struts.action.JournalVoucherAction#determineBalanceTypeEncumbranceChangeMode(org.kuali.module.financial.web.struts.form.JournalVoucherForm)
     */
    @Override
    protected int determineBalanceTypeEncumbranceChangeMode(JournalVoucherForm journalVoucherForm) throws Exception {
        int balanceTypeExternalEncumbranceChangeMode = NO_MODE_CHANGE;

        // retrieve fully populated balance type instances
        BalanceTyp origBalType = getPopulatedBalanceTypeInstance(journalVoucherForm.getOriginalBalanceType());
        BalanceTyp newBalType = getPopulatedBalanceTypeInstance(journalVoucherForm.getSelectedBalanceType().getCode());

        // then deal with external encumbrance changes
        if (origBalType.isFinBalanceTypeEncumIndicator() && !newBalType.isFinBalanceTypeEncumIndicator()) {
            balanceTypeExternalEncumbranceChangeMode = EXT_ENCUMB_TO_NON_EXT_ENCUMB;
        }
        else if (!origBalType.isFinBalanceTypeEncumIndicator() && newBalType.isFinBalanceTypeEncumIndicator()) {
            balanceTypeExternalEncumbranceChangeMode = NON_EXT_ENCUMB_TO_EXT_ENCUMB;
        }

        return balanceTypeExternalEncumbranceChangeMode;
    }

}
