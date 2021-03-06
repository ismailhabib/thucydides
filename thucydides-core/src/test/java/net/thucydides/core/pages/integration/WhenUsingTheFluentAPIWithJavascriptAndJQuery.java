package net.thucydides.core.pages.integration;


import net.thucydides.core.categories.RealBrowserTests;
import net.thucydides.core.webdriver.javascript.JavascriptExecutorFacade;
import net.thucydides.core.webdriver.jquery.ByJQuery;
import net.thucydides.core.webdriver.jquery.ByJQuerySelector;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

public class WhenUsingTheFluentAPIWithJavascriptAndJQuery {

    private static StaticSitePage page;
    private static WebDriver driver;

    @BeforeClass
    public static void openDriver() {
        driver = new PhantomJSDriver();
        page = new StaticSitePage(driver, 1000);
        page.open();
    }

    @AfterClass
    public static void shutdownDriver() {
        driver.quit();
    }

    @Test
    @Category(RealBrowserTests.class)
    public void should_check_and_close_javascript_alerts() {
        WebDriver driver = new FirefoxDriver();
        StaticSitePage page = new StaticSitePage(driver, 1000);
        page.open();

		page.openAlert();
        page.getAlert().accept();

        assertThat(page.getTitle(), is("Thucydides Test Site"));
        driver.close();
    }

    @Test
    public void should_inject_jquery_into_the_page() {
        WebDriver driver = new PhantomJSDriver();
        StaticSitePage page = new StaticSitePage(driver, 1000);
        page.open();

        page.evaluateJavascript("$('#firstname').focus();");

        Boolean jqueryInjected = (Boolean) page.evaluateJavascript("return (typeof jQuery === 'function')");
        assertThat(jqueryInjected, is(true));

        driver.close();
    }

    @Test
    public void should_be_able_to_use_the_javascript_executor_with_parameters() {
        StaticSitePage page = getPage();

        page.evaluateJavascript("$('#firstname').focus();", "#firstname");

        assertThat(page.element(page.firstName).hasFocus(), is(true));
    }

    @Test
    public void should_be_able_to_set_focus_directly() {
        StaticSitePage page = getPage();

        JavascriptExecutorFacade js = new JavascriptExecutorFacade(page.getDriver());
        js.executeScript("$('#firstname').focus();");

        assertThat(page.element(page.firstName).hasFocus(), is(true));
    }

    @Test
    public void should_support_jquery_queries_in_the_page() {

        StaticSitePage page = getPage();
        page.evaluateJavascript("$('#firstname').focus();");

        assertThat(page.element(page.firstName).hasFocus(), is(true));

        page.evaluateJavascript("$('#lastname').focus();");

        assertThat(page.element(page.lastName).hasFocus(), is(true));
    }

    @Test
    public void should_support_jquery_queries_that_return_values_in_the_page() {

        StaticSitePage page = getPage();
        Object result = page.evaluateJavascript("return $('#country').val();");

        assertThat(result.toString(), is("Australia"));
    }

    @Test
    public void should_be_able_to_find_an_element_using_a_jquery_expression() {
        StaticSitePage page = getPage();

        WebElement link = page.getDriver().findElement(ByJQuery.selector("a[title='Click Me']"));
        assertThat(link.isDisplayed(), is(true));
    }

    @Test
    public void should_be_able_to_find_multiple_elements_using_a_jquery_expression() {
        StaticSitePage page = getPage();
        List<WebElement> links = page.getDriver().findElements(ByJQuery.selector("h2"));
        assertThat(links.size(), is(2));
    }

    @Test(expected = WebDriverException.class)
    public void should_fail_gracefully_if_no_jquery_element_is_found() {
        StaticSitePage page = getPage();
        page.getDriver().findElement(ByJQuery.selector("a[title='Does Not Exist']"));
    }

    @Test(expected = WebDriverException.class)
    public void should_fail_gracefully_if_jquery_selector_is_invalid() {
        StaticSitePage page = getPage();
        page.getDriver().findElement(ByJQuery.selector("a[title='Does Not Exist'"));
    }

    @Test
    public void should_evaluate_javascript_within_browser() {
        StaticSitePage page = getPage();
        String result = (String) page.evaluateJavascript("return document.title");
        assertThat(result, is("Thucydides Test Site"));
    }

    @Test
    public void should_execute_javascript_within_browser() {
        StaticSitePage page = getPage();
        page.open();
        assertThat(page.element(page.firstName).hasFocus(), is(false));
        page.evaluateJavascript("document.getElementById('firstname').focus()");
        assertThat(page.element(page.firstName).hasFocus(), is(true));
    }


    @Test
    public void a_jquery_selector_should_be_described_by_the_corresponding_jquery_expression() {
        ByJQuerySelector jQuerySelector = ByJQuery.selector("a[title='Click Me']");

        assertThat(jQuerySelector.toString(), containsString("a[title='Click Me']"));
    }

    public StaticSitePage getPage() {
        return page;
    }
}
