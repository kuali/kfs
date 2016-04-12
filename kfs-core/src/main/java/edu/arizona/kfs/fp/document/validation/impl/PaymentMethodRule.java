package edu.arizona.kfs.fp.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.util.GlobalVariables;

import edu.arizona.kfs.fp.businessobject.PaymentMethod;
import edu.arizona.kfs.fp.businessobject.PaymentMethodChart;

public class PaymentMethodRule extends MaintenanceDocumentRuleBase {

    protected static final String ERROR_NO_BANK_WHEN_INTERDEPT = "error.document.paymentmethod.no.bank.when.interdept";
    protected static final String ERROR_NOT_PDP_AND_INTERDEPT = "error.document.paymentmethod.not.pdp.and.interdept";
    protected static final String ERROR_FLAGREQUIRED = "error.document.paymentmethod.flagrequired";
    protected static final String WARNING_CLEARING_OBJECT_NOTREQUIRED = "warning.document.paymentmethod.clearing.object.notrequired";
    protected static final String WARNING_CLEARING_ACCOUNT_NOTREQUIRED = "warning.document.paymentmethod.clearing.account.notrequired";
    protected static final String WARNING_CLEARING_CHART_NOTREQUIRED = "warning.document.paymentmethod.clearing.chart.notrequired";
    protected static final String ERROR_CLEARING_OBJECT_REQUIRED = "error.document.paymentmethod.clearing.object.required";
    protected static final String ERROR_CLEARING_ACCOUNT_REQUIRED = "error.document.paymentmethod.clearing.account.required";
    protected static final String ERROR_CLEARING_CHART_REQUIRED = "error.document.paymentmethod.clearing.chart.required";
    protected static final String ERROR_EFFECTIVEDATE_INPAST = "error.document.paymentmethod.effectivedate.inpast";
    protected static final String WARNING_FEE_AMOUNT_NOTREQUIRED = "warning.document.paymentmethod.fee.amount.notrequired";
    protected static final String WARNING_FEE_EXPOBJ_NOTREQUIRED = "warning.document.paymentmethod.fee.expobj.notrequired";
    protected static final String WARNING_FEE_INCOBJ_NOTREQUIRED = "warning.document.paymentmethod.fee.incobj.notrequired";
    protected static final String WARNING_FEE_ACCOUNT_NOTREQUIRED = "warning.document.paymentmethod.fee.account.notrequired";
    protected static final String WARNING_FEE_CHART_NOTREQUIRED = "warning.document.paymentmethod.fee.chart.notrequired";
    protected static final String ERROR_FEE_AMOUNT_REQUIRED = "error.document.paymentmethod.fee.amount.required";
    protected static final String ERROR_FEE_EXPOBJ_REQUIRED = "error.document.paymentmethod.fee.expobj.required";
    protected static final String ERROR_FEE_INCOBJ_REQUIRED = "error.document.paymentmethod.fee.incobj.required";
    protected static final String ERROR_FEE_ACCOUNT_REQUIRED = "error.document.paymentmethod.fee.account.required";
    protected static final String ERROR_FEE_CHART_REQUIRED = "error.document.paymentmethod.fee.chart.required";
    protected static final String DOCUMENT_NEW_MAINTAINABLE_OBJECT = "document.newMaintainableObject";

    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        boolean continueRouting = super.processCustomRouteDocumentBusinessRules(document);
        PaymentMethod paymentMethod = (PaymentMethod)document.getNewMaintainableObject().getBusinessObject();
        // checks on the main record
        GlobalVariables.getMessageMap().clearErrorPath();
        GlobalVariables.getMessageMap().addToErrorPath(DOCUMENT_NEW_MAINTAINABLE_OBJECT);
        
        continueRouting &= sanityCheckFlags( paymentMethod );
        continueRouting &= checkNeedForBankCode(paymentMethod);
        
        // checks on the chart records
        for ( int i = 0; i < paymentMethod.getPaymentMethodCharts().size(); i++ ) {
            GlobalVariables.getMessageMap().addToErrorPath("paymentMethodCharts["+i+"]");
            PaymentMethodChart paymentMethodChart = paymentMethod.getPaymentMethodCharts().get(i);
            if ( paymentMethodChart.isNewCollectionRecord() ) {
                continueRouting &= isNewEffectiveDateInFuture( paymentMethod.getPaymentMethodCharts().get(i) );
                continueRouting &= checkFeeInformation( paymentMethod, paymentMethod.getPaymentMethodCharts().get(i) );
                continueRouting &= checkClearingAccountInformation( paymentMethod, paymentMethod.getPaymentMethodCharts().get(i) );
            }
            
            GlobalVariables.getMessageMap().removeFromErrorPath("paymentMethodCharts["+i+"]");
        }
        GlobalVariables.getMessageMap().removeFromErrorPath(DOCUMENT_NEW_MAINTAINABLE_OBJECT);
        
