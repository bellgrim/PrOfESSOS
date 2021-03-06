/****************************************************************************
 * Copyright 2016 Ruhr-Universität Bochum.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ***************************************************************************/

package de.rub.nds.oidc.browser;

import de.rub.nds.oidc.server.op.OPParameterConstants;
import de.rub.nds.oidc.test_model.TestStepResult;
import javax.annotation.Nullable;
import org.openqa.selenium.By;

/**
 *
 * @author Tobias Wich
 */
public class DefaultRPTestBrowser extends BrowserSimulator {

	@Override
	public TestStepResult run() {
		String startUrl = rpConfig.getUrlClientTarget();
		logger.log(String.format("Opening browser with URL '%s'.", startUrl));
		driver.get(startUrl);

		// execute JS to start authentication
		String submitScriptRaw = rpConfig.getSeleniumScript();
		String submitScript = te.eval(createRPContext(), submitScriptRaw);

		// wait until a new html element appears, indicating a page load
		waitForPageLoad(() -> {
			driver.executeScript(submitScript);
			// capture state where the text is entered
			logger.log("Webfinger identity entered into the login form.");
			return null;
		});
		logger.log("HTML element found in Browser.");
		// wait a bit more in case we have an angular app or some other JS heavy application
		waitMillis(1000);

		// take a screenshot again to show the finished site
		logger.log("Finished login procedure, please check if it succeeded and correct the success URL and the user needle accordingly.");
		logScreenshot();

		// run condition check code
		TestStepResult checkResult = checkConditionAfterLogin();
		if (checkResult != null) {
			logger.log("Result returned from check condition funtion: " + checkResult.name());
			return checkResult;
		}

		// save the location of the finished state
		boolean urlReached = rpConfig.getFinalValidUrl().equals(driver.getCurrentUrl());
		boolean forceSuccessUrlFails = params.getBool(OPParameterConstants.FORCE_SUCCESS_URL_FAILS);
		if (forceSuccessUrlFails && urlReached) {
			logger.log("Target URL reached. Assuming login is successful.");
			logger.log("Successful login fails the test");
			return TestStepResult.FAIL;
		}
		if (! urlReached) {
			logger.log("Target URL not reached. Assuming login is not successful.");
			return TestStepResult.PASS;
		}

		// see if we need to go to another URL
		String profileUrl = rpConfig.getProfileUrl();
		if (profileUrl != null && ! profileUrl.isEmpty()) {
			logger.log("Loading profile URL page.");
			waitForPageLoad(() -> {
				driver.get(rpConfig.getProfileUrl());
				return null;
			});
			// wait a bit more in case we have an angular app or some other JS heavy application
			waitMillis(1000);
			logger.log("Loaded profile URL page.");
			logScreenshot();
		}

		String needle;
		if (params.getBool(OPParameterConstants.USE_EVIL_NEEDLE)) {
			needle = rpConfig.getEvilUserNeedle();
		} else {
			needle = rpConfig.getHonestUserNeedle();
		}
		if (needle != null && ! needle.isEmpty()) {
			needle = te.eval(createRPContext(), needle);
			needle = needle.replace("\"", "\\\""); // escape quotation marks
			String xpath = String.format("//*[contains(., \"%s\")]", needle);
			// search string
			boolean needleFound = withSearchTimeout(() -> ! driver.findElements(By.xpath(xpath)).isEmpty());

			logger.log("User needle search result: needle-found=" + needleFound);
			return needleFound ? TestStepResult.FAIL : TestStepResult.PASS;
		} else {
			logger.log("Search for user needle not possible, none specified.");
			return TestStepResult.UNDETERMINED;
		}
	}

	@Nullable
	protected TestStepResult checkConditionAfterLogin() {
		return null;
	}

}
