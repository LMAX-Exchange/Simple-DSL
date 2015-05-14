package com.lmax.simpledsl;


import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public abstract class DslValues
{
    public abstract String value(String name);

    public Optional<String> valueAsOptional(String name)
    {
        return Optional.ofNullable(value(name));
    }

    public abstract String[] values(String name);

    public List<String> valuesAsList(String name)
    {
        return Arrays.asList(values(name));
    }

    public Optional<List<String>> valuesAsOptional(String name)
    {
        final List<String> values = valuesAsList(name);
        return values.isEmpty() ? Optional.empty() : Optional.of(values);
    }

    public int valueAsInt(final String name)
    {
        return Integer.parseInt(value(name));
    }

    public long valueAsLong(final String name)
    {
        return Long.valueOf(value(name));
    }

    public boolean valueAsBoolean(final String name)
    {
        return Boolean.valueOf(value(name));
    }

    public BigDecimal valueAsBigDecimal(final String name)
    {
        final String value = value(name);
        return value != null ? new BigDecimal(value) : null;
    }

    public double valueAsDouble(final String name)
    {
        return Double.valueOf(value(name));
    }

    public String valueAsParam(final String name)
    {
        final String value = value(name);
        return value != null ? name + ": " + value : null;
    }

    public int[] valuesAsInts(final String name)
    {
        final String[] values = values(name);
        final int[] parsedValues = new int[values.length];
        for (int i = 0; i < values.length; i++)
        {
            parsedValues[i] = Integer.parseInt(values[i]);
        }
        return parsedValues;
    }

    public long[] valuesAsLongs(final String name)
    {
        final String[] values = values(name);
        final long[] parsedValues = new long[values.length];
        for (int i = 0; i < values.length; i++)
        {
            parsedValues[i] = Long.parseLong(values[i]);
        }
        return parsedValues;
    }

    public BigDecimal[] valuesAsBigDecimals(final String name)
    {
        final String[] values = values(name);
        final BigDecimal[] parsedValues = new BigDecimal[values.length];
        for (int i = 0; i < values.length; i++)
        {
            parsedValues[i] = new BigDecimal(values[i]);
        }
        return parsedValues;
    }

    public double[] valuesAsDoubles(final String name)
    {
        final String[] values = values(name);
        final double[] parsedValues = new double[values.length];
        for (int i = 0; i < values.length; i++)
        {
            parsedValues[i] = Double.parseDouble(values[i]);
        }
        return parsedValues;
    }

    public abstract boolean hasValue(String name);
}
