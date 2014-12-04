/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
