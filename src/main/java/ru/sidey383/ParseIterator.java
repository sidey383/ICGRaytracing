package ru.sidey383;

import java.util.Iterator;

public class ParseIterator implements Iterator<String> {

    private Iterator<String> iterator;

    private String current;

    public ParseIterator(Iterator<String> iterator) {
        if(iterator == null)
            throw new IllegalArgumentException("Iterator must be not null");
        this.iterator = iterator;
        this.current = readNext();
    }

    private String readNext() {
        while(iterator.hasNext()) {
            String current = iterator.next().split("//")[0];
            if(!current.isBlank()) {
                return current;
            }
        }
        return null;
    }

    @Override
    public boolean hasNext() {
        return current != null;
    }

    @Override
    public String next() {
        if (current == null)
            throw new IllegalStateException("No more elements");
        String cur = current;
        current = readNext();
        return cur;
    }
}
