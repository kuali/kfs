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

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.RiceConstants;
import org.kuali.core.rule.event.KualiDocumentEvent;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.KualiRuleService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.web.struts.action.KualiTransactionalDocumentActionBase;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.effort.bo.EffortCertificationDetail;
import org.kuali.module.effort.document.EffortCertificationDocument;
import org.kuali.module.effort.web.struts.form.EffortCertificationForm;

/**
 * This class handles Actions for EffortCertification.
 */
public class EffortCertificationAction extends KualiTransactionalDocumentActionBase {
    
    public ActionForward initiate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    
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
        String temp = (String) request.getAttribute(RiceConstants.METHOD_TO_CALL_ATTRIBUTE);
        int lineToRecalculateIndex = Integer.parseInt(StringUtils.substringBetween(temp, "recalculate.", ".x"));
        EffortCertificationForm effortForm = (EffortCertificationForm) form;
        EffortCertificationDocument effortDocument = (EffortCertificationDocument) effortForm.getDocument();
        List<EffortCertificationDetail> detailLines = effortDocument.getEffortCertificationDetailLines();
        EffortCertificationDetail lineToRecalculate = detailLines.get(lineToRecalculateIndex);

        lineToRecalculate.setEffortCertificationPayrollAmount(lineToRecalculate.getEffortCertificationOriginalPayrollAmount().multiply(new KualiDecimal(lineToRecalculate.getEffortCertificationUpdatedOverallPercent() * 100)));
        detailLines.remove(lineToRecalculateIndex);
        detailLines.add(lineToRecalculateIndex, lineToRecalculate);
        //effortDocument.setEffortCertificationDetailLines(detailLines);
        
        System.out.println("new value calculation = " + lineToRecalculate.getEffortCertificationOriginalPayrollAmount().multiply(new KualiDecimal(lineToRecalculate.getEffortCertificationUpdatedOverallPercent() * 100)));
        System.out.println("lineToRecalculate.getEffortCertificationPayrollAmount = " + lineToRecalculate.getEffortCertificationPayrollAmount());
        System.out.println("lineToRecalculate.getEffortCertificationCalculatedOverallPercent() = " + lineToRecalculate.getEffortCertificationCalculatedOverallPercent());
        System.out.println("lineToRecalculate.getEffortCertificationUpdatedOverallPercent() = " + lineToRecalculate.getEffortCertificationUpdatedOverallPercent());
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    
    public ActionForward add(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
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
        String temp = (String) request.getAttribute(RiceConstants.METHOD_TO_CALL_ATTRIBUTE);
        int lineToDeleteIndex = Integer.parseInt(StringUtils.substringBetween(temp, "delete.", ".x"));
        
        EffortCertificationForm effortForm = (EffortCertificationForm) form;
        EffortCertificationDocument effortDocument = (EffortCertificationDocument) effortForm.getDocument();
        List<EffortCertificationDetail> detailLines = effortDocument.getEffortCertificationDetailLines();
        
        detailLines.remove(lineToDeleteIndex);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
 
    /**
     * load the detail lines with the given information 
     */
    public ActionForward loadDetailLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        EffortCertificationForm effortCertificationForm = (EffortCertificationForm) form;
        EffortCertificationDocument effortCertificationDocument = (EffortCertificationDocument) effortCertificationForm.getDocument();
        
        //boolean isRulePassed = this.invokeRules(new LoadDetailLineEvent("", "", effortCertificationDocument));   

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }  
    
    /**
     * execute the rules associated with the given event
     * 
     * @param event the event that just occured
     * @return true if the rules associated with the given event pass; otherwise, false
     */
    private boolean invokeRules(KualiDocumentEvent event) {
        return SpringContext.getBean(KualiRuleService.class).applyRules(event);
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
        String temp = (String) request.getAttribute(RiceConstants.METHOD_TO_CALL_ATTRIBUTE);
        int lineToRevertIndex = Integer.parseInt(StringUtils.substringBetween(temp, "revert.", ".x"));
        
        EffortCertificationForm effortForm = (EffortCertificationForm) form;
        EffortCertificationDocument effortDocument = (EffortCertificationDocument) effortForm.getDocument();
        List<EffortCertificationDetail> detailLines = effortDocument.getEffortCertificationDetailLines();
        EffortCertificationDetail lineToRevert = detailLines.get(lineToRevertIndex);
        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        
        HashMap primaryKeys = new HashMap();
        primaryKeys.put("documentNumber", lineToRevert.getDocumentNumber());
        primaryKeys.put("chartOfAccountsCode", lineToRevert.getChartOfAccountsCode());
        primaryKeys.put("accountNumber", lineToRevert.getAccountNumber());
        /*primaryKeys.put("subAccountNumber", lineToRevert.getSubAccountNumber());*/
        primaryKeys.put("positionNumber", lineToRevert.getPositionNumber());
        primaryKeys.put("financialObjectCode", lineToRevert.getFinancialObjectCode());
        /*primaryKeys.put("sourceChartOfAccountsCode", lineToRevert.getSourceChartOfAccountsCode());
        primaryKeys.put("sourceAccountNumber", lineToRevert.getSubAccountNumber());*/
        
        EffortCertificationDetail revertedLine = (EffortCertificationDetail) businessObjectService.findByPrimaryKey(EffortCertificationDetail.class, primaryKeys);
        
        detailLines.remove(lineToRevertIndex);
        detailLines.add(lineToRevertIndex, revertedLine);
        
        System.out.println("revertedLine null ?" + (revertedLine == null));
        System.out.println("lineToRevert.documentNumber = " + lineToRevert.getDocumentNumber());
        System.out.println("lineToRevert.chartOfAccountsCode = " + lineToRevert.getChartOfAccountsCode());
        System.out.println("lineToRevert.accountNumber = " + lineToRevert.getAccountNumber());
        System.out.println("lineToRevert.subAccountNumber = " + lineToRevert.getSubAccountNumber());
        System.out.println("lineToRevert.positionNumber = " + lineToRevert.getPositionNumber());
        System.out.println("lineToRevert.financialObjectCode = " + lineToRevert.getFinancialObjectCode());
        System.out.println("lineToRevert.sourceChartOfAccountsCode = " + lineToRevert.getSourceChartOfAccountsCode());
        System.out.println("lineToRevert.sourceAccountNumber = " + lineToRevert.getSubAccountNumber());
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
}