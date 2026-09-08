package de.stromentlastung.zahlung.common;

/** Die Rolle passt, der Mandant nicht: fremdes Unternehmen, fremde Dienststelle. */
public class ZugriffVerweigert extends RuntimeException {

    public ZugriffVerweigert(String message) {
        super(message);
    }
}
