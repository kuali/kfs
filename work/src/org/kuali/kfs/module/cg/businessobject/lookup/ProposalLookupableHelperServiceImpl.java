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
package org.kuali.module.cg.lookup;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.module.cg.bo.ProjectDirector;

public class ProposalLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

	@Override
	protected List<? extends BusinessObject> getSearchResultsHelper(Map<String, String> fieldValues, boolean unbounded) {
		// perform the lookup on the project director object first
		if ( !StringUtils.isBlank( fieldValues.get( "lookupUserId" ) ) ) {
			HashMap<String,String> newParam = new HashMap<String,String>( 1 );
			newParam.put( "universalUser.personUserIdentifier", fieldValues.get( "lookupUserId" ) );
			Collection<ProjectDirector> pds = getLookupService().findCollectionBySearchUnbounded( ProjectDirector.class, newParam);
			if ( pds.isEmpty() ) {
				return Collections.EMPTY_LIST;
			}
			fieldValues.put("proposalProjectDirectors.projectDirector.personUniversalIdentifier", pds.iterator().next().getPersonUniversalIdentifier() );
			fieldValues.remove( "lookupUserId" );
		}
				
		return super.getSearchResultsHelper(fieldValues, unbounded);
	}

}
