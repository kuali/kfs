<%@ taglib prefix="c" uri="/tlds/c.tld" %>
<%@ taglib uri="/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib tagdir="/WEB-INF/tags/portal" prefix="portal" %>

 
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Kuali Portal Index</title>
<link href="css/portal.css" rel="stylesheet" type="text/css" />
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
