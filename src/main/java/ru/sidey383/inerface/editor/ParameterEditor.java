package ru.sidey383.inerface.editor;

import ru.sidey383.render.camera.Camera;
import ru.sidey383.configuration.Quality;
import ru.sidey383.configuration.RenderConfigurationRecord;
import ru.sidey383.configuration.SceneConfigurationRecord;
import ru.sidey383.inerface.param.DoubleParam;
import ru.sidey383.inerface.param.IntegerParam;
import ru.sidey383.inerface.param.OptionChooseParam;
import ru.sidey383.math.Vector3;
import ru.sidey383.math.Vector3Record;

import javax.swing.*;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

public class ParameterEditor extends JDialog {

    private final IntegerParam traceDeep;
    private final DoubleParam gamma;
    private final OptionChooseParam<Quality> quality;
    private final DoubleParam backgroundR;
    private final DoubleParam backgroundG;
    private final DoubleParam backgroundB;
    private final DoubleParam ambientR;
    private final DoubleParam ambientG;
    private final DoubleParam ambientB;

    public ParameterEditor(Camera camera, RenderConfigurationRecord renderConfiguration, SceneConfigurationRecord sceneConfiguration, BiConsumer<RenderConfigurationRecord, SceneConfigurationRecord> applyConfig) {
        traceDeep = new IntegerParam("Trace deep", renderConfiguration.traceDepth(), 1, 10);
        gamma = new DoubleParam("Gamma", renderConfiguration.gamma(), 0.1, 10, 100);
        quality = new OptionChooseParam<>(
                "Quality",
                renderConfiguration.quality(),
                List.of(Quality.values()),
                Stream.of(Quality.values()).map(Enum::name).toList()
        );
        Vector3 bgColor = renderConfiguration.background();
        backgroundR = new DoubleParam("Background red", bgColor.get(0), 0, 10, 100);
        backgroundG = new DoubleParam("Background green", bgColor.get(1), 0, 10, 100);
        backgroundB = new DoubleParam("Background blue", bgColor.get(2), 0, 10, 100);
        Vector3 ambient = sceneConfiguration.ambient();
        ambientR = new DoubleParam("Ambient red", ambient.get(0), 0, 10, 100);
        ambientG = new DoubleParam("Ambient red", ambient.get(0), 0, 10, 100);
        ambientB = new DoubleParam("Ambient red", ambient.get(0), 0, 10, 100);

        JButton apply = new JButton("Apply");

        apply.addActionListener((a) -> {
            SceneConfigurationRecord sc = new SceneConfigurationRecord(
                    new Vector3Record(ambientR.getValue(), ambientG.getValue(), ambientB.getValue()),
                    sceneConfiguration.lights(),
                    sceneConfiguration.objects()
            );
            RenderConfigurationRecord rc = new RenderConfigurationRecord(
                    new Vector3Record(backgroundR.getValue(), backgroundG.getValue(), backgroundB.getValue()),
                    gamma.getValue(),
                    traceDeep.getValue(),
                    quality.getValue(),
                    camera.eye(),
                    camera.view(),
                    camera.up(),
                    camera.near(),
                    camera.far(),
                    camera.width(100, 100),
                    camera.height(100, 100)
            );
            applyConfig.accept(rc, sc);
            this.setVisible(false);
        });

        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        add(traceDeep.editorComponent());
        add(gamma.editorComponent());
        add(quality.editorComponent());
        add(backgroundR.editorComponent());
        add(backgroundG.editorComponent());
        add(backgroundB.editorComponent());
        add(ambientR.editorComponent());
        add(ambientG.editorComponent());
        add(ambientB.editorComponent());
        add(apply);
        pack();


    }


}
