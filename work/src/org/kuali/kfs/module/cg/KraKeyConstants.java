/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.module.kra;

/**
 * Holds error key constants for KRA.
 * 
 * 
 */
public class KraKeyConstants {

    // KRA Audit Errors
    public static final String AUDIT_COST_SHARE_INSTITUTION_DISTRIBUTED = "audit.costShare.institution.distributed";
    public static final String AUDIT_COST_SHARE_3P_DISTRIBUTED = "audit.costShare.3p.distributed";
    public static final String AUDIT_MODULAR_CONSORTIUM = "audit.modular.consortium";
    public static final String AUDIT_PERSONNEL_STATUS = "audit.personnel.status";
    public static final String AUDIT_NONPERSONNEL_SUBCONTRACTOR_EXCESS_AMOUNT = "audit.nonpersonnel.subcontractorExceesAmount";
    public static final String AUDIT_PARAMETERS_NEGATIVE_IDC = "audit.parameters.negativeIdc";
    public static final String ERROR_INVALID_AMOUNT_POSITIVE_ONLY = "error.invalid.amount.positive.only";
    public static final String ERROR_ORG_ALREADY_EXISTS_ON_RF = "error.org.already.exists.on.rf";
    public static final String ERROR_ACCOUNT_ALREADY_EXISTS_ON_RF = "error.account.already.exists.on.rf";
    public static final String ERROR_ORG_NOT_FOUND = "error.org.not.found";
    public static final String ERROR_SUBCONTRACTOR_NOT_FOUND = "error.subcontractor.not.found";
    public static final String ERROR_SUBCONTRACTOR_ALREADY_EXISTS_ON_RF = "error.subcontractor.already.exists.on.rf";
    public static final String ERROR_FRINGE_RATE_TOO_LARGE = "error.fringeRate.tooLarge";
    public static final String ERROR_AGENCY_REQUIRED = "audit.routingForm.mainPage.agency";
    
    // Routing Form Research Risks page errors
    public static final String ERROR_APPROVAL_DATE_REQUIRED = "error.approvalDate.required";
    public static final String ERROR_APPROVAL_DATE_REMOVE = "error.approvalDate.remove";
    public static final String ERROR_EXPIRATION_DATE_REMOVE = "error.expirationDate.remove";
    public static final String ERROR_EXEMPTION_NUMBER_REQUIRED = "error.exemptionNumber.required";
    public static final String ERROR_EXEMPTION_NUMBER_REMOVE = "error.exemptionNumber.remove";
    public static final String ERROR_HUMAN_SUBJECTS_APPROVAL_DATE_TOO_OLD = "error.humanSubjects.approvalDate.tooOld";
    public static final String ERROR_ANIMALS_APPROVAL_DATE_TOO_OLD = "error.animals.approvalDate.tooOld";
    public static final String ERROR_EXPIRATION_DATE_TOO_EARLY = "error.expiration.tooEarly";
}