package cz.cilf.rewrite;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Filters;
import org.jboss.shrinkwrap.api.GenericArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.importer.ExplodedImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.io.File;
import java.net.URL;

import static org.junit.Assert.assertEquals;

@RunWith(Arquillian.class)
public class RewriteTest {

    private static final String WEBAPP_SRC = "src/main/webapp";

    @Drone
    private WebDriver browser;

    @ArquillianResource
    private URL base;

    @FindBy(tagName = "h1")
    private WebElement h1;

    @FindBy(tagName = "h2")
    private WebElement h2;

    @FindBy(tagName = "a")
    private WebElement a;

    @Deployment(testable = false)
    public static WebArchive createDeployment() {
        MavenResolverSystem resolver = Maven.resolver();
        WebArchive webArchive = ShrinkWrap.create(WebArchive.class, "rewrite-test.war")
                .setWebXML(new File(WEBAPP_SRC, "WEB-INF/web.xml"))
                .addClasses(ErrorBean.class, RewriteConfig.class, RewriteService.class)
                .addAsLibraries(resolver.loadPomFromFile("pom.xml").resolve("org.ocpsoft.rewrite:rewrite-servlet:3.0.0.Alpha3").withoutTransitivity().asFile())
                .addAsLibraries(resolver.loadPomFromFile("pom.xml").resolve("org.ocpsoft.rewrite:rewrite-integration-faces:3.0.0.Alpha3").withoutTransitivity().asFile())
                .addAsLibraries(resolver.loadPomFromFile("pom.xml").resolve("org.ocpsoft.rewrite:rewrite-integration-cdi:3.0.0.Alpha3").withoutTransitivity().asFile())
                .merge(ShrinkWrap.create(GenericArchive.class).as(ExplodedImporter.class)
                                .importDirectory(WEBAPP_SRC).as(GenericArchive.class),
                        "/", Filters.include(".*\\.xhtml$"))
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        System.out.println(webArchive.toString(true));
        return webArchive;
    }

    @Test
    public void shouldRenderIndexPage() {
        browser.get(base.toExternalForm());

        /** correct inbound of "/" */
        assertEquals("rewrite-java-ee-7-primefaces-5", h1.getText());
        assertEquals("Index Page", h2.getText());

        /** correct outbound for "/second-page" */
        assertEquals(base.toExternalForm() + "second-page", a.getAttribute("href"));
    }

    @Test
    public void shouldRenderSecondPage() {
        browser.get(base.toExternalForm() + "second-page");

        /** correct inbound for "/second-page" */
        assertEquals("rewrite-java-ee-7-primefaces-5", h1.getText());
        assertEquals("Second Page", h2.getText());

        /** correct outbound for "/" */
        assertEquals(base.toExternalForm(), a.getAttribute("href"));
    }

    @Test
    public void shouldRender404PageByJSF() {
        browser.get(base.toExternalForm() + "xx");

        /** correct error page for "/xx" */
        assertEquals("404 Error Page", h1.getText());
    }

    @Test
    public void shouldRender404PageByRewrite() {
        browser.get(base.toExternalForm() + "third-page");

        /** correct error page for "/third-page" */
        assertEquals("404 Rewrite Error Page", h1.getText());
    }
}