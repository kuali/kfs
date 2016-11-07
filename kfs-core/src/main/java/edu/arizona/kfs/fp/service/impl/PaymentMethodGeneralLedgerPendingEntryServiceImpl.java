package edu.arizona.kfs.fp.service.impl;

import static org.kuali.kfs.sys.KFSConstants.GL_CREDIT_CODE;
import static org.kuali.kfs.sys.KFSConstants.GL_DEBIT_CODE;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.integration.purap.PurchasingAccountsPayableModuleService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.GeneralLedgerPostingDocument;
import org.kuali.kfs.sys.document.service.AccountingDocumentRuleHelperService;
import org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBaseConstants.GENERAL_LEDGER_PENDING_ENTRY_CODE;
import org.kuali.kfs.sys.service.BankService;
import org.kuali.kfs.sys.service.GeneralLedgerPendingEntryService;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.kfs.sys.service.OptionsService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import edu.arizona.kfs.fp.businessobject.PaymentMethod;
import edu.arizona.kfs.fp.businessobject.PaymentMethodChart;
import edu.arizona.kfs.fp.service.PaymentMethodGeneralLedgerPendingEntryService;


@NonTransactional
public class PaymentMethodGeneralLedgerPendingEntryServiceImpl implements PaymentMethodGeneralLedgerPendingEntryService {
    private static final Logger LOG = Logger.getLogger(PaymentMethodGeneralLedgerPendingEntryServiceImpl.class);

    protected static final String DEFAULT_PAYMENT_METHOD_IF_MISSING = "A"; // check/ACH
    protected static final String PRNC = "PRNC";
    protected static final String CMNC = "CMNC";
    protected static final String AP_CREDIT_CARD_BANK_CODE = "0007";
    protected static final String AP_CREDIT_CARD_PAYMENT_METHOD_CODE = "C";
    
    protected GeneralLedgerPendingEntryService generalLedgerPendingEntryService;
    protected ObjectCodeService objectCodeService;
    protected OptionsService optionsService;
    protected BusinessObjectService businessObjectService;
    protected BankService bankService;
    protected PurchasingAccountsPayableModuleService purchasingAccountsPayableModuleService;
    

    public boolean isPaymentMethodProcessedUsingPdp(String paymentMethodCode) {
        if ( StringUtils.isBlank(paymentMethodCode) ) {
            paymentMethodCode = DEFAULT_PAYMENT_METHOD_IF_MISSING;
        }
        PaymentMethod pm = getBusinessObjectService().findBySinglePrimaryKey(PaymentMethod.class, paymentMethodCode);
        if ( pm != null ) {
            return pm.isProcessedUsingPdp();
        }
        return false;
    }
    
    /**
     * This implementation will also return null if the bank code on the payment method record does not exist in the bank table.
     */
    public Bank getBankForPaymentMethod(String paymentMethodCode) {
        if ( StringUtils.isBlank(paymentMethodCode) ) {
            paymentMethodCode = DEFAULT_PAYMENT_METHOD_IF_MISSING;
        }
        PaymentMethod pm = getBusinessObjectService().findBySinglePrimaryKey(PaymentMethod.class, paymentMethodCode);
        if ( pm != null ) {
            // if no bank code, short circuit and return null
            if ( pm.getBankCode() != null ) {
                return pm.getBank();
            }
        }
        return null;
    }
    
    /**
     * Generates additional document-level GL entries for the DV, depending on the payment method code. 
     * 
     * Return true if GLPE's are generated successfully (i.e. there are either 0 GLPE's or 1 GLPE in disbursement voucher document)
     * 
     * @param financialDocument submitted financial document
     * @param sequenceHelper helper class to keep track of GLPE sequence
     * @return true if GLPE's are generated successfully
     */
    public boolean generatePaymentMethodSpecificDocumentGeneralLedgerPendingEntries(
            AccountingDocument document, String paymentMethodCode, String bankCode, String bankCodePropertyName, 
            GeneralLedgerPendingEntry templatePendingEntry, 
            boolean feesWaived, boolean reverseCharge, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        return generatePaymentMethodSpecificDocumentGeneralLedgerPendingEntries(document, paymentMethodCode, bankCode, bankCodePropertyName, templatePendingEntry, feesWaived, reverseCharge, sequenceHelper, null, null);
    }

