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
package org.kuali.kfs.pdp.service.impl;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.ProjectCode;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.businessobject.SubObjectCode;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.coa.service.SubAccountService;
import org.kuali.kfs.coa.service.SubObjectCodeService;
import org.kuali.kfs.pdp.PdpConstants;
import org.kuali.kfs.pdp.PdpKeyConstants;
import org.kuali.kfs.pdp.PdpParameterConstants;
import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.kfs.pdp.businessobject.AccountingChangeCode;
import org.kuali.kfs.pdp.businessobject.CustomerProfile;
import org.kuali.kfs.pdp.businessobject.PayeeType;
import org.kuali.kfs.pdp.businessobject.PaymentAccountDetail;
import org.kuali.kfs.pdp.businessobject.PaymentAccountHistory;
import org.kuali.kfs.pdp.businessobject.PaymentDetail;
import org.kuali.kfs.pdp.businessobject.PaymentFileLoad;
import org.kuali.kfs.pdp.businessobject.PaymentGroup;
import org.kuali.kfs.pdp.businessobject.PaymentStatus;
import org.kuali.kfs.pdp.dataaccess.PaymentFileLoadDao;
import org.kuali.kfs.pdp.service.CustomerProfileService;
import org.kuali.kfs.pdp.service.PaymentFileValidationService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.businessobject.OriginationCode;
import org.kuali.kfs.sys.service.BankService;
import org.kuali.kfs.sys.service.OriginationCodeService;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.doctype.DocumentTypeService;
import org.kuali.rice.krad.bo.KualiCodeBase;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * @see org.kuali.kfs.pdp.batch.service.PaymentFileValidationService
 */
@Transactional
public class PaymentFileValidationServiceImpl implements PaymentFileValidationService {
    public static final String[] PAYMENT_GROUP_PROPERTIES_TO_CHECK_MAX_LENGTH = {"line1Address", "line2Address", "line3Address", "line4Address", "city", "state", "country", "zipCd", "adviceEmailAddress"};

    protected CustomerProfileService customerProfileService;
    protected PaymentFileLoadDao paymentFileLoadDao;
    protected ParameterService parameterService;
    protected ConfigurationService kualiConfigurationService;
    protected DateTimeService dateTimeService;
    protected AccountService accountService;
    protected SubAccountService subAccountService;
    protected ObjectCodeService objectCodeService;
    protected SubObjectCodeService subObjectCodeService;
    protected BankService bankService;
    protected OriginationCodeService originationCodeService;
    protected DocumentTypeService documentTypeService;
    protected BusinessObjectService businessObjectService;
    protected DataDictionaryService dataDictionaryService;

    /**
     * @see org.kuali.kfs.pdp.batch.service.PaymentFileValidationService#doHardEdits(org.kuali.kfs.pdp.businessobject.PaymentFile,
     *      org.kuali.rice.krad.util.MessageMap)
     */
    @Override
    public void doHardEdits(PaymentFileLoad paymentFile, MessageMap errorMap) {
        processHeaderValidation(paymentFile, errorMap);

        if (errorMap.hasNoErrors()) {
            processGroupValidation(paymentFile, errorMap);
        }

        if (errorMap.hasNoErrors()) {
            processTrailerValidation(paymentFile, errorMap);
        }
    }

    /**
     * Validates payment file header fields <li>Checks customer exists in customer profile table and is active</li>
     *
     * @param paymentFile payment file object
     * @param errorMap map in which errors will be added to
     */
    protected void processHeaderValidation(PaymentFileLoad paymentFile, MessageMap errorMap) {
        CustomerProfile customer = customerProfileService.get(paymentFile.getChart(), paymentFile.getUnit(), paymentFile.getSubUnit());
        if (customer == null) {
            errorMap.putError(KFSConstants.GLOBAL_ERRORS, PdpKeyConstants.ERROR_PAYMENT_LOAD_INVALID_CUSTOMER, paymentFile.getChart(), paymentFile.getUnit(), paymentFile.getSubUnit());
        }
        else {
            if (!customer.isActive()) {
                errorMap.putError(KFSConstants.GLOBAL_ERRORS, PdpKeyConstants.ERROR_PAYMENT_LOAD_INACTIVE_CUSTOMER, paymentFile.getChart(), paymentFile.getUnit(), paymentFile.getSubUnit());
            }
            else {
                paymentFile.setCustomer(customer);
            }
        }
    }

