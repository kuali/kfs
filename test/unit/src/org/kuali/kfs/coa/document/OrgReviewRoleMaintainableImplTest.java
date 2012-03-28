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

import org.kuali.kfs.coa.identity.OrgReviewRole;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.fixture.UserNameFixture;
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

    protected OrgReviewRoleMaintainableImpl newMaint;
    protected OrgReviewRoleMaintainableImpl oldMaint;
    protected OrgReviewRole orr;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        newMaint = new OrgReviewRoleMaintainableImpl();
        oldMaint = new OrgReviewRoleMaintainableImpl();
        orr = new OrgReviewRole();
    }

    public void testPrepareFieldsCommon() {
        Field f = new Field(OrgReviewRole.REVIEW_ROLES_INDICATOR_FIELD_NAME, "Doesn't Matter");
        newMaint.prepareFieldsCommon(f, true, false);
        assertTrue( "Field should have been read only after prepareFieldsCommon", f.isReadOnly() );
    }

    public void testPrepareBusinessObject_New() {
        fail("Not yet implemented");
    }

    public void testPrepareBusinessObject_Edit() {
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
        throw new UnsupportedOperationException();
    }

    protected MaintenanceDocument createCreateDelegationDocument() {
        throw new UnsupportedOperationException();
    }


    public void testPopulateBusinessObject() {
        fail("Not yet implemented");
    }

}
