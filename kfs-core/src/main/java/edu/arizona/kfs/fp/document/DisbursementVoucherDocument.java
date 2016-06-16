package edu.arizona.kfs.fp.document;

import org.apache.log4j.Logger;
import org.kuali.kfs.fp.document.DisbursementVoucherConstants;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import edu.arizona.kfs.fp.businessobject.DisbursementVoucherSourceAccountingLineExtension;
import edu.arizona.kfs.fp.businessobject.PaymentMethod;
import edu.arizona.kfs.fp.service.PaymentMethodGeneralLedgerPendingEntryService;
import edu.arizona.kfs.sys.KFSConstants;
import edu.arizona.kfs.sys.KFSPropertyConstants;
import edu.arizona.kfs.vnd.businessobject.VendorDetailExtension;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.vnd.businessobject.VendorAddress;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.kns.document.authorization.DocumentAuthorizer;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.kns.document.authorization.TransactionalDocumentPresentationController;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.coreservice.framework.parameter.ParameterConstants.COMPONENT;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange;


/**
 * Document class override to ensure that the bank code is synchronized with the
 * payment method code.
 */
// This annotation is needed to make parameter lookups work properly
@COMPONENT( component = "DisbursementVoucher" )
public class DisbursementVoucherDocument extends org.kuali.kfs.fp.document.DisbursementVoucherDocument {

    public static final String DOCUMENT_TYPE_DV_NON_CHECK = "DVNC";
    
    private static final long serialVersionUID = 8820340507728738505L;
    private static Logger LOG = Logger.getLogger(DisbursementVoucherDocument.class);
    private static PaymentMethodGeneralLedgerPendingEntryService paymentMethodGeneralLedgerPendingEntryService;

    @Override
    public void prepareForSave() {
        LOG.debug("DisbursementVoucherDocument.prepareForSave()");
        super.prepareForSave();

        DocumentHelperService documentHelperService = SpringContext.getBean(DocumentHelperService.class);
        DocumentAuthorizer docAuth = documentHelperService.getDocumentAuthorizer(this);

        // First, only do this if the document is in initiated status - after that, we don't want to 
        // accidentally reset the bank code
        if ( KewApiConstants.ROUTE_HEADER_INITIATED_CD.equals( getDocumentHeader().getWorkflowDocument().getStatus().getCode() )
                || KewApiConstants.ROUTE_HEADER_SAVED_CD.equals( getDocumentHeader().getWorkflowDocument().getStatus().getCode() ) ) {
            // need to check whether the user has the permission to edit the bank code
            // if so, don't synchronize since we can't tell whether the value coming in
            // was entered by the user or not.        
            if ( !docAuth.isAuthorizedByTemplate(this, 
                    KFSConstants.ParameterNamespaces.KFS, 
                    KFSConstants.PermissionTemplate.EDIT_BANK_CODE.name, 
                    GlobalVariables.getUserSession().getPrincipalId()  ) ) {
                synchronizeBankCodeWithPaymentMethod();        
            } else {
                refreshReferenceObject( "bank" );
            }
        } 
        else{           
            TransactionalDocumentPresentationController presentationController = (TransactionalDocumentPresentationController) documentHelperService.getDocumentPresentationController(this);
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
    }
    
    protected void synchronizeBankCodeWithPaymentMethod() {
        Bank bank = getPaymentMethodGeneralLedgerPendingEntryService().getBankForPaymentMethod( getDisbVchrPaymentMethodCode() );
        if ( bank != null ) {
            if ( !StringUtils.equals(bank.getBankCode(), getDisbVchrBankCode()) ) {
                setDisbVchrBankCode(bank.getBankCode());
                refreshReferenceObject( "bank" );
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
    
    protected PaymentMethod paymentMethod;
    
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
}
