<%--
   - The Kuali Financial System, a comprehensive financial management system for higher education.
   - 
   - Copyright 2005-2014 The Kuali Foundation
   - 
   - This program is free software: you can redistribute it and/or modify
   - it under the terms of the GNU Affero General Public License as
   - published by the Free Software Foundation, either version 3 of the
   - License, or (at your option) any later version.
   - 
   - This program is distributed in the hope that it will be useful,
   - but WITHOUT ANY WARRANTY; without even the implied warranty of
   - MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   - GNU Affero General Public License for more details.
   - 
   - You should have received a copy of the GNU Affero General Public License
   - along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
