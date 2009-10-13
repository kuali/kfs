/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.ec.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ec.businessobject.EffortCertificationDetail;
import org.kuali.kfs.module.ec.testdata.EffortTestDataPropertyConstants;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.ObjectUtil;
import org.kuali.kfs.sys.TestDataPreparator;
import org.kuali.kfs.sys.context.KualiTestBase;

@ConfigureContext
public class DetailLineGroupTest extends KualiTestBase {

    private final Properties properties, message;
    private final String detailFieldNames, consolidationFieldNames;
    private final String deliminator;

    /**
     * Constructs a EffortCertificationDocumentRuleUtilTest.java.
     */
    public DetailLineGroupTest() {
        super();
        String messageFileName = EffortTestDataPropertyConstants.TEST_DATA_PACKAGE_NAME + "/message.properties";
        String propertiesFileName = EffortTestDataPropertyConstants.TEST_DATA_PACKAGE_NAME + "/detailLineGroup.properties";

        properties = TestDataPreparator.loadPropertiesFromClassPath(propertiesFileName);
        message = TestDataPreparator.loadPropertiesFromClassPath(messageFileName);

        deliminator = properties.getProperty(EffortTestDataPropertyConstants.DELIMINATOR);
        detailFieldNames = properties.getProperty(EffortTestDataPropertyConstants.DETAIL_FIELD_NAMES);
        consolidationFieldNames = properties.getProperty(EffortTestDataPropertyConstants.CONSOLIDATION_FIELD_NAMES);
    }
    
    /**
     * test the constructor with a single parameter: EffortCertificationDetail
     */
    public void testConstructor_WithOneParameter() throws Exception {
        String testTarget = "constructor.withOneParameter.";

        EffortCertificationDetail detailLine = this.buildDetailLine(testTarget, EffortTestDataPropertyConstants.DETAIL);         
        DetailLineGroup detailLineGroupMap = new DetailLineGroup(detailLine);
        
        EffortCertificationDetail expectedSummaryLine = this.buildDetailLine(testTarget, EffortTestDataPropertyConstants.EXPECTED_SUMMARY_LINE);         
        EffortCertificationDetail expectedDelegateLine = this.buildDetailLine(testTarget, EffortTestDataPropertyConstants.EXPECTED_DELEGATE_LINE);         
        
        List<String> keyFields = ObjectUtil.split(detailFieldNames, deliminator);
        assertTrue(ObjectUtil.equals(expectedSummaryLine, detailLineGroupMap.getSummaryDetailLine(), keyFields));
        assertTrue(ObjectUtil.equals(expectedDelegateLine, detailLineGroupMap.getDelegateDetailLine(), keyFields));
    }
    
    /**
     * check if the effort on the delegating line can be updated aproperitely
     */
    public void testUpdateDelegateDetailLineEffort() throws Exception {
        String testTarget = "updateDelegateDetailLineEffort.";

        EffortCertificationDetail detailLine = this.buildDetailLine(testTarget, EffortTestDataPropertyConstants.DETAIL);         
        DetailLineGroup detailLineGroupMap = new DetailLineGroup(detailLine);
        
        String newEffortAsString = StringUtils.trim(properties.getProperty(testTarget + EffortTestDataPropertyConstants.NEW_EFFORT_PERCENT));        
        Integer newEffort = Integer.parseInt(newEffortAsString);
        detailLineGroupMap.getSummaryDetailLine().setEffortCertificationUpdatedOverallPercent(newEffort);
        
        detailLineGroupMap.updateDelegateDetailLineEffort();
        
        EffortCertificationDetail expectedSummaryLine = this.buildDetailLine(testTarget, EffortTestDataPropertyConstants.EXPECTED_SUMMARY_LINE);         
        EffortCertificationDetail expectedDelegateLine = this.buildDetailLine(testTarget, EffortTestDataPropertyConstants.EXPECTED_DELEGATE_LINE);         
        
        List<String> keyFields = ObjectUtil.split(detailFieldNames, deliminator);
        assertTrue(ObjectUtil.equals(expectedSummaryLine, detailLineGroupMap.getSummaryDetailLine(), keyFields));
        assertTrue(ObjectUtil.equals(expectedDelegateLine, detailLineGroupMap.getDelegateDetailLine(), keyFields));
    }
    
