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

package de.rub.nds.oidc.server.op;

import de.rub.nds.oidc.log.TestStepLogger;
import de.rub.nds.oidc.server.RequestPath;
import de.rub.nds.oidc.test_model.OPConfigType;
import de.rub.nds.oidc.test_model.ParameterType;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Tobias Wich
 */
public interface OPImplementation {

	void setConfig(OPConfigType cfg);

	void setLogger(TestStepLogger logger);

	void setContext(Map<String, ?> suiteCtx, Map<String, ?> stepCtx);

	void setParameters(List<ParameterType> params);


	void webfinger(RequestPath path, HttpServletRequest req, HttpServletResponse resp) throws IOException;

}