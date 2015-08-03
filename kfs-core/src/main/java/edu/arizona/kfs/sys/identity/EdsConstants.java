/*
 * Copyright 2012 The Kuali Foundation.
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
package edu.arizona.kfs.sys.identity;

// UAF-6.0 upgrade

/**
 * This class contains constants for EDS keys -- keys into the EDS schema
 * and keys into KFS params. The keys are populated via spring from spring-kim.xml 
 */
public class EdsConstants {

    // These are the field names for the EDS schema
    private String uaIdContextKey;
    private String uidContextKey;
    private String personAffiliationContextKey;
    private String employeeDeptCodeContextKey;
    private String employeeStatusCodeContextKey;
    private String employeeTypeCodeContextKey;
    private String givenNameContextKey;
    private String dccRelationContextKey;
    private String employeePhoneContextKey;
    private String mailContextKey;
    private String employeePrimaryDeptNameContextKey;
    private String employeePoBoxContextKey;
    private String employeeCityContextKey;
    private String employeeStateContextKey;
    private String employeeZipContextKey;
    private String cnContextKey;
    private String snContextKey;
    private String emplIdContextKey;

    // Used by ParameterService interface
    private String parameterNamespaceCode;
    private String parameterDetailTypeCode;

    // These are keys to pull params from KFS
    private String edsRespectedAndOrderedAffsParamKey;
    private String edsUnrespectedAndOrderedAffsParamKey;
    private String edsOrderedActiveStatusIndicatorsParamKey;
    private String edsEmployeeTypesParamKey;
    private String edsRestrictedEmployeeTypesParamKey;
    private String edsDefaultEmployeeTypeParamKey;
    private String edsProfessionalAffsParamKey;
    private String edsNonEmployeeAffsParamKey;
    
    // The delimiter used to split EDS values stored under KFS parameters
    private String kfsParamDelimiter;
    
    
    public EdsConstants() {
    }
    
    public String getUaIdContextKey() {
        return uaIdContextKey;
    }
    
    public void setUaIdContextKey(String uaIdContextKey) {
        this.uaIdContextKey = uaIdContextKey;
    }
    
    public String getUidContextKey(){
        return this.uidContextKey;
    }
    
    public void setUidContextKey(String uidContextKey){
        this.uidContextKey = uidContextKey;
    }
    
    public String getPersonAffiliationContextKey() {
        return personAffiliationContextKey;
    }
    
    public void setPersonAffiliationContextKey(String personAffiliationContextKey) {
        this.personAffiliationContextKey = personAffiliationContextKey;
    }
    
    public String getEmployeeDeptCodeContextKey() {
        return employeeDeptCodeContextKey;
    }
    
    public void setEmployeeDeptCodeContextKey(String employeeDeptCodeContextKey) {
        this.employeeDeptCodeContextKey = employeeDeptCodeContextKey;
    }
    
    public String getEmployeeStatusCodeContextKey() {
        return employeeStatusCodeContextKey;
    }
    
    public void setEmployeeStatusCodeContextKey(String employeeStatusCodeContextKey) {
        this.employeeStatusCodeContextKey = employeeStatusCodeContextKey;
    }
    
    public String getEmployeeTypeCodeContextKey() {
        return employeeTypeCodeContextKey;
    }
    
    public void setEmployeeTypeCodeContextKey(String employeeTypeCodeContextKey) {
        this.employeeTypeCodeContextKey = employeeTypeCodeContextKey;
    }
    
    public String getDccRelationContextKey() {
        return dccRelationContextKey;
    }
    
    public void setDccRelationContextKey(String dccRelationContextKey) {
        this.dccRelationContextKey = dccRelationContextKey;
    }
    
    public String getParameterNamespaceCode() {
        return parameterNamespaceCode;
    }
    
    public void setParameterNamespaceCode(String parameterNamespaceCode) {
        this.parameterNamespaceCode = parameterNamespaceCode;
    }
    
    public String getParameterDetailTypeCode() {
        return parameterDetailTypeCode;
    }
    
