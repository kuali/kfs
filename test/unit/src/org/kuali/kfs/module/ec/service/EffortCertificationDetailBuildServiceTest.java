/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.module.effort.service;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.effort.EffortConstants.SystemParameters;
import org.kuali.module.effort.bo.EffortCertificationDetailBuild;
import org.kuali.module.effort.bo.EffortCertificationReportDefinition;
import org.kuali.module.gl.web.TestDataGenerator;
import org.kuali.module.labor.bo.LaborOriginEntry;
import org.kuali.module.labor.bo.LedgerBalance;
import org.kuali.module.labor.util.ObjectUtil;
import org.kuali.module.labor.util.TestDataPreparator;
import org.kuali.test.ConfigureContext;

@ConfigureContext
public class EffortCertificationDetailBuildServiceTest extends KualiTestBase {

    private Properties properties, message;
    private String balanceFieldNames, detailFieldNames;
    private String deliminator;
    Integer postingYear;
    
    private Map<String, Object> fieldValues;
    private Map<String, List<String>> systemParameters;
    private EffortCertificationReportDefinition reportDefinition;

    private BusinessObjectService businessObjectService;
    private EffortCertificationDetailBuildService effortCertificationDetailBuildService;

    /**
     * Constructs a EffortCertificationDetailBuildServiceTest.java.
     */
    public EffortCertificationDetailBuildServiceTest() {
        super();
        String messageFileName = "test/src/org/kuali/module/effort/testdata/message.properties";
        String propertiesFileName = "test/src/org/kuali/module/effort/testdata/effortCertificationDetailBuildService.properties";

        TestDataGenerator generator = new TestDataGenerator(propertiesFileName, messageFileName);
        properties = generator.getProperties();
        message = generator.getMessage();
        
        deliminator = properties.getProperty("deliminator");
        
        balanceFieldNames = properties.getProperty("balanceFieldNames");
        detailFieldNames = properties.getProperty("detailFieldNames");
        
        postingYear = Integer.valueOf(properties.getProperty("postingYear"));
        
        List<String> expSubAccountType = ObjectUtil.split(properties.getProperty("systemParameter.expenseSubAccountTypeCode"), deliminator);
        List<String> csSubAccountType = ObjectUtil.split(properties.getProperty("systemParameter.costShareSubAccountTypeCode"), deliminator);
        
        systemParameters = new HashMap<String, List<String>>();       
        systemParameters.put(SystemParameters.EXPENSE_SUB_ACCOUNT_TYPE_CODE, expSubAccountType); 
        systemParameters.put(SystemParameters.COST_SHARE_SUB_ACCOUNT_TYPE_CODE, csSubAccountType);
        
        reportDefinition = new EffortCertificationReportDefinition();
        String reprtDefinitionFieldNames = properties.getProperty("reprtDefinitionFieldNames");
        ObjectUtil.populateBusinessObject(reportDefinition, properties, "reprtDefinitionFieldValues", reprtDefinitionFieldNames, deliminator);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        effortCertificationDetailBuildService = SpringContext.getBean(EffortCertificationDetailBuildService.class);

        LedgerBalance cleanup = new LedgerBalance();
        ObjectUtil.populateBusinessObject(cleanup, properties, "dataCleanup", balanceFieldNames, deliminator);
        fieldValues = ObjectUtil.buildPropertyMap(cleanup, Arrays.asList(StringUtils.split(balanceFieldNames, deliminator)));
        businessObjectService.deleteMatching(LedgerBalance.class, fieldValues);
    }

    /**
     * test if a build detail line can be generated from the specified ledger balance approperiately  
     */
    public void testGenerateDetailBuild() throws Exception {
        String testTarget = "generateDetailBuild.";
        List<String> keyFields = ObjectUtil.split(detailFieldNames, deliminator);   
        
        LedgerBalance ledgerBalance = TestDataPreparator.buildTestDataObject(LedgerBalance.class, properties, testTarget + "balance", balanceFieldNames, deliminator);
        businessObjectService.save(ledgerBalance);        
        ledgerBalance = (LedgerBalance)businessObjectService.retrieve(ledgerBalance);
        
        EffortCertificationDetailBuild detailBuild = effortCertificationDetailBuildService.generateDetailBuild(postingYear, ledgerBalance, reportDefinition, systemParameters);
        
        EffortCertificationDetailBuild expectedDetailBuild = TestDataPreparator.buildTestDataObject(EffortCertificationDetailBuild.class, properties, testTarget + "expected", detailFieldNames, deliminator);
        
        String errorMessage = message.getProperty("error.detailBuildService.unexpectedDetailLineGenerated");
        assertTrue(errorMessage, ObjectUtil.compareObject(detailBuild, expectedDetailBuild, keyFields));        
    }

}
