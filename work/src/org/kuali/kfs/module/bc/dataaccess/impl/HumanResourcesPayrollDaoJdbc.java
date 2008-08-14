/*
 * Copyright 2008 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.bc.dataaccess.impl;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;

import org.kuali.kfs.module.bc.businessobject.BudgetConstructionPosition;
import org.kuali.kfs.module.bc.businessobject.Position;
import org.kuali.kfs.module.bc.dataaccess.HumanResourcesPayrollDao;
import org.kuali.kfs.module.bc.document.dataaccess.impl.BudgetConstructionDaoJdbcBase;
import org.kuali.kfs.module.ld.businessobject.PositionData;
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
    private PositionData getPositionDataForFiscalYear(Integer universityFiscalYear, String positionNumber) {
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

}
