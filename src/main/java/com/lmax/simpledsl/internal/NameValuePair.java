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

class NameValuePair
{
    private static final String SPLIT_NAME_VALUE_REGEX = "=|:";

    private final String originalValue;
    private final String name;
    private final String value;

    NameValuePair(final String argString)
    {
        originalValue = argString;
        final String[] splitArg = (argString + " ").split(SPLIT_NAME_VALUE_REGEX);
        if (splitArg.length == 1)
        {
            name = null;
            value = argString.trim();
        }
        else
        {
            name = splitArg[0].trim();
            // result is the remainder of argString, in case it contains the split regex chars too
            value = argString.substring(splitArg[0].length() + 1).trim();
        }
    }

    public String getName()
    {
        return name;
    }

    public String getValue()
    {
        return value;
    }

    @Override
    public String toString()
    {
        return originalValue;
    }
}
