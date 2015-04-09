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
package org.kuali.kfs.sys.service.impl;

import org.kuali.kfs.sys.batch.Step;
import org.kuali.rice.krad.service.impl.KualiModuleServiceImpl;

public class KfsKualiModuleServiceImpl extends KualiModuleServiceImpl {


    /**
     * KFS has additional components related to our steps.  So, when resolving the component,
     * we need to allow objects of the "Step" type to be used as well.
     *
     * @see org.kuali.rice.krad.service.impl.KualiModuleServiceImpl#getComponentCode(java.lang.Class)
     */
    @Override
    public String getComponentCode(Class<?> documentClass) {
        if (documentClass == null) {
            throw new IllegalArgumentException("documentClass must not be null");
        }
        if ( Step.class.isAssignableFrom(documentClass) ) {
            return documentClass.getSimpleName();
        }
        // otherwise, we can restore the default behavior
        return super.getComponentCode(documentClass);
    }
}
