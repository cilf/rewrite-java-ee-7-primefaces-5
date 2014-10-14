package cz.cilf.rewrite;

import org.ocpsoft.rewrite.annotation.RewriteConfiguration;
import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.config.Direction;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.el.El;
import org.ocpsoft.rewrite.servlet.config.*;
import org.ocpsoft.rewrite.servlet.config.rule.Join;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

import javax.inject.Inject;
import javax.servlet.ServletContext;

@RewriteConfiguration
public class RewriteConfig extends HttpConfigurationProvider {

    @Inject
    private RewriteService service;

    @Override
    public int priority() {
        return 10;
    }

    @Override
    public Configuration getConfiguration(final ServletContext context) {

        return ConfigurationBuilder.begin()
                .addRule()
                .when(Direction.isInbound()
                        .and(Path.matches("/{path}"))
                        .andNot(new HttpCondition() {
                                    @Override
                                    public boolean evaluateHttp(HttpServletRewrite event, EvaluationContext evaluationContext) {
                                        String url = event.getRequest().getRequestURL().toString();
                                        return service.isPageFound(url);
                                    }
                                }
                        ))
                .perform(Forward.to("/404byRewrite.xhtml").and(Response.setStatus(404)))
                .where("path").matches(".*")
                .where("path").bindsTo(El.property("errorBean.page"))

                .addRule(Join.path("/second-page").to("/second-page.xhtml"))

                .addRule(Join.path("/").to("/index.xhtml"))
                ;
    }
}
