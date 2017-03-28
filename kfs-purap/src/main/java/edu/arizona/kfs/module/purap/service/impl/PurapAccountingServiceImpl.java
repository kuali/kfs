package edu.arizona.kfs.module.purap.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.businessobject.PurApItemUseTax;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.purap.util.UseTaxContainer;
import org.kuali.kfs.sys.businessobject.AccountingLineBase;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.util.ObjectPopulationUtils;
import org.kuali.rice.core.api.util.type.KualiDecimal;

import edu.arizona.kfs.module.purap.service.PurapAccountingService;


public class PurapAccountingServiceImpl extends org.kuali.kfs.module.purap.service.impl.PurapAccountingServiceImpl 
   implements PurapAccountingService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurapAccountingServiceImpl.class);
    
    @Override
    public List<PurApAccountingLine> generateAccountDistributionForProration(List<SourceAccountingLine> accounts, KualiDecimal totalAmount, Integer percentScale, Class clazz, List<PurApItem> purApItems) {
        String methodName = "generateAccountDistributionForProration()";
        if ( LOG.isDebugEnabled() ) {
            LOG.debug(methodName + " started");
        }
                
        if(totalAmount.isZero() || isAllSummaryAccoountsAmountZero(accounts)) {
            return getFirstItemAccountsForDistribution(purApItems, clazz);
        } else {
            return super.generateAccountDistributionForProration(accounts, totalAmount, percentScale, clazz);
        }
    }

    /*
     * Check if all the summary accounts amount are zero. If all are zero, it will cause exception when creating
     * distribution account.
     */
    private boolean isAllSummaryAccoountsAmountZero(List<SourceAccountingLine> summaryAccounts) {
    	boolean isZero = true;
    	for (SourceAccountingLine account : summaryAccounts) {
    		isZero = account.getAmount().isZero();
    		if (!isZero) {
    			break;
    		}
    	}
    	return isZero;
    	
    }

    /*
     * get first line item's accounts as distribution accounts
     */
    private List<PurApAccountingLine> getFirstItemAccountsForDistribution(List<PurApItem> purapItems, Class clazz) {
        List<PurApAccountingLine> newAccounts = new ArrayList<PurApAccountingLine>();
    	
        for (PurApItem item : purapItems) {
            if (item.getItemType().isLineItemIndicator()) {
            	for (PurApAccountingLine account : item.getSourceAccountingLines()) {
                    PurApAccountingLine newAccountingLine = null;
                    try {
                        newAccountingLine = (PurApAccountingLine) clazz.newInstance();
                        ObjectPopulationUtils.populateFromBaseClass(AccountingLineBase.class, account, newAccountingLine, PurapConstants.KNOWN_UNCOPYABLE_FIELDS);
                        newAccountingLine.setAccountLinePercent(account.getAccountLinePercent());
                        newAccounts.add(newAccountingLine);
                    }
                    catch (Exception e) {
                        LOG.error("getFirstItemAccountsForDistribution() : Instantiate Purap Account error " + e.getMessage());
                    }
                }
            	if (CollectionUtils.isNotEmpty(newAccounts)) {
            		break;
            	}
            }
       } 
        return newAccounts;
    }

    @Override
    public List<UseTaxContainer> generateUseTaxAccount(PurchasingAccountsPayableDocument document) {
        List<UseTaxContainer> useTaxAccounts = new ArrayList<UseTaxContainer>();

        HashMap<PurApItemUseTax, UseTaxContainer> useTaxItemMap = new HashMap<PurApItemUseTax, UseTaxContainer>();
        Class accountingLineClass = null;
        if (!document.isUseTaxIndicator()) {
            // not useTax, return
            return useTaxAccounts;
        }
        for (PurApItem purApItem : document.getItems()) {
            if (!purApItem.getUseTaxItems().isEmpty()) {
                if (accountingLineClass == null) {
                    accountingLineClass = purApItem.getAccountingLineClass();
                }
                UseTaxContainer useTaxContainer = new UseTaxContainer();
                for (PurApItemUseTax itemUseTax : purApItem.getUseTaxItems()) {
                    if (useTaxItemMap.containsKey(itemUseTax)) {
                        useTaxContainer = useTaxItemMap.get(itemUseTax);
                        PurApItemUseTax exisitingItemUseTax = useTaxContainer.getUseTax();
                        // if already in set we need to add on the old amount
                        KualiDecimal tax = exisitingItemUseTax.getTaxAmount();
                        tax = tax.add(itemUseTax.getTaxAmount());
                        exisitingItemUseTax.setTaxAmount(tax);

                        List<PurApItem> items = useTaxContainer.getItems();
                        items.add(purApItem);
                        useTaxContainer.setItems(items);

                    }
                    else {
                        useTaxContainer = new UseTaxContainer(itemUseTax, purApItem);
                        useTaxItemMap.put(itemUseTax, useTaxContainer);
                        useTaxAccounts.add(useTaxContainer);
                    }
                }
            }
        }
        // iterate over useTaxAccounts and set summary accounts using proration
        for (UseTaxContainer useTaxContainer : useTaxAccounts) {

            // create summary from items
            List<SourceAccountingLine> origSourceAccounts = this.generateSummaryWithNoZeroTotals(useTaxContainer.getItems());
            KualiDecimal totalAmount = calculateSumTotal(origSourceAccounts);
            // UAF-3794 only this statement changed
            List<PurApAccountingLine> accountingLines = generateAccountDistributionForProration(origSourceAccounts, totalAmount, PurapConstants.PRORATION_SCALE, accountingLineClass, document.getItems());


            List<SourceAccountingLine> newSourceLines = new ArrayList<SourceAccountingLine>();
            // convert back to source
            convertAmtToTax(accountingLines, useTaxContainer.getUseTax().getTaxAmount(), newSourceLines);

            // do we need an update accounts here?
            useTaxContainer.setAccounts(newSourceLines);
        }

        useTaxAccounts = new ArrayList<UseTaxContainer>(useTaxItemMap.values());
        return useTaxAccounts;
    }

}
