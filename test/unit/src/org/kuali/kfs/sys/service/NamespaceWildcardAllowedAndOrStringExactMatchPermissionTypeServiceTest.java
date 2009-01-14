/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.sys.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.role.dto.KimPermissionInfo;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kns.service.impl.NamespaceWildcardAllowedAndOrStringExactMatchPermissionTypeServiceImpl;
import org.kuali.rice.kns.util.KNSConstants;

@ConfigureContext
public class NamespaceWildcardAllowedAndOrStringExactMatchPermissionTypeServiceTest extends KualiTestBase {

    private static final String BUILDING_BO = "org.kuali.rice.kns.businessobject.Building";
    private static final String COUNTRY_BO = "org.kuali.rice.kns.businessobject.Country";
    
    NamespaceWildcardAllowedAndOrStringExactMatchPermissionTypeServiceImpl permissionService = new NamespaceWildcardAllowedAndOrStringExactMatchPermissionTypeServiceImpl();
    {
        permissionService.setExactMatchStringAttributeName( KimAttributes.COMPONENT_NAME );
        permissionService.setNamespaceRequiredOnStoredAttributeSet(true);
    }
    
    public void testPerformMatches_exact() {
        permissionService.setNamespaceRequiredOnStoredAttributeSet(true);
        
        AttributeSet requestedDetails = new AttributeSet();
        requestedDetails.put(KimAttributes.NAMESPACE_CODE, KNSConstants.KNS_NAMESPACE );
        requestedDetails.put(KimAttributes.COMPONENT_NAME, BUILDING_BO );
        
        List<KimPermissionInfo> permissions = 
            buildPermissionList( new String[][] { {KNSConstants.KNS_NAMESPACE,BUILDING_BO} } );
        
        List<KimPermissionInfo> results = permissionService.getMatchingPermissions(requestedDetails, permissions);
        System.out.println( results );
        assertEquals( "Wrong number of matches", 1, results.size() );
        assertPermInList( results, KNSConstants.KNS_NAMESPACE, BUILDING_BO );
    }

    public void testPerformMatches_list_with_other_component() {
        permissionService.setNamespaceRequiredOnStoredAttributeSet(true);
        
        AttributeSet requestedDetails = new AttributeSet();
        requestedDetails.put(KimAttributes.NAMESPACE_CODE, KNSConstants.KNS_NAMESPACE );
        requestedDetails.put(KimAttributes.COMPONENT_NAME, BUILDING_BO );
        
        List<KimPermissionInfo> permissions = 
            buildPermissionList( new String[][] { {KNSConstants.KNS_NAMESPACE,BUILDING_BO},
                    {KNSConstants.KNS_NAMESPACE,COUNTRY_BO} } );
        
        List<KimPermissionInfo> results = permissionService.getMatchingPermissions(requestedDetails, permissions);
        System.out.println( results );
        assertEquals( "Wrong number of matches", 1, results.size() );
        assertPermInList( results, KNSConstants.KNS_NAMESPACE, BUILDING_BO );
    }
    
    public void testPerformMatches_missing_namespace() {
        permissionService.setNamespaceRequiredOnStoredAttributeSet(true);
        
        AttributeSet requestedDetails = new AttributeSet();
        requestedDetails.put(KimAttributes.NAMESPACE_CODE, KNSConstants.KNS_NAMESPACE );
        requestedDetails.put(KimAttributes.COMPONENT_NAME, BUILDING_BO );
        
        List<KimPermissionInfo> permissions = 
            buildPermissionList( new String[][] { {null,BUILDING_BO} } );
        
        List<KimPermissionInfo> results = permissionService.getMatchingPermissions(requestedDetails, permissions);
        System.out.println( results );
        assertEquals( "Wrong number of matches", 0, results.size() );
    }

    public void testPerformMatches_missing_namespace_allowed() {
        permissionService.setNamespaceRequiredOnStoredAttributeSet(false);
        
        AttributeSet requestedDetails = new AttributeSet();
        requestedDetails.put(KimAttributes.NAMESPACE_CODE, KNSConstants.KNS_NAMESPACE );
        requestedDetails.put(KimAttributes.COMPONENT_NAME, BUILDING_BO );
        
        List<KimPermissionInfo> permissions = 
            buildPermissionList( new String[][] { {null,BUILDING_BO} } );
        
        List<KimPermissionInfo> results = permissionService.getMatchingPermissions(requestedDetails, permissions);
        System.out.println( results );
        assertEquals( "Wrong number of matches", 1, results.size() );
        assertPermInList( results, null, BUILDING_BO );
    }

    
    public void testPerformMatches_namespace_and_wildcard_namespace() {
        permissionService.setNamespaceRequiredOnStoredAttributeSet(false);
        
        AttributeSet requestedDetails = new AttributeSet();
        requestedDetails.put(KimAttributes.NAMESPACE_CODE, KNSConstants.KNS_NAMESPACE );
        requestedDetails.put(KimAttributes.COMPONENT_NAME, BUILDING_BO );
        
        List<KimPermissionInfo> permissions = 
            buildPermissionList( new String[][] { {KNSConstants.KNS_NAMESPACE,BUILDING_BO},
                    {"KR-*",BUILDING_BO}} );
        
        List<KimPermissionInfo> results = permissionService.getMatchingPermissions(requestedDetails, permissions);
        System.out.println( results );
        assertEquals( "Wrong number of matches", 1, results.size() );
        assertPermInList( results, KNSConstants.KNS_NAMESPACE, BUILDING_BO );
    }
    
