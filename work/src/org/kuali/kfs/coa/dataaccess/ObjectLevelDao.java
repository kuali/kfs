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
package org.kuali.kfs.coa.dataaccess;

import java.util.Collection;

import org.apache.ojb.broker.query.Criteria;
import org.kuali.kfs.coa.businessobject.ObjectLevel;
import org.kuali.kfs.module.ar.businessobject.Event;

/**
 * This interface defines basic methods that ObjLevel Dao's must provide
 */
public interface ObjectLevelDao {
    
    public Collection<ObjectLevel> getObjectLevelsByCriteria(Criteria criteria);
    
}
