import java.util.regex.Matcher
import java.util.regex.Pattern
// test change
def baseDir = '/java/projects/kfs'

def sourceDirectories = [       
    '/work/', 
    '/test/', 
]

def filesToDelete = [
    '/work/src/org/kuali/kfs/sys/businessobject/FinancialSystemUser.java',
    '/work/src/org/kuali/kfs/sys/businessobject/inquiry/FinancialSystemUserInquirableImpl.java',
    '/work/src/org/kuali/kfs/sys/businessobject/lookup/FinancialSystemUserLookupableImpl.java',
    '/work/src/org/kuali/kfs/sys/businessobject/FinancialSystemUserPrimaryOrganization.java',
    '/work/src/org/kuali/kfs/sys/businessobject/lookup/FinancialSystemUserLookupableHelperServiceImpl.java',
    '/work/src/org/kuali/kfs/sys/businessobject/FinancialSystemUserOrganizationSecurity.java',
    '/work/src/org/kuali/kfs/sys/document/authorization/FinancialSystemUserDocumentAuthorizer.java',
    '/work/src/org/kuali/kfs/sys/document/FinancialSystemUserMaintainable.java',
    '/work/src/org/kuali/kfs/sys/document/validation/impl/FinancialSystemUserRule.java',
    '/work/src/org/kuali/kfs/sys/service/FinancialSystemUserService.java',
    '/work/src/org/kuali/kfs/sys/service/impl/FinancialSystemUserServiceImpl.java',
    '/work/src/org/kuali/rice/kns/UserSession.java',
    '/work/src/org/kuali/kfs/sys/businessobject/datadictionary/FinancialSystemUser.xml',
    '/work/db/workflow/documentType/FinancialSystemUserMaintenanceDocument.xml',
]