        return continueRouting;
    }
    
    protected boolean isNewEffectiveDateInFuture( PaymentMethodChart paymentMethodChart ) {        
        // check if new, if so, they must have a future date
        if ( paymentMethodChart.getEffectiveDate() != null ) {
            if ( paymentMethodChart.getEffectiveDate().before( getDateTimeService().getCurrentDate() ) ) {
                GlobalVariables.getMessageMap().putError("effectiveDate", ERROR_EFFECTIVEDATE_INPAST, (String[])null);            
                return false;
            }
        }
        return true;
    }
    
    protected boolean checkFeeInformation( PaymentMethod paymentMethod, PaymentMethodChart paymentMethodChart ) {
        boolean continueRouting = true;
        if ( paymentMethod.isAssessedFees() ) {
            if ( StringUtils.isEmpty( paymentMethodChart.getFeeIncomeChartOfAccountsCode() ) ) {
                GlobalVariables.getMessageMap().putError("feeIncomeChartOfAccountsCode", ERROR_FEE_CHART_REQUIRED, (String[])null);
                continueRouting = false;
            }
            if ( StringUtils.isEmpty( paymentMethodChart.getFeeIncomeAccountNumber() ) ) {
                GlobalVariables.getMessageMap().putError("feeIncomeAccountNumber", ERROR_FEE_ACCOUNT_REQUIRED, (String[])null);
                continueRouting = false;
            }
            if ( StringUtils.isEmpty( paymentMethodChart.getFeeIncomeFinancialObjectCode() ) ) {
                GlobalVariables.getMessageMap().putError("feeIncomeFinancialObjectCode", ERROR_FEE_INCOBJ_REQUIRED, (String[])null);
                continueRouting = false;
            }
            if ( StringUtils.isEmpty( paymentMethodChart.getFeeExpenseFinancialObjectCode() ) ) {
                GlobalVariables.getMessageMap().putError("feeExpenseFinancialObjectCode", ERROR_FEE_EXPOBJ_REQUIRED, (String[])null);
                continueRouting = false;
            }
            if ( StringUtils.isEmpty( paymentMethodChart.getFeeIncomeChartOfAccountsCode() ) ) {
                GlobalVariables.getMessageMap().putError("feeAmount", ERROR_FEE_AMOUNT_REQUIRED, (String[])null);
                continueRouting = false;
            }
        } else {
            if ( StringUtils.isNotEmpty( paymentMethodChart.getFeeIncomeChartOfAccountsCode() ) ) {
                GlobalVariables.getMessageMap().putWarning("feeIncomeChartOfAccountsCode", WARNING_FEE_CHART_NOTREQUIRED, (String[])null);
            }
            if ( StringUtils.isNotEmpty( paymentMethodChart.getFeeIncomeAccountNumber() ) ) {
                GlobalVariables.getMessageMap().putWarning("feeIncomeAccountNumber", WARNING_FEE_ACCOUNT_NOTREQUIRED, (String[])null);
            }
            if ( StringUtils.isNotEmpty( paymentMethodChart.getFeeIncomeFinancialObjectCode() ) ) {
                GlobalVariables.getMessageMap().putWarning("feeIncomeFinancialObjectCode", WARNING_FEE_INCOBJ_NOTREQUIRED, (String[])null);
            }
            if ( StringUtils.isNotEmpty( paymentMethodChart.getFeeExpenseFinancialObjectCode() ) ) {
                GlobalVariables.getMessageMap().putWarning("feeExpenseFinancialObjectCode", WARNING_FEE_EXPOBJ_NOTREQUIRED, (String[])null);
            }
            if ( StringUtils.isNotEmpty( paymentMethodChart.getFeeIncomeChartOfAccountsCode() ) ) {
                GlobalVariables.getMessageMap().putWarning("feeAmount", WARNING_FEE_AMOUNT_NOTREQUIRED, (String[])null);
            }
        }        
        return continueRouting;
    }

    protected boolean checkClearingAccountInformation( PaymentMethod paymentMethod, PaymentMethodChart paymentMethodChart ) {
        boolean continueRouting = true;
        if ( paymentMethod.isOffsetUsingClearingAccount() ) {
            if ( StringUtils.isEmpty( paymentMethodChart.getClearingChartOfAccountsCode() ) ) {
                GlobalVariables.getMessageMap().putError("clearingChartOfAccountsCode", ERROR_CLEARING_CHART_REQUIRED, (String[])null);
                continueRouting = false;
            }
            if ( StringUtils.isEmpty( paymentMethodChart.getClearingAccountNumber() ) ) {
                GlobalVariables.getMessageMap().putError("clearingAccountNumber", ERROR_CLEARING_ACCOUNT_REQUIRED, (String[])null);
                continueRouting = false;
            }
            if ( StringUtils.isEmpty( paymentMethodChart.getClearingFinancialObjectCode() ) ) {
                GlobalVariables.getMessageMap().putError("clearingFinancialObjectCode", ERROR_CLEARING_OBJECT_REQUIRED, (String[])null);
                continueRouting = false;
            }
        } else {
            if ( StringUtils.isNotEmpty( paymentMethodChart.getClearingChartOfAccountsCode() ) ) {
                GlobalVariables.getMessageMap().putWarning("clearingChartOfAccountsCode", WARNING_CLEARING_CHART_NOTREQUIRED, (String[])null);
            }
            if ( StringUtils.isNotEmpty( paymentMethodChart.getClearingAccountNumber() ) ) {
                GlobalVariables.getMessageMap().putWarning("clearingAccountNumber", WARNING_CLEARING_ACCOUNT_NOTREQUIRED, (String[])null);
            }
            if ( StringUtils.isNotEmpty( paymentMethodChart.getClearingFinancialObjectCode() ) ) {
                GlobalVariables.getMessageMap().putWarning("clearingFinancialObjectCode", WARNING_CLEARING_OBJECT_NOTREQUIRED, (String[])null);
            }
        }        
        return continueRouting;
    }
    
    @Override
    public boolean processCustomAddCollectionLineBusinessRules(MaintenanceDocument document, String collectionName, PersistableBusinessObject line) {
        boolean continueAddingLine = true;
        GlobalVariables.getMessageMap().clearErrorPath();
        GlobalVariables.getMessageMap().addToErrorPath(DOCUMENT_NEW_MAINTAINABLE_OBJECT);
        if ( line instanceof PaymentMethodChart ) {
            GlobalVariables.getMessageMap().addToErrorPath(KFSConstants.MAINTENANCE_ADD_PREFIX + collectionName );
            
            continueAddingLine &= isNewEffectiveDateInFuture( ((PaymentMethodChart)line) );
            continueAddingLine &= checkFeeInformation( (PaymentMethod)document.getNewMaintainableObject().getBusinessObject(), (PaymentMethodChart)line );
            continueAddingLine &= checkClearingAccountInformation( (PaymentMethod)document.getNewMaintainableObject().getBusinessObject(), (PaymentMethodChart)line );
            
            GlobalVariables.getMessageMap().removeFromErrorPath(KFSConstants.MAINTENANCE_ADD_PREFIX + collectionName );
        }
        GlobalVariables.getMessageMap().removeFromErrorPath(DOCUMENT_NEW_MAINTAINABLE_OBJECT);
        return continueAddingLine;
    }
    
    protected boolean sanityCheckFlags( PaymentMethod paymentMethod ) {
        // the PDP and interdepartmental flags can not both be set
        if ( paymentMethod.isProcessedUsingPdp() && paymentMethod.isInterdepartmentalPayment() ) {
            GlobalVariables.getMessageMap().putError("processedUsingPdp",ERROR_NOT_PDP_AND_INTERDEPT,(String[])null);
            return false;
        }
        return true;
    }
    
    protected boolean checkNeedForBankCode( PaymentMethod paymentMethod ) {
        // when interdepartmental, it doesn't make sense to have a bank code
        if ( paymentMethod.isInterdepartmentalPayment() && StringUtils.isNotBlank(paymentMethod.getBankCode()) ) {
            GlobalVariables.getMessageMap().putError("bankCode",ERROR_NO_BANK_WHEN_INTERDEPT,(String[])null);
            return false;
        }
        return true;
    }
}

