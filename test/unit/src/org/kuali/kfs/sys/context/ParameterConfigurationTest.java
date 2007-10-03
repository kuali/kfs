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
package org.kuali.core.rule;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import org.kuali.core.bo.Parameter;
import org.kuali.core.bo.ParameterDetailType;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.rules.ParameterRule;
import org.kuali.kfs.service.ParameterService;
import org.kuali.test.ConfigureContext;

@ConfigureContext
public class ParameterConfigurationTest extends KualiTestBase {

    public void testValidateParameterComponents() throws Exception {
        Collection<Parameter> params = SpringContext.getBean(BusinessObjectService.class).findAll(Parameter.class);
        ParameterRule paramRule = new ParameterRule();
        StringBuffer badComponents = new StringBuffer();
        int failCount = 0;
        for (Parameter param : params) {
            if (!paramRule.checkComponent(param)) {
                badComponents.append("\n").append(param.getParameterNamespaceCode()).append("\t").append(param.getParameterDetailTypeCode()).append("\t").append(param.getParameterName()).append("\t");
                failCount++;
            }
        }
        badComponents.insert(0, "The following " + failCount + " parameters have invalid components:");
        if (failCount > 0) {
            Set<String> components = new TreeSet<String>();
            for (ParameterDetailType pdt : SpringContext.getBean(ParameterService.class).getNonDatabaseDetailTypes()) {
                components.add(pdt.getParameterNamespaceCode() + "/" + pdt.getParameterDetailTypeCode());
            }
            for (ParameterDetailType pdt : (Collection<ParameterDetailType>) SpringContext.getBean(BusinessObjectService.class).findAll(ParameterDetailType.class)) {
                components.add(pdt.getParameterNamespaceCode() + "/" + pdt.getParameterDetailTypeCode());
            }
            System.out.println("Valid Components: ");
            for (String component : components) {
                System.out.println(component);
            }
        }
        assertTrue(badComponents.toString(), failCount == 0);
    }
}