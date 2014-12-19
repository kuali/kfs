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

    /**
     * Always returns true, as collection activity report rows always have actions
     * @see org.kuali.kfs.module.ar.web.struts.ContractsGrantsReportLookupAction#shouldDisplayActionsForRow()
     */
    @Override
    public boolean shouldDisplayActionsForRow() {
        return true;
    }
}
