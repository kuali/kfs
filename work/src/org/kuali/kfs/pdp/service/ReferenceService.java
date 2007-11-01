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
 * Created on Jun 30, 2004
 *
 */
package org.kuali.module.pdp.service;

import java.util.List;
import java.util.Map;

import org.kuali.module.pdp.bo.Code;
import org.kuali.module.pdp.bo.PdpUser;


/**
 * @author jsissom
 */
public interface ReferenceService {
    public Code getCode(String type, String key);

    public List getAll(String type);

    public Map getallMap(String type);

    public Code addCode(String type, String code, String description, PdpUser u);

    public void updateCode(String code, String description, String type, PdpUser u);

    public void updateCode(Code item, PdpUser u);

    public void deleteCode(Code item);
}
