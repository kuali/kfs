/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.module.effort.web.struts.action;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.chart.bo.SubAccount;
import org.kuali.module.effort.bo.EffortCertificationDetail;
import org.kuali.module.effort.document.EffortCertificationDocument;
import org.kuali.module.effort.rule.event.AddDetailLineEvent;
import org.kuali.module.effort.rules.EffortCertificationDocumentRuleUtil;
import org.kuali.module.effort.util.PayrollAmountHolder;
import org.kuali.module.effort.web.struts.form.EffortCertificationForm;

/**
 * This class handles Actions for EffortCertification document approval.
 */
public class CertificationReportAction extends EffortCertificationAction {

    /**
     * Recalculates the detail line
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward recalculate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        int lineToRecalculateIndex = getLineToDelete(request);
        EffortCertificationForm effortForm = (EffortCertificationForm) form;
        EffortCertificationDocument effortDocument = (EffortCertificationDocument) effortForm.getDocument();
        List<EffortCertificationDetail> detailLines = effortDocument.getEffortCertificationDetailLines();
        EffortCertificationDetail lineToRecalculate = detailLines.get(lineToRecalculateIndex);

        PayrollAmountHolder payrollAmountHolder = new PayrollAmountHolder(effortDocument.getTotalOriginalPayrollAmount(), new KualiDecimal(0), 0);
        payrollAmountHolder.setPayrollAmount(lineToRecalculate.getEffortCertificationPayrollAmount());
        PayrollAmountHolder.calculatePayrollPercent(payrollAmountHolder);
        lineToRecalculate.setEffortCertificationUpdatedOverallPercent(payrollAmountHolder.getPayrollPercent());

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Adds New Effort Certification Detail Lines
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward add(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        EffortCertificationForm effortForm = (EffortCertificationForm) form;
        EffortCertificationDocument effortDocument = (EffortCertificationDocument) effortForm.getDocument();
        List<EffortCertificationDetail> detailLines = effortDocument.getEffortCertificationDetailLines();
        EffortCertificationDetail newDetailLine = effortForm.getNewDetailLine();
        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        
        //TODO: need to set subaccount reference in order for rules validations to work (hasA21SubAccount in particular)
        HashMap primaryKeys = new HashMap();
        primaryKeys.put("chartOfAccountsCode", newDetailLine.getChartOfAccountsCode());
        primaryKeys.put("accountNumber", newDetailLine.getAccountNumber());
        primaryKeys.put("subAccountNumber", newDetailLine.getSubAccountNumber());
        newDetailLine.setSubAccount((SubAccount)businessObjectService.findByPrimaryKey(SubAccount.class, primaryKeys));
        
        // TODO: should required fields be checked for null values?
        newDetailLine.setPositionNumber(effortDocument.getDefaultPositionNumber());
        newDetailLine.setFinancialObjectCode(effortDocument.getDefaultObjectCode());
        newDetailLine.setNewLineIndicator(true);
        EffortCertificationDocumentRuleUtil.applyDefaultvalues(newDetailLine);
        if (EffortCertificationDocumentRuleUtil.hasA21SubAccount(newDetailLine)) {
            EffortCertificationDocumentRuleUtil.updateSourceAccountInformation(newDetailLine);
        }

        // check business rules
        boolean isValid = this.invokeRules(new AddDetailLineEvent("", "newDetailLine", effortDocument, effortForm.getNewDetailLine()));
        if (isValid) {
            detailLines.add(newDetailLine);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Deletes detail line
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward delete(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        int lineToDeleteIndex = getLineToDelete(request);
        EffortCertificationForm effortForm = (EffortCertificationForm) form;
        EffortCertificationDocument effortDocument = (EffortCertificationDocument) effortForm.getDocument();
        List<EffortCertificationDetail> detailLines = effortDocument.getEffortCertificationDetailLines();

        detailLines.remove(lineToDeleteIndex);


        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Reverts the detail line to the original values
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward revert(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        int lineToRevertIndex = getLineToDelete(request);
        EffortCertificationForm effortForm = (EffortCertificationForm) form;
        EffortCertificationDocument effortDocument = (EffortCertificationDocument) effortForm.getDocument();
        List<EffortCertificationDetail> detailLines = effortDocument.getEffortCertificationDetailLines();

        EffortCertificationDetail lineToRevert = detailLines.get(lineToRevertIndex);
        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        EffortCertificationDetail revertedLine = (EffortCertificationDetail) businessObjectService.retrieve(lineToRevert);

        detailLines.remove(lineToRevertIndex);
        detailLines.add(lineToRevertIndex, revertedLine);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * 
     * @see org.kuali.core.web.struts.action.KualiAction#refresh(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        EffortCertificationForm effortForm = (EffortCertificationForm) form;
        EffortCertificationDocument effortDocument = (EffortCertificationDocument) effortForm.getDocument();
        List<EffortCertificationDetail> detailLines = effortDocument.getEffortCertificationDetailLines();
        
        for (EffortCertificationDetail detailLine : detailLines) {
            detailLine.refresh();
        }
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * 
     * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        refresh(mapping, form, request, response);
        
        return super.execute(mapping, form, request, response);
        
    }
}