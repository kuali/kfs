package edu.arizona.kfs.fp.document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.fp.document.DisbursementVoucherConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.vnd.businessobject.VendorAddress;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.kfs.vnd.businessobject.VendorHeader;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kns.document.authorization.TransactionalDocumentPresentationController;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.document.DocumentAuthorizer;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentDictionaryService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

import edu.arizona.kfs.fp.businessobject.DisbursementVoucherIncomeType;
import edu.arizona.kfs.fp.businessobject.DisbursementVoucherSourceAccountingLineExtension;
import edu.arizona.kfs.fp.businessobject.PaymentMethod;
import edu.arizona.kfs.fp.service.PaymentMethodGeneralLedgerPendingEntryService;
import edu.arizona.kfs.sys.KFSConstants;
import edu.arizona.kfs.sys.KFSPropertyConstants;
import edu.arizona.kfs.tax.TaxConstants;
import edu.arizona.kfs.tax.TaxPropertyConstants;
import edu.arizona.kfs.tax.document.IncomeTypeContainer;
import edu.arizona.kfs.tax.document.IncomeTypeHandler;
import edu.arizona.kfs.vnd.businessobject.VendorDetailExtension;


/**
 * Document class override to ensure that the bank code is synchronized with the
 * payment method code.
 */
public class DisbursementVoucherDocument extends org.kuali.kfs.fp.document.DisbursementVoucherDocument implements IncomeTypeContainer <DisbursementVoucherIncomeType, String>{

    public static final String DOCUMENT_TYPE_DV_NON_CHECK = "DVNC";
    public static final String BANK = "bank";
    
    private static final long serialVersionUID = 8820340507728738505L;
    private static Logger LOG = Logger.getLogger(DisbursementVoucherDocument.class);
    private static PaymentMethodGeneralLedgerPendingEntryService paymentMethodGeneralLedgerPendingEntryService;
    private transient IncomeTypeHandler <DisbursementVoucherIncomeType, String> incomeTypeHandler; 
    private List <DisbursementVoucherIncomeType> incomeTypes;
    protected PaymentMethod paymentMethod;
    protected boolean dv1099Ind;
    protected String dvPaidYear;

    @Override
    public void prepareForSave() {
        LOG.debug("DisbursementVoucherDocument.prepareForSave()");
        super.prepareForSave();

        DocumentDictionaryService documentDictionaryService = SpringContext.getBean(DocumentDictionaryService.class);
        DocumentAuthorizer docAuth = documentDictionaryService.getDocumentAuthorizer(this);

        // First, only do this if the document is in initiated status - after that, we don't want to 
        // accidentally reset the bank code
        if ( KewApiConstants.ROUTE_HEADER_INITIATED_CD.equals( getDocumentHeader().getWorkflowDocument().getStatus().getCode() )
                || KewApiConstants.ROUTE_HEADER_SAVED_CD.equals( getDocumentHeader().getWorkflowDocument().getStatus().getCode() ) ) {
            // need to check whether the user has the permission to edit the bank code
            // if so, don't synchronize since we can't tell whether the value coming in
            // was entered by the user or not.        
            if ( !docAuth.isAuthorizedByTemplate(this, 
                    KFSConstants.CoreModuleNamespaces.KFS, 
                    KFSConstants.PermissionTemplate.EDIT_BANK_CODE.name, 
                    GlobalVariables.getUserSession().getPrincipalId()  ) ) {
                synchronizeBankCodeWithPaymentMethod();        
            } else {
                refreshReferenceObject( BANK );
            }
        } 
        else{           
            TransactionalDocumentPresentationController presentationController = (TransactionalDocumentPresentationController) documentDictionaryService.getDocumentPresentationController(this);
            if(presentationController.getEditModes(this).contains(KFSConstants.Authorization.PAYMENT_METHOD_EDIT_MODE)){
                synchronizeBankCodeWithPaymentMethod();
            }
        }
        
        // need to initialize extension primary key values because OJB doesn't do a good job 
        for (Object o : getSourceAccountingLines()) {
            SourceAccountingLine accountingLine = (SourceAccountingLine) o;
            DisbursementVoucherSourceAccountingLineExtension accountingLineExtension = (DisbursementVoucherSourceAccountingLineExtension) accountingLine.getExtension();
            accountingLineExtension.setDocumentNumber(accountingLine.getDocumentNumber());
            accountingLineExtension.setSequenceNumber(accountingLine.getSequenceNumber());
        }
        
     // START KATTS-1961 Tag and JSP for DV, PREQ and CM Documents
        getIncomeTypeHandler().removeZeroValuedIncomeTypes();
        // END KATTS-1961
        
        //KATTS-1939 Add New Search Fields to DV, PREQ and CM Documents 
        // Only update paid year if the document is in final status 
        if (KewApiConstants.ROUTE_HEADER_FINAL_CD.equals( getDocumentHeader().getWorkflowDocument().getStatus().getCode() ) ) {
        	if (paidDate != null) {
                setDvPaidYear(getPaidDate().toString().substring(0, 4));                
            }
            else {
               setDvPaidYear(null);
            }
        }        
        for (DisbursementVoucherIncomeType incomeType : getIncomeTypes()) {
            if ((StringUtils.isBlank(incomeType.getIncomeTypeCode()) || incomeType.getIncomeTypeCode().equals(TaxPropertyConstants.INCOME_TYPE_CODE_NOT_REPORTABLE))) {
                setDv1099Ind(false);
            }
            else {
                setDv1099Ind(true);
                break;
            }
        }      
        //END KATTS-1939 
    }
    
