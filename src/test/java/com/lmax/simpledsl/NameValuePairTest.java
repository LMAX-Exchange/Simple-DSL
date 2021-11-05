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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class NameValuePairTest
{
    @Test
    public void shouldSplitNameAndValueWithEquals()
    {
        final NameValuePair pair = new NameValuePair("a=1");
        assertEquals("a", pair.getName());
        assertEquals("1", pair.getValue());
    }

    @Test
    public void shouldSplitNameAndValueWithColon()
    {
        final NameValuePair pair = new NameValuePair("a:1");
        assertEquals("a", pair.getName());
        assertEquals("1", pair.getValue());
    }

    @Test
    public void shouldTrimWhitespace()
    {
        final NameValuePair pair = new NameValuePair(" a = 1 ");
        assertEquals("a", pair.getName());
        assertEquals("1", pair.getValue());
    }

    @Test
    public void shouldPreserveValueIfMultipleSplitTokensArePresent()
    {
        final NameValuePair pair = new NameValuePair("message: ERROR: Something went wrong!");
        assertEquals("message", pair.getName());
        assertEquals("ERROR: Something went wrong!", pair.getValue());
    }

    @Test
    public void shouldSetTheNameToNullAndAssignsEverythingToTheValueInAnUnnamedPair()
    {
        final NameValuePair pair = new NameValuePair("value without a name");
        assertNull(pair.getName());
        assertEquals("value without a name", pair.getValue());
    }

    @Test
    public void shouldUseAnEmptyStringWhenNoValueIsProvided()
    {
        final NameValuePair pair = new NameValuePair("name:");
        assertEquals("name", pair.getName());
        assertEquals("", pair.getValue());
    }
}
