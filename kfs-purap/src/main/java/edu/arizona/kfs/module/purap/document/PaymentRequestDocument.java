package edu.arizona.kfs.module.purap.document;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.module.purap.util.ExpiredOrClosedAccountEntry;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.vnd.businessobject.VendorHeader;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.krad.document.DocumentAuthorizer;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

import edu.arizona.kfs.fp.businessobject.PaymentMethod;
import edu.arizona.kfs.fp.service.PaymentMethodGeneralLedgerPendingEntryService;
import edu.arizona.kfs.module.purap.businessobject.PaymentRequestIncomeType;
import edu.arizona.kfs.module.purap.businessobject.PaymentRequestItem;
import edu.arizona.kfs.module.purap.document.service.PurapIncomeTypeHandler;
import edu.arizona.kfs.module.purap.service.PurapUseTaxEntryArchiveService;
import edu.arizona.kfs.sys.KFSConstants;
import edu.arizona.kfs.sys.document.IncomeTypeContainer;
import edu.arizona.kfs.vnd.businessobject.VendorDetailExtension;

@SuppressWarnings({ "unchecked", "deprecation" })
public class PaymentRequestDocument extends org.kuali.kfs.module.purap.document.PaymentRequestDocument implements IncomeTypeContainer<PaymentRequestIncomeType, Integer> {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PaymentRequestDocument.class);
    private static final long serialVersionUID = -4229712908255426814L;

    public static final String DOCUMENT_TYPE_NON_CHECK = "PRNC";
    public static final String BANK = "bank";

    private transient PurapIncomeTypeHandler<PaymentRequestIncomeType, Integer> incomeTypeHandler;
    private List<PaymentRequestIncomeType> incomeTypes;
    private String paymentPaidYear;
    private boolean payment1099Indicator;

    // default this value to "A" to preserve baseline behavior
    protected String paymentMethodCode = "A"; // check
    protected transient PaymentMethod paymentMethod;
    protected static PaymentMethodGeneralLedgerPendingEntryService paymentMethodGeneralLedgerPendingEntryService;

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

    public String getPaymentMethodCode() {
        return paymentMethodCode;
    }

    public void setPaymentMethodCode(String paymentMethodCode) {
        this.paymentMethodCode = paymentMethodCode;
        paymentMethod = null;
    }

    public PaymentMethod getPaymentMethod() {
        if (paymentMethod == null) {
            paymentMethod = SpringContext.getBean(BusinessObjectService.class).findBySinglePrimaryKey(PaymentMethod.class, paymentMethodCode);
        }
        return paymentMethod;
    }

    @Override
    public void processAfterRetrieve() {
        synchronizeBankCodeWithPaymentMethod();
        super.processAfterRetrieve();
    }

    @Override
    public void doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent) {
        LOG.debug("doRouteStatusChange() in PaymentRequestDocument.java started");
        if (getDocumentHeader().getWorkflowDocument().isProcessed() && !paymentMethodCode.equalsIgnoreCase("A")) {
            Date processDate = dateTimeService.getCurrentSqlDate();
            setExtractedTimestamp(new Timestamp(processDate.getTime()));
            setPaymentPaidTimestamp(new Timestamp(processDate.getTime()));
            SpringContext.getBean(PurapService.class).saveDocumentNoValidation(this);
        }

        // Once a doc is final, there's no need to retain archived use tax entries, because they can't be
        // reversed anymore
        WorkflowDocument workflowDocument = getDocumentHeader().getWorkflowDocument();
        if (workflowDocument.isProcessed() || workflowDocument.isCanceled() || workflowDocument.isDisapproved()) {
            SpringContext.getBean(PurapUseTaxEntryArchiveService.class).deletePaymentRequestUseTaxArchivedEntries(this);
        }

        super.doRouteStatusChange(statusChangeEvent);
    }

    @Override
    public void prepareForSave() {
        super.prepareForSave();

        DocumentAuthorizer docAuth = SpringContext.getBean(DocumentHelperService.class).getDocumentAuthorizer(this);

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
            if (paymentPaidTimestamp != null) {
                setPaymentPaidYear(getPaymentPaidTimestamp().toString().substring(0, 4));
            } else {
                setPaymentPaidYear(null);
            }
        }

        setPayment1099Indicator(false);
        for (PaymentRequestIncomeType incomeType : getIncomeTypes()) {
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

    protected PaymentMethodGeneralLedgerPendingEntryService getPaymentMethodGeneralLedgerPendingEntryService() {
        if (paymentMethodGeneralLedgerPendingEntryService == null) {
            paymentMethodGeneralLedgerPendingEntryService = SpringContext.getBean(PaymentMethodGeneralLedgerPendingEntryService.class);
        }
        return paymentMethodGeneralLedgerPendingEntryService;
    }

    @Override
    public void customizeExplicitGeneralLedgerPendingEntry(GeneralLedgerPendingEntrySourceDetail postable, GeneralLedgerPendingEntry explicitEntry) {
        super.customizeExplicitGeneralLedgerPendingEntry(postable, explicitEntry);
        // if the document is not processed using PDP, then the cash entries
        // need to be created instead of liability
        // so, switch the document type so the offset generation uses a cash
        // offset object code
        if (!getPaymentMethodGeneralLedgerPendingEntryService().isPaymentMethodProcessedUsingPdp(getPaymentMethodCode())) {
            explicitEntry.setFinancialDocumentTypeCode(DOCUMENT_TYPE_NON_CHECK);
        }
    }

    /**
     * Update to baseline method to additionally set the payment method when a vendor is selected.
     */
    @Override
    public void populatePaymentRequestFromPurchaseOrder(PurchaseOrderDocument po, HashMap<String, ExpiredOrClosedAccountEntry> expiredOrClosedAccountList) {
        super.populatePaymentRequestFromPurchaseOrder(po, expiredOrClosedAccountList);
        if (ObjectUtils.isNotNull(po.getVendorDetail()) && ObjectUtils.isNotNull(po.getVendorDetail().getExtension())) {
            if (po.getVendorDetail().getExtension() instanceof VendorDetailExtension && StringUtils.isNotBlank(((VendorDetailExtension) po.getVendorDetail().getExtension()).getDefaultB2BPaymentMethodCode())) {
                setPaymentMethodCode(((VendorDetailExtension) po.getVendorDetail().getExtension()).getDefaultB2BPaymentMethodCode());
                synchronizeBankCodeWithPaymentMethod();
            }
        }
    }

    @Override
    protected String getCustomDocumentTitle() {

        // set the workflow document title
        String poNumber = getPurchaseOrderIdentifier().toString();
        String vendorName = StringUtils.trimToEmpty(getVendorName());
        // Changing to Total Dollar Amount as this will reflect pre-tax amount for use tax transactions
        String preqAmount = getTotalDollarAmount().toString();

        String documentTitle = "";
        Set<String> nodeNames = this.getFinancialSystemDocumentHeader().getWorkflowDocument().getCurrentNodeNames();

        // if this doc is final or will be final
        if (CollectionUtils.isEmpty(nodeNames) || this.getFinancialSystemDocumentHeader().getWorkflowDocument().isFinal()) {
            documentTitle = (new StringBuilder("PO: ")).append(poNumber).append(" Vendor: ").append(vendorName).append(" Amount: ").append(preqAmount).toString();
        } else {
            PurApAccountingLine theAccount = getFirstAccount();
            String accountNumber = (theAccount != null ? StringUtils.trimToEmpty(theAccount.getAccountNumber()) : "n/a");
            String subAccountNumber = (theAccount != null ? StringUtils.trimToEmpty(theAccount.getSubAccountNumber()) : "");
            String accountChart = (theAccount != null ? theAccount.getChartOfAccountsCode() : "");
            String payDate = getDateTimeService().toDateString(getPaymentRequestPayDate());
            String indicator = getTitleIndicator();
            // set title to: PO# - VendorName - Chart/Account - total amt - Pay Date - Indicator (ie Hold, Request Cancel)
            documentTitle = (new StringBuilder("PO: ")).append(poNumber).append(" Vendor: ").append(vendorName).append(" Account: ").append(accountChart).append(" ").append(accountNumber).append(" ").append(subAccountNumber).append(" Amount: ").append(preqAmount).append(" Pay Date: ").append(payDate).append(" ").append(indicator).toString();
        }
        return documentTitle;
    }

    @Override
    public KualiDecimal getTotalDollarAmount() {
        if (this.isUseTaxIndicator()) {
            KualiDecimal totalPreTaxDollarAmount = this.getTotalPreTaxDollarAmount();
            getFinancialSystemDocumentHeader().setFinancialDocumentTotalAmount(totalPreTaxDollarAmount);
            return totalPreTaxDollarAmount;
        }
        return super.getTotalDollarAmount();
    }

    @Override
    public KualiDecimal getGrandTotal() {
        return getTotalDollarAmountAllItems(null);
    }

    @Override
    public List<PaymentRequestIncomeType> getIncomeTypes() {
        if (incomeTypes == null) {
            incomeTypes = new ArrayList<PaymentRequestIncomeType>();
        }
        return incomeTypes;
    }

    public void setIncomeTypes(List<PaymentRequestIncomeType> incomeTypes) {
        this.incomeTypes = incomeTypes;
    }

    @Override
    public Integer getDocumentIdentifier() {
        return getPurapDocumentIdentifier();
    }

    @Override
    public String getPaidYear() {
        return getIncomeTypeHandler().getYearFromTimestamp(getPaymentPaidTimestamp());
    }

    @Override
    public PurapIncomeTypeHandler<PaymentRequestIncomeType, Integer> getIncomeTypeHandler() {
        if (incomeTypeHandler == null) {
            incomeTypeHandler = new PurapIncomeTypeHandler<PaymentRequestIncomeType, Integer>(this, PaymentRequestIncomeType.class) {
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
                            LOG.debug("financial object code: " + acctline.getFinancialObjectCode() + ", is reportable" + getIncomeTypeHandler().is1099Reportable(acctline.getFinancialObjectCode()));
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
        String retval = KFSConstants.EMPTY_STRING;
        try {
            retval = getDocumentHeader().getWorkflowDocument().getStatus().getCode();
        } catch (Exception ex) {
            LOG.warn(ex);
        }
        return retval;
    }

    @Override
    public void populateDocumentForRouting() {
        super.populateDocumentForRouting();
        getIncomeTypeHandler().removeZeroValuedIncomeTypes();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public List buildListOfDeletionAwareLists() {
        List managedLists = super.buildListOfDeletionAwareLists();
        if (incomeTypes != null) {
            managedLists.add(incomeTypes);
        }
        return managedLists;
    }

    public boolean getPayment1099IndicatorForSearching() {
        return payment1099Indicator;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Class getItemClass() {
        return PaymentRequestItem.class;
    }

}
