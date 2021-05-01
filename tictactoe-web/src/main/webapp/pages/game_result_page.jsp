<!DOCTYPE html>
<%--
  Created by dani0.
  Date: 4/27/2021
  Time: 15:04 PM
--%>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="hu.alkfejl.constants.Constants" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>${sessionScope.get(Constants.SESSION_GAME_RESULT)}</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta3/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-eOJMYsd53ii+scO/bJGFsiCZc+5NDVN2yr8+0RDqr0Ql0h+rP48ckxlpbzKgwra6" crossorigin="anonymous">
    <link href="../img/logo.png" rel="icon">

    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
    <script>
        $(document).ready(function () {
            $.ajax({
                type: 'POST',
                url: "../SessionCleaner"
            });
        });
    </script>
</head>
<body>
    <div class="container d-grid gap-2 col-3 mx-auto mt-3">
        <div class="card">
            <c:if test="${sessionScope.get(Constants.SESSION_GAME_RESULT).toLowerCase().contains('win')}">
                <img src="../img/trophy.png" alt="trophy" class="card-img-top mt-5">
            </c:if>
            <div class="card-body">
                <h5 class="card-title">${sessionScope.get(Constants.SESSION_GAME_RESULT)}</h5>
                <a href="../index.jsp" class="btn btn-primary">Back to home</a>
            </div>
        </div>
    </div>
</body>
</html>
