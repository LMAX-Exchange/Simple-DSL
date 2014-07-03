package com.lmax.simpledsl;

import java.math.BigDecimal;

public abstract class DslValues
{
    public abstract String value(String name);

    public abstract String[] values(String name);

    public int valueAsInt(String name)
    {
        return Integer.parseInt(value(name));
    }

    public long valueAsLong(String name)
    {
        return Long.valueOf(value(name));
    }

    public boolean valueAsBoolean(String name)
    {
        return Boolean.valueOf(value(name));
    }

    public BigDecimal valueAsBigDecimal(String name)
    {
        String value = value(name);
        return value != null ? new BigDecimal(value) : null;
    }

    public double valueAsDouble(String name)
    {
        return Double.valueOf(value(name));
    }

    public String valueAsParam(String name)
    {
        String value = value(name);
        return value != null ? name + ": " + value : null;
    }

    public int[] valuesAsInts(String name)
    {
        String[] values = values(name);
        int[] parsedValues = new int[values.length];
        for (int i = 0; i < values.length; i++)
        {
            parsedValues[i] = Integer.parseInt(values[i]);
        }
        return parsedValues;
    }

    public long[] valuesAsLongs(String name)
    {
        String[] values = values(name);
        long[] parsedValues = new long[values.length];
        for (int i = 0; i < values.length; i++)
        {
            parsedValues[i] = Long.parseLong(values[i]);
        }
        return parsedValues;
    }

    public BigDecimal[] valuesAsBigDecimals(String name)
    {
        String[] values = values(name);
        BigDecimal[] parsedValues = new BigDecimal[values.length];
        for (int i = 0; i < values.length; i++)
        {
            parsedValues[i] = new BigDecimal(values[i]);
        }
        return parsedValues;
    }

    public double[] valuesAsDoubles(String name)
    {
        String[] values = values(name);
        double[] parsedValues = new double[values.length];
        for (int i = 0; i < values.length; i++)
        {
            parsedValues[i] = Double.parseDouble(values[i]);
        }
        return parsedValues;
    }

    public abstract boolean hasValue(String name);
}
