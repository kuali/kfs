/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.core.maintenance;

import java.util.Iterator;
import java.util.Map;

import org.kuali.Constants;
import org.kuali.KeyConstants;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.document.MaintenanceDocumentBase;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRule;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.core.util.ErrorMessage;
import org.kuali.core.util.GlobalVariables;

import static org.kuali.kfs.util.SpringServiceLocator.getDictionaryValidationService;
import static org.kuali.kfs.util.SpringServiceLocator.getDocumentService;

import org.kuali.core.util.TypedArrayList;
import org.kuali.test.KualiTestBase;
import org.kuali.test.WithTestSpringContext;

import edu.iu.uis.eden.exception.WorkflowException;

@WithTestSpringContext
public abstract class MaintenanceRuleTestBase extends KualiTestBase {
        /**
         * 
         * This method creates a minimal MaintenanceDocument instance, and populates it with the provided businessObject for the
         * newMaintainable, and null for the oldMaintainable.
         * 
         * @param newSubAccount - populated subAccount for the newMaintainable
         * @return a populated MaintenanceDocument instance
         * 
         */
        protected MaintenanceDocument newMaintDoc(PersistableBusinessObject newBo) {
            return newMaintDoc(null, newBo);
        }

        /**
         * 
         * This method creates a minimal MaintenanceDocument instance, and populates it with the provided businessObjects for the
         * newMaintainable and oldMaintainable.
         * 
         * @param oldSubAccount - populated subAccount for the oldMaintainable
         * @param newSubAccount - populated subAccount for the newMaintainable
         * @return a populated MaintenanceDocument instance
         * 
         */
        protected MaintenanceDocument newMaintDoc(PersistableBusinessObject oldBo, PersistableBusinessObject newBo) {

            // disallow null value for newBo
            if (null == newBo) {
                throw new IllegalArgumentException("Invalid value (null) for newBo.  " + "This must always be a valid, populated BusinessObject instance.");
            }

            // get a new MaintenanceDocument from Spring
            MaintenanceDocument document = null;
            try {
                document = (MaintenanceDocument) getDocumentService().getNewDocument(MaintenanceDocumentBase.class);
            }
            catch (WorkflowException e) {
                throw new RuntimeException(e);
            }

            // add all the pieces
            document.getDocumentHeader().setFinancialDocumentDescription("test");
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
         * 
         * This method creates a new instance of the specified ruleClass, injects the businessObject(s).
         * 
         * With this method, the oldMaintainable will be set to null.
         * 
         * @param newBo - the populated businessObject for the newMaintainble
         * @param ruleClass - the class of rule to instantiate
         * @return a populated and ready-to-test rule, of the specified class
         * 
         */
        protected MaintenanceDocumentRule setupMaintDocRule(PersistableBusinessObject newBo, Class ruleClass) {
            MaintenanceDocument maintDoc = newMaintDoc(newBo);
            return setupMaintDocRule(maintDoc, ruleClass);
        }

        /**
         * 
         * This method first creates a new MaintenanceDocument with the BusinessObject(s) passed in. Note that the maintDoc is created
         * and destroyed internally, and is never returned.
         * 
         * This method then creates a new instance of the specified ruleClass, injects the businessObject(s).
         * 
         * @param oldBo - the populated businessObject for the oldMaintainable
         * @param newBo - the populated businessObject for the newMaintainable
         * @param ruleClass - the class of rule to instantiate
         * @return a populated and ready-to-test rule, of the specified class
         * 
         */
        protected MaintenanceDocumentRule setupMaintDocRule(PersistableBusinessObject oldBo, PersistableBusinessObject newBo, Class ruleClass) {

            MaintenanceDocument maintDoc = newMaintDoc(oldBo, newBo);

            return setupMaintDocRule(maintDoc, ruleClass);
        }

        /**
         * 
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
            assertEquals(0, GlobalVariables.getErrorMap().size());

            return rule;
        }

        protected void testDefaultExistenceCheck(PersistableBusinessObject bo, String fieldName, boolean shouldFail) {

            // init the error path
            GlobalVariables.getErrorMap().addToErrorPath("document.newMaintainableObject");

            // run the dataDictionary validation
            getDictionaryValidationService().validateDefaultExistenceChecks(bo);

            // clear the error path
            GlobalVariables.getErrorMap().removeFromErrorPath("document.newMaintainableObject");

            // assert that the existence of the error is what is expected
            assertFieldErrorExistence(fieldName, KeyConstants.ERROR_EXISTENCE, shouldFail);

        }

        /**
         * 
         * This method tests whether the expected number of errors exists in the errorMap.
         * 
         * The assert will fail if this expected number isnt what is returned.
         * 
         * @param expectedErrorCount - the number of errors expected
         * 
         */
        protected void assertErrorCount(int expectedErrorCount) {
            assertEquals(expectedErrorCount, GlobalVariables.getErrorMap().getErrorCount());
        }

        /**
         * 
         * This method tests whether the field error exists and returns the result of this test.
         * 
         * @param fieldName
         * @param errorKey
         * @return True if the error exists in the GlobalErrors, false if not.
         * 
         */
        protected boolean doesFieldErrorExist(String fieldName, String errorKey) {
            return GlobalVariables.getErrorMap().fieldHasMessage(MaintenanceDocumentRuleBase.MAINTAINABLE_ERROR_PREFIX + fieldName, errorKey);
        }

        /**
         * 
         * This method tests whether the existence check on the error matches what is expected by what is passed into expectedResult.
         * 
         * This method will fail the assertion if the presence of the error is not what is expected.
         * 
         * @param fieldName
         * @param errorKey
         * @param expectedResult - True if the error is expected, False if it is not.
         * 
         */
        protected void assertFieldErrorExistence(String fieldName, String errorKey, boolean expectedResult) {
            boolean result = doesFieldErrorExist(fieldName, errorKey);
            assertEquals("Existence check for Error on fieldName/errorKey: " + fieldName + "/" + errorKey, expectedResult, result);
        }

        /**
         * 
         * This method tests whether a given combination of fieldName and errorKey does NOT exist in the GlobalVariables.getErrorMap().
         * 
         * The assert will fail if the fieldName & errorKey combination DOES exist.
         * 
         * NOTE that fieldName should NOT include the prefix errorPath.
         * 
         * @param fieldName - fieldName as it would be provided when adding the error
         * @param errorKey - errorKey as it would be provided when adding the error
         * 
         */
        protected void assertFieldErrorDoesNotExist(String fieldName, String errorKey) {
            boolean result = doesFieldErrorExist(fieldName, errorKey);
            assertTrue("FieldName (" + fieldName + ") should NOT contain errorKey: " + errorKey, result);
        }

        /**
         * 
         * This method tests whether a given combination of fieldName and errorKey exists in the GlobalVariables.getErrorMap().
         * 
         * The assert will fail if the fieldName & errorKey combination doesnt exist.
         * 
         * NOTE that fieldName should NOT include the prefix errorPath.
         * 
         * @param fieldName - fieldName as it would be provided when adding the error
         * @param errorKey - errorKey as it would be provided when adding the error
         * 
         */
        protected void assertFieldErrorExists(String fieldName, String errorKey) {
            boolean result = GlobalVariables.getErrorMap().fieldHasMessage(MaintenanceDocumentRuleBase.MAINTAINABLE_ERROR_PREFIX + fieldName, errorKey);
            assertTrue("FieldName (" + fieldName + ") should contain errorKey: " + errorKey, result);
        }

        /**
         * 
         * This method tests whether a given errorKey exists on the document itself (ie, not tied to a specific field).
         * 
         * The assert will fail if the errorKey already exists on the document.
         * 
         * @param errorKey - errorKey as it would be provided when adding the error
         * 
         */
        protected void assertGlobalErrorExists(String errorKey) {
            boolean result = GlobalVariables.getErrorMap().fieldHasMessage(Constants.DOCUMENT_ERRORS, errorKey);
            assertTrue("Document should contain errorKey: " + errorKey, result);
        }


        /**
         * 
         * This method is used during debugging to dump the contents of the error map, including the key names. It is not used by the
         * application in normal circumstances at all.
         * 
         */
        protected void showErrorMap() {

            if (GlobalVariables.getErrorMap().isEmpty()) {
                return;
            }

            for (Iterator i = GlobalVariables.getErrorMap().entrySet().iterator(); i.hasNext();) {
                Map.Entry e = (Map.Entry) i.next();

                TypedArrayList errorList = (TypedArrayList) e.getValue();
                for (Iterator j = errorList.iterator(); j.hasNext();) {
                    ErrorMessage em = (ErrorMessage) j.next();

                    if (em.getMessageParameters() == null) {
                        System.err.println(e.getKey().toString() + " = " + em.getErrorKey());
                    }
                    else {
                        StringBuffer messageParams = new StringBuffer();
                        String delim = "";
                        for (int k = 0; k < em.getMessageParameters().length; k++) {
                            messageParams.append(delim + "'" + em.getMessageParameters()[k] + "'");
                            if ("".equals(delim)) {
                                delim = ", ";
                            }
                        }
                        System.err.println(e.getKey().toString() + " = " + em.getErrorKey() + " : " + messageParams.toString());
                    }
                }
            }

        }
    }