    /**
     * Validates payment file trailer fields <li>Reconciles actual to expected payment count and totals</li> <li>Verifies the batch
     * is not a duplicate</li>
     *
     * @param paymentFile payment file object
     * @param errorMap map in which errors will be added to
     */
    protected void processTrailerValidation(PaymentFileLoad paymentFile, MessageMap errorMap) {
        // compare trailer payment count to actual count loaded
        if (paymentFile.getActualPaymentCount() != paymentFile.getPaymentCount()) {
            errorMap.putError(KFSConstants.GLOBAL_ERRORS, PdpKeyConstants.ERROR_PAYMENT_LOAD_PAYMENT_COUNT_MISMATCH, Integer.toString(paymentFile.getPaymentCount()), Integer.toString(paymentFile.getActualPaymentCount()));
        }

        // compare trailer total amount with actual total amount
        if (paymentFile.getCalculatedPaymentTotalAmount().compareTo(paymentFile.getPaymentTotalAmount()) != 0) {
            errorMap.putError(KFSConstants.GLOBAL_ERRORS, PdpKeyConstants.ERROR_PAYMENT_LOAD_PAYMENT_TOTAL_MISMATCH, paymentFile.getPaymentTotalAmount().toString(), paymentFile.getCalculatedPaymentTotalAmount().toString());
        }

        // Check to see if this is a duplicate batch
        Timestamp now = new Timestamp(paymentFile.getCreationDate().getTime());

        if (paymentFileLoadDao.isDuplicateBatch(paymentFile.getCustomer(), paymentFile.getPaymentCount(), paymentFile.getPaymentTotalAmount().bigDecimalValue(), now)) {
            errorMap.putError(KFSConstants.GLOBAL_ERRORS, PdpKeyConstants.ERROR_PAYMENT_LOAD_DUPLICATE_BATCH);
        }
    }

    /**
     * Validates payment file groups <li>Checks number of note lines needed is not above the configured maximum allowed</li> <li>
     * Verifies group total is not negative</li> <li>Verifies detail accounting total equals net payment amount</li>
     *
     * @param paymentFile payment file object
     * @param errorMap map in which errors will be added to
     */
    protected void processGroupValidation(PaymentFileLoad paymentFile, MessageMap errorMap) {
        int groupCount = 0;
        for (PaymentGroup paymentGroup : paymentFile.getPaymentGroups()) {
            groupCount++;
            int noteLineCount = 0;
            int detailCount = 0;

            // We've encountered Payment Files that have address lines exceeding the column size in DB table;
            // so adding extra validation on payment group BO, especially the max length, based on DD definitions.
            // Check that PaymentGroup String properties don't exceed maximum allowed length
            checkPaymentGroupPropertyMaxLength(paymentGroup, errorMap);

            // verify payee id and owner code if customer requires them to be filled in
            if (paymentFile.getCustomer().getPayeeIdRequired() && StringUtils.isBlank(paymentGroup.getPayeeId())) {
                errorMap.putError(KFSConstants.GLOBAL_ERRORS, PdpKeyConstants.ERROR_PAYMENT_LOAD_PAYEE_ID_REQUIRED, Integer.toString(groupCount));
            }

            if (paymentFile.getCustomer().getOwnershipCodeRequired() && StringUtils.isBlank(paymentGroup.getPayeeOwnerCd())) {
                errorMap.putError(KFSConstants.GLOBAL_ERRORS, PdpKeyConstants.ERROR_PAYMENT_LOAD_PAYEE_OWNER_CODE, Integer.toString(groupCount));
            }

            // validate payee id type
            if (StringUtils.isNotBlank(paymentGroup.getPayeeIdTypeCd())) {
                PayeeType payeeType = businessObjectService.findBySinglePrimaryKey(PayeeType.class, paymentGroup.getPayeeIdTypeCd());
                if (payeeType == null) {
                    errorMap.putError(KFSConstants.GLOBAL_ERRORS, PdpKeyConstants.ERROR_PAYMENT_LOAD_INVALID_PAYEE_ID_TYPE, Integer.toString(groupCount), paymentGroup.getPayeeIdTypeCd());
                }
            }

            // validate bank
            String bankCode = paymentGroup.getBankCode();
            if (StringUtils.isNotBlank(bankCode)) {
                Bank bank = bankService.getByPrimaryId(bankCode);
                if (bank == null) {
                    errorMap.putError(KFSConstants.GLOBAL_ERRORS, PdpKeyConstants.ERROR_PAYMENT_LOAD_INVALID_BANK_CODE, Integer.toString(groupCount), bankCode);
                }
                else if (!bank.isActive()) {
                    errorMap.putError(KFSConstants.GLOBAL_ERRORS, PdpKeyConstants.ERROR_PAYMENT_LOAD_INACTIVE_BANK_CODE, Integer.toString(groupCount), bankCode);
                }
            }

            KualiDecimal groupTotal = KualiDecimal.ZERO;
            for (PaymentDetail paymentDetail : paymentGroup.getPaymentDetails()) {
                detailCount++;

                noteLineCount++; // Add a line to print the invoice number
                noteLineCount = noteLineCount + paymentDetail.getNotes().size();

                if ((paymentDetail.getNetPaymentAmount() == null) && (!paymentDetail.isDetailAmountProvided())) {
                    paymentDetail.setNetPaymentAmount(paymentDetail.getAccountTotal());
                }
                else if ((paymentDetail.getNetPaymentAmount() == null) && (paymentDetail.isDetailAmountProvided())) {
                    paymentDetail.setNetPaymentAmount(paymentDetail.getCalculatedPaymentAmount());
                }

                // compare net to accounting segments
                if (paymentDetail.getAccountTotal().compareTo(paymentDetail.getNetPaymentAmount()) != 0) {
                    errorMap.putError(KFSConstants.GLOBAL_ERRORS, PdpKeyConstants.ERROR_PAYMENT_LOAD_DETAIL_TOTAL_MISMATCH, Integer.toString(groupCount), Integer.toString(detailCount), paymentDetail.getAccountTotal().toString(), paymentDetail.getNetPaymentAmount().toString());
                }

                // validate origin code if given
                if (StringUtils.isNotBlank(paymentDetail.getFinancialSystemOriginCode())) {
                    OriginationCode originationCode = originationCodeService.getByPrimaryKey(paymentDetail.getFinancialSystemOriginCode());
                    if (originationCode == null) {
                        errorMap.putError(KFSConstants.GLOBAL_ERRORS, PdpKeyConstants.ERROR_PAYMENT_LOAD_INVALID_ORIGIN_CODE, Integer.toString(groupCount), Integer.toString(detailCount), paymentDetail.getFinancialSystemOriginCode());
                    }
                }

                // validate doc type if given
                if (StringUtils.isNotBlank(paymentDetail.getFinancialDocumentTypeCode())) {
                    if ( !documentTypeService.isActiveByName(paymentDetail.getFinancialDocumentTypeCode()) ) {
                        errorMap.putError(KFSConstants.GLOBAL_ERRORS, PdpKeyConstants.ERROR_PAYMENT_LOAD_INVALID_DOC_TYPE, Integer.toString(groupCount), Integer.toString(detailCount), paymentDetail.getFinancialDocumentTypeCode());
                    }
                }

                groupTotal = groupTotal.add(paymentDetail.getNetPaymentAmount());
            }

            // verify total for group is not negative
            if (groupTotal.doubleValue() < 0) {
                errorMap.putError(KFSConstants.GLOBAL_ERRORS, PdpKeyConstants.ERROR_PAYMENT_LOAD_NEGATIVE_GROUP_TOTAL, Integer.toString(groupCount));
            }

            // check that the number of detail items and note lines will fit on a check stub
            if (noteLineCount > getMaxNoteLines()) {
                errorMap.putError(KFSConstants.GLOBAL_ERRORS, PdpKeyConstants.ERROR_PAYMENT_LOAD_MAX_NOTE_LINES, Integer.toString(groupCount), Integer.toString(noteLineCount), Integer.toString(getMaxNoteLines()));
            }
        }
    }

