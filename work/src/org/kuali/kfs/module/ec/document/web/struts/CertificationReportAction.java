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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.document.Document;
import org.kuali.core.document.authorization.DocumentAuthorizer;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.web.struts.form.KualiDocumentFormBase;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.util.DynamicCollectionComparator;
import org.kuali.kfs.util.ObjectUtil;
import org.kuali.kfs.util.DynamicCollectionComparator.SortOrder;
import org.kuali.module.effort.EffortConstants;
import org.kuali.module.effort.EffortPropertyConstants;
import org.kuali.module.effort.EffortConstants.EffortCertificationEditMode;
import org.kuali.module.effort.bo.EffortCertificationDetail;
import org.kuali.module.effort.bo.EffortCertificationDetailLineOverride;
import org.kuali.module.effort.document.EffortCertificationDocument;
import org.kuali.module.effort.rule.event.AddDetailLineEvent;
import org.kuali.module.effort.rules.EffortCertificationDocumentRuleUtil;
import org.kuali.module.effort.util.DetailLineGroup;
import org.kuali.module.effort.web.struts.form.CertificationReportForm;
import org.kuali.module.effort.web.struts.form.EffortCertificationForm;
import org.kuali.rice.KNSServiceLocator;
import org.kuali.rice.kns.util.KNSConstants;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * This class handles Actions for EffortCertification document approval.
 */
