package ru.sidey383.raytrace;

import ru.sidey383.math.Vector3;

import java.util.List;
import java.util.function.Consumer;

public class RayTraceTask implements Runnable {

    private final Ray ray;

    private final int depth;
    private final List<RaytraceObject> objects;
    private final List<LightSource> lights;
    private final Consumer<Vector3> colorConsumer;

    private final Vector3 ambient;

    public RayTraceTask(
            Ray ray,
            int depth,
            List<RaytraceObject> objects,
            List<LightSource> lights,
            Vector3 ambient,
            Consumer<Vector3> colorConsumer) {
        this.ray = ray;
        this.depth = depth;
        this.objects = objects;
        this.lights = lights;
        this.colorConsumer = colorConsumer;
        this.ambient = ambient;
    }

    @Override
    public void run() {
        colorConsumer.accept(getColors(ray, depth).color());
    }

    private RaytraceResult getColors(Ray ray, int depth) {
        IntersectionInfo intersection = null;
        for (RaytraceObject o : objects) {
            final IntersectionInfo old = intersection;
            intersection = o.intersect(ray).map(info -> {
                if (old == null || info.distance() < old.distance())
                    return info;
                return null;
            }).orElse(old);
        }
        if (intersection == null)
            return new RaytraceResult(ambient, 1);
        Vector3 color = Vector3.ZERO;

        Vector3 position = ray.point(intersection.distance());
        Vector3 normal = intersection.normal();
        Vector3 viewVector = ray.direction().mul(-1);
        Vector3 light = getLightSourceColor(viewVector, position, normal, intersection.diffuse(), intersection.specular(), intersection.power());
        color = color.add(light);

        if (depth > 0) {
            Vector3 reflection = normal.mul(2).sub(viewVector).normalize();
            Ray reflectionRay = new Ray(position, reflection);
            RaytraceResult reflectionColor = getColors(reflectionRay, depth - 1);
            double f = 1 / (reflectionColor.distance + 1);
            color = color.add(reflectionColor.color.mul(intersection.specular().mul(f)));
        }

        return new RaytraceResult(color, intersection.distance());
    }

    private record RaytraceResult(Vector3 color, double distance) {}

    private Vector3 getLightSourceColor(Vector3 viewVector, Vector3 position, Vector3 normal, Vector3 defuse, Vector3 specular, double power) {
        Vector3 color = Vector3.ZERO;
        for (LightSource light : lights) {
            if (hasIntersection(position, light.position()))
                continue;
            Vector3 l = light.position().sub(position);
            double d = l.length();
            double f = 1 / (d + 1);
            l = l.normalize();
            Vector3 n = normal.normalize();
            Vector3 h = l.add(viewVector.normalize()).normalize();
            double nl = n.dot(l);
            double rv = n.dot(h);
            if (nl > 0)
                color = color.add(defuse.mul(nl).mul(light.color()).mul(f));
            if (rv > 0)
                color = color.add(specular.mul(Math.pow(rv, power)).mul(light.color()).mul(f));
        }
        return color;
    }

    private boolean hasIntersection(Vector3 position, Vector3 light) {
        Ray ray = new Ray(position, light.sub(position));
        for (RaytraceObject o : objects) {
            if (o.intersect(ray).isPresent()) {
                return true;
            }
        }
        return false;
    }

}