    public void setParameterDetailTypeCode(String parameterDetailTypeCode) {
        this.parameterDetailTypeCode = parameterDetailTypeCode;
    }

    /**
     * The key to the KFS EDS_ORDERED_ACTIVE_STATUS_INDICATORS param. All values
     * in this list are considered signify an active status.
     * 
     * Current param is:
     * EDS_ORDERED_ACTIVE_STATUS_INDICATORS=A;L;P;W
     * 
     * @return The string literal 'EDS_ORDERED_ACTIVE_STATUS_INDICATORS'
     */
    public String getEdsOrderedActiveStatusIndicatorsParamKey() {
        return edsOrderedActiveStatusIndicatorsParamKey;
    }

    public void setEdsOrderedActiveStatusIndicatorsParamKey(String edsOrderedActiveStatusIndicatorsParamKey) {
        this.edsOrderedActiveStatusIndicatorsParamKey = edsOrderedActiveStatusIndicatorsParamKey;
    }

    /**
     * The KFS EDS_PROFESSIONAL_AFFS param key. Any affiliation in this param
     * earns the user a 'Professional' designation, and any derived roles
     * along with it.
     * 
     * Current value is:
     * EDS_PROFESSIONAL_AFFS=faculty;staff;00910;00920;00970;00972;00974
     * 
     * @return The string literal "EDS_PROFESSIONAL_AFFS".
     */
    public String getEdsProfessionalAffsParamKey() {
        return edsProfessionalAffsParamKey;
    }

    public void setEdsProfessionalAffsParamKey(String edsProfessionalAffsParamKey) {
        this.edsProfessionalAffsParamKey = edsProfessionalAffsParamKey;
    }

    /**
     * The KFS EDS_RESTRICTED_EMPLOYEE_TYPES param key. Any employeeType in this param
     * will not be granted access to KFS.
     * 
     * Current value is:
     * EDS_RESTRICTED_EMPLOYEE_TYPES=J;U
     * 
     * @return The string literal "EDS_RESTRICTED_EMPLOYEE_TYPES".
     */
    public String getEdsRestrictedEmployeeTypesParamKey() {
        return edsRestrictedEmployeeTypesParamKey;
    }

    public void setEdsRestrictedEmployeeTypesParamKey(String edsExcludedEmployeeTypesParamKey) {
        this.edsRestrictedEmployeeTypesParamKey = edsExcludedEmployeeTypesParamKey;
    }

    public String getGivenNameContextKey() {
        return givenNameContextKey;
    }

    public void setGivenNameContextKey(String givenNameContextKey) {
        this.givenNameContextKey = givenNameContextKey;
    }

    public String getSnContextKey() {
        return snContextKey;
    }

    public void setSnContextKey(String snContextKey) {
        this.snContextKey = snContextKey;
    }

    public String getEmployeePhoneContextKey() {
        return employeePhoneContextKey;
    }

    public void setEmployeePhoneContextKey(String employeePhoneContextKey) {
        this.employeePhoneContextKey = employeePhoneContextKey;
    }

    public String getMailContextKey() {
        return mailContextKey;
    }

    public void setMailContextKey(String mailContextKey) {
        this.mailContextKey = mailContextKey;
    }

    public String getEmployeePrimaryDeptNameContextKey() {
        return employeePrimaryDeptNameContextKey;
    }

    public void setEmployeePrimaryDeptNameContextKey(String employeePrimaryDeptNameContextKey) {
        this.employeePrimaryDeptNameContextKey = employeePrimaryDeptNameContextKey;
    }

    public String getEmployeePoBoxContextKey() {
        return employeePoBoxContextKey;
    }

    public void setEmployeePoBoxContextKey(String employeePoBoxContextKey) {
        this.employeePoBoxContextKey = employeePoBoxContextKey;
    }

    public String getEmployeeCityContextKey() {
        return employeeCityContextKey;
    }

