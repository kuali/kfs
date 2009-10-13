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
package org.kuali.kfs.module.ec.document;

import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ec.businessobject.EffortCertificationDetail;
import org.kuali.kfs.module.ec.testdata.EffortTestDataPropertyConstants;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.TestDataPreparator;
import org.kuali.kfs.sys.context.KualiTestBase;

@ConfigureContext
public class EffortCertificationDocumentTest extends KualiTestBase {

    private Properties properties, message;
    private String detailFieldNames, documentFieldNames;
    private String deliminator;

    /**
     * Constructs a EffortCertificationDetailBuildServiceTest.java.
     */
    public EffortCertificationDocumentTest() {
        super();
        String messageFileName = EffortTestDataPropertyConstants.TEST_DATA_PACKAGE_NAME + "/message.properties";
        String propertiesFileName = EffortTestDataPropertyConstants.TEST_DATA_PACKAGE_NAME + "/effortCertificationDocument.properties";

        properties = TestDataPreparator.loadPropertiesFromClassPath(propertiesFileName);
        message = TestDataPreparator.loadPropertiesFromClassPath(messageFileName);

        deliminator = properties.getProperty("deliminator");

        detailFieldNames = properties.getProperty("detailFieldNames");
        documentFieldNames = properties.getProperty("documentFieldNames");
    }

    public void testGetEffortCertificationDetailWithMaxPayrollAmount_MultipleResults() throws Exception {
        String testTarget = "getEffortCertificationDetailWithMaxPayrollAmount.multipleResults.";

        EffortCertificationDocument document = this.buildDocument(testTarget);
        List<EffortCertificationDetail> detailLineWithMaxPayrollAmount = document.getEffortCertificationDetailWithMaxPayrollAmount();

        int numberOfExpectedDetail = Integer.valueOf(StringUtils.trim(properties.getProperty(testTarget + "numOfExpectedDetail")));

        assertEquals(numberOfExpectedDetail, detailLineWithMaxPayrollAmount.size());
    }

    public void testGetEffortCertificationDetailWithMaxPayrollAmount_SingleResult() throws Exception {
        String testTarget = "getEffortCertificationDetailWithMaxPayrollAmount.singleResult.";

        EffortCertificationDocument document = this.buildDocument(testTarget);
        List<EffortCertificationDetail> detailLineWithMaxPayrollAmount = document.getEffortCertificationDetailWithMaxPayrollAmount();

        int numberOfExpectedDetail = Integer.valueOf(StringUtils.trim(properties.getProperty(testTarget + "numOfExpectedDetail")));

        assertEquals(numberOfExpectedDetail, detailLineWithMaxPayrollAmount.size());
    }

    @SuppressWarnings("unchecked")
    private EffortCertificationDocument buildDocument(String testTarget) {
        EffortCertificationDocument document = TestDataPreparator.buildTestDataObject(EffortCertificationDocument.class, properties, testTarget + "document", documentFieldNames, deliminator);
        List<EffortCertificationDetail> detailLines = this.buildDetailLine(testTarget);
        document.setEffortCertificationDetailLines(detailLines);
        return document;
    }

    private List<EffortCertificationDetail> buildDetailLine(String testTarget) {
        int numberOfDetail = Integer.valueOf(properties.getProperty(testTarget + "numOfDetail"));
        return TestDataPreparator.buildTestDataList(EffortCertificationDetail.class, properties, testTarget + "detail", detailFieldNames, deliminator, numberOfDetail);
    }
}
