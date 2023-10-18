<%@ page contentType="text/html;charset=UTF-8" %>
<%
    String connectionStatus = (String)request.getAttribute("connectionStatus");
%>
<h2>Робота за базами даних</h2>
<p><%=connectionStatus%></p>
<button id="db-create-button" class="waves-effect waves-light blue lighten-2 btn">
    <i class="material-icons left">cloud</i>
    Create Table
</button>
<div class="row">
    <form class="col s12" id="db-insert-form" method="post" enctype="application/x-www-form-urlencoded">
        <div class="row">
            <div class="input-field col s12">
                <input placeholder="Name" type="text" name="name" id="user-name">
                <label for="user-name">Name</label>
            </div>
        </div>
        <div class="row">
            <div class="input-field col s12">
                <input placeholder="Phone" type="tel" name="phone" id="user-phone">
                <label for="user-phone">Phone</label>
            </div>
        </div>
        <button id="db-insert-button" class="waves-effect waves-light blue lighten-2 btn" type="submit">
            <i class="material-icons left">phone_iphone</i>
            Order a call
        </button>
    </form>
</div>
<br />
<div class="row">
    <button id="db-read-button" class="waves-effect waves-light blue lighten-2 btn" type="submit" style="margin-bottom: 1rem">
        <i class="material-icons left">phone_iphone</i>
        Show order
    </button>
    <div id="table-container"></div>
</div>