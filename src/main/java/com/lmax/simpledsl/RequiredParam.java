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

public class RequiredParam extends SimpleDslParam
{
    public RequiredParam(final String name)
    {
        super(name);
    }

    @Override
    public RequiredParam getAsRequiredParam()
    {
        return this;
    }

    @Override
    public boolean isValid()
    {
        return getValues().length != 0;
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

    @Override
    public RequiredParam setAllowedValues(final String... allowedValues)
    {
        super.setAllowedValues(allowedValues);
        return this;
    }

    @Override
    public RequiredParam setAllowMultipleValues()
    {
        super.setAllowMultipleValues();
        return this;
    }

    @Override
    public RequiredParam setAllowMultipleValues(final String delimiter)
    {
        super.setAllowMultipleValues(delimiter);
        return this;
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
