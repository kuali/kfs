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
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kim.bo.role.dto.KimPermissionInfo;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kns.service.impl.DocumentTypePermissionTypeServiceImpl;

@ConfigureContext
public class DocumentTypePermissionTypeServiceTest extends KualiTestBase {

    
    public void testPerformMatch_exact() {
        DocumentTypePermissionTypeServiceImpl srv = new DocumentTypePermissionTypeServiceImpl();
        
        AttributeSet requestedDetails = new AttributeSet();
        requestedDetails.put(KfsKimAttributes.DOCUMENT_TYPE_NAME, "RequisitionDocument" );
        
        AttributeSet permissionDetails = new AttributeSet();
        permissionDetails.put(KfsKimAttributes.DOCUMENT_TYPE_NAME, "RequisitionDocument" );
        
        assertTrue( "Exact match should have passed", srv.performMatch(requestedDetails, permissionDetails) );
    }

    public void testPerformMatch_parent() {
        DocumentTypePermissionTypeServiceImpl srv = new DocumentTypePermissionTypeServiceImpl();
        
        AttributeSet requestedDetails = new AttributeSet();
        requestedDetails.put(KfsKimAttributes.DOCUMENT_TYPE_NAME, "RequisitionDocument" );
        
        AttributeSet permissionDetails = new AttributeSet();
        permissionDetails.put(KfsKimAttributes.DOCUMENT_TYPE_NAME, "PurchasingTransactionalDocument" );
        
        assertTrue( "Parent match should have passed", srv.performMatch(requestedDetails, permissionDetails) );
    }

    public void testPerformMatch_top_parent() {
        DocumentTypePermissionTypeServiceImpl srv = new DocumentTypePermissionTypeServiceImpl();
        
        AttributeSet requestedDetails = new AttributeSet();
        requestedDetails.put(KfsKimAttributes.DOCUMENT_TYPE_NAME, "RequisitionDocument" );
        
        AttributeSet permissionDetails = new AttributeSet();
        permissionDetails.put(KfsKimAttributes.DOCUMENT_TYPE_NAME, "KualiDocument" );
        
        assertTrue( "Top Level Parent match should have passed", srv.performMatch(requestedDetails, permissionDetails) );
    }

    public void testPerformMatch_mismatch() {
        DocumentTypePermissionTypeServiceImpl srv = new DocumentTypePermissionTypeServiceImpl();
        
        AttributeSet requestedDetails = new AttributeSet();
        requestedDetails.put(KfsKimAttributes.DOCUMENT_TYPE_NAME, "RequisitionDocument" );
        
        AttributeSet permissionDetails = new AttributeSet();
        permissionDetails.put(KfsKimAttributes.DOCUMENT_TYPE_NAME, "FinancialProcessingTransactionalDocument" );
        
        assertFalse( "Parent match should have failed", srv.performMatch(requestedDetails, permissionDetails) );
    }

    public void testPerformMatch_star() {
        DocumentTypePermissionTypeServiceImpl srv = new DocumentTypePermissionTypeServiceImpl();
        
        AttributeSet requestedDetails = new AttributeSet();
        requestedDetails.put(KfsKimAttributes.DOCUMENT_TYPE_NAME, "RequisitionDocument" );
        
        AttributeSet permissionDetails = new AttributeSet();
        permissionDetails.put(KfsKimAttributes.DOCUMENT_TYPE_NAME, "*" );
        
        assertTrue( "Star (*) match should have passed", srv.performMatch(requestedDetails, permissionDetails) );
    }

    public void testPerformMatches_exact() {
        DocumentTypePermissionTypeServiceImpl srv = new DocumentTypePermissionTypeServiceImpl();
        
        AttributeSet requestedDetails = new AttributeSet();
        requestedDetails.put(KfsKimAttributes.DOCUMENT_TYPE_NAME, "RequisitionDocument" );
        
        List<KimPermissionInfo> permissions = new ArrayList<KimPermissionInfo>();
        KimPermissionInfo kpi = new KimPermissionInfo();
        kpi.setDetails( new AttributeSet() );
        kpi.getDetails().put(KfsKimAttributes.DOCUMENT_TYPE_NAME, "RequisitionDocument");
        permissions.add(kpi);
        
        List<KimPermissionInfo> results = srv.performPermissionMatches(requestedDetails, permissions);
        assertEquals( "Wrong number of matches", 1, results.size() );
    }

    public void testPerformMatches_list() {
        DocumentTypePermissionTypeServiceImpl srv = new DocumentTypePermissionTypeServiceImpl();
        
        AttributeSet requestedDetails = new AttributeSet();
        requestedDetails.put(KfsKimAttributes.DOCUMENT_TYPE_NAME, "RequisitionDocument" );
        
        List<KimPermissionInfo> permissions = new ArrayList<KimPermissionInfo>();
        KimPermissionInfo kpi = new KimPermissionInfo();
        kpi.setDetails( new AttributeSet() );
        kpi.getDetails().put(KfsKimAttributes.DOCUMENT_TYPE_NAME, "RequisitionDocument");
        permissions.add(kpi);
        kpi = new KimPermissionInfo();
        kpi.setDetails( new AttributeSet() );
        kpi.getDetails().put(KfsKimAttributes.DOCUMENT_TYPE_NAME, "PurchasingTransactionalDocument");
        permissions.add(kpi);
        kpi = new KimPermissionInfo();
        kpi.setDetails( new AttributeSet() );
        kpi.getDetails().put(KfsKimAttributes.DOCUMENT_TYPE_NAME, "FinancialProcessingTransactionalDocument");
        permissions.add(kpi);
        kpi = new KimPermissionInfo();
        kpi.setDetails( new AttributeSet() );
        kpi.getDetails().put(KfsKimAttributes.DOCUMENT_TYPE_NAME, "KualiDocument");
        permissions.add(kpi);
        kpi = new KimPermissionInfo();
        kpi.setDetails( new AttributeSet() );
        kpi.getDetails().put(KfsKimAttributes.DOCUMENT_TYPE_NAME, "*");
        permissions.add(kpi);
        
        List<KimPermissionInfo> results = srv.performPermissionMatches(requestedDetails, permissions);
        assertEquals( "Wrong number of matches", 4, results.size() );
        assertDocInList( results, "RequisitionDocument" );
        assertDocInList( results, "PurchasingTransactionalDocument" );
        assertDocInList( results, "KualiDocument" );
        assertDocInList( results, "*" );
    }
    
    private void assertDocInList( List<KimPermissionInfo> permissions, String docTypeName ) {
        boolean match = false;
        for ( KimPermissionInfo info : permissions ) {
            if ( info.getDetails().get(KfsKimAttributes.DOCUMENT_TYPE_NAME).equals(docTypeName) ) {
                match = true;
            }
        }
        assertTrue( docTypeName + " not found", match );        
    }
}
