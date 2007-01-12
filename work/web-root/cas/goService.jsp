<%@ page session="false" %>
<%
  String serviceId = (String) request.getAttribute("serviceId");
  String token = (String) request.getAttribute("token");
  String service = null;
  boolean safari = true;  // will set this below
  if (serviceId.indexOf('?') == -1)
    service = serviceId + "?ticket=" + token;
  else
    service = serviceId + "&ticket=" + token;
  service =
    edu.yale.its.tp.cas.util.StringUtil.substituteAll(service, "\n", "");
  service = 
    edu.yale.its.tp.cas.util.StringUtil.substituteAll(service, "\r", "");
  service =
    edu.yale.its.tp.cas.util.StringUtil.substituteAll(service, "\"", "");

  // Set Refresh header on initial login only if user isn't using Safari.
  // Fixes security bug where Safari would repost login credentials to the
  // web application rather than to CAS.
  if (((String)request.getAttribute("first")).equals("false")
    || request.getHeader("User-Agent") == null
    || request.getHeader("User-Agent").indexOf("Safari") == -1) {
    safari = false;
  }
%>
<html>
<head>
<title>Central Authentication Service</title>
<% if (!safari) { %>
<script>
  window.location.href="<%= service %>";
</script>
<% } %>
</head>

<body bgcolor="#0044AA" link="#ffffff" alink="#ffffff" vlink="#ffffff">
<% if (!safari) { %>
<noscript>
<% } %>
  <p>Login successful.</p>
  <p>
   Click <a href="<%= service %>">here</a>
   to access the service you requested.
  </p>
<% if (!safari) { %>
</noscript>
<% } %>
</body>

</html>
