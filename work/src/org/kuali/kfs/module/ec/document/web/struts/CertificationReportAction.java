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
package org.kuali.kfs.module.ec.document.web.struts;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.ec.EffortConstants;
import org.kuali.kfs.module.ec.EffortPropertyConstants;
import org.kuali.kfs.module.ec.EffortConstants.EffortCertificationEditMode;
import org.kuali.kfs.module.ec.businessobject.EffortCertificationDetail;
import org.kuali.kfs.module.ec.businessobject.EffortCertificationDetailLineOverride;
import org.kuali.kfs.module.ec.document.EffortCertificationDocument;
import org.kuali.kfs.module.ec.document.validation.event.AddDetailLineEvent;
import org.kuali.kfs.module.ec.document.validation.event.CheckDetailLineAmountEvent;
import org.kuali.kfs.module.ec.document.validation.impl.EffortCertificationDocumentRuleUtil;
import org.kuali.kfs.module.ec.service.EffortCertificationDocumentService;
import org.kuali.kfs.module.ec.util.DetailLineGroup;
import org.kuali.kfs.sys.DynamicCollectionComparator;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.ObjectUtil;
import org.kuali.kfs.sys.DynamicCollectionComparator.SortOrder;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.bo.AdHocRoutePerson;
import org.kuali.rice.kns.bo.AdHocRouteRecipient;
import org.kuali.rice.kns.bo.Note;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.document.authorization.DocumentAuthorizer;
import org.kuali.rice.kns.rule.event.ApproveDocumentEvent;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.RiceKeyConstants;
import org.kuali.rice.kns.util.WebUtils;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;

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
        EffortCertificationDocumentRuleUtil.applyDefaultValues(lineToRecalculate);

        String errorPathPrefix = KFSPropertyConstants.DOCUMENT + "." + EffortPropertyConstants.EFFORT_CERTIFICATION_DETAIL_LINES;
        boolean isValid = this.invokeRules(new CheckDetailLineAmountEvent("", errorPathPrefix, effortDocument, lineToRecalculate));
        if (isValid) {
            KualiDecimal totalPayrollAmount = effortDocument.getTotalOriginalPayrollAmount();
            lineToRecalculate.recalculatePayrollAmount(totalPayrollAmount);
        }

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

        // KFSMI-798 - refreshNonUpdatableReferences() used instead of refresh(),
        // EffortCertificationDetail does not have any updatable references
        newDetailLine.refreshNonUpdateableReferences();
        newDetailLine.setPositionNumber(effortDocument.getDefaultPositionNumber());
        newDetailLine.setFinancialObjectCode(effortDocument.getDefaultObjectCode());
        newDetailLine.setNewLineIndicator(true);

        EffortCertificationDetail workingDetailLine = new EffortCertificationDetail();
        ObjectUtil.buildObject(workingDetailLine, newDetailLine);

        EffortCertificationDocumentRuleUtil.applyDefaultValues(workingDetailLine);

        if (newDetailLine.isAccountExpiredOverride()) {
            this.updateDetailLineOverrideCode(workingDetailLine);
            this.updateDetailLineOverrideCode(newDetailLine);
        }

        // check business rules
        boolean isValid = this.invokeRules(new AddDetailLineEvent("", EffortPropertyConstants.EFFORT_CERTIFICATION_DOCUMENT_NEW_LINE, effortDocument, workingDetailLine));
        if (isValid) {
            if (EffortCertificationDocumentRuleUtil.hasA21SubAccount(workingDetailLine)) {
                EffortCertificationDocumentRuleUtil.updateSourceAccountInformation(workingDetailLine);
            }

            this.resetPersistedFields(workingDetailLine);
            detailLines.add(workingDetailLine);
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
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#loadDocument(org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase)
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
     * @see org.kuali.rice.kns.web.struts.action.KualiAction#refresh(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward actionForward = super.refresh(mapping, form, request, response);

        CertificationReportForm certificationReportForm = (CertificationReportForm) form;
        for (EffortCertificationDetail detailLine : certificationReportForm.getDetailLines()) {
            detailLine.refreshNonUpdateableReferences();

            EffortCertificationDocumentRuleUtil.applyDefaultValues(detailLine);
        }

        return actionForward;
    }

    /**
     * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CertificationReportForm certificationReportForm = (CertificationReportForm) form;
        if (this.isSummarizeDetailLinesRendered(certificationReportForm)) {
            this.updateDetailLinesFromSummaryLines(certificationReportForm);
        }

        ActionForward actionForward = super.execute(mapping, form, request, response);
        this.refresh(mapping, form, request, response);

        return actionForward;
    }

    /**
     * sort the detail lines by column
     */
    public ActionForward sortDetailLineByColumn(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CertificationReportForm certificationReportForm = (CertificationReportForm) form;

        String methodToCallAttribute = (String) request.getAttribute(KNSConstants.METHOD_TO_CALL_ATTRIBUTE);
        String sortMethodName = EffortConstants.SORT_DETAIL_LINE_BY_COLUMN_METHOD_NAME + ".";
        String sortColumn = StringUtils.substringBetween(methodToCallAttribute, sortMethodName, ".");

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
        EffortCertificationDocumentRuleUtil.applyDefaultValues(lineToRecalculate);

        String errorPathPrefix = KFSPropertyConstants.DOCUMENT + "." + EffortPropertyConstants.SUMMARIZED_DETAIL_LINES + "[" + lineToRecalculateIndex + "]";
        boolean isValid = this.invokeRules(new CheckDetailLineAmountEvent("", errorPathPrefix, effortDocument, lineToRecalculate));
        if (isValid) {
            lineToRecalculate.recalculatePayrollAmount(totalPayrollAmount);

            String groupId = lineToRecalculate.getGroupId();
            List<EffortCertificationDetail> detailLines = certificationReportForm.getDetailLines();
            List<EffortCertificationDetail> detailLinesToRecalculate = this.findDetailLinesInGroup(detailLines, groupId);
            for (EffortCertificationDetail line : detailLinesToRecalculate) {
                ObjectUtil.buildObject(line, lineToRecalculate, EffortConstants.DETAIL_LINES_GROUPING_FILEDS);
            }

            // rebuild the detail line groups from the detail lines of the current document
            Map<String, DetailLineGroup> detailLineGroupMap = DetailLineGroup.groupDetailLines(certificationReportForm.getDetailLines());
            DetailLineGroup detailLineGroup = detailLineGroupMap.get(DetailLineGroup.getKeysAsString(lineToRecalculate));
            this.updateDetailLineGroup(detailLineGroup, lineToRecalculate, totalPayrollAmount);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * add a new detail line and make a corresponding update on the underlying detail lines
     */
    public ActionForward addSummarizedDetailLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward actionForward = this.add(mapping, form, request, response);

        if (GlobalVariables.getErrorMap().getErrorCount() >= 0) {
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

        String groupId = lineToDelete.getGroupId();
        List<EffortCertificationDetail> detailLines = certificationReportForm.getDetailLines();
        List<EffortCertificationDetail> detailLinesToDelete = this.findDetailLinesInGroup(detailLines, groupId);
        detailLines.removeAll(detailLinesToDelete);

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

        Map<String, DetailLineGroup> detailLineGroupMap = DetailLineGroup.groupDetailLines(certificationReportForm.getDetailLines());
        DetailLineGroup detailLineGroup = detailLineGroupMap.get(DetailLineGroup.getKeysAsString(lineToRevert));
        List<EffortCertificationDetail> detailLinesInGroup = detailLineGroup.getDetailLines();

        List<EffortCertificationDetail> detailLines = certificationReportForm.getDetailLines();
        for (EffortCertificationDetail detailLine : detailLinesInGroup) {
            this.revertDetaiLine(detailLines, detailLine);
        }

        this.refreshDetailLineGroupMap(certificationReportForm);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * find the detail lines belonging to the given group from the given detail lines
     * 
     * @param detailLines the given detail lines
     * @param groupId the given group id
     * @return the detail lines belonging to the given group
     */
    private List<EffortCertificationDetail> findDetailLinesInGroup(List<EffortCertificationDetail> detailLines, String groupId) {
        List<EffortCertificationDetail> detailLinesInGroup = new ArrayList<EffortCertificationDetail>();

        for (EffortCertificationDetail line : detailLines) {
            if (StringUtils.equals(groupId, line.getGroupId())) {
                detailLinesInGroup.add(line);
            }
        }

        return detailLinesInGroup;
    }

    /**
     * determine whether the summarized detail lines need to be rendered
     * 
     * @param certificationReportForm the action form
     * @return true if the summarized detail lines need to be rendered; otherwise, false
     */
    private boolean isSummarizeDetailLinesRendered(CertificationReportForm certificationReportForm) {
        super.populateAuthorizationFields(certificationReportForm);

        return certificationReportForm.getEditingMode().containsKey(EffortCertificationEditMode.SUMMARY_TAB_ENTRY);
    }

    /**
     * recalculate all detail lines with the information in summarized detail lines
     * 
     * @param certificationReportForm the given action form
     */
    private void recalculateAllDetailLines(CertificationReportForm certificationReportForm) {
        Map<String, DetailLineGroup> detailLineGroupMap = DetailLineGroup.groupDetailLines(certificationReportForm.getDetailLines());

        EffortCertificationDocument effortDocument = (EffortCertificationDocument) certificationReportForm.getDocument();
        KualiDecimal totalPayrollAmount = effortDocument.getTotalOriginalPayrollAmount();

        List<EffortCertificationDetail> summarizedDetailLines = certificationReportForm.getSummarizedDetailLines();
        for (EffortCertificationDetail detailLine : summarizedDetailLines) {
            // recalculate the selected summary line
            detailLine.recalculatePayrollAmount(totalPayrollAmount);

            // rebuild the detail line groups from the detail lines of the current document
            DetailLineGroup detailLineGroup = detailLineGroupMap.get(DetailLineGroup.getKeysAsString(detailLine));
            this.updateDetailLineGroup(detailLineGroup, detailLine, totalPayrollAmount);
        }
    }

    /**
     * recalculate all detail lines with the information in summarized detail lines
     * 
     * @param certificationReportForm the given action form
     */
    private void updateDetailLinesFromSummaryLines(CertificationReportForm certificationReportForm) {
        EffortCertificationDocument effortDocument = (EffortCertificationDocument) certificationReportForm.getDocument();
        List<EffortCertificationDetail> detailLines = certificationReportForm.getDetailLines();
        List<EffortCertificationDetail> summarizedDetailLines = certificationReportForm.getSummarizedDetailLines();

        boolean isValid = true;
        int index = 0;
        for (EffortCertificationDetail detailLine : summarizedDetailLines) {
            EffortCertificationDocumentRuleUtil.applyDefaultValues(detailLine);

            String errorPathPrefix = KFSPropertyConstants.DOCUMENT + "." + EffortPropertyConstants.SUMMARIZED_DETAIL_LINES + "[" + index + "]";
            isValid &= this.invokeRules(new CheckDetailLineAmountEvent("", errorPathPrefix, effortDocument, detailLine));

            ++index;
        }

        if (!isValid) {
            return;
        }

        for (EffortCertificationDetail detailLine : summarizedDetailLines) {
            if (!detailLine.isNewLineIndicator()) {
                continue;
            }

            if (detailLine.isAccountExpiredOverride()) {
                detailLine.refreshReferenceObject(KFSPropertyConstants.ACCOUNT);
                this.updateDetailLineOverrideCode(detailLine);
            }

            List<EffortCertificationDetail> detailLinesToUpdate = this.findDetailLinesInGroup(detailLines, detailLine.getGroupId());
            for (EffortCertificationDetail line : detailLinesToUpdate) {
                ObjectUtil.buildObject(line, detailLine, EffortConstants.DETAIL_LINES_GROUPING_FILEDS);

                line.setAccountExpiredOverride(detailLine.isAccountExpiredOverride());
                line.setAccountExpiredOverrideNeeded(detailLine.isAccountExpiredOverrideNeeded());
                line.setOverrideCode(detailLine.getOverrideCode());
            }
        }

        Map<String, DetailLineGroup> detailLineGroupMap = DetailLineGroup.groupDetailLines(detailLines);
        KualiDecimal totalPayrollAmount = effortDocument.getTotalOriginalPayrollAmount();
        for (EffortCertificationDetail detailLine : summarizedDetailLines) {
            // rebuild the detail line groups from the detail lines of the current document
            detailLine.setGroupId(DetailLineGroup.getKeysAsString(detailLine));
            DetailLineGroup detailLineGroup = detailLineGroupMap.get(DetailLineGroup.getKeysAsString(detailLine));
            this.updateDetailLineGroup(detailLineGroup, detailLine, totalPayrollAmount);
        }

        this.processDetailLineOverrides(detailLines);
        this.processDetailLineOverrides(summarizedDetailLines);
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

        Map<String, DetailLineGroup> detailLineGroupMap = DetailLineGroup.groupDetailLines(certificationReportForm.getDetailLines());

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

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#approve(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward approve(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
        EffortCertificationDocument effortDocument = (EffortCertificationDocument) kualiDocumentFormBase.getDocument();

        EffortCertificationDocumentService effortCertificationDocumentService = SpringContext.getBean(EffortCertificationDocumentService.class);        
        effortCertificationDocumentService.addRouteLooping(effortDocument);
        
        ActionForward actionForward = super.approve(mapping, kualiDocumentFormBase, request, response);

        return actionForward;
    }

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#insertBONote(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward insertBONote(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
        kualiDocumentFormBase.getNewNote().setNewCollectionRecord(true);
        
        return super.insertBONote(mapping, form, request, response);
    }
    
    
}
