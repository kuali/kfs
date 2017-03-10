package edu.arizona.kfs.module.tax.document.web.struts;

import java.io.ByteArrayOutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRParameter;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.sys.KFSConstants.ReportGeneration;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ReportGenerationService;
import org.kuali.rice.kns.util.WebUtils;
import org.kuali.rice.kns.web.struts.action.KualiAction;
import org.kuali.rice.krad.util.GlobalVariables;

import edu.arizona.kfs.module.tax.TaxConstants;
import edu.arizona.kfs.module.tax.TaxPropertyConstants;
import edu.arizona.kfs.module.tax.businessobject.ElectronicFile;
import edu.arizona.kfs.module.tax.businessobject.ElectronicFileException;
import edu.arizona.kfs.module.tax.businessobject.Payer;
import edu.arizona.kfs.module.tax.service.ElectronicFilingService;
import edu.arizona.kfs.module.tax.service.TaxParameterHelperService;
import edu.arizona.kfs.sys.KFSConstants;
import edu.arizona.kfs.sys.KFSKeyConstants;

@SuppressWarnings("deprecation")
public class ElectronicFilingReportAction extends KualiAction {
    private static final Logger LOG = Logger.getLogger(ElectronicFilingReportAction.class);

    @SuppressWarnings("rawtypes")
    public ActionForward performReport(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.info("Beginning 1099 Exception Report");
        ElectronicFilingReportForm eform = (ElectronicFilingReportForm) form;
        ElectronicFile ef = null;
        ElectronicFileException temp = null;
        Collection<ElectronicFileException> reportSet = null;

        TaxParameterHelperService payeeServ = SpringContext.getBean(TaxParameterHelperService.class);

        Payer p = payeeServ.getDefaultPayer();

        if (p == null) {
            GlobalVariables.getMessageMap().putError(TaxPropertyConstants.ElectronicFilingReportFields.TAX_YEAR, KFSKeyConstants.ERROR_CUSTOM, "No 1099 Payer Record Found");
        } else {
            if (eform.getTaxYear() == null) {
                GlobalVariables.getMessageMap().putError(TaxPropertyConstants.ElectronicFilingReportFields.TAX_YEAR, KFSKeyConstants.ERROR_CUSTOM, "Tax Year is a required field.");
            } else {
                ElectronicFilingService serv = SpringContext.getBean(ElectronicFilingService.class);
                ef = serv.getElectronicFile(eform.getTaxYear());
                reportSet = ef.validateFile();
            }

            if (reportSet != null && reportSet.isEmpty()) {
                GlobalVariables.getMessageMap().putError(TaxPropertyConstants.ElectronicFilingReportFields.TAX_YEAR, KFSKeyConstants.ERROR_CUSTOM, "No Electronic Filing Problems Found");
            } else if (reportSet != null) {
                if ("excel".equals(eform.getFormat())) {
                    // build excel file and send
                    response.setContentType(KFSConstants.ReportGeneration.CSV_MIME_TYPE);
                    response.setHeader(KFSConstants.HttpHeaderResponse.PRAGMA, "No-cache");
                    response.setHeader(KFSConstants.HttpHeaderResponse.CACHE_CONTROL, "no-cache");
                    response.setHeader(KFSConstants.HttpHeaderResponse.CONTENT_DIPOSITION, "attachment; filename=" + TaxConstants.ELEC_REPORT_FILE_NAME + KFSConstants.ReportGeneration.CSV_FILE_EXTENSION);

                    java.io.PrintWriter out = response.getWriter();

                    out.println("TIN,NAME,TYPE,FIELD,VALUE,EXCEPTION");

                    for (Iterator i = ef.validateFile().iterator(); i.hasNext();) {
                        temp = (ElectronicFileException) i.next();
                        out.println(temp.toCsvString());
                    }
                    out.close();
                    return null;
                } else {
                    // build pdf and stream back
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();

                    ResourceBundle resourceBundle = ResourceBundle.getBundle(TaxConstants.REPORT_MESSAGES_CLASSPATH, Locale.getDefault());
                    Map<String, Object> reportData = new HashMap<String, Object>();
                    reportData.put(JRParameter.REPORT_RESOURCE_BUNDLE, resourceBundle);
                    reportData.put(ReportGeneration.PARAMETER_NAME_SUBREPORT_DIR, KFSConstants.EMPTY_STRING);

                    SpringContext.getBean(ReportGenerationService.class).generateReportToOutputStream(reportData, reportSet, TaxConstants.REPORT_TEMPLATE_CLASSPATH + TaxConstants.ELEC_REPORT_FILE_NAME, baos);
                    WebUtils.saveMimeOutputStreamAsFile(response, KFSConstants.ReportGeneration.PDF_MIME_TYPE, baos, TaxConstants.ELEC_REPORT_FILE_NAME + KFSConstants.ReportGeneration.PDF_FILE_EXTENSION);

                    return null;
                }
            }
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward returnToIndex(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward(KFSConstants.MAPPING_CLOSE);
    }
}
