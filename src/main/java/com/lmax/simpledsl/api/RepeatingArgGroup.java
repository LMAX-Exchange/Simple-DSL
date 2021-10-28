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
 * Define a group of arguments that can be repeated 0 or more times.
 * <p>
 * The first argument in the group must be a {@link RequiredArg} and is used to identify the start of the group in
 * arguments and when retrieving the groups from {@link DslParams#valuesAsGroup(String)}. Other argument may follow
 * the first argument in any order.
 * <p>
 * Multiple {@link RepeatingArgGroup RepeatingArgGroups} can be used within the same {@link DslParams} but they
 * cannot be nested.
 *
 * <pre>{@code
 * public void createUsers(String... args) {
 *     DslParams params = new DslParams(
 *         args,
 *         new RepeatingParamGroup(
 *             new RequiredParam("user"),
 *             new OptionalParam("password").setDefault("aPassword")
 *         ));
 *     for (RepeatingGroup user : params.valueAsGroup("user")) {
 *         driver.createUser(user.value("user"), user.value("password"));
 *     }
 * }
 * }</pre>
 */
public class RepeatingArgGroup implements DslArg
{
    private final RequiredArg identity;
    private final DslArg[] otherArgs;

    public RepeatingArgGroup(final RequiredArg firstArg, final SimpleDslArg<?>... otherArgs)
    {
        this.identity = firstArg;
        this.otherArgs = otherArgs;
    }

    @Override
    public String getName()
    {
        return identity.getName();
    }

    @Override
    public boolean isRequired()
    {
        return false;
    }

    @Override
    public boolean isAllowMultipleValues()
    {
        return false;
    }

    @Override
    public String getMultipleValueSeparator()
    {
        return null;
    }

    @Override
    public String[] getAllowedValues()
    {
        return null;
    }

    /**
     * Get the {@link RequiredArg} that identifies this {@link RepeatingArgGroup}.
     *
     * @return the {@link RequiredArg}.
     */
    public RequiredArg getIdentity()
    {
        return identity;
    }

    /**
     * Get all the {@link DslArg DslArgs}, except for the {@link #getIdentity() identity argument} that comprise this
     * {@link RepeatingArgGroup}.
     *
     * @return the {@link DslArg DslArgs}.
     */
    public DslArg[] getOtherArgs()
    {
        return otherArgs;
    }

    @Override
    public <T> T fold(final Function<RequiredArg, T> ifRequired, final Function<OptionalArg, T> ifOptional, final Function<RepeatingArgGroup, T> ifGroup)
    {
        return ifGroup.apply(this);
    }
}
