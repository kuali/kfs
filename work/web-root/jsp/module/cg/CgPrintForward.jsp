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
<html>
<head>

</head>
<body>
	<%-- Below form used for non java script enabled browsers --%>
	<form name="disabledJavaScriptPrintForm"
		id="disabledJavaScriptPrintForm" method="post" action="${printPDFUrl}">
		<noscript>
			Click this button to see the ${printLabel} PDF:&nbsp;&nbsp;&nbsp;<input
				type="submit" title="View ${printLabel} PDF"
				value="View ${printLabel} PDF">
		</noscript>
	</form>
	<form name="disabledJavaScriptReturnForm"
		id="disabledJavaScriptReturnForm" method="post"
		action="${displayTabbedPageUrl}">
		<noscript>
			Click this button return to the ${printLabel} tabbed
			page:&nbsp;&nbsp;&nbsp;<input type="submit"
				title="Return to the ${printLabel}"
				value="Return to the ${printLabel}">
		</noscript>
	</form>

	<%-- Below forms used for java script enabled browsers --%>

	<form name="backForm" id="backForm" method="post"
		action="${displayTabbedPageUrl}"></form>

	<form name="printPDFForm" id="printPDFForm" method="post"
		action="${printPDFUrl}">
		<input type="hidden" name="useJavascript" value="true" />
		<script language="javascript">
			window.onload = dothis();
			function dothis() {
				_win = window.open('', 'printcgpdf');
				document.printPDFForm.target = _win.name;
				document.printPDFForm.submit();
		<%-- Need to insert a brief pause to keep a race condition from occurring.  --%>
			waitThenSubmit(5);
			}
			function waitThenSubmit(sec) {
				setTimeout("document.backForm.submit();", sec * 1000);
				return false;
			}
		</script>
	</form>


</body>
</html>
