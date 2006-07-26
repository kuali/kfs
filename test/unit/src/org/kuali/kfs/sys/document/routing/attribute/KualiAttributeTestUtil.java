/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.workflow.attribute;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.apache.commons.lang.StringUtils;

import edu.iu.uis.eden.doctype.DocumentType;
import edu.iu.uis.eden.exception.InvalidXmlException;
import edu.iu.uis.eden.routeheader.DocumentContent;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.routeheader.StandardDocumentContent;
import edu.iu.uis.eden.routetemplate.RouteContext;

/**
 * This class contains various utility methods for doing tests on 
 * workflow attributes.
 * 
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class KualiAttributeTestUtil {

    public static final String BASE_PATH = "/java/projects/kuali_project/test/src/org/kuali/workflow/attribute/";

    public static final String TOF_FEMP_SUBCODE_ONELINER = "TransferOfFunds_FEMPSubcode_OneLiner.xml";
    public static final String PAYEE_MAINTENANCE_NEWDOC = "PayeeMaintenanceDocument_CreateNew.xml";
    
    /**
     * 
     * This method loads a document XML from a file in this directory, and loads it into a 
     * DocumentContent class, which is then returned.
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
        BufferedReader reader = new BufferedReader(new FileReader(KualiAttributeTestUtil.BASE_PATH + fileName));
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