    /**
     * all detail lines are consolidated into a single group
     */
    public void testGroupDetailLines_SingleGroup() throws Exception {
        String testTarget = "groupDetailLines.singleGroup.";
        List<String> consolidationKeyFields = ObjectUtil.split(consolidationFieldNames, deliminator);

        int numberOfDetails = Integer.valueOf(StringUtils.trim(properties.getProperty(testTarget + EffortTestDataPropertyConstants.NUM_OF_DETAILS)));
        List<EffortCertificationDetail> detailLines = this.buildDetailLines(testTarget, EffortTestDataPropertyConstants.DETAIL, numberOfDetails);
        Map<String, DetailLineGroup> detailLineGroupMap = DetailLineGroup.groupDetailLines(detailLines);
        
        int numOfExpectedGroups = Integer.valueOf(StringUtils.trim(properties.getProperty(testTarget + EffortTestDataPropertyConstants.NUM_OF_EXPECTED_GROUPS)));
        assertTrue(numOfExpectedGroups == detailLineGroupMap.size());
        
        EffortCertificationDetail expectedSummaryLine = this.buildDetailLine(testTarget, EffortTestDataPropertyConstants.EXPECTED_SUMMARY_LINE);         
        EffortCertificationDetail expectedDelegateLine = this.buildDetailLine(testTarget, EffortTestDataPropertyConstants.EXPECTED_DELEGATE_LINE);         

        EffortCertificationDetail summaryLine = null;
        EffortCertificationDetail delegateLine = null;
        for(String key : detailLineGroupMap.keySet()) {
            summaryLine = detailLineGroupMap.get(key).getSummaryDetailLine();
            delegateLine = detailLineGroupMap.get(key).getDelegateDetailLine();
        }
        
        List<String> keyFields = ObjectUtil.split(detailFieldNames, deliminator);
        assertTrue(ObjectUtil.equals(expectedSummaryLine, summaryLine, keyFields));
        assertTrue(ObjectUtil.equals(expectedDelegateLine, delegateLine, keyFields));
    }
    
    /**
     * all detail lines are consolidated into multiple groups
     */
    public void testGroupDetailLines_MultipleGroups() throws Exception {
        String testTarget = "groupDetailLines.multipleGroups.";
        List<String> consolidationKeyFields = ObjectUtil.split(consolidationFieldNames, deliminator);

        int numberOfDetails = Integer.valueOf(StringUtils.trim(properties.getProperty(testTarget + EffortTestDataPropertyConstants.NUM_OF_DETAILS)));
        List<EffortCertificationDetail> detailLines = this.buildDetailLines(testTarget, EffortTestDataPropertyConstants.DETAIL, numberOfDetails);
        Map<String, DetailLineGroup> detailLineGroupMap = DetailLineGroup.groupDetailLines(detailLines);
        
        int numOfExpectedGroups = Integer.valueOf(StringUtils.trim(properties.getProperty(testTarget + EffortTestDataPropertyConstants.NUM_OF_EXPECTED_GROUPS)));
        assertTrue(numOfExpectedGroups == detailLineGroupMap.size());
        
        List<EffortCertificationDetail> expectedSummaryLines = this.buildDetailLines(testTarget, EffortTestDataPropertyConstants.EXPECTED_SUMMARY_LINE, numOfExpectedGroups);
        List<EffortCertificationDetail> expectedDelegateLines = this.buildDetailLines(testTarget, EffortTestDataPropertyConstants.EXPECTED_DELEGATE_LINE, numOfExpectedGroups);
        
        List<EffortCertificationDetail> summaryLines = new ArrayList<EffortCertificationDetail>();
        List<EffortCertificationDetail> delegateLines = new ArrayList<EffortCertificationDetail>();
        for(String key : detailLineGroupMap.keySet()) {
            summaryLines.add(detailLineGroupMap.get(key).getSummaryDetailLine());
            delegateLines.add(detailLineGroupMap.get(key).getDelegateDetailLine());
        }
        
        List<String> keyFields = ObjectUtil.split(detailFieldNames, deliminator);        
        assertTrue(TestDataPreparator.hasSameElements(expectedSummaryLines, summaryLines, keyFields));
        assertTrue(TestDataPreparator.hasSameElements(expectedDelegateLines, delegateLines, keyFields));
    }
    
    /**
     * build a list of detail lines for the specified test data
     */
    private List<EffortCertificationDetail> buildDetailLines(String testTarget, String propertyKeyPrefix, int numberOfDetails) {
        return TestDataPreparator.buildTestDataList(EffortCertificationDetail.class, properties, testTarget + propertyKeyPrefix, detailFieldNames, deliminator, numberOfDetails);
    }
    
    /**
     * build a detail line for the specified test data
     */
    private EffortCertificationDetail buildDetailLine(String testTarget, String propertyKeyPrefix) {
        return TestDataPreparator.buildTestDataObject(EffortCertificationDetail.class, properties, testTarget + propertyKeyPrefix, detailFieldNames, deliminator);
    }
}