    public boolean isDv1099Ind() {
        return dv1099Ind;
    }

	public void setDv1099Ind(boolean dv1099Ind) {
		this.dv1099Ind = dv1099Ind;
	}
	public boolean getDv1099IndForSearching() {
		return dv1099Ind;
	}

	public String getDvPaidYear() {
		return dvPaidYear;
	}

	public void setDvPaidYear(String dvPaidYear) {
		this.dvPaidYear = dvPaidYear;
	}

	protected void synchronizeBankCodeWithPaymentMethod() {
        Bank bank = getPaymentMethodGeneralLedgerPendingEntryService().getBankForPaymentMethod( getDisbVchrPaymentMethodCode() );
        if ( bank != null ) {
            if ( !StringUtils.equals(bank.getBankCode(), getDisbVchrBankCode()) ) {
                setDisbVchrBankCode(bank.getBankCode());
                refreshReferenceObject( BANK );
            }
        } else {
            // no bank code, no bank needed
            setDisbVchrBankCode(null);
            setBank(null);
        }
    }
    
    /**
     * Override to change the doc type based on payment method. This is needed to pick up different offset definitions.
     * 
     * Replacing baseline method completely since has an else clause which needs to be replaced.
     * 
     * @param financialDocument submitted accounting document
     * @param accountingLine accounting line in submitted accounting document
     * @param explicitEntry explicit GLPE
     */
    @Override
    public void customizeExplicitGeneralLedgerPendingEntry(GeneralLedgerPendingEntrySourceDetail accountingLine, GeneralLedgerPendingEntry explicitEntry) {

        /* change document type based on payment method to pick up different offsets */
        if ( getPaymentMethodGeneralLedgerPendingEntryService().isPaymentMethodProcessedUsingPdp(getDisbVchrPaymentMethodCode())) {
            explicitEntry.setFinancialDocumentTypeCode(DisbursementVoucherConstants.DOCUMENT_TYPE_CHECKACH);
        } else { // wire transfer or foreign draft
            explicitEntry.setFinancialDocumentTypeCode(DOCUMENT_TYPE_DV_NON_CHECK);
        }
        if ( LOG.isDebugEnabled() ) {
            LOG.debug("changed doc type on pending entry " + explicitEntry.getTransactionLedgerEntrySequenceNumber() + " to " + explicitEntry.getFinancialDocumentTypeCode() );
        }
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
    @Override
    public boolean generateDocumentGeneralLedgerPendingEntries(GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        if (getGeneralLedgerPendingEntries() == null || getGeneralLedgerPendingEntries().size() < 2) {
            LOG.warn("No gl entries for accounting lines.");
            return true;
        }
        // waive any fees only for wire charges when the waiver flag is set
        boolean feesWaived = DisbursementVoucherConstants.PAYMENT_METHOD_WIRE.equals(getDisbVchrPaymentMethodCode()) 
                && getWireTransfer().isWireTransferFeeWaiverIndicator();

        getPaymentMethodGeneralLedgerPendingEntryService().generatePaymentMethodSpecificDocumentGeneralLedgerPendingEntries(this,getDisbVchrPaymentMethodCode(), getDisbVchrBankCode(),KRADConstants.DOCUMENT_PROPERTY_NAME + "." + KFSPropertyConstants.DISB_VCHR_BANK_CODE, getGeneralLedgerPendingEntry(0), feesWaived, false, sequenceHelper);
        
        return true;
    }
    
    /**
     * Returns the name associated with the payment method code
     * 
     * @return String
     */
    public String getDisbVchrPaymentMethodName() {
        if ( getPaymentMethod() != null ) {
            return getDisbVchrPaymentMethodCode() + " - " + getPaymentMethod().getPaymentMethodName();
        }
        return getDisbVchrPaymentMethodCode();
    }
    
    public PaymentMethod getPaymentMethod() {
        if ( paymentMethod == null || !StringUtils.equals( paymentMethod.getPaymentMethodCode(), getDisbVchrPaymentMethodCode() ) ) {
            paymentMethod = SpringContext.getBean(BusinessObjectService.class).findBySinglePrimaryKey(PaymentMethod.class, getDisbVchrPaymentMethodCode());
        }
        return paymentMethod;
    }
    
    protected PaymentMethodGeneralLedgerPendingEntryService getPaymentMethodGeneralLedgerPendingEntryService() {
        if ( paymentMethodGeneralLedgerPendingEntryService == null ) {
            paymentMethodGeneralLedgerPendingEntryService = SpringContext.getBean(PaymentMethodGeneralLedgerPendingEntryService.class);
        }
        return paymentMethodGeneralLedgerPendingEntryService;
    }
    
    /**
     * Update to baseline method to additionally set the payment method when a vendor is selected.
     */
    @Override
    public void templateVendor(VendorDetail vendor, VendorAddress vendorAddress) {
        super.templateVendor(vendor, vendorAddress);
        if ( vendor != null ) {
            if ( ObjectUtils.isNotNull( vendor.getExtension() ) 
                    && vendor.getExtension() instanceof VendorDetailExtension ) {
                if ( StringUtils.isNotBlank(((VendorDetailExtension)vendor.getExtension()).getDefaultB2BPaymentMethodCode())) {
                    disbVchrPaymentMethodCode = ((VendorDetailExtension)vendor.getExtension()).getDefaultB2BPaymentMethodCode();
                    // Ensure the bank code now matches the new payment method code
                    synchronizeBankCodeWithPaymentMethod();
                }
            }
        }
    }
    
    @Override
    public void doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent) {
        super.doRouteStatusChange(statusChangeEvent);
        
        // set the paid date when the DV will not go through PDP 
        if (getDocumentHeader().getWorkflowDocument().isProcessed()) {
            if (!getPaymentMethodGeneralLedgerPendingEntryService().isPaymentMethodProcessedUsingPdp(getDisbVchrPaymentMethodCode())) {
                setPaidDate(getDateTimeService().getCurrentSqlDate());                
            }
        }
    }
    @Override
    public void addSourceAccountingLine(SourceAccountingLine line) {
        super.addSourceAccountingLine(line);

        if (LOG.isDebugEnabled()) {
            LOG.debug("source accounting line added - account: " + line.getAccountNumber() + ", object code: " + line.getObjectCode());
        }
        
        if (isIncomeTypeUpdateAllowed()) {
            getIncomeTypeHandler().onAccountingLineAdded(line);
        }
    }

