package edu.arizona.kfs.module.purap.document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBaseConstants;
import org.kuali.kfs.vnd.businessobject.VendorHeader;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.krad.document.DocumentAuthorizer;
import org.kuali.rice.krad.service.DocumentDictionaryService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;

import edu.arizona.kfs.fp.service.PaymentMethodGeneralLedgerPendingEntryService;
import edu.arizona.kfs.module.purap.PurapConstants;
import edu.arizona.kfs.module.purap.businessobject.CreditMemoIncomeType;
import edu.arizona.kfs.module.purap.document.service.PurapIncomeTypeHandler;
import edu.arizona.kfs.sys.KFSConstants;
import edu.arizona.kfs.sys.document.IncomeTypeContainer;

/**
 * Added payment method code and supporting logic
 */
@SuppressWarnings("unchecked")
public class VendorCreditMemoDocument extends org.kuali.kfs.module.purap.document.VendorCreditMemoDocument implements IncomeTypeContainer<CreditMemoIncomeType, Integer> {
    private static final long serialVersionUID = -1120227847801097261L;

    private transient PurapIncomeTypeHandler<CreditMemoIncomeType, Integer> incomeTypeHandler;
    private List<CreditMemoIncomeType> incomeTypes;

    public static String DOCUMENT_TYPE_NON_CHECK = "CMNC";
    public static final String BANK = "bank";

    protected String paymentMethodCode = "A"; // check
    private static PaymentMethodGeneralLedgerPendingEntryService paymentMethodGeneralLedgerPendingEntryService;

    protected String paymentPaidYear;
    protected boolean payment1099Indicator;

    public String getPaymentMethodCode() {
        return paymentMethodCode;
    }

    public void setPaymentMethodCode(String paymentMethodCode) {
        this.paymentMethodCode = paymentMethodCode;
    }

    public String getPaymentPaidYear() {
        return paymentPaidYear;
    }

    public void setPaymentPaidYear(String paymentPaidYear) {
        this.paymentPaidYear = paymentPaidYear;
    }

    public boolean isPayment1099Indicator() {
        return payment1099Indicator;
    }

    public void setPayment1099Indicator(boolean payment1099Indicator) {
        this.payment1099Indicator = payment1099Indicator;
    }

    /*
     * This second getter is necessary as VendorCreditMemoDocument.xml uses the same Java field (payment1099Indicator) for multiple
     * UI fields (one for display and one for searching). Each UI field requires its own specific method to access the requisite data
     */
    public boolean getPayment1099IndicatorForSearching() {
        return payment1099Indicator;
    }

    @Override
    public void prepareForSave() {
        super.prepareForSave();

        DocumentDictionaryService documentDictionaryService = SpringContext.getBean(DocumentDictionaryService.class);
        DocumentAuthorizer docAuth = documentDictionaryService.getDocumentAuthorizer(this);

        // First, only do this if the document is in initiated status - after that, we don't want to
        // accidentally reset the bank code
        if (KewApiConstants.ROUTE_HEADER_INITIATED_CD.equals(getDocumentHeader().getWorkflowDocument().getStatus().getCode()) || KewApiConstants.ROUTE_HEADER_SAVED_CD.equals(getDocumentHeader().getWorkflowDocument().getStatus().getCode())) {
            // need to check whether the user has the permission to edit the bank code
            // if so, don't synchronize since we can't tell whether the value coming in
            // was entered by the user or not.

            if (!docAuth.isAuthorizedByTemplate(this, KFSConstants.CoreModuleNamespaces.KFS, KFSConstants.PermissionTemplate.EDIT_BANK_CODE.name, GlobalVariables.getUserSession().getPrincipalId())) {
                synchronizeBankCodeWithPaymentMethod();
            } else {
                // ensure that the name is updated properly
                refreshReferenceObject(BANK);
            }
        }

        getIncomeTypeHandler().removeZeroValuedIncomeTypes();

        // Only update paid year if the document is in final status
        if (KewApiConstants.ROUTE_HEADER_FINAL_CD.equals(getDocumentHeader().getWorkflowDocument().getStatus().getCode())) {
            if (creditMemoPaidTimestamp != null) {
                setPaymentPaidYear(getCreditMemoPaidTimestamp().toString().substring(0, 4));
            } else {
                setPaymentPaidYear(null);
            }
        }

        setPayment1099Indicator(false);
        for (CreditMemoIncomeType incomeType : getIncomeTypes()) {
            boolean isCodeExist = StringUtils.isNotBlank(incomeType.getIncomeTypeCode());
            boolean isReportable = !incomeType.getIncomeTypeCode().equals(KFSConstants.IncomeTypeConstants.INCOME_TYPE_NON_REPORTABLE_CODE);
            if (isCodeExist && isReportable) {
                setPayment1099Indicator(true);
                break;
            }
        }
    }

