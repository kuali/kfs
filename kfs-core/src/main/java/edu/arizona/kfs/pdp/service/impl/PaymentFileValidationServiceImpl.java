/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.arizona.kfs.pdp.service.impl;

import edu.arizona.kfs.gl.service.GlobalTransactionEditService;
import edu.arizona.kfs.sys.KFSKeyConstants;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.*;
import org.kuali.kfs.pdp.businessobject.PaymentDetail;
import org.kuali.kfs.pdp.PdpConstants;
import org.kuali.kfs.pdp.PdpKeyConstants;
import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.kfs.pdp.businessobject.*;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.Message;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.KualiCodeBase;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @see org.kuali.kfs.pdp.batch.service.PaymentFileValidationService
 */
@Transactional
public class PaymentFileValidationServiceImpl extends org.kuali.kfs.pdp.service.impl.PaymentFileValidationServiceImpl {

    private GlobalTransactionEditService globalTransactionEditService;


    /*
     * Needed to override in order to call our version of processAccountSoftEdits(), which has
     * a different method signature than Foundation's provided.
     */
    @Override
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

            processAccountSoftEdits(customer, paymentAccountDetail, warnings, paymentDetail);
        }
    }



    /**
     * Set default fields on account line and perform account field existence checks
     *
     * KATTS Note: I changed the method scope here to expelempify that this extension necessiates
     *             a departure from the foundation signature. This was due to how the legacy GTE
     *             code was implemented, and I wanted to ensure logic changes only occured in the
     *             edu.arizona packgage. The compramise bing that the foundation class's scope was
     *             changed too, but its method args remained the same.
     *
     * @param paymentFile payment file object
     * @param customer payment customer
     * @param paymentAccountDetail <code>PaymentAccountDetail</code> object to process
     * @param warnings <code>List</code> list of accumulated warning messages
     * @param paymentDetail needed for several codes for a call to the GTE service
     */
    private void processAccountSoftEdits(CustomerProfile customer, PaymentAccountDetail paymentAccountDetail, List<String> warnings, PaymentDetail paymentDetail) {
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

                account.refreshReferenceObject("subFundGroup");
                paymentAccountDetail.refreshReferenceObject("objectCode");
                Message result = globalTransactionEditService.isAccountingLineAllowable(
                        paymentDetail.getFinancialSystemOriginCode(),
                        account.getSubFundGroup().getFundGroupCode(),
                        account.getSubFundGroupCode(),
                        paymentDetail.getFinancialDocumentTypeCode(),
                        paymentAccountDetail.getObjectCode().getFinancialObjectTypeCode(),
                        paymentAccountDetail.getObjectCode().getFinancialObjectSubTypeCode());
                if (result != null) {
                    warnings.add(result.getMessage());
                    addWarningMessage(warnings, KFSKeyConstants.ERROR_GLOBAL_TRANSACTION_EDIT_ACCOUNT_NUMBER_CHANGED, paymentAccountDetail.getFinChartCode(), paymentAccountDetail.getAccountNbr());
                    KualiCodeBase objChangeCd = businessObjectService.findBySinglePrimaryKey(AccountingChangeCode.class, PdpConstants.AccountChangeCodes.INVALID_ACCOUNT);
                    replaceAccountingString(objChangeCd, changeRecords, customer, paymentAccountDetail);
                }

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

    public void setGlobalTransactionEditService(GlobalTransactionEditService globalTransactionEditService) {
        this.globalTransactionEditService = globalTransactionEditService;
    }

}
