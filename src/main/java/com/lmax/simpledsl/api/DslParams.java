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

import com.lmax.simpledsl.internal.DslParamsImpl;

/**
 * The main entry point for defining the DSL language. Create a DslParams instance with the supplied arguments and the supported params.
 * The supplied values can then be retrieved using {@link DslParams#value(String)} and related methods.
 *
 * <pre>{@code
 *     public void createUser(String... args) {
 *         DslParams params = new DslParams(args,
 *                                          new RequiredParam("user"),
 *                                          new OptionalParam("password"));
 *         getDriver().createUser(params.value("user"), params.valueAsOptional("password"));
 *     }
 * }</pre>
 */
public interface DslParams extends DslValues
{
    /**
     * Retrieve the set of values supplied for a {@link RepeatingArgGroup} or an empty array if no values were supplied.
     * {@link RepeatingArgGroup} requires that it's first argument be a {@link RequiredArg} and the group as a whole is
     * referenced using the name of that argument. e.g.
     *
     * <pre>{@code
     *   DslParams params = new DslParams(args,
     *                              new RepeatingArgGroup(
     *                                  new RequiredArg("user"),
     *                                  new OptionalArg("password")));
     *   RepeatingGroup[] usersToCreate = params.valuesAsGroup("user");
     * }</pre>
     *
     * @param groupName the name of the first required parameter.
     * @return an array of {@link RepeatingGroup} instances, one for each set of values supplied for the argument.
     * @throws IllegalArgumentException if {@code name} does not match the name of a {@link RepeatingArgGroup}.
     */
    RepeatingGroup[] valuesAsGroup(String groupName);

    /**
     * A shorthand way to create a {@link DslParams} instance that accepts a single required parameter and return the
     * value that was supplied for that parameter.
     *
     * @param args              the arguments supplied by the test.
     * @param requiredParamName the name of the required parameter.
     * @return the value supplied for the parameter.
     */
    static String getSingleRequiredParamValue(final String[] args, final String requiredParamName)
    {
        return create(args, new RequiredArg(requiredParamName)).value(requiredParamName);
    }

    /**
     * Utility method for defining a DSL method that doesn't accept any arguments.
     *
     * This is an alternative to removing the {@code String... args} parameter entirely when a consistent public API is desired.
     *
     * @param args the parameters provided.
     */
    static void checkEmpty(final String[] args)
    {
        new DslParamsImpl(args);
    }

    /**
     * Create a new DslParams to define the supported dsl language.
     *
     * @param args   the arguments supplied by the test.
     * @param params the supported parameters.
     * @return the parsed {@link DslParams}
     * @throws IllegalArgumentException if an invalid parameter is specified
     */
    static DslParams create(final String[] args, final DslArg... params)
    {
        return new DslParamsImpl(args, params);
    }
}
