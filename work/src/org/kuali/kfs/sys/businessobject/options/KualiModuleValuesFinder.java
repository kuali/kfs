/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.kfs.sys.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.service.ModuleService;

/**
 * Value Finder for Units Of Measure.
 */
public class KualiModuleValuesFinder extends KeyValuesBase {

    /**
     * Returns code/description pairs of all Units Of Measure.
     * 
     * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List<KeyValue> getKeyValues() {
        KualiModuleService moduleService = SpringContext.getBean(KualiModuleService.class);
        List<ModuleService> results = moduleService.getInstalledModuleServices();
        List<KeyValue> labels = new ArrayList<KeyValue>( results.size() );
        labels.add(new ConcreteKeyValue("", ""));
        for (ModuleService module : results) {
            labels.add(new ConcreteKeyValue(module.getModuleConfiguration().getNamespaceCode(), 
                    SpringContext.getBean(KualiModuleService.class).getNamespaceName(module.getModuleConfiguration().getNamespaceCode())));
        }
        return labels;
    }
}
