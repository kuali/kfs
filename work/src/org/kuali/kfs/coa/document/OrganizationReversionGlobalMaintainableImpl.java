/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.coa.document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.OrganizationReversion;
import org.kuali.kfs.coa.businessobject.OrganizationReversionCategory;
import org.kuali.kfs.coa.businessobject.OrganizationReversionGlobal;
import org.kuali.kfs.coa.businessobject.OrganizationReversionGlobalDetail;
import org.kuali.kfs.coa.businessobject.OrganizationReversionGlobalOrganization;
import org.kuali.kfs.coa.service.OrganizationReversionService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemGlobalMaintainable;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.lookup.LookupResultsService;
import org.kuali.rice.kns.web.ui.Column;
import org.kuali.rice.kns.web.ui.ResultRow;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.maintenance.MaintenanceLock;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * This class provides some specific functionality for the {@link OrganizationReversionGlobal} maintenance document inner class for
 * doing comparisons on {@link OrganizationReversionCategory} generateMaintenanceLocks - generates the appropriate maintenance locks
 * on {@link OrganizationReversion} setBusinessObject - populates the {@link OrganizationReversionGlobalDetail}s
 * isRelationshipRefreshable - makes sure that {@code organizationReversionGlobalDetails} isn't wiped out accidentally
 * processGlobalsAfterRetrieve - provides special handling for the details (which aren't a true collection)
 */
public class OrganizationReversionGlobalMaintainableImpl extends FinancialSystemGlobalMaintainable {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OrganizationReversionGlobalMaintainableImpl.class);

    private static transient OrganizationReversionService organizationReversionService;

    /**
     * This class is an inner class for comparing two {@link OrganizationReversionCategory}s
     */
    private class CategoryComparator implements Comparator<OrganizationReversionGlobalDetail> {
        @Override
        public int compare(OrganizationReversionGlobalDetail detailA, OrganizationReversionGlobalDetail detailB) {
            OrganizationReversionCategory categoryA = detailA.getOrganizationReversionCategory();
            OrganizationReversionCategory categoryB = detailB.getOrganizationReversionCategory();

            String code0 = categoryA.getOrganizationReversionCategoryCode();
            String code1 = categoryB.getOrganizationReversionCategoryCode();

            return code0.compareTo(code1);
        }
    }

    /**
     * This implementation locks all organization reversions that would be accessed by this global organization reversion. It does
     * not lock any OrganizationReversionDetail objects, as we expect that those will be inaccessible
     *
     * @see org.kuali.rice.kns.maintenance.KualiGlobalMaintainableImpl#generateMaintenaceLocks()
     */
    @Override
    public List<MaintenanceLock> generateMaintenanceLocks() {
        List<MaintenanceLock> locks = new ArrayList<MaintenanceLock>();
        OrganizationReversionGlobal globalOrgRev = (OrganizationReversionGlobal) this.getBusinessObject();
        if (globalOrgRev.getUniversityFiscalYear() != null && globalOrgRev.getOrganizationReversionGlobalOrganizations() != null && globalOrgRev.getOrganizationReversionGlobalOrganizations().size() > 0) { // only generate locks if we're going to have primary keys
            for (OrganizationReversionGlobalOrganization orgRevOrg : globalOrgRev.getOrganizationReversionGlobalOrganizations()) {
                MaintenanceLock maintenanceLock = new MaintenanceLock();
                maintenanceLock.setDocumentNumber(globalOrgRev.getDocumentNumber());

                StringBuilder lockRep = new StringBuilder();
                lockRep.append(OrganizationReversion.class.getName());
                lockRep.append(KFSConstants.Maintenance.AFTER_CLASS_DELIM);
                lockRep.append(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
                lockRep.append(KFSConstants.Maintenance.AFTER_FIELDNAME_DELIM);
                lockRep.append(orgRevOrg.getChartOfAccountsCode());
                lockRep.append(KFSConstants.Maintenance.AFTER_VALUE_DELIM);
                lockRep.append(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
                lockRep.append(KFSConstants.Maintenance.AFTER_FIELDNAME_DELIM);
                lockRep.append(globalOrgRev.getUniversityFiscalYear().toString());
                lockRep.append(KFSConstants.Maintenance.AFTER_VALUE_DELIM);
                lockRep.append(KFSPropertyConstants.ORGANIZATION_CODE);
                lockRep.append(KFSConstants.Maintenance.AFTER_FIELDNAME_DELIM);
                lockRep.append(orgRevOrg.getOrganizationCode());
                lockRep.append(KFSConstants.Maintenance.AFTER_VALUE_DELIM);

                maintenanceLock.setLockingRepresentation(lockRep.toString());
                locks.add(maintenanceLock);
            }
        }

        return locks;
    }

    /**
     * Just like OrganizationReversionMaintainableImpl's setBusinessObject method populates the list of details so there is one
     * detail per active Organization Reversion Category, this method populates a list of Organization Reversion Change details.
     *
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#setBusinessObject(org.kuali.rice.krad.bo.PersistableBusinessObject)
     */
    @Override
    public void processAfterNew(MaintenanceDocument document, Map<String, String[]> requestParameters) {
        super.processAfterNew(document, requestParameters);
        OrganizationReversionGlobal globalOrgRev = (OrganizationReversionGlobal) getBusinessObject();
        List<OrganizationReversionGlobalDetail> details = globalOrgRev.getOrganizationReversionGlobalDetails();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Details size before adding categories = " + details.size());
        }

        if (details == null) {
            details = new ArrayList<OrganizationReversionGlobalDetail>();
            globalOrgRev.setOrganizationReversionGlobalDetails(details);
        }

        if (details.size() == 0) {

            Collection<OrganizationReversionCategory> categories = getOrganizationReversionService().getCategoryList();
            for (OrganizationReversionCategory category : categories) {
                if (category.isActive()) {
                    OrganizationReversionGlobalDetail detail = new OrganizationReversionGlobalDetail();
                    detail.setOrganizationReversionCategoryCode(category.getOrganizationReversionCategoryCode());
                    detail.setOrganizationReversionCategory(category);
                    detail.setParentGlobalOrganizationReversion(globalOrgRev);
                    details.add(detail);
                }
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("Details size after adding categories = " + details.size());
            }
            Collections.sort(details, new CategoryComparator());
        }
    }

    /**
     * Prevents Organization Reversion Change Details from being refreshed by a look up (because doing that refresh before a save
     * would wipe out the list of organization reversion change details).
     *
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#isRelationshipRefreshable(java.lang.Class, java.lang.String)
     */
    @SuppressWarnings("unchecked")
    @Override
    protected boolean isRelationshipRefreshable(Class boClass, String relationshipName) {
        if (relationshipName.equals("organizationReversionGlobalDetails")) {
            return false;
        }
        else {
            return super.isRelationshipRefreshable(boClass, relationshipName);
        }
    }

    /**
     * The org reversion detail collection does not behave like a true collection (no add lines). The records on the collection
     * should not have the delete option.
     *
     * @see org.kuali.rice.kns.maintenance.KualiGlobalMaintainableImpl#processGlobalsAfterRetrieve()
     */
    @Override
    protected void processGlobalsAfterRetrieve() {
        super.processGlobalsAfterRetrieve();
        for (OrganizationReversionGlobalDetail changeDetail : ((OrganizationReversionGlobal) businessObject).getOrganizationReversionGlobalDetails()) {
            changeDetail.setNewCollectionRecord(false);
        }
    }

    /**
     *
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#refresh(java.lang.String, java.util.Map, org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void refresh(String refreshCaller, Map fieldValues, MaintenanceDocument document) {
        super.refresh(refreshCaller, fieldValues, document);
        final String lookupBusinessObjectClassName = (String)fieldValues.get(KFSConstants.LOOKUP_RESULTS_BO_CLASS_NAME);
        if (!StringUtils.isBlank(lookupBusinessObjectClassName)) {
            if (lookupBusinessObjectClassName.equals(OrganizationReversion.class.getName())) {
                final String lookupResultsSequenceNumber = (String)fieldValues.get(KFSConstants.LOOKUP_RESULTS_SEQUENCE_NUMBER);
                if (!StringUtils.isBlank(lookupResultsSequenceNumber)) {
                    final LookupResultsService lookupResultsService = SpringContext.getBean(LookupResultsService.class);
                    try {
                        final List<ResultRow> resultRows = lookupResultsService.retrieveResultsTable(lookupResultsSequenceNumber, GlobalVariables.getUserSession().getPrincipalId());
                        if (!resultRows.isEmpty()) {
                            final ResultRow topRow = resultRows.get(0);
                            for (Column column : topRow.getColumns()) {
                                if (column.getPropertyName().equals(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR)) {
                                    final String universityFiscalYearAsString = column.getPropertyValue();
                                    final Integer universityFiscalYear = Integer.parseInt(universityFiscalYearAsString);

                                    final OrganizationReversionGlobal orgRevGlobal = ((OrganizationReversionGlobal)document.getNewMaintainableObject().getBusinessObject());
                                    orgRevGlobal.setUniversityFiscalYear(universityFiscalYear);
                                }
                            }
                        }
                    }
                    catch (Exception ex) {
                        throw new RuntimeException("Could not retrieve lookup results to populate fiscal year", ex);
                    }


                }
            }
        }

    }

    @Override
    public Class<? extends PersistableBusinessObject> getPrimaryEditedBusinessObjectClass() {
        return OrganizationReversion.class;
    }

    protected OrganizationReversionService getOrganizationReversionService() {
        if ( organizationReversionService == null ) {
            organizationReversionService = SpringContext.getBean(OrganizationReversionService.class);
        }
        return organizationReversionService;
    }
}
