<%@ page contentType="text/html;charset=UTF-8" %>
<h2>Можливостi JSX</h2>
<ul>
    <li>
        <code>&lt;%@ page contentType="text/html;charset=UTF-8" %></code>
        <b>- об'явлення сторiнки та додання метаданих</b>
    </li>
    <li>
        <code>&lt;%= expression %></code>
        <b>- виконання виразу (java коду)</b>
    </li>
    <li>
        <code>
            &lt;% for (int i = 0; i < 5; i++) {%>
                &lt;li>
                    Item №&lt;%=i + 1%>
                &lt;/li>
            &lt;% } %>
        </code>
        <b>- цикл for</b>
    </li>
</ul>

