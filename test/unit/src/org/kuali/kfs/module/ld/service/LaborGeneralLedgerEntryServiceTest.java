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
package org.kuali.kfs.module.ld.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ld.businessobject.LaborGeneralLedgerEntry;
import org.kuali.kfs.module.ld.testdata.LaborTestDataPropertyConstants;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.ObjectUtil;
import org.kuali.kfs.sys.TestDataPreparator;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.service.BusinessObjectService;

@ConfigureContext
public class LaborGeneralLedgerEntryServiceTest extends KualiTestBase {

    private Properties properties;
    private String fieldNames;
    private String deliminator;
    private List<String> keyFieldList;

    private LaborGeneralLedgerEntryService laborGeneralLedgerEntryService;
    private BusinessObjectService businessObjectService;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        String messageFileName = LaborTestDataPropertyConstants.TEST_DATA_PACKAGE_NAME + "/message.properties";
        String propertiesFileName = LaborTestDataPropertyConstants.TEST_DATA_PACKAGE_NAME + "/laborGeneralLedgerEntryService.properties";

        properties = TestDataPreparator.loadPropertiesFromClassPath(propertiesFileName);
        
        fieldNames = properties.getProperty("fieldNames");
        deliminator = properties.getProperty("deliminator");
        keyFieldList = Arrays.asList(StringUtils.split(fieldNames, deliminator));

        laborGeneralLedgerEntryService = SpringContext.getBean(LaborGeneralLedgerEntryService.class);
        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
    }

    public void testSave() throws Exception {
        LaborGeneralLedgerEntry input1 = new LaborGeneralLedgerEntry();
        ObjectUtil.populateBusinessObject(input1, properties, "save.testData1", fieldNames, deliminator);

        LaborGeneralLedgerEntry expected1 = new LaborGeneralLedgerEntry();
        ObjectUtil.populateBusinessObject(expected1, properties, "save.expected1", fieldNames, deliminator);
        Map fieldValues = ObjectUtil.buildPropertyMap(expected1, keyFieldList);

        businessObjectService.deleteMatching(LaborGeneralLedgerEntry.class, fieldValues);
        assertEquals(0, businessObjectService.countMatching(LaborGeneralLedgerEntry.class, fieldValues));

        laborGeneralLedgerEntryService.save(input1);
        assertEquals(1, businessObjectService.countMatching(LaborGeneralLedgerEntry.class, fieldValues));

        LaborGeneralLedgerEntry input2 = new LaborGeneralLedgerEntry();
        ObjectUtil.populateBusinessObject(input2, properties, "save.testData2", fieldNames, deliminator);
        try {
            laborGeneralLedgerEntryService.save(input2);
            fail();
        }
        catch (Exception e) {
        }
    }

    public void testGetMaxSequenceNumber() throws Exception {
        LaborGeneralLedgerEntry input1 = new LaborGeneralLedgerEntry();
        ObjectUtil.populateBusinessObject(input1, properties, "maxSeqNumber.testData1", fieldNames, deliminator);

        Map fieldValues = ObjectUtil.buildPropertyMap(input1, keyFieldList);
        fieldValues.remove(KFSPropertyConstants.TRANSACTION_ENTRY_SEQUENCE_NUMBER);
        businessObjectService.deleteMatching(LaborGeneralLedgerEntry.class, fieldValues);

        Integer maxSeqNumber = laborGeneralLedgerEntryService.getMaxSequenceNumber(input1);
        assertEquals(Integer.valueOf(0), maxSeqNumber);

        LaborGeneralLedgerEntry LaborGeneralLedgerEntryExpected1 = new LaborGeneralLedgerEntry();
        String expectedSeqNumber1 = properties.getProperty("maxSeqNumber.expected1");

        laborGeneralLedgerEntryService.save(input1);
        maxSeqNumber = laborGeneralLedgerEntryService.getMaxSequenceNumber(input1);
        assertEquals(Integer.valueOf(expectedSeqNumber1), maxSeqNumber);

        LaborGeneralLedgerEntry input2 = new LaborGeneralLedgerEntry();
        ObjectUtil.populateBusinessObject(input2, properties, "maxSeqNumber.testData2", fieldNames, deliminator);

        LaborGeneralLedgerEntry expected2 = new LaborGeneralLedgerEntry();
        String expectedSeqNumber2 = properties.getProperty("maxSeqNumber.expected2");

        laborGeneralLedgerEntryService.save(input2);
        maxSeqNumber = laborGeneralLedgerEntryService.getMaxSequenceNumber(input1);
        assertEquals(Integer.valueOf(expectedSeqNumber2), maxSeqNumber);

        maxSeqNumber = laborGeneralLedgerEntryService.getMaxSequenceNumber(input2);
        assertEquals(Integer.valueOf(expectedSeqNumber2), maxSeqNumber);
    }
}
