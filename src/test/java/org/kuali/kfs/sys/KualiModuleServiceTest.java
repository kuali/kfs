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
