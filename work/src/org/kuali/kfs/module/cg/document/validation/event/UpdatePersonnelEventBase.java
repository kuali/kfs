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
package org.kuali.module.kra.budget.rules.event;

import java.util.ArrayList;
import java.util.List;

import org.kuali.core.document.Document;
import org.kuali.core.rule.BusinessRule;
import org.kuali.core.rule.event.KualiDocumentEventBase;
import org.kuali.module.kra.budget.rules.budget.BudgetDocumentRule;

/**
 * Class capturing an update personnel event.
 * 
 * @author Kuali Research Administration Team (kualidev@oncourse.iu.edu)
 */
public class UpdatePersonnelEventBase extends KualiDocumentEventBase implements UpdatePersonnelEvent {

    private final List personnel;

    /**
     * Constructs a UpdateNonpersonnelEventBase.java.
     * 
     * @param description
     * @param errorPathPrefix
     * @param document
     */
    public UpdatePersonnelEventBase(String errorPathPrefix, Document document, List personnel) {
        super("adding periodLine to document " + getDocumentId(document), errorPathPrefix, document);

        // by doing a deep copy, we are ensuring that the business rule class can't update
        // the original object by reference
        this.personnel = new ArrayList(personnel);
    }

    /**
     * Constructs an AddAccountingLineEvent for the given document and accountingLine
     * 
     * @param document
     * @param accountingLine
     */
    public UpdatePersonnelEventBase(Document document, List personnel) {
        this("", document, personnel);
    }

    /**
     * @see org.kuali.module.kra.budget.rules.event.UpdateNonpersonnelEvent#getNewNonpersonnelItems()
     */
    public List getPersonnel() {
        // TODO Auto-generated method stub
        return this.personnel;
    }

    /**
     * @see org.kuali.core.rule.event.KualiDocumentEvent#getRuleInterfaceClass()
     */
    public Class getRuleInterfaceClass() {
        return BudgetDocumentRule.class;
    }

    /**
     * @see org.kuali.core.rule.event.KualiDocumentEvent#invokeRuleMethod(org.kuali.core.rule.BusinessRule)
     */
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((BudgetDocumentRule) rule).processUpdatePersonnelBusinessRules(getDocument());
    }

}