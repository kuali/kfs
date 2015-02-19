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
package org.kuali.kfs.module.external.kc.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.integration.cg.ContractsAndGrantsProjectDirector;
import org.kuali.kfs.module.external.kc.businessobject.Award;
import org.kuali.kfs.module.external.kc.service.ExternalizableBusinessObjectService;
import org.kuali.rice.krad.bo.ExternalizableBusinessObject;

public class AwardProjectDirectorServiceImpl implements ExternalizableBusinessObjectService {

    private ExternalizableBusinessObjectService awardService;

    @Override
    public ExternalizableBusinessObject findByPrimaryKey(Map primaryKeys) {
        Award award = (Award) awardService.findByPrimaryKey(primaryKeys);
        if (award == null) {
            return null;
        } else {
            return award.getAwardPrimaryProjectDirector();
        }
    }

    @Override
    public Collection findMatching(Map fieldValues) {
        Collection<Award> awards = awardService.findMatching(fieldValues);
        List<ContractsAndGrantsProjectDirector> directors = new ArrayList<ContractsAndGrantsProjectDirector>();
        for (Award award : awards) {
            directors.add(award.getAwardPrimaryProjectDirector());
        }
        return directors;
    }

    protected ExternalizableBusinessObjectService getAwardService() {
        return awardService;
    }

    public void setAwardService(ExternalizableBusinessObjectService awardService) {
        this.awardService = awardService;
    }
}
