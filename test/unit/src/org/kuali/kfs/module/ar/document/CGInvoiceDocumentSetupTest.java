/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.document;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.coa.businessobject.OffsetDefinition;
import org.kuali.kfs.integration.cg.ContractsAndGrantsCGBAward;
import org.kuali.kfs.integration.cg.ContractsAndGrantsCGBAwardAccount;
import org.kuali.kfs.module.ar.batch.service.ContractsGrantsInvoiceCreateDocumentService;
import org.kuali.kfs.module.ar.businessobject.AccountsReceivableDocumentHeader;
import org.kuali.kfs.module.ar.businessobject.Customer;
import org.kuali.kfs.module.ar.businessobject.OrganizationAccountingDefault;
import org.kuali.kfs.module.ar.fixture.ARAwardFixture;
import org.kuali.kfs.module.ar.fixture.CustomerFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext; import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.kfs.sys.context.SpringContext; import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Basic setup to create Invoice Document
 */
public class CGInvoiceDocumentSetupTest extends KualiTestBase {

    BusinessObjectService boService;
    DocumentService documentService;
    ContractsGrantsInvoiceCreateDocumentService cginService;
    ContractsAndGrantsCGBAward award;
    ContractsGrantsInvoiceDocument document;

    @ConfigureContext(session = khuntley)
    protected void setUp() throws Exception {
        super.setUp();
        boService = SpringContext.getBean(BusinessObjectService.class);
        documentService = SpringContext.getBean(DocumentService.class);
        cginService = SpringContext.getBean(ContractsGrantsInvoiceCreateDocumentService.class);
        // this award is already present in the test drive's database

        award = ARAwardFixture.CG_AWARD_INV_ACCOUNT.createAward();

        document = (ContractsGrantsInvoiceDocument) documentService.getNewDocument("CGIN");
        Date start = SpringContext.getBean(DateTimeService.class).getCurrentSqlDate();
        Date stop = start;
        stop.setYear(start.getYear() + 1);
        if (ObjectUtils.isNotNull(award)) {


            // creating invoice document directly without using the service to get over validations.
            for (ContractsAndGrantsCGBAwardAccount awardAccount : award.getActiveAwardAccounts()) {
                if (!awardAccount.isFinalBilledIndicator()) {
                    List<ContractsAndGrantsCGBAwardAccount> list = new ArrayList<ContractsAndGrantsCGBAwardAccount>();
                    list.clear();
                    // only one account is added into the list to create cgin
                    list.add(awardAccount);
                    String coaCode = awardAccount.getAccount().getChartOfAccountsCode();
                    String orgCode = awardAccount.getAccount().getOrganizationCode();
                    Map<String, Object> criteria = new HashMap<String, Object>();
                    Integer currentYear = SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();
                    criteria.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, currentYear);
                    criteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, coaCode);
                    criteria.put(KFSPropertyConstants.ORGANIZATION_CODE, orgCode);
                    OrganizationAccountingDefault organizationAccountingDefault = (OrganizationAccountingDefault) boService.findByPrimaryKey(OrganizationAccountingDefault.class, criteria);
                    if (ObjectUtils.isNull(organizationAccountingDefault)) {
                        organizationAccountingDefault = new OrganizationAccountingDefault();
                        organizationAccountingDefault.setChartOfAccountsCode(coaCode);
                        organizationAccountingDefault.setOrganizationCode(orgCode);
                        organizationAccountingDefault.setUniversityFiscalYear(currentYear);
                        boService.save(organizationAccountingDefault);
                    }
                    criteria.clear();
                    criteria.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, currentYear);
                    criteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, coaCode);
                    criteria.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, "8000");
                    criteria.put(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, "AC");
                    criteria.put(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE, "CGIN");
                    OffsetDefinition offset = (OffsetDefinition) boService.findByPrimaryKey(OffsetDefinition.class, criteria);
                    if (ObjectUtils.isNull(offset)) {
                        offset = new OffsetDefinition();
                        offset.setChartOfAccountsCode(coaCode);
                        offset.setUniversityFiscalYear(currentYear);
                        offset.setFinancialObjectCode("8000");
                        offset.setFinancialDocumentTypeCode("CGIN");
                        offset.setFinancialBalanceTypeCode("AC");
                        boService.save(offset);
                    }
                    document = cginService.createCGInvoiceDocumentByAwardInfo(award, list, coaCode, orgCode);
                }
            }
        }
    }

    /**
     * Gets the document attribute.
     * 
     * @return Returns the document.
     */
    public ContractsGrantsInvoiceDocument getDocument() {
        try {
            if (ObjectUtils.isNull(document))
                this.setUp();
        }
        catch (Exception ex) {

            ex.printStackTrace();
        }
        return document;
    }

    /**
     * Sets the document attribute value.
     * 
     * @param document The document to set.
     */
    public void setDocument(ContractsGrantsInvoiceDocument document) {
        this.document = document;
    }


}
