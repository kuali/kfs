/*
 * Copyright 2013 The Kuali Foundation.
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
package org.kuali.rice.krad.datadictionary.validation.fieldlevel;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.coreservice.framework.CoreFrameworkServiceLocator;
import org.kuali.rice.coreservice.framework.parameter.ParameterConstants;
import org.kuali.rice.krad.datadictionary.validation.FieldLevelValidationPattern;
import org.kuali.rice.krad.util.KRADConstants;

public class SecureFieldDataValidationPattern extends FieldLevelValidationPattern  {
    @Override
    protected String getPatternTypeName()  {
      Boolean warnForSensitiveData = CoreFrameworkServiceLocator.getParameterService().getParameterValueAsBoolean(
                KRADConstants.KNS_NAMESPACE, ParameterConstants.ALL_COMPONENT,
                KFSConstants.SECURE_FIELD_DATA_WARNING_IND);

      if(warnForSensitiveData != null && warnForSensitiveData){
          return "secureFieldDataValidationPattern";
      }else{
          return "noSecureFieldDataValidationPattern";
      }

    }
}
