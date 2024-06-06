package ru.sidey383.inerface.actions;

import ru.sidey383.configuration.RenderConfigurationRecord;
import ru.sidey383.configuration.STLModelLoader;
import ru.sidey383.configuration.SceneConfigurationRecord;
import ru.sidey383.math.Vector3;
import ru.sidey383.model.ApplicationParameters;
import ru.sidey383.render.camera.EditableCamera;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.function.Supplier;

public class SettingLoader {

    private final ApplicationParameters parameters;
    private final Runnable sceneUpdate;
    private final Supplier<Dimension> dimensionSupplier;
    private static final FileNameExtensionFilter renderFilter = new FileNameExtensionFilter("Render", "render");
    private static final FileNameExtensionFilter sceneFilter = new FileNameExtensionFilter("Scene", "scene");
    private static final FileNameExtensionFilter stlFilter = new FileNameExtensionFilter("STL", "stl");

    public SettingLoader(ApplicationParameters parameters, Runnable sceneUpdate, Supplier<Dimension> dimensionSupplier) {
        this.parameters = parameters;
        this.sceneUpdate = sceneUpdate;
        this.dimensionSupplier = dimensionSupplier;
    }

    public void loadScene(JComponent component) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(sceneFilter);
        fileChooser.addChoosableFileFilter(renderFilter);
        load(fileChooser, component);
    }

    public void loadSTL(JComponent component) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(stlFilter);
        int res = fileChooser.showOpenDialog(component);
        if (res == JFileChooser.APPROVE_OPTION) {
            STLModelLoader loader = new STLModelLoader(new Vector3(0.5, 0.5, 0.5), new Vector3(0.5, 0.5, 0.5), 10);
            try (InputStream is = new FileInputStream(fileChooser.getSelectedFile())) {
                parameters.getSceneState().setObjects(loader.readSTL(is));
                JOptionPane.showMessageDialog(component, "STL file loaded");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(component, "Can't load STL file");
            }
        }
    }


    public void loadRender(JComponent component) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(renderFilter);
        fileChooser.addChoosableFileFilter(sceneFilter);
        load(fileChooser, component);
    }

    private void load(JFileChooser chooser, JComponent component) {
        int res = chooser.showOpenDialog(component);
        if (res == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            String name = file.getName();
            if (name.endsWith(".render")) {
                Optional<RenderConfigurationRecord> render = readRender(file);
                Optional<SceneConfigurationRecord> scene = foundSecondFile(file).flatMap(this::readScene);
                if (render.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Can't load render from file");
                    return;
                }
                load(scene, render);
                JOptionPane.showMessageDialog(component, scene.isEmpty() ? "Load render from file" : "Load render and scene from file");
            } else if (name.endsWith(".scene")) {
                Optional<SceneConfigurationRecord> scene = readScene(file);
                Optional<RenderConfigurationRecord> render = foundSecondFile(file).flatMap(this::readRender);
                if (scene.isEmpty()) {
                    JOptionPane.showMessageDialog(component, "Can't load scene from file");
                    return;
                }
                load(scene, render);
                JOptionPane.showMessageDialog(component, render.isEmpty() ? "Load scene from file" : "Load render and scene from file");
            } else {
                JOptionPane.showMessageDialog(component, "Unknown file type");
            }
        }
    }

    private void load(Optional<SceneConfigurationRecord> sceneOpt, Optional<RenderConfigurationRecord> renderOpt) {
        if (sceneOpt.isEmpty() && renderOpt.isEmpty()) {
            return;
        }
        if (sceneOpt.isPresent()) {
            SceneConfigurationRecord scene = sceneOpt.get();
            parameters.getRaytraceSettings().setAmbient(scene.ambient());
            parameters.getSceneState().setLight(scene.lights());
            parameters.getSceneState().setObjects(scene.objects());
            if (renderOpt.isEmpty()) {
                Dimension dim = dimensionSupplier.get();
                parameters.setCamera(new EditableCamera(scene.objects(), dim.width, dim.height));
            }
        }
        if (renderOpt.isPresent()) {
            RenderConfigurationRecord render = renderOpt.get();
            parameters.getRaytraceSettings().setBackground(render.background());
            parameters.getRaytraceSettings().setGamma(render.gamma());
            parameters.getRaytraceSettings().setTraceDeep(render.traceDepth());
            parameters.getRaytraceSettings().setQuality(render.quality());
            parameters.setCamera(new EditableCamera(render));
        }
        sceneUpdate.run();
    }


    private Optional<File> foundSecondFile(File f) {
        String name = f.getName();
        String newName;
        if (name.endsWith(".render")) {
            newName = name.substring(0, name.length() - 7) + ".scene";
        } else if (name.endsWith(".scene")) {
            newName = name.substring(0, name.length() - 6) + ".render";
        } else {
            return Optional.empty();
        }
        File newFile = new File(f.getParentFile(), newName);
        if (newFile.exists()) {
            return Optional.of(newFile);
        } else {
            return Optional.empty();
        }
    }

    private Optional<RenderConfigurationRecord> readRender(File f) {
        String value;
        try (InputStream is = new FileInputStream(f)) {
            value = new String(is.readAllBytes());
        } catch (IOException e) {
            return Optional.empty();
        }
        try {
            return Optional.of(RenderConfigurationRecord.parseConfiguration(value));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private Optional<SceneConfigurationRecord> readScene(File f) {
        String value;
        try (InputStream is = new FileInputStream(f)) {
            value = new String(is.readAllBytes());
        } catch (IOException e) {
            return Optional.empty();
        }
        try {
            return Optional.of(SceneConfigurationRecord.parseConfiguration(value));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

}
