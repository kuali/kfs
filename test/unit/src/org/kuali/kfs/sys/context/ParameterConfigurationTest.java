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
