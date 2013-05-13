/*
 * Copyright 2012 The Kuali Foundation
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
package org.kuali.kfs.sys.identity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.permission.Permission;

/**
 * Class to test implementation of the NamespacePermissionTypeService (allowing wildcards in the namespace and adding an additional attribute with wildcards).
 *
 */
@ConfigureContext
public class NamespaceWildcardAllowedAndOrStringWildcardAllowedPermissionTypeServiceFilePathTest
    extends KualiTestBase {

    private static final String PERMISSION_NAMESPACE_EXACT = "KFS-GL";
    private static final String PERMISSION_NAMESPACE_PARTIAL = "KFS*";
    private static final String PERMISSION_NAMESPACE_BLANK = null;

    private static final String PERMISSION_FILE_PATH_EXACT = "staging/gl/collectorXml/gl_collector1.xml";
    private static final String PERMISSION_FILE_PATH_PARTIAL1 = "staging/gl/collectorXml/*";
    private static final String PERMISSION_FILE_PATH_PARTIAL2 = "staging/*";
    private static final String PERMISSION_FILE_PATH_WILDCARD = "*";
    private static final String PERMISSION_FILE_PATH_BLANK = null;

    List<Permission> permissions =
        buildPermissionList( new String[][]
        {
            {"1", PERMISSION_NAMESPACE_EXACT,   PERMISSION_FILE_PATH_EXACT},     //KFS-GL:   staging/gl/collectorXml/gl_collector1.xml   -staging/gl/collectorXml/gl_collector1.xml
            {"2", PERMISSION_NAMESPACE_EXACT,   PERMISSION_FILE_PATH_PARTIAL1},  //KFS-GL:   staging/gl/collectorXml/*                   -staging/gl/collectorXml/*  EXCEPT gl_collector1.xml
            {"3", PERMISSION_NAMESPACE_EXACT,   PERMISSION_FILE_PATH_BLANK},     //KFS-GL:   null                                        -reports/gl/*, staging/gl/* EXCEPT staging/gl/collectorXml/*
            {"4", PERMISSION_NAMESPACE_PARTIAL, PERMISSION_FILE_PATH_EXACT},     //KFS*  :   staging/gl/collectorXml/gl_collector1.xml   -none
            {"5", PERMISSION_NAMESPACE_PARTIAL, PERMISSION_FILE_PATH_PARTIAL2},  //KFS*  :   staging/*                                   -staging/* EXCEPT staging/gl/* and staging/workflow/*
            {"6", PERMISSION_NAMESPACE_PARTIAL, PERMISSION_FILE_PATH_BLANK},     //KFS*  :   null                                        -reports/* EXCEPT reports/gl/*
            {"7", PERMISSION_NAMESPACE_BLANK,   PERMISSION_FILE_PATH_EXACT},     //null  :   staging/gl/collectorXml/gl_collector1.xml   -none
            {"8", PERMISSION_NAMESPACE_BLANK,   PERMISSION_FILE_PATH_WILDCARD},  //null  :   *                                           -staging/workflow/*

        } );

    private static final String REQUESTED_NAMESPACE_EXACT_MATCH = "KFS-GL";
    private static final String REQUESTED_NAMESPACE_PARTIAL_MATCH = "KFS-CAM";
    private static final String REQUESTED_NAMESPACE_BLANK_MATCH = null;

    private NamespaceWildcardAllowedAndOrStringWildcardAllowedPermissionTypeServiceImpl permissionTypeService;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        permissionTypeService = new NamespaceWildcardAllowedAndOrStringWildcardAllowedPermissionTypeServiceImpl();

        permissionTypeService.setWildcardMatchStringAttributeName(KfsKimAttributes.FILE_PATH);
    }

    /**
     * This test runs through all priority cases beginning with the least priority and adding a higher priority on each iteration.
     * The newest priority added should be returned in each case.
     *
     */
    public void testPerformMatches_moreExactMatch() {
        //Iterate over the list of permissions beginning with the broadest match down to the most specific match
        //With each iteration the results should return only one match and it should be the most specific match that was just added to the perms list
        String requestedFilePathExactMatch = "staging/gl/collectorXml/gl_collector1.xml";

        HashMap<String,String> requestedDetails = new HashMap<String,String>();
        requestedDetails.put(KimConstants.AttributeConstants.NAMESPACE_CODE, REQUESTED_NAMESPACE_EXACT_MATCH);
        requestedDetails.put(KfsKimAttributes.FILE_PATH, requestedFilePathExactMatch);

        List<Permission> perms = new ArrayList<Permission>();

        for( int i = permissions.size(); i > 0; i--) {
            perms.add(permissions.get(i-1));

            List<Permission> results = permissionTypeService.getMatchingPermissions(requestedDetails, perms);

            assertResults( perms, results, perms.size()-1 );
        }
    }

	public void testPerformMatches_namespace_exact_filepath_exact() {//PERMISSION 1
	    //Only the first permission should be returned even though the requested details match all the permissions
	    String requestedFilePathExactMatch = "staging/gl/collectorXml/gl_collector1.xml";

        HashMap<String,String> requestedDetails = new HashMap<String,String>();
        requestedDetails.put(KimConstants.AttributeConstants.NAMESPACE_CODE, REQUESTED_NAMESPACE_EXACT_MATCH);
        requestedDetails.put(KfsKimAttributes.FILE_PATH, requestedFilePathExactMatch);

        List<Permission> results = permissionTypeService.getMatchingPermissions(requestedDetails, permissions);

        assertResults( permissions, results, 0);
	}

	public void testPerformMatches_namespace_exact_filepath_partial() {//PERMISSION 2
	    //Partial match: Only the second permission should be returned even though the requested details match the 2nd, 3rd, 5th, 6th, and 8th permissions
        String requestedFilePathPartialMatch = "staging/gl/collectorXml/gl_collector2.xml";

        HashMap<String,String> requestedDetails = new HashMap<String,String>();
        requestedDetails.put(KimConstants.AttributeConstants.NAMESPACE_CODE, REQUESTED_NAMESPACE_EXACT_MATCH);
        requestedDetails.put(KfsKimAttributes.FILE_PATH, requestedFilePathPartialMatch);

        List<Permission> results = permissionTypeService.getMatchingPermissions(requestedDetails, permissions);

        assertResults( permissions, results, 1);
	}

    public void testPerformMatches_namespace_exact_filepath_blank() {//PERMISSION 3
        //Blank match: Only the third permission should be returned even though the requested details match the 3rd, 5th, 6th, and 8th permissions
        String requestedFilePathBlankMatchStaging = "staging/gl/enterpriseFeed/entp_test_file_001.data";

        HashMap<String,String> requestedDetails = new HashMap<String,String>();
        requestedDetails.put(KimConstants.AttributeConstants.NAMESPACE_CODE, REQUESTED_NAMESPACE_EXACT_MATCH);
        requestedDetails.put(KfsKimAttributes.FILE_PATH, requestedFilePathBlankMatchStaging);

        List<Permission> results = permissionTypeService.getMatchingPermissions(requestedDetails, permissions);

        assertResults( permissions, results, 2);

        //Blank match: Only the third permission should be returned even though the requested details match the 3rd, 6th, and 8th permissions
        String requestedFilePathBlankMatchReports = "reports/gl/placeholder.txt";

        requestedDetails.put(KfsKimAttributes.FILE_PATH, requestedFilePathBlankMatchReports);

        results = permissionTypeService.getMatchingPermissions(requestedDetails, permissions);

        assertResults( permissions, results, 2);
    }

    public void testPerformMatches_namespace_partial_filepath_partial() {//PERMISSION 5
        //Partial match: Only the fifth permission should be returned even though the requested details match the 5th, 6th, and 8th permissions
        String requestedFilePathPartialMatch = "staging/cm/barcode/placeholder.txt";

        HashMap<String,String> requestedDetails = new HashMap<String,String>();
        requestedDetails.put(KimConstants.AttributeConstants.NAMESPACE_CODE, REQUESTED_NAMESPACE_PARTIAL_MATCH);
        requestedDetails.put(KfsKimAttributes.FILE_PATH, requestedFilePathPartialMatch);

        List<Permission> results = permissionTypeService.getMatchingPermissions(requestedDetails, permissions);

        assertResults( permissions, results, 4);
    }

    public void testPerformMatches_namespace_partial_filepath_blank() {//PERMISSION 6
        //Blank match: Only the sixth permission should be returned even though the requested details match the 6th and 8th permissions
        String requestedFilePathBlankMatch = "reports/cm/placeholder.txt";

        HashMap<String,String> requestedDetails = new HashMap<String,String>();
        requestedDetails.put(KimConstants.AttributeConstants.NAMESPACE_CODE, REQUESTED_NAMESPACE_PARTIAL_MATCH);
        requestedDetails.put(KfsKimAttributes.FILE_PATH, requestedFilePathBlankMatch);

        List<Permission> results = permissionTypeService.getMatchingPermissions(requestedDetails, permissions);

        assertResults( permissions, results, 5);
    }

    public void testPerformMatches_namespace_blank_filepath_blank() {//PERMISSION 8
        //A null namespace in the request details should only match null namespaces in the permissions. They do not match all permissions.
        //A null namespace in the permissions, however, behaves like a wildcard and matches any namespace, including null.

        //Therefore these request details should only match the eighth permission and not the 3rd, 5th, or 6th permissions
        String requestedFilePathBlankMatch = "staging/workflow/loaded/placeholder.txt";

        HashMap<String,String> requestedDetails = new HashMap<String,String>();
        requestedDetails.put(KimConstants.AttributeConstants.NAMESPACE_CODE, REQUESTED_NAMESPACE_BLANK_MATCH);
        requestedDetails.put(KfsKimAttributes.FILE_PATH, requestedFilePathBlankMatch);

        List<Permission> results = permissionTypeService.getMatchingPermissions(requestedDetails, permissions);

        assertResults( permissions, results, 7);
    }

    protected void assertResults(List<Permission> permissionsList, List<Permission> resultsList, int permissionIndexExpected) {
        String resultStrPrefix = "returned PERMISSION ";
        String resultStr = (resultsList.size() == 1 ? resultStrPrefix + resultsList.get(0).getId() +": " : "");

        System.out.println ( resultStr + resultsList );

        assertEquals( "Wrong nunmber of matches", 1, resultsList.size());
        assertEquals( "Wrong permission was returned", permissionsList.get(permissionIndexExpected), resultsList.get(0));
    }

	protected List<Permission> buildPermissionList( String[][] permissionData ) {
	    List<Permission> permissions = new ArrayList<Permission>();

	    for ( String[] perm : permissionData ) {
	        Permission.Builder kpi = Permission.Builder.create("KFS-SYS", "Namesapce Wildcard And String Wildcard Test");
	        HashMap<String,String> details = new HashMap<String,String>();
            details.put(KimConstants.AttributeConstants.NAMESPACE_CODE, perm[1] );
            details.put(KfsKimAttributes.FILE_PATH, perm[2] );
	        kpi.setAttributes( details );
	        kpi.setId(perm[0]);
	        permissions.add(kpi.build());
	    }
	    return permissions;
    }
}
