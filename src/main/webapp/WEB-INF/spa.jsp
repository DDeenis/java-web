<%@ page contentType="text/html;charset=UTF-8" %>
<h2>SPA</h2>
<p>
    Token: <b id="spa-token-status"></b>
</p>
<p>
    Expire status: <b id="spa-exp-status"></b>
</p>
<auth-part></auth-part>
<button class="btn green darken-1" id="spa-log-out">Log out</button>
<div>
    <button class="btn green lighten-1" id="spa-get-data1">Get data (1)</button>
    <button class="btn green lighten-1" id="spa-get-data2">Get data (2)</button>
    <button class="btn green lighten-1" id="spa-get-notfound">Get data (not found)</button>
</div>
<div id="data-1"></div>
<div id="data-2"></div>
<div id="data-notfound"></div>
