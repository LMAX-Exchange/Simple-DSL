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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class NameValuePairTest
{
    @Test
    public void shouldSplitNameAndValueWithEquals()
    {
        final NameValuePair pair = NameValuePair.fromArgumentString("a=1");
        assertEquals("a", pair.name);
        assertEquals("1", pair.value);
    }

    @Test
    public void shouldSplitNameAndValueWithColon()
    {
        final NameValuePair pair = NameValuePair.fromArgumentString("a:1");
        assertEquals("a", pair.name);
        assertEquals("1", pair.value);
    }

    @Test
    public void shouldTrimWhitespace()
    {
        final NameValuePair pair = NameValuePair.fromArgumentString(" a = 1 ");
        assertEquals("a", pair.name);
        assertEquals("1", pair.value);
    }

    @Test
    public void shouldPreserveValueIfMultipleSplitTokensArePresent()
    {
        final NameValuePair pair = NameValuePair.fromArgumentString("message: ERROR: Something went wrong!");
        assertEquals("message", pair.name);
        assertEquals("ERROR: Something went wrong!", pair.value);
    }

    @Test
    public void shouldSetTheNameToNullAndAssignsEverythingToTheValueInAnUnnamedPair()
    {
        final NameValuePair pair = NameValuePair.fromArgumentString("value without a name");
        assertNull(pair.name);
        assertEquals("value without a name", pair.value);
    }

    @Test
    public void shouldUseAnEmptyStringWhenNoValueIsProvided()
    {
        final NameValuePair pair = NameValuePair.fromArgumentString("name:");
        assertEquals("name", pair.name);
        assertEquals("", pair.value);
    }
}
