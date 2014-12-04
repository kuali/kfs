/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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