    /**
     * Checks the max length for PaymentGroup properties that could possibly exceed the maximum length defined in DD.
     * This checking is needed because we've encountered Payment Files that have address lines exceeding the column size in DB table, and this causes
     * SQL exceptions while saving PaymentGroup. So we need check all PaymentGroup property values which might exceed the maximum length allowed.
     *
     * @param paymentGroup the payment group for which field lengths are checked
     * @param errorMap map in which errors will be added to
     */
    protected void checkPaymentGroupPropertyMaxLength(PaymentGroup paymentGroup, MessageMap errorMap) {
        for (String propertyName : PAYMENT_GROUP_PROPERTIES_TO_CHECK_MAX_LENGTH) {
            // we only check max length on String type properties
            String propertyValue = (String)ObjectUtils.getPropertyValue(paymentGroup, propertyName);
            if (StringUtils.isNotEmpty(propertyValue)) {
                // we assume that max length defined in DD is the same as the size of the column in PaymentGroup table
                Integer maxLength = dataDictionaryService.getAttributeMaxLength(PaymentGroup.class, propertyName);
                if ((maxLength != null) && (maxLength.intValue() < propertyValue.length())) {
                    String errorLabel = dataDictionaryService.getAttributeErrorLabel(PaymentGroup.class, propertyName);
                    errorLabel += " with the value '" + propertyValue + "'";
                    errorMap.putError(KFSConstants.GLOBAL_ERRORS, RiceKeyConstants.ERROR_MAX_LENGTH, new String[]{errorLabel, maxLength.toString()});
                }
            }
        }
    }