    public boolean generatePaymentMethodSpecificDocumentGeneralLedgerPendingEntries(AccountingDocument document, 
            String paymentMethodCode, 
            String bankCode, 
            String bankCodePropertyName, 
            GeneralLedgerPendingEntry templatePendingEntry, 
            boolean feesWaived, 
            boolean reverseCharge, 
            GeneralLedgerPendingEntrySequenceHelper sequenceHelper, 
            KualiDecimal bankOffsetAmount, 
            Map<String, KualiDecimal> actualTotalsByChart) {

        if ( StringUtils.isBlank(paymentMethodCode) ) {
            paymentMethodCode = DEFAULT_PAYMENT_METHOD_IF_MISSING;
        }
        PaymentMethod pm = getBusinessObjectService().findBySinglePrimaryKey(PaymentMethod.class, paymentMethodCode);
        // no payment method? abort.
        if ( pm == null ) {
            return false;
        }
        
        if ( pm.isAssessedFees() ) {
            if ( !feesWaived ) {
                generateFeeAssessmentEntries(pm, document, templatePendingEntry, sequenceHelper, reverseCharge);
            }                        
        }
        
        if ( pm.isOffsetUsingClearingAccount() ) {
            generateClearingAccountOffsetEntries(pm, document, sequenceHelper, actualTotalsByChart);
        }
        
        if ( !pm.isProcessedUsingPdp() && StringUtils.isNotBlank( bankCode ) ) {
            generateDocumentBankOffsetEntries(document,bankCode,bankCodePropertyName,templatePendingEntry.getFinancialDocumentTypeCode(), sequenceHelper, bankOffsetAmount, reverseCharge );
        }
        
        return true;
    }
    
