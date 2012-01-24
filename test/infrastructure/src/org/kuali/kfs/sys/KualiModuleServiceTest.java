/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.sys;

import java.util.List;

import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.service.ModuleService;
import org.kuali.rice.krad.util.KRADConstants;

@ConfigureContext
public class KualiModuleServiceTest extends KualiTestBase {

    public void testGetInstalledModules() {
        KualiModuleService kmi = SpringContext.getBean(KualiModuleService.class);
        List<ModuleService> modules = kmi.getInstalledModuleServices();
        assertTrue( "There must be more than one module", modules.size() > 1 );
        boolean riceKnsFound = false;
        boolean riceKimFound = false;
        boolean riceKewFound = false;
        boolean riceKsbFound = false;
        boolean coaFound = false;
        boolean sysFound = false;
        boolean fpFound = false;
        boolean glFound = false;
        for ( ModuleService m : modules ) {
            String namespaceCode = m.getModuleConfiguration().getNamespaceCode();
            assertNotNull( "Namespace code must not be null in the ModuleConfiguration: " + m.getModuleConfiguration(), namespaceCode );
            System.out.println( namespaceCode );
            if ( namespaceCode.equals( KFSConstants.ParameterNamespaces.CHART ) ) {
                coaFound = true;
            }
            if ( namespaceCode.equals( KFSConstants.ParameterNamespaces.FINANCIAL ) ) {
                fpFound = true;
            }
            if ( namespaceCode.equals( KFSConstants.ParameterNamespaces.KFS ) ) {
                sysFound = true;
            }
            if ( namespaceCode.equals( KFSConstants.ParameterNamespaces.GL ) ) {
                glFound = true;
            }
            if ( namespaceCode.equals( KRADConstants.KRAD_NAMESPACE ) ) {
                riceKnsFound = true;
            }
            if ( namespaceCode.equals( KimConstants.NAMESPACE_CODE ) ) {
                riceKimFound = true;
            }
            if ( namespaceCode.equals( KRADConstants.KUALI_RICE_WORKFLOW_NAMESPACE ) ) {
                riceKewFound = true;
            }
            if ( namespaceCode.equals( KRADConstants.KUALI_RICE_SERVICE_BUS_NAMESPACE ) ) {
                riceKsbFound = true;
            }
        }
        // TODO: constants in Rice do not match the namespaces used in the configuration
        // so commenting out for now until the codes have stabilized
        assertTrue( "Unable to find " + KRADConstants.KRAD_NAMESPACE + " module in installed modules list", riceKnsFound );
        assertTrue( "Unable to find " + KRADConstants.KUALI_RICE_WORKFLOW_NAMESPACE + " module in installed modules list", riceKewFound );
        assertTrue( "Unable to find " + KimConstants.NAMESPACE_CODE + " module in installed modules list", riceKimFound );
        assertTrue( "Unable to find " + KRADConstants.KUALI_RICE_SERVICE_BUS_NAMESPACE + " module in installed modules list", riceKsbFound );
        assertTrue( "Unable to find " + KFSConstants.ParameterNamespaces.CHART + " module in installed modules list", coaFound );
        assertTrue( "Unable to find " + KFSConstants.ParameterNamespaces.KFS + " module in installed modules list", sysFound );
        assertTrue( "Unable to find " + KFSConstants.ParameterNamespaces.FINANCIAL + " module in installed modules list", fpFound );
        assertTrue( "Unable to find " + KFSConstants.ParameterNamespaces.GL + " module in installed modules list", glFound );
    }
}
