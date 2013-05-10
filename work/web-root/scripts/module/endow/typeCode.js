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

 function loadTypeCodeDesc(typeCodeFieldName){
	var elPrefix = findElPrefix( typeCodeFieldName.name );
	var typeCodeDescFieldName = elPrefix + ".type.name";
 	setTypeCodeDesc(typeCodeFieldName, typeCodeDescFieldName);
 }
 
 function setTypeCodeDesc( typeCodeFieldName, typeCodeDescFieldName ){
 
	var typeCode = dwr.util.getValue( typeCodeFieldName );

	if (typeCode =='') {
		clearRecipients(typeCodeDescFieldName, "");
	} else {
		typeCode = typeCode.toUpperCase();
		var dwrReply = {
			callback:function(data) {
			if ( data != null && typeof data == 'object' ) {
				setRecipientValue( typeCodeDescFieldName, data.name );
			} else {
				setRecipientValue( typeCodeDescFieldName, wrapError( "type code not found" ), true );			
			} },
			errorHandler:function( errorMessage ) { 
				setRecipientValue( typeCodeDescFieldName, wrapError( "type code not found" ), true );
			}
		};
		TypeCodeService.getByPrimaryKey( typeCode, dwrReply );
	}
}