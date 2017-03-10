package edu.arizona.kfs.module.tax.document.web.struts;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSConstants.ReportGeneration;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.util.KfsWebUtils;
import org.kuali.rice.kns.web.struts.action.KualiAction;

import edu.arizona.kfs.module.tax.TaxConstants;
import edu.arizona.kfs.module.tax.TaxPropertyConstants;
import edu.arizona.kfs.module.tax.businessobject.Payee;
import edu.arizona.kfs.module.tax.businessobject.Payer;
import edu.arizona.kfs.module.tax.service.TaxForm1099GeneratorService;
import edu.arizona.kfs.module.tax.service.TaxParameterHelperService;
import edu.arizona.kfs.module.tax.service.TaxPayeeService;

@SuppressWarnings("deprecation")
public class TaxFormAction extends KualiAction {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KualiAction.class);

    public ActionForward downloadTaxForm(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Integer id = Integer.valueOf(request.getParameter(TaxPropertyConstants.TaxFormFields.ID));
        Integer year = Integer.valueOf(request.getParameter(TaxPropertyConstants.TaxFormFields.YEAR));
        generatePayee1099Form(response, id, year);
        return null;
    }

    public ActionForward returnToIndex(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward(KFSConstants.MAPPING_CLOSE);
    }

    private void generatePayee1099Form(HttpServletResponse response, Integer id, Integer year) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] pdf = getPayee1099Form(id, year);
        if ((pdf != null) && (pdf.length > 0)) {
            baos.write(pdf);
            KfsWebUtils.saveMimeOutputStreamAsFile(response, ReportGeneration.PDF_MIME_TYPE, baos, TaxConstants.Form1099.PDF_1099_FILENAME_PREFIX + year.toString() + ReportGeneration.PDF_FILE_EXTENSION, false);
            if (LOG.isDebugEnabled()) {
                LOG.debug("File " + TaxConstants.Form1099.PDF_1099_FILENAME_PREFIX + year.toString() + ReportGeneration.PDF_FILE_EXTENSION + " sent to user.");
            }
        }
    }

    private byte[] getPayee1099Form(Integer id, Integer year) {
        TaxPayeeService taxPayeeService = SpringContext.getBean(TaxPayeeService.class);
        TaxParameterHelperService taxParameterHelperService = SpringContext.getBean(TaxParameterHelperService.class);
        TaxForm1099GeneratorService taxForm1099GeneratorService = SpringContext.getBean(TaxForm1099GeneratorService.class);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        if (LOG.isDebugEnabled()) {
            LOG.debug("attempting to load 1099 form for payee= " + id + ", year=" + year);
        }

        // make sure we have a default payer
        Payer payer = taxParameterHelperService.getDefaultPayer();
        if (payer == null) {
            throw new RuntimeException("Default payer for 1099 process not found. Check 1099 payer system property configuration.");
        }

        Integer taxYear = Integer.valueOf(taxParameterHelperService.getTaxYear());

        if ((year != null)) {
            taxYear = year;
        }

        // write out populated pdf 1099 forms
        List<Payee> payees = new ArrayList<Payee>();

        Payee p = taxPayeeService.loadPayee(taxYear, taxPayeeService.getPayee(id));

        if (p != null) {
            payees.add(p);
            taxForm1099GeneratorService.createPdfFile(taxYear, TaxConstants.Form1099.PDF_1099_PAGE_COPY_A, bos, payer, payees);
        } else {
            throw new RuntimeException("cannot find Payee: " + id);
        }

        return bos.toByteArray();
    }

}
