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

    public void testPerformMatches_exact() {
        DocumentTypePermissionTypeServiceImpl srv = new DocumentTypePermissionTypeServiceImpl();
        
        AttributeSet requestedDetails = new AttributeSet();
        requestedDetails.put(KfsKimAttributes.DOCUMENT_TYPE_NAME, "REQS" );
        
        List<KimPermissionInfo> permissions = 
            buildPermissionlist( new String[] {"REQS"} );
        
        List<KimPermissionInfo> results = srv.performPermissionMatches(requestedDetails, permissions);
        assertEquals( "Wrong number of matches", 1, results.size() );
        assertDocInList( results, "REQS" );
    }

    public void testPerformMatches_fulllist() {
        DocumentTypePermissionTypeServiceImpl srv = new DocumentTypePermissionTypeServiceImpl();
        
        AttributeSet requestedDetails = new AttributeSet();
        requestedDetails.put(KfsKimAttributes.DOCUMENT_TYPE_NAME, "REQS" );
        
        List<KimPermissionInfo> permissions = 
            buildPermissionlist( new String[] {"REQS","PurchasingTransactionalDocument","FinancialProcessingTransactionalDocument","KualiDocument","*"} );
        
        List<KimPermissionInfo> results = srv.performPermissionMatches(requestedDetails, permissions);
        for ( KimPermissionInfo info : results ) {
            System.out.println( info );
        }
        assertEquals( "Wrong number of matches", 1, results.size() );
        assertDocInList( results, "REQS" );
    }

    public void testPerformMatches_parentlist() {
        DocumentTypePermissionTypeServiceImpl srv = new DocumentTypePermissionTypeServiceImpl();
        
        AttributeSet requestedDetails = new AttributeSet();
        requestedDetails.put(KfsKimAttributes.DOCUMENT_TYPE_NAME, "REQS" );
        
        List<KimPermissionInfo> permissions = 
            buildPermissionlist( new String[] {"PurchasingTransactionalDocument","FinancialProcessingTransactionalDocument","KualiDocument","*"} );
        
        List<KimPermissionInfo> results = srv.performPermissionMatches(requestedDetails, permissions);
        for ( KimPermissionInfo info : results ) {
            System.out.println( info );
        }
        assertEquals( "Wrong number of matches", 1, results.size() );
        assertDocInList( results, "PurchasingTransactionalDocument" );
    }

    public void testPerformMatches_parentlist_2() {
        DocumentTypePermissionTypeServiceImpl srv = new DocumentTypePermissionTypeServiceImpl();
        
        AttributeSet requestedDetails = new AttributeSet();
        requestedDetails.put(KfsKimAttributes.DOCUMENT_TYPE_NAME, "REQS" );
        
        List<KimPermissionInfo> permissions = 
            buildPermissionlist( new String[] {"KualiDocument","*"} );
        
        List<KimPermissionInfo> results = srv.performPermissionMatches(requestedDetails, permissions);
        for ( KimPermissionInfo info : results ) {
            System.out.println( info );
        }
        assertEquals( "Wrong number of matches", 1, results.size() );
        assertDocInList( results, "KualiDocument" );
    }

    public void testPerformMatches_starlist() {
        DocumentTypePermissionTypeServiceImpl srv = new DocumentTypePermissionTypeServiceImpl();
        
        AttributeSet requestedDetails = new AttributeSet();
        requestedDetails.put(KfsKimAttributes.DOCUMENT_TYPE_NAME, "REQS" );
        
        List<KimPermissionInfo> permissions = 
            buildPermissionlist( new String[] {"FinancialProcessingTransactionalDocument","*"} );
        
        List<KimPermissionInfo> results = srv.performPermissionMatches(requestedDetails, permissions);
        for ( KimPermissionInfo info : results ) {
            System.out.println( info );
        }
        assertEquals( "Wrong number of matches", 1, results.size() );
        assertDocInList( results, "*" );
    }

    public void testPerformMatches_list_nomatch() {
        DocumentTypePermissionTypeServiceImpl srv = new DocumentTypePermissionTypeServiceImpl();
        
        AttributeSet requestedDetails = new AttributeSet();
        requestedDetails.put(KfsKimAttributes.DOCUMENT_TYPE_NAME, "REQS" );
        
        List<KimPermissionInfo> permissions = 
            buildPermissionlist( new String[] {"FinancialProcessingTransactionalDocument"} );
        
        List<KimPermissionInfo> results = srv.performPermissionMatches(requestedDetails, permissions);
        for ( KimPermissionInfo info : results ) {
            System.out.println( info );
        }
        assertEquals( "Wrong number of matches", 0, results.size() );
    }
    
    private List<KimPermissionInfo> buildPermissionlist( String[] docTypeNames ) {
        List<KimPermissionInfo> permissions = new ArrayList<KimPermissionInfo>();
        for ( String docTypeName : docTypeNames ) {
            KimPermissionInfo kpi = new KimPermissionInfo();
            kpi.setDetails( new AttributeSet() );
            kpi.getDetails().put(KfsKimAttributes.DOCUMENT_TYPE_NAME, docTypeName );
            permissions.add(kpi);
        }
        return permissions;
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