    public void setEmployeeCityContextKey(String employeeCityContextKey) {
        this.employeeCityContextKey = employeeCityContextKey;
    }

    public String getEmployeeStateContextKey() {
        return employeeStateContextKey;
    }

    public void setEmployeeStateContextKey(String employeeStateContextKey) {
        this.employeeStateContextKey = employeeStateContextKey;
    }

    public String getEmployeeZipContextKey() {
        return employeeZipContextKey;
    }

    public void setEmployeeZipContextKey(String employeeZipContextKey) {
        this.employeeZipContextKey = employeeZipContextKey;
    }

    public String getCnContextKey() {
        return cnContextKey;
    }

    public void setCnContextKey(String cnContextKey) {
        this.cnContextKey = cnContextKey;
    }

    /**
     * This is the KFS key for the param named 'EDS_EMPLOYEE_TYPES'. As of
     * release 58, the actual value was:
     * '0,1,2,3,4,5,6,7,8,9,A,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,U,V,W,X,Y,Z' 
     * 
     * The EDS schema describes the employeeType code as:
     * One-character code from UAccess Employee that identifies the type of an employment:
     *
     * 0 = Post Doc Academic
     * 1 = Student Employees
     * 2 = Classified Staff Salary
     * 3 = Ancillary Staff Salary
     * 4 = Regular App/Fac Fiscal
     * 5 = Clinical Faculty
     * 6 = Federal Employees
     * 7 = Ancillary App/Fac Fiscal
     * 8 = Graduate Assistants/Associates
     * 9 = Post Doc Fiscal
     * A = Classified Staff Wage
     * C = -
     * D = Ancillary Staff Wage
     * E = Regular App/Fac Acad
     * F = Regular App/Fac Temp Funding
     * G = Ancillary App/Fac Academic
     * H = Ancillary App/FacTemp Funding
     * I = Supplemental Compensation
     * J = High School Students
     * K = Summer/Winter Session
     * L = Limited Ancillary App/Fac
     * M = Clinical Assistant
     * N = -
     * O = Ancillary Appointed Wages
     * P = POI or Contingent Worker
     * Q = Post Doc Temp Funding
     * R = Clinical Asst - Pharm/Nursing
     * U = Unknown
     * V = Exec/Eve MBA Ancillary App/Fac
     * W = Exec/Eve MBA Regular App/Fac
     * X = Extra Help Ancl App/Fac Wages
     * Y = -
     * Z = Other Professional Service
     */
    public String getEdsEmployeeTypesParamKey() {
        return edsEmployeeTypesParamKey;
    }

    public void setEdsEmployeeTypesParamKey(String edsEmployeeTypesParamKey) {
        this.edsEmployeeTypesParamKey = edsEmployeeTypesParamKey;
    }

    /**
     * This is the employeeType that will be set when an EDS record
     * returns an employeeType that is not contained in the
     * KFS param 'EDS_EMPLOYEE_TYPES'. This is done in order to gracefully
     * handle the cases when HR starts using new employee type codes
     * without telling anyone (otherwise jsp's blow up when looking in KFS
     * tables for the 'plain-english' name of the code).
     * 
     *  IMPORTANT: As of release 58, this param is named 'EDS_DEFAULT_EMPLOYEE_TYPE',
     *  and its value is 'U'. This is important as this code is also in the
     *  'EDS_RESTRICED_EMPLOYEE_TYPES' param, meaning the user will be restricted
     *  from logging in. This is done on purpose, as doing so will alert us to the fact
     *  that a new employeeType code exists, and we should add it to the list
     *  found under the 'EDS_EMPLOYEE_TYPES' param.
     */
    public String getEdsDefaultEmployeeTypeParamKey() {
        return edsDefaultEmployeeTypeParamKey;
    }

    public void setEdsDefaultEmployeeTypeParamKey(String edsDefaultEmployeeTypeParamKey) {
        this.edsDefaultEmployeeTypeParamKey = edsDefaultEmployeeTypeParamKey;
    }