    public void testPerformMatches_namespace_and_missing_namespace() {
        permissionService.setNamespaceRequiredOnStoredAttributeSet(false);
        
        AttributeSet requestedDetails = new AttributeSet();
        requestedDetails.put(KimAttributes.NAMESPACE_CODE, KNSConstants.KNS_NAMESPACE );
        requestedDetails.put(KimAttributes.COMPONENT_NAME, BUILDING_BO );
        
        List<KimPermissionInfo> permissions = 
            buildPermissionList( new String[][] { {KNSConstants.KNS_NAMESPACE,BUILDING_BO},
                    {null,BUILDING_BO}} );
        
        List<KimPermissionInfo> results = permissionService.getMatchingPermissions(requestedDetails, permissions);
        System.out.println( results );
        assertEquals( "Wrong number of matches", 1, results.size() );
        assertPermInList( results, KNSConstants.KNS_NAMESPACE, BUILDING_BO );
    }
    
    public void testPerformMatches_missing_namespace_and_wildcard_namespace() {
        permissionService.setNamespaceRequiredOnStoredAttributeSet(false);
        
        AttributeSet requestedDetails = new AttributeSet();
        requestedDetails.put(KimAttributes.NAMESPACE_CODE, KNSConstants.KNS_NAMESPACE );
        requestedDetails.put(KimAttributes.COMPONENT_NAME, BUILDING_BO );
        
        List<KimPermissionInfo> permissions = 
            buildPermissionList( new String[][] { {null,BUILDING_BO},
                    {"KR-*",BUILDING_BO}} );
        
        List<KimPermissionInfo> results = permissionService.getMatchingPermissions(requestedDetails, permissions);
        System.out.println( results );
        assertEquals( "Wrong number of matches", 1, results.size() );
        assertPermInList( results, "KR-*", BUILDING_BO );
    }
    
    public void testPerformMatches_namespace_on_other_component() {
        permissionService.setNamespaceRequiredOnStoredAttributeSet(false);
        
        AttributeSet requestedDetails = new AttributeSet();
        requestedDetails.put(KimAttributes.NAMESPACE_CODE, KNSConstants.KNS_NAMESPACE );
        requestedDetails.put(KimAttributes.COMPONENT_NAME, BUILDING_BO );
        
        List<KimPermissionInfo> permissions = 
            buildPermissionList( new String[][] { {null,BUILDING_BO},
                    {KNSConstants.KNS_NAMESPACE,COUNTRY_BO}} );
        
        List<KimPermissionInfo> results = permissionService.getMatchingPermissions(requestedDetails, permissions);
        System.out.println( results );
        assertEquals( "Wrong number of matches", 1, results.size() );
        assertPermInList( results, null, BUILDING_BO );
    }    
    
    public void testMatchWithMissingPermissionNamespace() {
        permissionService.setNamespaceRequiredOnStoredAttributeSet(false);
        
        AttributeSet requestedDetails = new AttributeSet();
        requestedDetails.put(KimAttributes.NAMESPACE_CODE, "KFS-BC" );
        requestedDetails.put(KimAttributes.COMPONENT_NAME, "org.kuali.kfs.module.bc.document.web.struts.BudgetConstructionSelectionAction" );
        
        List<KimPermissionInfo> permissions = new ArrayList<KimPermissionInfo>();
        KimPermissionInfo kpi = new KimPermissionInfo();
        kpi.setDetails(new AttributeSet() );
        kpi.getDetails().put(KimAttributes.COMPONENT_NAME, "org.kuali.kfs.module.bc.document.web.struts.BudgetConstructionSelectionAction" );
        permissions.add(kpi);
        
        List<KimPermissionInfo> results = permissionService.getMatchingPermissions(requestedDetails, permissions);
        System.out.println( results );
        assertEquals( "Wrong number of matches", 1, results.size() );
    }
    
    private List<KimPermissionInfo> buildPermissionList( String[][] permissionData ) {
        List<KimPermissionInfo> permissions = new ArrayList<KimPermissionInfo>();
        for ( String[] perm : permissionData ) {
            KimPermissionInfo kpi = new KimPermissionInfo();
            kpi.setDetails( new AttributeSet() );
            kpi.getDetails().put(KimAttributes.NAMESPACE_CODE, perm[0] );
            kpi.getDetails().put(KimAttributes.COMPONENT_NAME, perm[1] );
            permissions.add(kpi);
        }
        return permissions;
    }
    
    private void assertPermInList( List<KimPermissionInfo> permissions, String namespaceCode, String componentName ) {
        boolean match = false;
        for ( KimPermissionInfo info : permissions ) {
            if ( StringUtils.equals( info.getDetails().get(KimAttributes.NAMESPACE_CODE), namespaceCode )
                    && StringUtils.equals( info.getDetails().get(KimAttributes.COMPONENT_NAME), componentName ) ) {
                match = true;
            }
        }
        assertTrue( namespaceCode + "/" + componentName + " not found", match );        
    }
}
