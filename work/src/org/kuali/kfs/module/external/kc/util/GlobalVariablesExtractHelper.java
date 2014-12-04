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
package org.kuali.kfs.module.external.kc.util;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.krad.util.ErrorMessage;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;

/**
 * This class will help extract the error messages from GlobalVariables object and creates a list of string.
 */
public class GlobalVariablesExtractHelper {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(GlobalVariablesExtractHelper.class);

    /**
     * Extracts errors for error report writing.
     *
     * @return a list of error messages
     */
    public static void insertError(String message, String param) {
        MessageMap errorMap = GlobalVariables.getMessageMap();
        errorMap.putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_CUSTOM, message + param);
    }

    public static List<String> extractGlobalVariableErrors() {
        List<String> result = new ArrayList<String>();

        MessageMap errorMap = GlobalVariables.getMessageMap();

        // Set<String> errorKeys = errorMap.keySet(); // deprecated
        Set<String> errorKeys = errorMap.getAllPropertiesWithErrors();
        List<ErrorMessage> errorMessages = null;
        Object[] messageParams;
        String errorKeyString;
        String errorString;

        for (String errorProperty : errorKeys) {
            // errorMessages = (List<ErrorMessage>) errorMap.get(errorProperty); // deprecated
            errorMessages = errorMap.getErrorMessagesForProperty(errorProperty);
            LOG.debug("error Messages :::: " + errorMessages.toString());
            for (ErrorMessage errorMessage : errorMessages) {
                errorKeyString = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(errorMessage.getErrorKey());
                messageParams = errorMessage.getMessageParameters();
                LOG.debug("message parameters:::  " + messageParams);
                LOG.debug("errorKeyString :::: " + errorKeyString);
                // MessageFormat.format only seems to replace one
                // per pass, so I just keep beating on it until all are gone.
                if (StringUtils.isBlank(errorKeyString)) {
                    errorString = errorMessage.getErrorKey();
                }
                else {
                    errorString = errorKeyString;
                }
                LOG.debug(errorString);
                if (errorString.matches("^.*\\{\\d\\}.*$")) {
                    errorString = MessageFormat.format(errorString, messageParams);
                }
                result.add(errorString);
            }
        }

        // clear the stuff out of global vars, as we need to reformat it and put it back
        GlobalVariables.clear();
        return result;
    }

    public static String replaceTokens(String line, String ... replacements) {
        int i = 0;
        for (String err : replacements) {
            String repl = "{" + String.valueOf(i++) + "}";
            line = StringUtils.replaceOnce(line, repl, err);
        }
        return line;
    }

}
