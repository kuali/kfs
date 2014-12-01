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
<form name="disabledJavaScriptPrintForm" id="disabledJavaScriptPrintForm" method="post" action="${printReceivingTicketPDFUrl}">
  <noscript>
    Click this button to see the ${receivingDocLabel} PDF:&nbsp;&nbsp;&nbsp;<input type="submit" title="View ${receivingDocLabel} PDF" value="View ${receivingDocLabel} PDF">
  </noscript>
</form>
<form name="disabledJavaScriptReturnForm" id="disabledJavaScriptReturnForm" method="post" action="${displayReceivingDocTabbedPageUrl}">
  <noscript>
    Click this button return to the ${receivingDocLabel} tabbed page:&nbsp;&nbsp;&nbsp;<input type="submit" title="Return to the ${receivingDocLabel}" value="Return to the ${receivingDocLabel}">
  </noscript>
</form>

<%-- Below forms used for java script enabled browsers --%>

<form name="backForm" id="backForm" method="post" action="${displayReceivingDocTabbedPageUrl}">
</form>


<form name="printRecTicketPDFForm" id="printRecTicketPDFForm" method="post" action="${printReceivingTicketPDFUrl}">
  <input type="hidden" name="useJavascript" value="true"/>
  <script language ="javascript">
    window.onload = dothis();
    function dothis() {
      _win = window.open('', 'printpopdf');
      document.printRecTicketPDFForm.target=_win.name;
      document.printRecTicketPDFForm.submit();
      document.backForm.submit();
    }
  </script>
</form>


</body>
</html>