    @SuppressWarnings("deprecation")
    private boolean isIncomeTypeUpdateAllowed() {
        boolean retval = false;

        if (LOG.isDebugEnabled()) {
            LOG.debug("in isIncomeTypeUpdateAllowed() - routeStatus=" + getRouteStatus());
        }

        // if document is in initiated status we will automatically populate income types when
        // accounting lines are added, if not we will only populate if 1099 tab is visible
        if (KewApiConstants.ROUTE_HEADER_INITIATED_CD.equals(getRouteStatus())) {
            retval = true;
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("route status is not initiated");
            }

            HashMap<String, String> att = new HashMap<String, String>();
            att.put(KimConstants.AttributeConstants.EDIT_MODE, TaxConstants.Authorization.VIEW_INCOME_TYPES_EDIT_MODE);
            att.put(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME, KFSConstants.DOCUWARE_DV_DOC_TYPE);
            
            // see if current user can view 1099 tab
            retval = getIdentityManagementService().isAuthorizedByTemplateName(
                    GlobalVariables.getUserSession().getPrincipalId(),
                    KRADConstants.KNS_NAMESPACE,
                    KimConstants.PermissionTemplateNames.USE_TRANSACTIONAL_DOCUMENT, 
                    att, 
                    null);
        } 
        
