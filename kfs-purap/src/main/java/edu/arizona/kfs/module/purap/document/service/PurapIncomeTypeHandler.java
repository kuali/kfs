package edu.arizona.kfs.module.purap.document.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurApItemUseTax;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.vnd.businessobject.VendorHeader;
import org.kuali.rice.core.api.util.type.KualiDecimal;

import edu.arizona.kfs.sys.businessobject.DocumentIncomeType;
import edu.arizona.kfs.sys.document.IncomeTypeContainer;
import edu.arizona.kfs.sys.document.IncomeTypeHandler;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class PurapIncomeTypeHandler<T1 extends DocumentIncomeType<T2>, T2> extends IncomeTypeHandler<T1, T2> {

    public PurapIncomeTypeHandler(IncomeTypeContainer incomeTypeDocumentContainer, Class documentIncomeTypeClass) {
        super(incomeTypeDocumentContainer, documentIncomeTypeClass);
    }

    /**
     * This method populates the 1099 IncomeTypes from the document accounting lines and handles use tax items if required
     * 
     * @param accountingLines
     * @param useTaxItems
     */
    public void populateIncomeTypes(List<? extends AccountingLine> accountingLines, List<PurApItemUseTax> useTaxItems) {
        if (isEditableRouteStatus()) {
            VendorHeader vendorHeader = incomeTypeDocumentContainer.getVendorHeader();
            zeroOutDocumentIncomeTypes();

            // need to handle the use tax calculations differently
            Map<Integer, List<PurApItemUseTax>> useTaxItemMap = null;
            if ((useTaxItems != null) && !useTaxItems.isEmpty()) {
                useTaxItemMap = new HashMap<Integer, List<PurApItemUseTax>>();

                for (PurApItemUseTax useTax : useTaxItems) {
                    List<PurApItemUseTax> useTaxList = useTaxItemMap.get(useTax.getItemIdentifier());

                    if (useTaxList == null) {
                        useTaxItemMap.put(useTax.getItemIdentifier(), useTaxList = new ArrayList<PurApItemUseTax>());
                    }
                    useTaxList.add(useTax);
                }
            }

            // loop over the accounting lines associated with the documents and summarize
            // valid 1099 accounting lines by income type (box on the 1099 form)
            for (AccountingLine acctline : accountingLines) {
                // See if a DocumentIncomeType associated with this 1099 box exists in map
                String box = get1099Box(vendorHeader, acctline);
                if (StringUtils.isNotBlank(box)) {
                    T1 dit = (T1) getDocumentIncomeType(acctline, box);

                    if (dit != null) {
                        // need to handle the use tax calculations differently
                        KualiDecimal amount = acctline.getAmount();

                        // if we have useTaxItems then we need to subtract use tax from the total amount
                        if (useTaxItemMap != null) {
                            amount = amount.subtract(getAccountUseTaxAmount(acctline, useTaxItemMap));
                        }

                        // add the amount from the current accounting line to the DocumentIncomeType
                        dit.setAmount(dit.getAmount().add(amount));
                    }
                }
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("populated " + incomeTypeDocumentContainer.getIncomeTypes().size() + " document income types");
            }
        }
    }

    protected KualiDecimal getAccountUseTaxAmount(AccountingLine acctline, Map<Integer, List<PurApItemUseTax>> useTaxItemMap) {
        KualiDecimal retval = KualiDecimal.ZERO;

        if (PurApAccountingLine.class.isAssignableFrom(acctline.getClass())) {
            PurApAccountingLine pal = (PurApAccountingLine) acctline;
            List<PurApItemUseTax> useTaxList = useTaxItemMap.get(pal.getItemIdentifier());
            if (useTaxList != null) {
                KualiDecimal itemUseTaxTotal = getItemUseTaxTotal(useTaxList);

                // if we have use tax for this item calculate the amount applied based on the account percentage
                BigDecimal pct = pal.getAccountLinePercent();
                pct = pct.divide(new BigDecimal(100));
                retval = itemUseTaxTotal.multiply(new KualiDecimal(pct.setScale(KualiDecimal.SCALE, KualiDecimal.ROUND_BEHAVIOR)));
            }
        }

        return retval;
    }

    protected KualiDecimal getItemUseTaxTotal(List<PurApItemUseTax> useTaxList) {
        KualiDecimal retval = KualiDecimal.ZERO;

        for (PurApItemUseTax useTax : useTaxList) {
            retval = retval.add(useTax.getTaxAmount());
        }

        return retval;
    }

}
