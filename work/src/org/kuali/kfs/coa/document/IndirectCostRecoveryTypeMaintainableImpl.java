/*
 * Copyright 2008 The Kuali Foundation
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

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.coa.businessobject.IndirectCostRecoveryExclusionType;
import org.kuali.kfs.sys.document.FinancialSystemMaintainable;
import org.kuali.rice.kns.datadictionary.MaintainableSectionDefinition;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.util.MaintenanceUtils;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

public class IndirectCostRecoveryTypeMaintainableImpl extends FinancialSystemMaintainable {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(IndirectCostRecoveryTypeMaintainableImpl.class);
    public static final String DOCUMENT_ERROR_PREFIX = "document.";
    public static final String MAINTAINABLE_ERROR_PATH = DOCUMENT_ERROR_PREFIX + "newMaintainableObject";
    public static final String DETAIL_ERROR_PATH = MAINTAINABLE_ERROR_PATH + ".add.indirectCostRecoveryExclusionTypeDetails";

    @Override
    public void addMultipleValueLookupResults(MaintenanceDocument document, String collectionName, Collection<PersistableBusinessObject> rawValues, boolean needsBlank, PersistableBusinessObject bo) {
        // PersistableBusinessObject bo = document.getNewMaintainableObject().getBusinessObject();
        Collection maintCollection = (Collection) ObjectUtils.getPropertyValue(bo, collectionName);
        String docTypeName = document.getDocumentHeader().getWorkflowDocument().getDocumentTypeName();
        
        List<String> duplicateIdentifierFieldsFromDataDictionary = getDuplicateIdentifierFieldsFromDataDictionary(docTypeName, collectionName);
        
        List<String> existingIdentifierList = getMultiValueIdentifierList(maintCollection, duplicateIdentifierFieldsFromDataDictionary);
        
        Class collectionClass = getMaintenanceDocumentDictionaryService().getCollectionBusinessObjectClass(docTypeName, collectionName);

        List<MaintainableSectionDefinition> sections = getMaintenanceDocumentDictionaryService().getMaintainableSections(docTypeName);
        Map<String, String> template = MaintenanceUtils.generateMultipleValueLookupBOTemplate(sections, collectionName);
        try {
            int collectionItemNumber = 0; // is there a better way to do this? ie- old school i=0;i<rawValues.size();i++ and rawValues.get(i)?
            boolean isValid = true;
            GlobalVariables.getMessageMap().addToErrorPath(DETAIL_ERROR_PATH);
            for (PersistableBusinessObject nextBo : rawValues) {
                IndirectCostRecoveryExclusionType templatedBo = (IndirectCostRecoveryExclusionType) ObjectUtils.createHybridBusinessObject(collectionClass, nextBo, template);
                templatedBo.setNewCollectionRecord(true);
                prepareBusinessObjectForAdditionFromMultipleValueLookup(collectionName, templatedBo);
                if(!hasBusinessObjectExisted(templatedBo, existingIdentifierList, duplicateIdentifierFieldsFromDataDictionary)) {
                    maintCollection.add(templatedBo);
                }
                collectionItemNumber++;
                templatedBo.setActive(true); // TODO remove after active indicator work is complete
            }
            GlobalVariables.getMessageMap().removeFromErrorPath(DETAIL_ERROR_PATH);
            // putGlobalError(KFSKeyConstants.ERROR_DOCUMENT_ACCTDELEGATEMAINT_PRIMARY_ROUTE_ALREADY_EXISTS_FOR_DOCTYPE);
        } 
        catch (Exception e) {
            LOG.error("Unable to add multiple value lookup results " + e.getMessage());
            throw new RuntimeException("Unable to add multiple value lookup results " + e.getMessage());
        }
    }
    
}
