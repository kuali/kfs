/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.sys.identity;

import org.kuali.rice.kns.kim.role.RoleTypeServiceBase;

/**
 * The "default" Rice type service (org.kuali.rice.kns.kim.type.DataDictionaryTypeServiceBase) we were using for
 * FO delegation is no longer a DelegationTypeService, so this is an alternative we can use to avoid
 * having error messages in the log.
 */
public class DefaultRoleTypeServiceImpl extends RoleTypeServiceBase {

}
