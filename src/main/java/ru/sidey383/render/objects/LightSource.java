package ru.sidey383.render.objects;

import ru.sidey383.ConfigurationUtility;
import ru.sidey383.math.Vector3;

public record LightSource(Vector3 position, Vector3 color) {

    public LightSource {
        color = ConfigurationUtility.ranged(color);
    }

}
