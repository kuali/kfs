/*
 * Copyright 2007 The Kuali Foundation.
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

 function loadFundSourceDesc(fundSourceCodeFieldName)
 { 
	var elPrefix = findElPrefix( fundSourceCodeFieldName.name );
	var fundSourceCodeDescFieldName = elPrefix + ".fundSource.name";
	setFundSourceDesc(fundSourceCodeFieldName, fundSourceCodeDescFieldName);
 }
 
 function setFundSourceDesc( fundSourceCodeFieldName, fundSourceCodeDescFieldName ){
	 
	var fundSourceCode = dwr.util.getValue( fundSourceCodeFieldName );

	if (fundSourceCode =='') {
		clearRecipients(fundSourceCodeDescFieldName, "");
	} else {
		fundSourceCode = fundSourceCode.toUpperCase();
		var dwrReply = {
			callback:function(data) {
			if ( data != null && typeof data == 'object' ) {
				setRecipientValue( fundSourceCodeDescFieldName, data.name );
			} else {
				setRecipientValue( fundSourceCodeDescFieldName, wrapError( "Fund source code not found" ), true );			
			} },
			errorHandler:function( errorMessage ) { 
				setRecipientValue( fundSourceCodeDescFieldName, wrapError( "Fund source code not found" ), true );
			}
		};
		FundSourceService.getByPrimaryKey(fundSourceCode, dwrReply );
	}
}