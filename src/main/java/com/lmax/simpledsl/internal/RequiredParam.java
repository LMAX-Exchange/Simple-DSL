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
package com.lmax.simpledsl.internal;

import com.lmax.simpledsl.api.RequiredArg;

class RequiredParam extends SimpleDslParam<RequiredArg, RequiredParam>
{
    RequiredParam(final String name)
    {
        this(new RequiredArg(name));
    }

    RequiredParam(final RequiredArg arg)
    {
        super(arg);
    }

    @Override
    boolean isValid()
    {
        return getValues().length != 0;
    }

    @Override
    int consume(final int currentPosition, final NameValuePair... args)
    {
        int position = currentPosition;
        while (position < args.length && consumeSingleParam(args, position))
        {
            position++;
            if (!getArg().isAllowMultipleValues())
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
        return argName == null || getArg().getName().equalsIgnoreCase(argName);
    }

    private NameValuePair getArg(final NameValuePair[] args, final int position)
    {
        if (position >= args.length)
        {
            throw new IllegalArgumentException("Missing parameter: " + getArg().getName());
        }
        return args[position];
    }
}
