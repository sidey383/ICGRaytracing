package ru.sidey383.render.raytrace;

import ru.sidey383.math.Vector3;
import ru.sidey383.render.objects.LightSource;
import ru.sidey383.render.raytrace.controller.RaytraceController;

import java.util.concurrent.atomic.AtomicInteger;

public class RayTraceThread extends Thread {
    private static final ThreadGroup THREAD_GROUP = new ThreadGroup("RayTraceThread");

    private final RaytraceController controller;

    private final RaytraceConfiguration configuration;

    private final AtomicInteger counter;

    private boolean isComplete = false;

    public RayTraceThread(RaytraceController controller, RaytraceConfiguration configuration, AtomicInteger counter) {
        super(THREAD_GROUP, (Runnable) null);
        this.controller = controller;
        this.configuration = configuration;
        this.counter = counter;
    }

    public void run() {
        int num;
        while ((num = counter.getAndIncrement()) < controller.total()) {
            int x = num % controller.totalX();
            int y = num / controller.totalX();
            RaytraceResult result = getColors(controller.getRay(x, y), configuration.depth());
            controller.applyRay(result.color(), x, y);
            if (Thread.interrupted()) {
                isComplete = false;
                return;
            }
        }
    }

    public boolean isComplete() {
        return isComplete;
    }

    private RaytraceResult getColors(Ray ray, int depth) {
        IntersectionInfo intersection = null;
        for (RaytraceObject o : configuration.objectList()) {
            final IntersectionInfo old = intersection;
            intersection = o.intersect(ray).map(info -> {
                if (old == null || info.distance() < old.distance())
                    return info;
                return null;
            }).orElse(old);
        }
        if (intersection == null)
            return new RaytraceResult(configuration.background(), 1);
        Vector3 color = configuration.ambient();

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
        for (LightSource light : configuration.lights()) {
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
        for (RaytraceObject o : configuration.objectList()) {
            if (o.intersect(ray).isPresent()) {
                return true;
            }
        }
        return false;
    }

}