    /**
     * Generates the GL entries to charge the department for the foreign draft and credit the Wire Charge
     * Fee Account as specified by system parameters.
     * 
     * @param document Document into which to add the generated GL Entries.
     */
    protected boolean generateFeeAssessmentEntries(PaymentMethod pm, AccountingDocument document, GeneralLedgerPendingEntry templatePendingEntry, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, boolean reverseEntries) {
        LOG.debug("generateForeignDraftChargeEntries started");
        
        Integer fiscalYear = optionsService.getCurrentYearOptions().getUniversityFiscalYear();
        SystemOptions options = optionsService.getOptions(fiscalYear);
        PaymentMethodChart pmc = pm.getPaymentMethodChartInfo(templatePendingEntry.getChartOfAccountsCode(), new java.sql.Date( document.getDocumentHeader().getWorkflowDocument().getDateCreated().toDate().getTime()));
        if ( pmc == null ) {
            LOG.warn( "No Applicable PaymentMethodChart found for chart: " + templatePendingEntry.getChartOfAccountsCode() + " and date: " + document.getDocumentHeader().getWorkflowDocument().getDateCreated() );
            return false;
        }
        // Get all the parameters which control these entries
        String feeIncomeChartCode = pmc.getFeeIncomeChartOfAccountsCode();
        String feeIncomeAccountNumber = pmc.getFeeIncomeAccountNumber();
        String feeExpenseObjectCode = pmc.getFeeExpenseFinancialObjectCode();
        String feeIncomeObjectCode = pmc.getFeeIncomeFinancialObjectCode();
        KualiDecimal feeAmount = pmc.getFeeAmount();

        // skip creation if the fee has been set to zero
        if ( !KualiDecimal.ZERO.equals(feeAmount) ) {
            // grab the explicit entry for the first accounting line and adjust for the foreign draft fee
            GeneralLedgerPendingEntry chargeEntry = new GeneralLedgerPendingEntry(document.getGeneralLedgerPendingEntry(0));        
            chargeEntry.setTransactionLedgerEntrySequenceNumber(sequenceHelper.getSequenceCounter());
            
            // change the object code (expense to the department)
            chargeEntry.setFinancialObjectCode(feeExpenseObjectCode);
            chargeEntry.setFinancialSubObjectCode(GENERAL_LEDGER_PENDING_ENTRY_CODE.getBlankFinancialSubObjectCode());
            chargeEntry.setTransactionLedgerEntryDescription( StringUtils.left( "Automatic debit for " + pm.getPaymentMethodName() + " fee", 40 ));
            chargeEntry.setFinancialBalanceTypeCode(options.getActualFinancialBalanceTypeCd());
    
            // retrieve object type
            ObjectCode objectCode = getObjectCodeService().getByPrimaryIdForCurrentYear(chargeEntry.getChartOfAccountsCode(), chargeEntry.getFinancialObjectCode());
            if ( objectCode == null ) {
                LOG.fatal("Specified offset object code: " + chargeEntry.getChartOfAccountsCode() + "-" + chargeEntry.getFinancialObjectCode() + " does not exist - failed to generate foreign draft fee entries", new RuntimeException() );
                return false;
            }       
            chargeEntry.setFinancialObjectTypeCode(objectCode.getFinancialObjectTypeCode());
            
            // Set the amount from the parameter
            chargeEntry.setTransactionLedgerEntryAmount(feeAmount);
            chargeEntry.setTransactionDebitCreditCode(reverseEntries?GL_CREDIT_CODE:GL_DEBIT_CODE);
    
            document.addPendingEntry(chargeEntry);
            sequenceHelper.increment();
    
            // handle the offset entry
            GeneralLedgerPendingEntry offsetEntry = new GeneralLedgerPendingEntry(chargeEntry);
            getGeneralLedgerPendingEntryService().populateOffsetGeneralLedgerPendingEntry(document.getPostingYear(), chargeEntry, sequenceHelper, offsetEntry);
    
            document.addPendingEntry(offsetEntry);
            sequenceHelper.increment();
            
            // Now, create the income entry in the AP Foreign draft fee account
            
            GeneralLedgerPendingEntry feeIncomeEntry = new GeneralLedgerPendingEntry(document.getGeneralLedgerPendingEntry(0));
            feeIncomeEntry.setTransactionLedgerEntrySequenceNumber(sequenceHelper.getSequenceCounter());
    
            feeIncomeEntry.setChartOfAccountsCode(feeIncomeChartCode);
            feeIncomeEntry.setAccountNumber(feeIncomeAccountNumber);
            feeIncomeEntry.setFinancialObjectCode(feeIncomeObjectCode);
            feeIncomeEntry.setFinancialSubObjectCode(GENERAL_LEDGER_PENDING_ENTRY_CODE.getBlankFinancialSubObjectCode());
            feeIncomeEntry.setSubAccountNumber(GENERAL_LEDGER_PENDING_ENTRY_CODE.getBlankSubAccountNumber());
            feeIncomeEntry.setProjectCode(GENERAL_LEDGER_PENDING_ENTRY_CODE.getBlankProjectCode());
    
            // retrieve object type
            objectCode = getObjectCodeService().getByPrimaryIdForCurrentYear(feeIncomeChartCode, feeIncomeObjectCode);
            if ( objectCode == null ) {
                LOG.fatal("Specified income object code: " + feeIncomeChartCode + "-" + feeIncomeObjectCode + " does not exist - failed to generate foreign draft income entries", new RuntimeException() );
                return false;
            }
            feeIncomeEntry.setFinancialObjectTypeCode(objectCode.getFinancialObjectTypeCode());       
            feeIncomeEntry.setTransactionLedgerEntryAmount(feeAmount);
            feeIncomeEntry.setTransactionDebitCreditCode(reverseEntries?GL_DEBIT_CODE:GL_CREDIT_CODE);
            feeIncomeEntry.setFinancialBalanceTypeCode(options.getActualFinancialBalanceTypeCd());
    
            document.addPendingEntry(feeIncomeEntry);
            sequenceHelper.increment();
            
            // create the offset entry
            offsetEntry = new GeneralLedgerPendingEntry(feeIncomeEntry);
            getGeneralLedgerPendingEntryService().populateOffsetGeneralLedgerPendingEntry(document.getPostingYear(), feeIncomeEntry, sequenceHelper, offsetEntry);
    
            document.addPendingEntry(offsetEntry);
            sequenceHelper.increment();
        }
        return true;
    }
    
