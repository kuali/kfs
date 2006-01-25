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
package org.kuali.module.kra.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.kra.document.BudgetDocument;
import org.kuali.module.kra.document.ResearchDocument;
import org.kuali.module.kra.service.BudgetService;
import org.kuali.module.kra.service.ResearchDocumentService;
import org.springframework.orm.ojb.PersistenceBrokerTemplate;

/**
 * This class...
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class ResearchDocumentServiceImpl extends PersistenceBrokerTemplate implements ResearchDocumentService {

    private BudgetService budgetService;
    
    /**
     * @see org.kuali.module.kra.service.ResearchDocumentService#prepareResearchDocumentForSave(org.kuali.module.kra.document.ResearchDocument)
     */
    public void prepareResearchDocumentForSave(ResearchDocument researchDocument) {
        if (researchDocument instanceof BudgetDocument) {
            budgetService.prepareBudgetForSave((BudgetDocument)researchDocument);
        }
        SpringServiceLocator.getOjbCollectionHelper().processCollections(this,researchDocument.getFinancialDocumentNumber(),researchDocument);
    }

    /**
     * @see org.kuali.core.util.OjbCollectionAware#getListOfCollectionsToProcess(java.lang.Object)
     */
    public List getListOfCollectionsToProcess(Object object) {
        List list=null;
        if (object instanceof BudgetDocument) {
            list=new ArrayList();
            list.add(((BudgetDocument)object).getBudget().getTasks());
            list.add(((BudgetDocument)object).getBudget().getPeriods());
            list.add(((BudgetDocument)object).getBudget().getNonpersonnelItems());
        }
        return list;
    }
    
    public void setBudgetService(BudgetService budgetService) {
        this.budgetService = budgetService;
    }
}
