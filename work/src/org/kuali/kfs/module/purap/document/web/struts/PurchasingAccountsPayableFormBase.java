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

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.purap.service.PurapAccountingService;
import org.kuali.kfs.module.purap.util.SummaryAccount;
import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.kfs.pdp.businessobject.PaymentDetail;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSParameterKeyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.kfs.sys.web.struts.KualiAccountingDocumentFormBase;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.util.TypedArrayList;
import org.kuali.rice.kns.util.UrlFactory;
import org.kuali.rice.kns.web.ui.ExtraButton;

/**
 * Struts Action Form for Purchasing and Accounts Payable documents.
 */
public class PurchasingAccountsPayableFormBase extends KualiAccountingDocumentFormBase {

    private transient List<SummaryAccount> summaryAccounts;

    /**
     * Constructs a PurchasingAccountsPayableFormBase instance and initializes summary accounts.
     */
    public PurchasingAccountsPayableFormBase() {
        super();
        clearSummaryAccounts();
    }

    /**
     * Updates the summaryAccounts that are contained in the form. Currently we are only calling this on load and when
     * refreshAccountSummary is called.
     */
    public void refreshAccountSummmary() {
        clearSummaryAccounts();
        PurchasingAccountsPayableDocument purapDocument = (PurchasingAccountsPayableDocument) getDocument();
        summaryAccounts.addAll(SpringContext.getBean(PurapAccountingService.class).generateSummaryAccounts(purapDocument));
    }

    /**
     * Initializes summary accounts.
     */
    public void clearSummaryAccounts() {
        summaryAccounts = new TypedArrayList(SummaryAccount.class);
    }

    /**
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentFormBase#getBaselineSourceAccountingLines()
     */
    public List getBaselineSourceAccountingLines() {
        List<AccountingLine> accounts = new ArrayList<AccountingLine>();
        if (ObjectUtils.isNull(accounts) || accounts.isEmpty()) {
            accounts = new ArrayList<AccountingLine>();
            for (PurApItem item : ((PurchasingAccountsPayableDocument) getDocument()).getItems()) {
                List<PurApAccountingLine> lines = item.getBaselineSourceAccountingLines();
                for (PurApAccountingLine line : lines) {
                    accounts.add(line);
                }

            }
        }
        return accounts;
    }

    @Override
    public void populate(HttpServletRequest request) {
        super.populate(request);
        //fix document item/account references if necessary
        PurchasingAccountsPayableDocument purapDoc = (PurchasingAccountsPayableDocument)this.getDocument();
        //FIXME this is breaking stuff (like copy req and updateview on cams)
//        SpringContext.getBean(BusinessObjectDictionaryService.class).performForceUppercase(purapDoc);
        purapDoc.fixItemReferences();
    }

    public List<SummaryAccount> getSummaryAccounts() {
        if (summaryAccounts == null) {
            refreshAccountSummmary();
        }
        return summaryAccounts;
    }

    public void setSummaryAccounts(List<SummaryAccount> summaryAccounts) {
        this.summaryAccounts = summaryAccounts;
    }

    protected void addExtraButton(String property, String source, String altText) {
    
        ExtraButton newButton = new ExtraButton();
    
        newButton.setExtraButtonProperty(property);
        newButton.setExtraButtonSource(source);
        newButton.setExtraButtonAltText(altText);
    
        extraButtons.add(newButton);
    }
    
    /**
     * This method builds the url for the disbursement info on the purap documents.
     * @return the disbursement info url
     */
    public String getDisbursementInfoUrl() {
        String basePath = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KFSConstants.APPLICATION_URL_KEY);
        ParameterService parameterService = SpringContext.getBean(ParameterService.class);

        String orgCode = parameterService.getParameterValue(KfsParameterConstants.PURCHASING_BATCH.class, KFSParameterKeyConstants.PurapPdpParameterConstants.PURAP_PDP_EPIC_ORG_CODE);
        String subUnitCode = parameterService.getParameterValue(KfsParameterConstants.PURCHASING_BATCH.class, KFSParameterKeyConstants.PurapPdpParameterConstants.PURAP_PDP_EPIC_SBUNT_CODE);

        Properties parameters = new Properties();
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.SEARCH_METHOD);
        parameters.put(KFSConstants.BACK_LOCATION, basePath + "/" + KFSConstants.MAPPING_PORTAL + ".do");
        parameters.put(KNSConstants.DOC_FORM_KEY, "88888888");
        parameters.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, PaymentDetail.class.getName());
        parameters.put(KFSConstants.HIDE_LOOKUP_RETURN_LINK, "true");
        parameters.put(KFSConstants.SUPPRESS_ACTIONS, "false");
        parameters.put(PdpPropertyConstants.PaymentDetail.PAYMENT_CUSTOMER_DOC_NUMBER, getDocument().getDocumentNumber());
        parameters.put(PdpPropertyConstants.PaymentDetail.PAYMENT_DISBURSEMENT_FINANCIAL_DOCUMENT_TYPE_CODE, SpringContext.getBean(DataDictionaryService.class).getDocumentTypeNameByClass(getDocument().getClass()));
        parameters.put(PdpPropertyConstants.PaymentDetail.PAYMENT_ORG_CODE, orgCode);
        parameters.put(PdpPropertyConstants.PaymentDetail.PAYMENT_SUBUNIT_CODE, subUnitCode);

        String lookupUrl = UrlFactory.parameterizeUrl(basePath + "/" + KFSConstants.LOOKUP_ACTION, parameters);

        return lookupUrl;
    }
    
}
