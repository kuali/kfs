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

 function loadSubClassCodeDesc(subclassCodeFieldName){
	var elPrefix = findElPrefix( subclassCodeFieldName.name );
	var subclassCodeDescFieldName = elPrefix + ".subclassCode.name";
 	setSubclassCodeDesc(subclassCodeFieldName, subclassCodeDescFieldName);
 }
 
 function setSubclassCodeDesc( subclassCodeFieldName, subclassCodeDescFieldName ){
 
	var subclassCode = dwr.util.getValue( subclassCodeFieldName );

	if (subclassCode =='') {
		clearRecipients(subclassCodeDescFieldName, "");
	} else {
		subclassCode = subclassCode.toUpperCase();
		var dwrReply = {
			callback:function(data) {
			if ( data != null && typeof data == 'object' ) {
				setRecipientValue( subclassCodeDescFieldName, data.name );
			} else {
				setRecipientValue( subclassCodeDescFieldName, wrapError( "subclass code not found" ), true );			
			} },
			errorHandler:function( errorMessage ) { 
				setRecipientValue( subclassCodeDescFieldName, wrapError( "subclass code not found" ), true );
			}
		};
		SubClassCodeService.getByPrimaryKey( subclassCode, dwrReply );
	}
}