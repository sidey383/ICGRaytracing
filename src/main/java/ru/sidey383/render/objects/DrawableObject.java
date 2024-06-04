package ru.sidey383.render.objects;

import ru.sidey383.render.linemodel.model.LinesSupplier;
import ru.sidey383.render.raytrace.RaytraceObject;

public interface DrawableObject extends LinesSupplier, RaytraceObject, BoxedObject {
}
