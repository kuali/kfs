/*
 * Copyright 2014 The Kuali Foundation.
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
package org.kuali.kfs.sys.context;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.datadictionary.AttributeDefinition;
import org.kuali.rice.krad.datadictionary.BusinessObjectEntry;
import org.kuali.rice.krad.service.PersistenceStructureService;

@ConfigureContext
public class DictionaryPrimaryKeyTest extends KualiTestBase {
    public void testPrimaryKeysHaveDDAttributes() {
        final DataDictionaryService ddService = SpringContext.getBean(DataDictionaryService.class);
        final PersistenceStructureService persistenceStructureService = SpringContext.getBean(PersistenceStructureService.class);
        final ConfigurationService configService = SpringContext.getBean(ConfigurationService.class);

        Set<String> missingPkFields = new HashSet<String>();
        final String appName = configService.getPropertyValueAsString(CoreConstants.Config.APPLICATION_ID).toLowerCase();

        for (BusinessObjectEntry boEntry : ddService.getDataDictionary().getBusinessObjectEntries().values()) {
            final Class<?> boClazz = boEntry.getBusinessObjectClass();
            if (boClazz.getName().contains(appName) && ((org.kuali.rice.kns.datadictionary.BusinessObjectEntry)boEntry).getLookupDefinition() != null && persistenceStructureService.isPersistable(boClazz)) {
                List pkFieldNames = persistenceStructureService.getPrimaryKeys(boClazz);
                for (Object pkFieldNameAsObject : pkFieldNames) {
                    final String pkFieldName = (String)pkFieldNameAsObject;

                    final AttributeDefinition attrDefn = boEntry.getAttributeDefinition(pkFieldName);
                    if (attrDefn == null) {
                        missingPkFields.add(boClazz.getName()+" "+pkFieldName);
                    }
                }
            }
        }
        if (!missingPkFields.isEmpty()) {
            System.err.println(StringUtils.join(missingPkFields, "\n"));
        }
        assertTrue("Some business object are missing pk fields as attributes in the data dictionary", missingPkFields.isEmpty());
    }
}
