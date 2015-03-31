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

import org.junit.Assert;
import org.junit.Test;

public class NameValuePairTest
{
    @Test
    public void testSplittingNameAndValueWithEquals()
    {
        final NameValuePair pair = new NameValuePair("a=1");
        Assert.assertEquals("a", pair.getName());
        Assert.assertEquals("1", pair.getValue());
    }

    @Test
    public void testSplittingNameAndValueWithColon()
    {
        final NameValuePair pair = new NameValuePair("a:1");
        Assert.assertEquals("a", pair.getName());
        Assert.assertEquals("1", pair.getValue());
    }

    @Test
    public void testTrimmingWhitespace()
    {
        final NameValuePair pair = new NameValuePair(" a = 1 ");
        Assert.assertEquals("a", pair.getName());
        Assert.assertEquals("1", pair.getValue());
    }

    @Test
    public void testMultipleSplitTokensArePreservedInTheValue()
    {
        final NameValuePair pair = new NameValuePair("message: ERROR: Something went wrong!");
        Assert.assertEquals("message", pair.getName());
        Assert.assertEquals("ERROR: Something went wrong!", pair.getValue());
    }

    @Test
    public void testUnnamedPairSetsTheNameToNullAndAssignsEverythingToTheValue()
    {
        final NameValuePair pair = new NameValuePair("value without a name");
        Assert.assertEquals(null, pair.getName());
        Assert.assertEquals("value without a name", pair.getValue());
    }

    @Test
    public void testEmptyStringIsUsedForAnEmptyValueRatherThanNull()
    {
        final NameValuePair pair = new NameValuePair("name:");
        Assert.assertEquals("name", pair.getName());
        Assert.assertEquals("", pair.getValue());
    }
}