replacements = [
    // import replacements - these need to be before the rest of the replacements
   
    ['import org\\.kuali\\.rice\\.kns\\.bo\\.user\\.AuthenticationUserId;', ''],
    ['import org\\.kuali\\.rice\\.kns\\.bo\\.user\\.PersonPayrollId;', ''],
    ['import org\\.kuali\\.rice\\.kns\\.bo\\.user\\.PersonTaxId;', ''],
    ['import org\\.kuali\\.rice\\.kns\\.bo\\.user\\.UniversalUser;', 'import org\\.kuali\\.rice\\.kim\\.bo\\.Person;'],
    ['import org\\.kuali\\.kfs\\.sys\\.businessobject\\.FinancialSystemUser;', 'import org\\.kuali\\.rice\\.kim\\.bo\\.Person;'],
    ['import org\\.kuali\\.rice\\.kns\\.bo\\.user\\.UserId;', ''],
    ['import org\\.kuali\\.rice\\.kns\\.bo\\.user\\.Uuid;', ''],
    ['import org\\.kuali\\.rice\\.kns\\.bo\\.user\\.UuId;', ''],
    ['import org\\.kuali\\.rice\\.kns\\.bo\\.user\\.KualiGroup;', 'import org\\.kuali\\.rice\\.kim\\.bo\\.group\\.KimGroup;'],
    ['import org\\.kuali\\.rice\\.kns\\.service\\.impl\\.KualiGroupServiceImpl;', ''],
    ['import org\\.kuali\\.rice\\.kns\\.service\\.KualiGroupService;', 'import org\\.kuali\\.rice\\.kim\\.service\\.GroupService;'],
    ['import org\\.kuali\\.rice\\.kns\\.bo\\.user\\.KualiUniversalGroup;', ''],
    ['import org\\.kuali\\.rice\\.kns\\.dao\\.impl\\.UniversalUserDaoJpa;', ''],
    ['import org\\.kuali\\.rice\\.kns\\.dao\\.impl\\.UniversalUserDaoOjb;', ''],
    ['import org\\.kuali\\.rice\\.kns\\.dao\\.proxy\\.UniversalUserDaoProxy;', ''],
    ['import org\\.kuali\\.rice\\.kns\\.dao\\.RiceKNSDefaultUserDAOImpl;', ''],
    ['import org\\.kuali\\.rice\\.kns\\.dao\\.UniversalUserDao;', ''],
    ['import org\\.kuali\\.rice\\.kns\\.document\\.authorization\\.UniversalUserDocumentAuthorizer;', ''],
    ['import org\\.kuali\\.rice\\.kns\\.lookup\\.UniversalUserLookupableHelperServiceImpl;', ''],
    ['import org\\.kuali\\.rice\\.kns\\.lookup\\.UniversalUserLookupableImpl;', ''],
    ['import org\\.kuali\\.rice\\.kns\\.maintenance\\.UniversalUserMaintainable;', ''],
    ['import org\\.kuali\\.rice\\.kns\\.service\\.impl\\.UniversalUserServiceImpl;', 'import org\\.kuali\\.rice\\.kim\\.service\\.impl\\.PersonServiceImpl;'],
    ['import org\\.kuali\\.rice\\.kns\\.service\\.UniversalUserService;', 'import org\\.kuali\\.rice\\.kim\\.service\\.PersonService;'],
    ['import org\\.kuali\\.rice\\.kns\\.rules\\.UniversalUserRule;', ''],
    ['import org\\.kuali\\.rice\\.kns\\.rules\\.UniversalUserPreRules;', ''],
    ['import org\\.kuali\\.rice\\.kns\\.authorization\\.UniversalUserAuthorizationConstants;', ''],    
    // done with imports
    
    [ 'KNSServiceLocator\\.getUniversalUserService\\(\\)\\.getUniversalUser\\(', 'org\\.kuali\\.rice\\.kim\\.service\\.KIMServiceLocator\\.getPersonService\\(\\)\\.getPerson\\(' ],
    [ 'KNSServiceLocator\\.getUniversalUserService\\(\\)', 'org\\.kuali\\.rice\\.kim\\.service\\.KIMServiceLocator\\.getPersonService\\(\\)' ],
    [ 'DataDictionary\\.UniversalUser\\.attributes\\.personUserIdentifier', 'DataDictionary\\.PersonImpl\\.attributes\\.principalName' ],
    [ 'DataDictionary\\.UniversalUser\\.attributes', 'DataDictionary\\.PersonImpl\\.attributes' ],
    [ 'universalUser\\.personFirstName', 'person\\.firstName' ],
    [ 'universalUser\\.personMiddleName', 'person\\.middleName' ],
    [ 'universalUser\\.personLastName', 'person\\.lastName' ],
    [ 'universalUserService\\.getUniversalUserByAuthenticationUserId', 'personService\\.getPersonByPrincipalName' ],
    [ 'UserService\\.getUniversalUserByAuthenticationUserId', 'PersonService\\.getPersonByPrincipalName' ],
    [ 'UserService\\.js', 'PersonService\\.js' ],
    [ 'org\\.kuali\\.rice\\.kns\\.bo\\.user\\.UniversalUser', 'org\\.kuali\\.rice\\.kim\\.bo\\.Person' ],
    [ 'org\\.kuali\\.rice\\.kns\\.service\\.UniversalUserService', 'org\\.kuali\\.rice\\.kim\\.service\\.PersonService' ],
    [ '\\.getPersonUniversalIdentifier\\(\\)', '\\.getPrincipalId\\(\\)' ],
    [ '\\.getPersonUserIdentifier\\(\\)', '\\.getPrincipalName\\(\\)' ],
    [ '\\.getPersonName\\(\\)', '\\.getName\\(\\)' ],
    [ 'resolveUserIdentifiersToUniversalIdentifiers', 'resolvePrincipalNamesToPrincipalIds' ],
    [ '\\bUniversalUserService\\b', 'org\\.kuali\\.rice\\.kim\\.service\\.PersonService' ],
    [ 'org\\.kuali\\.kfs\\.sys\\.service\\.FinancialSystemUserService', 'org\\.kuali\\.rice\\.kim\\.service\\.PersonService' ],
    [ 'FinancialSystemUserService', 'PersonService' ],
    [ 'financialSystemUserService', 'personService' ],
    [ 'UniversalUserService', 'PersonService' ],
    [ 'universalUserService', 'personService' ],
    [ 'universalUserService\\.getUniversalUser\\(', 'personService\\.getPerson\\(' ],   
    [ 'new FinancialSystemUser\\(', 'new org\\.kuali\\.rice\\.kim\\.bo\\.impl\\.PersonImpl\\(' ],
    [ 'new UniversalUser\\(', 'new org\\.kuali\\.rice\\.kim\\.bo\\.impl\\.PersonImpl\\(' ],
    [ 'UniversalUser\\.class\\.isAssignableFrom\\(referenceClass\\) \\|\\| ', '' ],
    [ 'UniversalUser', 'Person' ],
    [ 'universalUser', 'person' ],
    [ 'org\\.kuali\\.kfs\\.sys\\.businessobject\\.FinancialSystemUser', 'org\\.kuali\\.rice\\.kim\\.bo\\.Person'],
    [ 'FinancialSystemUser', 'Person'],
    [ 'personUniversalIdentifier', 'principalId' ],
    [ 'personUserIdentifier', 'principalName' ],
    [ '\\.getPersonPayrollIdentifier\\(\\)', '\\.getExternalId\\( org\\.kuali\\.rice\\.kim\\.util\\.KimConstants\\.EMPLOYEE_EXT_ID_TYPE \\)' ],
    [ '\\.getPersonTaxIdentifier\\(\\)', '\\.getExternalId\\( org\\.kuali\\.rice\\.kim\\.util\\.KimConstants\\.TAX_EXT_ID_TYPE \\)' ],
    [ '\\.getPersonEmailAddress\\(\\)', '\\.getEmailAddress\\(\\)' ],
    [ '\\.getPersonFirstName\\(\\)', '\\.getFirstName\\(\\)' ],
    [ '\\.getPersonLastName\\(\\)', '\\.getLastName\\(\\)' ],
    [ '\\.getPersonCampusAddress\\(\\)', '\\.getAddressLine1\\(\\)' ],
    [ '\\.getPersonLocalPhoneNumber\\(\\)', '\\.getPhoneNumber\\(\\)' ],
    [ '\\.getCampusCode\\(\\)', '\\.getCampusCode\\(\\)' ],
    [ '\\.getPersonMiddleName\\(\\)', '\\.getMiddleName\\(\\)' ],
    [ '\\.isAffiliate\\(\\)', '\\.hasAffiliationOfType\\( org\\.kuali\\.rice\\.kim\\.util\\.KimConstants\\.AFFILIATE_AFFILIATION_TYPE \\)' ],
    [ '\\.isFaculty\\(\\)', '\\.hasAffiliationOfType\\( org\\.kuali\\.rice\\.kim\\.util\\.KimConstants\\.FACULTY_AFFILIATION_TYPE \\)' ],
    [ '\\.isStaff\\(\\)', '\\.hasAffiliationOfType\\( org\\.kuali\\.rice\\.kim\\.util\\.KimConstants\\.STAFF_AFFILIATION_TYPE \\)' ],
    [ '\\.isStudent\\(\\)', '\\.hasAffiliationOfType\\( org\\.kuali\\.rice\\.kim\\.util\\.KimConstants\\.STUDENT_AFFILIATION_TYPE \\)' ],
    [ 'new KualiGroup\\(', 'new org\\.kuali\\.rice\\.kim\\.bo\\.group\\.impl\\.KimGroupImpl\\(' ],
    [ 'getPersonService\\(\\)\\.getPersonByAuthenticationUserId\\(', 'getPersonService\\(\\)\\.getPersonByPrincipalName\\(' ],    
    [ 'KUALI_GROUP_SERVICE', 'KIM_GROUP_SERVICE'],
    [ 'UNIVERSAL_USER_SERVICE', 'PERSON_SERVICE'],
    [ 'KNSServiceLocator\\.getKualiGroupService\\(\\)\\.getByGroupName\\(', 'org\\.kuali\\.rice\\.kim\\.service\\.KIMServiceLocator\\.getIdentityManagementService\\(\\)\\.getGroupByName\\(\"KFS\", ' ],
    [ 'kualiGroupService\\(\\)\\.getByGroupName\\(', 'org\\.kuali\\.rice\\.kim\\.service\\.KIMServiceLocator\\.getIdentityManagementService\\(\\)\\.getGroupByName\\(\"KFS\", ' ],
    [ 'KNSServiceLocator\\.getKualiGroupService\\(\\)\\.getUsersGroups\\((.*)\\)', 'org\\.kuali\\.rice\\.kim\\.service\\.KIMServiceLocator\\.getPersonService\\(\\)\\.getPersonGroups\\(\$1, \"KFS\"\\)' ],
    [ 'kualiGroupService\\(\\)\\.getUsersGroups\\((.*)\\)', 'org\\.kuali\\.rice\\.kim\\.service\\.KIMServiceLocator\\.getPersonService\\(\\)\\.getPersonGroups\\(\$1, \"KFS\"\\)' ],
    [ 'KNSServiceLocator\\.getKualiGroupService\\(\\)\\.groupExists\\(', 'org\\.kuali\\.rice\\.kim\\.service\\.KIMServiceLocator\\.getIdentityManagementService\\(\\)\\.groupExistsByName\\(\"KFS\", ' ],
    [ 'kualiGroupService\\(\\)\\.groupExists\\(', 'org\\.kuali\\.rice\\.kim\\.service\\.KIMServiceLocator\\.getIdentityManagementService\\(\\)\\.groupExistsByName\\(\"KFS\", ' ],
    [ 'SpringContext\\.getBean\\(KualiGroupService\\.class\\)\\.getByGroupName\\((.*)\\)\\.hasMember\\((.*)\\)', 'org\\.kuali\\.rice\\.kim\\.service\\.KIMServiceLocator\\.getIdentityManagementService\\(\\)\\.isMemberOfGroup\\(\$2.getPrincipalId\\(\\), org\\.kuali\\.rice\\.kim\\.service\\.KIMServiceLocator\\.getIdentityManagementService\\(\\)\\.getGroupByName\\(\"KFS\", \$1\\)\\.getGroupId\\(\\)\\)' ],
    [ 'groupService\\.', 'org\\.kuali\\.rice\\.kim\\.service\\.KIMServiceLocator\\.getIdentityManagementService\\(\\)\\.' ],
    [ 'getGroupService\\(\\)\\.', 'org\\.kuali\\.rice\\.kim\\.service\\.KIMServiceLocator\\.getIdentityManagementService\\(\\)\\.' ],
    
    [ '\\.getByGroupName\\(', '\\.getGroupByName\\(\"KFS\", ' ],
    [ '\\.getGroupUsers\\(', '\\.getMembers\\(' ],    
    [ 'KualiGroupService', 'GroupService' ],
    [ 'KualiGroup', 'KimGroup' ],
    [ 'kualiGroup', 'kimGroup' ],
    [ 'kimGroupService\\.', 'org\\.kuali\\.rice\\.kim\\.service\\.KIMServiceLocator\\.getIdentityManagementService\\(\\)\\.' ],
    [ 'SpringContext\\.getBean\\(GroupService\\.class\\)\\.', 'org\\.kuali\\.rice\\.kim\\.service\\.KIMServiceLocator\\.getIdentityManagementService\\(\\)\\.' ],
    [ '\\.findPersons\\(', '\\.findPeople\\(' ], 
    // These are ugly, but will need revisited anyway once we figure out what we are really going to do with supervisor access given KIM permissions/roles.
    [ 'user\\.isSupervisorUser\\(\\)', 'org\\.kuali\\.rice\\.kim\\.service\\.KIMServiceLocator\\.getPersonService\\(\\)\\.isMemberOfGroup\\(user, \"KFS\", org\\.kuali\\.rice\\.kns\\.service\\.KNSServiceLocator\\.getKualiConfigurationService\\(\\)\\.getParameterValue\\(org\\.kuali\\.rice\\.kns\\.util\\.KNSConstants\\.KNS_NAMESPACE, org\\.kuali\\.rice\\.kns\\.util\\.KNSConstants.DetailTypes\\.DOCUMENT_DETAIL_TYPE, org\\.kuali\\.rice\\.kns\\.util\\.KNSConstants\\.CoreApcParms.SUPERVISOR_WORKGROUP\\)\\)' ], 
    [ 'user\\.isWorkflowExceptionUser\\(\\)', 'org\\.kuali\\.rice\\.kim\\.service\\.KIMServiceLocator\\.getPersonService\\(\\)\\.isMemberOfGroup\\(user, \"KFS\", org\\.kuali\\.rice\\.kns\\.service\\.KNSServiceLocator\\.getKualiConfigurationService\\(\\)\\.getParameterValue\\(org\\.kuali\\.rice\\.kns\\.util\\.KNSConstants\\.KNS_NAMESPACE, org\\.kuali\\.rice\\.kns\\.util\\.KNSConstants.DetailTypes\\.DOCUMENT_DETAIL_TYPE, org\\.kuali\\.rice\\.kns\\.util\\.KNSConstants\\.CoreApcParms.WORKFLOW_EXCEPTION_WORKGROUP\\)\\)' ], 
    [ 'currentUser\\.isWorkflowExceptionUser\\(\\)', 'org\\.kuali\\.rice\\.kim\\.service\\.KIMServiceLocator\\.getPersonService\\(\\)\\.isMemberOfGroup\\(currentUser, \"KFS\", org\\.kuali\\.rice\\.kns\\.service\\.KNSServiceLocator\\.getKualiConfigurationService\\(\\)\\.getParameterValue\\(org\\.kuali\\.rice\\.kns\\.util\\.KNSConstants\\.KNS_NAMESPACE, org\\.kuali\\.rice\\.kns\\.util\\.KNSConstants.DetailTypes\\.DOCUMENT_DETAIL_TYPE, org\\.kuali\\.rice\\.kns\\.util\\.KNSConstants\\.CoreApcParms.WORKFLOW_EXCEPTION_WORKGROUP\\)\\)' ], 
    [ 'user\\.isAdministratorUser\\(\\)', 'org\\.kuali\\.rice\\.kim\\.service\\.KIMServiceLocator\\.getIdentityManagementService\\(\\)\\.isMemberOfGroup\\(user\\.getPrincipalId\\(\\), SpringContext\\.getBean\\(org\\.kuali\\.kfs\\.sys\\.service\\.ParameterService\\.class\\)\\.getParameterValue\\(org\\.kuali\\.kfs\\.sys\\.service\\.impl\\.ParameterConstants\\.CHART_DOCUMENT\\.class, org\\.kuali\\.kfs\\.sys\\.KFSConstants\\.MAINTENANCE_ADMIN_WORKGROUP_PARM_NM\\)\\)' ], 
    [ '\"personUniversalIdentifier\"', '\"principalId\"' ],
    [ '\"personUserIdentifier\"', '\"principalName\"' ],
    [ '\"personName\"', '\"name\"' ],
    [ '\"personLocalPhoneNumbe\r\"', '\"phoneNumber\"' ],
    [ '\"personFirstName\"', '\"firstName\"' ],
    [ '\"personLastName\"', '\"lastName\"' ],
    [ '\"personEmailAddress\"', '\"emailAddress\"' ],
    [ '\"personCampusAddress\"', '\"addressLine1\"' ],
    [ '\"personBaseSalaryAmount\"', '\"baseSalaryAmount\"' ],
    [ '\\.getPersonByAuthenticationUserId\\(', '\\.getPersonByPrincipalName\\(' ],

    // Special cases
    [ 'budgetOverviewPersonnelHelper\\.getName\\(', 'budgetOverviewPersonnelHelper\\.getPersonName\\(' ],
    [ 'projectDirector\\.getEmailAddress\\(', 'projectDirector\\.getPersonEmailAddress\\(' ],
    [ 'contactPerson\\.getEmailAddress\\(', 'contactPerson\\.getPersonEmailAddress\\(' ],
    [ 'incumbent\\.getName\\(', 'incumbent\\.getPersonName\\(' ],
    [ 'getProjectDirector\\(\\)\\.getName\\(', 'getProjectDirector\\(\\)\\.getPersonName\\(' ],
    [ 'budgetConstructionIntendedIncumbent\\.getName\\(', 'budgetConstructionIntendedIncumbent\\.getPersonName\\(' ],
    [ 'getBudgetConstructionIntendedIncumbent\\(\\)\\.getName\\(', 'getBudgetConstructionIntendedIncumbent\\(\\)\\.getPersonName\\(' ],
    [ 'budgetOverviewBoHelper\\.getName\\(', 'budgetOverviewBoHelper\\.getPersonName\\(' ],
    [ 'incumbent\\.getName\\(', 'incumbent\\.getPersonName\\(' ],
    [ 'incumbent\\.getName\\(', 'incumbent\\.getPersonName\\(' ],
    [ 'pd\\.getPrincipalId\\(', 'pd\\.getPersonUniversalIdentifier\\(' ],
    [ 'projectDirector\\.getPrincipalId\\(', 'projectDirector\\.getPersonUniversalIdentifier\\(' ],
    [ 'budgetUser\\.getPrincipalId\\(', 'budgetUser\\.getPersonUniversalIdentifier\\(' ],
    [ 'contactPerson\\.getEmailAddress\\(', 'contactPerson\\.getPersonEmailAddress\\(' ],
    [ 'projectDirector\\.getEmailAddress\\(', 'projectDirector\\.getPersonEmailAddress\\(' ],
    [ 'return principalId\\.equals\\(person\\.getPrincipalId\\(\\)\\);', 'return principalId\\.equals\\(person\\.getPersonUniversalIdentifier\\(\\)\\);' ],
    [ 'xml\\.append\\(person\\.getPrincipalId\\(\\)\\);', 'xml\\.append\\(person\\.getPersonUniversalIdentifier\\(\\)\\);' ],
    [ 'budgetConstructionObjectPick\\.getPrincipalId\\(', 'budgetConstructionObjectPick\\.getPersonUniversalIdentifier\\(' ],
    [ 'bcPullup\\.getPrincipalId\\(', 'bcPullup\\.getPersonUniversalIdentifier\\(' ],
    [ 'budgetConstructionObjectPick\\.getPrincipalId\\(', 'budgetConstructionObjectPick\\.getPersonUniversalIdentifier\\(' ],
    [ 'budgetUser\\.getPrincipalId\\(', 'budgetUser\\.getPersonUniversalIdentifier\\(' ],
    [ 'personnel\\.\" + person\\.getPrincipalId\\(', 'personnel\\.\" + person\\.getPersonUniversalIdentifier\\(' ],
    [ 'newBudgetUser\\.getPrincipalId\\(', 'newBudgetUser\\.getPersonUniversalIdentifier\\(' ],
    [ '\\(\\(BudgetUser\\) budgetUserIter\\.next\\(\\)\\)\\.getPrincipalId\\(', '\\(\\(BudgetUser\\) budgetUserIter\\.next\\(\\)\\)\\.getPersonUniversalIdentifier\\(' ],
    [ 'dir\\.getPrincipalId\\(', 'dir\\.getPersonUniversalIdentifier\\(' ],
    [ 'budgetConstructionSubFundPick\\.getPrincipalId\\(', 'budgetConstructionSubFundPick\\.getPersonUniversalIdentifier\\(' ],    
    [ 'director\\.getPrincipalId\\(', 'director\\.getPersonUniversalIdentifier\\(' ],
    [ 'projectDirector\\.getPrincipalName\\(', 'projectDirector\\.getPersonUserIdentifier\\(' ],
    [ 'getProjectDirector\\(\\)\\.getPrincipalName\\(', 'getProjectDirector\\(\\)\\.getPersonUserIdentifier\\(' ],
    [ 'budgetConstructionReasonCodePick\\.getPrincipalId\\(', 'budgetConstructionReasonCodePick\\.getPersonUniversalIdentifier\\(' ],
    [ '\\.getPersonBaseSalaryAmount\\(\\)', '\\.getBaseSalaryAmount\\(\\)' ],
    [ 'if \\(this\\.getPrincipalId\\(\\) != null\\)', 'if \\(this\\.getPersonUniversalIdentifier\\(\\) != null\\)' ],    
    [ 'holdingRecord\\.getName\\(', 'holdingRecord\\.getPersonName\\(' ],
    [ 'positionFundingDetailEntry\\.getName\\(', 'positionFundingDetailEntry\\.getPersonName\\(' ],
    [ 'positionFunding\\.getName\\(', 'positionFunding\\.getPersonName\\(' ],
    [ 'bcSSN\\.getName\\(', 'bcSSN\\.getPersonName\\(' ],
    [ 'String accountProjectDirectorPersonUserId = projectDirector\\.getPersonUserIdentifier\\(\\)', 'String accountProjectDirectorPersonUserId = projectDirector\\.getPrincipalId\\(\\)' ],
    [ 'newPayeeAchAccount\\.getPrincipalId\\(', 'newPayeeAchAccount\\.getPersonUniversalIdentifier\\(' ],
    [ 'oldPayeeAchAccount\\.getPrincipalId\\(', 'oldPayeeAchAccount\\.getPersonUniversalIdentifier\\(' ],
    [ 'm\\.put\\(\"person\\.getUniversalIdentifier\", this\\.getPrincipalId\\(\\)\\);', 'm\\.put\\(\"person\\.getUniversalIdentifier\", this\\.getPersonUniversalIdentifier\\(\\)\\);' ],
    [ 'pds\\.iterator\\(\\)\\.next\\(\\)\\.getPrincipalId\\(\\)\\);', 'pds\\.iterator\\(\\)\\.next\\(\\)\\.getPersonUniversalIdentifier\\(\\)\\);' ],
    [ 'pDirector\\.getPrincipalId\\(', 'pDirector\\.getPersonUniversalIdentifier\\(' ],
    [ 'u\\.getPrincipalId\\(\\)\\.equals\\(person\\.getPrincipalId\\(\\)\\)', 'u\\.getPrincipalId\\(\\)\\.equals\\(person\\.getPersonUniversalIdentifier\\(\\)\\)' ],
    [ 'fieldValues\\.put\\(KFSPropertyConstants\\.PERSON_UNIVERSAL_IDENTIFIER, person\\.getPrincipalId\\(\\)\\)', 'fieldValues\\.put\\(KFSPropertyConstants\\.PERSON_UNIVERSAL_IDENTIFIER, person\\.getPersonUniversalIdentifier\\(\\)\\)' ],
    [ 'if \\(person\\.getPrincipalId\\(\\) == null && !person\\.isPersonToBeNamedIndicator\\(\\)\\)', 'if \\(person\\.getPersonUniversalIdentifier\\(\\) == null && !person\\.isPersonToBeNamedIndicator\\(\\)\\)' ],
    [ 'tempListLookupForm\\.getPrincipalId\\(', 'tempListLookupForm\\.getPersonUniversalIdentifier\\(' ],
    [ 'chartManager\\.isActivePerson\\(\\)', 'org\\.kuali\\.kfs\\.sys\\.context\\.SpringContext\\.getBean\\(org\\.kuali\\.kfs\\.sys\\.service\\.KNSAuthorizationService\\.class\\)\\.isActive\\(chartManager\\)' ],
    [ 'fiscalOfficer\\.isActivePerson\\(\\)', 'org\\.kuali\\.kfs\\.sys\\.context\\.SpringContext\\.getBean\\(org\\.kuali\\.kfs\\.sys\\.service\\.KNSAuthorizationService\\.class\\)\\.isActive\\(fiscalOfficer\\)' ],
    [ 'user\\.isActivePerson\\(\\)', 'org\\.kuali\\.kfs\\.sys\\.context\\.SpringContext\\.getBean\\(org\\.kuali\\.kfs\\.sys\\.service\\.KNSAuthorizationService\\.class\\)\\.isActive\\(user\\)' ],
    [ 'personService\\.convertPersonToPerson\\(user\\)\\.isActivePerson\\(\\)', 'org\\.kuali\\.kfs\\.sys\\.context\\.SpringContext\\.getBean\\(org\\.kuali\\.kfs\\.sys\\.service\\.KNSAuthorizationService\\.class\\)\\.isActive\\(user\\)' ],    
    [ 'currentUser\\.getChartOfAccountsCode\\(\\)', 'org\\.kuali\\.kfs\\.sys\\.context\\.SpringContext\\.getBean\\(org\\.kuali\\.kfs\\.sys\\.service\\.KNSAuthorizationService\\.class\\)\\.getPrimaryChartOrganization\\(currentUser\\)\\.getChartOfAccountsCode\\(\\)' ],
    [ 'currentUser\\.getOrganizationCode\\(\\)', 'org\\.kuali\\.kfs\\.sys\\.context\\.SpringContext\\.getBean\\(org\\.kuali\\.kfs\\.sys\\.service\\.KNSAuthorizationService\\.class\\)\\.getPrimaryChartOrganization\\(currentUser\\)\\.getOrganizationCode\\(\\)' ],
    [ 'String chartCode = chartUser\\.getChartOfAccountsCode\\(\\)', 'String chartCode = org\\.kuali\\.kfs\\.sys\\.context\\.SpringContext\\.getBean\\(org\\.kuali\\.kfs\\.sys\\.service\\.KNSAuthorizationService\\.class\\)\\.getPrimaryChartOrganization\\(chartUser\\)\\.getChartOfAccountsCode\\(\\)' ],
    [ 'String orgCode = chartUser\\.getOrganizationCode\\(\\)', 'String orgCode = org\\.kuali\\.kfs\\.sys\\.context\\.SpringContext\\.getBean\\(org\\.kuali\\.kfs\\.sys\\.service\\.KNSAuthorizationService\\.class\\)\\.getPrimaryChartOrganization\\(chartUser\\)\\.getOrganizationCode\\(\\)' ],
    [ 'fUser\\.getChartOfAccountsCode\\(\\)', 'org\\.kuali\\.kfs\\.sys\\.context\\.SpringContext\\.getBean\\(org\\.kuali\\.kfs\\.sys\\.service\\.KNSAuthorizationService\\.class\\)\\.getPrimaryChartOrganization\\(fUser\\)\\.getChartOfAccountsCode\\(\\)' ],
    [ 'fUser\\.getOrganizationCode\\(\\)', 'org\\.kuali\\.kfs\\.sys\\.context\\.SpringContext\\.getBean\\(org\\.kuali\\.kfs\\.sys\\.service\\.KNSAuthorizationService\\.class\\)\\.getPrimaryChartOrganization\\(fUser\\)\\.getOrganizationCode\\(\\)' ],    
    [ 'personnel\\.\" \\+ person\\.getPrincipalId\\(\\) \\+ \"\\.status\"', 'personnel\\.\" \\+ person\\.getPersonUniversalIdentifier\\(\\) \\+ \"\\.status\"' ],
    
    [ 'SpringContext\\.getBean\\(org\\.kuali\\.rice\\.kim\\.service\\.PersonService\\.class\\)\\.getUsersGroups\\((.*)\\)', '\\(List<KimGroup>\\)org\\.kuali\\.rice\\.kim\\.service\\.KIMServiceLocator\\.getPersonService\\(\\)\\.getPersonGroups\\(\$1, \"KFS\"\\)' ],
    [ 'editBankGroup\\.hasMember\\(user\\)', 'org\\.kuali\\.rice\\.kim\\.service\\.KIMServiceLocator\\.getIdentityManagementService\\(\\)\\.isMemberOfGroup\\(user.getPrincipalId\\(\\), editBankGroup\\.getGroupId\\(\\)\\)' ],
    [ 'personService\\.isMember\\(user, purchasingGroup\\)', 'personService\\.isMemberOfGroup\\(user, \"KFS\", purchasingGroup\\)' ],
    [ 'user\\.isMember\\(group\\)', 'org\\.kuali\\.rice\\.kim\\.service\\.KIMServiceLocator\\.getPersonService\\(\\)\\.isMemberOfGroup\\(user, group\\.getGroupId\\(\\)\\)' ],

    [ 'SpringContext\\.getBean\\(PersonService\\.class\\)\\.getOrganizationByModuleId', 'org\\.kuali\\.kfs\\.sys\\.context\\.SpringContext\\.getBean\\(org\\.kuali\\.kfs\\.sys\\.service\\.KNSAuthorizationService\\.class\\)\\.getOrganizationByModuleId' ],
    [ 'personService\\.getOrganizationByModuleId', 'org\\.kuali\\.kfs\\.sys\\.context\\.SpringContext\\.getBean\\(org\\.kuali\\.kfs\\.sys\\.service\\.KNSAuthorizationService\\.class\\)\\.getOrganizationByModuleId' ],
    [ 'getKfsUserService\\(\\)\\.getOrganizationByModuleId', 'org\\.kuali\\.kfs\\.sys\\.context\\.SpringContext\\.getBean\\(org\\.kuali\\.kfs\\.sys\\.service\\.KNSAuthorizationService\\.class\\)\\.getOrganizationByModuleId' ],
    [ 'kfsUserService\\.getOrganizationByModuleId', 'org\\.kuali\\.kfs\\.sys\\.context\\.SpringContext\\.getBean\\(org\\.kuali\\.kfs\\.sys\\.service\\.KNSAuthorizationService\\.class\\)\\.getOrganizationByModuleId' ],    
    
    [ 'getCustomerInvoiceDetailFromOrganizationAccountingDefault\\(currentUniversityFiscalYear, org\\.kuali\\.kfs\\.sys\\.context\\.SpringContext\\.getBean\\(org\\.kuali\\.kfs\\.sys\\.service\\.KNSAuthorizationService\\.class\\)\\.getPrimaryChartOrganization\\(currentUser\\)\\.getChartOfAccountsCode\\(\\), org\\.kuali\\.kfs\\.sys\\.context\\.SpringContext\\.getBean\\(org\\.kuali\\.kfs\\.sys\\.service\\.KNSAuthorizationService\\.class\\)\\.getPrimaryChartOrganization\\(currentUser\\)\\.getOrganizationCode\\(\\)\\)', 'getCustomerInvoiceDetailFromOrganizationAccountingDefault\\(currentUniversityFiscalYear, currentUser\\.getChartOfAccountsCode\\(\\), currentUser\\.getOrganizationCode\\(\\)\\)' ],
    [ 'getCustomerInvoiceDetailFromCustomerInvoiceItemCode\\(invoiceItemCode, org\\.kuali\\.kfs\\.sys\\.context\\.SpringContext\\.getBean\\(org\\.kuali\\.kfs\\.sys\\.service\\.KNSAuthorizationService\\.class\\)\\.getPrimaryChartOrganization\\(currentUser\\)\\.getChartOfAccountsCode\\(\\), org\\.kuali\\.kfs\\.sys\\.context\\.SpringContext\\.getBean\\(org\\.kuali\\.kfs\\.sys\\.service\\.KNSAuthorizationService\\.class\\)\\.getPrimaryChartOrganization\\(currentUser\\)\\.getOrganizationCode\\(\\)\\)', 'getCustomerInvoiceDetailFromCustomerInvoiceItemCode\\(invoiceItemCode, currentUser\\.getChartOfAccountsCode\\(\\), currentUser\\.getOrganizationCode\\(\\)\\)' ],
    [ 'getNewAccountsReceivableDocumentHeader\\(org\\.kuali\\.kfs\\.sys\\.context\\.SpringContext\\.getBean\\(org\\.kuali\\.kfs\\.sys\\.service\\.KNSAuthorizationService\\.class\\)\\.getPrimaryChartOrganization\\(currentUser\\)\\.getChartOfAccountsCode\\(\\), org\\.kuali\\.kfs\\.sys\\.context\\.SpringContext\\.getBean\\(org\\.kuali\\.kfs\\.sys\\.service\\.KNSAuthorizationService\\.class\\)\\.getPrimaryChartOrganization\\(currentUser\\)\\.getOrganizationCode\\(\\)\\)', 'getNewAccountsReceivableDocumentHeader\\(currentUser\\.getChartOfAccountsCode\\(\\), currentUser\\.getOrganizationCode\\(\\)\\)' ],
    [ 'document\\.setBillByChartOfAccountCode\\(org\\.kuali\\.kfs\\.sys\\.context\\.SpringContext\\.getBean\\(org\\.kuali\\.kfs\\.sys\\.service\\.KNSAuthorizationService\\.class\\)\\.getPrimaryChartOrganization\\(currentUser\\)\\.getChartOfAccountsCode\\(\\)\\);', 'document\\.setBillByChartOfAccountCode\\(currentUser\\.getChartOfAccountsCode\\(\\)\\);' ],
    [ 'document\\.setBilledByOrganizationCode\\(org\\.kuali\\.kfs\\.sys\\.context\\.SpringContext\\.getBean\\(org\\.kuali\\.kfs\\.sys\\.service\\.KNSAuthorizationService\\.class\\)\\.getPrimaryChartOrganization\\(currentUser\\)\\.getOrganizationCode\\(\\)\\);', 'document\\.setBilledByOrganizationCode\\(currentUser\\.getOrganizationCode\\(\\)\\);' ],
    [ 'criteria\\.put\\(\"chartOfAccountsCode\", org\\.kuali\\.kfs\\.sys\\.context\\.SpringContext\\.getBean\\(org\\.kuali\\.kfs\\.sys\\.service\\.KNSAuthorizationService\\.class\\)\\.getPrimaryChartOrganization\\(currentUser\\)\\.getChartOfAccountsCode\\(\\)\\);', 'criteria\\.put\\(\"chartOfAccountsCode\", currentUser\\.getChartOfAccountsCode\\(\\)\\);' ],
    [ 'criteria\\.put\\(\"organizationCode\", org\\.kuali\\.kfs\\.sys\\.context\\.SpringContext\\.getBean\\(org\\.kuali\\.kfs\\.sys\\.service\\.KNSAuthorizationService\\.class\\)\\.getPrimaryChartOrganization\\(currentUser\\)\\.getOrganizationCode\\(\\)\\);', 'criteria\\.put\\(\"organizationCode\", currentUser\\.getOrganizationCode\\(\\)\\);' ],
    
    [ 'SpringContext\\.getBean\\(PersonService\\.class\\)\\.getPersonByPersonPayrollIdentifier\\(this\\.getEmplid\\(\\)\\)', '\\(Person\\) SpringContext\\.getBean\\(PersonService\\.class\\)\\.getPersonByExternalIdentifier\\(org\\.kuali\\.rice\\.kim\\.util\\.KimConstants\\.EMPLOYEE_EXT_ID_TYPE, this\\.getEmplid\\(\\)\\)\\.get\\(0\\)' ],
    [ 'personService\\.getPersonByPersonPayrollIdentifier\\(emplid\\)', '\\(Person\\) personService\\.getPersonByExternalIdentifier\\(org\\.kuali\\.rice\\.kim\\.util\\.KimConstants\\.EMPLOYEE_EXT_ID_TYPE, emplid\\)\\.get\\(0\\)' ],
    [ 'SpringContext\\.getBean\\(PersonService\\.class\\)\\.getPersonByPersonPayrollIdentifier\\(emplid\\)', '\\(Person\\) SpringContext\\.getBean\\(PersonService\\.class\\)\\.getPersonByExternalIdentifier\\(org\\.kuali\\.rice\\.kim\\.util\\.KimConstants\\.EMPLOYEE_EXT_ID_TYPE, emplid\\)\\.get\\(0\\)' ],
    [ 'SpringContext\\.getBean\\(PersonService\\.class\\)\\.getPersonByPersonPayrollIdentifier\\(id\\)', '\\(Person\\) SpringContext\\.getBean\\(PersonService\\.class\\)\\.getPersonByExternalIdentifier\\(org\\.kuali\\.rice\\.kim\\.util\\.KimConstants\\.EMPLOYEE_EXT_ID_TYPE, id\\)\\.get\\(0\\)' ],
    [ 'SpringContext\\.getBean\\(PersonService\\.class\\)\\.getPersonByPersonPayrollIdentifier\\(getSalaryExpenseTransferDocument\\(\\)\\.getEmplid\\(\\)\\)', '\\(Person\\) SpringContext\\.getBean\\(PersonService\\.class\\)\\.getPersonByExternalIdentifier\\(org\\.kuali\\.rice\\.kim\\.util\\.KimConstants\\.EMPLOYEE_EXT_ID_TYPE, getSalaryExpenseTransferDocument\\(\\)\\.getEmplid\\(\\)\\)\\.get\\(0\\)' ],
    
    [ 'user.isResponsibleForAccount\\(', 'org\\.kuali\\.kfs\\.sys\\.context\\.SpringContext\\.getBean\\(org\\.kuali\\.kfs\\.sys\\.service\\.KNSAuthorizationService\\.class\\)\\.isResponsibleForAccount\\(user\\.getPrincipalId\\(\\), ' ],
    [ 'currentUser.isResponsibleForAccount\\(', 'org\\.kuali\\.kfs\\.sys\\.context\\.SpringContext\\.getBean\\(org\\.kuali\\.kfs\\.sys\\.service\\.KNSAuthorizationService\\.class\\)\\.isResponsibleForAccount\\(currentUser\\.getPrincipalId\\(\\), ' ],
    
    [ 'user = SpringContext\\.getBean\\(PersonService\\.class\\)\\.convertPersonToPerson\\( GlobalVariables\\.getUserSession\\(\\)\\.getPerson\\(\\) \\);', 'user = GlobalVariables\\.getUserSession\\(\\)\\.getPerson\\(\\);' ],
    [ 'SpringContext\\.getBean\\(PersonService\\.class\\)\\.convertPersonToPerson\\( newDelegate\\.getAccountDelegate\\(\\) \\);', 'newDelegate\\.getAccountDelegate\\(\\);' ],    
    [ 'Person kfsUser = SpringContext\\.getBean\\(PersonService\\.class\\)\\.convertPersonToPerson\\(user\\);', 'Person kfsUser = user;' ],    
    [ 'Person fUser = SpringContext\\.getBean\\(PersonService\\.class\\)\\.convertPersonToPerson\\(user\\);', 'Person fUser = user;' ],    
    [ 'canAccessModule\\(personService\\.convertPersonToPerson\\(user\\)\\)', 'canAccessModule\\(user\\)' ],    
    [ 'getKfsUserService\\(\\)\\.convertPersonToPerson\\(user\\)', 'user' ],    
    [ 'user\\.setPersonUserIdentifier\\(StringUtils\\.upperCase\\(user\\.getPersonUserIdentifier\\(\\)\\)\\);', '\\/\\/user\\.setPersonUserIdentifier\\(StringUtils\\.upperCase\\(user\\.getPersonUserIdentifier\\(\\)\\)\\);' ],

    [ 'Person-principalName', 'PersonImpl-principalName' ],
    [ 'Person-personName', 'PersonImpl-name' ],
    [ 'Person-primaryDepartmentCode', 'PersonImpl-primaryDepartmentCode' ],
    [ 'Person-employeeStatusCode', 'PersonImpl-employeeStatusCode' ],
    [ 'Person-employeeTypeCode', 'PersonImpl-employeeTypeCode' ],
    [ 'Person-principalId', 'PersonImpl-principalId' ],
    [ 'Person-personPayrollIdentifier', 'PersonImpl-principalId' ],
    [ 'Person-personLastName', 'PersonImpl-lastName' ],
    [ 'Person-personFirstName', 'PersonImpl-firstName' ],
    [ 'Person-primaryDepartmentCode', 'PersonImpl-primaryDepartmentCode' ],
    [ 'Person-employeeStatusCode', 'PersonImpl-employeeStatusCode' ],
    [ 'Person-employeeTypeCode', 'PersonImpl-employeeTypeCode' ],
    
    // For some reason the replace below isn't replacing more than one occurance per line
    // So this is the hacky fix... could probably do some sort of /g fix below, but not
    // taking the time just yet to look into it.
    [ 'account(.*)User\\.personName', 'account\$1User\\.name' ],
    [ 'account(.*)User\\.personName', 'account\$1User\\.name' ],
    [ 'finCoaManagerUniversal\\.personName', 'finCoaManagerUniversal\\.name' ],
    [ 'finCoaManagerUniversal\\.personName', 'finCoaManagerUniversal\\.name' ],    
    [ 'accountDelegate\\.personName', 'accountDelegate\\.name' ],
    [ 'accountDelegate\\.personName', 'accountDelegate\\.name' ],    
    [ 'organizationManagerUniversal\\.personName', 'organizationManagerUniversal\\.name' ],
    [ 'organizationManagerUniversal\\.personName', 'organizationManagerUniversal\\.name' ],        
    [ 'hrmsPersonnelApproverUniversal\\.personName', 'hrmsPersonnelApproverUniversal\\.name' ],
    [ 'hrmsPersonnelApproverUniversal\\.personName', 'hrmsPersonnelApproverUniversal\\.name' ],
    [ 'fiscalApproverUniversal\\.personName', 'fiscalApproverUniversal\\.name' ],
    [ 'fiscalApproverUniversal\\.personName', 'fiscalApproverUniversal\\.name' ],
    [ 'projectManagerUniversal\\.personName', 'projectManagerUniversal\\.name' ],
    [ 'projectManagerUniversal\\.personName', 'projectManagerUniversal\\.name' ],
    [ 'person\\.personName', 'person\\.name' ],
    [ 'person\\.personName', 'person\\.name' ],
    [ 'vendorRestrictedPerson\\.personName', 'vendorRestrictedPerson\\.name' ],
    [ 'vendorRestrictedPerson\\.personName', 'vendorRestrictedPerson\\.name' ],
    [ 'vendorTaxChangePerson\\.personName', 'vendorTaxChangePerson\\.name' ],
    [ 'vendorTaxChangePerson\\.personName', 'vendorTaxChangePerson\\.name' ],
    [ 'documentInitiatorUser\\.personName', 'documentInitiatorUser\\.name' ],
    [ 'documentInitiatorUser\\.personName', 'documentInitiatorUser\\.name' ],
    [ 'financialDocumentInitiator\\.personName', 'financialDocumentInitiator\\.name' ],
    [ 'financialDocumentInitiator\\.personName', 'financialDocumentInitiator\\.name' ],
    [ 'name\\.personName', 'name\\.name' ],
    [ 'name\\.personName', 'name\\.name' ],
    [ 'borrowerPerson\\.personName', 'borrowerPerson\\.name' ],
    [ 'borrowerPerson\\.personName', 'borrowerPerson\\.name' ],
    [ 'assetRepresentative\\.personName', 'assetRepresentative\\.name' ],
    [ 'assetRepresentative\\.personName', 'assetRepresentative\\.name' ],
    [ 'personUniversal\\.personName', 'personUniversal\\.name' ],
    [ 'personUniversal\\.personName', 'personUniversal\\.name' ],
    [ 'projectDirector\\.personName', 'projectDirector\\.name' ],
    [ 'projectDirector\\.personName', 'projectDirector\\.name' ],
    [ 'user\\.personName', 'user\\.name' ],
    [ 'user\\.personName', 'user\\.name' ],
    [ 'lookupPerson\\.personName', 'lookupPerson\\.name' ],
    [ 'lookupPerson\\.personName', 'lookupPerson\\.name' ],
    [ 'projectDirector\\.personName', 'projectDirector\\.name' ],
    [ 'projectDirector\\.personName', 'projectDirector\\.name' ],
    [ 'emailAddress\\.personName', 'emailAddress\\.name' ],
    [ 'emailAddress\\.personName', 'emailAddress\\.name' ],
    
    [ '-personName', '-name' ],
    [ 'getPersonName\\(\\)', 'getName\\(\\)' ],
    [ 'setPersonName\\(', 'setName\\(' ],
    [ 'getPersonEmailAddress\\(\\)', 'getEmailAddress\\(\\)' ],
    [ 'setPersonEmailAddress\\(', 'setEmailAddress\\(' ],
    [ 'getPersonFirstName\\(\\)', 'getFirstName\\(\\)' ],
    [ 'setPersonFirstName\\(', 'setFirstName\\(' ],
    [ 'getPersonLastName\\(\\)', 'getLastName\\(\\)' ],
    [ 'setPersonLastName\\(', 'setLastName\\(' ],    
    [ 'getPersonUniversalIdentifier\\(\\)', 'getPrincipalId\\(\\)' ],
    [ 'setPersonUniversalIdentifier\\(', 'setPrincipalId\\(' ],
    [ 'getPersonUserIdentifier\\(\\)', 'getPrincipalName\\(\\)' ],
    [ 'setPersonUserIdentifier\\(', 'setPrincipalName\\(' ],
    [ 'employee\\.personName', 'employee\\.name' ],
    [ 'employee\\.personName', 'employee\\.name' ],
    [ 'ledgerPerson\\.personName', 'ledgerPerson\\.name' ],  
    [ 'ledgerPerson\\.personName', 'ledgerPerson\\.name' ],
    // Hoping we don't need to do these three because it changes a lot of comments 
    //[ 'getEmployee\\(\\)', 'getPerson\\(\\)' ],
    //[ 'setEmployee\\(', 'setPerson\\(' ],
    //[ '\\bemployee\\b', 'person' ],    
    //[ 'kfs:person', 'kfs:employee' ],
    [ 'FinancialSystemPersonService\\.js', 'PersonService\\.js' ],
 
    [ '\\bKULUSER\\b', 'kuluser' ],
    [ '\\bKHUNTLEY\\b', 'khuntley' ],
    [ '\\bGHATTEN\\b', 'ghatten' ],
    [ '\\bSTROUD\\b', 'stroud' ],
    [ '\\bDFOGLE\\b', 'dfogle' ],
    [ '\\bRJWEISS\\b', 'rjweiss' ],
    [ '\\bRORENFRO\\b', 'rorenfro' ],
    [ '\\bHSCHREIN\\b', 'hschrein' ],
    [ '\\bHSOUCY\\b', 'hsoucy' ],
    [ '\\bLRAAB\\b', 'lraab' ],
    [ '\\bJHAVENS\\b', 'jhavens' ],
    [ '\\bKCOPLEY\\b', 'kcopley' ],
    [ '\\bMHKOZLOW\\b', 'mhkozlow' ],
    [ '\\bINEFF\\b', 'ineff' ],
    [ '\\bVPUTMAN\\b', 'vputman' ],
    [ '\\bCSWINSON\\b', 'cswinson' ],
    [ '\\bMYLARGE\\b', 'mylarge' ],
    [ '\\bRRUFFNER\\b', 'rruffner' ],
    [ '\\bSEASON\\b', 'season' ],
    [ '\\bDQPERRON\\b', 'dqperron' ],
    [ '\\bAATWOOD\\b', 'aatwood' ],
    [ '\\bPARKE\\b', 'parke' ],
    [ '\\bAPPLETON\\b', 'appleton' ],
    [ '\\bTWATSON\\b', 'twatson' ],
    [ '\\bBUTT\\b', 'butt' ],
    [ '\\bJKITCHEN\\b', 'jkitchen' ],

]

