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
package org.kuali.kfs.module.bc.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.document.web.struts.BudgetExpansionForm;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kns.web.struts.form.KualiForm;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.UrlFactory;

/**
 * Provides helper methods for building URLs to various budget actions.
 */
public class BudgetUrlUtil {

    /**
     * Builds url for temp list action.
     * 
     * @param mapping struts action mapping
     * @param form BudgetExpansionForm
     * @param tempListMode mode for temp list action
     * @param tempListLookupClass class name to lookup
     * @param additionalParameters appended to the url or replace default
     * @return string url
     */
    public static String buildTempListLookupUrl(ActionMapping mapping, BudgetExpansionForm form, Integer tempListMode, String tempListLookupClass, Map<String, String> additionalParameters) {
        Map<String, String> parameters = new HashMap<String, String>();

        parameters.put(KRADConstants.DOC_FORM_KEY, GlobalVariables.getUserSession().addObjectWithGeneratedKey(form, BCConstants.FORMKEY_PREFIX));
        parameters.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, tempListLookupClass);
        parameters.put(KFSConstants.HIDE_LOOKUP_RETURN_LINK, "true");
        parameters.put(KFSConstants.SUPPRESS_ACTIONS, "false");
        parameters.put(BCConstants.SHOW_INITIAL_RESULTS, "true");
        parameters.put(BCConstants.TempListLookupMode.TEMP_LIST_LOOKUP_MODE, Integer.toString(tempListMode));
        
        if (additionalParameters != null) {
            for (String parameterKey : additionalParameters.keySet()) {
                parameters.put(parameterKey, additionalParameters.get(parameterKey));
            }
        }

        return buildBudgetUrl(mapping, form, BCConstants.ORG_TEMP_LIST_LOOKUP, parameters);
    }

    /**
     * Builds a budget URL setting default parameters.
     * 
     * @param mapping struts action mapping
     * @param form BudgetExpansionForm
     * @param actionPath url path for requested action
     * @param additionalParameters appended to the url or replace default
     * @return string url
     */
    public static String buildBudgetUrl(ActionMapping mapping, BudgetExpansionForm form, String actionPath, Map<String, String> additionalParameters) {
        Properties parameters = new Properties();
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.START_METHOD);
        parameters.put(BCConstants.RETURN_FORM_KEY, GlobalVariables.getUserSession().addObjectWithGeneratedKey(form, BCConstants.FORMKEY_PREFIX));

        String basePath = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSConstants.APPLICATION_URL_KEY);
        parameters.put(KFSConstants.BACK_LOCATION, basePath + mapping.getPath() + ".do");
        parameters.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, form.getUniversityFiscalYear().toString());
        parameters.put(KFSPropertyConstants.KUALI_USER_PERSON_UNIVERSAL_IDENTIFIER, GlobalVariables.getUserSession().getPerson().getPrincipalId());
        
        if (StringUtils.isNotEmpty(((KualiForm) form).getAnchor())) {
            parameters.put(BCConstants.RETURN_ANCHOR, ((KualiForm) form).getAnchor());
        }

        if (additionalParameters != null) {
            for (String parameterKey : additionalParameters.keySet()) {
                parameters.put(parameterKey, additionalParameters.get(parameterKey));
            }
        }

        return UrlFactory.parameterizeUrl(basePath + "/" + actionPath, parameters);
    }
}

