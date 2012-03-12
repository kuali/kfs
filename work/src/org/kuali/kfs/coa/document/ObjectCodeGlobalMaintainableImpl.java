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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.ObjectCodeGlobal;
import org.kuali.kfs.coa.businessobject.ObjectCodeGlobalDetail;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.coa.service.SubObjectTrickleDownInactivationService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemGlobalMaintainable;
import org.kuali.rice.krad.bo.GlobalBusinessObject;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.maintenance.MaintenanceLock;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This class provides some specific functionality for the {@link ObjectCodeGlobal} maintenance document refresh - sets the current
 * fiscal year from the {@link ObjectCodeGlobalDetail} prepareGlobalsForSave - sets the object code on each detail object in the
 * collection generateMaintenanceLocks - generates the appropriate maintenance locks for the {@link ObjectCode}
 */
public class ObjectCodeGlobalMaintainableImpl extends FinancialSystemGlobalMaintainable {
    private static String CHANGE_DETAIL_COLLECTION = "objectCodeGlobalDetails";

    //@Override
    //public void refresh(String refreshCaller, Map fieldValues, MaintenanceDocument document) {
    //  if our detail objects have a null fiscal year we need to fill these in with the "addLine" fiscal year
    //      otherwise we leave it alone, these should only be null when coming back from a multiple value lookup
    //    if (refreshCaller != null && refreshCaller.equals(KFSConstants.MULTIPLE_VALUE)) {
    //        ObjectCodeGlobal objectCodeGlobal = (ObjectCodeGlobal) document.getDocumentBusinessObject();
    //        ObjectCodeGlobalDetail addLineDetail = (ObjectCodeGlobalDetail) newCollectionLines.get(CHANGE_DETAIL_COLLECTION);
    //        int fiscalYear = addLineDetail.getUniversityFiscalYear();
    //        for (ObjectCodeGlobalDetail detail : objectCodeGlobal.getObjectCodeGlobalDetails()) {
    //            if (detail.getUniversityFiscalYear() == null) {
    //                detail.setUniversityFiscalYear(fiscalYear);
    //            }
    //        }
    //    }

    //    super.refresh(refreshCaller, fieldValues, document);
    //}

    //
    // @Override
    // protected List<String> getMultiValueIdentifierList(Collection maintCollection) {
    // List<String> identifierList = new ArrayList<String>();
    // for (ObjectCodeGlobalDetail bo : (Collection<ObjectCodeGlobalDetail>)maintCollection) {
    // identifierList.add(bo.getChartOfAccountsCode());
    // }
    // return identifierList;
    // }

    // @Override
    // protected boolean hasBusinessObjectExistedInLookupResult(BusinessObject bo, List<String> existingIdentifierList) {
    // // default implementation does nothing
    // if (existingIdentifierList.contains(((ObjectCodeGlobalDetail)bo).getChartOfAccountsCode())) {
    // return true;
    // }
    // else {
    // return false;
    // }
    // }

    /**
     * This method sets the object code on each detail object in the collection
     */
    @Override
    protected void prepareGlobalsForSave() {
        // copy the object code down from the header into the details
        ObjectCodeGlobal objectCodeGlobal = (ObjectCodeGlobal) getBusinessObject();

        for (ObjectCodeGlobalDetail detail : objectCodeGlobal.getObjectCodeGlobalDetails()) {
            detail.setFinancialObjectCode(objectCodeGlobal.getFinancialObjectCode());
        }
        super.prepareGlobalsForSave();
    }

    /**
     * This generates the appropriate maintenance locks for the {@link ObjectCode}
     * 
     * @see org.kuali.rice.kns.maintenance.Maintainable#generateMaintenanceLocks()
     */
    @Override
    public List<MaintenanceLock> generateMaintenanceLocks() {
        ObjectCodeGlobal objectCodeGlobal = (ObjectCodeGlobal) getBusinessObject();
        List<MaintenanceLock> maintenanceLocks = new ArrayList();
        SubObjectTrickleDownInactivationService subObjectTrickleDownInactivationService = SpringContext.getBean(SubObjectTrickleDownInactivationService.class);
        
        for (ObjectCodeGlobalDetail detail : objectCodeGlobal.getObjectCodeGlobalDetails()) {
            MaintenanceLock maintenanceLock = new MaintenanceLock();
            StringBuffer lockrep = new StringBuffer();

            lockrep.append(ObjectCode.class.getName() + KFSConstants.Maintenance.AFTER_CLASS_DELIM);
            lockrep.append("universityFiscalYear" + KFSConstants.Maintenance.AFTER_FIELDNAME_DELIM);
            lockrep.append(detail.getUniversityFiscalYear() + KFSConstants.Maintenance.AFTER_VALUE_DELIM);
            lockrep.append("chartOfAccountsCode" + KFSConstants.Maintenance.AFTER_FIELDNAME_DELIM);
            lockrep.append(detail.getChartOfAccountsCode() + KFSConstants.Maintenance.AFTER_VALUE_DELIM);
            lockrep.append("financialObjectCode" + KFSConstants.Maintenance.AFTER_FIELDNAME_DELIM);
            lockrep.append(detail.getFinancialObjectCode());

            maintenanceLock.setDocumentNumber(objectCodeGlobal.getDocumentNumber());
            maintenanceLock.setLockingRepresentation(lockrep.toString());
            maintenanceLocks.add(maintenanceLock);
            
            ObjectCode objectCode = new ObjectCode();
            objectCode.setUniversityFiscalYear(detail.getUniversityFiscalYear());
            objectCode.setChartOfAccountsCode(detail.getChartOfAccountsCode());
            objectCode.setFinancialObjectCode(detail.getFinancialObjectCode());
            objectCode.setActive(objectCodeGlobal.isFinancialObjectActiveIndicator());
            
            if (isInactivatingObjectCode(objectCode)) {
                // if it turns out that the object code does not have associated sub-objects (either because the object code doesn't exist or doesn't have sub-objects)
                // then the generateTrickleDownMaintenanceLocks method returns an empty list 
                maintenanceLocks.addAll(subObjectTrickleDownInactivationService.generateTrickleDownMaintenanceLocks(objectCode, getDocumentNumber()));
            }
        }
        return maintenanceLocks;
    }
    
