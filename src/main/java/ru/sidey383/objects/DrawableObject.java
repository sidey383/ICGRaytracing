package ru.sidey383.objects;

import ru.sidey383.linemodel.model.LinesSupplier;
import ru.sidey383.raytrace.RaytraceObject;

public interface DrawableObject {
    LinesSupplier getLineSupplier();
    RaytraceObject getRaytraceObject();
    BoxDescription getBoxDescription();
}
