/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.kns.datadictionary;

import org.kuali.rice.kns.document.authorization.DocumentAuthorizer;
import org.kuali.rice.kns.document.authorization.DocumentPresentationController;
import org.kuali.rice.kns.rule.PromptBeforeValidation;
import org.kuali.rice.kns.web.derivedvaluesetter.DerivedValuesSetter;
import org.kuali.rice.krad.datadictionary.DataDictionaryEntry;
import org.kuali.rice.krad.datadictionary.ReferenceDefinition;
import org.kuali.rice.krad.datadictionary.WorkflowAttributes;
import org.kuali.rice.krad.datadictionary.WorkflowProperties;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.keyvalues.KeyValuesFinder;
import org.kuali.rice.krad.rules.rule.BusinessRule;
import org.springframework.beans.factory.InitializingBean;

import java.io.Serializable;
import java.util.List;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Deprecated
public interface KNSDocumentEntry extends DataDictionaryEntry, Serializable, InitializingBean {

    public Class<? extends Document> getDocumentClass();

    public void setDocumentClass(Class<? extends Document> documentClass);

    public Class<? extends Document> getBaseDocumentClass();

    public void setBaseDocumentClass(Class<? extends Document> baseDocumentClass);

    public Class<? extends BusinessRule> getBusinessRulesClass();

    public void setBusinessRulesClass(Class<? extends BusinessRule> businessRulesClass);

    public String getDocumentTypeName();

    public void setDocumentTypeName(String documentTypeName);

    public boolean getDisplayTopicFieldInNotes();

    public void setDisplayTopicFieldInNotes(boolean displayTopicFieldInNotes);

    public boolean getUsePessimisticLocking();

    public void setUsePessimisticLocking(boolean usePessimisticLocking);

    public boolean getUseWorkflowPessimisticLocking();

    public void setUseWorkflowPessimisticLocking(boolean useWorkflowPessimisticLocking);

    public Class<? extends KeyValuesFinder> getAttachmentTypesValuesFinderClass();

    public void setAttachmentTypesValuesFinderClass(Class<? extends KeyValuesFinder> attachmentTypesValuesFinderClass);

    public boolean getAllowsCopy();

    public void setAllowsCopy(boolean allowsCopy);

    public boolean getAllowsNoteAttachments();

    public void setAllowsNoteAttachments(boolean allowsNoteAttachments);

    public boolean getAllowsNoteFYI();

    public void setAllowsNoteFYI(boolean allowsNoteFYI);

    public WorkflowProperties getWorkflowProperties();

    public void setWorkflowProperties(WorkflowProperties workflowProperties);

    public WorkflowAttributes getWorkflowAttributes();

    public void setWorkflowAttributes(WorkflowAttributes workflowAttributes);

    public List<ReferenceDefinition> getDefaultExistenceChecks();

    public void setDefaultExistenceChecks(List<ReferenceDefinition> defaultExistenceChecks);

    public boolean isEncryptDocumentDataInPersistentSessionStorage();

    public void setEncryptDocumentDataInPersistentSessionStorage(
                boolean encryptDocumentDataInPersistentSessionStorage);

    List<HeaderNavigation> getHeaderNavigationList();

    List<String> getWebScriptFiles();

    Class<? extends PromptBeforeValidation> getPromptBeforeValidationClass();

    void setPromptBeforeValidationClass(Class<? extends PromptBeforeValidation> preRulesCheckClass);

    void setWebScriptFiles(List<String> webScriptFiles);

    void setHeaderNavigationList(List<HeaderNavigation> headerNavigationList);

    boolean isSessionDocument();

    void setSessionDocument(boolean sessionDocument);

    Class<? extends DerivedValuesSetter> getDerivedValuesSetterClass();

    void setDerivedValuesSetterClass(Class<? extends DerivedValuesSetter> derivedValuesSetter);

    public Class<? extends DocumentAuthorizer> getDocumentAuthorizerClass();

    public Class<? extends DocumentPresentationController> getDocumentPresentationControllerClass();


}
