/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.effort.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.UniversalUserService;
import org.kuali.core.util.spring.Logged;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.LaborLedgerEntry;
import org.kuali.kfs.context.Log4jConfigurer;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.effort.service.EffortCertificationCreateService;
import org.kuali.module.effort.service.EffortCertificationExtractService;
import org.kuali.module.gl.web.TestDataGenerator;
import org.kuali.module.labor.bo.LedgerBalance;
import org.kuali.module.labor.bo.LedgerEntry;
import org.kuali.module.labor.service.impl.LaborScrubberProcess;
import org.kuali.module.labor.util.LaborSpringContext;
import org.kuali.module.labor.util.TestDataPreparator;

public class EffortBatchRunner {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborScrubberProcess.class);

    private final Properties properties, message;
    private final String balanceFieldNames, entryFieldNames;
    private final String deliminator;

    private BusinessObjectService businessObjectService;

    public EffortBatchRunner() {
        String messageFileName = "test/src/org/kuali/module/effort/testdata/message.properties";
        String propertiesFileName = "test/src/org/kuali/module/effort/testdata/effortCertificationExtractServiceProformance.properties";

        TestDataGenerator generator = new TestDataGenerator(propertiesFileName, messageFileName);
        properties = generator.getProperties();
        message = generator.getMessage();

        deliminator = properties.getProperty("deliminator");

        balanceFieldNames = properties.getProperty("balanceFieldNames");
        entryFieldNames = properties.getProperty("entryFieldNames");

        Log4jConfigurer.configureLogging(false);

        LaborSpringContext.initializeApplicationContext();
        businessObjectService = LaborSpringContext.getBean(BusinessObjectService.class);
    }

    public void loadData() {
        this.doCleanup();

        int numberOfEmplid = Integer.valueOf(properties.getProperty("emplid.numOfEmplid"));
        for (int i = 1; i <= numberOfEmplid; i++) {
            String emplid = StringUtils.trim(properties.getProperty("emplid" + i));
            this.loadDataForEmployee(emplid, i);
        }
    }

    private void doCleanup() {
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, "2007");

        businessObjectService.deleteMatching(LedgerEntry.class, fieldValues);
        businessObjectService.deleteMatching(LedgerBalance.class, fieldValues);

    }

    private void loadDataForEmployee(String emplid, Integer sequenceNumber) {
        int numberOfEntries = Integer.valueOf(properties.getProperty("ledgerEntry.numOfEntries"));
        List<LedgerEntry> ledgerEntries = TestDataPreparator.buildTestDataList(LedgerEntry.class, properties, "ledgerEntry.seed", entryFieldNames, deliminator, numberOfEntries);
        for (LaborLedgerEntry entry : ledgerEntries) {
            entry.setEmplid(emplid);
            entry.setTransactionLedgerEntrySequenceNumber(sequenceNumber);
        }
        businessObjectService.save(ledgerEntries);

        int numberOfBalances = Integer.valueOf(properties.getProperty("ledgerBalance.numOfBalances"));
        List<LedgerBalance> ledgerBalances = TestDataPreparator.buildTestDataList(LedgerBalance.class, properties, "ledgerBalance.seed", balanceFieldNames, deliminator, numberOfBalances);
        for (LedgerBalance balance : ledgerBalances) {
            balance.setEmplid(emplid);
        }
        businessObjectService.save(ledgerBalances);
    }

    @Logged
    public static void main(String[] args) {
        EffortBatchRunner batchRunner = new EffortBatchRunner();

        try {
            if (StringUtils.equalsIgnoreCase("-load", args[0])) {
                System.out.println("Loading data into labor Entry and balance tables ...");
                batchRunner.loadData();
            }
            else if (StringUtils.equalsIgnoreCase("-extract", args[0])) {
                System.out.println("Extracting Effort Certifications ...");
                EffortCertificationExtractService effortCertificationExtractService = LaborSpringContext.getBean(EffortCertificationExtractService.class);
                effortCertificationExtractService.extract(2007, "B01");
            }
            else if (StringUtils.equalsIgnoreCase("-create", args[0])) {
                System.out.println("Creating Effort Certifications ...");
                EffortCertificationCreateService effortCertificationCreateService = LaborSpringContext.getBean(EffortCertificationCreateService.class);
                effortCertificationCreateService.create(2007, "B01");
            }
            else {
                throw new IllegalArgumentException("Wrong argument -- The argument only can be -load or -extract");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            System.exit(0);
        }
    }
}
