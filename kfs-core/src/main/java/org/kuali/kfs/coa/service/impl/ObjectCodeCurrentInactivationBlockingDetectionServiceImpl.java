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
package org.kuali.kfs.coa.service.impl;

import java.util.Map;

import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.datadictionary.InactivationBlockingMetadata;
import org.kuali.rice.krad.service.impl.InactivationBlockingDetectionServiceImpl;

/**
 * This class overrides the base Inactivation Blocking Detection Service.  It is intended to be used with ObjectCode or ObjectCodeCurrent BOs when they
 * represent the BLOCKED bo.
 */
public class ObjectCodeCurrentInactivationBlockingDetectionServiceImpl extends InactivationBlockingDetectionServiceImpl {
    private UniversityDateService universityDateService;
    
    @Override
    protected Map<String, String> buildInactivationBlockerQueryMap(BusinessObject blockedBo, InactivationBlockingMetadata inactivationBlockingMetadata) {
        ObjectCode blockedObjectCode = (ObjectCode) blockedBo;
        if (universityDateService.getCurrentFiscalYear().equals(blockedObjectCode.getUniversityFiscalYear())) {
            return super.buildInactivationBlockerQueryMap(blockedBo, inactivationBlockingMetadata);
        }
        return null;
        
    }

    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }

}
