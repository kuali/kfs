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
