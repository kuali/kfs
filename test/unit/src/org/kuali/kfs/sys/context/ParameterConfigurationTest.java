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
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.suite.AnnotationTestSuite;
import org.kuali.kfs.sys.suite.PreCommitSuite;
import org.kuali.rice.coreservice.api.component.Component;
import org.kuali.rice.coreservice.api.component.ComponentService;
import org.kuali.rice.coreservice.impl.component.ComponentBo;
import org.kuali.rice.coreservice.impl.parameter.ParameterBo;
import org.kuali.rice.coreservice.web.parameter.ParameterRule;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.service.ModuleService;

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
        Collection<ParameterBo> params = SpringContext.getBean(BusinessObjectService.class).findAll(ParameterBo.class);
        ParameterRule paramRule = new ParameterRule();
        StringBuffer badComponents = new StringBuffer();
        int failCount = 0;
        System.out.println("Starting Component Validation");
        for (ParameterBo param : params) {
            // skip unimplemented modules
            ModuleService moduleService = SpringContext.getBean(KualiModuleService.class).getModuleServiceByNamespaceCode(param.getNamespaceCode()); 
            if ( moduleService == null || moduleService instanceof UnimplementedKfsModuleServiceImpl ) {
                continue;
            }
            try{
               if (!paramRule.checkComponent(param)) {
               if (param.getNamespaceCode().startsWith("KR"))continue;
                badComponents.append("\n").append(param.getNamespaceCode()).append("\t").append(param.getComponentCode()).append("\t").append(param.getName()).append("\t");
                failCount++;
            }
            }catch (Exception e){
                badComponents.append("\n").append(e.getMessage()).append(param.getNamespaceCode()).append("\t").append(param.getComponentCode()).append("\t").append(param.getName()).append("\t");
                failCount++;
            }
        }
        badComponents.insert(0, "The following " + failCount + " parameters have invalid components:");
        if (failCount > 0) {
            Set<String> components = new TreeSet<String>();
            for (Component pdt : SpringContext.getBean(ComponentService.class).getDerivedComponentSet(KFSConstants.APPLICATION_NAMESPACE_CODE)) {
                components.add(pdt.getNamespaceCode() + "/" + pdt.getCode());
            }
            for (ComponentBo pdt : (Collection<ComponentBo>) SpringContext.getBean(BusinessObjectService.class).findAll(ComponentBo.class)) {
                components.add(pdt.getNamespaceCode() + "/" + pdt.getCode());
            }
            System.out.println("Valid Components: ");
            for (String component : components) {
                System.out.println(component);
            }
        }
        assertTrue(badComponents.toString(), failCount == 0);
    }
}
