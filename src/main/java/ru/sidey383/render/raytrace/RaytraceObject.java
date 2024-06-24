package ru.sidey383.render.raytrace;

import org.jetbrains.annotations.Nullable;

public interface RaytraceObject {

    @Nullable
    IntersectionInfo intersect(Ray ray);

    default RaytraceObject composition(RaytraceObject object) {
        return ray -> {
            IntersectionInfo info1 = RaytraceObject.this.intersect(ray);
            if(info1 == null)
                return object.intersect(ray);
            IntersectionInfo info2 = object.intersect(ray);
            if(info2 == null)
                return info1;
            return info1.distance() < info2.distance() ? info1 : info2;
        };
    }

}
