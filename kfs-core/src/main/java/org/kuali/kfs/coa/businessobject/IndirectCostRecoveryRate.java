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
package org.kuali.kfs.coa.businessobject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.FiscalYearBasedBusinessObject;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

public class IndirectCostRecoveryRate extends PersistableBusinessObjectBase implements MutableInactivatable, FiscalYearBasedBusinessObject {
    
    private Integer universityFiscalYear;
    private String financialIcrSeriesIdentifier;
    private boolean active;
    private List indirectCostRecoveryRateDetails;
    
    private SystemOptions universityFiscal;
    
    public IndirectCostRecoveryRate() {
        universityFiscalYear = SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();
        indirectCostRecoveryRateDetails = new ArrayList<IndirectCostRecoveryRateDetail>();
    }
    
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {

        LinkedHashMap m = new LinkedHashMap();
        m.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, this.universityFiscalYear);
        m.put(KFSPropertyConstants.FINANCIAL_ICR_SERIES_IDENTIFIER, this.financialIcrSeriesIdentifier);

        return m;
    }

    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }

    public void setUniversityFiscalYear(Integer universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
    }

    public SystemOptions getUniversityFiscal() {
        return universityFiscal;
    }

    public void setUniversityFiscal(SystemOptions universityFiscal) {
        this.universityFiscal = universityFiscal;
    }

    public List getIndirectCostRecoveryRateDetails() {
        return indirectCostRecoveryRateDetails;
    }

    public void setIndirectCostRecoveryRateDetails(List indirectCostRecoveryRateDetails) {
        this.indirectCostRecoveryRateDetails = indirectCostRecoveryRateDetails;
    }

    public String getFinancialIcrSeriesIdentifier() {
        return financialIcrSeriesIdentifier;
    }

    public void setFinancialIcrSeriesIdentifier(String financialIcrSeriesIdentifier) {
        this.financialIcrSeriesIdentifier = financialIcrSeriesIdentifier;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
    
}
