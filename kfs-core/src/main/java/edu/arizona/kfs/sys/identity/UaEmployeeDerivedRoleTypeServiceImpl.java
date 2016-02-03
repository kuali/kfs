package edu.arizona.kfs.sys.identity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.identity.EmployeeDerivedRoleTypeServiceImpl;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.coreservice.api.parameter.Parameter;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kim.api.identity.affiliation.EntityAffiliation;
import org.kuali.rice.kim.api.identity.entity.EntityDefault;

import edu.arizona.kfs.sys.UaKFSConstants;
import edu.arizona.kim.eds.UaEdsConstants;

public class UaEmployeeDerivedRoleTypeServiceImpl extends EmployeeDerivedRoleTypeServiceImpl {

	/**
	 * The basic role that grants users access to KFS. It gives users the
	 * ability to initiate most documents and use inquiries and search screens.
	 */
	private static final String ROLE_54_TITLE = KFSConstants.SysKimApiConstants.KFS_USER_ROLE_NAME;

	/**
	 * A role that uses the Affiliation Type and Employee Status on a Principal
	 * record to determine if a user is an active faculty or staff employee.
	 * These users can initiate some KFS-PURAP documents and inquire into
	 * certain KFS screens, including Balance Inquiry screens.
	 */
	private static final String ROLE_32_TITLE = KFSConstants.SysKimApiConstants.ACTIVE_FACULTY_OR_STAFF_KIM_ROLE_NAME;

	/**
	 * This role requires a Principal to have role 32 and the 'Professional'
	 * designation. The professional designation is determined by a Principal
	 * have one or more active affiliations found in the EDS_PROFESSIONAL_AFFS
	 * parameter. These users are allowed to be Account Supervisors or Account
	 * Managers on Accounts.
	 */
	private static final String ROLE_33_TITLE = KFSConstants.SysKimApiConstants.ACTIVE_PROFESSIONAL_EMPLOYEE_KIM_ROLE_NAME;

	/**
	 * To be granted role 93, a Principal must have role 32 and role 54. These
	 * users are allowed to be fiscal Officers on Accounts.
	 */
	private static final String ROLE_93_TITLE = KFSConstants.SysKimApiConstants.ACTIVE_EMPLOYEE_AND_KFS_USER_KIM_ROLE_NAME;

	/**
	 * To be granted role 94, a Principal must have role 33 and role 54. These
	 * users are allowed to be fiscal Officers on Accounts.
	 */
	private static final String ROLE_94_TITLE = KFSConstants.SysKimApiConstants.ACTIVE_PROFESSIONAL_EMPLOYEE_AND_KFS_USER_KIM_ROLE_NAME;

	/**
	 * This role is a non-derived role that mimics role 32, and is used for
	 * individuals that need KFS base access but do not qualify for role 32,
	 * e.g. 'DCC: Independant Contractors' do not qualify for role 32, but some
	 * still need KFS access.
	 */
	private static final String ROLE_11173_TITLE = UaKFSConstants.BASE_FINANCIAL_SYSTEM_USER_KIM_ROLE_NAME;

	private static final String EDS_CONSTANTS_BEAN_NAME = "edsConstants";

	private ParameterService parameterService;
	private UaEdsConstants edsConstants;

	@Override
	public boolean hasDerivedRole(String principalId, List<String> groupIds, String namespaceCode, String roleName, Map<String, String> qualification) {
		boolean retval = false;

		// Nothing to go off of, reject
		if (StringUtils.isNotBlank(roleName) && StringUtils.isNotBlank(principalId)) {

			// Work through our cases
			EntityDefault entity = getIdentityService().getEntityDefaultByPrincipalId(principalId);
			if (entity == null || entity.getEmployment() == null) {
				retval = false;
			} else if (hasRestrictedEmployeeType(entity)) {
				retval = false;
			} else if (roleName.equals(ROLE_32_TITLE)) {
				// Note: this will return true if the person has role 11173
				retval = hasRole32(entity, principalId);
			} else if (roleName.equals(ROLE_33_TITLE)) {
				retval = hasRole33(entity, principalId);
			} else if (roleName.equals(ROLE_93_TITLE)) {
				retval = hasRole93(entity, principalId);
			} else if (roleName.equals(ROLE_94_TITLE)) {
				retval = hasRole94(entity, principalId);
			}
		}
		return retval;
	}

	private boolean hasRole32(EntityDefault entity, String principalId) {
		// Role 32: has a respected EDS affiliation, and an active status,
		// *or* has role 11173. Role 11173 is the manually assigned role
		// for when we need to override role 32 requirments, e.g. our
		// KATT DCC's are of the unrespected type 000975, and would not
		// have KFS access, so we can just assign this role, and they're in
		return (hasRespectedAffiliation(entity) && hasActiveStatus(entity)) || hasRole(principalId, ROLE_11173_TITLE);
	}

	private boolean hasRole33(EntityDefault entity, String principalId) {
		// Role 33: has role 32, and has a designated 'professional' affiliation
		return hasRole32(entity, principalId) && hasProfessionalDesignation(entity);
	}

	private boolean hasRole93(EntityDefault entity, String principalId) {
		// Role 93: has role 32 and 54
		return hasRole32(entity, principalId) && hasRole54(principalId);
	}

	private boolean hasRole94(EntityDefault entity, String principalId) {
		// Role 94: has roles 33 and 54
		return hasRole33(entity, principalId) && hasRole54(principalId);
	}

	private boolean hasRole54(String principalId) {
		return hasRole(principalId, ROLE_54_TITLE);
	}

