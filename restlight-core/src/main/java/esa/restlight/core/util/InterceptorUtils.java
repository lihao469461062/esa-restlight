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
package esa.restlight.core.util;

import esa.commons.Checks;
import esa.commons.UrlUtils;
import esa.commons.collection.LinkedMultiArrayValueMap;
import esa.commons.collection.MultiValueMap;
import esa.restlight.core.DeployContext;
import esa.restlight.core.annotation.Intercepted;
import esa.restlight.core.config.RestlightOptions;
import esa.restlight.core.handler.Handler;
import esa.restlight.core.interceptor.Interceptor;
import esa.restlight.core.interceptor.InterceptorFactory;
import esa.restlight.core.interceptor.InterceptorPredicate;
import esa.restlight.core.method.InvocableMethod;
import esa.restlight.server.route.Mapping;
import esa.restlight.server.route.Route;

import java.util.List;
import java.util.Optional;

import static esa.restlight.core.interceptor.HandlerInterceptor.PATTERN_FOR_ALL;

public class InterceptorUtils {

    public static boolean isIntercepted(InvocableMethod handler) {
        Intercepted intercepted = handler.getMethodAnnotation(Intercepted.class);
        if (intercepted == null) {
            intercepted = handler.beanType().getAnnotation(Intercepted.class);
        }
        if (intercepted == null) {
            return true;
        }
        return intercepted.value();
    }

    static MultiValueMap<InterceptorPredicate, Interceptor> filter(DeployContext<? extends RestlightOptions> ctx,
                                                                   Mapping mapping,
                                                                   Handler handler,
                                                                   List<InterceptorFactory> interceptors) {
        final MultiValueMap<InterceptorPredicate, Interceptor> filtered =
                new LinkedMultiArrayValueMap<>(interceptors.size());

        // use a fake route to represent
        final Route fake = Route.route(mapping)
                .handlerObject(handler.handler());

        for (InterceptorFactory factory : interceptors) {
            Optional<Interceptor> interceptor = factory.create(ctx, fake);
            int affinity;
            if (!interceptor.isPresent() || (affinity = interceptor.get().affinity()) < 0) {
                continue;
            }
            if (affinity == 0) {
                // certainly match
                filtered.add(InterceptorPredicate.ALWAYS, interceptor.get());
            } else {
                filtered.add(Checks.checkNotNull(interceptor.get().predicate(),
                        "Unexpected null predicate of interceptor '" + interceptor.get() + "'"),
                        interceptor.get());
            }
        }
        return filtered;
    }

    public static String[] parseIncludesOrExcludes(String contextPath, String[] patterns) {
        String[] withContextPath = null;
        if (patterns != null) {
            withContextPath = new String[patterns.length];
            for (int i = 0; i < patterns.length; i++) {
                if (!PATTERN_FOR_ALL.equals(patterns[i])) {
                    withContextPath[i] = ConverterUtils.standardContextPath(contextPath)
                            + UrlUtils.prependLeadingSlash(patterns[i]);
                } else {
                    withContextPath[i] = UrlUtils.prependLeadingSlash(patterns[i]);
                }
            }
        }
        return withContextPath;
    }
}
