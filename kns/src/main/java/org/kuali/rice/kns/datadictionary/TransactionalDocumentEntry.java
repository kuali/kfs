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
import org.kuali.rice.kns.document.authorization.TransactionalDocumentAuthorizerBase;
import org.kuali.rice.kns.document.authorization.TransactionalDocumentPresentationControllerBase;
import org.kuali.rice.kns.rule.PromptBeforeValidation;
import org.kuali.rice.kns.web.derivedvaluesetter.DerivedValuesSetter;
import org.kuali.rice.krad.datadictionary.ReferenceDefinition;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Deprecated
public class TransactionalDocumentEntry extends org.kuali.rice.krad.datadictionary.TransactionalDocumentEntry implements KNSDocumentEntry {

    protected Class<? extends PromptBeforeValidation> promptBeforeValidationClass;
    protected Class<? extends DerivedValuesSetter> derivedValuesSetterClass;
    protected List<String> webScriptFiles = new ArrayList<String>(3);
    protected List<HeaderNavigation> headerNavigationList = new ArrayList<HeaderNavigation>();

    protected boolean sessionDocument = false;

    public TransactionalDocumentEntry() {
        super();

    documentAuthorizerClass = TransactionalDocumentAuthorizerBase.class;
    documentPresentationControllerClass = TransactionalDocumentPresentationControllerBase.class;
    }

    @Override
    public List<HeaderNavigation> getHeaderNavigationList() {
        return headerNavigationList;
    }

    @Override
    public List<String> getWebScriptFiles() {
        return webScriptFiles;
    }

    /**
     * @return Returns the preRulesCheckClass.
     */
    @Override
    public Class<? extends PromptBeforeValidation> getPromptBeforeValidationClass() {
        return promptBeforeValidationClass;
    }

    /**
     * The promptBeforeValidationClass element is the full class name of the java
     * class which determines whether the user should be asked any questions prior to running validation.
     *
     * @see KualiDocumentActionBase#promptBeforeValidation(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse, String)
     */
    @Override
    public void setPromptBeforeValidationClass(Class<? extends PromptBeforeValidation> preRulesCheckClass) {
        this.promptBeforeValidationClass = preRulesCheckClass;
    }

    /**
     * The webScriptFile element defines the name of javascript files
     * that are necessary for processing the document.  The specified
     * javascript files will be included in the generated html.
     */
    @Override
    public void setWebScriptFiles(List<String> webScriptFiles) {
        this.webScriptFiles = webScriptFiles;
    }

    /**
     * The headerNavigation element defines a set of additional
     * tabs which will appear on the document.
     */
    @Override
    public void setHeaderNavigationList(List<HeaderNavigation> headerNavigationList) {
        this.headerNavigationList = headerNavigationList;
    }

    @Override
    public boolean isSessionDocument() {
        return this.sessionDocument;
    }

    @Override
    public void setSessionDocument(boolean sessionDocument) {
        this.sessionDocument = sessionDocument;
    }

    /**
     * @return the derivedValuesSetter
     */
    @Override
    public Class<? extends DerivedValuesSetter> getDerivedValuesSetterClass() {
        return this.derivedValuesSetterClass;
    }

    /**
     * @param derivedValuesSetter the derivedValuesSetter to set
     */
    @Override
    public void setDerivedValuesSetterClass(Class<? extends DerivedValuesSetter> derivedValuesSetter) {
        this.derivedValuesSetterClass = derivedValuesSetter;
    }

    /**
     * Returns the document authorizer class for the document.  Only framework code should be calling this method.
     * Client devs should use {@link DocumentTypeService#getDocumentAuthorizer(org.kuali.rice.krad.document.Document)}
     * or
     * {@link DocumentTypeService#getDocumentAuthorizer(String)}
     *
     * @return a document authorizer class
     */
    @Override
    public Class<? extends DocumentAuthorizer> getDocumentAuthorizerClass() {
        return (Class<? extends DocumentAuthorizer>) super.getDocumentAuthorizerClass();
    }

    /**
     * Returns the document presentation controller class for the document.  Only framework code should be calling
     * this
     * method.
     * Client devs should use {@link DocumentTypeService#getDocumentPresentationController(org.kuali.rice.krad.document.Document)}
     * or
     * {@link DocumentTypeService#getDocumentPresentationController(String)}
     *
     * @return the documentPresentationControllerClass
     */
    @Override
    public Class<? extends DocumentPresentationController> getDocumentPresentationControllerClass() {
        return (Class<? extends DocumentPresentationController>) super.getDocumentPresentationControllerClass();
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.DocumentEntry#completeValidation()
     */
    @Override
    public void completeValidation() {
        super.completeValidation();
        for (ReferenceDefinition reference : defaultExistenceChecks) {
            reference.completeValidation(documentClass, null);
        }
    }

    @Override
    public String toString() {
        return "TransactionalDocumentEntry for documentType " + getDocumentTypeName();
    }
}
