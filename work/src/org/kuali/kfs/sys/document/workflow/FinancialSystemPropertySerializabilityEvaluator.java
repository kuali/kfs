/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.sys.document.workflow;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.datadictionary.DataDictionary;
import org.kuali.rice.kns.datadictionary.DocumentEntry;
import org.kuali.rice.kns.datadictionary.WorkflowProperties;
import org.kuali.rice.kns.datadictionary.WorkflowProperty;
import org.kuali.rice.kns.datadictionary.WorkflowPropertyGroup;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.documentserializer.PropertySerializabilityEvaluator;
import org.kuali.rice.kns.util.documentserializer.PropertySerializabilityEvaluatorBase;
import org.kuali.rice.kns.util.documentserializer.PropertySerializerTrie;

/**
 * This class...
 */
public class FinancialSystemPropertySerializabilityEvaluator extends PropertySerializabilityEvaluatorBase implements PropertySerializabilityEvaluator {

    List<String> properties = new ArrayList<String>();

    /**
     * Reads the data dictionary to determine which properties of the document should be serialized.
     * 
     * @see org.kuali.rice.kns.util.documentserializer.PropertySerializabilityEvaluator#initializeEvaluator(org.kuali.rice.kns.document.Document)
     */
    public void initializeEvaluator(Document document) {
        serializableProperties = new PropertySerializerTrie();
        DataDictionary dictionary = KNSServiceLocator.getDataDictionaryService().getDataDictionary();
        DocumentEntry docEntry = dictionary.getDocumentEntry(document.getDocumentHeader().getWorkflowDocument().getDocumentType());

        WorkflowProperties workflowProperties = docEntry.getWorkflowProperties();
        
        if (null != workflowProperties) {
            for (WorkflowPropertyGroup group : workflowProperties.getWorkflowPropertyGroups()) {
                // the basepath of each workflow property group is serializable
                if (StringUtils.isEmpty(group.getBasePath())) {
                    // automatically serialize all primitives of document when the base path is null or empty string
                    serializableProperties.addSerializablePropertyName(document.getBasePathToDocumentDuringSerialization(), false);
                }
                else {
                    serializableProperties.addSerializablePropertyName(group.getBasePath(), false);
                }

                for (WorkflowProperty property : group.getWorkflowProperties()) {
                    String fullPath;
                    if (StringUtils.isEmpty(group.getBasePath())) {
                        fullPath = document.getBasePathToDocumentDuringSerialization() + "." + property.getPath();
                    }
                    else {
                        fullPath = group.getBasePath() + "." + property.getPath(); 
                    }
                    serializableProperties.addSerializablePropertyName(fullPath, false);
                }

            }
        }
        String path = document.getBasePathToDocumentDuringSerialization();
        for (String prop : properties) {
            serializableProperties.addSerializablePropertyName( path+ "." + prop, false);
        }
    }

    public void addPropertyPath(String path) {
        properties.add(path);

    }
}
