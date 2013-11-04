/*
 * Copyright 2006 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.gl.businessobject.lookup;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.gl.businessobject.TrialBalanceReport;
import org.kuali.kfs.gl.service.TrialBalanceService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;

/**
 * An extension of KualiLookupableImpl to support balance lookups
 */
public class TrialBalanceLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {
    private Map fieldValues;
    private TrialBalanceService trialBalanceService;
    private static final String TOTAL = "Total";

    /**
     * ASR-1212: append the rice prefix for inquiry url
     *
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getInquiryUrl(org.kuali.rice.kns.bo.BusinessObject,
     *      java.lang.String)
     */
    @Override
    public HtmlData getInquiryUrl(BusinessObject bo, String propertyName) {
        HtmlData inquiryUrl = super.getInquiryUrl(bo, propertyName);

        if (StringUtils.isNotBlank(((AnchorHtmlData) inquiryUrl).getHref())) {
            ((AnchorHtmlData) inquiryUrl).setHref(KFSConstants.RICE_PATH_PREFIX + ((AnchorHtmlData) inquiryUrl).getHref());
        }

        // setting up url should exclude total debit/credit entry
        if ((KFSConstants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME.equals(propertyName)) && (TOTAL.equals(((TrialBalanceReport) bo).getChartOfAccountsCode()))) {
            ((AnchorHtmlData) inquiryUrl).setHref("");
        }
        return inquiryUrl;
    }

    /**
     * ASR-1212: trial balance report lookup result
     *
     * @see org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl#getSearchResults(java.util.Map)
     */
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        setBackLocation(fieldValues.get(KRADConstants.BACK_LOCATION));
        setDocFormKey(fieldValues.get(KRADConstants.DOC_FORM_KEY));

        String selectedFiscalYear = fieldValues.get(KFSConstants.UNIVERSITY_FISCAL_YEAR_PROPERTY_NAME);
        String chartCode = fieldValues.get(KFSConstants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME);

        return trialBalanceService.findTrialBalance(selectedFiscalYear, chartCode);
    }


    /**
     * validate university fiscal year
     *
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#validateSearchParameters(java.util.Map)
     */
    @Override
    public void validateSearchParameters(Map fieldValues) {
        super.validateSearchParameters(fieldValues);

        String selectedFiscalYear = (String) fieldValues.get(KFSConstants.UNIVERSITY_FISCAL_YEAR_PROPERTY_NAME);
        if (StringUtils.isNotBlank(selectedFiscalYear)) {
            try {
                int year = Integer.parseInt(selectedFiscalYear);
            }
            catch (NumberFormatException e) {
                GlobalVariables.getMessageMap().putError("universityFiscalYear", KFSKeyConstants.ERROR_CUSTOM, new String[] { "Fiscal Year must be a four-digit number" });
                throw new ValidationException("errors in search criteria");
            }
        }
    }


    /**
     * Gets the trialBalanceService attribute.
     *
     * @return Returns the trialBalanceService.
     */
    public TrialBalanceService getTrialBalanceService() {
        return trialBalanceService;
    }

    /**
     * Sets the trialBalanceService attribute value.
     *
     * @param trialBalanceService The trialBalanceService to set.
     */
    public void setTrialBalanceService(TrialBalanceService trialBalanceService) {
        this.trialBalanceService = trialBalanceService;
    }


}
