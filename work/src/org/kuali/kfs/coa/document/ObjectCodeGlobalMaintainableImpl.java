/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.chart.maintenance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.core.bo.BusinessObject;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.document.MaintenanceLock;
import org.kuali.core.maintenance.KualiGlobalMaintainableImpl;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSConstants;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.ObjectCodeGlobalDetail;
import org.kuali.module.chart.bo.ObjectCodeGlobal;

public class ObjectCodeGlobalMaintainableImpl extends KualiGlobalMaintainableImpl {
    private static String CHANGE_DETAIL_COLLECTION = "objectCodeGlobalDetails";
    
    /**
     * 
     * @see org.kuali.core.maintenance.KualiMaintainableImpl#refresh(java.lang.String, java.util.Map, org.kuali.core.document.MaintenanceDocument)
     */
    @Override
    public void refresh(String refreshCaller, Map fieldValues, MaintenanceDocument document) {
        //if our detail objects have a null fiscal year we need to fill these in with the "addLine" fiscal year
        //otherwise we leave it alone, these should only be null when coming back from a multiple value lookup
        if(refreshCaller != null && refreshCaller.equals(KFSConstants.MULTIPLE_VALUE)) {
            ObjectCodeGlobal objectCodeGlobal = (ObjectCodeGlobal)document.getDocumentBusinessObject();
            ObjectCodeGlobalDetail addLineDetail = (ObjectCodeGlobalDetail)newCollectionLines.get(CHANGE_DETAIL_COLLECTION);
            int fiscalYear = addLineDetail.getUniversityFiscalYear();
            for ( ObjectCodeGlobalDetail detail : objectCodeGlobal.getObjectCodeGlobalDetails() ) {
                if(detail.getUniversityFiscalYear() == null) {
                    
                    detail.setUniversityFiscalYear(fiscalYear);
                }
            }
        }
        
        super.refresh(refreshCaller, fieldValues, document);
    }
//
//    @Override
//    protected List<String> getMultiValueIdentifierList(Collection maintCollection) {
//        List<String> identifierList = new ArrayList<String>();
//        for (ObjectCodeGlobalDetail bo : (Collection<ObjectCodeGlobalDetail>)maintCollection) {
//            identifierList.add(bo.getChartOfAccountsCode());
//        }
//        return identifierList;
//    }

//    @Override
//    protected boolean hasBusinessObjectExistedInLookupResult(BusinessObject bo, List<String> existingIdentifierList) {
//        // default implementation does nothing
//        if (existingIdentifierList.contains(((ObjectCodeGlobalDetail)bo).getChartOfAccountsCode())) {
//            return true; 
//        }
//        else {
//            return false;
//        }
//    } 
    
    /**
     * This method does special-case handling for Globals, filling out various fields that need to be filled, etc.
     */
    @Override
    protected void prepareGlobalsForSave() {
        // copy the object code down from the header into the details
        ObjectCodeGlobal objectCodeGlobal = (ObjectCodeGlobal)getBusinessObject();
        
        for ( ObjectCodeGlobalDetail detail : objectCodeGlobal.getObjectCodeGlobalDetails() ) {
            detail.setFinancialObjectCode( objectCodeGlobal.getFinancialObjectCode() );
        }
        super.prepareGlobalsForSave();
    }

    /**
     * This creates the particular locking representation for this global document.
     * @see org.kuali.core.maintenance.Maintainable#generateMaintenanceLocks()
     */
    @Override
    public List<MaintenanceLock> generateMaintenanceLocks() {
        ObjectCodeGlobal objectCodeGlobal = (ObjectCodeGlobal)getBusinessObject();
        List <MaintenanceLock> maintenanceLocks = new ArrayList();
        
        for ( ObjectCodeGlobalDetail detail : objectCodeGlobal.getObjectCodeGlobalDetails() ) {
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
        }
        return maintenanceLocks;
    }
}
