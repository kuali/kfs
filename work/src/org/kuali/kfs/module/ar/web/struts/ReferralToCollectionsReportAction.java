/*
 * Copyright 2006-2008 The Kuali Foundation
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
package org.kuali.kfs.module.ar.web.struts;

import java.util.List;

import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.CollectionActivityReport;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This class handles Actions for lookup flow for Referral To Collections Report
 */
public class ReferralToCollectionsReportAction extends ContractsGrantsReportLookupAction {
    private static final String TOTALS_TABLE_KEY = "totalsTable";

    /**
     * Returns the String "Referral to Collections Report" as a title
     * @see org.kuali.kfs.module.ar.web.struts.ContractsGrantsReportLookupAction#generateReportTitle(org.kuali.rice.kns.web.struts.form.LookupForm)
     */
    @Override
    public String generateReportTitle(LookupForm lookupForm) {
        return "Referral to Collections Report";
    }

    /**
     * The sortPropertyName here is hard coded to so we need to do some special logic
     * @see org.kuali.kfs.module.ar.web.struts.ContractsGrantsReportLookupAction#sortReportValues(java.util.List, java.lang.String)
     */
    @Override
    protected <B extends BusinessObject> String sortReportValues(List<B> displayList) {
        Object sortIndexObject = GlobalVariables.getUserSession().retrieveObject(ArConstants.SORT_INDEX_SESSION_KEY);
        // set default sort index as 0 (Proposal Number)
        if (ObjectUtils.isNull(sortIndexObject)) {
            sortIndexObject = "0";
        }

        // get sort property
        String sortPropertyName = ArPropertyConstants.ReferralToCollectionsReportFields.PDF_SORT_PROPERTY;
        // sort list
        sortReport(displayList, ArPropertyConstants.ReferralToCollectionsReportFields.LIST_SORT_PROPERTY);
        return sortPropertyName;
    }

    /**
     * Returns "referralToCollectionsReportBuilderService"
     * @see org.kuali.kfs.module.ar.web.struts.ContractsGrantsReportLookupAction#getReportBuilderServiceBeanName()
     */
    @Override
    public String getReportBuilderServiceBeanName() {
        return ArConstants.ReportBuilderDataServiceBeanNames.REFERRAL_TO_COLLECTION;
    }

    /**
     * This isn't used on this report - see sortReportValues override on this class
     * @see org.kuali.kfs.module.ar.web.struts.ContractsGrantsReportLookupAction#getSortFieldName()
     */
    @Override
    protected String getSortFieldName() {
        return null;
    }

    /**
     * Returns the class for CollectionActivityReport
     * @see org.kuali.kfs.module.ar.web.struts.ContractsGrantsReportLookupAction#getPrintSearchCriteriaClass()
     */
    @Override
    public Class<? extends BusinessObject> getPrintSearchCriteriaClass() {
        return CollectionActivityReport.class;
    }
}