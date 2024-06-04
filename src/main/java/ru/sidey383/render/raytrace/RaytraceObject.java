package ru.sidey383.render.raytrace;

import java.util.Optional;

public interface RaytraceObject {

    Optional<IntersectionInfo> intersect(Ray ray);

    default RaytraceObject composition(RaytraceObject object) {
        return ray -> {
            Optional<IntersectionInfo> info = RaytraceObject.this.intersect(ray);
            if(info.isEmpty())
                return object.intersect(ray);
            Optional<IntersectionInfo> info2 = object.intersect(ray);
            if(info2.isEmpty())
                return info;
            return info.get().distance() < info2.get().distance() ? info : info2;
        };
    }

}
