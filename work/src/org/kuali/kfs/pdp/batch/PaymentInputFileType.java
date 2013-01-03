/*
 * Copyright 2008 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.pdp.batch;

import java.io.File;
import java.sql.Timestamp;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.pdp.PdpConstants;
import org.kuali.kfs.pdp.PdpKeyConstants;
import org.kuali.kfs.pdp.businessobject.LoadPaymentStatus;
import org.kuali.kfs.pdp.businessobject.PaymentAccountDetail;
import org.kuali.kfs.pdp.businessobject.PaymentDetail;
import org.kuali.kfs.pdp.businessobject.PaymentFileLoad;
import org.kuali.kfs.pdp.businessobject.PaymentGroup;
import org.kuali.kfs.pdp.service.PaymentFileService;
import org.kuali.kfs.pdp.service.PdpEmailService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.batch.XmlBatchInputFileTypeBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.exception.ParseException;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;

/**
 * Batch input type for the PDP payment file.
 */
public class PaymentInputFileType extends XmlBatchInputFileTypeBase {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PaymentInputFileType.class);

    private DateTimeService dateTimeService;
    private PaymentFileService paymentFileService;
    private PdpEmailService paymentFileEmailService;

    /**
     * @see org.kuali.kfs.sys.batch.BatchInputFileType#getFileName(org.kuali.rice.kim.api.identity.Person, java.lang.Object,
     *      java.lang.String)
     */
    @Override
    public String getFileName(String principalName, Object parsedFileContents, String fileUserIdentifer) {
        Timestamp currentTimestamp = dateTimeService.getCurrentTimestamp();

        String fileName = PdpConstants.PDP_FILE_UPLOAD_FILE_PREFIX  + "_" + principalName;
        if (StringUtils.isNotBlank(fileUserIdentifer)) {
            fileName += "_" + StringUtils.remove(fileUserIdentifer, " ");
        }
        fileName += "_" + dateTimeService.toString(currentTimestamp, "yyyyMMdd_HHmmss");

        // remove spaces in filename
        fileName = StringUtils.remove(fileName, " ");

        return fileName;
    }

    @Override
    public String getAuthorPrincipalName(File file) {
        String[] fileNameParts = StringUtils.split(file.getName(), "_");
        if (fileNameParts.length > 3) {
            return fileNameParts[2];
        }
        return null;
    }

    /**
     * @see org.kuali.kfs.sys.batch.BatchInputFileType#getFileTypeIdentifer()
     */
    @Override
    public String getFileTypeIdentifer() {
        return PdpConstants.PAYMENT_FILE_TYPE_INDENTIFIER;
    }

    /**
     * @see org.kuali.kfs.sys.batch.BatchInputFileType#validate(java.lang.Object)
     */
    @Override
    public boolean validate(Object parsedFileContents) {
        PaymentFileLoad paymentFile = (PaymentFileLoad) parsedFileContents;

        // add validation for chartCode-accountNumber, as chartCode is not required in xsd due to accounts-cant-cross-charts option
        AccountService acctserv = SpringContext.getBean(AccountService.class);
        boolean validAccounts = true;
        if (paymentFile.getPaymentGroups() != null) {
            for (PaymentGroup payGroup : paymentFile.getPaymentGroups()) {
                if (payGroup.getPaymentDetails() == null) {
                    continue;
                }
                for (PaymentDetail payDetail : payGroup.getPaymentDetails()) {
                    if (payDetail.getAccountDetail() == null) {
                        continue;
                    }
                    for (PaymentAccountDetail acctDetail : payDetail.getAccountDetail()) {
                        // if chart code is empty while accounts cannot cross charts, then derive chart code from account number
                        if (StringUtils.isEmpty(acctDetail.getFinChartCode())) {
                            if (acctserv.accountsCanCrossCharts()) {
                                GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_BATCH_UPLOAD_FILE_EMPTY_CHART, acctDetail.getAccountNbr());
                                validAccounts = false;
                            }
                            else {
                                // accountNumber shall not be empty, otherwise won't pass schema validation
                                Account account = acctserv.getUniqueAccountForAccountNumber(acctDetail.getAccountNbr());
                                if (account != null) {
                                    acctDetail.setFinChartCode(account.getChartOfAccountsCode());
                                }
                                else {
                                    GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_BATCH_UPLOAD_FILE_INVALID_ACCOUNT, acctDetail.getAccountNbr());
                                    validAccounts = false;
                                }
                            }
                        }
                    }
                }
            }
        }

        paymentFileService.doPaymentFileValidation(paymentFile, GlobalVariables.getMessageMap());
        return validAccounts && paymentFile.isPassedValidation();
    }

    /**
     * @see org.kuali.kfs.sys.batch.BatchInputType#getTitleKey()
     */
    @Override
    public String getTitleKey() {
        return PdpKeyConstants.MESSAGE_BATCH_UPLOAD_TITLE_PAYMENT;
    }

    /**
     * @see org.kuali.kfs.sys.batch.BatchInputFileTypeBase#process(java.lang.String, java.lang.Object)
     */
    @Override
    public void process(String fileName, Object parsedFileContents) {
        PaymentFileLoad paymentFile = (PaymentFileLoad) parsedFileContents;
        if (paymentFile.isPassedValidation()) {
            // collect various information for status of load
            LoadPaymentStatus status = new LoadPaymentStatus();
            status.setMessageMap(new MessageMap());

            paymentFileService.loadPayments(paymentFile, status, fileName);
            paymentFileService.createOutputFile(status, fileName);
        }
    }

    /**
     * Overridden so we can send the error email here. (keep it consistent between the batch processing and the
     * online file upload web processing)
     *
     * @see org.kuali.kfs.sys.batch.XmlBatchInputFileTypeBase#parse(byte[])
     */
    @Override
    public Object parse(byte[] fileByteContent) throws ParseException {
        PaymentFileLoad paymentFile = null;

        try {
            paymentFile = (PaymentFileLoad) super.parse(fileByteContent);
        } catch (ParseException e1) {
            LOG.error("Error parsing xml contents: " + e1.getMessage());
            MessageMap errorMap = new MessageMap();
            errorMap.putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_BATCH_UPLOAD_PARSING_XML, new String[] { "Error parsing xml contents: " + e1.getMessage() });

            // Send error email
            paymentFileEmailService.sendErrorEmail(paymentFile, errorMap);
            throw new ParseException("Error parsing xml contents: " + e1.getMessage(), e1);
        }

        return paymentFile;
    }

    /**
     * Sets the paymentFileService attribute value.
     *
     * @param paymentFileService The paymentFileService to set.
     */
    public void setPaymentFileService(PaymentFileService paymentFileService) {
        this.paymentFileService = paymentFileService;
    }

    /**
     * Sets the dateTimeService attribute value.
     *
     * @param dateTimeService The dateTimeService to set.
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    /**
     * Sets the paymentFileEmailService attribute value.
     *
     * @param paymentFileEmailService The paymentFileEmailService to set.
     */
    public void setPaymentFileEmailService(PdpEmailService paymentFileEmailService) {
        this.paymentFileEmailService = paymentFileEmailService;
    }
}

