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

public class RequiredParam extends DslParam
{
    public RequiredParam(String name)
    {
        super(name);
    }

    @Override
    public RequiredParam getAsRequiredParam()
    {
        return this;
    }

    @Override
    public OptionalParam getAsOptionalParam()
    {
        return null;
    }

    public int consume(final int currentPosition, final String... args)
    {
        if (!consumeSingleParam(args, currentPosition))
        {
            throw new IllegalArgumentException("Expected required parameter '" + name + "' missing.");
        }
        int position = currentPosition + 1;

        if (allowMultipleValues)
        {
            while (position < args.length)
            {
                if (!consumeSingleParam(args, position))
                {
                    break;
                }
                position++;
            }

        }
        return position;
    }

    private boolean consumeSingleParam(final String[] args, final int position)
    {
        NameValuePair nameValue = new NameValuePair(getArg(args, position));
        if (!matches(nameValue.getName()))
        {
            return false;
        }
        addValue(nameValue.getValue());
        return true;
    }

    private boolean matches(String argName)
    {
        return argName == null || name.equalsIgnoreCase(argName);
    }

    private String getArg(final String[] args, final int position)
    {
        if (position >= args.length)
        {
            throw new IllegalArgumentException("Missing parameter: " + name);
        }
        return args[position];
    }
}
