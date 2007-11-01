/*
 * Copyright 2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * Created on Jun 29, 2004
 *
 */
package org.kuali.module.pdp.bo;

import java.sql.Timestamp;

/**
 * @author jsissom
 */
public interface Code {
    public String getData();

    public String getCode();

    public void setCode(String code);

    public String getDescription();

    public void setDescription(String description);

    public Timestamp getLastUpdate();

    public void setLastUpdate(Timestamp lastUpdate);

    public PdpUser getLastUpdateUser();

    public void setLastUpdateUser(PdpUser u);

    public Integer getVersion();

    public void setVersion(Integer version);
}
