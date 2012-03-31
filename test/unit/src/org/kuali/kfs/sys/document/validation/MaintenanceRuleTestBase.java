/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.sys.document.validation;

import groovy.util.logging.Log;

import java.util.List;

import org.apache.log4j.Logger;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.KualiMaintainableImpl;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.kns.rules.MaintenanceDocumentRule;
import org.kuali.rice.kns.service.DictionaryValidationService;
import org.kuali.rice.kns.service.MaintenanceDocumentDictionaryService;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.ErrorMessage;
import org.kuali.rice.krad.util.GlobalVariables;

@ConfigureContext
public abstract class MaintenanceRuleTestBase extends KualiTestBase {
    /**
     * This method creates a minimal MaintenanceDocument instance, and populates it with the provided businessObject for the
     * newMaintainable, and null for the oldMaintainable.
     * 
     * @param newSubAccount - populated subAccount for the newMaintainable
     * @return a populated MaintenanceDocument instance
     */
    protected MaintenanceDocument newMaintDoc(PersistableBusinessObject newBo) {
        return newMaintDoc(null, newBo);
    }

    /**
     * This method creates a minimal MaintenanceDocument instance, and populates it with the provided businessObjects for the
     * newMaintainable and oldMaintainable.
     * 
     * @param oldSubAccount - populated subAccount for the oldMaintainable
     * @param newSubAccount - populated subAccount for the newMaintainable
     * @return a populated MaintenanceDocument instance
     */
    protected MaintenanceDocument newMaintDoc(PersistableBusinessObject oldBo, PersistableBusinessObject newBo) {

        // disallow null value for newBo
        if (null == newBo) {
            throw new IllegalArgumentException("Invalid value (null) for newBo.  " + "This must always be a valid, populated BusinessObject instance.");
        }

        // get a new MaintenanceDocument from Spring
        MaintenanceDocument document = null;
        try {
            document = (MaintenanceDocument) SpringContext.getBean(DocumentService.class).getNewDocument(SpringContext.getBean(MaintenanceDocumentDictionaryService.class).getDocumentTypeName(newBo.getClass()));
        }
        catch (WorkflowException e) {
            throw new RuntimeException(e);
        }

        // add all the pieces
        document.getDocumentHeader().setDocumentDescription("test");
        if (null == oldBo) {
            document.setOldMaintainableObject(new KualiMaintainableImpl());
        }
        else {
            document.setOldMaintainableObject(new KualiMaintainableImpl(oldBo));
            document.getOldMaintainableObject().setBoClass(oldBo.getClass());
        }
        document.setNewMaintainableObject(new KualiMaintainableImpl(newBo));
        document.getNewMaintainableObject().setBoClass(newBo.getClass());
        return document;
    }

    /**
     * This method creates a new instance of the specified ruleClass, injects the businessObject(s). With this method, the
     * oldMaintainable will be set to null.
     * 
     * @param newBo - the populated businessObject for the newMaintainble
     * @param ruleClass - the class of rule to instantiate
     * @return a populated and ready-to-test rule, of the specified class
     */
    protected MaintenanceDocumentRule setupMaintDocRule(PersistableBusinessObject newBo, Class ruleClass) {
        MaintenanceDocument maintDoc = newMaintDoc(newBo);
        return setupMaintDocRule(maintDoc, ruleClass);
    }

    /**
     * This method first creates a new MaintenanceDocument with the BusinessObject(s) passed in. Note that the maintDoc is created
     * and destroyed internally, and is never returned. This method then creates a new instance of the specified ruleClass, injects
     * the businessObject(s).
     * 
     * @param oldBo - the populated businessObject for the oldMaintainable
     * @param newBo - the populated businessObject for the newMaintainable
     * @param ruleClass - the class of rule to instantiate
     * @return a populated and ready-to-test rule, of the specified class
     */
    protected MaintenanceDocumentRule setupMaintDocRule(PersistableBusinessObject oldBo, PersistableBusinessObject newBo, Class ruleClass) {

        MaintenanceDocument maintDoc = newMaintDoc(oldBo, newBo);

        return setupMaintDocRule(maintDoc, ruleClass);
    }

