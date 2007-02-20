/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.workflow.attribute;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.apache.commons.lang.StringUtils;

import edu.iu.uis.eden.doctype.DocumentType;
import edu.iu.uis.eden.engine.RouteContext;
import edu.iu.uis.eden.exception.InvalidXmlException;
import edu.iu.uis.eden.routeheader.DocumentContent;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.routeheader.StandardDocumentContent;

/**
 * This class contains various utility methods for doing tests on workflow attributes.
 * 
 * 
 */
public class KualiAttributeTestUtil {

    public static final String RELATIVE_PATH_IN_PROJECT = "test/src/org/kuali/workflow/attribute/";

    public static final String TOF_FEMP_SUBCODE_ONELINER = "TransferOfFunds_FEMPSubcode_OneLiner.xml";
    public static final String PAYEE_MAINTENANCE_NEWDOC = "PayeeMaintenanceDocument_CreateNew.xml";

    /**
     * 
     * This method loads a document XML from a file in this directory, and loads it into a DocumentContent class, which is then
     * returned.
     * 
     * @param fileName - file name (no path) of a file in the same directory as the test
     * @return Returns a DocumentContent instance (StandardDocumentContent) populated with the XML loaded from the file
     * @throws IOException
     * @throws InvalidXmlException
     */
    public static final DocumentContent getDocumentContentFromXmlFile(String fileName, String docTypeName) throws IOException, InvalidXmlException {
        if (StringUtils.isBlank(fileName)) {
            throw new IllegalArgumentException("The fileName parameter passed in was blank.");
        }
        BufferedReader reader = new BufferedReader(new FileReader(KualiAttributeTestUtil.RELATIVE_PATH_IN_PROJECT + fileName));
        RouteContext routeContext = RouteContext.getCurrentRouteContext();
        DocumentRouteHeaderValue docRouteHeaderValue = new DocumentRouteHeaderValue();
        DocumentType docType = new DocumentType();
        docType.setName(docTypeName);
        docRouteHeaderValue.setDocumentTypeId(docType.getDocumentTypeId());
        routeContext.setDocument(docRouteHeaderValue);
        return new StandardDocumentContent(readerToString(reader), routeContext);
    }

    private static String readerToString(Reader is) throws IOException {

        StringBuffer sb = new StringBuffer();
        char[] charBytes = new char[8192];
        int charsRead;

        // read a block, if it gets any chars, append them
        while ((charsRead = is.read(charBytes)) > 0) {
            sb.append(charBytes, 0, charsRead);
        }

        // only construct the string once, here
        return sb.toString();
    }

}
