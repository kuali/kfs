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
package org.kuali.kfs.sys.context;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.kuali.kfs.integration.UnimplementedKfsModuleServiceImpl;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.suite.AnnotationTestSuite;
import org.kuali.kfs.sys.suite.PreCommitSuite;
import org.kuali.rice.core.api.parameter.Parameter;
import org.kuali.rice.core.api.parameter.ParameterDetailType;
import org.kuali.rice.krad.rules.ParameterRule;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.service.ModuleService;
import org.kuali.rice.krad.service.ParameterServerService;

@ConfigureContext(shouldCommitTransactions=true)
@AnnotationTestSuite(PreCommitSuite.class)
public class ParameterConfigurationTest extends KualiTestBase {
    private static final Logger LOG = Logger.getLogger(ParameterConfigurationTest.class);

    /**
     * 
     * This method...
     * @throws Exception
     */
    
    public void testValidateParameterComponents() throws Exception {
        Collection<Parameter> params = SpringContext.getBean(BusinessObjectService.class).findAll(Parameter.class);
        ParameterRule paramRule = new ParameterRule();
        StringBuffer badComponents = new StringBuffer();
        int failCount = 0;
        System.out.println("Starting Component Validation");
        for (Parameter param : params) {
            // skip unimplemented modules
            ModuleService moduleService = SpringContext.getBean(KualiModuleService.class).getModuleServiceByNamespaceCode(param.getParameterNamespaceCode()); 
            if ( moduleService == null || moduleService instanceof UnimplementedKfsModuleServiceImpl ) {
                continue;
            }
            try{
               if (!paramRule.checkComponent(param)) {
               if (param.getParameterNamespaceCode().startsWith("KR"))continue;
                badComponents.append("\n").append(param.getParameterNamespaceCode()).append("\t").append(param.getParameterDetailTypeCode()).append("\t").append(param.getParameterName()).append("\t");
                failCount++;
            }
            }catch (Exception e){
                badComponents.append("\n").append(e.getMessage()).append(param.getParameterNamespaceCode()).append("\t").append(param.getParameterDetailTypeCode()).append("\t").append(param.getParameterName()).append("\t");
                failCount++;
            }
        }
        badComponents.insert(0, "The following " + failCount + " parameters have invalid components:");
        if (failCount > 0) {
            Set<String> components = new TreeSet<String>();
            for (ParameterDetailType pdt : SpringContext.getBean(ParameterServerService.class).getNonDatabaseComponents()) {
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
