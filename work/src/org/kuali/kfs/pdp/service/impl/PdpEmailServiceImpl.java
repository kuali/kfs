/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.pdp.service.impl;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.pdp.PdpKeyConstants;
import org.kuali.kfs.pdp.PdpParameterConstants;
import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.kfs.pdp.batch.ExtractAchPaymentsStep;
import org.kuali.kfs.pdp.batch.LoadPaymentsStep;
import org.kuali.kfs.pdp.businessobject.ACHBank;
import org.kuali.kfs.pdp.businessobject.Batch;
import org.kuali.kfs.pdp.businessobject.CustomerProfile;
import org.kuali.kfs.pdp.businessobject.PaymentDetail;
import org.kuali.kfs.pdp.businessobject.PaymentFileLoad;
import org.kuali.kfs.pdp.businessobject.PaymentGroup;
import org.kuali.kfs.pdp.businessobject.PaymentNoteText;
import org.kuali.kfs.pdp.service.AchBankService;
import org.kuali.kfs.pdp.service.CustomerProfileService;
import org.kuali.kfs.pdp.service.PdpEmailService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.mail.InvalidAddressException;
import org.kuali.rice.kns.mail.MailMessage;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.service.MailService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.ErrorMessage;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.MessageMap;
import org.kuali.rice.kns.web.format.CurrencyFormatter;
import org.kuali.rice.kns.web.format.Formatter;

/**
 * @see org.kuali.kfs.pdp.service.PdpEmailService
 */
