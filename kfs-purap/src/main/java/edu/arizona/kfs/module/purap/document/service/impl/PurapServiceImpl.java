package edu.arizona.kfs.module.purap.document.service.impl;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.businessobject.PurApItemUseTax;
import org.kuali.kfs.module.purap.document.AccountsPayableDocument;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.purap.document.PurchasingDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.TaxDetail;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.rice.core.api.parameter.ParameterEvaluatorService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.ObjectUtils;

import edu.arizona.kfs.vnd.VendorParameterConstants;
import edu.arizona.kfs.vnd.businessobject.VendorDetailExtension;
import edu.arizona.kfs.vnd.VendorPropertyConstants;

/**
 * This class checks the default B2B payment method on vendors. If it matches a
 * given system parameter, then tax will not be applied, regardless of the item or funding source.
 */
public class PurapServiceImpl extends org.kuali.kfs.module.purap.document.service.impl.PurapServiceImpl implements edu.arizona.kfs.module.purap.document.service.PurapService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurapServiceImpl.class);
    
    protected ParameterEvaluatorService parameterEvaluatorService;

    
    @Override
    public void calculateTax(PurchasingAccountsPayableDocument purapDocument) {
        
        String deliveryCountryCode = null;
        
        if (purapDocument instanceof PurchasingDocument) {
            deliveryCountryCode = ((PurchasingDocument)purapDocument).getDeliveryCountryCode();
        }
        else if (purapDocument instanceof AccountsPayableDocument) {
            AccountsPayableDocument apDoc = (AccountsPayableDocument) purapDocument;
            if (ObjectUtils.isNotNull(apDoc.getPurchaseOrderDocument())) {
                deliveryCountryCode = apDoc.getPurchaseOrderDocument().getDeliveryCountryCode();
            }
        }
        else {
            throw new RuntimeException("This type of document cannot be processed here [" + purapDocument.getClass().getSimpleName() + "]");
        }
        
        if (!KFSConstants.COUNTRY_CODE_UNITED_STATES.equalsIgnoreCase(deliveryCountryCode)) {
            return;
        }
        
        boolean salesTaxInd = parameterService.getParameterValueAsBoolean(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.ENABLE_SALES_TAX_IND);
        // calculate if sales tax enabled for purap
        if (salesTaxInd) {
            // get the default payment method code from the vendor
            if (ObjectUtils.isNull(purapDocument.getVendorDetail())) {
                purapDocument.refreshReferenceObject(VendorPropertyConstants.VENDOR_DETAIL);
            }

            // If the vendor is still null, then the vendor hasn't been set. In this case the tax should be calculated as $0.00.
            if (!ObjectUtils.isNull(purapDocument.getVendorDetail())) {
                String vendorPaymentMethodCode = ((VendorDetailExtension) purapDocument.getVendorDetail().getExtension()).getDefaultB2BPaymentMethodCode();
                // Get the new vendor parameter and check if it matches
                if (!parameterEvaluatorService.getParameterEvaluator(VendorDetail.class, VendorParameterConstants.NON_TAXABLE_PAYMENT_METHODS, vendorPaymentMethodCode).evaluationSucceeds()) {
                    // if not a match, then determine taxability normally
                    boolean useTaxIndicator = purapDocument.isUseTaxIndicator();
                    String deliveryState = getDeliveryState(purapDocument);
                    String deliveryPostalCode = getDeliveryPostalCode(purapDocument);
                    Date transactionTaxDate = purapDocument.getTransactionTaxDate();

                    // iterate over items and calculate tax if taxable
                    for (PurApItem item : purapDocument.getItems()) {
                        if (isTaxable(useTaxIndicator, deliveryState, item)) {
                            calculateItemTax(useTaxIndicator, deliveryPostalCode, transactionTaxDate, item, item.getUseTaxClass(), purapDocument);
                        }
                    }
                }
            }
        }
    }
    
    
    @Override
    public boolean isItemTypeConflictWithTaxPolicy(PurchasingAccountsPayableDocument purchasingDocument, PurApItem item) {
        boolean conflict = false;

        String deliveryState = getDeliveryState(purchasingDocument);
        if (item.getItemType().isLineItemIndicator()) {
            if (item.getItemType().isTaxableIndicator()) {
                if (isTaxDisabledForVendor(purchasingDocument)) {
                    conflict = true;
                }
                // only check account tax policy if accounting line exists
                if (!item.getSourceAccountingLines().isEmpty()) {
                    if (!isAllAccountingLinesTaxable(deliveryState, item)) {
                        conflict = true;
                    }
                }
                if (!doesCommodityAllowCallToTaxService(item)) {
                    conflict = true;
                }
            }
        }
        
        return conflict;
    }
    
    
    protected boolean isTaxDisabledForVendor(PurchasingAccountsPayableDocument purapDocument) {
        boolean disable = false;
        if (!ObjectUtils.isNull(purapDocument.getVendorDetail())) {
            String vendorPaymentMethodCode = ((VendorDetailExtension) purapDocument.getVendorDetail().getExtension()).getDefaultB2BPaymentMethodCode();
            // Get the new vendor parameter and check if it matches
            if (parameterEvaluatorService.getParameterEvaluator(VendorDetail.class, VendorParameterConstants.NON_TAXABLE_PAYMENT_METHODS, vendorPaymentMethodCode).evaluationSucceeds()) {
                disable = true;
            }
        }

        return disable;
    }

    // Overriding since every item in the document has a reference to a cached purap document for optimization,
    // and we need to update the tax indicator on the cached document when the real document's
    // use tax indicator is updated.
    @Override
    public void updateUseTaxIndicator(PurchasingAccountsPayableDocument purapDocument, boolean newUseTaxIndicatorValue) {
        super.updateUseTaxIndicator(purapDocument, newUseTaxIndicatorValue);
        // update the tax indicator on each item's cached purap document.
        for (PurApItem item : purapDocument.getItems()) {
            if (ObjectUtils.isNotNull(item.getPurapDocument())) {
                item.getPurapDocument().setUseTaxIndicator(newUseTaxIndicatorValue);
            }
        }
    }
    
    
    @Override
    protected void calculateItemTax(boolean useTaxIndicator, String deliveryPostalCode, Date transactionTaxDate, PurApItem item, Class itemUseTaxClass, PurchasingAccountsPayableDocument purapDocument) {
     // sales tax items get done by the original code
        if (!useTaxIndicator) {
            super.calculateItemTax(useTaxIndicator, deliveryPostalCode, transactionTaxDate, item, itemUseTaxClass, purapDocument);

            return;
        }

        KualiDecimal extendedPrice = item.getExtendedPrice();
        String itemTypeCode = item.getItemTypeCode();

        if ((StringUtils.equals(itemTypeCode, PurapConstants.ItemTypeCodes.ITEM_TYPE_ORDER_DISCOUNT_CODE) || StringUtils.equals(itemTypeCode, PurapConstants.ItemTypeCodes.ITEM_TYPE_PMT_TERMS_DISCOUNT_CODE))) {
            KualiDecimal taxablePrice = getFullDiscountTaxablePrice(extendedPrice, purapDocument);

            extendedPrice = taxablePrice;
        }

        List<TaxDetail> taxDetails = taxService.getUseTaxDetails(transactionTaxDate, deliveryPostalCode, extendedPrice);
        if (ObjectUtils.isNotNull(taxDetails)) {
            List<PurApItemUseTax> useTaxItems = this.buildItemUseTax(item, itemUseTaxClass, taxDetails);
           
            item.setUseTaxItems(useTaxItems);
        }
    }

    /**
     * build item use tax according to the given information
     * 
     * @param item the given item
     * @param itemUseTaxClass the use tax class of the given item
     * @param taxDetails the given tax details
     * @return a list of item use tax built from the given information
     */
    protected List<PurApItemUseTax> buildItemUseTax(PurApItem item, Class<PurApItemUseTax> itemUseTaxClass, List<TaxDetail> taxDetails) {
        List<PurApItemUseTax> newUseTaxItems = new ArrayList<PurApItemUseTax>();
        
        for (TaxDetail taxDetail : taxDetails) {
            KualiDecimal taxAmount = taxDetail.getTaxAmount();
            if (ObjectUtils.isNull(taxAmount) || taxAmount.isZero()) {
                continue;
            }
            
            /* this method is called when a PaymentRequest is being approved. We do not want to
             * recreate useTaxItems that already exist. The function calling this one will
             * overwrite the useTaxItems, but because of a bug in OJB, the items are doubled in
             * DocumentDaoOjb.save().
             */
            List<PurApItemUseTax> currentUseTaxItems = item.getUseTaxItems();
            boolean thisItemAlreadyHasUseTaxForThisRateCode = false;
            if (currentUseTaxItems != null) {
                for (PurApItemUseTax currentUseTaxItem : currentUseTaxItems) {
                    if (StringUtils.equals(currentUseTaxItem.getRateCode(), taxDetail.getRateCode())) {
                        thisItemAlreadyHasUseTaxForThisRateCode = true;
                        newUseTaxItems.add(currentUseTaxItem);
                    }
                }
            }
            
            if (thisItemAlreadyHasUseTaxForThisRateCode) {
                continue;
            }
            
            try {
                PurApItemUseTax useTaxItem = (PurApItemUseTax) itemUseTaxClass.newInstance();

                useTaxItem.setChartOfAccountsCode(taxDetail.getChartOfAccountsCode());
                useTaxItem.setFinancialObjectCode(taxDetail.getFinancialObjectCode());
                useTaxItem.setAccountNumber(taxDetail.getAccountNumber());
                useTaxItem.setItemIdentifier(item.getItemIdentifier());
                useTaxItem.setRateCode(taxDetail.getRateCode());
                useTaxItem.setTaxAmount(taxDetail.getTaxAmount());
                newUseTaxItems.add(useTaxItem);
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        
        return newUseTaxItems;
    }

     /**
     * Checks if all accounting lines are taxable, based on the delivery state, fund/subfund groups, and object code
     * level/consolidations.
     * 
     * @param deliveryState the given delivery state
     * @param item the given item
     * @return true if all accounting lines are taxable; otherwise, false
     */
    protected boolean isAllAccountingLinesTaxable(String deliveryState, PurApItem item) {
        boolean deliveryStateTaxable = isDeliveryStateTaxable(deliveryState);

        for (PurApAccountingLine acctLine : item.getSourceAccountingLines()) {
            if (!isAccountingLineTaxable(acctLine, deliveryStateTaxable)) {
                return false;
            }
        }

        return true;
    }
    
    public void setParameterEvaluatorService(ParameterEvaluatorService parameterEvaluatorService) {
        this.parameterEvaluatorService = parameterEvaluatorService;
    }
}
