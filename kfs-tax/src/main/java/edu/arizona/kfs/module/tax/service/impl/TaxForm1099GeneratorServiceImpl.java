package edu.arizona.kfs.module.tax.service.impl;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;

import com.lowagie.text.Document;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;

import edu.arizona.kfs.module.tax.TaxConstants;
import edu.arizona.kfs.module.tax.TaxParameterConstants;
import edu.arizona.kfs.module.tax.businessobject.Payee;
import edu.arizona.kfs.module.tax.businessobject.Payer;
import edu.arizona.kfs.module.tax.service.TaxForm1099GeneratorService;
import edu.arizona.kfs.module.tax.service.TaxParameterHelperService;
import edu.arizona.kfs.module.tax.service.TaxPayeeService;
import edu.arizona.kfs.sys.KFSConstants;

public class TaxForm1099GeneratorServiceImpl implements TaxForm1099GeneratorService {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TaxForm1099GeneratorService.class);

    private ParameterService parameterService;
    private TaxPayeeService taxPayeeService;
    private ConfigurationService configurationService;
    private TaxParameterHelperService taxParameterHelperService;
    private String pdfDirectory;

    // Spring Injectors

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public void setTaxPayeeService(TaxPayeeService taxPayeeService) {
        this.taxPayeeService = taxPayeeService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public void setTaxParameterHelperService(TaxParameterHelperService taxParameterHelperService) {
        this.taxParameterHelperService = taxParameterHelperService;
    }

    public void setPdfDirectory(String pdfDirectory) {
        this.pdfDirectory = pdfDirectory;
    }

    // Public Service Methods

    @Override
    public boolean generateBatchPayeeForms(int year, List<Payee> payeeList) {
        boolean retval = false;

        LOG.info("start generateBatchPayeeForms(); taxYear=" + year + ", startTime=" + new Date());

        // don't move forward if there are no payees to process
        if ((payeeList == null) || payeeList.isEmpty()) {
            LOG.info("NO PAYEE RECORDS FOUND. ELECTRONIC FILING STEP WILL NOT BE RUN.");
        } else {
            LOG.info("payee counts=" + payeeList.size());

            Payer payer = taxParameterHelperService.getDefaultPayer();
            if (payer == null) {
                throw new RuntimeException("Default payer for 1099 process not found. Check 1099 payer system property configuration.");
            }

            File taxYearFolder = getTaxYearFolder(year);

            Date dt = new Date();
            BufferedOutputStream osa = null;
            BufferedOutputStream osb = null;
            try {
                String pdfFilenameA = taxYearFolder.getAbsolutePath() + "/1099A-" + year + "-" + TaxConstants.FILE_TIMESTAMP.format(dt) + KFSConstants.ReportGeneration.PDF_FILE_EXTENSION;
                FileOutputStream fosA = new FileOutputStream(pdfFilenameA);
                osa = new BufferedOutputStream(fosA);
                createPdfFile(year, TaxConstants.Form1099.PDF_1099_PAGE_COPY_A, osa, payer, payeeList);

                String pdfFilenameB = taxYearFolder.getAbsolutePath() + "/1099B-" + year + "-" + TaxConstants.FILE_TIMESTAMP.format(dt) + KFSConstants.ReportGeneration.PDF_FILE_EXTENSION;
                FileOutputStream fosB = new FileOutputStream(pdfFilenameB);
                osb = new BufferedOutputStream(fosB);
                createPdfFile(year, TaxConstants.Form1099.PDF_1099_PAGE_COPY_B, osb, payer, payeeList);
                retval = true;
            } catch (Exception ex) {
                throw new RuntimeException("Error ocurred while attepmting to create/populate PDF file", ex);
            } finally {
                try {
                    osa.flush();
                    osa.close();
                } catch (Exception ex) {
                    throw new RuntimeException("Error ocurred while attepmting to close the BufferedOutputStream osa", ex);
                }

                try {
                    osb.flush();
                    osb.close();
                } catch (Exception ex) {
                    throw new RuntimeException("Error ocurred while attepmting to close the BufferedOutputStream osb", ex);
                }
            }
        }

        LOG.info("end generateBatchPayeeForms: " + new Date());

        return retval;
    }

    @Override
    public File getTaxYearFolder(int year) {
        String folderName = configurationService.getPropertyValueAsString(KFSConstants.STAGING_DIRECTORY_KEY) + "/tax/" + year;
        File retval = new File(folderName);

        if (retval.exists() && retval.isDirectory()) {
            LOG.info("tax folder " + retval.getAbsolutePath() + " already exists.");
        } else if (!retval.mkdirs()) {
            throw new RuntimeException("Could not create folder at [" + retval.getAbsolutePath() + "].");
        } else {
            LOG.info("tax folder " + retval.getAbsolutePath() + " created.");
        }
        return retval;
    }

    @Override
    public boolean createPdfFile(Integer taxYear, int pageNum, OutputStream os, Payer payer, List<Payee> payees) {
        boolean success = false;
        PdfReader reader = getPdfReader(taxYear.intValue());
        Document document = null;
        PdfCopy copy = null;
        try {
            document = new Document(reader.getPageSizeWithRotation(1));
            copy = new PdfCopy(document, os);
            document.open();
            for (Payee payee : payees) {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                getPdfFilePage(reader, pageNum, bos, payer, payee);
                PdfReader r = new PdfReader(bos.toByteArray());
                copy.addPage(copy.getImportedPage(r, 1));
                copy.freeReader(r);
            }
            success = true;
        } catch (Exception ex) {
            LOG.error(ex.getMessage());
        } finally {
            try {
                copy.close();
            } catch (Exception ex) {
                LOG.error(ex.getMessage());
            }
            try {
                document.close();
            } catch (Exception ex) {
                LOG.error(ex.getMessage());
            }
            try {
                reader.close();
            } catch (Exception ex) {
                LOG.error(ex.getMessage());
            }
        }
        return success;
    }

    // Internal Service Methods

    private PdfReader getPdfReader(Integer year) {
        PdfReader retval = null;
        FileInputStream fispdf = null;
        try {
            File pdfFile = get1099PdfFile(year);
            fispdf = new FileInputStream(pdfFile);
            retval = new PdfReader(fispdf);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            try {
                fispdf.close();
                retval.close();
            } catch (Exception ex) {
                LOG.error("Error closing FileInputStream in TaxForm1099GeneratorServiceImpl#getPdfReader().\n" + ex.getMessage());
            }
        }
        return retval;
    }

    private File get1099PdfFile(Integer year) {
        File retval = null;
        String fname = pdfDirectory + "/" + TaxConstants.Form1099.PDF_1099_FILENAME_PREFIX + "-" + year + KFSConstants.ReportGeneration.PDF_FILE_EXTENSION;
        LOG.info("pdfDirectory: " + pdfDirectory);
        retval = new File(fname);
        if (!retval.exists() || !retval.isFile() || retval.length() <= 0) {
            throw new RuntimeException("The IRS 1099 Misc PDF [" + retval.getAbsolutePath() + "] is not available in the file system.");
        }
        LOG.info("1099 PDF filename = " + retval.getAbsolutePath());
        return retval;
    }

    private void getPdfFilePage(PdfReader reader, int pageNum, OutputStream os, Payer payer, Payee payee) throws Exception {

        if (LOG.isDebugEnabled()) {
            int sz = 0;
            if (payee.getPayments() != null) {
                sz = payee.getPayments().size();
            }
            LOG.debug("creating pdf for payee " + payee.getVendorNumber() + ", #payments=" + sz);
        }

        PdfStamper stamper = null;
        Document document = null;
        PdfCopy copy = null;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            document = new Document(reader.getPageSizeWithRotation(1));

            copy = new PdfCopy(document, bos);

            document.open();

            PdfImportedPage page = copy.getImportedPage(reader, pageNum);
            copy.addPage(page);
            document.close();

            PdfReader nr = new PdfReader(bos.toByteArray());
            stamper = new PdfStamper(nr, os);

            // Always page 1
            addPageText(stamper, 1, payer, payee);

            stamper.close();
        } catch (Exception ex) {
            LOG.error("Error in TaxForm1099GeneratorServiceImpl#getPdfFilePage.\n" + ex.getMessage());
        }

        finally {
            try {
                copy.close();
            } catch (Exception ex) {
                LOG.error("Error in TaxForm1099GeneratorServiceImpl#getPdfFilePage.\n" + ex.getMessage());
            }

            try {
                document.close();
            } catch (Exception ex) {
                LOG.error("Error in TaxForm1099GeneratorServiceImpl#getPdfFilePage.\n" + ex.getMessage());
            }

            try {
                stamper.close();
            } catch (Exception ex) {
                LOG.error("Error in TaxForm1099GeneratorServiceImpl#getPdfFilePage.\n" + ex.getMessage());
            }

        }
    }

    private void addPageText(PdfStamper stamper, int pageNum, Payer payer, Payee payee) throws Exception {
        BaseFont font = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);

        int x = getLocationX(TaxParameterConstants.Form1099Paramaters.PAYER);
        int y = getLocationY(TaxParameterConstants.Form1099Paramaters.PAYER);

        PdfContentByte pcb = stamper.getOverContent(pageNum);
        addText(pcb, font, TaxConstants.Form1099.FORM_1099_DEFAULT_FONT_SIZE, payer.getName1() + " " + (payer.getName2() == null ? KFSConstants.EMPTY_STRING : payer.getName2()), x, y);
        addText(pcb, font, TaxConstants.Form1099.FORM_1099_DEFAULT_FONT_SIZE, payer.getAddress(), x, y - 12);
        addText(pcb, font, TaxConstants.Form1099.FORM_1099_DEFAULT_FONT_SIZE, payer.getCity() + ", " + payer.getState() + " " + payer.getZipCode(), x, y - 24);
        addText(pcb, font, TaxConstants.Form1099.FORM_1099_DEFAULT_FONT_SIZE, payer.getPhoneNumber(), x, y - 36);

        // Payer TIN
        addText(pcb, font, TaxConstants.Form1099.FORM_1099_DEFAULT_FONT_SIZE, payer.getTin(), getLocationX(TaxParameterConstants.Form1099Paramaters.PAYER_TAX_ID), getLocationY(TaxParameterConstants.Form1099Paramaters.PAYER_TAX_ID));
        // Payee TIN
        addText(pcb, font, TaxConstants.Form1099.FORM_1099_DEFAULT_FONT_SIZE, (payee.getHeaderTaxNumber() == null ? KFSConstants.EMPTY_STRING : payee.getHeaderTaxNumber()), getLocationX(TaxParameterConstants.Form1099Paramaters.PAYEE_TAX_ID), getLocationY(TaxParameterConstants.Form1099Paramaters.PAYEE_TAX_ID));

        Integer taxYear = payee.getTaxYear();

        populate1099TaxBoxes(pcb, font, taxYear, payee);

        // State Tax
        addText(pcb, font, TaxConstants.Form1099.FORM_1099_DEFAULT_FONT_SIZE, getStateTaxWitheld(payee), getLocationX(TaxParameterConstants.Form1099Paramaters.WITHHELD), getLocationY(TaxParameterConstants.Form1099Paramaters.WITHHELD));
        // State No
        addText(pcb, font, TaxConstants.Form1099.FORM_1099_DEFAULT_FONT_SIZE, payer.getState(), getLocationX(TaxParameterConstants.Form1099Paramaters.STATE_NO), getLocationY(TaxParameterConstants.Form1099Paramaters.STATE_NO));
        // State Inc
        addText(pcb, font, TaxConstants.Form1099.FORM_1099_DEFAULT_FONT_SIZE, getStateIncome(payee), getLocationX(TaxParameterConstants.Form1099Paramaters.STATE_INCOME), getLocationY(TaxParameterConstants.Form1099Paramaters.STATE_INCOME));

        // Payee Name and Address
        x = getLocationX(TaxParameterConstants.Form1099Paramaters.PAYEE_ADDRESS);
        y = getLocationY(TaxParameterConstants.Form1099Paramaters.PAYEE_ADDRESS);

        addText(pcb, font, TaxConstants.Form1099.FORM_1099_DEFAULT_FONT_SIZE, payee.getVendorName(), getLocationX(TaxParameterConstants.Form1099Paramaters.PAYEE_NAME), getLocationY(TaxParameterConstants.Form1099Paramaters.PAYEE_NAME));

        if (Boolean.TRUE.equals(payee.getVendorForeignIndicator()) || !KFSConstants.COUNTRY_CODE_UNITED_STATES.equals(payee.getAddressCountryCode())) {
            String payeeForeignAddressLine = buildPayeeForeignAddressLine(payee);

            if (payeeForeignAddressLine.length() > TaxConstants.VENDOR_MAX_ADDRESS_LENGTH) {
                payeeForeignAddressLine = payeeForeignAddressLine.substring(0, TaxConstants.VENDOR_MAX_ADDRESS_LENGTH).trim();
            }

            addText(pcb, font, TaxConstants.Form1099.FORM_1099_DEFAULT_FONT_SIZE, payeeForeignAddressLine, x, y);
        } else {
            if (StringUtils.isNotBlank(payee.getAddressLine1Address()) && payee.getAddressLine1Address().startsWith(TaxConstants.DBA_BUSINESS_PREFIX)) {
                addText(pcb, font, TaxConstants.Form1099.FORM_1099_DEFAULT_FONT_SIZE, payee.getAddressLine1Address(), getLocationX(TaxParameterConstants.Form1099Paramaters.PAYEE_NAME), getLocationY(TaxParameterConstants.Form1099Paramaters.PAYEE_NAME) - 12);

                if (StringUtils.isNotBlank(payee.getAddressLine2Address()) && payee.getAddressLine2Address().contains(",")) {
                    addText(pcb, font, TaxConstants.Form1099.FORM_1099_DEFAULT_FONT_SIZE, payee.getAddressLine2Address().substring(0, payee.getAddressLine2Address().indexOf(",")), x, y);
                    addText(pcb, font, TaxConstants.Form1099.FORM_1099_DEFAULT_FONT_SIZE, payee.getAddressLine2Address().substring(payee.getAddressLine2Address().indexOf(",") + 1).trim(), x, y - 12);
                } else {
                    addText(pcb, font, TaxConstants.Form1099.FORM_1099_DEFAULT_FONT_SIZE, payee.getAddressLine2Address(), x, y);
                }
            } else {
                addText(pcb, font, TaxConstants.Form1099.FORM_1099_DEFAULT_FONT_SIZE, payee.getAddressLine1Address(), x, y);
                addText(pcb, font, TaxConstants.Form1099.FORM_1099_DEFAULT_FONT_SIZE, payee.getAddressLine2Address(), x, y - 12);
            }
            // Payee State, City, Zip
            addText(pcb, font, TaxConstants.Form1099.FORM_1099_DEFAULT_FONT_SIZE, payee.getAddressCityName() + ", " + convertNullToBlank(payee.getAddressStateCode()) + " " + getFormattedZip(payee.getAddressZipCode()), getLocationX(TaxParameterConstants.Form1099Paramaters.PAYEE_STATE_CITY_ZIP), getLocationY(TaxParameterConstants.Form1099Paramaters.PAYEE_STATE_CITY_ZIP));
        }

        // Account #
        addText(pcb, font, TaxConstants.Form1099.FORM_1099_DEFAULT_FONT_SIZE, payee.getVendorNumber(), getLocationX(TaxParameterConstants.Form1099Paramaters.ACCOUNT_NUM), getLocationY(TaxParameterConstants.Form1099Paramaters.ACCOUNT_NUM));

        // Sec 409a Deferrals
        addText(pcb, font, TaxConstants.Form1099.FORM_1099_DEFAULT_FONT_SIZE, taxPayeeService.getPayeeTaxAmount(payee, KFSConstants.IncomeTypeConstants.IncomeTypeAmountCodes.AMOUNT_CODE_D, taxYear).toString(), getLocationX(TaxParameterConstants.Form1099Paramaters.DEFERRALS), getLocationY(TaxParameterConstants.Form1099Paramaters.DEFERRALS));
        // Sec 409a Income
        addText(pcb, font, TaxConstants.Form1099.FORM_1099_DEFAULT_FONT_SIZE, taxPayeeService.getPayeeTaxAmount(payee, KFSConstants.IncomeTypeConstants.IncomeTypeAmountCodes.AMOUNT_CODE_E, taxYear).toString(), getLocationX(TaxParameterConstants.Form1099Paramaters.INCOME), getLocationY(TaxParameterConstants.Form1099Paramaters.INCOME));

        if (payee.getCorrectionIndicator().booleanValue() && pageNum == TaxConstants.Form1099.PDF_1099_PAGE_COPY_A) {
            addCorrectionIndicator(pcb, getLocationX(TaxParameterConstants.Form1099Paramaters.CORRECTION_IND), getLocationY(TaxParameterConstants.Form1099Paramaters.CORRECTION_IND));
        } else if (payee.getCorrectionIndicator().booleanValue() && pageNum == TaxConstants.Form1099.PDF_1099_PAGE_COPY_B) {
            addCorrectionIndicator(pcb, getLocationX(TaxParameterConstants.Form1099Paramaters.CORRECTION_IND), getLocationY(TaxParameterConstants.Form1099Paramaters.CORRECTION_IND));
        } else if (payee.getCorrectionIndicator().booleanValue() && pageNum == TaxConstants.Form1099.PDF_1099_PAGE_COPY_C) {
            addCorrectionIndicator(pcb, getLocationX(TaxParameterConstants.Form1099Paramaters.CORRECTION_IND), getLocationY(TaxParameterConstants.Form1099Paramaters.CORRECTION_IND));
        } else if (payee.getCorrectionIndicator().booleanValue() && pageNum == TaxConstants.Form1099.PDF_1099_PAGE_COPY_1) {
            addCorrectionIndicator(pcb, getLocationX(TaxParameterConstants.Form1099Paramaters.CORRECTION_IND), getLocationY(TaxParameterConstants.Form1099Paramaters.CORRECTION_IND));
        } else if (payee.getCorrectionIndicator().booleanValue() && pageNum == TaxConstants.Form1099.PDF_1099_PAGE_COPY_2) {
            addCorrectionIndicator(pcb, getLocationX(TaxParameterConstants.Form1099Paramaters.CORRECTION_IND), getLocationY(TaxParameterConstants.Form1099Paramaters.CORRECTION_IND));
        }
    }

    private int getLocationX(String parm) {
        int retval = 0;
        String value = parameterService.getParameterValueAsString(TaxConstants.TAX_NAMESPACE_CODE, TaxConstants.PAYEE_MASTER_EXTRACT_STEP, parm);

        if (StringUtils.isNotBlank(value)) {
            String[] s = value.split(KFSConstants.MULTI_VALUE_SEPERATION_CHARACTER);

            if ((s == null) || (s.length < 1)) {
                throw new RuntimeException(parm + " is not a valid location");
            } else {
                retval = Integer.parseInt(s[0]);
            }
        } else {
            throw new RuntimeException(parm + " is not a valid location");
        }

        return retval;
    }

    private int getLocationY(String parm) {
        int retval = 0;
        String value = parameterService.getParameterValueAsString(TaxConstants.TAX_NAMESPACE_CODE, TaxConstants.PAYEE_MASTER_EXTRACT_STEP, parm);

        if (StringUtils.isNotBlank(value)) {
            String[] s = value.split(KFSConstants.MULTI_VALUE_SEPERATION_CHARACTER);

            if ((s == null) || (s.length < 2)) {
                throw new RuntimeException(parm + " is not a valid location");
            } else {
                retval = Integer.parseInt(s[1]);
            }
        } else {
            throw new RuntimeException(parm + " is not a valid location");
        }

        return retval;
    }

    private void addText(PdfContentByte pcb, BaseFont bf, int fontSize, String text, int x, int y) {
        pcb.beginText();
        pcb.setFontAndSize(bf, fontSize);
        pcb.setTextMatrix(x, y);
        text = convertNullToBlank(text);
        pcb.showText(text);
        pcb.endText();
    }

    private void populate1099TaxBoxes(PdfContentByte pcb, BaseFont font, int taxYear, Payee payee) {
        Map<String, String> boxMap = taxPayeeService.getForm1099Boxes();
        for (String box : boxMap.keySet()) {
            String param = boxMap.get(box);

            if (param != null) {
                KualiDecimal taxAmount = taxPayeeService.getPayeeTaxAmount(payee, box, taxYear);
                addText(pcb, font, TaxConstants.Form1099.FORM_1099_DEFAULT_FONT_SIZE, taxAmount.toString(), getLocationX(param), getLocationY(param));
            }
        }
    }

    private String getStateTaxWitheld(Payee payee) {
        return KFSConstants.EMPTY_STRING;
    }

    private String getStateIncome(Payee payee) {
        return KFSConstants.EMPTY_STRING;
    }

    private String buildPayeeForeignAddressLine(Payee payee) {
        StringBuilder retval = new StringBuilder(256);

        if (StringUtils.isNotBlank(payee.getAddressLine1Address())) {
            retval.append(payee.getAddressLine1Address().trim());
            retval.append(" ");
        }

        if (StringUtils.isNotBlank(payee.getAddressLine2Address())) {
            retval.append(payee.getAddressLine2Address().trim());
            retval.append(" ");
        }

        if (StringUtils.isNotBlank(payee.getAddressCityName())) {
            retval.append(payee.getAddressCityName().trim());
            retval.append(" ");
        }

        if (StringUtils.isNotBlank(payee.getAddressStateCode())) {
            retval.append(payee.getAddressStateCode().trim());
            retval.append(" ");
        }

        if (StringUtils.isNotBlank(payee.getAddressZipCode())) {
            retval.append(getFormattedZip(payee.getAddressZipCode()));
            retval.append(" ");
        }

        if (StringUtils.isNotBlank(payee.getAddressCountryCode())) {
            retval.append(payee.getAddressCountryCode().trim());
        }

        return retval.toString().trim();
    }

    private String getFormattedZip(String input) {
        String s = convertNullToBlank(input);
        StringBuilder retval = new StringBuilder(9);

        if (s.length() == 9) {
            retval.append(s.substring(0, 5));
            retval.append("-");
            retval.append(s.substring(5, 9));
        } else {
            retval.append(s);
        }
        return retval.toString();
    }

    private String convertNullToBlank(String input) {
        String retval = input;
        if (retval == null) {
            retval = KFSConstants.EMPTY_STRING;
        }
        return retval;
    }

    private void addCorrectionIndicator(PdfContentByte pcb, int x, int y) {
        pcb.moveTo(x, y);
        pcb.lineTo(x + TaxConstants.Form1099.FORM_1099_CORRECTION_INDICATOR_LENGTH, y + TaxConstants.Form1099.FORM_1099_CORRECTION_INDICATOR_LENGTH);
        pcb.stroke();
        pcb.moveTo(x, y + TaxConstants.Form1099.FORM_1099_CORRECTION_INDICATOR_LENGTH);
        pcb.lineTo(x + TaxConstants.Form1099.FORM_1099_CORRECTION_INDICATOR_LENGTH, y);
        pcb.stroke();
    }

}
