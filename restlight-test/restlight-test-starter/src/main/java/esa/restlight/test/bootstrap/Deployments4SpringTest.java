/*
 * Copyright 2020 OPPO ESA Stack Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package esa.restlight.test.bootstrap;

import esa.restlight.core.config.RestlightOptions;
import esa.restlight.server.handler.RestlightHandler;
import esa.restlight.spring.Deployments4Spring;
import org.springframework.context.ApplicationContext;

class Deployments4SpringTest extends Deployments4Spring<Restlight4SpringTest, Deployments4SpringTest,
        RestlightOptions> {

    Deployments4SpringTest(Restlight4SpringTest restlight, ApplicationContext context, RestlightOptions options) {
        super(restlight, context, options);
    }

    RestlightHandler handler() {
        return getRestlightHandler();
    }
}
