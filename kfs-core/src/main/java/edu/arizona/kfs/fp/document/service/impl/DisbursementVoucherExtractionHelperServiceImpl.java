package edu.arizona.kfs.fp.document.service.impl;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.businessobject.DisbursementVoucherNonEmployeeExpense;
import org.kuali.kfs.fp.businessobject.DisbursementVoucherNonEmployeeTravel;
import org.kuali.kfs.fp.businessobject.DisbursementVoucherPayeeDetail;
import org.kuali.kfs.fp.businessobject.DisbursementVoucherPreConferenceDetail;
import org.kuali.kfs.fp.businessobject.DisbursementVoucherPreConferenceRegistrant;
import org.kuali.kfs.fp.document.DisbursementVoucherConstants;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.fp.document.service.DisbursementVoucherTaxService;
import org.kuali.kfs.pdp.PdpParameterConstants;
import org.kuali.kfs.pdp.businessobject.PaymentAccountDetail;
import org.kuali.kfs.pdp.businessobject.PaymentDetail;
import org.kuali.kfs.pdp.businessobject.PaymentNoteText;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class DisbursementVoucherExtractionHelperServiceImpl extends org.kuali.kfs.fp.document.service.impl.DisbursementVoucherExtractionHelperServiceImpl {
    public static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DisbursementVoucherExtractionHelperServiceImpl.class);

    @Override
    protected PaymentDetail buildPaymentDetail(DisbursementVoucherDocument document, Date processRunDate) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("buildPaymentDetail() started");
        }
        final String maxNoteLinesParam = getParameterService().getParameterValueAsString(KfsParameterConstants.PRE_DISBURSEMENT_ALL.class, PdpParameterConstants.MAX_NOTE_LINES);

        int maxNoteLines;
        try {
            maxNoteLines = Integer.parseInt(maxNoteLinesParam);
        } catch (NumberFormatException nfe) {
            throw new IllegalArgumentException("Invalid Max Notes Lines parameter, value: " + maxNoteLinesParam + " cannot be converted to an integer");
        }

        PaymentDetail pd = new PaymentDetail();
        if (StringUtils.isNotEmpty(document.getDocumentHeader().getOrganizationDocumentNumber())) {
            pd.setOrganizationDocNbr(document.getDocumentHeader().getOrganizationDocumentNumber());
        }
        pd.setCustPaymentDocNbr(document.getDocumentNumber());
        pd.setInvoiceDate(new java.sql.Date(processRunDate.getTime()));
        pd.setOrigInvoiceAmount(document.getDisbVchrCheckTotalAmount());
        pd.setInvTotDiscountAmount(KualiDecimal.ZERO);
        pd.setInvTotOtherCreditAmount(KualiDecimal.ZERO);
        pd.setInvTotOtherDebitAmount(KualiDecimal.ZERO);
        pd.setInvTotShipAmount(KualiDecimal.ZERO);
        pd.setNetPaymentAmount(document.getDisbVchrCheckTotalAmount());
        pd.setPrimaryCancelledPayment(Boolean.FALSE);
        pd.setFinancialDocumentTypeCode(DisbursementVoucherConstants.DOCUMENT_TYPE_CHECKACH);
        pd.setFinancialSystemOriginCode(KFSConstants.ORIGIN_CODE_KUALI);

        KualiDecimal discountAmount = KualiDecimal.ZERO;

        // Handle accounts
        for (SourceAccountingLine sal : (List<? extends SourceAccountingLine>) document.getSourceAccountingLines()) {
            KualiDecimal remitAmount = sal.getAmount();
            KualiDecimal dvnraTaxAmount = SpringContext.getBean(DisbursementVoucherTaxService.class).getNonResidentAlienTaxAmount(document);

            if (!KualiDecimal.ZERO.equals(dvnraTaxAmount)) {
                if (document.getDvNonResidentAlienTax() != null && document.getDvNonResidentAlienTax().getFinancialDocumentAccountingLineText() != null && document.getDvNonResidentAlienTax().getFinancialDocumentAccountingLineText().contains(sal.getSequenceNumber().toString())) {
                    if (sal.getAmount().isNegative()) {
                        discountAmount = discountAmount.add(sal.getAmount());
                    }
                    continue;
                } else {
                    BigDecimal accountAmount = sal.getAmount().bigDecimalValue();
                    BigDecimal taxPercentWhole = document.getDvNonResidentAlienTax().getFederalIncomeTaxPercent().add(document.getDvNonResidentAlienTax().getStateIncomeTaxPercent()).bigDecimalValue();
                    BigDecimal taxPercent = taxPercentWhole.divide(new BigDecimal(100), 5, BigDecimal.ROUND_HALF_UP);
                    KualiDecimal withholdingAmount = new KualiDecimal(accountAmount.multiply(taxPercent).setScale(KualiDecimal.SCALE, KualiDecimal.ROUND_BEHAVIOR));
                    remitAmount = new KualiDecimal(accountAmount).subtract(withholdingAmount);
                }
            }

            PaymentAccountDetail pad = new PaymentAccountDetail();
            pad.setFinChartCode(sal.getChartOfAccountsCode());
            pad.setAccountNbr(sal.getAccountNumber());
            if (StringUtils.isNotEmpty(sal.getSubAccountNumber())) {
                pad.setSubAccountNbr(sal.getSubAccountNumber());
            } else {
                pad.setSubAccountNbr(KFSConstants.getDashSubAccountNumber());
            }
            pad.setFinObjectCode(sal.getFinancialObjectCode());
            if (StringUtils.isNotEmpty(sal.getFinancialSubObjectCode())) {
                pad.setFinSubObjectCode(sal.getFinancialSubObjectCode());
            } else {
                pad.setFinSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
            }
            if (StringUtils.isNotEmpty(sal.getOrganizationReferenceId())) {
                pad.setOrgReferenceId(sal.getOrganizationReferenceId());
            }
            if (StringUtils.isNotEmpty(sal.getProjectCode())) {
                pad.setProjectCode(sal.getProjectCode());
            } else {
                pad.setProjectCode(KFSConstants.getDashProjectCode());
            }
            pad.setAccountNetAmount(remitAmount);
            pd.addAccountDetail(pad);
        }
        pd.setInvTotDiscountAmount(discountAmount);

        // Handle notes
        DisbursementVoucherPayeeDetail dvpd = document.getDvPayeeDetail();

        int line = 0;
        PaymentNoteText pnt = new PaymentNoteText();
        pnt.setCustomerNoteLineNbr(new KualiInteger(line++));
        pnt.setCustomerNoteText("Info: " + document.getDisbVchrContactPersonName() + " " + document.getDisbVchrContactPhoneNumber());
        pd.addNote(pnt);

        String dvSpecialHandlingPersonName = null;
        String dvSpecialHandlingLine1Address = null;
        String dvSpecialHandlingLine2Address = null;
        String dvSpecialHandlingCity = null;
        String dvSpecialHandlingState = null;
        String dvSpecialHandlingZip = null;

        dvSpecialHandlingPersonName = dvpd.getDisbVchrSpecialHandlingPersonName();
        dvSpecialHandlingLine1Address = dvpd.getDisbVchrSpecialHandlingLine1Addr();
        dvSpecialHandlingLine2Address = dvpd.getDisbVchrSpecialHandlingLine2Addr();
        dvSpecialHandlingCity = dvpd.getDisbVchrSpecialHandlingCityName();
        dvSpecialHandlingState = dvpd.getDisbVchrSpecialHandlingStateCode();
        dvSpecialHandlingZip = dvpd.getDisbVchrSpecialHandlingZipCode();

        if (StringUtils.isNotEmpty(dvSpecialHandlingPersonName)) {
            pnt = new PaymentNoteText();
            pnt.setCustomerNoteLineNbr(new KualiInteger(line++));
            pnt.setCustomerNoteText("Send Check To: " + dvSpecialHandlingPersonName);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Creating special handling person name note: " + pnt.getCustomerNoteText());
            }
            pd.addNote(pnt);
        }
        if (StringUtils.isNotEmpty(dvSpecialHandlingLine1Address)) {
            pnt = new PaymentNoteText();
            pnt.setCustomerNoteLineNbr(new KualiInteger(line++));
            pnt.setCustomerNoteText(dvSpecialHandlingLine1Address);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Creating special handling address 1 note: " + pnt.getCustomerNoteText());
            }
            pd.addNote(pnt);
        }
        if (StringUtils.isNotEmpty(dvSpecialHandlingLine2Address)) {
            pnt = new PaymentNoteText();
            pnt.setCustomerNoteLineNbr(new KualiInteger(line++));
            pnt.setCustomerNoteText(dvSpecialHandlingLine2Address);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Creating special handling address 2 note: " + pnt.getCustomerNoteText());
            }
            pd.addNote(pnt);
        }
        if (StringUtils.isNotEmpty(dvSpecialHandlingCity)) {
            pnt = new PaymentNoteText();
            pnt.setCustomerNoteLineNbr(new KualiInteger(line++));
            pnt.setCustomerNoteText(dvSpecialHandlingCity + ", " + dvSpecialHandlingState + " " + dvSpecialHandlingZip);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Creating special handling city note: " + pnt.getCustomerNoteText());
            }
            pd.addNote(pnt);
        }
        if (document.isDisbVchrAttachmentCode()) {
            pnt = new PaymentNoteText();
            pnt.setCustomerNoteLineNbr(new KualiInteger(line++));
            pnt.setCustomerNoteText("Attachment Included");
            if (LOG.isDebugEnabled()) {
                LOG.debug("create attachment note: " + pnt.getCustomerNoteText());
            }
            pd.addNote(pnt);
        }

        String paymentReasonCode = dvpd.getDisbVchrPaymentReasonCode();
        if (/* REFACTORME */getParameterEvaluatorService().getParameterEvaluator(DisbursementVoucherDocument.class, DisbursementVoucherConstants.NONEMPLOYEE_TRAVEL_PAY_REASONS_PARM_NM, paymentReasonCode).evaluationSucceeds()) {
            DisbursementVoucherNonEmployeeTravel dvnet = document.getDvNonEmployeeTravel();

            pnt = new PaymentNoteText();
            pnt.setCustomerNoteLineNbr(new KualiInteger(line++));
            pnt.setCustomerNoteText("Reimbursement associated with " + dvnet.getDisbVchrServicePerformedDesc());
            if (LOG.isDebugEnabled()) {
                LOG.debug("Creating non employee travel notes: " + pnt.getCustomerNoteText());
            }
            pd.addNote(pnt);

            pnt = new PaymentNoteText();
            pnt.setCustomerNoteLineNbr(new KualiInteger(line++));
            pnt.setCustomerNoteText("The total per diem amount for your daily expenses is " + dvnet.getDisbVchrPerdiemCalculatedAmt());
            if (LOG.isDebugEnabled()) {
                LOG.debug("Creating non employee travel notes: " + pnt.getCustomerNoteText());
            }
            pd.addNote(pnt);

            if (dvnet.getDisbVchrPersonalCarAmount() != null && dvnet.getDisbVchrPersonalCarAmount().compareTo(KualiDecimal.ZERO) != 0) {
                pnt = new PaymentNoteText();
                pnt.setCustomerNoteLineNbr(new KualiInteger(line++));
                pnt.setCustomerNoteText("The total dollar amount for your vehicle mileage is " + dvnet.getDisbVchrPersonalCarAmount());
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Creating non employee travel vehicle note: " + pnt.getCustomerNoteText());
                }
                pd.addNote(pnt);

                for (DisbursementVoucherNonEmployeeExpense exp : (List<DisbursementVoucherNonEmployeeExpense>) dvnet.getDvNonEmployeeExpenses()) {
                    if (line < (maxNoteLines - 8)) {
                        pnt = new PaymentNoteText();
                        pnt.setCustomerNoteLineNbr(new KualiInteger(line++));
                        pnt.setCustomerNoteText(exp.getDisbVchrExpenseCompanyName() + " " + exp.getDisbVchrExpenseAmount());
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Creating non employee travel expense note: " + pnt.getCustomerNoteText());
                        }
                        pd.addNote(pnt);
                    }
                }
            }
        } else if (/* REFACTORME */getParameterEvaluatorService().getParameterEvaluator(DisbursementVoucherDocument.class, DisbursementVoucherConstants.PREPAID_TRAVEL_PAYMENT_REASONS_PARM_NM, paymentReasonCode).evaluationSucceeds()) {
            pnt = new PaymentNoteText();
            pnt.setCustomerNoteLineNbr(new KualiInteger(line++));
            pnt.setCustomerNoteText("Payment is for the following individuals/charges:");
            pd.addNote(pnt);
            if (LOG.isDebugEnabled()) {
                LOG.info("Creating prepaid travel note note: " + pnt.getCustomerNoteText());
            }

            DisbursementVoucherPreConferenceDetail dvpcd = document.getDvPreConferenceDetail();

            for (DisbursementVoucherPreConferenceRegistrant dvpcr : (List<DisbursementVoucherPreConferenceRegistrant>) dvpcd.getDvPreConferenceRegistrants()) {
                if (line < (maxNoteLines - 8)) {
                    pnt = new PaymentNoteText();
                    pnt.setCustomerNoteLineNbr(new KualiInteger(line++));
                    pnt.setCustomerNoteText(dvpcr.getDvConferenceRegistrantName() + " " + dvpcr.getDisbVchrExpenseAmount());
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Creating pre-paid conference registrants note: " + pnt.getCustomerNoteText());
                    }
                    pd.addNote(pnt);
                }
            }
        }

        // Get the original, raw form, note text from the DV document.
        final String text = document.getDisbVchrCheckStubText();
        if (!StringUtils.isBlank(text)) {
            pnt = this.getPaymentSourceHelperService().buildNoteForCheckStubText(text, line);
            // Logging...
            if (LOG.isDebugEnabled()) {
                LOG.debug("Creating check stub text note: " + pnt.getCustomerNoteText());
            }
            pd.addNote(pnt);
        }

        return pd;
    }

}
