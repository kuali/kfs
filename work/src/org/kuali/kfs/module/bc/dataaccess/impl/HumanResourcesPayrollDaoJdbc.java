/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.bc.dataaccess.impl;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;

import org.kuali.kfs.gl.Constant;
import org.kuali.kfs.gl.businessobject.TransientBalanceInquiryAttributes;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionPosition;
import org.kuali.kfs.module.bc.businessobject.Position;
import org.kuali.kfs.module.bc.dataaccess.HumanResourcesPayrollDao;
import org.kuali.kfs.module.bc.document.dataaccess.impl.BudgetConstructionDaoJdbcBase;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

/**
 * Implementation of <code>HumanResourcesPayrollDao</code> using JDBC to query PS_POSITION_DATA and then set other 
 * <code>Position</code> fields to hard-coded IU values. This is for bootstrap only.
 * 
 * @see org.kuali.kfs.module.bc.dataaccess.HumanResourcesPayrollDao
 */
public class HumanResourcesPayrollDaoJdbc extends BudgetConstructionDaoJdbcBase implements HumanResourcesPayrollDao {

    /**
     * Gets the <code>Position</code> data by querying PS_POSITION_DATA then setting other fields using IU business rules. This is
     * used in the bootstrap service implementation for an example.
     * 
     * @see org.kuali.kfs.module.bc.dataaccess.HumanResourcesPayrollDao#getPosition(java.lang.Integer, java.lang.String)
     */
    public Position getPosition(Integer universityFiscalYear, String positionNumber) {
        PositionData positionData = getPositionDataForFiscalYear(universityFiscalYear, positionNumber);
        
        if (positionData == null) {
            return null;
        }

        Position position = new BudgetConstructionPosition();
        position.setUniversityFiscalYear(universityFiscalYear);
        position.setPositionNumber(positionNumber);

        populatePositionData(position, positionData);

        setDefaultObjectClass(position);

        return position;
    }

    /**
     * find positions with effective date before July 1 of fiscal year or on August 1 of fiscal year for academic tenure salary plan
     * 
     * @param universityFiscalYear
     * @param positionNumber
     * @return
     */
    protected PositionData getPositionDataForFiscalYear(Integer universityFiscalYear, String positionNumber) {
        Collection<PositionData> positionData = getPositionData(positionNumber);
        
        if (positionData == null || positionData.isEmpty()) {
            return null;
        }

        // find positions with effective date before July 1 of fiscal year or on August 1 of fiscal year
        // for academic tenure salary plan
        Integer baseFiscalYear = universityFiscalYear - 1;

        GregorianCalendar calendarJuly1 = new GregorianCalendar(baseFiscalYear, Calendar.JULY, 1);
        GregorianCalendar calendarAugust1 = new GregorianCalendar(universityFiscalYear, Calendar.AUGUST, 1);
        Date julyFirst = new Date(calendarJuly1.getTimeInMillis());
        Date augustFirst = new Date(calendarAugust1.getTimeInMillis());

        String academicTenureTrackSalaryPlan = new String("AC1");

        PositionData positionDataMaxEffectiveDate = null;
        for (PositionData posData : positionData) {
            Date positionEffectiveDate = posData.getEffectiveDate();
            if ((positionEffectiveDate.compareTo(julyFirst) <= 0) || (academicTenureTrackSalaryPlan.equals(posData.getPositionSalaryPlanDefault()) && positionEffectiveDate.equals(augustFirst))) {
                // get position with max effective date for year
                if (positionDataMaxEffectiveDate == null || positionDataMaxEffectiveDate.getEffectiveDate().compareTo(positionEffectiveDate) < 0) {
                    positionDataMaxEffectiveDate = posData;
                }
            }
        }

        return positionDataMaxEffectiveDate;
    }

    /**
     * Retrieves record for position key from PS_POSITION_DATA and returns populated <code>PositionData</code> business object.
     */
    public Collection<PositionData> getPositionData(String positionNumber) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT POSITION_NBR, EFFDT, JOBCODE, POS_EFF_STATUS, DESCR, \n");
        sqlBuilder.append("        DESCRSHORT, BUSINESS_UNIT, DEPTID, POSN_STATUS, STATUS_DT, \n");
        sqlBuilder.append("        BUDGETED_POSN, STD_HRS_DEFAULT, STD_HRS_FREQUENCY, POS_REG_TEMP, \n");
        sqlBuilder.append("        POS_FTE, POS_SAL_PLAN_DFLT, POS_GRADE_DFLT \n");
        sqlBuilder.append(" FROM PS_POSITION_DATA \n");
        sqlBuilder.append(" WHERE POSITION_NBR = ? \n");

