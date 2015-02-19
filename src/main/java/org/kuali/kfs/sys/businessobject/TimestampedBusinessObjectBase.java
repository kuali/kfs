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
package org.kuali.kfs.sys.businessobject;

import java.sql.Timestamp;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.util.GlobalVariables;

public abstract class TimestampedBusinessObjectBase extends PersistableBusinessObjectBase implements TimestampedBusinessObject {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TimestampedBusinessObjectBase.class);

    private Timestamp lastUpdate;
    private String lastUpdateUserId;

    /**
     * @see org.kuali.kfs.sys.businessobject.TimestampedBusinessObject#getLastUpdate()
     */
    public Timestamp getLastUpdate() {
        return this.lastUpdate;
    }

    /**
     * @see org.kuali.kfs.sys.businessobject.TimestampedBusinessObject#getLastUpdateUser()
     */
    public Person getLastUpdateUser() {
        Person user = null;
        if (StringUtils.isNotBlank(lastUpdateUserId)) {
            user = SpringContext.getBean(org.kuali.rice.kim.api.identity.PersonService.class).getPersonByPrincipalName(lastUpdateUserId);
        }

        return user;
    }

    /**
     * @see org.kuali.kfs.sys.businessobject.TimestampedBusinessObject#getLastUpdateUserId()
     */
    public String getLastUpdateUserId() {
        return this.lastUpdateUserId;
    }

    /**
     * @param lastUpdateUserId The lastUpdateUserId to set.
     */
    public void setLastUpdateUserId(String lastUpdateUserId) {
        this.lastUpdateUserId = lastUpdateUserId;
    }

    /**
     * @param lastUpdate The lastUpdate to set.
     */
    public void setLastUpdate(Timestamp lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    @Override protected void prePersist() {
        super.prePersist();

        lastUpdate = new Timestamp((new Date()).getTime());
        lastUpdateUserId = GlobalVariables.getUserSession().getPerson().getPrincipalName();
    }

    @Override protected void preUpdate() {
        super.preUpdate();

        lastUpdate = new Timestamp((new Date()).getTime());
        lastUpdateUserId = GlobalVariables.getUserSession().getPerson().getPrincipalName();
    }
}
