/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.businessobject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.bo.GlobalBusinessObject;
import org.kuali.rice.krad.bo.GlobalBusinessObjectDetail;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.service.BusinessObjectService;

public class CreditCardImportedExpenseClearingObject extends PersistableBusinessObjectBase implements GlobalBusinessObject{
    private String documentNumber;



    List<CreditCardImportedExpenseClearingDetail> expenses = new ArrayList<CreditCardImportedExpenseClearingDetail>();

    /**
     * Gets the expenses attribute.
     *
     * @return Returns the expenses
     */

    public List<CreditCardImportedExpenseClearingDetail> getExpenses() {
        return expenses;
    }

    /**
     * Sets the expenses attribute.
     *
     * @param expenses The expenses to set.
     */
    public void setExpenses(List<CreditCardImportedExpenseClearingDetail> expenses) {
        this.expenses = expenses;
    }

    @Override
    public String getDocumentNumber() {
        return documentNumber;
    }

    @Override
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;

    }

    @Override
    public List<PersistableBusinessObject> generateGlobalChangesToPersist() {
        List<PersistableBusinessObject> historicalTravelExpenses = new ArrayList<PersistableBusinessObject>();

        for(CreditCardImportedExpenseClearingDetail detail : getExpenses()){
            Map<String, Object> fieldValues = new HashMap<String, Object>();
            fieldValues.put("creditCardStagingDataId", detail.getCreditCardStagingDataId());
            List<HistoricalTravelExpense> expenseList = (List<HistoricalTravelExpense>) SpringContext.getBean(BusinessObjectService.class).findMatching(HistoricalTravelExpense.class, fieldValues);

            expenseList.get(0).setReconciled(TemConstants.ReconciledCodes.CLEARED);
            historicalTravelExpenses.add(expenseList.get(0));
        }
        return historicalTravelExpenses;
    }

    @Override
    public List<PersistableBusinessObject> generateDeactivationsToPersist() {
        return null;
    }

    @Override
    public boolean isPersistable() {
        return true;
    }

    @Override
    public List<? extends GlobalBusinessObjectDetail> getAllDetailObjects() {
        return getExpenses();
    }

    @SuppressWarnings("rawtypes")
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        return null;
    }

    /**
     * @see org.kuali.rice.kns.bo.PersistableBusinessObjectBase#buildListOfDeletionAwareLists()
     */
    @Override
    public List<Collection<PersistableBusinessObject>> buildListOfDeletionAwareLists() {
        List<Collection<PersistableBusinessObject>> managedLists = super.buildListOfDeletionAwareLists();
        managedLists.add( new ArrayList<PersistableBusinessObject>( getExpenses() ) );
        return managedLists;
    }
}