	/*
	 * Verify with role service that principal has role
	 */
	private boolean hasRole(String principalId, String roleName) {
		String namespaceCode = KFSConstants.CoreModuleNamespaces.KFS;
		List<String> roleIds = new ArrayList<String>(1);
		String roleId = getRoleService().getRoleIdByNamespaceCodeAndName(namespaceCode, roleName);
		roleIds.add(roleId);
		return getRoleService().principalHasRole(principalId, roleIds, null);
	}

	/*
	 * Verify principal is active. To be active, an employee record must have
	 * its active flag set true *and* its active indicator must be in the
	 * defined set of active indicators identified in the KFS param
	 * 'EDS_EMPLOYEE_ACTIVE_INDICATORS'.
	 * 
	 * These two variables are set by the class EdsPrincipalDaoImpl using
	 * information collected from an EDS interface.
	 */
	private boolean hasActiveStatus(EntityDefault entity) {

		// Compare status code against KFS params
		Set<String> activeIndicators = getValueSetForParameter(getEdsConstants().getEdsOrderedActiveStatusIndicatorsParamKey());
		String statusCode = entity.getEmployment().getEmployeeStatus().getCode();
		boolean statusCodeIsActive = activeIndicators.contains(statusCode);

		// Get status flag (is redundant w/ statusCode comparison, do it to be
		// safe)
		boolean statusFlagIsActive = entity.getEmployment().isActive();

		// Return combo of the two
		return statusCodeIsActive && statusFlagIsActive;

	}

	/**
	 * An active affiliation is set on an employee by EdsPrincipalDaoImpl from
	 * an EDS interface. The set of active affiliations are defined in two KFS
	 * parameters: 1. EDS_EMPLOYEE_ACTIVE_AFFILIATIONS 2.
	 * EDS_DCC_ACTIVE_AFFILIATIONS
	 */
	private boolean hasRespectedAffiliation(EntityDefault entity) {

		// Ensure they have at least one affiliation
		List<EntityAffiliation> affInfoList = entity.getAffiliations();
		if (affInfoList == null || affInfoList.size() == 0) {
			return false;
		}

		// Collect all of our affiliations from KFS params
		Set<String> respectedAffStrings = getValueSetForParameter(getEdsConstants().getEdsRespectedAndOrderedAffsParamKey());

		// Do actual comparison
		for (EntityAffiliation affInfo : affInfoList) {
			String affString = affInfo.getAffiliationType().getCode();
			if (respectedAffStrings.contains(affString)) {
				return true;
			}
		}

		return false;

	}

	/**
	 * The 'professional' designation of a user is determined by the user's
	 * affiliation. The affiliations designated as 'professional' are defined in
	 * the KFS param 'EDS_PROFESSIONAL_AFFILIATIONS'.
	 */
	private boolean hasProfessionalDesignation(EntityDefault entity) {
		// Collect all of our affiliations from KFS params
		Set<String> allProfessionalAffiliations = getValueSetForParameter(getEdsConstants().getEdsProfessionalAffsParamKey());

		// Ensure they have at least one affiliation
		List<EntityAffiliation> affInfoList = entity.getAffiliations();
		if (affInfoList == null || affInfoList.size() == 0) {
			return false;
		}

		// Do actual comparison
		for (EntityAffiliation affInfo : affInfoList) {
			if (allProfessionalAffiliations.contains(affInfo.getAffiliationType().getCode())) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Restrict employees from access if their employee type code is in the
	 * restricted list. The list of resticted emplyees types live in the kfs
	 * 'EDS_RESTRICTED_EMPLOYEE_TYPES' param. As of release 58, the codes
	 * included 'J' and 'U', which are highschool student workers, and workers
	 * of the 'unknown' type.
	 */
	private boolean hasRestrictedEmployeeType(EntityDefault entity) {
		String employeeType = entity.getEmployment().getEmployeeType().getCode();
		Set<String> restrictedEmployeeTypes = getValueSetForParameter(getEdsConstants().getEdsRestrictedEmployeeTypesParamKey());
		return restrictedEmployeeTypes.contains(employeeType);
	}

	/**
	 * Method to obtain a set of values associated with a KFS paramater key.
	 */
	private Set<String> getValueSetForParameter(String parameterKey) {
		String listAsCommaString = getStringForParameter(parameterKey);
		String[] listAsArray = listAsCommaString.split(getEdsConstants().getKfsParamDelimiter());
		Set<String> resultSet = new HashSet<String>();
		for (String result : listAsArray) {
			resultSet.add(result);
		}
		return resultSet;
	}

	private String getStringForParameter(String parameterKey) {
		String namespaceCode = getEdsConstants().getParameterNamespaceCode();
		String detailTypeCode = getEdsConstants().getParameterDetailTypeCode();
		Parameter parameter = getParameterService().getParameter(namespaceCode, detailTypeCode, parameterKey);
		if (parameter == null) {
			String message = String.format("ParameterService returned null for parameterKey: '%s', namespaceCode: '%s', detailTypeCode: '%s'", parameterKey, namespaceCode, detailTypeCode);
			throw new RuntimeException(message);
		}
		return parameter.getValue();
	}

	private ParameterService getParameterService() {
		if (parameterService == null) {
			parameterService = SpringContext.getBean(ParameterService.class);
		}
		return parameterService;
	}

	private UaEdsConstants getEdsConstants() {
		if (this.edsConstants == null) {
			setEdsConstants(GlobalResourceLoader.<UaEdsConstants> getService(EDS_CONSTANTS_BEAN_NAME));
		}
		return edsConstants;
	}
	public void setEdsConstants( UaEdsConstants edsConstants ) {
		this.edsConstants = edsConstants;
	}

}