    /**
     * @see org.kuali.rice.kns.maintenance.Maintainable#saveBusinessObject()
     */
    @Override
    public void saveBusinessObject() {
        BusinessObjectService boService = SpringContext.getBean(BusinessObjectService.class);
        
        GlobalBusinessObject gbo = (GlobalBusinessObject) businessObject;

        // delete any indicated BOs
        List<PersistableBusinessObject> bosToDeactivate = gbo.generateDeactivationsToPersist();
        if (bosToDeactivate != null) {
            if (!bosToDeactivate.isEmpty()) {
                boService.save(bosToDeactivate);
            }
        }
        
        // OJB caches the any ObjectCodes that are retrieved from the database.  If multiple queries return the same row (identified by the PK
        // values), OJB will return the same instance of the ObjectCode.  However, in generateGlobalChangesToPersist(), the ObjectCode returned by
        // OJB is altered, meaning that any subsequent OJB calls will return the altered object.  The following cache will store the active statuses
        // of object codes affected by this global document before generateGlobalChangesToPersist() alters them.
        Map<String, Boolean> objectCodeActiveStatusCache = buildObjectCodeActiveStatusCache((ObjectCodeGlobal) gbo);
        
        SubObjectTrickleDownInactivationService subObjectTrickleDownInactivationService = SpringContext.getBean(SubObjectTrickleDownInactivationService.class);
        // persist any indicated BOs
        List<PersistableBusinessObject> bosToPersist = gbo.generateGlobalChangesToPersist();
        if (bosToPersist != null) {
            if (!bosToPersist.isEmpty()) {
                for (PersistableBusinessObject bo : bosToPersist) {
                    ObjectCode objectCode = (ObjectCode) bo;
                    
                    boService.save(objectCode);
                    
                    if (isInactivatingObjectCode(objectCode, objectCodeActiveStatusCache)) {
                        subObjectTrickleDownInactivationService.trickleDownInactivateSubObjects(objectCode, getDocumentNumber());
                    }
                }
            }
        }
    }
    
    protected boolean isInactivatingObjectCode(ObjectCode objectCode) {
        ObjectCodeService objectCodeService = SpringContext.getBean(ObjectCodeService.class);
        if (!objectCode.isActive()) {
            ObjectCode objectCodeFromDB = objectCodeService.getByPrimaryId(objectCode.getUniversityFiscalYear(), objectCode.getChartOfAccountsCode(), objectCode.getFinancialObjectCode());
            if (objectCodeFromDB != null && objectCodeFromDB.isActive()) {
                return true;
            }
        }
        return false;
    }
    
    protected boolean isInactivatingObjectCode(ObjectCode objectCode, Map<String, Boolean> objectCodeActiveStatusCache) {
        if (!objectCode.isActive()) {
            if (Boolean.TRUE.equals(objectCodeActiveStatusCache.get(buildObjectCodeCachingKey(objectCode)))) {
                return true;
            }
        }
        return false;
    }
    
    protected String buildObjectCodeCachingKey(ObjectCode objectCode) {
        return objectCode.getUniversityFiscalYear() + KRADConstants.Maintenance.LOCK_AFTER_VALUE_DELIM + objectCode.getChartOfAccountsCode() + 
                KRADConstants.Maintenance.LOCK_AFTER_VALUE_DELIM + objectCode.getFinancialObjectCode(); 
    }
    
    protected Map<String, Boolean> buildObjectCodeActiveStatusCache(ObjectCodeGlobal objectCodeGlobal) {
        ObjectCodeService objectCodeService = SpringContext.getBean(ObjectCodeService.class);
        Map<String, Boolean> cache = new HashMap<String, Boolean>();
        for (ObjectCodeGlobalDetail detail : objectCodeGlobal.getObjectCodeGlobalDetails()) {
            ObjectCode objectCodeFromDB = objectCodeService.getByPrimaryId(detail.getUniversityFiscalYear(), detail.getChartOfAccountsCode(), objectCodeGlobal.getFinancialObjectCode());
            if (ObjectUtils.isNotNull(objectCodeFromDB)) {
                cache.put(buildObjectCodeCachingKey(objectCodeFromDB), Boolean.valueOf(objectCodeFromDB.isActive()));
            }
        }
        return cache;
    }

    @Override
    public Class<? extends PersistableBusinessObject> getPrimaryEditedBusinessObjectClass() {
        return ObjectCode.class;
    }
}
