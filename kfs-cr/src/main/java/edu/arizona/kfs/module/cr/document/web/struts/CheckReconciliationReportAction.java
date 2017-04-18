package edu.arizona.kfs.module.cr.document.web.struts;

import java.sql.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.web.struts.action.KualiAction;
import org.kuali.rice.krad.util.GlobalVariables;

import edu.arizona.kfs.module.cr.CrPropertyConstants;
import edu.arizona.kfs.module.cr.businessobject.CheckReconciliationReportLine;
import edu.arizona.kfs.module.cr.service.CheckReconciliationReportService;
import edu.arizona.kfs.sys.KFSKeyConstants;

/**
 * Check Reconciliation Report Action
 * 
 * Deprecation: KualiAction and WebUtils are marked as deprecated by Eclipse.
 */
@SuppressWarnings("deprecation")
public class CheckReconciliationReportAction extends KualiAction {

    private CheckReconciliationReportService checkReconciliationReportService;
    private static final String EXCEL = "excel";

    private CheckReconciliationReportService getCheckReconciliationReportService() {
        if (checkReconciliationReportService == null) {
            checkReconciliationReportService = SpringContext.getBean(CheckReconciliationReportService.class);
        }
        return checkReconciliationReportService;
    }

    /**
     * Generates the CR Report and returns it.
     */
    public ActionForward performReport(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CheckReconciliationReportForm crForm = (CheckReconciliationReportForm) form;
        Date reportEndDate = crForm.getEndDate();
        String reportFormat = crForm.getFormat();

        List<CheckReconciliationReportLine> reportSet = getCheckReconciliationReportService().generateReportSet(reportEndDate);
        if (reportSet == null || reportSet.isEmpty()) {
            GlobalVariables.getMessageMap().putError(CrPropertyConstants.CheckReconciliationReport.END_DATE, KFSKeyConstants.ERROR_CUSTOM, "No Check Records Found");
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
        if (EXCEL.equals(reportFormat)) {
            getCheckReconciliationReportService().generateCsvReport(response, reportEndDate, reportSet);
        } else {
            getCheckReconciliationReportService().generatePdfReport(response, reportEndDate, reportSet);

        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward returnToIndex(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward(KFSConstants.MAPPING_CLOSE);
    }

}
