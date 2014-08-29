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

import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.CollectionActivityReport;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.krad.bo.BusinessObject;


/**
 * This class handles Actions for Collection activity report.
 */

public class CollectionActivityReportAction extends ContractsGrantsReportLookupAction {
    private static final String TOTALS_TABLE_KEY = "totalsTable";

    /**
     * Returns "Collection Activity Report" as the report title
     * @see org.kuali.kfs.module.ar.web.struts.ContractsGrantsReportLookupAction#setReportTitle(org.kuali.rice.kns.web.struts.form.LookupForm)
     */
    @Override
    public String generateReportTitle(LookupForm lookupForm) {
        return ArConstants.COLLECTION_ACTIVITY_REPORT_TITLE;
    }

    /**
     * Returns "collectionActivityReportBuilderService"
     * @see org.kuali.kfs.module.ar.web.struts.ContractsGrantsReportLookupAction#getReportBuilderServiceBeanName()
     */
    @Override
    public String getReportBuilderServiceBeanName() {
        return ArConstants.ReportBuilderDataServiceBeanNames.COLLECTION_ACTIVITY;
    }

    /**
     * Returns the sort field for this report's pdf generation, "CollectionActivityReport"
     * @see org.kuali.kfs.module.ar.web.struts.ContractsGrantsReportLookupAction#getSortFieldName()
     */
    @Override
    protected String getSortFieldName() {
        return ArConstants.COLLECTION_ACTIVITY_REPORT_SORT_FIELD;
    }

    /**
     * Returns the class of CollectionActivityReport
     * @see org.kuali.kfs.module.ar.web.struts.ContractsGrantsReportLookupAction#getPrintSearchCriteriaClass()
     */
    @Override
    public Class<? extends BusinessObject> getPrintSearchCriteriaClass() {
        return CollectionActivityReport.class;
    }
}
