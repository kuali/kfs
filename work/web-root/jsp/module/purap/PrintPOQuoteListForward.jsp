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
<%-- Below form used for non java script enabled browsers --%>
<form name="disabledJavaScriptPrintForm" id="disabledJavaScriptPrintForm" method="post" action="${printPOQuoteListPDFUrl}">
  <noscript>
    Click this button to see the ${purchaseOrderLabel} PDF:&nbsp;&nbsp;&nbsp;<input type="submit" title="View ${purchaseOrderLabel} PDF" value="View ${purchaseOrderLabel} PDF">
  </noscript>
</form>
<form name="disabledJavaScriptReturnForm" id="disabledJavaScriptReturnForm" method="post" action="${displayPOTabbedPageUrl}">
  <noscript>
    Click this button return to the ${purchaseOrderLabel} tabbed page:&nbsp;&nbsp;&nbsp;<input type="submit" title="Return to the ${purchaseOrderLabel}" value="Return to the ${purchaseOrderLabel}">
  </noscript>
</form>

<%-- Below forms used for java script enabled browsers --%>

<form name="backForm" id="backForm" method="post" action="${displayPOTabbedPageUrl}">
</form>


<form name="printPOQuoteListPDFForm" id="printPOQuoteListPDFForm" method="post" action="${printPOQuoteListPDFUrl}">
  <input type="hidden" name="useJavascript" value="true"/>
  <script language ="javascript">
    window.onload = dothis();
    function dothis() {
      _win = window.open('', 'printpoquotelistpdf');
      document.printPOQuoteListPDFForm.target=_win.name;
      document.printPOQuoteListPDFForm.submit();
      document.backForm.submit();
    }
  </script>
</form>


</body>
</html>
