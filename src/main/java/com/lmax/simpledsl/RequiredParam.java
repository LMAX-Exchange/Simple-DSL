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
package com.lmax.simpledsl;

/**
 * A required parameter. An exception will be thrown when parsing the arguments if a value is not supplied for this parameter.
 * The name of the parameter may be omitted if required parameters are listed first. For example with the params:
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
 * Required parameters can be supplied out of order if the name is specified, so the following is also equivalent:
 *
 * <pre>{@code
 * login("password: myPassword", "user: joan");
 * }</pre>
 * <p>
 * It is highly recommended to include the parameter name if there is any ambiguity about the meaning of the parameter.
 *
 * <p>By default, only a single value is allowed. Multiple values can be allowed by calling {@link #setAllowMultipleValues()}</p>
 */
public class RequiredParam extends SimpleDslParam<RequiredParam>
{
    /**
     * Create a required parameter.
     *
     * @param name the name of the parameter.
     */
    public RequiredParam(final String name)
    {
        super(name);
    }

    @Override
    public boolean isValid()
    {
        return getValues().length != 0;
    }

    @Override
    public boolean isRequired()
    {
        return true;
    }

    @Override
    public int consume(final int currentPosition, final NameValuePair... args)
    {
        int position = currentPosition;
        while (position < args.length && consumeSingleParam(args, position))
        {
            position++;
            if (!allowMultipleValues)
            {
                break;
            }
        }
        return position;
    }

    private boolean consumeSingleParam(final NameValuePair[] args, final int position)
    {
        final NameValuePair nameValue = getArg(args, position);
        if (!matches(nameValue.getName()))
        {
            return false;
        }
        addValue(nameValue.getValue());
        return true;
    }

    private boolean matches(final String argName)
    {
        return argName == null || name.equalsIgnoreCase(argName);
    }

    private NameValuePair getArg(final NameValuePair[] args, final int position)
    {
        if (position >= args.length)
        {
            throw new IllegalArgumentException("Missing parameter: " + name);
        }
        return args[position];
    }
}
