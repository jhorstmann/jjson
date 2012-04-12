package net.jhorstmann.json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

public class JSONArray extends ArrayList {

    @Override
    public Iterator iterator() {
        return new JSONIterator(super.iterator());
    }

    @Override
    public ListIterator listIterator() {
        return new JSONListIterator(super.listIterator());
    }

    @Override
    public ListIterator listIterator(int index) {
        return new JSONListIterator(super.listIterator(index));
    }

    public JSONListIterator jsonListIterator() {
        return new JSONListIterator(super.listIterator());
    }

    @Override
    public String toString() {
        return toString(false);
    }

    public String toString(boolean pretty) {
        try {
            return JSONUtils.format(this, pretty);
        } catch (IOException ex) {
            throw new RuntimeException(ex.getClass().getSimpleName() + " in " + getClass().getSimpleName() + ".toString()", ex);
        }
    }
}