public class PdpEmailServiceImpl implements PdpEmailService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PdpEmailServiceImpl.class);

    private CustomerProfileService customerProfileService;
    private KualiConfigurationService kualiConfigurationService;
    private MailService mailService;
    private ParameterService parameterService;
    private DataDictionaryService dataDictionaryService;
    private AchBankService achBankService;

    /**
     * @see org.kuali.kfs.pdp.service.PdpEmailService#sendErrorEmail(org.kuali.kfs.pdp.businessobject.PaymentFileLoad,
     *      org.kuali.rice.kns.util.ErrorMap)
     */
    public void sendErrorEmail(PaymentFileLoad paymentFile, MessageMap errors) {
        LOG.debug("sendErrorEmail() starting");

        // check email configuration
        if (!isPaymentEmailEnabled()) {
            return;
        }

        MailMessage message = new MailMessage();

        message.setFromAddress(mailService.getBatchMailingList());
        message.setSubject(getEmailSubject(PdpParameterConstants.PAYMENT_LOAD_FAILURE_EMAIL_SUBJECT_PARAMETER_NAME));

        StringBuffer body = new StringBuffer();
        List<String> ccAddresses = parameterService.getParameterValues(LoadPaymentsStep.class, PdpParameterConstants.HARD_EDIT_CC);

        if (paymentFile == null) {
            if (ccAddresses.isEmpty()) {
                LOG.error("sendErrorEmail() No HARD_EDIT_CC addresses.  No email sent");
                return;
            }

            message.getToAddresses().addAll(ccAddresses);

            body.append(getMessage(PdpKeyConstants.MESSAGE_PAYMENT_EMAIL_BAD_FILE_PARSE) + "\n\n");
        }
        else {
            CustomerProfile customer = customerProfileService.get(paymentFile.getChart(), paymentFile.getUnit(), paymentFile.getSubUnit());
            if (customer == null) {
                LOG.error("sendErrorEmail() Invalid Customer.  Sending email to CC addresses");

                if (ccAddresses.isEmpty()) {
                    LOG.error("sendErrorEmail() No HARD_EDIT_CC addresses.  No email sent");
                    return;
                }

                message.getToAddresses().addAll(ccAddresses);

                body.append(getMessage(PdpKeyConstants.MESSAGE_PAYMENT_EMAIL_INVALID_CUSTOMER) + "\n\n");
            }
            else {
                String toAddresses = StringUtils.deleteWhitespace(customer.getProcessingEmailAddr());
                List<String> toAddressList = Arrays.asList(toAddresses.split(","));

                message.getToAddresses().addAll(toAddressList);
                message.getCcAddresses().addAll(ccAddresses);
                //TODO: for some reason the mail service does not work unless the bcc list has addresss. This is a temporary workaround
                message.getBccAddresses().addAll(ccAddresses);
            }
        }

        if (paymentFile != null) {
            body.append(getMessage(PdpKeyConstants.MESSAGE_PAYMENT_EMAIL_FILE_NOT_LOADED) + "\n\n");
            addPaymentFieldsToBody(body, null, paymentFile.getChart(), paymentFile.getUnit(), paymentFile.getSubUnit(), paymentFile.getCreationDate(), paymentFile.getPaymentCount(), paymentFile.getPaymentTotalAmount());
        }

        body.append("\n" + getMessage(PdpKeyConstants.MESSAGE_PAYMENT_EMAIL_ERROR_MESSAGES) + "\n");
        List<ErrorMessage> errorMessages = errors.getMessages(KFSConstants.GLOBAL_ERRORS);
        for (ErrorMessage errorMessage : errorMessages) {
            body.append(getMessage(errorMessage.getErrorKey(), (Object[]) errorMessage.getMessageParameters()) + "\n\n");
        }

        message.setMessage(body.toString());

        try {
            mailService.sendMessage(message);
        }
        catch (InvalidAddressException e) {
            LOG.error("sendErrorEmail() Invalid email address.  Message not sent", e);
        }
    }

    /**
     * @see org.kuali.kfs.pdp.service.PdpEmailService#sendLoadEmail(org.kuali.kfs.pdp.businessobject.PaymentFileLoad,
     *      java.util.List)
     */
    public void sendLoadEmail(PaymentFileLoad paymentFile, List<String> warnings) {
        LOG.debug("sendLoadEmail() starting");

        // check email configuration
        if (!isPaymentEmailEnabled()) {
            return;
        }

        MailMessage message = new MailMessage();

        message.setFromAddress(mailService.getBatchMailingList());
        message.setSubject(getEmailSubject(PdpParameterConstants.PAYMENT_LOAD_SUCCESS_EMAIL_SUBJECT_PARAMETER_NAME));

        List<String> ccAddresses = parameterService.getParameterValues(LoadPaymentsStep.class, PdpParameterConstants.HARD_EDIT_CC);
        message.getCcAddresses().addAll(ccAddresses);
        message.getBccAddresses().addAll(ccAddresses);

        CustomerProfile customer = customerProfileService.get(paymentFile.getChart(), paymentFile.getUnit(), paymentFile.getSubUnit());
        String toAddresses = StringUtils.deleteWhitespace(customer.getProcessingEmailAddr());
        List<String> toAddressList = Arrays.asList(toAddresses.split(","));

        message.getToAddresses().addAll(toAddressList);

        StringBuffer body = new StringBuffer();
        body.append(getMessage(PdpKeyConstants.MESSAGE_PAYMENT_EMAIL_FILE_LOADED) + "\n\n");
        addPaymentFieldsToBody(body, paymentFile.getBatchId().intValue(), paymentFile.getChart(), paymentFile.getUnit(), paymentFile.getSubUnit(), paymentFile.getCreationDate(), paymentFile.getPaymentCount(), paymentFile.getPaymentTotalAmount());

        body.append("\n" + getMessage(PdpKeyConstants.MESSAGE_PAYMENT_EMAIL_WARNING_MESSAGES) + "\n");
        for (String warning : warnings) {
            body.append(warning + "\n\n");
        }

        message.setMessage(body.toString());

        try {
            mailService.sendMessage(message);
        }
        catch (InvalidAddressException e) {
            LOG.error("sendErrorEmail() Invalid email address. Message not sent", e);
        }

        if (paymentFile.isFileThreshold()) {
            sendThresholdEmail(true, paymentFile, customer);
        }

        if (paymentFile.isDetailThreshold()) {
            sendThresholdEmail(false, paymentFile, customer);
        }
    }

    /**
     * Sends email for a payment that was over the customer file threshold or the detail threshold
     * 
     * @param fileThreshold indicates whether the file threshold (true) was violated or the detail threshold (false)
     * @param paymentFile parsed payment file object
     * @param customer payment customer
     */
    protected void sendThresholdEmail(boolean fileThreshold, PaymentFileLoad paymentFile, CustomerProfile customer) {
        MailMessage message = new MailMessage();

        message.setFromAddress(mailService.getBatchMailingList());
        message.setSubject(getEmailSubject(PdpParameterConstants.PAYMENT_LOAD_THRESHOLD_EMAIL_SUBJECT_PARAMETER_NAME));

        StringBuffer body = new StringBuffer();

        body.append(getMessage(PdpKeyConstants.MESSAGE_PAYMENT_EMAIL_FILE_LOADED) + "\n\n");
        addPaymentFieldsToBody(body, paymentFile.getBatchId().intValue(), paymentFile.getChart(), paymentFile.getUnit(), paymentFile.getSubUnit(), paymentFile.getCreationDate(), paymentFile.getPaymentCount(), paymentFile.getPaymentTotalAmount());

        if (fileThreshold) {
            String toAddresses = StringUtils.deleteWhitespace(customer.getFileThresholdEmailAddress());
            List<String> toAddressList = Arrays.asList(toAddresses.split(","));

            message.getToAddresses().addAll(toAddressList);
            body.append("\n" + getMessage(PdpKeyConstants.MESSAGE_PAYMENT_EMAIL_FILE_THRESHOLD, paymentFile.getPaymentTotalAmount(), customer.getFileThresholdAmount()));
        }
        else {
            String toAddresses = StringUtils.deleteWhitespace(customer.getPaymentThresholdEmailAddress());
            List<String> toAddressList = Arrays.asList(toAddresses.split(","));

            message.getToAddresses().addAll(toAddressList);

            body.append("\n" + getMessage(PdpKeyConstants.MESSAGE_PAYMENT_EMAIL_DETAIL_THRESHOLD, customer.getPaymentThresholdAmount()) + "\n\n");
            for (PaymentDetail paymentDetail : paymentFile.getThresholdPaymentDetails()) {
                paymentDetail.refresh();
                body.append(getMessage(PdpKeyConstants.MESSAGE_PAYMENT_EMAIL_FILE_THRESHOLD, paymentDetail.getPaymentGroup().getPayeeName(), paymentDetail.getNetPaymentAmount()) + "\n");
            }
        }
        
        List<String> ccAddresses = parameterService.getParameterValues(LoadPaymentsStep.class, PdpParameterConstants.HARD_EDIT_CC);
        message.getCcAddresses().addAll(ccAddresses);
        message.getBccAddresses().addAll(ccAddresses);

        message.setMessage(body.toString());

        try {
            mailService.sendMessage(message);
        }
        catch (InvalidAddressException e) {
            LOG.error("sendErrorEmail() Invalid email address. Message not sent", e);
        }
    }

    /**
     * @see org.kuali.kfs.pdp.service.PdpEmailService#sendTaxEmail(org.kuali.kfs.pdp.businessobject.PaymentFileLoad)
     */
    public void sendTaxEmail(PaymentFileLoad paymentFile) {
        LOG.debug("sendTaxEmail() starting");

        MailMessage message = new MailMessage();

        message.setFromAddress(mailService.getBatchMailingList());
        message.setSubject(getEmailSubject(PdpParameterConstants.PAYMENT_LOAD_TAX_EMAIL_SUBJECT_PARAMETER_NAME));

        StringBuffer body = new StringBuffer();

        String taxEmail = parameterService.getParameterValue(KfsParameterConstants.PRE_DISBURSEMENT_ALL.class, PdpParameterConstants.TAX_GROUP_EMAIL_ADDRESS);
        if (StringUtils.isBlank(taxEmail)) {
            LOG.error("No Tax E-mail Application Setting found to send notification e-mail");
            return;
        }
        else {
            message.addToAddress(taxEmail);
        }
        List<String> ccAddresses = parameterService.getParameterValues(LoadPaymentsStep.class, PdpParameterConstants.HARD_EDIT_CC);
        message.getCcAddresses().addAll(ccAddresses);
        message.getBccAddresses().addAll(ccAddresses);

        body.append(getMessage(PdpKeyConstants.MESSAGE_PAYMENT_EMAIL_FILE_TAX_LOADED) + "\n\n");
        addPaymentFieldsToBody(body, paymentFile.getBatchId().intValue(), paymentFile.getChart(), paymentFile.getUnit(), paymentFile.getSubUnit(), paymentFile.getCreationDate(), paymentFile.getPaymentCount(), paymentFile.getPaymentTotalAmount());

        body.append("\n" + getMessage(PdpKeyConstants.MESSAGE_PAYMENT_EMAIL_GO_TO_PDP) + "\n");

        message.setMessage(body.toString());

        try {
            mailService.sendMessage(message);
        }
        catch (InvalidAddressException e) {
            LOG.error("sendErrorEmail() Invalid email address. Message not sent", e);
        }
    }

    /**
     * @see org.kuali.kfs.pdp.service.PdpEmailService#sendLoadEmail(org.kuali.kfs.pdp.businessobject.Batch)
     */
    public void sendLoadEmail(Batch batch) {
        LOG.debug("sendLoadEmail() starting");

        // check email configuration
        if (!isPaymentEmailEnabled()) {
            return;
        }

        MailMessage message = new MailMessage();

        message.setFromAddress(mailService.getBatchMailingList());
        message.setSubject(getEmailSubject(PdpParameterConstants.PAYMENT_LOAD_SUCCESS_EMAIL_SUBJECT_PARAMETER_NAME));

        StringBuffer body = new StringBuffer();

        List<String> ccAddresses = parameterService.getParameterValues(LoadPaymentsStep.class, PdpParameterConstants.HARD_EDIT_CC);
        message.getCcAddresses().addAll(ccAddresses);
        message.getBccAddresses().addAll(ccAddresses);

        CustomerProfile customer = batch.getCustomerProfile();
        String toAddresses = StringUtils.deleteWhitespace(customer.getProcessingEmailAddr());
        List<String> toAddressList = Arrays.asList(toAddresses.split(","));

        message.getToAddresses().addAll(toAddressList);

        body.append(getMessage(PdpKeyConstants.MESSAGE_PAYMENT_EMAIL_FILE_LOADED) + "\n\n");
        addPaymentFieldsToBody(body, batch.getId().intValue(), customer.getChartCode(), customer.getUnitCode(), customer.getSubUnitCode(), batch.getCustomerFileCreateTimestamp(), batch.getPaymentCount().intValue(), batch.getPaymentTotalAmount());

        message.setMessage(body.toString());

        try {
            mailService.sendMessage(message);
        }
        catch (InvalidAddressException e) {
            LOG.error("sendErrorEmail() Invalid email address. Message not sent", e);
        }
    }

    /**
     * @see org.kuali.kfs.pdp.service.PdpEmailService#sendExceedsMaxNotesWarningEmail(java.util.List, java.util.List, int, int)
     */
    public void sendExceedsMaxNotesWarningEmail(List<String> creditMemos, List<String> paymentRequests, int lineTotal, int maxNoteLines) {
        LOG.debug("sendExceedsMaxNotesWarningEmail() starting");

        // check email configuration
        if (!isPaymentEmailEnabled()) {
            return;
        }

        MailMessage message = new MailMessage();
        message.setFromAddress(mailService.getBatchMailingList());

        StringBuffer body = new StringBuffer();

        String productionEnvironmentCode = kualiConfigurationService.getPropertyString(KFSConstants.PROD_ENVIRONMENT_CODE_KEY);
        String environmentCode = kualiConfigurationService.getPropertyString(KFSConstants.ENVIRONMENT_KEY);
        if (StringUtils.equals(productionEnvironmentCode, environmentCode)) {
            message.setSubject(getMessage(PdpKeyConstants.MESSAGE_PURAP_EXTRACT_MAX_NOTES_SUBJECT));
        }
        else {
            message.setSubject(environmentCode + "-" + getMessage(PdpKeyConstants.MESSAGE_PURAP_EXTRACT_MAX_NOTES_SUBJECT));
        }

        // Get recipient email address
        String toAddresses = parameterService.getParameterValue(KfsParameterConstants.PRE_DISBURSEMENT_ALL.class, PdpParameterConstants.PDP_ERROR_EXCEEDS_NOTE_LIMIT_EMAIL);
        List<String> toAddressList = Arrays.asList(toAddresses.split(","));
        message.getToAddresses().addAll(toAddressList);

        List<String> ccAddresses = parameterService.getParameterValues(LoadPaymentsStep.class, PdpParameterConstants.SOFT_EDIT_CC);
        message.getCcAddresses().addAll(ccAddresses);

        
        message.getBccAddresses().addAll(ccAddresses);
        
        body.append(getMessage(PdpKeyConstants.MESSAGE_PURAP_EXTRACT_MAX_NOTES_MESSAGE, StringUtils.join(creditMemos, ","), StringUtils.join(paymentRequests, ","), lineTotal, maxNoteLines));
        message.setMessage(body.toString());

        try {
            mailService.sendMessage(message);
        }
        catch (InvalidAddressException e) {
            LOG.error("sendExceedsMaxNotesWarningEmail() Invalid email address. Message not sent", e);
        }
    }

    /**
     * @see org.kuali.kfs.pdp.service.PdpEmailService#sendAchSummaryEmail(java.util.Map, java.util.Map, java.util.Date)
     */
    public void sendAchSummaryEmail(Map<String, Integer> unitCounts, Map<String, KualiDecimal> unitTotals, Date disbursementDate) {
        LOG.debug("sendAchSummaryEmail() starting");

        MailMessage message = new MailMessage();

        List<String> toAddressList = parameterService.getParameterValues(ExtractAchPaymentsStep.class, PdpParameterConstants.ACH_SUMMARY_TO_EMAIL_ADDRESS_PARMAETER_NAME);
        message.getToAddresses().addAll(toAddressList);
        message.getCcAddresses().addAll(toAddressList);
        message.getBccAddresses().addAll(toAddressList);

        message.setFromAddress(mailService.getBatchMailingList());

        String subject = parameterService.getParameterValue(ExtractAchPaymentsStep.class, PdpParameterConstants.ACH_SUMMARY_EMAIL_SUBJECT_PARAMETER_NAME);
        message.setSubject(subject);

        StringBuffer body = new StringBuffer();
        body.append(getMessage(PdpKeyConstants.MESSAGE_PDP_ACH_SUMMARY_EMAIL_DISB_DATE, disbursementDate) + "\n");

        Integer totalCount = 0;
        KualiDecimal totalAmount = KualiDecimal.ZERO;
        for (String unit : unitCounts.keySet()) {
            body.append(getMessage(PdpKeyConstants.MESSAGE_PDP_ACH_SUMMARY_EMAIL_UNIT_TOTAL, StringUtils.leftPad(unit, 13), StringUtils.leftPad(unitCounts.get(unit).toString(), 10), StringUtils.leftPad(unitTotals.get(unit).toString(), 20)) + "\n");

            totalCount = totalCount + unitCounts.get(unit);
            totalAmount = totalAmount.add(unitTotals.get(unit));
        }

        body.append(getMessage(PdpKeyConstants.MESSAGE_PDP_ACH_SUMMARY_EMAIL_EXTRACT_TOTALS, StringUtils.leftPad(totalCount.toString(), 10), StringUtils.leftPad(totalAmount.toString(), 20)) + "\n");
        body.append(getMessage(PdpKeyConstants.MESSAGE_PDP_ACH_SUMMARY_EMAIL_COMPLETE));

        message.setMessage(body.toString());

        try {
            mailService.sendMessage(message);
        }
        catch (InvalidAddressException e) {
            LOG.error("sendAchSummaryEmail() Invalid email address. Message not sent", e);
        }
    }

    /**
     * @see org.kuali.kfs.pdp.service.PdpEmailService#sendAchAdviceEmail(org.kuali.kfs.pdp.businessobject.PaymentGroup,
     *      org.kuali.kfs.pdp.businessobject.CustomerProfile, org.kuali.kfs.pdp.businessobject.PaymentDetail)
     */
    public void sendAchAdviceEmail(PaymentGroup paymentGroup, PaymentDetail paymentDetail, CustomerProfile customer) {
        LOG.debug("sendAchAdviceEmail() starting");

        MailMessage message = new MailMessage();

        String productionEnvironmentCode = kualiConfigurationService.getPropertyString(KFSConstants.PROD_ENVIRONMENT_CODE_KEY);
        String environmentCode = kualiConfigurationService.getPropertyString(KFSConstants.ENVIRONMENT_KEY);
        if (StringUtils.equals(productionEnvironmentCode, environmentCode)) {
            message.addToAddress(paymentGroup.getAdviceEmailAddress());
            message.addCcAddress(paymentGroup.getAdviceEmailAddress());
            message.addBccAddress(paymentGroup.getAdviceEmailAddress());
            message.setFromAddress(customer.getAdviceReturnEmailAddr());
            message.setSubject(customer.getAdviceSubjectLine());
        }
        else {
            message.addToAddress(mailService.getBatchMailingList());
            message.addCcAddress(mailService.getBatchMailingList());
            message.addBccAddress(mailService.getBatchMailingList());

            message.setFromAddress(customer.getAdviceReturnEmailAddr());
            message.setSubject(environmentCode + ": " + customer.getAdviceSubjectLine() + ":" + paymentGroup.getAdviceEmailAddress());
        }
        

        LOG.debug("sending email to " + paymentGroup.getAdviceEmailAddress() + " for disb # " + paymentGroup.getDisbursementNbr());

        StringBuffer body = new StringBuffer();
        body.append(getMessage(PdpKeyConstants.MESSAGE_PDP_ACH_ADVICE_EMAIL_TOFROM, paymentGroup.getPayeeName(), customer.getAchPaymentDescription()));

        // formatter for payment amounts
        Formatter formatter = new CurrencyFormatter();

        // get bank name to which the payment is being transferred
        String bankName = "";

        ACHBank achBank = achBankService.getByPrimaryId(paymentGroup.getAchBankRoutingNbr());
        if (achBank == null) {
            LOG.error("Bank cound not be found for routing number " + paymentGroup.getAchBankRoutingNbr());
        }
        else {
            bankName = achBank.getBankName();
        }

        body.append(getMessage(PdpKeyConstants.MESSAGE_PDP_ACH_ADVICE_EMAIL_BANKAMOUNT, bankName, formatter.formatForPresentation(paymentDetail.getNetPaymentAmount())));

        // print detail amounts
        int labelPad = 25;

        String newPaymentAmountLabel = dataDictionaryService.getAttributeLabel(PaymentDetail.class, PdpPropertyConstants.PaymentDetail.PAYMENT_NET_AMOUNT);
        body.append(StringUtils.rightPad(newPaymentAmountLabel, labelPad) + formatter.formatForPresentation(paymentDetail.getNetPaymentAmount()) + "\n");

        if (paymentDetail.getOrigInvoiceAmount().isNonZero()) {
            String origInvoiceAmountLabel = dataDictionaryService.getAttributeLabel(PaymentDetail.class, PdpPropertyConstants.PaymentDetail.PAYMENT_ORIGINAL_INVOICE_AMOUNT);
            body.append(StringUtils.rightPad(origInvoiceAmountLabel, labelPad) + formatter.formatForPresentation(paymentDetail.getOrigInvoiceAmount()) + "\n");
        }

        if (paymentDetail.getInvTotDiscountAmount().isNonZero()) {
            String invTotDiscountAmountLabel = dataDictionaryService.getAttributeLabel(PaymentDetail.class, PdpPropertyConstants.PaymentDetail.PAYMENT_INVOICE_TOTAL_DISCOUNT_AMOUNT);
            body.append(StringUtils.rightPad(invTotDiscountAmountLabel, labelPad) + formatter.formatForPresentation(paymentDetail.getInvTotDiscountAmount()) + "\n");
        }

        if (paymentDetail.getInvTotShipAmount().isNonZero()) {
            String invTotShippingAmountLabel = dataDictionaryService.getAttributeLabel(PaymentDetail.class, PdpPropertyConstants.PaymentDetail.PAYMENT_INVOICE_TOTAL_SHIPPING_AMOUNT);
            body.append(StringUtils.rightPad(invTotShippingAmountLabel, labelPad) + formatter.formatForPresentation(paymentDetail.getInvTotShipAmount()) + "\n");
        }

        if (paymentDetail.getInvTotOtherDebitAmount().isNonZero()) {
            String invTotOtherDebitAmountLabel = dataDictionaryService.getAttributeLabel(PaymentDetail.class, PdpPropertyConstants.PaymentDetail.PAYMENT_INVOICE_TOTAL_OTHER_DEBIT_AMOUNT);
            body.append(StringUtils.rightPad(invTotOtherDebitAmountLabel, labelPad) + formatter.formatForPresentation(paymentDetail.getInvTotOtherDebitAmount()) + "\n");
        }

        if (paymentDetail.getInvTotOtherCreditAmount().isNonZero()) {
            String invTotOtherCreditAmountLabel = dataDictionaryService.getAttributeLabel(PaymentDetail.class, PdpPropertyConstants.PaymentDetail.PAYMENT_INVOICE_TOTAL_OTHER_CREDIT_AMOUNT);
            body.append(StringUtils.rightPad(invTotOtherCreditAmountLabel, labelPad) + formatter.formatForPresentation(paymentDetail.getInvTotOtherCreditAmount()) + "\n");
        }

        body.append("\n" + customer.getAdviceHeaderText() + "\n");

        if (StringUtils.isNotBlank(paymentDetail.getPurchaseOrderNbr())) {
            String purchaseOrderNbrLabel = dataDictionaryService.getAttributeLabel(PaymentDetail.class, PdpPropertyConstants.PaymentDetail.PAYMENT_PURCHASE_ORDER_NUMBER);
            body.append(StringUtils.rightPad(purchaseOrderNbrLabel, labelPad) + paymentDetail.getPurchaseOrderNbr() + "\n");
        }

        if (StringUtils.isNotBlank(paymentDetail.getInvoiceNbr())) {
            String invoiceNbrLabel = dataDictionaryService.getAttributeLabel(PaymentDetail.class, PdpPropertyConstants.PaymentDetail.PAYMENT_INVOICE_NUMBER);
            body.append(StringUtils.rightPad(invoiceNbrLabel, labelPad) + paymentDetail.getInvoiceNbr() + "\n");
        }

        if (StringUtils.isNotBlank(paymentDetail.getCustPaymentDocNbr())) {
            String custPaymentDocNbrLabel = dataDictionaryService.getAttributeLabel(PaymentDetail.class, PdpPropertyConstants.PaymentDetail.PAYMENT_CUSTOMER_DOC_NUMBER);
            body.append(StringUtils.rightPad(custPaymentDocNbrLabel, labelPad) + paymentDetail.getCustPaymentDocNbr() + "\n");
        }

        if (StringUtils.isNotBlank(paymentGroup.getCustomerInstitutionNumber())) {
            String customerInstituitionNbrLabel = dataDictionaryService.getAttributeLabel(PaymentGroup.class, PdpPropertyConstants.CUSTOMER_INSTITUTION_NUMBER);
            body.append(StringUtils.rightPad(customerInstituitionNbrLabel, labelPad) + paymentGroup.getCustomerInstitutionNumber() + "\n");
        }

        body.append("\n");

        // print payment notes
        for (PaymentNoteText paymentNoteText : paymentDetail.getNotes()) {
            body.append(paymentNoteText.getCustomerNoteText() + "\n");
        }

        if (paymentDetail.getNotes().isEmpty()) {
            body.append(getMessage(PdpKeyConstants.MESSAGE_PDP_ACH_ADVICE_EMAIL_NONOTES));
        }

        message.setMessage(body.toString());

        try {
            mailService.sendMessage(message);
        }
        catch (InvalidAddressException e) {
            LOG.error("sendAchAdviceEmail() Invalid email address. Sending message to " + customer.getAdviceReturnEmailAddr(), e);

            // send notification to advice return address with payment details
            if (StringUtils.equals(productionEnvironmentCode, environmentCode)) {
                message.addToAddress(customer.getAdviceReturnEmailAddr());
            }
            else {
                message.addToAddress(mailService.getBatchMailingList());
            }
            
            message.setFromAddress(mailService.getBatchMailingList());
            message.setSubject(getMessage(PdpKeyConstants.MESSAGE_PDP_ACH_ADVICE_INVALID_EMAIL_ADDRESS));

            LOG.debug("bouncing email to " + customer.getAdviceReturnEmailAddr() + " for disb # " + paymentGroup.getDisbursementNbr());
            try {
                mailService.sendMessage(message);
            }
            catch (InvalidAddressException e1) {
                LOG.error("Could not send email to advice return email address on customer profile: " + customer.getAdviceReturnEmailAddr(), e1);
                throw new RuntimeException("Could not send email to advice return email address on customer profile: " + customer.getAdviceReturnEmailAddr());
            }
        }
    }
    
    /**
     * 
     * @see org.kuali.kfs.pdp.service.PdpEmailService#sendCancelEmail(org.kuali.kfs.pdp.businessobject.PaymentGroup, java.lang.String, org.kuali.rice.kim.bo.Person)
     */
    public void sendCancelEmail(PaymentGroup paymentGroup, String note, Person user) {
        LOG.debug("sendCancelEmail() starting");

        MailMessage message = new MailMessage();
        
        String productionEnvironmentCode = kualiConfigurationService.getPropertyString(KFSConstants.PROD_ENVIRONMENT_CODE_KEY);
        String environmentCode = kualiConfigurationService.getPropertyString(KFSConstants.ENVIRONMENT_KEY);
        
        if (StringUtils.equals(productionEnvironmentCode, environmentCode)) {
            message.setSubject("PDP --- Cancelled Payment by Tax");
        }
        else {
            message.setSubject(environmentCode + "-PDP --- Cancelled Payment by Tax");
        }

        CustomerProfile cp = paymentGroup.getBatch().getCustomerProfile();
        String toAddresses = cp.getAdviceReturnEmailAddr();
        String toAddressList[] = toAddresses.split(",");

        if (toAddressList.length > 0) {
            for (int i = 0; i < toAddressList.length; i++) {
                if (toAddressList[i] != null) {
                    message.addToAddress(toAddressList[i].trim());
                    message.addBccAddress(toAddressList[i].trim());
                }
            }
        }
        // message.addToAddress(cp.getAdviceReturnEmailAddr());

        String ccAddresses = parameterService.getParameterValue(KfsParameterConstants.PRE_DISBURSEMENT_ALL.class, PdpParameterConstants.TAX_CANCEL_EMAIL_LIST);
        String ccAddressList[] = ccAddresses.split(",");

        if (ccAddressList.length > 0) {
            for (int i = 0; i < ccAddressList.length; i++) {
                if (ccAddressList[i] != null) {
                    message.addCcAddress(ccAddressList[i].trim());
                }
            }
        }

        String fromAddressList[] = { mailService.getBatchMailingList() };

        if (fromAddressList.length > 0) {
            for (int i = 0; i < fromAddressList.length; i++) {
                if (fromAddressList[i] != null) {
                    message.setFromAddress(fromAddressList[i].trim());
                }
            }
        }

        StringBuffer body = new StringBuffer();

        String messageKey = kualiConfigurationService.getPropertyString(PdpKeyConstants.MESSAGE_PDP_PAYMENT_MAINTENANCE_EMAIL_LINE_1);
            body.append(MessageFormat.format(messageKey, new Object[] { null }) + " \n\n");
        
        body.append(note + "\n\n");
        String taxEmail = parameterService.getParameterValue(KfsParameterConstants.PRE_DISBURSEMENT_ALL.class, PdpParameterConstants.TAX_GROUP_EMAIL_ADDRESS);
        String taxContactDepartment = parameterService.getParameterValue(KfsParameterConstants.PRE_DISBURSEMENT_ALL.class, PdpParameterConstants.TAX_CANCEL_CONTACT);
        if (StringUtils.isBlank(taxEmail)) {
            messageKey = kualiConfigurationService.getPropertyString(PdpKeyConstants.MESSAGE_PDP_PAYMENT_MAINTENANCE_EMAIL_LINE_2);
            body.append(MessageFormat.format(messageKey, new Object[] { taxContactDepartment }) + " \n\n");
        }
        else {
            messageKey = kualiConfigurationService.getPropertyString(PdpKeyConstants.MESSAGE_PDP_PAYMENT_MAINTENANCE_EMAIL_LINE_3);
            body.append(MessageFormat.format(messageKey, new Object[] { taxContactDepartment, taxEmail }) + " \n\n");
        }

        messageKey = kualiConfigurationService.getPropertyString(PdpKeyConstants.MESSAGE_PDP_PAYMENT_MAINTENANCE_EMAIL_LINE_4);
            body.append(MessageFormat.format(messageKey, new Object[] { null }) + " \n\n");
        
        for (PaymentDetail pd : paymentGroup.getPaymentDetails()) {

            String payeeLabel = dataDictionaryService.getAttributeLabel(PaymentGroup.class, PdpPropertyConstants.PaymentGroup.PAYMENT_GROUP_PAYEE_NAME);
            String netPaymentAccountLabel = dataDictionaryService.getAttributeLabel(PaymentDetail.class, PdpPropertyConstants.PaymentDetail.PAYMENT_NET_AMOUNT);
            String sourceDocumentNumberLabel = dataDictionaryService.getAttributeLabel(PaymentDetail.class, PdpPropertyConstants.PaymentDetail.PAYMENT_DISBURSEMENT_CUST_PAYMENT_DOC_NBR);
            String invoiceNumberLabel = dataDictionaryService.getAttributeLabel(PaymentDetail.class, PdpPropertyConstants.PaymentDetail.PAYMENT_INVOICE_NUMBER);
            String purchaseOrderNumberLabel = dataDictionaryService.getAttributeLabel(PaymentDetail.class, PdpPropertyConstants.PaymentDetail.PAYMENT_PURCHASE_ORDER_NUMBER);
            String paymentDetailIdLabel = dataDictionaryService.getAttributeLabel(PaymentDetail.class, PdpPropertyConstants.PaymentDetail.PAYMENT_ID);
            
            body.append(payeeLabel + ": " + paymentGroup.getPayeeName() + " \n");
            body.append(netPaymentAccountLabel + ": " + pd.getNetPaymentAmount() + " \n");
            body.append(sourceDocumentNumberLabel + ": " + pd.getCustPaymentDocNbr() + " \n");
            body.append(invoiceNumberLabel + ": " + pd.getInvoiceNbr() + " \n");
            body.append(purchaseOrderNumberLabel + ": " + pd.getPurchaseOrderNbr() + " \n");
            body.append(paymentDetailIdLabel + ": " + pd.getId() + "\n");
            
        }

        body.append(MessageFormat.format(messageKey, new Object[] { null }) + " \n\n");
        
        String batchIdLabel = dataDictionaryService.getAttributeLabel(Batch.class, PdpPropertyConstants.BatchConstants.BATCH_ID);
        String chartMessageLabel = dataDictionaryService.getAttributeLabel(CustomerProfile.class, PdpPropertyConstants.CustomerProfile.CUSTOMER_PROFILE_CHART_CODE);
        String organizationLabel = dataDictionaryService.getAttributeLabel(CustomerProfile.class, PdpPropertyConstants.CustomerProfile.CUSTOMER_PROFILE_UNIT_CODE);
        String subUnitLabel = dataDictionaryService.getAttributeLabel(CustomerProfile.class, PdpPropertyConstants.CustomerProfile.CUSTOMER_PROFILE_SUB_UNIT_CODE);
        String creationDateLabel = dataDictionaryService.getAttributeLabel(Batch.class, PdpPropertyConstants.BatchConstants.FILE_CREATION_TIME);
        String paymentCountLabel = dataDictionaryService.getAttributeLabel(Batch.class, PdpPropertyConstants.BatchConstants.PAYMENT_COUNT);
        String paymentTotalLabel = dataDictionaryService.getAttributeLabel(Batch.class, PdpPropertyConstants.BatchConstants.PAYMENT_TOTAL_AMOUNT);
        
        body.append(batchIdLabel + ": " + paymentGroup.getBatch().getId() + " \n");
        body.append(chartMessageLabel + ": " + cp.getChartCode() + " \n");
        body.append(organizationLabel + ": " + cp.getUnitCode() + " \n");
        body.append(subUnitLabel + ": " + cp.getSubUnitCode() + " \n");
        body.append(creationDateLabel + ": " + paymentGroup.getBatch().getCustomerFileCreateTimestamp() + " \n\n");
        body.append(paymentCountLabel + ": " + paymentGroup.getBatch().getPaymentCount() + " \n\n");
        body.append(paymentTotalLabel + ": " + paymentGroup.getBatch().getPaymentTotalAmount() + " \n\n");

        message.setMessage(body.toString());
        try {
            mailService.sendMessage(message);
        }
        catch (InvalidAddressException e) {
            LOG.error("sendErrorEmail() Invalid email address. Message not sent", e);
        }
    }
    
    /**
     * Writes out payment file field labels and values to <code>StringBuffer</code>
     * 
     * @param body <code>StringBuffer</code>
     */
    protected void addPaymentFieldsToBody(StringBuffer body, Integer batchId, String chart, String unit, String subUnit, Date createDate, int paymentCount, KualiDecimal paymentTotal) {
        String batchIdLabel = dataDictionaryService.getAttributeLabel(PaymentFileLoad.class, PdpPropertyConstants.BATCH_ID);
        body.append(batchIdLabel + ": " + batchId + "\n");

        String chartLabel = dataDictionaryService.getAttributeLabel(PaymentFileLoad.class, KFSPropertyConstants.CHART);
        body.append(chartLabel + ": " + chart + "\n");

        String orgLabel = dataDictionaryService.getAttributeLabel(PaymentFileLoad.class, PdpPropertyConstants.UNIT);
        body.append(orgLabel + ": " + unit + "\n");

        String subUnitLabel = dataDictionaryService.getAttributeLabel(PaymentFileLoad.class, PdpPropertyConstants.SUB_UNIT);
        body.append(subUnitLabel + ": " + subUnit + "\n");

        String createDateLabel = dataDictionaryService.getAttributeLabel(PaymentFileLoad.class, PdpPropertyConstants.CREATION_DATE);
        body.append(createDateLabel + ": " + createDate + "\n");

        String paymentCountLabel = dataDictionaryService.getAttributeLabel(PaymentFileLoad.class, PdpPropertyConstants.PAYMENT_COUNT);
        body.append("\n" + paymentCountLabel + ": " + paymentCount + "\n");

        String paymentTotalLabel = dataDictionaryService.getAttributeLabel(PaymentFileLoad.class, PdpPropertyConstants.PAYMENT_TOTAL_AMOUNT);
        body.append(paymentTotalLabel + ": " + paymentTotal + "\n");
    }

    /**
     * Reads system parameter indicating whether to status emails should be sent
     * 
     * @return true if email should be sent, false otherwise
     */
    protected boolean isPaymentEmailEnabled() {
        boolean noEmail = parameterService.getIndicatorParameter(KfsParameterConstants.PRE_DISBURSEMENT_ALL.class, PdpParameterConstants.NO_PAYMENT_FILE_EMAIL);
        if (noEmail) {
            LOG.debug("sendLoadEmail() sending payment file email is disabled");
            return false;
        }

        return true;
    }

    /**
     * Retrieves the email subject text from system parameter then checks environment code and prepends to message if not
     * production.
     * 
     * @param subjectParmaterName name of parameter giving the subject text
     * @return subject text
     */
    protected String getEmailSubject(String subjectParmaterName) {
        String subject = parameterService.getParameterValue(LoadPaymentsStep.class, subjectParmaterName);

        String productionEnvironmentCode = kualiConfigurationService.getPropertyString(KFSConstants.PROD_ENVIRONMENT_CODE_KEY);
        String environmentCode = kualiConfigurationService.getPropertyString(KFSConstants.ENVIRONMENT_KEY);
        if (!StringUtils.equals(productionEnvironmentCode, environmentCode)) {
            subject = environmentCode + ": " + subject;
        }

        return subject;
    }

    /**
     * Helper method to retrieve a message from resources and substitute place holder values
     * 
     * @param messageKey key of message in resource file
     * @param messageParameters parameter for message
     * @return <code>String</code> Message with substituted values
     */
    protected String getMessage(String messageKey, Object... messageParameters) {
        String message = kualiConfigurationService.getPropertyString(messageKey);
        return MessageFormat.format(message, messageParameters);
    }

    /**
     * Sets the customerProfileService attribute value.
     * 
     * @param customerProfileService The customerProfileService to set.
     */
    public void setCustomerProfileService(CustomerProfileService customerProfileService) {
        this.customerProfileService = customerProfileService;
    }

    /**
     * Sets the kualiConfigurationService attribute value.
     * 
     * @param kualiConfigurationService The kualiConfigurationService to set.
     */
    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    /**
     * Sets the mailService attribute value.
     * 
     * @param mailService The mailService to set.
     */
    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }

    /**
     * Sets the parameterService attribute value.
     * 
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    /**
     * Sets the dataDictionaryService attribute value.
     * 
     * @param dataDictionaryService The dataDictionaryService to set.
     */
    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    /**
     * Sets the achBankService attribute value.
     * 
     * @param achBankService The achBankService to set.
     */
    public void setAchBankService(AchBankService achBankService) {
        this.achBankService = achBankService;
    }

}
