/*
 * Copyright 2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.purap.document.web.struts;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.document.AccountsPayableDocument;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.module.purap.util.PurApItemUtils;
import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.kfs.pdp.businessobject.PaymentDetail;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSParameterKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.UrlFactory;

/**
 * Struts Action Form for Accounts Payable documents.
 */
public class AccountsPayableFormBase extends PurchasingAccountsPayableFormBase {

    private PurApItem newPurchasingItemLine;
    private boolean calculated;
    private int countOfAboveTheLine = 0;
    private int countOfBelowTheLine = 0;

    /**
     * Constructs an AccountsPayableForm instance and sets up the appropriately casted document.
     */
    public AccountsPayableFormBase() {
        super();
        calculated = false;
    }

    public PurApItem getNewPurchasingItemLine() {
        return newPurchasingItemLine;
    }

    public void setNewPurchasingItemLine(PurApItem newPurchasingItemLine) {
        this.newPurchasingItemLine = newPurchasingItemLine;
    }

    public PurApItem getAndResetNewPurchasingItemLine() {
        PurApItem aPurchasingItemLine = getNewPurchasingItemLine();
        setNewPurchasingItemLine(setupNewPurchasingItemLine());
        return aPurchasingItemLine;
    }

    /**
     * This method should be overriden (or see accountingLines for an alternate way of doing this with newInstance)
     * 
     * @return - null, enforces overriding
     */
    public PurApItem setupNewPurchasingItemLine() {
        return null;
    }

    public boolean isCalculated() {
        return calculated;
    }

    public void setCalculated(boolean calculated) {
        this.calculated = calculated;
    }

    public int getCountOfAboveTheLine() {
        return countOfAboveTheLine;
    }

    public void setCountOfAboveTheLine(int countOfAboveTheLine) {
        this.countOfAboveTheLine = countOfAboveTheLine;
    }

    public int getCountOfBelowTheLine() {
        return countOfBelowTheLine;
    }

    public void setCountOfBelowTheLine(int countOfBelowTheLine) {
        this.countOfBelowTheLine = countOfBelowTheLine;
    }

    /**
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentFormBase#populate(javax.servlet.http.HttpServletRequest)
     */
    @Override
    public void populate(HttpServletRequest request) {
        super.populate(request);
        AccountsPayableDocument apDoc = (AccountsPayableDocument) this.getDocument();

        if (apDoc != null) {
            // update po doc
            apDoc.setPurchaseOrderDocument(SpringContext.getBean(PurchaseOrderService.class).getCurrentPurchaseOrder(apDoc.getPurchaseOrderIdentifier()));
    
            // update counts after populate
            updateItemCounts();
        }
    }

    /**
     * overridden to make sure accounting lines on items are repopulated
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentFormBase#populateAccountingLinesForResponse(java.lang.String, java.util.Map)
     */
    @Override
    protected void populateAccountingLinesForResponse(String methodToCall, Map parameterMap) {
        super.populateAccountingLinesForResponse(methodToCall, parameterMap);
        
        populateItemAccountingLines(parameterMap);
    }
    
    /**
     * Populates accounting lines for each item on the AP document
     * @param parameterMap the map of parameters
     */
    protected void populateItemAccountingLines(Map parameterMap) {
       int itemCount = 0;
       for (PurApItem item : ((AccountsPayableDocument)getDocument()).getItems()) {
           populateAccountingLine(item.getNewSourceLine(), KFSPropertyConstants.DOCUMENT+"."+KFSPropertyConstants.ITEM+"["+itemCount+"]."+KFSPropertyConstants.NEW_SOURCE_LINE, parameterMap);
           
           int sourceLineCount = 0;
           for (PurApAccountingLine purApLine : item.getSourceAccountingLines()) {
               populateAccountingLine(purApLine, KFSPropertyConstants.DOCUMENT+"."+KFSPropertyConstants.ITEM+"["+itemCount+"]."+KFSPropertyConstants.SOURCE_ACCOUNTING_LINE+"["+sourceLineCount+"]", parameterMap);
               sourceLineCount += 1;
           }
       }
    }

    /**
     * Updates item counts for display
     */
    public void updateItemCounts() {
        List<PurApItem> items = ((AccountsPayableDocument) this.getDocument()).getItems();
        countOfBelowTheLine = PurApItemUtils.countBelowTheLineItems(items);
        countOfAboveTheLine = items.size() - countOfBelowTheLine;
    }

}