    /**
     * The delimiter to use when splitting EDS values from a KFS param.
     * 
     * Current value is:
     * ";"
     * 
     * @return The string literal ";".
     */
    public String getKfsParamDelimiter() {
        return kfsParamDelimiter;
    }

    public void setKfsParamDelimiter(String kfsParamDelimiter) {
        this.kfsParamDelimiter = kfsParamDelimiter;
    }

    public String getEmplIdContextKey() {
        return emplIdContextKey;
    }

    public void setEmplIdContextKey(String emplIdContextKey) {
        this.emplIdContextKey = emplIdContextKey;
    }

    /**
     * If a user only has affiliations from this set, and none from the
     * EDS_ALL_RESPECTED_AFFS, then the user should not have access to KFS.
     * However, these affiliations should still show up in the KFS UI.
     * Note that these affiliations are all leaf nodes in the EDS schema
     * where the root node is 'member'
     * 
     * EDS Name        Title
     * --------        -----
     * 00960           DCC: Pre-1999 UA Retiree
     * 00973           DCC: Grad Committee Member
     * 00975           DCC: Independent Contractor
     * 00976           DCC: Inter-Institutional Staff/Faculty
     * 00977           DCC: Religious Center Personnel
     * 00978           DCC: Temp Agency Employee
     * 00979           DCC: Volunteer
     * 00990           DCC: Pending Approval
     * retiree         Retiree
     * student         Student
     * 
     * Actual param:
     * EDS_ALL_UNRESPECTED_AFFS=00960;00972;00975;00976;00977;00978;00979;00990;retiree;student
     * 
     */
    public String getEdsUnrespectedAndOrderedAffsParamKey() {
        return edsUnrespectedAndOrderedAffsParamKey;
    }
    
    public void setEdsUnrespectedAndOrderedAffsParamKey(String edsUnrespectedAndOrderedAffsParamKey){
        this.edsUnrespectedAndOrderedAffsParamKey = edsUnrespectedAndOrderedAffsParamKey;
    }
    
    /**
     * KFS Access should be granted to these affiliations. Note that
     * these affiliations are all leaf nodes in the EDS schema
     * where the root node is 'member'
     * 
     * Important: The order of of the values in this param
     *            are used by a comparator to decide which
     *            affiliation is designated as 'primary'.
     * 
     * EDS Name        Title
     * --------        -----
     * faculty         Faculty
     * staff           Staff
     * gradasst        Graduate Assistant
     * studentworker   Student Worker
     * 00910           DCC: Affiliate
     * 00920           DCC: Associate
     * 00950           DCC: Pre-Hire
     * 00970           DCC: UA Foundation Members
     * 00972           DCC: Government Agency Staff
     * 00974           DCC: Time Approver
     * 
     * Actual param:
     * EDS_ALL_RESPECTED_AFFS=faculty;staff;gradasst;studentworker;00910;00920;00950;00970;00972;00974
     */
    public String getEdsRespectedAndOrderedAffsParamKey() {
        return edsRespectedAndOrderedAffsParamKey;
    }

    public void setEdsRespectedAndOrderedAffsParamKey(String edsRespectedAndOrderedAffsParamKey) {
        this.edsRespectedAndOrderedAffsParamKey = edsRespectedAndOrderedAffsParamKey;
    }

    /**
     * Non-Employee Subtypes, KFS Unrespected:
     * EDS Name        Title
     * --------        -----
     * retiree         Retiree
     * student         Student
     * 
     * Param value:
     * EDS_NON_EMPLOYEE_AFFS=student;retiree
     * 
     * @return The string literal that is the key to the KFS EDS_NON_EMPLOYEE_AFFS param.
     */
    public String getEdsNonEmployeeAffsParamKey() {
        return edsNonEmployeeAffsParamKey;
    }

    public void setEdsNonEmployeeAffsParamKey(String edsNonEmployeeAffsParamKey) {
        this.edsNonEmployeeAffsParamKey = edsNonEmployeeAffsParamKey;
    }
}
