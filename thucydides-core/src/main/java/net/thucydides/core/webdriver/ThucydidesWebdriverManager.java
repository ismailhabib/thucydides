package net.thucydides.core.webdriver;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Manage WebDriver instances.
 * It instantiates browser drivers, based on the test configuration, and manages them for the
 * duration of the tests.
 * A webdriver manager needs to be thread-safe. Tests can potentially be run in parallel, and different
 * tests can use different drivers.
 *                                                  ˜
 * @author johnsmart
 *
 */
public class ThucydidesWebdriverManager implements WebdriverManager {

    private static final ThreadLocal<WebdriverInstances> webdriverInstancesThreadLocal = new ThreadLocal<WebdriverInstances>();

    private final WebDriverFactory webDriverFactory;

    private final Configuration configuration;

    private static final Logger LOGGER = LoggerFactory.getLogger(ThucydidesWebdriverManager.class);

    @Inject
    public ThucydidesWebdriverManager(final WebDriverFactory webDriverFactory, final Configuration configuration) {
        this.webDriverFactory = webDriverFactory;
        this.configuration = configuration;
    }

    /**
     * Create a new driver instance based on system property values. You can
     * override this method to use a custom driver if you really know what you
     * are doing.
     *
     * @throws net.thucydides.core.webdriver.UnsupportedDriverException
     *             if the driver type is not supported.
     */
    private static WebDriver newDriver(final Configuration configuration,
                                       final WebDriverFactory webDriverFactory,
                                       final String driver) {
        SupportedWebDriver supportedDriverType = getConfiguredWebDriverWithOverride(configuration, driver);
        Class<? extends WebDriver> webDriverType = WebDriverFactory.getClassFor(supportedDriverType);
        return WebdriverProxyFactory.getFactory().proxyFor(webDriverType, webDriverFactory);
    }

    private static SupportedWebDriver getConfiguredWebDriverWithOverride(final Configuration configuration,
                                                                         final String driver) {
        if (StringUtils.isEmpty(driver)) {
            return configuration.getDriverType();
        }  else {
            return SupportedWebDriver.getDriverTypeFor(driver);
        }
    }

    public void closeDriver() {
        inThisTestThread().closeCurrentDriver();
    }

    public void closeAllDrivers() {
        inThisTestThread().closeAllDrivers();
    }

    public WebDriver getWebdriver() {
        return getThreadLocalWebDriver(configuration, webDriverFactory, inThisTestThread().getCurrentDriverName());
    }

    public WebDriver getWebdriver(final String driver) {
       return getThreadLocalWebDriver(configuration, webDriverFactory, driver);
    }

    private static WebDriver getThreadLocalWebDriver(final Configuration configuration,
                                                     final WebDriverFactory webDriverFactory,
                                                     final String driver) {


        if (!inThisTestThread().driverIsRegisteredFor(driver)) {
            inThisTestThread().registerDriverCalled(driver)
                                   .forDriver(newDriver(configuration, webDriverFactory, driver));
        }

        return inThisTestThread().useDriver(driver);
    }

    private static WebdriverInstances inThisTestThread() {
        if (webdriverInstancesThreadLocal.get() == null) {
            webdriverInstancesThreadLocal.set(new WebdriverInstances());
        }
        return webdriverInstancesThreadLocal.get();
    }

}