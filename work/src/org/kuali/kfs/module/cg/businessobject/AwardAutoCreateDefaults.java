/*
 * Copyright 2005 The Kuali Foundation
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
package org.kuali.kfs.module.cg.businessobject;

import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.kfs.integration.cg.ContractsAndGrantsUnit;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.bo.Inactivateable;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.service.KualiModuleService;
import org.kuali.rice.kns.util.TypedArrayList;

/**
 * Award Auto Create Defaults.
 */
public class AwardAutoCreateDefaults extends PersistableBusinessObjectBase implements Inactivateable {

    private Integer awardDefaultId;
    private String kcUnit;
    private String kcUnitName;
    private List<AwardProjectDirector> awardProjectDirectors;
    private boolean active;

    private List<AwardAccountDefaults> awardAccountDefaults;
	private ContractsAndGrantsUnit unitDTO;

    public AwardAutoCreateDefaults() {
        awardAccountDefaults = new TypedArrayList(AwardAccountDefaults.class);
    }

    /**
     * @return
     */
    public Integer getAwardDefaultId() {
        return awardDefaultId;
    }

    /**
     * @param awardDefaultId
     */
    public void setAwardDefaultId(Integer awardDefaultId) {
        this.awardDefaultId = awardDefaultId;
    }

    /**
     * @return
     */
    public String getKcUnit() {
        return kcUnit;
    }

    /**
     * @param KcUnit
     */
    public void setKcUnit(String kcUnit) {
        this.kcUnit = kcUnit;
    }

	public String getKcUnitName() {
        return kcUnitName;
    }
    
    public void setKcUnitName(String kcUnitName) {
        this.kcUnitName = kcUnitName;
    }
	
    /**
     * @return
     */
    public List<AwardProjectDirector> getAwardProjectDirectors() {
        return awardProjectDirectors;
    }

    /**
     * @param awardProjectDirectors
     */
    public void setAwardProjectDirectors(List<AwardProjectDirector> awardProjectDirectors) {
        this.awardProjectDirectors = awardProjectDirectors;
    }

    /**
     * @see org.kuali.rice.kns.bo.Inactivateable#isActive()
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @see org.kuali.rice.kns.bo.Inactivateable#setActive(boolean)
     */
    public void setActive(boolean arg0) {
        this.active = active;

    }

    /**
     * @return
     */
    public List<AwardAccountDefaults> getAwardAccountDefaults() {
        return awardAccountDefaults;
    }

    /**
     * @param awardAccountDefaults
     */
    public void setAwardAccountDefaults(List<AwardAccountDefaults> awardAccountDefaults) {
        this.awardAccountDefaults = awardAccountDefaults;
    }

    /**
     * @return
     */
    public ContractsAndGrantsUnit getUnitDTO() {
        return unitDTO = (ContractsAndGrantsUnit) SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsUnit.class).retrieveExternalizableBusinessObjectIfNecessary(this, unitDTO, "unitDTO");        
    }

    /**
     * @param unitDTO
     */
    public void setUnitDTO(ContractsAndGrantsUnit unitDTO) {
        this.unitDTO = unitDTO;
    }
	
    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("awardDefaultId", awardDefaultId);
        m.put("kcUnit", kcUnit);
        m.put("active", active);
        return m;
    }
}
