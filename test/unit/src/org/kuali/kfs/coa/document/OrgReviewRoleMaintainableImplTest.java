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

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.kuali.kfs.coa.identity.OrgReviewRole;
import org.kuali.kfs.coa.service.impl.OrgReviewRoleServiceImpl;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.TestUtils;
import org.kuali.kfs.sys.fixture.UserNameFixture;
import org.kuali.rice.core.api.membership.MemberType;
import org.kuali.rice.kew.api.action.ActionRequestPolicy;
import org.kuali.rice.kew.api.action.ActionRequestType;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kim.api.responsibility.ResponsibilityService;
import org.kuali.rice.kim.api.role.Role;
import org.kuali.rice.kim.api.role.RoleService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.api.type.KimTypeInfoService;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Section;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

@ConfigureContext(session=UserNameFixture.khuntley)
public class OrgReviewRoleMaintainableImplTest extends KualiTestBase {

    protected static final String ORG_REVIEW_DOC_TYPE = "ORR";

    protected OrgReviewRoleServiceImpl orgReviewRoleService;
    protected RoleService roleService;
    protected ResponsibilityService responsibilityService;
    protected KimTypeInfoService kimTypeInfoService;
    protected PersonService personService;
    protected Role orgHierRole;
    protected Role acctHierRole;

    protected OrgReviewRoleMaintainableImpl newMaint;
    protected OrgReviewRoleMaintainableImpl oldMaint;
    protected OrgReviewRole orgHierOrgReviewRole;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        orgReviewRoleService =  (OrgReviewRoleServiceImpl) TestUtils.getUnproxiedService( "orgReviewRoleService" );
        roleService = KimApiServiceLocator.getRoleService();
        responsibilityService = KimApiServiceLocator.getResponsibilityService();
        kimTypeInfoService = KimApiServiceLocator.getKimTypeInfoService();
        personService = KimApiServiceLocator.getPersonService();
        orgHierRole = roleService.getRoleByNamespaceCodeAndName(KFSConstants.SysKimApiConstants.ORGANIZATION_REVIEWER_ROLE_NAMESPACECODE, KFSConstants.SysKimApiConstants.ORGANIZATION_REVIEWER_ROLE_NAME);
        acctHierRole = roleService.getRoleByNamespaceCodeAndName(KFSConstants.SysKimApiConstants.ORGANIZATION_REVIEWER_ROLE_NAMESPACECODE, KFSConstants.SysKimApiConstants.ACCOUNTING_REVIEWER_ROLE_NAME);
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

        orgHierOrgReviewRole.setMethodToCall(KRADConstants.MAINTENANCE_EDIT_ACTION);
        newMaint.prepareBusinessObject(orgHierOrgReviewRole);

        assertTrue( "ORR should be in edit mode", orgHierOrgReviewRole.isEdit() );
        assertFalse( "ORR should not be in copy mode", orgHierOrgReviewRole.isCopy() );
        assertFalse( "ORR should not think it's a delegate", orgHierOrgReviewRole.isDelegate() );
        assertTrue( "ORR should be in edit role member mode", orgHierOrgReviewRole.isEditRoleMember() );
        assertFalse( "ORR should not be in new role member mode", orgHierOrgReviewRole.isCreateRoleMember() );

        assertEquals( "The marker ORMId value should be blank", "", orgHierOrgReviewRole.getORMId() );
    }

    public void testPrepareBusinessObject_OrgHier_RoleMember_Copy() {
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

    public void testGetSections_New() throws Exception {
        MaintenanceDocument document = createNewDocument();

        // populate the old side (should be blank)
        List<Section> oldSections = document.getNewMaintainableObject().getSections(document, null);

        // populate the new side
        List<Section> newSections = document.getNewMaintainableObject().getSections(document, document.getNewMaintainableObject());
        fail( "need some tests");
    }

    public void testGetSections_Edit() throws Exception {

        MaintenanceDocument document = createEditDocument( buildOrgHierData() );

        // populate the old side (should be blank)
        List<Section> oldSections = document.getNewMaintainableObject().getSections(document, null);

        // populate the new side
        List<Section> newSections = document.getNewMaintainableObject().getSections(document, document.getNewMaintainableObject());
        fail( "need some tests");
    }

    protected MaintenanceDocument createNewDocument() throws Exception {
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

    protected MaintenanceDocument createEditDocument( OrgReviewRole existingOrr ) throws Exception {
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

    protected MaintenanceDocument createCopyDocument() {
        fail("Not yet implemented");
        throw new UnsupportedOperationException();
    }

    protected MaintenanceDocument createCreateDelegationDocument() {
        fail("Not yet implemented");
        throw new UnsupportedOperationException();
    }


    public void testPopulateBusinessObject() {
        fail("Not yet implemented");
    }


    protected OrgReviewRole buildOrgHierData() {
        OrgReviewRole orr = new OrgReviewRole();

        orr.setRoleId( orgHierRole.getId() );
        orr.setKimTypeId( orgHierRole.getKimTypeId() );
        orr.setRoleName(orgHierRole.getName());
        orr.setNamespaceCode(orgHierRole.getNamespaceCode());

        orr.setChartOfAccountsCode("BL");
        orr.setOrganizationCode("PSY");
        //orr.setOverrideCode("");
        //orr.setFromAmount(KualiDecimal.ZERO);
        //orr.setToAmount(new KualiDecimal("5000.00"));
        orr.setFinancialSystemDocumentTypeCode("ACCT");

        orr.setActionTypeCode(ActionRequestType.APPROVE.getCode());
        orr.setPriorityNumber("");
        orr.setActionPolicyCode(ActionRequestPolicy.FIRST.getCode());
        orr.setForceAction(false);

        orr.setMemberTypeCode(MemberType.PRINCIPAL.getCode());
        Person p = UserNameFixture.khuntley.getPerson();
        orr.setPrincipalMemberPrincipalName(p.getPrincipalName());
        orr.setPrincipalMemberPrincipalId(p.getPrincipalId());

        //orr.setActiveFromDate(null);
        //orr.setActiveToDate(null);
        return orr;
    }

}
