<%@ page import="step.learning.dto.models.RegFormModel" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%
    RegFormModel model = (RegFormModel) request.getAttribute("reg-model");
    String loginValue = model == null ? "" : model.getLogin();
    String realNameValue = model == null ? "" : model.getRealName();
    String emailValue = model == null ? "" : model.getEmail();
    String birthDateValue = model == null ? "" : model.getBirthDateAsString();
    Map<String, String> errors = model == null ? new HashMap<String, String>() : model.getErrorMessages();
    System.out.println(errors);
%>
<p><%=request.getAttribute("reg-message")%></p>
<div class="row">
    <form class="col s12" method="post" action="<%=request.getContextPath()%>/signup">
        <div class="row">
            <div class="input-field col s6">
                <i class="material-icons prefix">person</i>
                <input value="<%=loginValue%>" id="reg-login" name="reg-login" type="text" class="<%=errors.containsKey("login") ? "invalid" : "validate"%>">
                <label for="reg-login">Login</label>
                <% if(errors.containsKey("login")) { %>
                    <span class="helper-text" data-error="<%=errors.get("login")%>"></span>
                <% } %>
            </div>
            <div class="input-field col s6">
                <i class="material-icons prefix">badge</i>
                <input value="<%=realNameValue%>" id="reg-name" name="reg-name" type="text" class="<%=errors.containsKey("realName") ? "invalid" : "validate"%>">
                <label for="reg-name">Real name</label>
                <% if(errors.containsKey("realName")) { %>
                <span class="helper-text" data-error="<%=errors.get("realName")%>"></span>
                <% } %>
            </div>
        </div>
        <div class="row">
            <div class="input-field col s6">
                <i class="material-icons prefix">password</i>
                <input value="123" id="reg-password" name="reg-password" type="password" class="<%=errors.containsKey("password") ? "invalid" : "validate"%>">
                <label for="reg-password">Password</label>
                <% if(errors.containsKey("password")) { %>
                <span class="helper-text" data-error="<%=errors.get("password")%>"></span>
                <% } %>
            </div>
            <div class="input-field col s6">
                <i class="material-icons prefix">key</i>
                <input value="123" id="reg-repeat-password" name="reg-repeat-password" type="password" class="<%=errors.containsKey("repeatPassword") ? "invalid" : "validate"%>">
                <label for="reg-repeat-password">Repeat password</label>
                <% if(errors.containsKey("repeatPassword")) { %>
                <span class="helper-text" data-error="<%=errors.get("repeatPassword")%>"></span>
                <% } %>
            </div>
        </div>
        <div class="row">
            <div class="input-field col s6">
                <i class="material-icons prefix">alternate_email</i>
                <input value="<%=emailValue%>" id="reg-email" name="reg-email" type="email" class="<%=errors.containsKey("email") ? "invalid" : "validate"%>">
                <label for="reg-email">Email</label>
                <% if(errors.containsKey("email")) { %>
                <span class="helper-text" data-error="<%=errors.get("email")%>"></span>
                <% } %>
            </div>
            <div class="input-field col s6">
                <i class="material-icons prefix">cake</i>
                <input value="<%=birthDateValue%>" id="reg-birthday" name="reg-birthday" type="date" class="<%=errors.containsKey("birthDate") ? "invalid" : "validate"%>">
                <label for="reg-birthday">Birthday</label>
                <% if(errors.containsKey("birthDate")) { %>
                <span class="helper-text" data-error="<%=errors.get("birthDate")%>"></span>
                <% } %>
            </div>
        </div>
        <div class="row">
            <div class="input-field col s6">
                <label> &emsp;
                    <input type="checkbox" class="filled-in validate" name="reg-agree" <%=errors.containsKey("isAgree") ? "invalid" : "validate"%> />
                    <span>
                        I am not <span style="font-size: 1.25rem">ඞ</span>
                    </span>
                </label>
                <% if(errors.containsKey("isAgree")) { %>
                <span class="helper-text" data-error="<%=errors.get("isAgree")%>"></span>
                <% } %>
            </div>
            <div class="input-field col s6 right-align">
                <button type="submit" class="waves-effect waves-light btn blue darken-1">
                    <i class="material-icons right">how_to_reg</i>
                    Register
                </button>
            </div>
        </div>
    </form>
</div>

