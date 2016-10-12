package edu.arizona.kfs.fp.document.service.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

import edu.arizona.kfs.fp.document.DisbursementVoucherConstants;
import edu.arizona.kfs.sys.businessobject.options.PaymentMethodValuesFinder;

import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.fp.businessobject.PaymentReasonCode;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.parameter.ParameterEvaluatorService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;

import org.apache.commons.lang.StringUtils;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;

public class DisbursementVoucherCoverSheetServiceImpl extends org.kuali.kfs.fp.document.service.impl.DisbursementVoucherCoverSheetServiceImpl {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DisbursementVoucherCoverSheetServiceImpl.class);

    protected ParameterEvaluatorService parameterEvaluatorService;
    
    /**
     * remove the line, bar and rline fields from PDF template and move the payee name, document number and payment reason to lower part
     */
    @Override
    public void generateDisbursementVoucherCoverSheet(String templateDirectory, String templateName, DisbursementVoucherDocument document, OutputStream outputStream) throws DocumentException, IOException {
        if (!this.isCoverSheetPrintable(document)) {
            return;
        }

        ParameterService parameterService = SpringContext.getBean(ParameterService.class);

        String attachment = "";
        String handling = "";
        String alien = "";

        String docNumber = document.getDocumentNumber();
        String initiator = document.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId();
        String payee = document.getDvPayeeDetail().getDisbVchrPayeePersonName();

        String reason = ((PaymentReasonCode) businessObjectService.findBySinglePrimaryKey(PaymentReasonCode.class, document.getDvPayeeDetail().getDisbVchrPaymentReasonCode())).getName();
        reason = StringUtils.replace(reason, "/", " / ");
        
        String check_total = document.getDisbVchrCheckTotalAmount().toString();

        String address = retrieveAddress(document.getDisbursementVoucherDocumentationLocationCode());

        // retrieve attachment label
        if (document.isDisbVchrAttachmentCode()) {
            attachment = parameterService.getParameterValueAsString(DisbursementVoucherDocument.class, DisbursementVoucherConstants.DV_COVER_SHEET_TEMPLATE_ATTACHMENT_PARM_NM);
        }
        // retrieve handling label
        if (document.isDisbVchrSpecialHandlingCode()) {
            handling = parameterService.getParameterValueAsString(DisbursementVoucherDocument.class, DisbursementVoucherConstants.DV_COVER_SHEET_TEMPLATE_HANDLING_PARM_NM);
        }
        // retrieve data for alien payment code
        if (document.getDvPayeeDetail().isDisbVchrAlienPaymentCode()) {
            String taxDocumentationLocationCode = parameterService.getParameterValueAsString(DisbursementVoucherDocument.class, DisbursementVoucherConstants.TAX_DOCUMENTATION_LOCATION_CODE_PARM_NM);

            address = retrieveAddress(taxDocumentationLocationCode);
            alien = parameterService.getParameterValueAsString(DisbursementVoucherDocument.class, DisbursementVoucherConstants.DV_COVER_SHEET_TEMPLATE_ALIEN_PARM_NM);
        }

        // determine if non-employee travel payment reasons
        String paymentReasonCode = document.getDvPayeeDetail().getDisbVchrPaymentReasonCode();
        
        String rlines = "";
		if (parameterService.parameterExists(DisbursementVoucherDocument.class, DisbursementVoucherConstants.RLINES_PAYMENT_REASONS_PARM_NM) 
			&& parameterEvaluatorService.getParameterEvaluator(DisbursementVoucherDocument.class, DisbursementVoucherConstants.RLINES_PAYMENT_REASONS_PARM_NM, paymentReasonCode).evaluationSucceeds()) {
		    rlines = parameterService.getParameterValueAsString(DisbursementVoucherDocument.class, DisbursementVoucherConstants.DV_COVER_SHEET_TEMPLATE_RLINES_PARM_NM);
		}
		
        try {

            URL url = new URL(templateDirectory + templateName);
            PdfReader reader = new PdfReader(url);

            // populate form with document values
            PdfStamper stamper = new PdfStamper(reader, outputStream);

            AcroFields populatedCoverSheet = stamper.getAcroFields();
            populatedCoverSheet.setField("initiator", initiator);
            populatedCoverSheet.setField("attachment", attachment);
            populatedCoverSheet.setField("handling", handling);
            populatedCoverSheet.setField("alien", alien);
            populatedCoverSheet.setField("payee_name", payee);
            populatedCoverSheet.setField("check_total", check_total);
            populatedCoverSheet.setField("docNumber", docNumber);
            populatedCoverSheet.setField("payment_reason", reason);
            populatedCoverSheet.setField("destination_address", address);
            populatedCoverSheet.setField("rlines", rlines);

            stamper.setFormFlattening(true);
            stamper.close();
        }
        catch (DocumentException e) {
            LOG.error("Error creating coversheet for: " + docNumber + ". ::" + e);
            throw e;
        }
        catch (IOException e) {
            LOG.error("Error creating coversheet for: " + docNumber + ". ::" + e);
            throw e;
        }
    }
    
    
    public void setParameterEvaluatorService(ParameterEvaluatorService parameterEvaluatorService) {
        this.parameterEvaluatorService = parameterEvaluatorService;
    }
}