        if (LOG.isDebugEnabled()) {
            LOG.debug("retval=" + retval);
        }
        
        return retval;
    }
    
    public List<DisbursementVoucherIncomeType> getIncomeTypes() {
        if (incomeTypes == null) {
            incomeTypes = new ArrayList<DisbursementVoucherIncomeType>();
        }
        return incomeTypes;
    }

    public void setIncomeTypes(List<DisbursementVoucherIncomeType> incomeTypes) {
        this.incomeTypes = incomeTypes;
    }
    
    @Override
    public String getDocumentIdentifier() {
        return getDocumentNumber();
    }

    @Override
    public String getPaidYear() {
        return getIncomeTypeHandler().getYearFromDate(this.getPaidDate());
    }

    @Override
    public IncomeTypeHandler <DisbursementVoucherIncomeType, String> getIncomeTypeHandler() {
        if (incomeTypeHandler == null) {
            incomeTypeHandler =  new IncomeTypeHandler<DisbursementVoucherIncomeType, String>(this, DisbursementVoucherIncomeType.class){};
        }
        
        return incomeTypeHandler;
    }

    @Override
    public VendorHeader getVendorHeader() {
        return getIncomeTypeHandler().getVendorHeaderFromVendorNumber(getDvPayeeDetail().getDisbVchrPayeeIdNumber());
    }
    
    @Override
    public void processAfterRetrieve() {
        super.processAfterRetrieve();

        // if the DocumentIncomeType list is empty then initialize from the document accounting lines
        if (getIncomeTypes().isEmpty()) {
            if (getDvPayeeDetail() != null) {
                getIncomeTypeHandler().populateIncomeTypes(getSourceAccountingLines());
            }
        }
    }
    
    @Override
    public boolean getReportable1099TransactionsFlag() {
        boolean retval = false;
        
        if (getSourceAccountingLines() != null) {
            Iterator <? extends AccountingLine> it = getSourceAccountingLines().iterator();
            
            while (it.hasNext()) {
                if (getIncomeTypeHandler().is1099Reportable(it.next().getFinancialObjectCode())) {
                    retval = true;
                    break;
                }
            }
        }
        
        return retval;
    }
	@Override
	public String getRouteStatus() {
		String retval = KFSConstants.EMPTY_STRING;
		try {
			retval = getDocumentHeader().getWorkflowDocument().getStatus().getCode();
		} catch (Exception ex) {
			LOG.warn(ex);
		}
		return retval;
	}
}
