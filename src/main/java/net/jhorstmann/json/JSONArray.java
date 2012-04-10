package net.jhorstmann.json;

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
}
