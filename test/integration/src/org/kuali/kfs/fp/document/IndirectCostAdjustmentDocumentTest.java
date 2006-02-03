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
package org.kuali.module.financial.document;

import java.util.ArrayList;
import java.util.List;

import org.kuali.core.document.TransactionalDocumentTestBase;
import org.kuali.test.parameters.DocumentParameter;
import org.kuali.test.parameters.TransactionalDocumentParameter;

/**
 * This class is used to test the IndirectCostAdjustmentDocument.
 * 
 * @author Kuali Financial Transactions Blue Team (kualidev@oncourse.iu.edu)
 */
public class IndirectCostAdjustmentDocumentTest extends TransactionalDocumentTestBase {
    public static final String COLLECTION_NAME = "IndirectCostAdjustmentDocumentTest.collection1";
    public static final String USER_NAME = "user1";
    public static final String DOCUMENT_PARAMETER = "indirectCostAdjustmentDocumentParameter1";
    public static final String SOURCE_LINE1 = "sourceLine2";
    public static final String TARGET_LINE1 = "targetLine2";
    public static final String SERIALIZED_LINE_PARAMTER = "serializedLine1";

    /**
     * Retrieve names of fixture collections test class is using.
     *
     * @return String[]
     */
    public String[] getFixtureCollectionNames() {
        return new String[] { COLLECTION_NAME };
    }

    /**
     * 
     * @see org.kuali.core.document.DocumentTestBase#getDocumentParameterFixture()
     */
    public DocumentParameter getDocumentParameterFixture() {
        return (TransactionalDocumentParameter) getFixtureEntryFromCollection(COLLECTION_NAME, DOCUMENT_PARAMETER).createObject();
    }

    /**
     * 
     * @see org.kuali.core.document.TransactionalDocumentTestBase#getTargetAccountingLineParametersFromFixtures()
     */
    public List getTargetAccountingLineParametersFromFixtures() {
        ArrayList list = new ArrayList();
        list.add(getFixtureEntryFromCollection(COLLECTION_NAME, TARGET_LINE1).createObject());
        return list;
    }

    /**
     * 
     * @see org.kuali.core.document.TransactionalDocumentTestBase#getSourceAccountingLineParametersFromFixtures()
     */
    public List getSourceAccountingLineParametersFromFixtures() {
        ArrayList list = new ArrayList();
        list.add(getFixtureEntryFromCollection(COLLECTION_NAME, SOURCE_LINE1).createObject());
        return list;
    }

    /**
     * 
     * @see org.kuali.core.document.TransactionalDocumentTestBase#getUserName()
     */
    public String getUserName() {
        return (String) getFixtureEntryFromCollection(COLLECTION_NAME, USER_NAME).createObject();
    }
    
    //  START TEST METHODS
    /**
     * Overrides the parent to do nothing since the ICA doesn't set the posting period in 
     * the record it stores.  This test doesn't apply to this type of document.
     */
    public final void testConvertIntoCopy_invalidYear() throws Exception {
        //do nothing to pass
    }
    
    /**
     * Overrides the parent to do nothing since the ICA doesn't set the posting period in 
     * the record it stores.  This test doesn't apply to this type of document.
     */
    public final void testConvertIntoErrorCorrection_invalidYear() throws Exception {
        //do nothing to pass
    }
}
