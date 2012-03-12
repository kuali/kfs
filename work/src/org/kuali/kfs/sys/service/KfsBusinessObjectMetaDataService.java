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
package org.kuali.kfs.sys.service;

import java.util.List;

import org.kuali.kfs.sys.businessobject.BusinessObjectComponent;
import org.kuali.kfs.sys.businessobject.BusinessObjectProperty;
import org.kuali.kfs.sys.businessobject.DataMappingFieldDefinition;
import org.kuali.kfs.sys.businessobject.FunctionalFieldDescription;

public interface KfsBusinessObjectMetaDataService {
    public BusinessObjectProperty getBusinessObjectProperty(String componentClass, String propertyName);

    public DataMappingFieldDefinition getDataMappingFieldDefinition(String componentClass, String propertyName);

    public DataMappingFieldDefinition getDataMappingFieldDefinition(FunctionalFieldDescription functionalFieldDescription);

    public List<BusinessObjectComponent> findBusinessObjectComponents(String namespaceCode, String componentLabel);

    public List<BusinessObjectProperty> findBusinessObjectProperties(String namespaceCode, String componentLabel, String propertyLabel);

    public List<FunctionalFieldDescription> findFunctionalFieldDescriptions(String namespaceCode, String componentClass, String propertyName, String description, String active);

    public boolean isMatch(String componentClass, String propertyName, String tableNameSearchCriterion, String fieldNameSearchCriterion);

    public String getReferenceComponentLabel(Class componentClass, String propertyName);
}
