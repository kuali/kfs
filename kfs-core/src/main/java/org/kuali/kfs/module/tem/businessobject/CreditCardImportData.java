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
import java.util.List;

public class CreditCardImportData {
    private String importBy;
    private List<CreditCardStagingData> creditCardData;

    public CreditCardImportData(){
        creditCardData = new ArrayList<CreditCardStagingData>();
    }

    public void setImportBy(String argImportBy){
        importBy = argImportBy;
    }
    public String getImportBy(){
        return importBy;
    }

    public void seetCreditCardData(List<CreditCardStagingData> argCreditCardData){
        creditCardData = argCreditCardData;
    }

    public List<CreditCardStagingData> getCreditCardData(){
        return creditCardData;
    }

    /**
     *
     * This method adds the {@link CreditCardStagingData} to the list.
     * @param creditCard
     */
    public void addCreditCard(CreditCardStagingData creditCard) {
        this.getCreditCardData().add(creditCard);
    }
}
