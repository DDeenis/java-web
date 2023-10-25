package step.learning.ioc;

import com.google.inject.servlet.ServletModule;
import step.learning.filters.*;
import step.learning.servlets.*;

public class RouterModule extends ServletModule {
    @Override
    protected void configureServlets() {
        filter("/*").through(CharsetFilter.class);

        serve("/").with(HomeServlet.class);
        serve("/filters").with(FiltersServlet.class);
        serve("/ioc").with(IocServlet.class);
        serve("/jsp").with(JspServlet.class);
        serve("/signup").with(SignUpServlet.class);
        serve("/db").with(DbServlet.class);
        serve("/auth").with(AuthServlet.class);
        serve("/spa").with(SpaServlet.class);
        serve("/tpl/*").with(TemplatesServlet.class);
    }
}
