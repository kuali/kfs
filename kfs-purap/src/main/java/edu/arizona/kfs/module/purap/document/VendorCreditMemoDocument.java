package edu.arizona.kfs.module.purap.document;

import org.kuali.rice.krad.service.DocumentDictionaryService;

import edu.arizona.kfs.fp.service.PaymentMethodGeneralLedgerPendingEntryService;
import edu.arizona.kfs.module.purap.PurapConstants;
import edu.arizona.kfs.sys.KFSConstants;

import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.krad.document.DocumentAuthorizer;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBaseConstants;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;


/**
 * Added payment method code and supporting logic
 */
public class VendorCreditMemoDocument extends org.kuali.kfs.module.purap.document.VendorCreditMemoDocument {

    public static String DOCUMENT_TYPE_NON_CHECK = "CMNC";
    public static final String BANK = "bank";

    protected String paymentMethodCode = "A"; // check
    private static PaymentMethodGeneralLedgerPendingEntryService paymentMethodGeneralLedgerPendingEntryService;

    
    public String getPaymentMethodCode() {
        return paymentMethodCode;
    }

    public void setPaymentMethodCode(String paymentMethodCode) {
        this.paymentMethodCode = paymentMethodCode;
    }

    @Override
    public void prepareForSave() {
        super.prepareForSave();
        
        DocumentDictionaryService documentDictionaryService = SpringContext.getBean(DocumentDictionaryService.class);
        DocumentAuthorizer docAuth = documentDictionaryService.getDocumentAuthorizer(this);
        
        // First, only do this if the document is in initiated status - after that, we don't want to 
        // accidentally reset the bank code
        if ( KewApiConstants.ROUTE_HEADER_INITIATED_CD.equals( getDocumentHeader().getWorkflowDocument().getStatus().getCode())
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
                // ensure that the name is updated properly
                refreshReferenceObject( BANK );
            }
        }        
    }

    public void synchronizeBankCodeWithPaymentMethod() {
        Bank bank = getPaymentMethodGeneralLedgerPendingEntryService().getBankForPaymentMethod( getPaymentMethodCode() );
        if ( bank != null ) {
            setBankCode(bank.getBankCode());
            setBank(bank);
        } else {
            // no bank code, no bank needed
            setBankCode(null);
            setBank(null);
        }
    }

    @Override
    public void customizeExplicitGeneralLedgerPendingEntry(GeneralLedgerPendingEntrySourceDetail postable, GeneralLedgerPendingEntry explicitEntry) {
        super.customizeExplicitGeneralLedgerPendingEntry(postable, explicitEntry);
        // if the document is not processed using PDP, then the cash entries need to be created instead of liability
        // so, switch the document type so the offset generation uses a cash offset object code
        if ( !getPaymentMethodGeneralLedgerPendingEntryService().isPaymentMethodProcessedUsingPdp(getPaymentMethodCode())) {
            explicitEntry.setFinancialDocumentTypeCode(DOCUMENT_TYPE_NON_CHECK);
        }
    }

    public boolean generateDocumentGeneralLedgerPendingEntries(GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        if (getGeneralLedgerPendingEntries() == null || getGeneralLedgerPendingEntries().size() < 2) {
            LOG.warn("No gl entries for accounting lines.");
            return true;
        }

        getPaymentMethodGeneralLedgerPendingEntryService().generatePaymentMethodSpecificDocumentGeneralLedgerPendingEntries(
                this,getPaymentMethodCode(),getBankCode(), KRADConstants.DOCUMENT_PROPERTY_NAME + AccountingDocumentRuleBaseConstants.ERROR_PATH.DOCUMENT_ERROR_PREFIX + PurapPropertyConstants.BANK_CODE, getGeneralLedgerPendingEntry(0), true, true, sequenceHelper);
        
        return true;
    }
    
    
    protected PaymentMethodGeneralLedgerPendingEntryService getPaymentMethodGeneralLedgerPendingEntryService() {
        if ( paymentMethodGeneralLedgerPendingEntryService == null ) {
            paymentMethodGeneralLedgerPendingEntryService = SpringContext.getBean(PaymentMethodGeneralLedgerPendingEntryService.class);
        }
        return paymentMethodGeneralLedgerPendingEntryService;
    }
    
    public KualiDecimal getGrandPreTaxTotalExcludingDiscount() {
        String[] discountCode = new String[] { PurapConstants.ItemTypeCodes.ITEM_TYPE_PMT_TERMS_DISCOUNT_CODE };
        return this.getTotalPreTaxDollarAmountWithExclusions(discountCode, true);
    }
}
