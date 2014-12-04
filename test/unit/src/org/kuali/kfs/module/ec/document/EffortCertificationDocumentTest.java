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
