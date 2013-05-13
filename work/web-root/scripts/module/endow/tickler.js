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
 
 //TODO: 1) Add a on page load function to associate this function with Onchange event for active. 2) Ensure if tickler is inactive we hide the date selector.
 //document.getElementById('document.newMaintainableObject.active').onchange = inactivateTickler1;
 function inactivateTickler(event)
 {
	var activeFieldName;
	if(event == null)
	{
		activeFieldName = document.getElementById('document.newMaintainableObject.active');
	}
	else
	{
		if(event.srcElement)
		{
			activeFieldName = event.srcElement;
		}
		else
		{
			activeFieldName = event.target;
		}
	}
	
	var elPrefix = findElPrefix( activeFieldName.name );
		
	var terminationDate = document.getElementById(elPrefix + '.terminationDate');
	var terminationDatePicker = document.getElementById(elPrefix + '.terminationDate_datepicker');
	
	var activeChecked = dwr.util.getValue( activeFieldName.name );
	
	if(activeChecked)
	{
		terminationDate.readOnly = false;
		terminationDatePicker.style.visibility = 'visible';
	} 
	else 
	{
		terminationDate.readOnly = true;
		terminationDatePicker.style.visibility = 'hidden';
		
		
		var dwrReply = {
			callback:function(data) {
			if ( data != null) {
				setRecipientValue( terminationDate.name, data );
			} else {
				setRecipientValue( terminationDate.name, wrapError( "Error obtaining System Date" ), true );			
			} },
			errorHandler:function( errorMessage ) { 
				setRecipientValue( terminationDate.name, wrapError( "Error obtaining System Date" ), true );
			}
		};
		KEMService.getCurrentSystemProcessDateFormated(dwrReply);
		
	}
}
 
  function registerActiveFlagOnclick()
 {
	  inactivateTickler();
	  var activeFlagName = 'document.newMaintainableObject.active';
	  registerBrowserEvent(activeFlagName,'click',inactivateTickler);
 }
  
  registerTicklerEvents();
  
  function registerTicklerEvents()
  {
	  registerBrowserEvent('window','load',registerActiveFlagOnclick);
  }  
  
  function registerBrowserEvent(elementName,eventName,functionName)
  {
	  var element;
	  
	  if(elementName == 'window')
	  {
		  element = window;
	  }  
	  else
	  {
		  element =  document.getElementById(elementName);
	  }
		  
	  //Register on Page load  Event for active flag.
	  if(window.addEventListener)
	  {
		  //This is for Gecko(Mozilla) engines
		  element.addEventListener(eventName,functionName,false);
	  }
	  else
	  {
		//This is for Trident(IE) engine
		  element.attachEvent('on' + eventName ,functionName);
	  }	
  }

