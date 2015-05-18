/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.kns.web.struts.config;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.config.ControllerConfig;
import org.kuali.rice.coreservice.framework.CoreFrameworkServiceLocator;
import org.kuali.rice.krad.util.KRADConstants;

/**
 * Kuali customization of ControllerConfig which delegates max upload size lookup to
 * parameter service: KRADConstants.KNS_NAMESPACE, KRADConstants.DetailTypes.ALL_DETAIL_TYPE, KRADConstants.MAX_UPLOAD_SIZE_PARM_NM
 * The value must be a string compatible with Struts maxFileSize attribute.
 */
public class KualiControllerConfig extends ControllerConfigWrapper {
    public KualiControllerConfig(ControllerConfig config) {
        super(config);
    }

    /**
     * Returns the global max file upload size, which is dynamically derived from the Rice parameter service.
     * This technically breaks the implicit contract in ControllerConfig that the config is frozen after startup.
     * @return the global max file upload size
     */
    @Override
    public String getMaxFileSize() {
        String maxFileSize = CoreFrameworkServiceLocator.getParameterService().getParameterValueAsString(KRADConstants.KNS_NAMESPACE, KRADConstants.DetailTypes.ALL_DETAIL_TYPE, KRADConstants.MAX_UPLOAD_SIZE_PARM_NM);
        if (StringUtils.isNotBlank(maxFileSize)) {
            return maxFileSize;
        }
        return super.getMaxFileSize();
    }

    /**
     * Overridden to throw an UnsupportedOperationException.  Once our KualiControllerConfig is
     * in place, it does not make sense to support this setter.
     */
    @Override
    public void setMaxFileSize(String s) {
        throw new UnsupportedOperationException("Cannot set max file size through KualiControllerConfig");
    }
}
