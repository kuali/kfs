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
package org.kuali.kfs.module.ar.businessobject.lookup;

import org.kuali.kfs.coa.businessobject.defaultvalue.CurrentUserChartValueFinder;
import org.kuali.kfs.coa.businessobject.defaultvalue.CurrentUserOrgValueFinder;
import org.kuali.kfs.module.ar.businessobject.InvoiceTemplate;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.fixture.UserNameFixture;
import org.kuali.kfs.sys.service.FinancialSystemUserService;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * This class is used to test Invoice template class
 */
@ConfigureContext(session = UserNameFixture.khuntley)
public class InvoiceTemplateTest extends KualiTestBase {

    public static final String TYPE_CODE = "1115";
    public static final String TYPE_DESCRIPTION = "Federal SF-1115";
    public static final boolean ACTIVE = true;
    private InvoiceTemplate invoiceTemplate;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        invoiceTemplate = new InvoiceTemplate();
        invoiceTemplate.setInvoiceTemplateCode(TYPE_CODE);
        invoiceTemplate.setInvoiceTemplateDescription(TYPE_DESCRIPTION);
        invoiceTemplate.setActive(ACTIVE);
    }

    public void testValidOrganization() {
        InvoiceTemplateLookupableHelperServiceImpl invoiceTemplateLookupable = new InvoiceTemplateLookupableHelperServiceImpl();
        invoiceTemplateLookupable.setFinancialSystemUserService(SpringContext.getBean(FinancialSystemUserService.class));
        final Person currentUser = GlobalVariables.getUserSession().getPerson();

        assertFalse(invoiceTemplateLookupable.isTemplateValidForUser(invoiceTemplate, currentUser));
        invoiceTemplate.setBillByChartOfAccountCode((new CurrentUserChartValueFinder()).getValue());
        invoiceTemplate.setBilledByOrganizationCode((new CurrentUserOrgValueFinder()).getValue());
        assertTrue(invoiceTemplateLookupable.isTemplateValidForUser(invoiceTemplate, currentUser));
    }

    public void testIsTemplateValidForContractsGrantsInvoiceDocument() {
        final ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService = SpringContext.getBean(ContractsGrantsInvoiceDocumentService.class);
        final Person currentUser = GlobalVariables.getUserSession().getPerson();
        String billByChartOfAccountCode = (new CurrentUserChartValueFinder()).getValue();
        String billedByOrganizationCode = (new CurrentUserOrgValueFinder()).getValue();
        ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument = new ContractsGrantsInvoiceDocument();
        contractsGrantsInvoiceDocument.setBillByChartOfAccountCode(billByChartOfAccountCode);
        contractsGrantsInvoiceDocument.setBilledByOrganizationCode(billedByOrganizationCode);

        assertFalse(contractsGrantsInvoiceDocumentService.isTemplateValidForContractsGrantsInvoiceDocument(invoiceTemplate, contractsGrantsInvoiceDocument));
        invoiceTemplate.setBillByChartOfAccountCode(billByChartOfAccountCode);
        invoiceTemplate.setBilledByOrganizationCode(billedByOrganizationCode);
        assertTrue(contractsGrantsInvoiceDocumentService.isTemplateValidForContractsGrantsInvoiceDocument(invoiceTemplate, contractsGrantsInvoiceDocument));
    }

}
