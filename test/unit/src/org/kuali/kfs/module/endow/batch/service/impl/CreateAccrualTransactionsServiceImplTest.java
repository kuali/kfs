/*
 * Copyright 2008-2009 The Kuali Foundation
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
package org.kuali.kfs.module.endow.batch.service.impl;

import static org.kuali.kfs.sys.fixture.UserNameFixture.kfs;

import java.sql.Date;
import java.util.Collection;
import java.util.List;

import org.kuali.kfs.module.endow.businessobject.ClassCode;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionCode;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.businessobject.SecurityReportingGroup;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.kfs.module.endow.fixture.ClassCodeFixture;
import org.kuali.kfs.module.endow.fixture.EndowmentTransactionCodeFixture;
import org.kuali.kfs.module.endow.fixture.SecurityFixture;
import org.kuali.kfs.module.endow.fixture.SecurityReportingGroupFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.context.TestUtils;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DocumentService;


@ConfigureContext(session = kfs)
public class CreateAccrualTransactionsServiceImplTest extends KualiTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CreateAccrualTransactionsServiceImplTest.class);

    private CreateAccrualTransactionsServiceImpl createAccrualTransactionsServiceImpl;    
    private BusinessObjectService businessObjectService;
    private DocumentService documentService;
    private KEMService kemService;
    
    private ClassCode classCode;
    private Security security;
    private SecurityReportingGroup securityReportingGroup;
    private EndowmentTransactionCode endowmentTransactionCode;
    
    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        createAccrualTransactionsServiceImpl = (CreateAccrualTransactionsServiceImpl) TestUtils.getUnproxiedService("mockCreateAccrualTransactionsService");
        kemService = SpringContext.getBean(KEMService.class);
        
        securityReportingGroup = SecurityReportingGroupFixture.REPORTING_GROUP.createSecurityReportingGroup();
        endowmentTransactionCode = EndowmentTransactionCodeFixture.INCOME_TRANSACTION_CODE.createEndowmentTransactionCode();
        classCode = ClassCodeFixture.ACCRUAL_PROCESSING_CLASS_CODE.createClassCodeRecord();
        
        changeNextPayDateAllSecurityRecords(); //change the date
        //add a new one....so that GetAllSecuritiesWithNextPayDateEqualCurrentDate will find the record...
        security = SecurityFixture.ALTERNATIVE_INVEST_ACTIVE_SECURITY.createSecurityRecord();
        security.setIncomeNextPayDate(kemService.getCurrentDate());
        businessObjectService.save(security);
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    private void changeNextPayDateAllSecurityRecords() {
        Collection<Security> allSecurities = businessObjectService.findAll(Security.class);
        Date incomeNextPayDate = null;
        
        for (Security security : allSecurities) {
            security.setIncomeNextPayDate(incomeNextPayDate);
            businessObjectService.save(security);
        }
    }
    
    /**
     * test to check the method getAllSecuritiesWithNextPayDateEqualCurrentDate() returning just
     * one record that we inserted in the setup method
     */
    public void testGetAllSecuritiesWithNextPayDateEqualCurrentDate() {
        List<Security> result = createAccrualTransactionsServiceImpl.getAllSecuritiesWithNextPayDateEqualCurrentDate();
        assertTrue("There should be just one record in the Security table.", result.size() == 1);
    }
    
    /**
     * Sets the documentService attribute value.
     * @param documentService The documentService to set.
     */
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }
    /**
     * Gets the documentService attribute value.
     */
    protected DocumentService getDocumentService() {
        return documentService;
    }
    
    /**
     * Sets the kemService.
     * @param kemService
     */
    public void setKemService(KEMService kemService) {
        this.kemService = kemService;
    }
}