    /**
     * @see org.kuali.kfs.pdp.service.PaymentFileValidationService#doSoftEdits(org.kuali.kfs.pdp.businessobject.PaymentFile)
     */
    @Override
    public List<String> doSoftEdits(PaymentFileLoad paymentFile) {
        List<String> warnings = new ArrayList<String>();

        CustomerProfile customer = customerProfileService.get(paymentFile.getChart(), paymentFile.getUnit(), paymentFile.getSubUnit());

        // check payment amount does not exceed the configured threshold amount of this customer
        if (paymentFile.getPaymentTotalAmount().compareTo(customer.getFileThresholdAmount()) > 0) {
            addWarningMessage(warnings, PdpKeyConstants.MESSAGE_PAYMENT_LOAD_FILE_THRESHOLD, paymentFile.getPaymentTotalAmount().toString(), customer.getFileThresholdAmount().toString());
            paymentFile.setFileThreshold(true);
        }

        processGroupSoftEdits(paymentFile, customer, warnings);

        return warnings;
    }

    /**
     * Set defaults for group fields and do tax checks.
     *
     * @param paymentFile payment file object
     * @param customer payment customer
     * @param warnings <code>List</code> list of accumulated warning messages
     */
    public void processGroupSoftEdits(PaymentFileLoad paymentFile, CustomerProfile customer, List<String> warnings) {
        PaymentStatus openStatus = businessObjectService.findBySinglePrimaryKey(PaymentStatus.class, PdpConstants.PaymentStatusCodes.OPEN);

        for (PaymentGroup paymentGroup : paymentFile.getPaymentGroups()) {
            paymentGroup.setBatchId(paymentFile.getBatchId());
            paymentGroup.setPaymentStatusCode(openStatus.getCode());
            paymentGroup.setPaymentStatus(openStatus);
            paymentGroup.setPayeeName(paymentGroup.getPayeeName().toUpperCase());

            // Set defaults for missing information
            defaultGroupIndicators(paymentGroup);

            // Tax Group Requirements for automatic Holding
            checkForTaxEmailRequired(paymentFile, paymentGroup, customer);

            // KFSMI-9997 / KFSMI-9998
            // Checks for valid payment date or set to tomorrow if missing
            checkGroupPaymentDate(paymentGroup, warnings);

            // do edits on detail lines
            for (PaymentDetail paymentDetail : paymentGroup.getPaymentDetails()) {
                paymentDetail.setPaymentGroupId(paymentGroup.getId());

                processDetailSoftEdits(paymentFile, customer, paymentDetail, warnings);
            }

        }
    }

    /**
     * Set default fields on detail line and check amount against customer threshold.
     *
     * @param paymentFile payment file object
     * @param customer payment customer
     * @param paymentDetail <code>PaymentDetail</code> object to process
     * @param warnings <code>List</code> list of accumulated warning messages
     */
    protected void processDetailSoftEdits(PaymentFileLoad paymentFile, CustomerProfile customer, PaymentDetail paymentDetail, List<String> warnings) {
        updateDetailAmounts(paymentDetail);

        // Check net payment amount
        KualiDecimal testAmount = paymentDetail.getNetPaymentAmount();
        if (testAmount.compareTo(customer.getPaymentThresholdAmount()) > 0) {
            addWarningMessage(warnings, PdpKeyConstants.MESSAGE_PAYMENT_LOAD_DETAIL_THRESHOLD, testAmount.toString(), customer.getPaymentThresholdAmount().toString());
            paymentFile.setDetailThreshold(true);
            paymentFile.getThresholdPaymentDetails().add(paymentDetail);
        }

        // set invoice date if it doesn't exist
        if (paymentDetail.getInvoiceDate() == null) {
            paymentDetail.setInvoiceDate(dateTimeService.getCurrentSqlDate());
        }

        if (paymentDetail.getPrimaryCancelledPayment() == null) {
            paymentDetail.setPrimaryCancelledPayment(Boolean.FALSE);
        }

        // do accounting edits
        for (PaymentAccountDetail paymentAccountDetail : paymentDetail.getAccountDetail()) {
            paymentAccountDetail.setPaymentDetailId(paymentDetail.getId());

            processAccountSoftEdits(paymentFile, customer, paymentAccountDetail, warnings);
        }
    }