    /**
     * Adds up the amounts of all cash to offset GeneralLedgerPendingEntry records on the given AccountingDocument
     * 
     * Copied from the GL Pending entry service since that one does not make any distinction between
     * expense and encumbrance balance types
     * 
     * @param glPostingDocument the accounting document total the offset to cash amount for
     * @return the offset to cash amount, where debited values have been subtracted and credited values have been added
     */
    protected Map<String,KualiDecimal> getNonOffsetActualTotalsByChart(GeneralLedgerPostingDocument glPostingDocument) {
    	Integer fiscalYear = optionsService.getCurrentYearOptions().getUniversityFiscalYear();
        SystemOptions options = optionsService.getOptions(fiscalYear);
        Map<String,KualiDecimal> totals = new HashMap<String, KualiDecimal>();
        for (GeneralLedgerPendingEntry glpe : glPostingDocument.getGeneralLedgerPendingEntries()) {
            if ( options.getActualFinancialBalanceTypeCd().equals(glpe.getFinancialBalanceTypeCode()) ) {
                if ( !glpe.isTransactionEntryOffsetIndicator() ) {
                    if ( !totals.containsKey(glpe.getChartOfAccountsCode() ) ) {
                        totals.put(glpe.getChartOfAccountsCode(), KualiDecimal.ZERO);
                    }
                    if (glpe.getTransactionDebitCreditCode().equals(KFSConstants.GL_DEBIT_CODE)) {
                        totals.put(glpe.getChartOfAccountsCode(),totals.get(glpe.getChartOfAccountsCode()).add(glpe.getTransactionLedgerEntryAmount()));
                    } else if (glpe.getTransactionDebitCreditCode().equals(KFSConstants.GL_CREDIT_CODE)) {
                        totals.put(glpe.getChartOfAccountsCode(),totals.get(glpe.getChartOfAccountsCode()).subtract(glpe.getTransactionLedgerEntryAmount()));
                    }
                }
            }
        }
        return totals;
    }    

