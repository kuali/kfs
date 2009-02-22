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

import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.role.dto.KimPermissionInfo;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.support.impl.NamespacePermissionTypeServiceImpl;

@ConfigureContext
public class NamespacePermissionTypeServiceTest extends KualiTestBase {

    NamespacePermissionTypeServiceImpl permissionService = new NamespacePermissionTypeServiceImpl();
    
    
    public void testPerformMatches_exact() {
        AttributeSet requestedDetails = new AttributeSet();
        requestedDetails.put(KimAttributes.NAMESPACE_CODE, "KR-NS" );
        
        List<KimPermissionInfo> permissions = 
            buildPermissionlist( new String[] {"KR-NS"} );
        
        List<KimPermissionInfo> results = permissionService.getMatchingPermissions(requestedDetails, permissions);
        System.out.println( results );
        assertEquals( "Wrong number of matches", 1, results.size() );
        assertNamespaceInList( results, "KR-NS" );
    }
    
    public void testPerformMatches_empty_list() {
        AttributeSet requestedDetails = new AttributeSet();
        requestedDetails.put(KimAttributes.NAMESPACE_CODE, "KR-NS" );
        
        List<KimPermissionInfo> permissions = 
            buildPermissionlist( new String[] {} );
        
        List<KimPermissionInfo> results = permissionService.getMatchingPermissions(requestedDetails, permissions);
        System.out.println( results );
        assertEquals( "Wrong number of matches", 0, results.size() );
    }

    public void testPerformMatches_blank_in_list() {
        AttributeSet requestedDetails = new AttributeSet();
        requestedDetails.put(KimAttributes.NAMESPACE_CODE, "KR-NS" );
        
        List<KimPermissionInfo> permissions = 
            buildPermissionlist( new String[] {""} );
        
        List<KimPermissionInfo> results = permissionService.getMatchingPermissions(requestedDetails, permissions);
        System.out.println( results );
        assertEquals( "Wrong number of matches", 0, results.size() );
    }
    
    public void testPerformMatches_wild_only() {
        AttributeSet requestedDetails = new AttributeSet();
        requestedDetails.put(KimAttributes.NAMESPACE_CODE, "KR-NS" );
        
        List<KimPermissionInfo> permissions = 
            buildPermissionlist( new String[] {"KR-*"} );
        
        List<KimPermissionInfo> results = permissionService.getMatchingPermissions(requestedDetails, permissions);
        System.out.println( results );
        assertEquals( "Wrong number of matches", 1, results.size() );
        assertNamespaceInList( results, "KR-*" );
    }
    
    public void testPerformMatches_wild_and_exact() {
        AttributeSet requestedDetails = new AttributeSet();
        requestedDetails.put(KimAttributes.NAMESPACE_CODE, "KR-NS" );
        
        List<KimPermissionInfo> permissions = 
            buildPermissionlist( new String[] {"KR-*","KR-NS"} );
        
        List<KimPermissionInfo> results = permissionService.getMatchingPermissions(requestedDetails, permissions);
        System.out.println( results );
        assertEquals( "Wrong number of matches", 1, results.size() );
        assertNamespaceInList( results, "KR-NS" );
    }
    
    private List<KimPermissionInfo> buildPermissionlist( String[] namespaceCodes ) {
        List<KimPermissionInfo> permissions = new ArrayList<KimPermissionInfo>();
        for ( String namespaceCode : namespaceCodes ) {
            KimPermissionInfo kpi = new KimPermissionInfo();
            kpi.setDetails( new AttributeSet() );
            kpi.getDetails().put(KimAttributes.NAMESPACE_CODE, namespaceCode );
            permissions.add(kpi);
        }
        return permissions;
    }
    
    private void assertNamespaceInList( List<KimPermissionInfo> permissions, String namespaceCode ) {
        boolean match = false;
        for ( KimPermissionInfo info : permissions ) {
            if ( info.getDetails().get(KimAttributes.NAMESPACE_CODE).equals(namespaceCode) ) {
                match = true;
            }
        }
        assertTrue( namespaceCode + " not found", match );        
    }
}