    /**
     * This method creates a new instance of the specified ruleClass, and then injects the maintenanceDocument and associated
     * business objects.
     * 
     * @param maintDoc - the populated MaintenanceDocument instance
     * @param ruleClass - the class of rule to instantiate
     * @return a populated and ready-to-test rule, of the specified class
     */
    protected MaintenanceDocumentRule setupMaintDocRule(MaintenanceDocument maintDoc, Class ruleClass) {

        MaintenanceDocumentRule rule;
        try {
            rule = (MaintenanceDocumentRule) ruleClass.newInstance();
        }
        catch (InstantiationException e) {
            throw new RuntimeException(e);
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        rule.setupBaseConvenienceObjects(maintDoc);

        // confirm that we're starting with no errors
        assertEquals(0, GlobalVariables.getMessageMap().getNumberOfPropertiesWithErrors());

        return rule;
    }

    protected void testDefaultExistenceCheck(PersistableBusinessObject bo, String fieldName, boolean shouldFail) {

        // init the error path
        GlobalVariables.getMessageMap().addToErrorPath("document.newMaintainableObject");

        // run the dataDictionary validation
        SpringContext.getBean(DictionaryValidationService.class).validateDefaultExistenceChecks(bo);

        // clear the error path
        GlobalVariables.getMessageMap().removeFromErrorPath("document.newMaintainableObject");

        // assert that the existence of the error is what is expected
        assertFieldErrorExistence(fieldName, KFSKeyConstants.ERROR_EXISTENCE, shouldFail);

    }

    /**
     * This method tests whether the expected number of errors exists in the errorMap. The assert will fail if this expected number
     * isnt what is returned.
     * 
     * @param expectedErrorCount - the number of errors expected
     */
    protected void assertErrorCount(int expectedErrorCount) {
        assertEquals(expectedErrorCount, GlobalVariables.getMessageMap().getErrorCount());
    }

    /**
     * This method tests whether the field error exists and returns the result of this test.
     * 
     * @param fieldName
     * @param errorKey
     * @return True if the error exists in the GlobalErrors, false if not.
     */
    protected boolean doesFieldErrorExist(String fieldName, String errorKey) {
        return GlobalVariables.getMessageMap().fieldHasMessage(MaintenanceDocumentRuleBase.MAINTAINABLE_ERROR_PREFIX + fieldName, errorKey);
    }

    /**
     * This method tests whether the existence check on the error matches what is expected by what is passed into expectedResult.
     * This method will fail the assertion if the presence of the error is not what is expected.
     * 
     * @param fieldName
     * @param errorKey
     * @param expectedResult - True if the error is expected, False if it is not.
     */
    protected void assertFieldErrorExistence(String fieldName, String errorKey, boolean expectedResult) {
        boolean result = doesFieldErrorExist(fieldName, errorKey);
        assertEquals("Existence check for Error on fieldName/errorKey: " + fieldName + "/" + errorKey + ". " + GlobalVariables.getMessageMap(), expectedResult, result);
    }

    /**
     * This method tests whether a given combination of fieldName and errorKey does NOT exist in the GlobalVariables.getMessageMap().
     * The assert will fail if the fieldName & errorKey combination DOES exist. NOTE that fieldName should NOT include the prefix
     * errorPath.
     * 
     * @param fieldName - fieldName as it would be provided when adding the error
     * @param errorKey - errorKey as it would be provided when adding the error
     */
    protected void assertFieldErrorDoesNotExist(String fieldName, String errorKey) {
        boolean result = doesFieldErrorExist(fieldName, errorKey);
        assertTrue("FieldName (" + fieldName + ") should NOT contain errorKey: " + errorKey, !result);
    }

    /**
     * This method tests whether a given combination of fieldName and errorKey exists in the GlobalVariables.getMessageMap(). The
     * assert will fail if the fieldName & errorKey combination doesnt exist. NOTE that fieldName should NOT include the prefix
     * errorPath.
     * 
     * @param fieldName - fieldName as it would be provided when adding the error
     * @param errorKey - errorKey as it would be provided when adding the error
     */
    protected void assertFieldErrorExists(String fieldName, String errorKey) {
        boolean result = GlobalVariables.getMessageMap().fieldHasMessage(MaintenanceDocumentRuleBase.MAINTAINABLE_ERROR_PREFIX + fieldName, errorKey);
        if ( !result ) {
            Logger.getLogger(getClass()).info("Messages in MessageMap: " + GlobalVariables.getMessageMap());
        }
        assertTrue("FieldName (" + fieldName + ") should contain errorKey: " + errorKey, result);
    }

    /**
     * This method tests whether a given errorKey exists on the document itself (ie, not tied to a specific field). The assert will
     * fail if the errorKey already exists on the document.
     * 
     * @param errorKey - errorKey as it would be provided when adding the error
     */
    protected void assertGlobalErrorExists(String errorKey) {
        boolean result = GlobalVariables.getMessageMap().fieldHasMessage(KFSConstants.DOCUMENT_ERRORS, errorKey);
        assertTrue("Document should contain errorKey: " + errorKey, result);
    }
}
