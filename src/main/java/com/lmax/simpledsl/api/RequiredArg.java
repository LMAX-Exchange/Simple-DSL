/*
 * Copyright 2011 LMAX Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lmax.simpledsl.api;

import java.util.function.Function;

/**
 * A required argument.
 * <p>
 * An exception will be thrown when parsing the arguments if a value is not supplied for this argument.
 * The name of the argument may be omitted if required arguments are listed first.
 * For example with the params:
 *
 * <pre>{@code
 * public void login(String... args) {
 *     DslParams params = new DslParams(args,
 *                                      new RequiredParam("user"),
 *                                      new RequiredParam("password"));
 * }
 * }</pre>
 * <p>
 * The following two calls are exactly equivalent:
 * <pre>{@code
 * login("joan", "myPassword");
 * login("user: joan", "password: myPassword");
 * }</pre>
 * <p>
 * Required arguments can be supplied out of order if the name is specified, so the following is also equivalent:
 *
 * <pre>{@code
 * login("password: myPassword", "user: joan");
 * }</pre>
 * <p>
 * It is highly recommended including the argument name if there is any ambiguity about the meaning of the argument.
 * <p>
 * By default, only a single value is allowed.
 * Multiple values can be allowed by calling {@link #setAllowMultipleValues()}.
 */
public class RequiredArg extends SimpleDslArg<RequiredArg>
{
    public RequiredArg(final String name)
    {
        super(name, true);
    }

    @Override
    public <T> T fold(final Function<RequiredArg, T> ifRequired, final Function<OptionalArg, T> ifOptional, final Function<RepeatingArgGroup, T> ifGroup)
    {
        return ifRequired.apply(this);
    }
}
