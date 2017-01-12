package edu.arizona.kfs.module.purap.document.web.struts;

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
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kns.util.WebUtils;
import org.kuali.rice.kns.web.struts.action.KualiAction;
import org.kuali.rice.krad.exception.AuthorizationException;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;

import edu.arizona.kfs.module.purap.document.service.ElectronicFilingService;
import edu.arizona.kfs.module.purap.service.TaxReporting1099Service;
import edu.arizona.kfs.sys.KFSConstants;
import edu.arizona.kfs.sys.KFSKeyConstants;
import edu.arizona.kfs.tax.TaxConstants;
import edu.arizona.kfs.tax.TaxPropertyConstants;
import edu.arizona.kfs.tax.businessobject.ElectronicFile;
import edu.arizona.kfs.tax.businessobject.ElectronicFileException;
import edu.arizona.kfs.tax.businessobject.Payer;

public class ElectronicFilingReportAction extends KualiAction {
	private static final Logger LOG = Logger.getLogger(ElectronicFilingReportAction.class);

	@Override
	protected void checkAuthorization(ActionForm form, String methodToCall) throws AuthorizationException {
		String principalId = GlobalVariables.getUserSession().getPrincipalId();
		Map<String, String> roleQualifier = new HashMap<String, String>(getRoleQualification(form, methodToCall));
		Map<String, String> permissionDetails = new HashMap<String, String>();
		permissionDetails.put(KRADConstants.NAMESPACE_CODE, TaxConstants.NMSPC_CD);
		permissionDetails.put(KRADConstants.ACTION_CLASS, this.getClass().getName());

		if (!KimApiServiceLocator.getPermissionService().isAuthorizedByTemplate(principalId, KRADConstants.KNS_NAMESPACE, KimConstants.PermissionTemplateNames.USE_SCREEN, permissionDetails, roleQualifier)) {
			throw new AuthorizationException(GlobalVariables.getUserSession().getPerson().getPrincipalName(), methodToCall, this.getClass().getSimpleName());
		}
	}

	public ActionForward performReport(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		LOG.info("Beginning 1099 Exception Report");
		ElectronicFilingReportForm eform = (ElectronicFilingReportForm) form;
		ElectronicFile ef = null;
		ElectronicFileException temp = null;
		Collection<ElectronicFileException> reportSet = null;

		TaxReporting1099Service payeeServ = SpringContext.getBean(TaxReporting1099Service.class);

		Payer p = payeeServ.getDefaultPayer();

		if (p == null) {
			GlobalVariables.getMessageMap().putError(TaxPropertyConstants.TAX_YEAR, KFSKeyConstants.ERROR_CUSTOM, "No 1099 Payer Record Found");
		} else {
			if (eform.getTaxYear() == null) {
				GlobalVariables.getMessageMap().putError(TaxPropertyConstants.TAX_YEAR, KFSKeyConstants.ERROR_CUSTOM, "Tax Year is a required field.");
			} else {
				ElectronicFilingService serv = SpringContext.getBean(ElectronicFilingService.class);
				ef = serv.getElectronicFile(eform.getTaxYear());
				reportSet = ef.validateFile();
			}

			if (reportSet != null && reportSet.isEmpty()) {
				GlobalVariables.getMessageMap().putError(TaxPropertyConstants.TAX_YEAR, KFSKeyConstants.ERROR_CUSTOM, "No Electronic Filing Problems Found");
			} else if (reportSet != null) {
				if ("excel".equals(eform.getFormat())) {
					// build excel file and send
					response.setContentType(KFSConstants.ReportGeneration.CSV_MIME_TYPE);
					response.setHeader("Pragma", "No-cache");
					response.setHeader("Cache-Control", "no-cache");
					response.setHeader("Content-Disposition", "attachment; filename=" + TaxConstants.ELEC_REPORT_FILE_NAME + KFSConstants.ReportGeneration.CSV_FILE_EXTENSION);

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
					reportData.put(ReportGeneration.PARAMETER_NAME_SUBREPORT_DIR, "");

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
