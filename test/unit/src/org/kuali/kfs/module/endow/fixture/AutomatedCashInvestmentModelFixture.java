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
package org.kuali.kfs.module.endow.fixture;

import java.math.BigDecimal;
import java.sql.Date;

import org.kuali.kfs.module.endow.businessobject.AutomatedCashInvestmentModel;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.service.BusinessObjectService;

public enum AutomatedCashInvestmentModelFixture {

    ACI_MODEL_DATA(
            new Integer(10),"ACI Test","P",
            "99PLTF021",BigDecimal.ONE,
            "",BigDecimal.ZERO,
            "",BigDecimal.ZERO,
            "",BigDecimal.ZERO,
            "D",
            Date.valueOf("2010-11-10"),
            Date.valueOf("2010-11-10"),
            true);

    private Integer aciModelID;
    private String aciModelName;
    private String ipIndicator;
    
    private String investment1SecurityID;
    private BigDecimal investment1Percent;
    private String investment2SecurityID;
    private BigDecimal investment2Percent;
    private String investment3SecurityID;
    private BigDecimal investment3Percent;
    private String investment4SecurityID;
    private BigDecimal investment4Percent;
    
    private String aciFrequencyCode;
    private Date aciNextDueDate;
    private Date dateOfLastACIModelChange;
    private boolean active;
   
    private AutomatedCashInvestmentModelFixture(    
            Integer aciModelID,
            String aciModelName,
            String ipIndicator,
            String investment1SecurityID,
            BigDecimal investment1Percent,
            String investment2SecurityID,
            BigDecimal investment2Percent,
            String investment3SecurityID,
            BigDecimal investment3Percent,
            String investment4SecurityID,
            BigDecimal investment4Percent,
            String aciFrequencyCode,
            Date aciNextDueDate,
            Date dateOfLastACIModelChange,
            boolean active) {  
        
        this.aciModelID = aciModelID;
        this.aciModelName = aciModelName;
        this.ipIndicator = ipIndicator;
        this.investment1SecurityID = investment1SecurityID;
        this.investment1Percent = investment1Percent;
        this.investment2SecurityID = investment2SecurityID;
        this.investment2Percent = investment2Percent;
        this.investment3SecurityID = investment3SecurityID;
        this.investment3Percent = investment3Percent;
        this.investment4SecurityID = investment4SecurityID;
        this.investment4Percent = investment4Percent;
        this.aciFrequencyCode = aciFrequencyCode;
        this.aciNextDueDate = aciNextDueDate;
        this.dateOfLastACIModelChange = dateOfLastACIModelChange;
        this.active = active;
    }
    
    public AutomatedCashInvestmentModel createAutomatedCashInvestmentModel() {
        
        AutomatedCashInvestmentModel aciModel = new AutomatedCashInvestmentModel();
        
        aciModel.setAciModelID(aciModelID);
        aciModel.setAciModelName(aciModelName);
        aciModel.setIpIndicator(ipIndicator);
        aciModel.setInvestment1SecurityID(investment1SecurityID);
        aciModel.setInvestment1Percent(investment1Percent);
        aciModel.setInvestment2SecurityID(investment2SecurityID);
        aciModel.setInvestment2Percent(investment2Percent);
        aciModel.setInvestment3SecurityID(investment3SecurityID);
        aciModel.setInvestment3Percent(investment3Percent);
        aciModel.setInvestment4SecurityID(investment4SecurityID);
        aciModel.setInvestment4Percent(investment4Percent);
        aciModel.setAciFrequencyCode(aciFrequencyCode);
        aciModel.setAciNextDueDate(aciNextDueDate);
        aciModel.setDateOfLastACIModelChange(dateOfLastACIModelChange);
        aciModel.setActive(active);
        
        saveAutomatedCashInvestmentModel(aciModel);
        
        return aciModel;
    }
    
    /**
     * Method to save the business object....
     */
    private void saveAutomatedCashInvestmentModel(AutomatedCashInvestmentModel aciModel) {
        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        businessObjectService.save(aciModel);
    }
}
