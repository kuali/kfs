/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.coa.document;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.kuali.kfs.coa.identity.OrgReviewRole;
import org.kuali.kfs.coa.service.impl.OrgReviewRoleServiceImpl;
import org.kuali.kfs.coa.service.impl.OrgReviewRoleTestBase;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.fixture.UserNameFixture;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;
import org.kuali.rice.kns.web.ui.Section;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

@ConfigureContext(session=UserNameFixture.khuntley)
public class OrgReviewRoleMaintainableImplTest extends OrgReviewRoleTestBase {

    protected static final String ORG_REVIEW_DOC_TYPE = "ORR";

    protected OrgReviewRoleMaintainableImpl newMaint;
    protected OrgReviewRoleMaintainableImpl oldMaint;
    protected OrgReviewRole orgHierOrgReviewRole;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Logger.getLogger(OrgReviewRoleServiceImpl.class).setLevel(Level.DEBUG);
        Logger.getLogger(OrgReviewRoleMaintainableImpl.class).setLevel(Level.DEBUG);

        newMaint = new OrgReviewRoleMaintainableImpl();
        oldMaint = new OrgReviewRoleMaintainableImpl();
        orgHierOrgReviewRole = buildOrgHierData();
    }

    public void testPrepareFieldsCommon() {
        Field f = new Field(OrgReviewRole.REVIEW_ROLES_INDICATOR_FIELD_NAME, "Doesn't Matter");
        newMaint.prepareFieldsCommon(f, true, false);
        assertTrue( "Field should have been read only after prepareFieldsCommon", f.isReadOnly() );
    }

    public void testPrepareBusinessObject_OrgHier_RoleMember_New() {
        orgHierOrgReviewRole.setMethodToCall("");
        newMaint.prepareBusinessObject(orgHierOrgReviewRole);
        assertFalse( "ORR should not be in edit mode", orgHierOrgReviewRole.isEdit() );
        assertFalse( "ORR should not be in copy mode", orgHierOrgReviewRole.isCopy() );
        assertFalse( "ORR should not think it's a delegate", orgHierOrgReviewRole.isDelegate() );
//        assertFalse( "ORR should not be in edit role member mode", orgHierOrgReviewRole.isEditRoleMember() );
//        assertFalse( "ORR should not be in edit delegate member mode", orgHierOrgReviewRole.isEditDelegation() );
//        assertFalse( "ORR should not be in create delegate member mode", orgHierOrgReviewRole.isCreateDelegation() );
        assertTrue( "ORR should be in new role member mode", orgHierOrgReviewRole.isCreateRoleMember() );
    }

    public void testPrepareBusinessObject_OrgHier_RoleMember_Edit() {
        // since an edit, we need to save it first, since the document will
        // attempt to retrieve it from the database
        orgHierOrgReviewRole.setEdit(false);
        orgReviewRoleService.saveOrgReviewRoleToKim(orgHierOrgReviewRole);

        orgHierOrgReviewRole.setMethodToCall(KRADConstants.MAINTENANCE_EDIT_METHOD_TO_CALL);
        newMaint.prepareBusinessObject(orgHierOrgReviewRole);

        assertTrue( "ORR should be in edit mode", orgHierOrgReviewRole.isEdit() );
        assertFalse( "ORR should not be in copy mode", orgHierOrgReviewRole.isCopy() );
        assertFalse( "ORR should not think it's a delegate", orgHierOrgReviewRole.isDelegate() );
        assertTrue( "ORR should be in edit role member mode", orgHierOrgReviewRole.isEditRoleMember() );
        assertFalse( "ORR should not be in new role member mode", orgHierOrgReviewRole.isCreateRoleMember() );

        assertEquals( "The marker ORMId value should be blank", "", orgHierOrgReviewRole.getORMId() );
        assertFalse( "The roleMemberId should not have been blank", StringUtils.isBlank(orgHierOrgReviewRole.getRoleMemberId() ) );
    }

    public void testPrepareBusinessObject_OrgHier_RoleMember_Copy() {
        // since an copy, we need to save it first, since the document will
        // attempt to retrieve it from the database
        orgHierOrgReviewRole.setEdit(false);
        orgReviewRoleService.saveOrgReviewRoleToKim(orgHierOrgReviewRole);

        orgHierOrgReviewRole.setMethodToCall(KRADConstants.MAINTENANCE_COPY_METHOD_TO_CALL);
        newMaint.prepareBusinessObject(orgHierOrgReviewRole);

        assertFalse( "ORR should not be in edit mode", orgHierOrgReviewRole.isEdit() );
        assertTrue( "ORR should be in copy mode", orgHierOrgReviewRole.isCopy() );
        assertFalse( "ORR should not think it's a delegate", orgHierOrgReviewRole.isDelegate() );
        assertTrue( "ORR should be in copy role member mode", orgHierOrgReviewRole.isCopyRoleMember() );
        assertFalse( "ORR should not be in new role member mode", orgHierOrgReviewRole.isCreateRoleMember() );

        assertEquals( "The marker ORMId value should be blank", "", orgHierOrgReviewRole.getORMId() );
        assertEquals( "The roleMemberId should have been blank", "", orgHierOrgReviewRole.getRoleMemberId() );
    }

    public void testPrepareBusinessObject_OrgHier_Delegate_New() throws Exception {
        fail("Not yet implemented");
    }

    public void testPrepareBusinessObject_OrgHier_Delegate_Edit() {
        fail("Not yet implemented");
    }

    public void testPrepareBusinessObject_OrgHier_Delegate_Copy() {
        fail("Not yet implemented");
    }

    public void testPrepareBusinessObject_AcctHier_New() {
        fail("Not yet implemented");
    }

    public void testProcessAfterEdit() {
        fail("Not yet implemented");
    }

    public void testProcessAfterCopy() {
        fail("Not yet implemented");
    }

    protected static String[] ORG_HIER_READ_ONLY_PROP_NAMES = {
        //OrgReviewRole.REVIEW_ROLES_INDICATOR_FIELD_NAME,
        OrgReviewRole.FROM_AMOUNT_FIELD_NAME,
        OrgReviewRole.TO_AMOUNT_FIELD_NAME,
        OrgReviewRole.OVERRIDE_CODE_FIELD_NAME
    };

    public void testGetSections_OrgHier_RoleMember_New() throws Exception {
        MaintenanceDocument document = createNewDocument_RoleMember();

        // populate the old side (should be blank)
        List<Section> oldSections = document.getNewMaintainableObject().getSections(document, null);

        // populate the new side
        List<Section> newSections = document.getNewMaintainableObject().getSections(document, document.getNewMaintainableObject());
        for ( Section section : newSections ) {
            for ( Row row : section.getRows() ) {
                for ( Field field : row.getFields() ) {
                    if ( field.getPropertyName().equals(OrgReviewRole.DELEGATION_TYPE_CODE) ) {
                        assertEquals( "Delegation type Field should have been hidden: ", Field.HIDDEN, field.getFieldType() );
                    } else {
                        assertFalse( field.getPropertyName() + " should have been editable", field.isReadOnly() );
                    }
                    // TODO: check that all fields are editable
//                    for ( String readOnlyPropertyName : ORG_HIER_READ_ONLY_PROP_NAMES ) {
//                        if ( field.getPropertyName().equals(readOnlyPropertyName) ) {
//                            assertTrue( readOnlyPropertyName + " should have been read only", field.isReadOnly() );
//                        }
//                    }
                }
            }
        }
    }

    public void testGetSections_OrgHier_RoleMember_Edit() throws Exception {

        MaintenanceDocument document = createEditDocument_OrgHier_RoleMember( buildOrgHierData() );

        // populate the old side (should be blank)
        List<Section> oldSections = document.getNewMaintainableObject().getSections(document, null);

        // populate the new side
        List<Section> newSections = document.getNewMaintainableObject().getSections(document, document.getNewMaintainableObject());
        for ( Section section : newSections ) {
            for ( Row row : section.getRows() ) {
                for ( Field field : row.getFields() ) {
                    if ( field.getPropertyName().equals(OrgReviewRole.DELEGATION_TYPE_CODE) ) {
                        assertEquals( "Delegation type Field should have been hidden: ", Field.HIDDEN, field.getFieldType() );
                    }
                    for ( String readOnlyPropertyName : ORG_HIER_READ_ONLY_PROP_NAMES ) {
                        if ( field.getPropertyName().equals(readOnlyPropertyName) ) {
                            assertTrue( readOnlyPropertyName + " should have been read only", field.isReadOnly() );
                        }
                    }
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    protected MaintenanceDocument createNewDocument_RoleMember() throws Exception {
        MaintenanceDocument document = (MaintenanceDocument) KRADServiceLocatorWeb.getDocumentService().getNewDocument(ORG_REVIEW_DOC_TYPE);
        document.getOldMaintainableObject().prepareBusinessObject(new OrgReviewRole());
        PersistableBusinessObject oldBo = document.getOldMaintainableObject().getBusinessObject();
        document.getOldMaintainableObject().setBusinessObject(oldBo);
        document.getOldMaintainableObject().setBoClass(OrgReviewRole.class);
        document.getNewMaintainableObject().setBusinessObject((PersistableBusinessObject) ObjectUtils.deepCopy(oldBo));
        document.getNewMaintainableObject().setBoClass(OrgReviewRole.class);
        document.getNewMaintainableObject().setGenerateDefaultValues(ORG_REVIEW_DOC_TYPE);
        document.getNewMaintainableObject().processAfterNew( document, new HashMap<String, String[]>() );
        document.getNewMaintainableObject().setMaintenanceAction(KRADConstants.MAINTENANCE_NEW_ACTION);

        return document;
    }

    @SuppressWarnings("deprecation")
    protected MaintenanceDocument createEditDocument_OrgHier_RoleMember( OrgReviewRole existingOrr ) throws Exception {
        MaintenanceDocument document = (MaintenanceDocument) KRADServiceLocatorWeb.getDocumentService().getNewDocument(ORG_REVIEW_DOC_TYPE);
        document.getOldMaintainableObject().prepareBusinessObject((BusinessObject) ObjectUtils.deepCopy(existingOrr));
        PersistableBusinessObject oldBo = document.getOldMaintainableObject().getBusinessObject();
        document.getOldMaintainableObject().setBusinessObject(oldBo);
        document.getOldMaintainableObject().setBoClass(OrgReviewRole.class);
        document.getNewMaintainableObject().setBusinessObject((PersistableBusinessObject) ObjectUtils.deepCopy(oldBo));
        document.getNewMaintainableObject().setBoClass(OrgReviewRole.class);
        document.getNewMaintainableObject().processAfterEdit( document, new HashMap<String, String[]>() );
        document.getNewMaintainableObject().setMaintenanceAction(KRADConstants.MAINTENANCE_EDIT_ACTION);

        return document;
    }

    public void testDocumentSubmit_OrgHier_Delegate_New() throws Exception {
        // create an org hierarchy role member on which to create a delegation
        orgHierOrgReviewRole.setEdit(false);
        orgReviewRoleService.saveOrgReviewRoleToKim(orgHierOrgReviewRole);
        MaintenanceDocument doc = createNewDocument_OrgHier_Delegation( orgHierOrgReviewRole.getRoleMemberId() );

        ((OrgReviewRole)doc.getNewMaintainableObject().getBusinessObject()).setDelegationTypeCode("P");
        doc.getDocumentHeader().setDocumentDescription("Unit Test");

        SpringContext.getBean(BusinessObjectService.class).linkUserFields(doc);


        KRADServiceLocatorWeb.getDocumentService().routeDocument(doc, null, null);
    }

    @SuppressWarnings("deprecation")
    protected MaintenanceDocument createCopyDocument() {
        fail("Not yet implemented");
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("deprecation")
    protected MaintenanceDocument createNewDocument_OrgHier_Delegation( String roleMemberId ) throws Exception {
        OrgReviewRole existingOrr = new OrgReviewRole();
        orgReviewRoleService.populateOrgReviewRoleFromRoleMember(existingOrr, roleMemberId);
        MaintenanceDocument document = (MaintenanceDocument) KRADServiceLocatorWeb.getDocumentService().getNewDocument(ORG_REVIEW_DOC_TYPE);
        existingOrr.setODelMId("New");
        existingOrr.setORMId(roleMemberId);
        document.getOldMaintainableObject().prepareBusinessObject((BusinessObject) ObjectUtils.deepCopy(existingOrr));
        PersistableBusinessObject oldBo = document.getOldMaintainableObject().getBusinessObject();
        document.getOldMaintainableObject().setBusinessObject(oldBo);
        document.getOldMaintainableObject().setBoClass(OrgReviewRole.class);
        document.getNewMaintainableObject().setBusinessObject((PersistableBusinessObject) ObjectUtils.deepCopy(oldBo));
        document.getNewMaintainableObject().setBoClass(OrgReviewRole.class);
        document.getNewMaintainableObject().processAfterEdit( document, new HashMap<String, String[]>() );
        document.getNewMaintainableObject().setMaintenanceAction(KRADConstants.MAINTENANCE_EDIT_ACTION);

        return document;
    }


    public void testPopulateBusinessObject() {
        fail("Not yet implemented");
    }

}
