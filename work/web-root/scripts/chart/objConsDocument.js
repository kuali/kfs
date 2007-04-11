/*
 * Copyright 2006-2007 The Kuali Foundation.
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
function updateObjectName( objField ) {
	// we want the base label - and the object field is in the detail section
  var elPrefix = findElPrefix( objField.name );
	var coaCode = getElementValue( elPrefix + ".chartOfAccountsCode" );
	var objectCode = getElementValue( objField.name );
	var nameFieldName = elPrefix + ".financialEliminationsObject.financialObjectCodeName";
	if ( coaCode != "" && objectCode != "" ) {
		var dwrReply = {
			callback:function(data) {
			if ( data != null && typeof data == 'object' ) {
				setRecipientValue( nameFieldName, data.financialObjectCodeName );
			} else {
				setRecipientValue( nameFieldName, wrapError( "object not found" ), true );			
			} },
			errorHandler:function( errorMessage ) { 
				window.status = errorMessage;
				setRecipientValue( nameFieldName, wrapError( "object not found" ), true );
			}
		};
		ObjectCodeService.getByPrimaryIdForCurrentYear( coaCode, objectCode, dwrReply );
	} else if ( objectCode == "" ) {
		clearRecipients( nameFieldName );
	} else if ( coaCode == "" ) {
		setRecipientValue(nameFieldName, wrapError( 'chart code is empty' ), true );
	} else if ( fiscalYear == "" ) {
		setRecipientValue(nameFieldName, wrapError( 'fiscal year is missing' ), true );	
	}
}
