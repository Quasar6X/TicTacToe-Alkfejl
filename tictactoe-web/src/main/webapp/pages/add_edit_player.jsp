<!DOCTYPE html>
<%--
  Created by dani0.
  Date: 4/24/2021
  Time: 10:02 PM
--%>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="hu.alkfejl.constants.Constants" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html lang="en">
<head>
    <title>New Player</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta3/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-eOJMYsd53ii+scO/bJGFsiCZc+5NDVN2yr8+0RDqr0Ql0h+rP48ckxlpbzKgwra6" crossorigin="anonymous">
    <link href="../img/logo.png" rel="icon">
</head>
    <body>
        <div class="container d-grid gap-2 col-3 mx-auto mt-3">
            <div class="card">
                <div class="card-body">
                    <c:choose>
                        <c:when test="${param[Constants.FORM_INPUT_NAME_PLAYER_ID] != null}"><h5 class="card-title">Edit Player</h5></c:when>
                        <c:otherwise><h5 class="card-title">Add new Player</h5></c:otherwise>
                    </c:choose>

                    <form action="../AddEditPlayerController" method="post">
                        <label for="pname">New player's name</label>
                        <c:if test="${param[Constants.FORM_INPUT_NAME_PLAYER_ID] != null}">
                            <input type="hidden" name="${Constants.FORM_INPUT_NAME_PLAYER_ID}" value="${param[Constants.FORM_INPUT_NAME_PLAYER_ID]}">
                        </c:if>
                        <input id="pname" type="text" name="${Constants.FORM_INPUT_NAME_PLAYER_NAME}">
                        <button class="btn btn-primary" type="submit">
                            <c:choose>
                                <c:when test="${param[Constants.FORM_INPUT_NAME_PLAYER_ID] != null}">Edit</c:when>
                                <c:otherwise>Add</c:otherwise>
                            </c:choose>
                        </button>
                    </form>
                </div>
            </div>
        </div>
    </body>
</html>
