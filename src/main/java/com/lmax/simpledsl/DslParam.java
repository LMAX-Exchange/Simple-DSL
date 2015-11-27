package com.lmax.simpledsl;

public abstract class DslParam
{
    /** @deprecated With the addition of covariant return types, this should no longer be necessary. */
    @Deprecated
    public RequiredParam getAsRequiredParam() {
        return null;
    }

    public abstract SimpleDslParam getAsSimpleDslParam();

    public abstract RepeatingParamGroup asRepeatingParamGroup();

    public abstract int consume(int currentPosition, NameValuePair... args);

    public boolean isRequired()
    {
        return false;
    }

    public abstract boolean hasValue();

    public abstract String getName();

    public boolean isValid()
    {
        return true;
    }
}