        String sqlString = sqlBuilder.toString();

        ParameterizedRowMapper<PositionData> mapper = new ParameterizedRowMapper<PositionData>() {

            public PositionData mapRow(ResultSet rs, int rowNum) throws SQLException {
                PositionData positionData = new PositionData();
                positionData.setPositionNumber(rs.getString("POSITION_NBR"));
                positionData.setEffectiveDate(rs.getDate("EFFDT"));
                positionData.setJobCode(rs.getString("JOBCODE"));
                positionData.setPositionEffectiveStatus(rs.getString("POS_EFF_STATUS"));
                positionData.setDescription(rs.getString("DESCR"));
                positionData.setShortDescription(rs.getString("DESCRSHORT"));
                positionData.setBusinessUnit(rs.getString("BUSINESS_UNIT"));
                positionData.setDepartmentId(rs.getString("DEPTID"));
                positionData.setPositionStatus(rs.getString("POSN_STATUS"));
                positionData.setStatusDate(rs.getDate("STATUS_DT"));
                positionData.setBudgetedPosition(rs.getString("BUDGETED_POSN"));
                positionData.setStandardHoursDefault(rs.getBigDecimal("STD_HRS_DEFAULT"));
                positionData.setStandardHoursFrequency(rs.getString("STD_HRS_FREQUENCY"));
                positionData.setPositionRegularTemporary(rs.getString("POS_REG_TEMP"));
                positionData.setPositionFullTimeEquivalency(rs.getBigDecimal("POS_FTE"));
                positionData.setPositionSalaryPlanDefault(rs.getString("POS_SAL_PLAN_DFLT"));
                positionData.setPositionGradeDefault(rs.getString("POS_GRADE_DFLT"));

                return positionData;
            }
        };

