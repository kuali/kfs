<%--
 Copyright 2005-2006 The Kuali Foundation.
 
 Licensed under the Educational Community License, Version 1.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl1.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ taglib prefix="c" uri="/tlds/c.tld" %>
<%@ taglib uri="/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib tagdir="/WEB-INF/tags/portal" prefix="portal" %>

 
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Kuali Portal Index</title>
<link href="css/portal.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="scripts/log4javascript.js"></script>
<script type="text/javascript">
	//comment the next line to enable logging
	log4javascript.setEnabled(false);
	// Create the logger. "mylogger" is the unique name of the logger and
	// can be any string.
	var log = log4javascript.getLogger("mylogger"); 
	

	// Create a PopUpAppender with default options
	var appender = new log4javascript.PopUpAppender();
	
	// Change the desired configuration options
	appender.setFocusPopUp(true);
	appender.setNewestMessageAtTop(true);
	
	// Add the appender to the logger
	log.addAppender(appender);
	
</script>
<script language="javascript" src="scripts/my_common.js"></script>
<script language="javascript" >
if (top.location != self.location) {
	top.location = self.location;
}
</script>

</head>
<body topmargin="0" leftmargin="0" marginheight="0" marginwidth="0">

 <div id="header" title="Kuali Financial System">
    <h1></h1>Kuali Financial System
  </div>
  <div id="build">${ConfigProperties.version}</div>
