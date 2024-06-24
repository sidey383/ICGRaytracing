package ru.sidey383.inerface.actions;

import ru.sidey383.configuration.Quality;
import ru.sidey383.inerface.editor.RenderSizeProvider;
import ru.sidey383.inerface.param.DoubleParam;
import ru.sidey383.inerface.param.IntegerParam;
import ru.sidey383.inerface.param.OptionChooseParam;
import ru.sidey383.math.Vector3;
import ru.sidey383.model.RaytraceSettings;

import javax.swing.*;
import java.util.List;
import java.util.stream.Stream;

public class RenderSettingsDialog extends JDialog {

    private final DoubleParam backgroundR;
    private final DoubleParam backgroundG;
    private final DoubleParam backgroundB;
    private final DoubleParam gamma;
    private final IntegerParam deep;
    private final OptionChooseParam<Quality> quality;
    private final OptionChooseParam<RenderSizeProvider> sizeProvider;
    private final IntegerParam renderWidth;
    private final IntegerParam renderHeight;

    public RenderSettingsDialog(RaytraceSettings raytraceSettings, Runnable onUpdate) {
        super();
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        deep = new IntegerParam("Trace deep", raytraceSettings.getTraceDeep(), 1, 10);
        backgroundR = new DoubleParam("Background red", raytraceSettings.getBackground().x(), 0, 1, 100);
        backgroundG = new DoubleParam("Background green", raytraceSettings.getBackground().y(), 0, 1, 100);
        backgroundB = new DoubleParam("Background blue", raytraceSettings.getBackground().z(), 0, 1, 100);
        gamma = new DoubleParam("Gamma", raytraceSettings.getGamma(), 0.1, 10, 100);
        quality = new OptionChooseParam<>("Quality", raytraceSettings.getQuality(), List.of(Quality.values()), Stream.of(Quality.values()).map(Enum::name).toList());
        sizeProvider = new OptionChooseParam<>(
                "Render size provider",
                raytraceSettings.isCustomSize() ? RenderSizeProvider.CONSTANTS : RenderSizeProvider.WINDOW,
                List.of(RenderSizeProvider.values()),
                List.of("Adaptive", "Constant")
        );
        renderHeight = new IntegerParam("Render height", raytraceSettings.getRenderHeight(), 100, 10000);
        renderWidth = new IntegerParam("Render width", raytraceSettings.getRenderWidth(), 100, 10000);
        JButton apply = new JButton("Apply");
        apply.addActionListener((a) -> {
            raytraceSettings.setTraceDeep(deep.getValue());
            raytraceSettings.setBackground(new Vector3(backgroundR.getValue(), backgroundG.getValue(), backgroundB.getValue()));
            raytraceSettings.setGamma(gamma.getValue());
            raytraceSettings.setQuality(quality.getValue());
            raytraceSettings.setCustomSize(sizeProvider.getValue() == RenderSizeProvider.CONSTANTS);
            raytraceSettings.setRenderHeight(renderHeight.getValue());
            raytraceSettings.setRenderWidth(renderWidth.getValue());
            onUpdate.run();
            JOptionPane.showMessageDialog(this, "Settings applied");
            this.setVisible(false);
        });
        add(deep.editorComponent());
        add(backgroundR.editorComponent());
        add(backgroundG.editorComponent());
        add(backgroundB.editorComponent());
        add(gamma.editorComponent());
        add(quality.editorComponent());
        add(sizeProvider.editorComponent());
        add(renderWidth.editorComponent());
        add(renderHeight.editorComponent());
        add(apply);
        pack();
        setVisible(true);
    }

}