    /**
     * Set default fields on account line and perform account field existence checks
     *
     * @param paymentFile payment file object
     * @param customer payment customer
     * @param paymentAccountDetail <code>PaymentAccountDetail</code> object to process
     * @param warnings <code>List</code> list of accumulated warning messages
     */
    protected void processAccountSoftEdits(PaymentFileLoad paymentFile, CustomerProfile customer, PaymentAccountDetail paymentAccountDetail, List<String> warnings) {
        List<PaymentAccountHistory> changeRecords = paymentAccountDetail.getAccountHistory();

        // uppercase chart
        paymentAccountDetail.setFinChartCode(paymentAccountDetail.getFinChartCode().toUpperCase());

        // only do accounting edits if required by customer
        if (customer.getAccountingEditRequired()) {
            // check account number
            Account account = accountService.getByPrimaryId(paymentAccountDetail.getFinChartCode(), paymentAccountDetail.getAccountNbr());
            if (account == null) {
                addWarningMessage(warnings, PdpKeyConstants.MESSAGE_PAYMENT_LOAD_INVALID_ACCOUNT, paymentAccountDetail.getFinChartCode(), paymentAccountDetail.getAccountNbr());

                KualiCodeBase objChangeCd = businessObjectService.findBySinglePrimaryKey(AccountingChangeCode.class, PdpConstants.AccountChangeCodes.INVALID_ACCOUNT);
                replaceAccountingString(objChangeCd, changeRecords, customer, paymentAccountDetail);
            }
            else {
                // check sub account code
                if (StringUtils.isNotBlank(paymentAccountDetail.getSubAccountNbr())) {
                    SubAccount subAccount = subAccountService.getByPrimaryId(paymentAccountDetail.getFinChartCode(), paymentAccountDetail.getAccountNbr(), paymentAccountDetail.getSubAccountNbr());
                    if (subAccount == null) {
                        addWarningMessage(warnings, PdpKeyConstants.MESSAGE_PAYMENT_LOAD_INVALID_SUB_ACCOUNT, paymentAccountDetail.getFinChartCode(), paymentAccountDetail.getAccountNbr(), paymentAccountDetail.getSubAccountNbr());

                        KualiCodeBase objChangeCd = businessObjectService.findBySinglePrimaryKey(AccountingChangeCode.class, PdpConstants.AccountChangeCodes.INVALID_SUB_ACCOUNT);
                        changeRecords.add(newAccountHistory(PdpPropertyConstants.SUB_ACCOUNT_DB_COLUMN_NAME, KFSConstants.getDashSubAccountNumber(), paymentAccountDetail.getSubAccountNbr(), objChangeCd));

                        paymentAccountDetail.setSubAccountNbr(KFSConstants.getDashSubAccountNumber());
                    }
                }

                // check object code
                ObjectCode objectCode = objectCodeService.getByPrimaryIdForCurrentYear(paymentAccountDetail.getFinChartCode(), paymentAccountDetail.getFinObjectCode());
                if (objectCode == null) {
                    addWarningMessage(warnings, PdpKeyConstants.MESSAGE_PAYMENT_LOAD_INVALID_OBJECT, paymentAccountDetail.getFinChartCode(), paymentAccountDetail.getFinObjectCode());

                    KualiCodeBase objChangeCd = businessObjectService.findBySinglePrimaryKey(AccountingChangeCode.class, PdpConstants.AccountChangeCodes.INVALID_OBJECT);
                    replaceAccountingString(objChangeCd, changeRecords, customer, paymentAccountDetail);
                }

                // check sub object code
                else if (StringUtils.isNotBlank(paymentAccountDetail.getFinSubObjectCode())) {
                    SubObjectCode subObjectCode = subObjectCodeService.getByPrimaryIdForCurrentYear(paymentAccountDetail.getFinChartCode(), paymentAccountDetail.getAccountNbr(), paymentAccountDetail.getFinObjectCode(), paymentAccountDetail.getFinSubObjectCode());
                    if (subObjectCode == null) {
                        addWarningMessage(warnings, PdpKeyConstants.MESSAGE_PAYMENT_LOAD_INVALID_SUB_OBJECT, paymentAccountDetail.getFinChartCode(), paymentAccountDetail.getAccountNbr(), paymentAccountDetail.getFinObjectCode(), paymentAccountDetail.getFinSubObjectCode());

                        KualiCodeBase objChangeCd = businessObjectService.findBySinglePrimaryKey(AccountingChangeCode.class, PdpConstants.AccountChangeCodes.INVALID_SUB_OBJECT);
                        changeRecords.add(newAccountHistory(PdpPropertyConstants.SUB_OBJECT_DB_COLUMN_NAME, KFSConstants.getDashFinancialSubObjectCode(), paymentAccountDetail.getFinSubObjectCode(), objChangeCd));

                        paymentAccountDetail.setFinSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
                    }
                }
            }

            // check project code
            if (StringUtils.isNotBlank(paymentAccountDetail.getProjectCode())) {
                ProjectCode projectCode = businessObjectService.findBySinglePrimaryKey(ProjectCode.class, paymentAccountDetail.getProjectCode());
                if (projectCode == null) {
                    addWarningMessage(warnings, PdpKeyConstants.MESSAGE_PAYMENT_LOAD_INVALID_PROJECT, paymentAccountDetail.getProjectCode());

                    KualiCodeBase objChangeCd = businessObjectService.findBySinglePrimaryKey(AccountingChangeCode.class, PdpConstants.AccountChangeCodes.INVALID_PROJECT);
                    changeRecords.add(newAccountHistory(PdpPropertyConstants.PROJECT_DB_COLUMN_NAME, KFSConstants.getDashProjectCode(), paymentAccountDetail.getProjectCode(), objChangeCd));
                    paymentAccountDetail.setProjectCode(KFSConstants.getDashProjectCode());
                }
            }
        }

        // change nulls into ---'s for the fields that need it
        if (StringUtils.isBlank(paymentAccountDetail.getFinSubObjectCode())) {
            paymentAccountDetail.setFinSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
        }

        if (StringUtils.isBlank(paymentAccountDetail.getSubAccountNbr())) {
            paymentAccountDetail.setSubAccountNbr(KFSConstants.getDashSubAccountNumber());
        }

        if (StringUtils.isBlank(paymentAccountDetail.getProjectCode())) {
            paymentAccountDetail.setProjectCode(KFSConstants.getDashProjectCode());
        }

    }

