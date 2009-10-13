<%--
 Copyright 2007-2009 The Kuali Foundation
 
 Licensed under the Educational Community License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl2.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%-- @ include file="tldHeader.jsp" --%>
<html>
<head>

</head>
<body>

<br /><br />

<form method="post" action="purapElectronicInvoiceTest.do" enctype="multipart/form-data">
	<input type="hidden" name="action" value="postXML" />
	<INPUT TYPE="file" NAME="xmlFile" /><br />
	<INPUT TYPE="submit" VALUE="Upload XML eInvoice File" />
</form>

<br /><br />

<form method="post" action="purapElectronicInvoiceTest.do">
	<input type="hidden" name="action" value="returnXML" />
	PO Document Number: <INPUT TYPE="text" NAME="poDocNumber" size="10" /><br />
	<INPUT TYPE="submit" VALUE="Get XML eInvoice File for Specfied PO">
</form>

<br /><br />

</body>
</html>