excludedDirectories = [
   '.svn',
   'CVS',
   'kim',
   'target',
   '.externalToolBuilders',
   '.settings'
]

backupExtension = ".backup"

createBackups = false;
deleteBackups = false;
testMode = false;
convertFiles = true;
deleteObseleteFiles = true;
restoreBackups = false;

def processDir( dir ) {
    println "Processing Directory: " + dir
    def files = new File(dir).list()
    files.each {
        String fileName ->
        if ( !excludedDirectories.contains( fileName ) && !fileName.endsWith( backupExtension ) ) {
            File file = new File(dir,fileName)
            //println "Processing File: " + file.getAbsolutePath()
            if ( file.isDirectory() ) {
                processDir( file.getAbsolutePath() )
            } else {
                String originalFileText = file.text
                String convertedFileText = originalFileText
                replacements.each {
                    fromStr, toStr -> 
                    //println "Converting: " + fromStr + " to " + toStr
                    convertedFileText = convertedFileText.replaceAll( fromStr, toStr )
                }
                if ( !convertedFileText.equals( originalFileText ) ) {
                    // if the file is about to be changed, backup the original
                    backupFile( file );
                    if ( !testMode ) {
                        file.delete();
                        file << convertedFileText;
                        file << "\n";
                        println "Changed File: " + file.getAbsolutePath();
                    } else {
                        println "Changed File Contents:"
                        println convertedFileText
                    }
                }
            }
        }
    }
}

