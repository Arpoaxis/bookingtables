<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
  // Forward securely to the real view under WEB-INF
  request.getRequestDispatcher("/WEB-INF/jsp/index.jsp").forward(request, response);
%>