    /**
     * Replaces the entire accounting string with defaults from the customer profile.
     *
     * @param objChangeCd code indicating reason for change
     * @param changeRecords <code>List</code> of <code>PaymentAccountHistory</code> records
     * @param customer profile of payment customer
     * @param paymentAccountDetail account detail record
     */
    protected void replaceAccountingString(KualiCodeBase objChangeCd, List<PaymentAccountHistory> changeRecords, CustomerProfile customer, PaymentAccountDetail paymentAccountDetail) {
        changeRecords.add(newAccountHistory(PdpPropertyConstants.CHART_DB_COLUMN_NAME, customer.getDefaultChartCode(), paymentAccountDetail.getFinChartCode(), objChangeCd));
        changeRecords.add(newAccountHistory(PdpPropertyConstants.ACCOUNT_DB_COLUMN_NAME, customer.getDefaultAccountNumber(), paymentAccountDetail.getAccountNbr(), objChangeCd));
        changeRecords.add(newAccountHistory(PdpPropertyConstants.SUB_ACCOUNT_DB_COLUMN_NAME, customer.getDefaultSubAccountNumber(), paymentAccountDetail.getSubAccountNbr(), objChangeCd));
        changeRecords.add(newAccountHistory(PdpPropertyConstants.OBJECT_DB_COLUMN_NAME, customer.getDefaultObjectCode(), paymentAccountDetail.getFinObjectCode(), objChangeCd));
        changeRecords.add(newAccountHistory(PdpPropertyConstants.SUB_OBJECT_DB_COLUMN_NAME, customer.getDefaultSubObjectCode(), paymentAccountDetail.getFinSubObjectCode(), objChangeCd));

        paymentAccountDetail.setFinChartCode(customer.getDefaultChartCode());
        paymentAccountDetail.setAccountNbr(customer.getDefaultAccountNumber());
        if (StringUtils.isNotBlank(customer.getDefaultSubAccountNumber())) {
            paymentAccountDetail.setSubAccountNbr(customer.getDefaultSubAccountNumber());
        }
        else {
            paymentAccountDetail.setSubAccountNbr(KFSConstants.getDashSubAccountNumber());
        }
        paymentAccountDetail.setFinObjectCode(customer.getDefaultObjectCode());
        if (StringUtils.isNotBlank(customer.getDefaultSubAccountNumber())) {
            paymentAccountDetail.setFinSubObjectCode(customer.getDefaultSubObjectCode());
        }
        else {
            paymentAccountDetail.setFinSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
        }
    }

    /**
     * Helper method to construct a new payment account history record
     *
     * @param attName name of field that has changed
     * @param newValue new value for the field
     * @param oldValue field value that was changed
     * @param changeCode code indicating reason for change
     * @return <code>PaymentAccountHistory</code>
     */
    protected PaymentAccountHistory newAccountHistory(String attName, String newValue, String oldValue, KualiCodeBase changeCode) {
        PaymentAccountHistory paymentAccountHistory = new PaymentAccountHistory();

        paymentAccountHistory.setAcctAttributeName(attName);
        paymentAccountHistory.setAcctAttributeNewValue(newValue);
        paymentAccountHistory.setAcctAttributeOrigValue(oldValue);
        paymentAccountHistory.setAcctChangeDate(dateTimeService.getCurrentTimestamp());
        paymentAccountHistory.setAccountingChange((AccountingChangeCode) changeCode);

        return paymentAccountHistory;
    }

