/*
 * Copyright 2008-2009 The Kuali Foundation
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
package org.kuali.kfs.module.ar.document.validation.impl;

import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.CollectorHierarchy;
import org.kuali.kfs.module.ar.businessobject.CollectorInformation;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This class is Rule class for CollectorHierarchy
 */
public class CollectorHierarchyRule extends MaintenanceDocumentRuleBase {

    protected CollectorHierarchy oldCollectorHierarchy;
    protected CollectorHierarchy newCollectorHierarchy;
    protected DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);

    /**
     * This method initializes the old and new customer
     * 
     * @param document
     */
    protected void initializeAttributes(MaintenanceDocument document) {
        if (ObjectUtils.isNull(newCollectorHierarchy)) {
            newCollectorHierarchy = (CollectorHierarchy) document.getNewMaintainableObject().getBusinessObject();
        }
        if (ObjectUtils.isNull(oldCollectorHierarchy)) {
            oldCollectorHierarchy = (CollectorHierarchy) document.getOldMaintainableObject().getBusinessObject();
        }
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        boolean isValid = true;
        isValid &= super.processCustomRouteDocumentBusinessRules(document);
        MessageMap errorMap = GlobalVariables.getMessageMap();
        isValid &= errorMap.hasErrors();
        if (isValid) {
            initializeAttributes(document);
            isValid &= validateCollectorInformations(newCollectorHierarchy);
        }

        return isValid;
    }


    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomAddCollectionLineBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument,
     *      java.lang.String, org.kuali.rice.krad.bo.PersistableBusinessObject)
     */
    @Override
    public boolean processCustomAddCollectionLineBusinessRules(MaintenanceDocument document, String collectionName, PersistableBusinessObject line) {
        boolean isValid = true;
        isValid &= super.processCustomAddCollectionLineBusinessRules(document, collectionName, line);
        MessageMap errorMap = GlobalVariables.getMessageMap();
        isValid &= errorMap.hasErrors();
        initializeAttributes(document);
        if (collectionName.equals(ArPropertyConstants.CollectorHierarchyFields.COLLECTOR_INFORMATIONS)) {
            CollectorInformation collectorInformation = (CollectorInformation) line;

            if (isValid) {
                isValid &= checkCollectorInformationIsValid(collectorInformation);
            }

        }
        return isValid;

    }

    /**
     * This method checks if the collectorInformation is valid
     * 
     * @param collectorInformation
     * @return true if valid, false otherwise
     */
    public boolean checkCollectorInformationIsValid(CollectorInformation collectorInformation, int ind) {
        boolean isValid = true;
        String propertyName = ArPropertyConstants.CollectorHierarchyFields.COLLECTOR_INFORMATIONS + "[" + ind + "].";
        if (ObjectUtils.isNotNull(this.newCollectorHierarchy.getPrincipalId())) {
            if (this.newCollectorHierarchy.getPrincipalId().equalsIgnoreCase(collectorInformation.getPrincipalId())) {
                isValid = false;
                putFieldError(propertyName + ArPropertyConstants.CollectorHierarchyFields.COLLECTOR_PRINC_NAME, ArKeyConstants.CollectionHierarchyDocumentErrors.ERROR_COLLINFO_SAME_AS_COLLHEAD);
            }
        }
        if (isValid) {
            // chk if already exists in list
            int count = 0;
            if (ObjectUtils.isNotNull(this.newCollectorHierarchy.getCollectorInformations()) && !this.newCollectorHierarchy.getCollectorInformations().isEmpty()) {
                for (CollectorInformation collInfo : this.newCollectorHierarchy.getCollectorInformations()) {
                    if (collectorInformation.getPrincipalId().equalsIgnoreCase(collInfo.getPrincipalId())) {
                        count++;
                    }
                }
            }
            if (count > 1) {
                isValid = false;
                putFieldError(propertyName + ArPropertyConstants.CollectorHierarchyFields.COLLECTOR_PRINC_NAME, ArKeyConstants.CollectionHierarchyDocumentErrors.ERROR_DUPLICATE_COLL_INFO);
            }
        }
        return isValid;
    }

    /**
     * This method validates the given collector information.
     * 
     * @param collectorInformation.
     * @return Returns true if given collector information is valid.
     */
    public boolean checkCollectorInformationIsValid(CollectorInformation collectorInformation) {
        boolean isValid = true;
        if (ObjectUtils.isNotNull(this.newCollectorHierarchy.getPrincipalId())) {
            if (this.newCollectorHierarchy.getPrincipalId().equalsIgnoreCase(collectorInformation.getPrincipalId())) {
                isValid = false;
                GlobalVariables.getMessageMap().putError(ArPropertyConstants.CollectorHierarchyFields.COLLECTOR_PRINC_NAME, ArKeyConstants.CollectionHierarchyDocumentErrors.ERROR_COLLINFO_SAME_AS_COLLHEAD);
            }
        }
        if (isValid) {
            // chk if already exists in list
            if (ObjectUtils.isNotNull(this.newCollectorHierarchy.getCollectorInformations()) && !this.newCollectorHierarchy.getCollectorInformations().isEmpty()) {
                for (CollectorInformation collInfo : this.newCollectorHierarchy.getCollectorInformations()) {
                    if (collectorInformation.getPrincipalId().equalsIgnoreCase(collInfo.getPrincipalId())) {
                        isValid = false;
                        GlobalVariables.getMessageMap().putError(ArPropertyConstants.CollectorHierarchyFields.COLLECTOR_PRINC_NAME, ArKeyConstants.CollectionHierarchyDocumentErrors.ERROR_DUPLICATE_COLL_INFO);
                    }
                }
            }
        }
        return isValid;
    }

    /**
     * This method validates the collector Hierarchy.
     * 
     * @param collectorHierarchy
     * @return
     */
    public boolean validateCollectorInformations(CollectorHierarchy collectorHierarchy) {
        boolean isValid = true;
        int i = 0;
        for (CollectorInformation collectorInformation : collectorHierarchy.getCollectorInformations()) {
            if (ObjectUtils.isNotNull(collectorInformation.getPrincipalId())) {
                isValid &= checkCollectorInformationIsValid(collectorInformation, i);
            }
            i++;
        }
        return isValid;
    }


}