public class CertificationReportAction extends EffortCertificationAction {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CertificationReportAction.class);

    /**
     * recalculate the detail line
     */
    public ActionForward recalculate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        int lineToRecalculateIndex = getLineToDelete(request);

        EffortCertificationForm effortForm = (EffortCertificationForm) form;
        EffortCertificationDocument effortDocument = (EffortCertificationDocument) effortForm.getDocument();
        List<EffortCertificationDetail> detailLines = effortDocument.getEffortCertificationDetailLines();
        EffortCertificationDetail lineToRecalculate = detailLines.get(lineToRecalculateIndex);

        KualiDecimal totalPayrollAmount = effortDocument.getTotalOriginalPayrollAmount();
        lineToRecalculate.recalculatePayrollAmount(totalPayrollAmount);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * add New Effort Certification Detail Lines
     */
    public ActionForward add(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CertificationReportForm certificationReportForm = (CertificationReportForm) form;
        EffortCertificationDocument effortDocument = (EffortCertificationDocument) certificationReportForm.getDocument();
        List<EffortCertificationDetail> detailLines = effortDocument.getEffortCertificationDetailLines();
        EffortCertificationDetail newDetailLine = certificationReportForm.getNewDetailLine();

        newDetailLine.refresh();
        newDetailLine.setPositionNumber(effortDocument.getDefaultPositionNumber());
        newDetailLine.setFinancialObjectCode(effortDocument.getDefaultObjectCode());
        newDetailLine.setNewLineIndicator(true);

        EffortCertificationDocumentRuleUtil.applyDefaultValues(newDetailLine);

        if (newDetailLine.isAccountExpiredOverride()) {
            this.updateDetailLineOverrideCode(newDetailLine);
        }

        // check business rules
        boolean isValid = this.invokeRules(new AddDetailLineEvent("", EffortPropertyConstants.EFFORT_CERTIFICATION_DOCUMENT_NEW_LINE, effortDocument, newDetailLine));
        if (isValid) {
            if (EffortCertificationDocumentRuleUtil.hasA21SubAccount(newDetailLine)) {
                EffortCertificationDocumentRuleUtil.updateSourceAccountInformation(newDetailLine);
            }

            this.resetPersistedFields(newDetailLine);
            detailLines.add(newDetailLine);
            certificationReportForm.setNewDetailLine(certificationReportForm.createNewDetailLine());
        }
        else {
            EffortCertificationDetailLineOverride.processForOutput(newDetailLine);
        }

        this.processDetailLineOverrides(detailLines);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * delete detail line
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
     * revert the detail line to the original values
     */
    public ActionForward revert(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        int lineToRevertIndex = getLineToDelete(request);

        EffortCertificationForm effortForm = (EffortCertificationForm) form;
        EffortCertificationDocument effortDocument = (EffortCertificationDocument) effortForm.getDocument();
        List<EffortCertificationDetail> detailLines = effortDocument.getEffortCertificationDetailLines();

        this.revertDetaiLine(detailLines, lineToRevertIndex);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#loadDocument(org.kuali.core.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void loadDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.loadDocument(kualiDocumentFormBase);

        CertificationReportForm certificationReportForm = (CertificationReportForm) kualiDocumentFormBase;
        if (this.isSummarizeDetailLinesRendered(certificationReportForm)) {
            this.refreshDetailLineGroupMap(certificationReportForm);
        }
    }

    /**
     * @see org.kuali.core.web.struts.action.KualiAction#refresh(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
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
     * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        refresh(mapping, form, request, response);

        CertificationReportForm certificationReportForm = (CertificationReportForm) form;
        this.updateDetailLinesFromSummaryLines(certificationReportForm);

        return super.execute(mapping, form, request, response);
    }

    /**
     * sort the detail lines by column
     */
    public ActionForward sortDetailLineByColumn(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CertificationReportForm certificationReportForm = (CertificationReportForm) form;

        String methodToCallAttribute = (String) request.getAttribute(KNSConstants.METHOD_TO_CALL_ATTRIBUTE);
        String sortColumn = StringUtils.substringBetween(methodToCallAttribute, "sortDetailLineByColumn.", ".");

        this.toggleSortOrder(certificationReportForm);
        this.sortDetailLine(certificationReportForm, certificationReportForm.getDetailLines(), sortColumn);

        if (this.isSummarizeDetailLinesRendered(certificationReportForm)) {
            this.sortDetailLine(certificationReportForm, certificationReportForm.getSummarizedDetailLines(), sortColumn);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * recalculate a detail line in summarized detail lines and make a corresponding update on the underlying detail lines
     */
    public ActionForward recalculateSummarizedDetailLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CertificationReportForm certificationReportForm = (CertificationReportForm) form;
        EffortCertificationDocument effortDocument = (EffortCertificationDocument) certificationReportForm.getDocument();
        KualiDecimal totalPayrollAmount = effortDocument.getTotalOriginalPayrollAmount();

        // recalculate the selected summary line
        List<EffortCertificationDetail> summarizedDetailLines = certificationReportForm.getSummarizedDetailLines();
        int lineToRecalculateIndex = this.getSelectedLine(request);
        EffortCertificationDetail lineToRecalculate = summarizedDetailLines.get(lineToRecalculateIndex);
        lineToRecalculate.recalculatePayrollAmount(totalPayrollAmount);

        // rebuild the detail line groups from the detail lines of the current document
        Map<String, DetailLineGroup> detailLineGroupMap = DetailLineGroup.groupDetailLines(certificationReportForm.getDetailLines(), EffortConstants.DETAIL_LINES_CONSOLIDATION_FILEDS);
        DetailLineGroup detailLineGroup = this.getDetailLineGroupByDetailLine(detailLineGroupMap, lineToRecalculate);
        this.updateDetailLineGroup(detailLineGroup, lineToRecalculate, totalPayrollAmount);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * add a new detail line and make a corresponding update on the underlying detail lines
     */
    public ActionForward addSummarizedDetailLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward actionForward = this.add(mapping, form, request, response);

        if (GlobalVariables.getErrorMap().isEmpty()) {
            CertificationReportForm certificationReportForm = (CertificationReportForm) form;
            this.refreshDetailLineGroupMap(certificationReportForm);
        }

        return actionForward;
    }

    /**
     * delete a detail line from summarized detail lines and make a corresponding update on the underlying detail lines
     */
    public ActionForward deleteSummarizedDetailLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CertificationReportForm certificationReportForm = (CertificationReportForm) form;

        // remove the selected summary line
        List<EffortCertificationDetail> summarizedDetailLines = certificationReportForm.getSummarizedDetailLines();
        int lineToRecalculateIndex = this.getSelectedLine(request);
        EffortCertificationDetail lineToDelete = summarizedDetailLines.get(lineToRecalculateIndex);
        summarizedDetailLines.remove(lineToDelete);

        // remove the corresponding detail line from the current document
        Map<String, DetailLineGroup> detailLineGroupMap = DetailLineGroup.groupDetailLines(certificationReportForm.getDetailLines(), EffortConstants.DETAIL_LINES_CONSOLIDATION_FILEDS);
        DetailLineGroup detailLineGroup = this.getDetailLineGroupByDetailLine(detailLineGroupMap, lineToDelete);
        EffortCertificationDetail delegateDetailLine = detailLineGroup.getDelegateDetailLine();
        certificationReportForm.getDetailLines().remove(delegateDetailLine);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * revert a detail line in summarized detail lines and make a corresponding update on the underlying detail lines
     */
    public ActionForward revertSummarizedDetailLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CertificationReportForm certificationReportForm = (CertificationReportForm) form;

        List<EffortCertificationDetail> summarizedDetailLines = certificationReportForm.getSummarizedDetailLines();
        int lineToRevertIndex = this.getSelectedLine(request);
        EffortCertificationDetail lineToRevert = summarizedDetailLines.get(lineToRevertIndex);

        Map<String, DetailLineGroup> detailLineGroupMap = DetailLineGroup.groupDetailLines(certificationReportForm.getDetailLines(), EffortConstants.DETAIL_LINES_CONSOLIDATION_FILEDS);
        DetailLineGroup detailLineGroup = this.getDetailLineGroupByDetailLine(detailLineGroupMap, lineToRevert);
        List<EffortCertificationDetail> detailLinesInGroup = detailLineGroup.getDetailLines();

        List<EffortCertificationDetail> detailLines = certificationReportForm.getDetailLines();
        for (EffortCertificationDetail detailLine : detailLinesInGroup) {
            this.revertDetaiLine(detailLines, detailLine);
        }

        this.refreshDetailLineGroupMap(certificationReportForm);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * find the detail line group through the given detail line
     * 
     * @param detailLineGroupMap the given detail line group map
     * @param detailLine the given detail line
     * @return the detail line group
     */
    private DetailLineGroup getDetailLineGroupByDetailLine(Map<String, DetailLineGroup> detailLineGroupMap, EffortCertificationDetail detailLine) {
        String keysAsString = ObjectUtil.concatPropertyAsString(detailLine, EffortConstants.DETAIL_LINES_CONSOLIDATION_FILEDS);
        return detailLineGroupMap.get(keysAsString);
    }

    /**
     * determine whether the summarized detail lines need to be rendered
     * 
     * @param certificationReportForm the action form
     * @return true if the summarized detail lines need to be rendered; otherwise, false
     */
    private boolean isSummarizeDetailLinesRendered(CertificationReportForm certificationReportForm) {
        Document document = certificationReportForm.getDocument();
        DocumentAuthorizer documentAuthorizer = KNSServiceLocator.getDocumentAuthorizationService().getDocumentAuthorizer(document);

        certificationReportForm.populateAuthorizationFields(documentAuthorizer);
        Map<String, String> editMode = certificationReportForm.getEditingMode();

        if (editMode.containsKey(EffortCertificationEditMode.PROJECT_ENTRY)) {
            return Boolean.parseBoolean(editMode.get(EffortCertificationEditMode.PROJECT_ENTRY));
        }

        return false;
    }

    /**
     * recalculate all detail lines with the information in summarized detail lines
     * 
     * @param certificationReportForm the given action form
     */
    private void recalculateAllDetailLines(CertificationReportForm certificationReportForm) {
        Map<String, DetailLineGroup> detailLineGroupMap = DetailLineGroup.groupDetailLines(certificationReportForm.getDetailLines(), EffortConstants.DETAIL_LINES_CONSOLIDATION_FILEDS);

        EffortCertificationDocument effortDocument = (EffortCertificationDocument) certificationReportForm.getDocument();
        KualiDecimal totalPayrollAmount = effortDocument.getTotalOriginalPayrollAmount();

        List<EffortCertificationDetail> summarizedDetailLines = certificationReportForm.getSummarizedDetailLines();
        for (EffortCertificationDetail detailLine : summarizedDetailLines) {
            // recalculate the selected summary line
            detailLine.recalculatePayrollAmount(totalPayrollAmount);

            // rebuild the detail line groups from the detail lines of the current document
            DetailLineGroup detailLineGroup = this.getDetailLineGroupByDetailLine(detailLineGroupMap, detailLine);
            this.updateDetailLineGroup(detailLineGroup, detailLine, totalPayrollAmount);
        }
    }

    /**
     * recalculate all detail lines with the information in summarized detail lines
     * 
     * @param certificationReportForm the given action form
     */
    private void updateDetailLinesFromSummaryLines(CertificationReportForm certificationReportForm) {
        Map<String, DetailLineGroup> detailLineGroupMap = DetailLineGroup.groupDetailLines(certificationReportForm.getDetailLines(), EffortConstants.DETAIL_LINES_CONSOLIDATION_FILEDS);

        EffortCertificationDocument effortDocument = (EffortCertificationDocument) certificationReportForm.getDocument();
        KualiDecimal totalPayrollAmount = effortDocument.getTotalOriginalPayrollAmount();

        List<EffortCertificationDetail> summarizedDetailLines = certificationReportForm.getSummarizedDetailLines();
        for (EffortCertificationDetail detailLine : summarizedDetailLines) {
            // rebuild the detail line groups from the detail lines of the current document
            DetailLineGroup detailLineGroup = this.getDetailLineGroupByDetailLine(detailLineGroupMap, detailLine);
            this.updateDetailLineGroup(detailLineGroup, detailLine, totalPayrollAmount);
        }
    }

    /**
     * update detail line group with the the information in the given detail line
     * 
     * @param detailLineGroup the given detail line group
     * @param detailLine the given detail line
     * @param totalPayrollAmount the total payroll amount of the document associating with the detail line group
     */
    private void updateDetailLineGroup(DetailLineGroup detailLineGroup, EffortCertificationDetail detailLine, KualiDecimal totalPayrollAmount) {
        EffortCertificationDetail summaryLine = detailLineGroup.getSummaryDetailLine();
        summaryLine.setEffortCertificationUpdatedOverallPercent(detailLine.getEffortCertificationUpdatedOverallPercent());
        summaryLine.setEffortCertificationPayrollAmount(detailLine.getEffortCertificationPayrollAmount());

        detailLineGroup.updateDetailLineEffortPercent();
        detailLineGroup.updateDetailLinePayrollAmount();
    }

    /**
     * Toggles the sort order between ascending and descending. If the current order is ascending, then the sort order will be set
     * to descending, and vice versa.
     */
    private void toggleSortOrder(CertificationReportForm certificationReportForm) {
        if (SortOrder.ASC.name().equals(certificationReportForm.getSortOrder())) {
            certificationReportForm.setSortOrder(SortOrder.DESC.name());
        }
        else {
            certificationReportForm.setSortOrder(SortOrder.ASC.name());
        }
    }

    /**
     * sort the detail lines based on the values of the sort order and sort column
     */
    private void sortDetailLine(CertificationReportForm certificationReportForm, List<EffortCertificationDetail> detailLines, String... sortColumn) {
        String sortOrder = certificationReportForm.getSortOrder();
        DynamicCollectionComparator.sort(detailLines, SortOrder.valueOf(sortOrder), sortColumn);
    }

    /**
     * rebuild the detail line group map from the detail lines of the current document
     */
    private Map<String, DetailLineGroup> refreshDetailLineGroupMap(CertificationReportForm certificationReportForm) {
        LOG.info("refreshDetailLineGroupMap() started");

        List<EffortCertificationDetail> summarizedDetailLines = certificationReportForm.getSummarizedDetailLines();
        if (summarizedDetailLines == null) {
            EffortCertificationDocument effortCertificationDocument = (EffortCertificationDocument) certificationReportForm.getDocument();
            effortCertificationDocument.setSummarizedDetailLines(new ArrayList<EffortCertificationDetail>());
        }
        summarizedDetailLines.clear();

        Map<String, DetailLineGroup> detailLineGroupMap = DetailLineGroup.groupDetailLines(certificationReportForm.getDetailLines(), EffortConstants.DETAIL_LINES_CONSOLIDATION_FILEDS);

        for (String key : detailLineGroupMap.keySet()) {
            EffortCertificationDetail sumaryline = detailLineGroupMap.get(key).getSummaryDetailLine();

            summarizedDetailLines.add(sumaryline);
        }

        return detailLineGroupMap;
    }

    /**
     * find the given detail line from the given collection of detail lines and revert it
     */
    private void revertDetaiLine(List<EffortCertificationDetail> detailLines, EffortCertificationDetail lineToRevert) {
        int lineToRevertIndex = detailLines.lastIndexOf(lineToRevert);

        this.revertDetaiLine(detailLines, lineToRevertIndex);
    }

    /**
     * revert the detail line in the specified position
     */
    private void revertDetaiLine(List<EffortCertificationDetail> detailLines, int lineToRevertIndex) {
        EffortCertificationDetail lineToRevert = detailLines.get(lineToRevertIndex);

        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        EffortCertificationDetail revertedLine = (EffortCertificationDetail) businessObjectService.retrieve(lineToRevert);
        this.resetPersistedFields(revertedLine);

        detailLines.remove(lineToRevertIndex);
        detailLines.add(lineToRevertIndex, revertedLine);
    }

    /**
     * reset the persisted fields of the given detail line
     */
    private void resetPersistedFields(EffortCertificationDetail detailLine) {
        int persistedEffortPercent = detailLine.getEffortCertificationUpdatedOverallPercent();
        detailLine.setPersistedEffortPercent(new Integer(persistedEffortPercent));

        BigDecimal persistedPayrollAmount = detailLine.getEffortCertificationPayrollAmount().bigDecimalValue();
        detailLine.setPersistedPayrollAmount(new KualiDecimal(persistedPayrollAmount));
    }
}