    /**
     * Sets null amount fields to 0
     *
     * @param paymentDetail <code>PaymentDetail</code> to update
     */
    protected void updateDetailAmounts(PaymentDetail paymentDetail) {
        KualiDecimal zero = KualiDecimal.ZERO;

        if (paymentDetail.getInvTotDiscountAmount() == null) {
            paymentDetail.setInvTotDiscountAmount(zero);
        }

        if (paymentDetail.getInvTotShipAmount() == null) {
            paymentDetail.setInvTotShipAmount(zero);
        }

        if (paymentDetail.getInvTotOtherDebitAmount() == null) {
            paymentDetail.setInvTotOtherDebitAmount(zero);
        }

        if (paymentDetail.getInvTotOtherCreditAmount() == null) {
            paymentDetail.setInvTotOtherCreditAmount(zero);
        }

        // update the total payment amount with the amount from the accounts if null
        if (paymentDetail.getNetPaymentAmount() == null) {
            paymentDetail.setNetPaymentAmount(paymentDetail.getAccountTotal());
        }

        if (paymentDetail.getOrigInvoiceAmount() == null) {
            KualiDecimal amt = paymentDetail.getNetPaymentAmount();
            amt = amt.add(paymentDetail.getInvTotDiscountAmount());
            amt = amt.subtract(paymentDetail.getInvTotShipAmount());
            amt = amt.subtract(paymentDetail.getInvTotOtherDebitAmount());
            amt = amt.add(paymentDetail.getInvTotOtherCreditAmount());
            paymentDetail.setOrigInvoiceAmount(amt);
        }
    }

    /**
     * Sets null indicators to false
     *
     * @param paymentGroup <code>PaymentGroup</code> to update
     */
    protected void defaultGroupIndicators(PaymentGroup paymentGroup) {
        // If combineGroups isn't specified, we want it to default to true
        if (paymentGroup.getCombineGroups() == null) {
            paymentGroup.setCombineGroups(Boolean.TRUE);
        }

        if (paymentGroup.getCampusAddress() == null) {
            paymentGroup.setCampusAddress(Boolean.FALSE);
        }

        if (paymentGroup.getPymtAttachment() == null) {
            paymentGroup.setPymtAttachment(Boolean.FALSE);
        }

        if (paymentGroup.getPymtSpecialHandling() == null) {
            paymentGroup.setPymtSpecialHandling(Boolean.FALSE);
        }

        if (paymentGroup.getProcessImmediate() == null) {
            paymentGroup.setProcessImmediate(Boolean.FALSE);
        }

        if (paymentGroup.getEmployeeIndicator() == null) {
            paymentGroup.setEmployeeIndicator(Boolean.FALSE);
        }

        if (paymentGroup.getNraPayment() == null) {
            paymentGroup.setNraPayment(Boolean.FALSE);
        }

        if (paymentGroup.getTaxablePayment() == null) {
            paymentGroup.setTaxablePayment(Boolean.FALSE);
        }
    }

    /**
     * Checks whether payment status should be set to held and a tax email sent indicating so
     *
     * @param paymentFile payment file object
     * @param paymentGroup <code>PaymentGroup</code> being checked
     * @param customer payment customer
     */
    protected void checkForTaxEmailRequired(PaymentFileLoad paymentFile, PaymentGroup paymentGroup, CustomerProfile customer) {
        PaymentStatus heldForNRAEmployee = businessObjectService.findBySinglePrimaryKey(PaymentStatus.class, PdpConstants.PaymentStatusCodes.HELD_TAX_NRA_EMPL_CD);
        PaymentStatus heldForEmployee = businessObjectService.findBySinglePrimaryKey(PaymentStatus.class, PdpConstants.PaymentStatusCodes.HELD_TAX_EMPLOYEE_CD);
        PaymentStatus heldForNRA = businessObjectService.findBySinglePrimaryKey(PaymentStatus.class, PdpConstants.PaymentStatusCodes.HELD_TAX_NRA_CD);

        if (customer.getNraReview() && customer.getEmployeeCheck() && paymentGroup.getEmployeeIndicator().booleanValue() && paymentGroup.getNraPayment().booleanValue()) {
            paymentGroup.setPaymentStatus(heldForNRAEmployee);
            paymentFile.setTaxEmailRequired(true);
        }

        else if (customer.getEmployeeCheck() && paymentGroup.getEmployeeIndicator().booleanValue()) {
            paymentGroup.setPaymentStatus(heldForEmployee);
            paymentFile.setTaxEmailRequired(true);
        }

        else if (customer.getNraReview() && paymentGroup.getNraPayment().booleanValue()) {
            paymentGroup.setPaymentStatus(heldForNRA);
            paymentFile.setTaxEmailRequired(true);
        }
    }

