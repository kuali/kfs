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
 * Created on Jul 7, 2004
 *
 */
package org.kuali.module.pdp.service;

import java.util.List;

import org.kuali.module.pdp.bo.CustomerBank;
import org.kuali.module.pdp.bo.CustomerProfile;


/**
 * @author jsissom
 */
public interface CustomerProfileService {
    public List getAll();

    public CustomerProfile get(Integer id);

    public CustomerProfile get(String chartCode, String orgCode, String subUnitCode);

    public void save(CustomerProfile cp);

    public void saveCustomerBank(CustomerBank cb);

    public void deleteCustomerBank(CustomerBank cb);
}
