package com.lmax.simpledsl;

public abstract class DslParam
{
    public abstract RequiredParam getAsRequiredParam();

    public abstract SimpleDslParam getAsSimpleDslParam();

    public abstract RepeatingParamGroup asRepeatingParamGroup();

    public abstract int consume(int currentPosition, NameValuePair... args);

    public final boolean isRequired()
    {
        return getAsRequiredParam() != null;
    }

    public abstract boolean hasValue();

    public abstract String getName();

    public boolean isValid()
    {
        return true;
    }
}