    /**
     * Checks the payment date is not more than 30 days past or 30 days coming
     *
     * @param paymentGroup <code>PaymentGroup</code> being checked
     * @param warnings <code>List</code> list of accumulated warning messages
     */
    protected void checkGroupPaymentDate(PaymentGroup paymentGroup, List<String> warnings) {
        Timestamp now = dateTimeService.getCurrentTimestamp();

        Calendar nowPlus30 = Calendar.getInstance();
        nowPlus30.setTime(now);
        nowPlus30.add(Calendar.DATE, 30);

        Calendar nowMinus30 = Calendar.getInstance();
        nowMinus30.setTime(now);
        nowMinus30.add(Calendar.DATE, -30);

        if (paymentGroup.getPaymentDate() != null) {
            Calendar payDate = Calendar.getInstance();
            payDate.setTime(paymentGroup.getPaymentDate());

            if (payDate.before(nowMinus30)) {
                addWarningMessage(warnings, PdpKeyConstants.MESSAGE_PAYMENT_LOAD_PAYDATE_OVER_30_DAYS_PAST, dateTimeService.toDateString(paymentGroup.getPaymentDate()));
            }

            if (payDate.after(nowPlus30)) {
                addWarningMessage(warnings, PdpKeyConstants.MESSAGE_PAYMENT_LOAD_PAYDATE_OVER_30_DAYS_OUT, dateTimeService.toDateString(paymentGroup.getPaymentDate()));
            }
        }
        else {
            // KFSMI-9997
            // Calculate tomorrow's date to set as payment date rather than null
            Calendar tomorrow = Calendar.getInstance();
            tomorrow.setTime(now);
            tomorrow.add(Calendar.DATE, 1);
            tomorrow.getTime();

            Date paymentDate = new Date(tomorrow.getTime().getTime());
            paymentGroup.setPaymentDate(paymentDate);
        }
    }

    /**
     * @return system parameter value giving the maximum number of notes allowed.
     */
    protected int getMaxNoteLines() {
        String maxLines = parameterService.getParameterValueAsString(KfsParameterConstants.PRE_DISBURSEMENT_ALL.class, PdpParameterConstants.MAX_NOTE_LINES);
        if (StringUtils.isBlank(maxLines)) {
            throw new RuntimeException("System parameter for max note lines is blank");
        }

        return Integer.parseInt(maxLines);
    }

    /**
     * Helper method for substituting message parameters and adding the message to the warning list.
     *
     * @param warnings <code>List</code> of messages to add to
     * @param messageKey resource key for message
     * @param arguments message substitute parameters
     */
    protected void addWarningMessage(List<String> warnings, String messageKey, String... arguments) {
        // Add to global warnings so they will show up on the Payment File Batch Upload screen if
        // the payment file was loaded via that screen
        GlobalVariables.getMessageMap().putWarning(KFSConstants.GLOBAL_MESSAGES, messageKey, arguments);

        String message = kualiConfigurationService.getPropertyValueAsString(messageKey);
        warnings.add(MessageFormat.format(message, (Object[]) arguments));
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
     * Sets the paymentFileLoadDao attribute value.
     *
     * @param paymentFileLoadDao The paymentFileLoadDao to set.
     */
    public void setPaymentFileLoadDao(PaymentFileLoadDao paymentFileLoadDao) {
        this.paymentFileLoadDao = paymentFileLoadDao;
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
     * Sets the dateTimeService attribute value.
     *
     * @param dateTimeService The dateTimeService to set.
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    /**
     * Sets the accountService attribute value.
     *
     * @param accountService The accountService to set.
     */
    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * Sets the subAccountService attribute value.
     *
     * @param subAccountService The subAccountService to set.
     */
    public void setSubAccountService(SubAccountService subAccountService) {
        this.subAccountService = subAccountService;
    }

    /**
     * Sets the objectCodeService attribute value.
     *
     * @param objectCodeService The objectCodeService to set.
     */
    public void setObjectCodeService(ObjectCodeService objectCodeService) {
        this.objectCodeService = objectCodeService;
    }

    /**
     * Sets the subObjectCodeService attribute value.
     *
     * @param subObjectCodeService The subObjectCodeService to set.
     */
    public void setSubObjectCodeService(SubObjectCodeService subObjectCodeService) {
        this.subObjectCodeService = subObjectCodeService;
    }

    /**
     * Sets the kualiConfigurationService attribute value.
     *
     * @param kualiConfigurationService The kualiConfigurationService to set.
     */
    public void setConfigurationService(ConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    /**
     * Sets the bankService attribute value.
     *
     * @param bankService The bankService to set.
     */
    public void setBankService(BankService bankService) {
        this.bankService = bankService;
    }

    /**
     * Sets the originationCodeService attribute value.
     *
     * @param originationCodeService The originationCodeService to set.
     */
    public void setOriginationCodeService(OriginationCodeService originationCodeService) {
        this.originationCodeService = originationCodeService;
    }

    /**
     * Gets the businessObjectService attribute.
     *
     * @return Returns the businessObjectService.
     */
    protected BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * Sets the businessObjectService attribute value.
     *
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public void setDocumentTypeService(DocumentTypeService documentTypeService) {
        this.documentTypeService = documentTypeService;
    }

    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

}
