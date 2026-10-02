package com.invoicecomposer.creational.prototype;

/**
 * Prototype pattern contract: an object that knows how to produce an independent deep copy
 * of itself. The copy shares no mutable state with the original.
 */
public interface Prototype<T> {

    /** Returns a deep copy of this object. */
    T deepCopy();
}