    public void synchronizeBankCodeWithPaymentMethod() {
        Bank bank = getPaymentMethodGeneralLedgerPendingEntryService().getBankForPaymentMethod(getPaymentMethodCode());
        if (bank != null) {
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
        if (!getPaymentMethodGeneralLedgerPendingEntryService().isPaymentMethodProcessedUsingPdp(getPaymentMethodCode())) {
            explicitEntry.setFinancialDocumentTypeCode(DOCUMENT_TYPE_NON_CHECK);
        }
    }

    public boolean generateDocumentGeneralLedgerPendingEntries(GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        if (getGeneralLedgerPendingEntries() == null || getGeneralLedgerPendingEntries().size() < 2) {
            LOG.warn("No gl entries for accounting lines.");
            return true;
        }

        getPaymentMethodGeneralLedgerPendingEntryService().generatePaymentMethodSpecificDocumentGeneralLedgerPendingEntries(this, getPaymentMethodCode(), getBankCode(), KRADConstants.DOCUMENT_PROPERTY_NAME + AccountingDocumentRuleBaseConstants.ERROR_PATH.DOCUMENT_ERROR_PREFIX + PurapPropertyConstants.BANK_CODE, getGeneralLedgerPendingEntry(0), true, true, sequenceHelper);

        return true;
    }

    protected PaymentMethodGeneralLedgerPendingEntryService getPaymentMethodGeneralLedgerPendingEntryService() {
        if (paymentMethodGeneralLedgerPendingEntryService == null) {
            paymentMethodGeneralLedgerPendingEntryService = SpringContext.getBean(PaymentMethodGeneralLedgerPendingEntryService.class);
        }
        return paymentMethodGeneralLedgerPendingEntryService;
    }

    public KualiDecimal getGrandPreTaxTotalExcludingDiscount() {
        String[] discountCode = new String[] { PurapConstants.ItemTypeCodes.ITEM_TYPE_PMT_TERMS_DISCOUNT_CODE };
        return this.getTotalPreTaxDollarAmountWithExclusions(discountCode, true);
    }

    // Tag and JSP for DV, PREQ and CM Documents
    @Override
    public List<CreditMemoIncomeType> getIncomeTypes() {
        if (incomeTypes == null) {
            incomeTypes = new ArrayList<CreditMemoIncomeType>();
        }
        return incomeTypes;
    }

    public void setIncomeTypes(List<CreditMemoIncomeType> incomeTypes) {
        this.incomeTypes = incomeTypes;
    }

    @Override
    public Integer getDocumentIdentifier() {
        return getPurapDocumentIdentifier();
    }

    @Override
    public String getPaidYear() {
        return getIncomeTypeHandler().getYearFromTimestamp(getCreditMemoPaidTimestamp());
    }

    @Override
    public PurapIncomeTypeHandler<CreditMemoIncomeType, Integer> getIncomeTypeHandler() {
        if (incomeTypeHandler == null) {
            incomeTypeHandler = new PurapIncomeTypeHandler<CreditMemoIncomeType, Integer>(this, CreditMemoIncomeType.class) {
            };
        }

        return incomeTypeHandler;
    }

    @Override
    public VendorHeader getVendorHeader() {
        VendorHeader retval = getVendorDetail().getVendorHeader();

        if (retval == null) {
            retval = getIncomeTypeHandler().getVendorHeaderFromVendorNumber(getVendorNumber());
        }

        return retval;
    }

    @Override
    public boolean getReportable1099TransactionsFlag() {
        boolean retval = false;

        if (getItems() != null) {
            Iterator<PurApItem> it = getItems().iterator();

            while (!retval && it.hasNext()) {
                List<PurApAccountingLine> acctlines = it.next().getBaselineSourceAccountingLines();

                if (acctlines != null) {
                    for (PurApAccountingLine acctline : acctlines) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("financial object code: " + acctline.getFinancialObjectCode() + ", is reportable: " + getIncomeTypeHandler().is1099Reportable(acctline.getFinancialObjectCode()));
                        }

                        if (getIncomeTypeHandler().is1099Reportable(acctline.getFinancialObjectCode())) {
                            retval = true;
                            break;
                        }
                    }
                }
            }
        }

        return retval;
    }

    @Override
    public String getRouteStatus() {
        String retval = null;
        try {
            retval = getDocumentHeader().getWorkflowDocument().getStatus().getCode();
        }

        // if for some reason we can't get the route status we will return null and log the error
        // this method is currently part of the IncomeTypeContainer interface and is used by
        // the IncomeTypeHandler to determine read only settings. Null will cause read only
        catch (Exception ex) {
            LOG.warn(ex);
        }

        return retval;
    }

    @Override
    public void populateDocumentForRouting() {
        super.populateDocumentForRouting();

        // make sure we remove any income types with 0 amounts
        getIncomeTypeHandler().removeZeroValuedIncomeTypes();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public List buildListOfDeletionAwareLists() {
        List<Collection<CreditMemoIncomeType>> managedLists = super.buildListOfDeletionAwareLists();

        if (incomeTypes != null) {
            managedLists.add(incomeTypes);
        }

        return managedLists;
    }
}
