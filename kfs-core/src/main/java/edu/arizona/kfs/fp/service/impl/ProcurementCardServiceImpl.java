package edu.arizona.kfs.fp.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.service.BalanceTypeService;
import org.kuali.kfs.fp.businessobject.ProcurementCardTargetAccountingLine;
import org.kuali.kfs.fp.document.ProcurementCardDocument;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.businessobject.TaxRegion;
import org.kuali.kfs.sys.businessobject.TaxRegionRate;
import org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBaseConstants;
import org.kuali.kfs.sys.service.GeneralLedgerPendingEntryService;
import org.kuali.rice.core.api.parameter.ParameterEvaluatorService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.core.api.util.type.KualiDecimal;

import edu.arizona.kfs.fp.businessobject.ProcurementCardTransactionDetail;
import edu.arizona.kfs.fp.service.ProcurementCardService;
import edu.arizona.kfs.sys.KFSConstants;
import edu.arizona.kfs.sys.KFSParameterKeyConstants;
import edu.arizona.kfs.sys.KFSPropertyConstants;

public class ProcurementCardServiceImpl implements ProcurementCardService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ProcurementCardServiceImpl.class);
    
    private static final String NON_TAXABLE_OBJECT_CODES = "NON_TAXABLE_OBJECT_CODES";
    private static final String TAX_REGION_CODE = "taxRegionCode";
    private static final String AZ = "AZ";
    
    private ParameterService parameterService;
    private ParameterEvaluatorService parameterEvaluatorService;
    private BusinessObjectService businessObjectService;
    private GeneralLedgerPendingEntryService generalLedgerPendingEntryService;

    
    @Override
    public boolean generateUseTaxPendingEntries(ProcurementCardDocument procurementCardDocument, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        boolean success = true;
        if (isUseTaxTransaction(procurementCardDocument)) {
            //load the use tax tax region
            Map<String, String> pkMap = new HashMap<String, String>();
            pkMap.put(TAX_REGION_CODE, parameterService.getParameterValueAsString(ProcurementCardDocument.class, KFSParameterKeyConstants.GL_USETAX_TAX_REGION));
            TaxRegion taxRegion = (TaxRegion) businessObjectService.findByPrimaryKey(TaxRegion.class, pkMap);
            Date postDate = getProcurementCardTransactionPostingDetailDate(procurementCardDocument);
            TaxRegionRate taxRegionRate = taxRegion.getEffectiveTaxRegionRate(new java.sql.Date(postDate.getTime()));
            //generate pending gl entries for each source account line
            for (ProcurementCardTargetAccountingLine sourceDetail : (List<ProcurementCardTargetAccountingLine>)procurementCardDocument.getTargetAccountingLines()) {
                success &= generateUseTaxPendingEntries(procurementCardDocument, sourceDetail, sequenceHelper, taxRegion, taxRegionRate);
            }
        }
        return success;
    }
    
    private Date getProcurementCardTransactionPostingDetailDate(ProcurementCardDocument procurementCardDocument) {
        Date date = null;
        
        if(!procurementCardDocument.getTransactionEntries().isEmpty()) {
            date = ((ProcurementCardTransactionDetail)procurementCardDocument.getTransactionEntries().get(0)).getTransactionPostingDate();
        }
        
        return date;
    }
    
    /**
     * For the given PCDO and specific accounting line create and add pending entries for use tax based on the
     * tax region rate given.
     */
    protected boolean generateUseTaxPendingEntries(ProcurementCardDocument procurementCardDocument, ProcurementCardTargetAccountingLine sourceDetail, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, 
            TaxRegion taxRegion, TaxRegionRate taxRegionRate) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("generateUseTaxEntries started");
        }
        //Is the Object Code Non Taxable?
        String financialObjectCode = sourceDetail.getFinancialObjectCode();
        if (StringUtils.isBlank(financialObjectCode) || parameterEvaluatorService.getParameterEvaluator(ProcurementCardDocument.class, NON_TAXABLE_OBJECT_CODES, financialObjectCode).evaluationSucceeds()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("isUseTaxTransaction() Use Tax Excluded Object Code - not posted");
            }
            return true;
        }
        
        KualiDecimal generatedTransactionAmount = new KualiDecimal(sourceDetail.getAmount().bigDecimalValue().multiply(taxRegionRate.getTaxRate())).abs();
        
        GeneralLedgerPendingEntry e = new GeneralLedgerPendingEntry();
        generalLedgerPendingEntryService.populateExplicitGeneralLedgerPendingEntry(procurementCardDocument, sourceDetail, sequenceHelper, e);
        e.setTransactionLedgerEntryAmount(generatedTransactionAmount);
        e.setTransactionLedgerEntryDescription(getUseTaxDescription(sourceDetail.getAmount(), taxRegionRate.getTaxRate()));
        procurementCardDocument.customizeExplicitGeneralLedgerPendingEntry(sourceDetail, e);
        procurementCardDocument.addPendingEntry(e);
        sequenceHelper.increment();
    
        // handle the offset entry
        GeneralLedgerPendingEntry offsetEntry = new GeneralLedgerPendingEntry(e);
        generalLedgerPendingEntryService.populateOffsetGeneralLedgerPendingEntry(procurementCardDocument.getPostingYear(), e, sequenceHelper, offsetEntry);
        procurementCardDocument.customizeOffsetGeneralLedgerPendingEntry(sourceDetail, e, offsetEntry);
    
        procurementCardDocument.addPendingEntry(offsetEntry);
        sequenceHelper.increment();
            
        //Create the entry and offset for the Use Tax account
        e = new GeneralLedgerPendingEntry(e);
        e.setTransactionLedgerEntrySequenceNumber(sequenceHelper.getSequenceCounter());
        e.setAccountNumber(taxRegion.getAccountNumber());
        // Sub-Accounts, Sub-Object Codes or Project Codes on PCDO with Use Tax Cause GL Scrubber Errors
        e.setSubAccountNumber(AccountingDocumentRuleBaseConstants.GENERAL_LEDGER_PENDING_ENTRY_CODE.getBlankSubAccountNumber());
        e.setProjectCode(AccountingDocumentRuleBaseConstants.GENERAL_LEDGER_PENDING_ENTRY_CODE.getBlankProjectCode());
        e.setChartOfAccountsCode(taxRegion.getChartOfAccountsCode());
        e.setFinancialSubObjectCode(AccountingDocumentRuleBaseConstants.GENERAL_LEDGER_PENDING_ENTRY_CODE.getBlankFinancialSubObjectCode());
        e.setFinancialObjectCode(taxRegion.getFinancialObjectCode());
        e.refreshReferenceObject(KFSPropertyConstants.FINANCIAL_OBJECT);
        e.setFinancialObjectTypeCode(e.getFinancialObject().getFinancialObjectTypeCode());
        if (StringUtils.equals(e.getTransactionDebitCreditCode(), KFSConstants.GL_DEBIT_CODE)) {
            e.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
        } else {
            e.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
        }
        e.setFinancialBalanceTypeCode(BalanceTypeService.ACTUAL_BALANCE_TYPE);

        procurementCardDocument.addPendingEntry(e);
        sequenceHelper.increment();
        
        // create the offset entry
        offsetEntry = new GeneralLedgerPendingEntry(e);
        generalLedgerPendingEntryService.populateOffsetGeneralLedgerPendingEntry(procurementCardDocument.getPostingYear(), e, sequenceHelper, offsetEntry);
        procurementCardDocument.customizeOffsetGeneralLedgerPendingEntry(sourceDetail, e, offsetEntry);
        procurementCardDocument.addPendingEntry(offsetEntry);
        sequenceHelper.increment();
        
        return true;
    }
    
    @Override
    public boolean isUseTaxTransaction(ProcurementCardDocument procurementCardDocument) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("isUseTaxTransaction() started");
        }
        
        // Now check the sales tax amount and tax exempt indicator
        for (Iterator<ProcurementCardTransactionDetail> iter = procurementCardDocument.getTransactionEntries().iterator(); iter.hasNext();) {
            ProcurementCardTransactionDetail transactionEntry = iter.next();
            //Was Sales Tax supplied?
            if (transactionEntry.getTransactionSalesTaxAmount() != null 
                    && !transactionEntry.getTransactionSalesTaxAmount().equals(KualiDecimal.ZERO)) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("isUseTaxTransaction() Sales Tax greater than zero - not posted");
                }
                return false;
            }
            //Was Sales Tax entered?
            if (transactionEntry.getTransactionEditableSalesTaxAmount() != null
                    && !transactionEntry.getTransactionEditableSalesTaxAmount().equals(KualiDecimal.ZERO)) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("isUseTaxTransaction() Editable Sales Tax greater than zero - not posted");
                }
                return false;
            }
            //Is Tax Exempt Indicator on?
            if (transactionEntry.getTransactionTaxExemptIndicator()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("isUseTaxTransaction() Use Tax Exempt - not posted");
                }
                return false;
            }
            //Is AZ Vendor?
            if (StringUtils.equals(transactionEntry.getProcurementCardVendor().getVendorStateCode(), AZ)) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("isUseTaxTransaction() AZ Vendor - not posted");
                }
                return false;
            }
        }
        return true; 
    }
    
    /**
     * Returns a description for the ledger entry in the form of 'Use Tax 6.600% on 20.00'
     */
    protected String getUseTaxDescription(KualiDecimal amount, BigDecimal taxRate) {
        return "Use Tax " + taxRate.multiply(new BigDecimal(100)).toString() + "% on " + amount.toString();
    }
    
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
    
    public void setParameterEvaluatorService(ParameterEvaluatorService parameterEvaluatorService) {
        this.parameterEvaluatorService = parameterEvaluatorService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public void setGeneralLedgerPendingEntryService(GeneralLedgerPendingEntryService generalLedgerPendingEntryService) {
        this.generalLedgerPendingEntryService = generalLedgerPendingEntryService;
    }
}
