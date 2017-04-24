package edu.arizona.kfs.fp.document;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.kuali.kfs.fp.batch.ProcurementCardLoadStep;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.businessobject.TargetAccountingLine;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.api.group.GroupService;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;

import edu.arizona.kfs.fp.businessobject.ProcurementCardHolder;
import org.apache.commons.lang.StringUtils;

import edu.arizona.kfs.fp.businessobject.ProcurementCardTransactionDetail;
import edu.arizona.kfs.fp.service.ProcurementCardService;

public class ProcurementCardDocument extends org.kuali.kfs.fp.document.ProcurementCardDocument {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ProcurementCardDocument.class);
    
    /**
     * Default serial version UID, courtesy of Eclipse
     */
    private static final long serialVersionUID = 3228920537446507176L;
    
    private static final String HAS_RECONCILER_NODE = "HasReconciler";
    private static final String FINAL_ACCOUNTING_PERIOD = "13";
    private static final String ALLOW_BACKPOST_DAYS = "ALLOW_BACKPOST_DAYS";
    
    protected ProcurementCardHolder procurementCardHolder;
    
    @Override
    public ProcurementCardHolder getProcurementCardHolder() {
        return procurementCardHolder;
    }

    public void setProcurementCardHolder(ProcurementCardHolder procurementCardHolder) {
        this.procurementCardHolder = procurementCardHolder;
    }
    
    /**
     * @return the previous fiscal year used with all GLPE
     */
    protected static final Integer getPreviousFiscalYear() {
        int i = SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear().intValue() - 1;
        return new Integer(i);
    }

    @Override
    public void customizeExplicitGeneralLedgerPendingEntry(GeneralLedgerPendingEntrySourceDetail postable, GeneralLedgerPendingEntry explicitEntry) {
        Date temp = getProcurementCardTransactionPostingDetailDate();
        
        if( temp != null && allowBackpost(temp) ) {
            Integer prevFiscYr = getPreviousFiscalYear();
            
            explicitEntry.setUniversityFiscalPeriodCode(FINAL_ACCOUNTING_PERIOD);
            explicitEntry.setUniversityFiscalYear(prevFiscYr);
            
            String documentDescription = getDocumentHeader().getDocumentDescription();
            if(StringUtils.isNotEmpty(documentDescription) && !documentDescription.contains("FY " + prevFiscYr) ) {
                getDocumentHeader().setDocumentDescription("FY " + prevFiscYr + " " + documentDescription);
            } 
            
            List<SourceAccountingLine> srcLines = getSourceAccountingLines();
            
            for(SourceAccountingLine src : srcLines) {
                src.setPostingYear(prevFiscYr);
            }

            List<TargetAccountingLine> trgLines = getTargetAccountingLines();
            
            for(TargetAccountingLine trg : trgLines) {
                trg.setPostingYear(prevFiscYr);
            }
        }
    }
    
    /**
     * Get Transaction Date - CSU assumes there will be only one
     */
    private Date getProcurementCardTransactionPostingDetailDate() {
        Date date = null;
        
        if(!getTransactionEntries().isEmpty()) {
            date = ((ProcurementCardTransactionDetail)getTransactionEntries().get(0)).getTransactionPostingDate();
        }
        
        return date;
    }

    @Override
    public boolean generateDocumentGeneralLedgerPendingEntries(GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        boolean success = super.generateDocumentGeneralLedgerPendingEntries(sequenceHelper);
        
        success &= SpringContext.getBean(ProcurementCardService.class).generateUseTaxPendingEntries(this, sequenceHelper);
        
        return success;
    } 

    /**
     * Answers true when invoice recurrence details are provided by the user
     * 
     * @see org.kuali.kfs.sys.document.FinancialSystemTransactionalDocumentBase#answerSplitNodeQuestion(java.lang.String)
     */
    @Override
    public boolean answerSplitNodeQuestion(String nodeName) throws UnsupportedOperationException {
        if(LOG.isDebugEnabled()){
            LOG.debug("Answering split node question '" + nodeName +"'.");  
        }
        if (HAS_RECONCILER_NODE.equalsIgnoreCase(nodeName)) {
            return hasReconciler();
        }       
        return super.answerSplitNodeQuestion(nodeName);
    }
    
    /**
     * @return <code>true</code> if this {@link ProcurementCardDocument} has a
     *         reconciler entity, <code>false</code> otherwise.
     */
    private boolean hasReconciler() {
        boolean retCode = true;
        if (ObjectUtils.isNull(getProcurementCardHolder()) || 
            ObjectUtils.isNull(getProcurementCardHolder().getProcurementCardDefault()) ||
            ObjectUtils.isNull(getProcurementCardHolder().getProcurementCardDefault().getReconcilerGroupId()) ||
            ObjectUtils.isNull(getProcurementCardHolder().getProcurementCardDefault().getCardHolderSystemId())) {
            retCode = false;
        }
        else {
            List<String> groupMembers = new ArrayList<String>();
            groupMembers = SpringContext.getBean(GroupService.class).getMemberPrincipalIds(getProcurementCardHolder().getProcurementCardDefault().getReconcilerGroupId());
            if (groupMembers.isEmpty() ||
                (groupMembers.size() == 1 &&
                 groupMembers.get(0).equals(getProcurementCardHolder().getProcurementCardDefault().getCardHolderSystemId()))) {
                retCode = false;
            }
        }
        return retCode;
    } 
    @Override
    public String getDocumentTitle() {       
        return this.buildDocumentTitle(super.getDocumentTitle());
    }

    protected String buildDocumentTitle(String title) { 
        if(this.transactionEntries == null) {
           return title; 
        }
        
        List<ProcurementCardTransactionDetail> procurementCardTransactionDetails = new ArrayList<ProcurementCardTransactionDetail>();
        procurementCardTransactionDetails = this.getTransactionEntries();
        
        String tranNumber = procurementCardTransactionDetails.get(0).getTransactionReferenceNumber().trim();
        String vendorName = StringUtils.trimToEmpty(procurementCardTransactionDetails.get(0).getProcurementCardVendor().getVendorName());
        String totAmount = this.getTotalDollarAmount().toString().trim();

        title = title + " / " + tranNumber + " / " + vendorName + " / $" + totAmount;
         
        return title;
    }
    
    protected boolean allowBackpost(Date tranDate) {
        ParameterService parameterService = SpringContext.getBean(ParameterService.class);
        UniversityDateService universityDateService = SpringContext.getBean(UniversityDateService.class);
       
        String allowBackpostParam = parameterService.getParameterValueAsString(ProcurementCardLoadStep.class, ALLOW_BACKPOST_DAYS);
        int allowBackpost = 0;
        if (StringUtils.isNotEmpty(allowBackpostParam) && StringUtils.isNumeric(allowBackpostParam)) {
            allowBackpost = (Integer.parseInt(allowBackpostParam));
        }
        else {
            LOG.debug("Invalid value for allowBackpostParam: " + allowBackpostParam + ". Returning false");
            return false;
        }

        Calendar today = Calendar.getInstance();
        Integer currentFY = universityDateService.getCurrentUniversityDate().getUniversityFiscalYear();
        java.util.Date priorClosingDateTemp = universityDateService.getLastDateOfFiscalYear(currentFY - 1);
        
        Calendar priorClosingDate = Calendar.getInstance();
        priorClosingDate.setTime(priorClosingDateTemp);

        // adding 1 to set the date to midnight the day after backpost is allowed so that preqs allow backpost on the last day
        Calendar allowBackpostDate = Calendar.getInstance();
        allowBackpostDate.setTime(priorClosingDate.getTime());
        allowBackpostDate.add(Calendar.DATE, allowBackpost + 1);

        Calendar tranCal = Calendar.getInstance();
        tranCal.setTime(tranDate);

        // if today is after the closing date but before/equal to the allowed backpost date and the transaction date is for the
        // prior year, set the year to prior year
        if ((today.compareTo(priorClosingDate) > 0) && (today.compareTo(allowBackpostDate) <= 0) && (tranCal.compareTo(priorClosingDate) <= 0)) {
            LOG.debug("allowBackpost() within range to allow backpost; posting entry to period 12 of previous FY");
            return true;
        }

        LOG.debug("allowBackpost() not within range to allow backpost; posting entry to current FY");
        return false;
    }
}
