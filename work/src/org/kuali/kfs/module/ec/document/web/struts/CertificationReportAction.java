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

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.RiceConstants;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.bo.AccountingLineOverride;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.effort.EffortConstants;
import org.kuali.module.effort.bo.EffortCertificationDetail;
import org.kuali.module.effort.bo.EffortCertificationDetailLineOverride;
import org.kuali.module.effort.document.EffortCertificationDocument;
import org.kuali.module.effort.rule.event.AddDetailLineEvent;
import org.kuali.module.effort.rules.EffortCertificationDocumentRuleUtil;
import org.kuali.module.effort.web.struts.form.CertificationReportForm;
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
        
        KualiDecimal newSalary = KualiDecimal.ZERO;
        KualiDecimal convertedPercent = (new KualiDecimal(lineToRecalculate.getEffortCertificationUpdatedOverallPercent()).divide(new KualiDecimal(100)));
        
        if (lineToRecalculate.isFederalOrFederalPassThroughIndicator()) {
            newSalary = convertedPercent.multiply(effortDocument.getSalaryOrigFederalTotal());
        } else {
            newSalary = convertedPercent.multiply(effortDocument.getSalaryOrigOtherTotal());
        }
        lineToRecalculate.setEffortCertificationPayrollAmount(newSalary);
        
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
        
        // TODO: should required fields be checked for null values?
        newDetailLine.refresh();
        newDetailLine.setPositionNumber(effortDocument.getDefaultPositionNumber());
        newDetailLine.setFinancialObjectCode(effortDocument.getDefaultObjectCode());
        newDetailLine.setNewLineIndicator(true);
        newDetailLine.setEffortCertificationOriginalPayrollAmount(KualiDecimal.ZERO);
        
        if(newDetailLine.isAccountExpiredOverride()) {
            this.updateDetailLineOverrideCode(newDetailLine);
        }
        
        // check business rules
        boolean isValid = this.invokeRules(new AddDetailLineEvent("", "newDetailLine", effortDocument, newDetailLine));
        if (isValid) {
            
            EffortCertificationDocumentRuleUtil.applyDefaultValues(newDetailLine);
            if (EffortCertificationDocumentRuleUtil.hasA21SubAccount(newDetailLine)) {
                EffortCertificationDocumentRuleUtil.updateSourceAccountInformation(newDetailLine);
            }
            detailLines.add(newDetailLine);
            effortForm.setNewDetailLine(new EffortCertificationDetail());
        } 
        else {
            EffortCertificationDetailLineOverride.processForOutput(newDetailLine);
        }         
        
        this.processDetailLineOverrides(detailLines);

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
    
    /**
     * Sorts the federal pass through detail lines for display
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward sortFed(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CertificationReportForm effortForm = (CertificationReportForm) form;
        
        effortForm.setSortColumnFed(getColumnToSortBy(mapping, form, request, response));
        effortForm.toggleFedSortOrder();
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC); 
    }
    
    /**
     * Sorts the non federal pass through lines for display
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward sortOther(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CertificationReportForm effortForm = (CertificationReportForm) form;
        
        effortForm.setSortColumnOther(getColumnToSortBy(mapping, form, request, response));
        effortForm.toggleOtherSortOrder();
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC); 
    }
    
    /**
     * Gets the correct column for sorting
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    private String getColumnToSortBy(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String columnToSortBy = "";
        String parameterName = (String) request.getAttribute(RiceConstants.METHOD_TO_CALL_ATTRIBUTE);
        if (StringUtils.isNotBlank(parameterName)) {
            columnToSortBy = StringUtils.substringBetween(parameterName, ".column", ".");
        }
        
        if (columnToSortBy.equals(EffortConstants.EFFORT_DOCUMENT_SORT_COLUMN_CHART)) return EffortConstants.EFFORT_DOCUMENT_SORT_COLUMN_CHART;
        else if (columnToSortBy.equals(EffortConstants.EFFORT_DOCUMENT_SORT_COLUMN_ACCOUNT) ) return EffortConstants.EFFORT_DOCUMENT_SORT_COLUMN_ACCOUNT;
        else if (columnToSortBy.equals(EffortConstants.EFFORT_DOCUMENT_SORT_COLUMN_SALARY) ) return EffortConstants.EFFORT_DOCUMENT_SORT_COLUMN_SALARY;
        
        return EffortConstants.EFFORT_DOCUMENT_DEFAULT_SORT_COLUMN;
    }
}