def backupFile( File file ) {
    if ( createBackups ) {
        File backupFile = new File(file.getAbsolutePath() + backupExtension)
        if ( !backupFile.exists() ) {
            if ( !testMode ) {
                backupFile << file.text
                backupFile << "\n"
            } else {
                println "Would have backed up to: " + backupFile.getAbsolutePath()
            }
        }
    }
}

def deleteBackupsInDir( dir ) {
    println "Deleting Backups from Directory: " + dir
    def files = new File(dir).list()
    files.each {
        String fileName ->
        File backupFile = new File(dir,fileName)
        if ( !excludedDirectories.contains( fileName ) ) {
            if ( backupFile.isDirectory() ) {
                deleteBackupsInDir( backupFile.getAbsolutePath() )
            } else {
                if ( fileName.endsWith( backupExtension ) ) {
                    if ( !testMode ) {
                        backupFile.delete()
                    } else {
                        println "Would have deleted backup file: " + backupFile.getAbsolutePath()
                    }
                }
            }
        }
    }   
}

def restoreBackupsInDir( dir ) {
    println "Restoring Backups in Directory: " + dir
    def files = new File(dir).list()
    files.each {
        String fileName ->
        File backupFile = new File(dir,fileName)
        if ( !excludedDirectories.contains( fileName ) ) {
            if ( backupFile.isDirectory() ) {
                restoreBackupsInDir( backupFile.getAbsolutePath() )
            } else {
                if ( fileName.endsWith( backupExtension ) ) {
                    if ( !testMode ) {
                        File originalFile = new File( backupFile.getAbsolutePath().substring( 0, backupFile.getAbsolutePath().length() - 7 ) )
                        if ( originalFile.exists() ) {
                            originalFile.delete();
                        }
                        backupFile.renameTo( originalFile )
                        println "Restored: " + originalFile.getAbsolutePath()
                    } else {
                        println "Would have restored backup file: " + backupFile.getAbsolutePath()
                    }
                }
            }
        }
    }   
}

/** Initial tickoff of the processing **/
if ( convertFiles ) {   
    println "***** Converting Files *****"
    sourceDirectories.each {
        dir -> 
            processDir( baseDir + dir )
    }
}

if ( deleteObseleteFiles ) {
    println "******* Deleting specified files ******"
    filesToDelete.each {
        fileName ->
            File file = new File( baseDir + fileName )
            backupFile( file );
            println "Deleting File: " + file.getAbsolutePath();
            file.delete();
    }
}


if ( restoreBackups ) {
    println "***** Restoring Backup Files *****"
    sourceDirectories.each {
        dir ->
            restoreBackupsInDir( baseDir + dir )
    }
}

if ( deleteBackups ) {
    println "***** Deleting Backup Files *****"
    sourceDirectories.each {
        dir -> 
            deleteBackupsInDir( baseDir + dir )
    }
}