        return this.getSimpleJdbcTemplate().query(sqlString, mapper, positionNumber);
    }

    /**
     * Sets <code>Position</code> fields with data from <code>PositionData</code>
     */
    public void populatePositionData(Position position, PositionData positionData) {
        position.setPositionEffectiveDate(positionData.getEffectiveDate());
        position.setJobCode(positionData.getJobCode());
        position.setPositionEffectiveStatus(positionData.getPositionEffectiveStatus());
        position.setPositionDescription(positionData.getDescription());
        position.setSetidDepartment(positionData.getBusinessUnit());
        position.setPositionDepartmentIdentifier(positionData.getDepartmentId());
        position.setPositionStatus(positionData.getPositionStatus());

        if ("Y".equalsIgnoreCase(positionData.getBudgetedPosition())) {
            position.setBudgetedPosition(true);
        }
        else {
            position.setBudgetedPosition(false);
        }

        position.setPositionStandardHoursDefault(positionData.getStandardHoursDefault());
        position.setPositionRegularTemporary(positionData.getPositionRegularTemporary());
        position.setPositionFullTimeEquivalency(positionData.getPositionFullTimeEquivalency());
        position.setPositionSalaryPlanDefault(positionData.getPositionSalaryPlanDefault());
        position.setPositionGradeDefault(positionData.getPositionGradeDefault());
    }

    /**
     * Sets defaults based on salary plan.
     * 
     * @param position <code>Position</code> to update
     */
    public void setDefaultObjectClass(Position position) {
        String salaryPlan = position.getPositionSalaryPlanDefault();

        if ("AC1".equals(salaryPlan)) {
            position.setIuNormalWorkMonths(new Integer(10));
            position.setIuPayMonths(new Integer(10));
            position.setIuPositionType("AC");
            position.setIuDefaultObjectCode("2000");
        }

        else if ("PAO".equals(salaryPlan) || "PAU".equals(salaryPlan)) {
            position.setIuNormalWorkMonths(new Integer(12));
            position.setIuPayMonths(new Integer(12));
            position.setIuPositionType("SM");
            position.setIuDefaultObjectCode("2480");
        }

        else if (salaryPlan.startsWith("P")) {
            position.setIuNormalWorkMonths(new Integer(12));
            position.setIuPayMonths(new Integer(12));
            position.setIuPositionType("SM");
            position.setIuDefaultObjectCode("2400");
        }

        else {
            position.setIuNormalWorkMonths(new Integer(12));
            position.setIuPayMonths(new Integer(12));
            position.setIuPositionType("SB");
            position.setIuDefaultObjectCode("2500");
            position.setPositionUnionCode("B1");
        }
    }
    
    protected class PositionData extends PersistableBusinessObjectBase {
        private String positionNumber;
        private String jobCode;
        private Date effectiveDate;
        private String positionEffectiveStatus;
        private String description;
        private String shortDescription;
        private String businessUnit;
        private String departmentId;
        private String positionStatus;
        private Date statusDate;
        private String budgetedPosition;
        private BigDecimal standardHoursDefault;
        private String standardHoursFrequency;
        private String positionRegularTemporary;
        private BigDecimal positionFullTimeEquivalency;
        private String positionSalaryPlanDefault;
        private String positionGradeDefault;
        private TransientBalanceInquiryAttributes dummyBusinessObject;

        /**
         * Default constructor.
         */
        public PositionData() {
            super();
            this.dummyBusinessObject = new TransientBalanceInquiryAttributes();
            this.dummyBusinessObject.setLinkButtonOption(Constant.LOOKUP_BUTTON_VALUE);
        }

        /**
         * Gets the positionNumber
         * 
         * @return Returns the positionNumber
         */
        public String getPositionNumber() {
            return positionNumber;
        }

        /**
         * Sets the positionNumber
         * 
         * @param positionNumber The positionNumber to set.
         */
        public void setPositionNumber(String positionNumber) {
            this.positionNumber = positionNumber;
        }

        /**
         * Gets the jobCode
         * 
         * @return Returns the jobCode
         */
        public String getJobCode() {
            return jobCode;
        }

        /**
         * Sets the jobCode
         * 
         * @param jobCode The jobCode to set.
         */
        public void setJobCode(String jobCode) {
            this.jobCode = jobCode;
        }

        /**
         * Gets the effectiveDate
         * 
         * @return Returns the effectiveDate
         */
        public Date getEffectiveDate() {
            return effectiveDate;
        }

        /**
         * Sets the effectiveDate
         * 
         * @param effectiveDate The effectiveDate to set.
         */
        public void setEffectiveDate(Date effectiveDate) {
            this.effectiveDate = effectiveDate;
        }

        /**
         * Gets the positionEffectiveStatus
         * 
         * @return Returns the positionEffectiveStatus
         */
        public String getPositionEffectiveStatus() {
            return positionEffectiveStatus;
        }

        /**
         * Sets the positionEffectiveStatus
         * 
         * @param positionEffectiveStatus The positionEffectiveStatus to set.
         */
        public void setPositionEffectiveStatus(String positionEffectiveStatus) {
            this.positionEffectiveStatus = positionEffectiveStatus;
        }

        /**
         * Gets the description
         * 
         * @return Returns the description
         */
        public String getDescription() {
            return description;
        }

        /**
         * Sets the description
         * 
         * @param description The description to set.
         */
        public void setDescription(String description) {
            this.description = description;
        }

        /**
         * Gets the shortDescription
         * 
         * @return Returns the shortDescription
         */
        public String getShortDescription() {
            return shortDescription;
        }

        /**
         * Sets the shortDescription
         * 
         * @param shortDescription The shortDescription to set.
         */
        public void setShortDescription(String shortDescription) {
            this.shortDescription = shortDescription;
        }

        /**
         * Gets the businessUnit
         * 
         * @return Returns the businessUnit
         */
        public String getBusinessUnit() {
            return businessUnit;
        }

        /**
         * Sets the businessUnit
         * 
         * @param businessUnit The businessUnit to set.
         */
        public void setBusinessUnit(String businessUnit) {
            this.businessUnit = businessUnit;
        }

        /**
         * Gets the departmentId
         * 
         * @return Returns the departmentId
         */
        public String getDepartmentId() {
            return departmentId;
        }

        /**
         * Sets the departmentId
         * 
         * @param departmentId The departmentId to set.
         */
        public void setDepartmentId(String departmentId) {
            this.departmentId = departmentId;
        }

        /**
         * Gets the positionStatus
         * 
         * @return Returns the positionStatus
         */
        public String getPositionStatus() {
            return positionStatus;
        }

        /**
         * Sets the positionStatus
         * 
         * @param positionStatus The positionStatus to set.
         */
        public void setPositionStatus(String positionStatus) {
            this.positionStatus = positionStatus;
        }

        /**
         * Gets the statusDate
         * 
         * @return Returns the statusDate
         */
        public Date getStatusDate() {
            return statusDate;
        }

        /**
         * Sets the statusDate
         * 
         * @param statusDate The statusDate to set.
         */
        public void setStatusDate(Date statusDate) {
            this.statusDate = statusDate;
        }

        /**
         * Gets the budgetedPosition
         * 
         * @return Returns the budgetedPosition
         */
        public String getBudgetedPosition() {
            return budgetedPosition;
        }

        /**
         * Sets the budgetedPosition
         * 
         * @param budgetedPosition The budgetedPosition to set.
         */
        public void setBudgetedPosition(String budgetedPosition) {
            this.budgetedPosition = budgetedPosition;
        }

        /**
         * Gets the standardHoursDefault
         * 
         * @return Returns the standardHoursDefault
         */
        public BigDecimal getStandardHoursDefault() {
            return standardHoursDefault;
        }

        /**
         * Sets the standardHoursDefault
         * 
         * @param standardHoursDefault The standardHoursDefault to set.
         */
        public void setStandardHoursDefault(BigDecimal standardHoursDefault) {
            this.standardHoursDefault = standardHoursDefault;
        }

        /**
         * Gets the standardHoursFrequency
         * 
         * @return Returns the standardHoursFrequency
         */
        public String getStandardHoursFrequency() {
            return standardHoursFrequency;
        }

        /**
         * Sets the standardHoursFrequency
         * 
         * @param standardHoursFrequency The standardHoursFrequency to set.
         */
        public void setStandardHoursFrequency(String standardHoursFrequency) {
            this.standardHoursFrequency = standardHoursFrequency;
        }

        /**
         * Gets the positionRegularTemporary
         * 
         * @return Returns the positionRegularTemporary
         */
        public String getPositionRegularTemporary() {
            return positionRegularTemporary;
        }

        /**
         * Sets the positionRegularTemporary
         * 
         * @param positionRegularTemporary The positionRegularTemporary to set.
         */
        public void setPositionRegularTemporary(String positionRegularTemporary) {
            this.positionRegularTemporary = positionRegularTemporary;
        }

        /**
         * Gets the positionFullTimeEquivalency
         * 
         * @return Returns the positionFullTimeEquivalency
         */
        public BigDecimal getPositionFullTimeEquivalency() {
            return positionFullTimeEquivalency;
        }

        /**
         * Sets the positionFullTimeEquivalency
         * 
         * @param positionFullTimeEquivalency The positionFullTimeEquivalency to set.
         */
        public void setPositionFullTimeEquivalency(BigDecimal positionFullTimeEquivalency) {
            this.positionFullTimeEquivalency = positionFullTimeEquivalency;
        }

        /**
         * Gets the positionSalaryPlanDefault
         * 
         * @return Returns the positionSalaryPlanDefault
         */
        public String getPositionSalaryPlanDefault() {
            return positionSalaryPlanDefault;
        }

        /**
         * Sets the positionSalaryPlanDefault
         * 
         * @param positionSalaryPlanDefault The positionSalaryPlanDefault to set.
         */
        public void setPositionSalaryPlanDefault(String positionSalaryPlanDefault) {
            this.positionSalaryPlanDefault = positionSalaryPlanDefault;
        }

        /**
         * Gets the positionGradeDefault
         * 
         * @return Returns the positionGradeDefault
         */
        public String getPositionGradeDefault() {
            return positionGradeDefault;
        }

        /**
         * Sets the positionGradeDefault
         * 
         * @param positionGradeDefault The positionGradeDefault to set.
         */
        public void setPositionGradeDefault(String positionGradeDefault) {
            this.positionGradeDefault = positionGradeDefault;
        }

        /**
         * construct the key list of the business object.
         * 
         * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
         */
        protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
            LinkedHashMap m = new LinkedHashMap();
            m.put("positionNumber", this.positionNumber);
            if (this.effectiveDate != null) {
                m.put("effectiveDate", this.effectiveDate.toString());
            }

            return m;
        }

        /**
         * Gets the dummyBusinessObject
         * 
         * @return Returns the dummyBusinessObject.
         */
        public TransientBalanceInquiryAttributes getDummyBusinessObject() {
            return dummyBusinessObject;
        }

        /**
         * Sets the dummyBusinessObject
         * 
         * @param dummyBusinessObject The dummyBusinessObject to set.
         */
        public void setDummyBusinessObject(TransientBalanceInquiryAttributes dummyBusinessObject) {
            this.dummyBusinessObject = dummyBusinessObject;
        }
    }

}
