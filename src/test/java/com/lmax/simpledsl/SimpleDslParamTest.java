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

public class SimpleDslParamTest
{
    @Test
    public void testGetValueReturnsTheValueAdded()
    {
        SimpleDslParam param = new TestParam("foo");
        param.addValue("12");
        Assert.assertEquals("12", param.getValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSecondCallToAddValueWhenMultipleValuesNotAllowedThrowsIllegalArgumentException()
    {
        SimpleDslParam param = new TestParam("foo");
        param.addValue("12");
        param.addValue("12");
    }

    @Test
    public void testCanAddMultipleValuesWhenMultipleValuesAllowed()
    {
        SimpleDslParam param = new TestParam("foo").setAllowMultipleValues();
        param.addValue("12");
        param.addValue("34");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCannotAddMultipleValuesIfAddMultipleValuesHasNotBeenCalled()
    {
        SimpleDslParam param = new TestParam("foo");
        param.addValue("12");
        param.addValue("34");
    }

    @Test
    public void testGetValuesReturnsAllTheValuesAdded()
    {
        SimpleDslParam param = new TestParam("foo").setAllowMultipleValues();
        param.addValue("12");
        param.addValue("34");
        Assert.assertArrayEquals(new String[]{"12", "34"}, param.getValues());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetValueThrowsAnExceptionIfTheParamAllowsMultipleValues()
    {
        SimpleDslParam param = new TestParam("foo").setAllowMultipleValues();
        param.getValue();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddingAnAllowedValuesListRestrictsWhichValuesAreAllowed()
    {
        SimpleDslParam param = new TestParam("foo").setAllowedValues("12", "34").setAllowMultipleValues();
        param.addValue("12");
        param.addValue("34");
        param.addValue("56");
    }

    @Test
    public void requiredValuesAreMatchedCaseInsensitivelyButReturnedInTheCorrectCase()
    {
        SimpleDslParam param = new TestParam("foo").setAllowedValues("abc", "def").setAllowMultipleValues();
        param.addValue("abc");
        param.addValue("DeF");
        Assert.assertArrayEquals(new String[]{"abc", "def"}, param.getValues());
    }

    private static class TestParam extends SimpleDslParam
    {
        public TestParam(final String name)
        {
            super(name);
        }

        @Override
        public RequiredParam getAsRequiredParam()
        {
            return null;
        }

    }
}