    /**
     * When the "C" payment method is used for AP Credit Cards - generate the needed entries in the clearing account.
     * 
     * @param document Document into which to add the generated GL Entries.
     * @param sequenceHelper helper class to keep track of GLPE sequence
     */
    public boolean generateClearingAccountOffsetEntries(PaymentMethod pm, AccountingDocument document, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, Map<String,KualiDecimal> actualTotalsByChart) {
    	Integer fiscalYear = optionsService.getCurrentYearOptions().getUniversityFiscalYear();
        SystemOptions options = optionsService.getOptions(fiscalYear);
        
    	if ( actualTotalsByChart == null ) {
            actualTotalsByChart = getNonOffsetActualTotalsByChart(document);
        }

        for ( String chart : actualTotalsByChart.keySet() ) {
            KualiDecimal offsetAmount = actualTotalsByChart.get(chart);
            if ( !KualiDecimal.ZERO.equals(offsetAmount) ) {
                PaymentMethodChart pmc = pm.getPaymentMethodChartInfo(chart, new java.sql.Date( document.getDocumentHeader().getWorkflowDocument().getDateCreated().toDate().getTime() ) );
                if ( pmc == null ) {
                    LOG.warn( "No Applicable PaymentMethodChart found for chart: " + chart + " and date: " + document.getDocumentHeader().getWorkflowDocument().getDateCreated() );
                    // skip this line - still attempt for other charts
                    continue;
                }
                String clearingChartCode = pmc.getClearingChartOfAccountsCode();
                String clearingAccountNumber = pmc.getClearingAccountNumber();
                String clearingObjectCode = pmc.getClearingFinancialObjectCode(); // liability object code
                
                GeneralLedgerPendingEntry apOffsetEntry = new GeneralLedgerPendingEntry(document.getGeneralLedgerPendingEntry(0));
                apOffsetEntry.setTransactionLedgerEntrySequenceNumber(new Integer(sequenceHelper.getSequenceCounter()));
    
                apOffsetEntry.setChartOfAccountsCode(clearingChartCode);
                apOffsetEntry.setAccountNumber(clearingAccountNumber);
                apOffsetEntry.setFinancialObjectCode(clearingObjectCode);
                apOffsetEntry.setFinancialSubObjectCode(GENERAL_LEDGER_PENDING_ENTRY_CODE.getBlankFinancialSubObjectCode());
                apOffsetEntry.setSubAccountNumber(GENERAL_LEDGER_PENDING_ENTRY_CODE.getBlankSubAccountNumber());
                apOffsetEntry.setProjectCode(GENERAL_LEDGER_PENDING_ENTRY_CODE.getBlankProjectCode());
    
                // retrieve object type
                ObjectCode objectCode = getObjectCodeService().getByPrimaryIdForCurrentYear(clearingChartCode, clearingObjectCode);
                if ( objectCode == null ) {
                    LOG.fatal("Specified offset object code: " + clearingChartCode + "-" + clearingObjectCode + " does not exist - failed to generate CC offset entries", new RuntimeException() );
                    return false;
                }
                apOffsetEntry.setFinancialObjectTypeCode(objectCode.getFinancialObjectTypeCode());
                apOffsetEntry.setTransactionLedgerEntryAmount(offsetAmount.abs());
                apOffsetEntry.setTransactionDebitCreditCode(offsetAmount.isNegative()?KFSConstants.GL_DEBIT_CODE:KFSConstants.GL_CREDIT_CODE);
                apOffsetEntry.setFinancialBalanceTypeCode(options.getActualFinancialBalanceTypeCd());
                
                // Exclude use tax PREQ/CM with AP PCard and Use Tax
                if ((apOffsetEntry.getFinancialDocumentTypeCode().equals(PRNC) || apOffsetEntry.getFinancialDocumentTypeCode().equals(CMNC)) 
                        && pm.getPaymentMethodCode().equals(AP_CREDIT_CARD_PAYMENT_METHOD_CODE)) {
                	if (getPurchasingAccountsPayableModuleService().hasUseTax(document.getDocumentHeader().getDocumentNumber())) {
                        KualiDecimal totalPreTaxDollarAmount = getPurchasingAccountsPayableModuleService().getTotalPreTaxDollarAmount(document.getDocumentHeader().getDocumentNumber());
                        apOffsetEntry.setTransactionLedgerEntryAmount(totalPreTaxDollarAmount.abs());
                	}
                }
                   
                document.addPendingEntry(apOffsetEntry);
                sequenceHelper.increment();
                
                // handle the offset entry
                GeneralLedgerPendingEntry offsetEntry = new GeneralLedgerPendingEntry(apOffsetEntry);
                getGeneralLedgerPendingEntryService().populateOffsetGeneralLedgerPendingEntry(document.getPostingYear(), apOffsetEntry, sequenceHelper, offsetEntry);
                    
                document.addPendingEntry(offsetEntry);
                sequenceHelper.increment();
            }
        }
        
        return true;
    }

