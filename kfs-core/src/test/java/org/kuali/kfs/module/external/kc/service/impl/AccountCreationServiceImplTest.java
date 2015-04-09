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
package org.kuali.kfs.module.external.kc.service.impl;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.integration.cg.dto.AccountCreationStatusDTO;
import org.kuali.kfs.integration.cg.dto.AccountParametersDTO;
import org.kuali.kfs.module.external.kc.KcConstants;
import org.kuali.kfs.module.external.kc.businessobject.AccountAutoCreateDefaults;
import org.kuali.kfs.module.external.kc.service.AccountCreationService;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.context.TestUtils;
import org.kuali.kfs.sys.fixture.UserNameFixture;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

@ConfigureContext(session = khuntley)
public class AccountCreationServiceImplTest extends KualiTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountCreationServiceImplTest.class);

    private AccountCreationService accountCreationService;
    private DateTimeService dateTimeService;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        dateTimeService = SpringContext.getBean(DateTimeService.class);
        accountCreationService = SpringContext.getBean(AccountCreationService.class);
        changeCurrentUser(UserNameFixture.khuntley);
     }

    @Override
    protected void changeCurrentUser(UserNameFixture sessionUser) throws Exception {
        GlobalVariables.setUserSession(new UserSession(sessionUser.toString()));
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testDummyTest() {
        // just here to keep jUnit from complaining
    }

    /**
     * This method will create a CgDocument for the document type ACCT. Successful if there are no error messages
     */
    // public void testCreateCGAccountMaintenanceDocument() {
    // List<String> errorMessages = new ArrayList();
    //
    // MaintenanceDocument maintenanceAccountDocument = (MaintenanceDocument)
    // accountCreationServiceImpl.createCGAccountMaintenanceDocument(errorMessages);
    // assertTrue(ObjectUtils.isNotNull(maintenanceAccountDocument));
    // }

    /**
     * This method will create AccountsParameters with test values...
     *
     * @return accountParameters
     */
    public AccountParametersDTO getAccountParameters() {

        AccountParametersDTO accountParameters = new AccountParametersDTO();
        accountParameters.setPrincipalId(GlobalVariables.getUserSession().getPrincipalId());
        accountParameters.setAccountName("Test Account Name");
        accountParameters.setAccountNumber("1031400");
        accountParameters.setCfdaNumber("123456");
        accountParameters.setExpenseGuidelineText("expense guidelines");
        accountParameters.setIncomeGuidelineText("income guidelines");
        accountParameters.setPurposeText("purpose text");
        accountParameters.setOffCampusIndicator(true);
        accountParameters.setEffectiveDate(dateTimeService.getCurrentDate());
        accountParameters.setExpirationDate(dateTimeService.getCurrentDate());
        accountParameters.setUnit("000001");

        return accountParameters;
    }

    /**
     * This method will construct AccountAutoCreateDefaults object with default values
     *
     * @return accountAutoCreateDefaults
     */
    public AccountAutoCreateDefaults getAccountAutoCreateDefaults() {
        AccountAutoCreateDefaults defaults = new AccountAutoCreateDefaults();

        defaults.setKcUnit("testUnit");

        return defaults;
    }

    /**
     * This method will test the creation of a CgDocument and try to route it based on system parameter value...
     */
    public void norun_testCreateRouteAutomaticCGAccountDocument() throws WorkflowException {
        AccountParametersDTO accountParametersDTO = this.getAccountParameters();
        // set the ACCOUNT_AUTO_CREATE_ROUTE as "save"
        TestUtils.setSystemParameter(Account.class, KcConstants.AccountCreationService.PARAMETER_KC_ACCOUNT_ADMIN_AUTO_CREATE_ACCOUNT_WORKFLOW_ACTION, KFSConstants.WORKFLOW_DOCUMENT_SAVE);

        AccountCreationStatusDTO accountCreationStatusDTO = accountCreationService.createAccount(accountParametersDTO);
        assertTrue(ObjectUtils.isNotNull(accountCreationStatusDTO.getErrorMessages()));

        String documentNumber = accountCreationStatusDTO.getDocumentNumber();
        assertTrue(StringUtils.isNotEmpty(documentNumber));

        // verify that the doc was saved
        MaintenanceDocument retrievedDoc;
        retrievedDoc = (MaintenanceDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(documentNumber);
        assertTrue("Document shouldn't be null", ObjectUtils.isNotNull(retrievedDoc));
        assertEquals("Document is in incorrect workflow status; should be SAVED", DocumentStatus.SAVED, retrievedDoc.getDocumentHeader().getWorkflowDocument().getStatus());
    }
}
