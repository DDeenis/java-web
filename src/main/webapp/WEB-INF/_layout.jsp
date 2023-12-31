<%@ page import="java.util.Date" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  String pageBody = (String) request.getAttribute("page-body");
  String context = request.getContextPath();
%>
<html>
<head>
    <title>Java web</title>
    <!-- Compiled and minified CSS -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/materialize/1.0.0/css/materialize.min.css">
    <!--Import Google Icon Font-->
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <link rel="stylesheet" href="<%=context%>/css/styles.css?time=<%=new Date().getTime()%>">
</head>
<body>
<nav>
    <div class="nav-wrapper blue lighten-2">
        <a href="#" class="brand-logo">Logo</a>
        <ul id="nav-mobile" class="right hide-on-med-and-down">
            <li class="<%=pageBody.equals("about.jsp") ? "active" : ""%>">
                <a href="<%=context%>/jsp">About</a>
            </li>
            <li class="<%=pageBody.equals("filters.jsp") ? "active" : ""%>">
                <a href="<%=context%>/filters">Filters</a>
            </li>
            <li class="<%=pageBody.equals("ioc.jsp") ? "active" : ""%>">
                <a href="<%=context%>/ioc">IOC</a>
            </li>
            <li class="<%=pageBody.equals("db.jsp") ? "active" : ""%>">
                <a href="<%=context%>/db">DB</a>
            </li>
            <li class="<%=pageBody.equals("spa.jsp") ? "active" : ""%>">
                <a href="<%=context%>/spa">SPA</a>
            </li>
            <li class="<%=pageBody.equals("ws.jsp") ? "active" : ""%>">
                <a href="<%=context%>/ws">WS</a>
            </li>
            <li>
                <a class="modal-trigger auth-icon" href="#signup-modal">
                    <i class="material-icons">login</i>
                </a>
            </li>
        </ul>
    </div>
</nav>
<main style="padding: 0.5rem 1rem">
    <jsp:include page="<%=pageBody%>"/>
</main>
<footer class="page-footer blue lighten-2">
    <div class="container">
        <div class="row">
            <div class="col l6 s12">
                <h5 class="white-text">Footer Content</h5>
                <p class="grey-text text-lighten-4">You can use rows and columns here to organize your footer content.</p>
            </div>
            <div class="col l4 offset-l2 s12">
                <h5 class="white-text">Links</h5>
                <ul>
                    <li><a class="grey-text text-lighten-3" href="#!">Link 1</a></li>
                    <li><a class="grey-text text-lighten-3" href="#!">Link 2</a></li>
                    <li><a class="grey-text text-lighten-3" href="#!">Link 3</a></li>
                    <li><a class="grey-text text-lighten-3" href="#!">Link 4</a></li>
                </ul>
            </div>
        </div>
    </div>
    <div class="footer-copyright">
        <div class="container">
            © 2014 Copyright Text
            <a class="grey-text text-lighten-4 right" href="#!">More Links</a>
        </div>
    </div>
</footer>
<div id="signup-modal" class="modal">
    <div class="modal-content">
        <h4>Modal Header</h4>
        <form class="row" novalidate>
            <div class="input-field col s6">
                <i class="material-icons prefix">person</i>
                <input id="auth-login" name="auth-login" type="text" class="validate" required>
                <label for="auth-login">Login</label>
            </div>
            <div class="input-field col s6">
                <i class="material-icons prefix">password</i>
                <input id="auth-password" name="auth-password" type="password" class="validate" required>
                <label for="auth-password">Password</label>
            </div>
        </form>
    </div>
    <div class="modal-footer">
        <b id="auth-message"></b>
        <a href="<%=context%>/signup" class="modal-close waves-effect waves-green btn-flat blue lighten-3">Register</a>
        <a href="#!" id="auth-sign-in-btn" class="waves-effect blue lighten-4 btn-flat">Sign In</a>
    </div>
</div>
<!-- Compiled and minified JavaScript -->
<script defer src="https://cdnjs.cloudflare.com/ajax/libs/materialize/1.0.0/js/materialize.min.js"></script>
<script src="<%=context%>/js/index.js" defer></script>
<script src="<%=context%>/js/spa.js?time=<%=new Date().getTime()%>" defer></script>
</body>
</html>