    /**
     * If bank specification is enabled generates bank offsetting entries for the document amount
     * 
     */
    public boolean generateDocumentBankOffsetEntries(AccountingDocument document, String bankCode, String bankCodePropertyName, String documentTypeCode, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, KualiDecimal bankOffsetAmount, boolean reverseCharge ) {
        boolean success = true;

        if (!SpringContext.getBean(BankService.class).isBankSpecificationEnabled()) {
            return success;
        }
        
        // Exclude use tax PREQ with AP PCard and Use Tax
        // Overage amounts are posting to the wrong account
        // After a similar problem was experienced with Unit Price Overage corrections, it was
        // decided that a PRNC with bank 0007 (AP Credit Card) should never create bank offsets 
        if (documentTypeCode.equals(PRNC) && bankCode.equals(AP_CREDIT_CARD_BANK_CODE)) {
        	return success;
        }
        
        Bank bank = getBankService().getByPrimaryId(bankCode);

        if ( bankOffsetAmount == null ) {
            // When it's a use tax PRNC or CMNC, create the bank offset for the total pre tax amount.
            if(PRNC.equals(documentTypeCode) || CMNC.equals(documentTypeCode)) {
	            if (getPurchasingAccountsPayableModuleService().hasUseTax(document.getDocumentHeader().getDocumentNumber())) {
	            	KualiDecimal totalPreTaxDollarAmount = getPurchasingAccountsPayableModuleService().getTotalPreTaxDollarAmount(document.getDocumentHeader().getDocumentNumber());
	            	if(PRNC.equals(documentTypeCode)) {
	            		if (reverseCharge) {
	            			bankOffsetAmount = totalPreTaxDollarAmount;
	            		} else {
	            			bankOffsetAmount = totalPreTaxDollarAmount.negated();
	            		}
	            	} else if (CMNC.equals(documentTypeCode)) {
	            		if (reverseCharge) {
	            			bankOffsetAmount = totalPreTaxDollarAmount.negated();
	            		} else {
	            			bankOffsetAmount = totalPreTaxDollarAmount;
	            		}
	            	}
	            }
            }
            
            if(bankOffsetAmount == null) {
                bankOffsetAmount = getGeneralLedgerPendingEntryService().getOffsetToCashAmount(document).negated();
            }
        }
        if ( !KualiDecimal.ZERO.equals(bankOffsetAmount) ) {
            GeneralLedgerPendingEntry bankOffsetEntry = new GeneralLedgerPendingEntry();
            success &= getGeneralLedgerPendingEntryService()
                    .populateBankOffsetGeneralLedgerPendingEntry(bank, bankOffsetAmount, document, 
                            document.getPostingYear(), sequenceHelper, bankOffsetEntry, bankCodePropertyName);
    
            if (success) {
                AccountingDocumentRuleHelperService accountingDocumentRuleUtil = SpringContext.getBean(AccountingDocumentRuleHelperService.class);
                bankOffsetEntry.setTransactionLedgerEntryDescription(accountingDocumentRuleUtil.formatProperty(KFSKeyConstants.Bank.DESCRIPTION_GLPE_BANK_OFFSET));
                bankOffsetEntry.setFinancialDocumentTypeCode(documentTypeCode);
                document.addPendingEntry(bankOffsetEntry);
                sequenceHelper.increment();
    
                GeneralLedgerPendingEntry offsetEntry = new GeneralLedgerPendingEntry(bankOffsetEntry);
                success &= getGeneralLedgerPendingEntryService().populateOffsetGeneralLedgerPendingEntry(document.getPostingYear(), bankOffsetEntry, sequenceHelper, offsetEntry);
                bankOffsetEntry.setFinancialDocumentTypeCode(documentTypeCode);
                document.addPendingEntry(offsetEntry);
                sequenceHelper.increment();
            }
        }

        return success;
    }
    
    protected GeneralLedgerPendingEntryService getGeneralLedgerPendingEntryService() {
        if ( generalLedgerPendingEntryService == null ) {
            generalLedgerPendingEntryService = SpringContext.getBean(GeneralLedgerPendingEntryService.class);
        }
        return generalLedgerPendingEntryService;
    }
    
    public void setGeneralLedgerPendingEntryService(GeneralLedgerPendingEntryService generalLedgerPendingEntryService) {
    	this.generalLedgerPendingEntryService = generalLedgerPendingEntryService;
    }
    
    protected ObjectCodeService getObjectCodeService() {
        if ( objectCodeService == null ) {
            objectCodeService = SpringContext.getBean(ObjectCodeService.class);
        }
        return objectCodeService;
    }
    
    public void setObjectCodeService(ObjectCodeService objectCodeService) {
    	this.objectCodeService = objectCodeService;
    }
    
    public void setOptionsService(OptionsService optionsService) {
        this.optionsService = optionsService;
    }

    protected BusinessObjectService getBusinessObjectService() {
        if ( businessObjectService == null ) {
            businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        }
        return businessObjectService;
    }
    
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
    	this.businessObjectService = businessObjectService;
    }
    
    protected BankService getBankService() {
        if ( bankService == null ) {
            bankService = SpringContext.getBean(BankService.class);
        }
        return bankService;
    }
    
    public void setBankService(BankService bankService) {
    	this.bankService = bankService;
    }
    
    protected PurchasingAccountsPayableModuleService getPurchasingAccountsPayableModuleService() {
    	if ( purchasingAccountsPayableModuleService == null ) {
    		purchasingAccountsPayableModuleService = SpringContext.getBean(PurchasingAccountsPayableModuleService.class);
    	}
    	return purchasingAccountsPayableModuleService;
    }

    public void setPurchasingAccountsPayableModuleService(PurchasingAccountsPayableModuleService purchasingAccountsPayableModuleService) {
        this.purchasingAccountsPayableModuleService = purchasingAccountsPayableModuleService;
    }
}

