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
    static final NameValuePair NULL = new NameValuePair(null, null, null);

    private static final String SPLIT_NAME_VALUE_REGEX = "[=:]";

    public final String originalValue;
    public final String name;
    public final String value;

    NameValuePair(final String originalValue, final String name, final String value)
    {
        this.originalValue = originalValue;
        this.name = name;
        this.value = value;
    }

    static NameValuePair fromArgumentString(final String argString)
    {
        if (argString == null)
        {
            return NULL;
        }
        else
        {
            final String[] splitArg = (argString + " ").split(SPLIT_NAME_VALUE_REGEX, 2);
            return splitArg.length == 2
                    ? new NameValuePair(argString, splitArg[0].trim(), splitArg[1].trim())
                    : new NameValuePair(argString, null, argString.trim());
        }
    }
}
