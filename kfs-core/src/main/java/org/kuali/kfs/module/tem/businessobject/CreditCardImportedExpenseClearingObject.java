/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
