package ru.sidey383.configuration;

public enum Quality {
    ROUGH, NORMAL, FINE;

    public static Quality getQuality(String name) {
        if (ROUGH.name().equalsIgnoreCase(name))
            return ROUGH;
        else if (NORMAL.name().equalsIgnoreCase(name))
            return NORMAL;
        else if (FINE.name().equalsIgnoreCase(name))
            return FINE;
        else
            throw new IllegalArgumentException("Unknown quality: " + name);
    }

}
