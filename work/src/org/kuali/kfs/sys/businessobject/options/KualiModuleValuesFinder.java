/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.kfs.sys.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesBase;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.KualiModuleService;
import org.kuali.rice.kns.service.ModuleService;
import org.kuali.rice.core.util.KeyLabelPair;

/**
 * Value Finder for Units Of Measure.
 */
public class KualiModuleValuesFinder extends KeyValuesBase {

    /**
     * Returns code/description pairs of all Units Of Measure.
     * 
     * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List<KeyLabelPair> getKeyValues() {
        KualiModuleService moduleService = SpringContext.getBean(KualiModuleService.class);
        List<ModuleService> results = moduleService.getInstalledModuleServices();
        List<KeyLabelPair> labels = new ArrayList<KeyLabelPair>( results.size() );
        labels.add(new KeyLabelPair("", ""));
        for (ModuleService module : results) {
            labels.add(new KeyLabelPair(module.getModuleConfiguration().getNamespaceCode(), 
                    SpringContext.getBean(KualiModuleService.class).getNamespaceName(module.getModuleConfiguration().getNamespaceCode())));
        }
        return labels;
    }
}
