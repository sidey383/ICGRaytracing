package ru.sidey383.inerface.actions;

import ru.sidey383.configuration.RenderConfigurationRecord;
import ru.sidey383.model.ApplicationParameters;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Supplier;

public class SettingSaver {

    private final ApplicationParameters parameters;
    private final Supplier<Dimension> dimensionSupplier;
    private static final FileNameExtensionFilter renderFilter = new FileNameExtensionFilter("Render", "render");

    public SettingSaver(ApplicationParameters parameters, Supplier<Dimension> dimensionSupplier) {
        this.parameters = parameters;
        this.dimensionSupplier = dimensionSupplier;
    }

    public void saveRender(JComponent component) {
        Dimension dim = dimensionSupplier.get();
        RenderConfigurationRecord render = new RenderConfigurationRecord(
                parameters.getRaytraceSettings().getBackground(),
                parameters.getRaytraceSettings().getGamma(),
                parameters.getRaytraceSettings().getTraceDeep(),
                parameters.getRaytraceSettings().getQuality(),
                parameters.getCamera().eye(),
                parameters.getCamera().view(),
                parameters.getCamera().up(),
                parameters.getCamera().near(),
                parameters.getCamera().far(),
                parameters.getCamera().width(dim.height, dim.width),
                parameters.getCamera().height(dim.height, dim.width)
        );
        String value = render.writeConfiguration();
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(renderFilter);
        int res = fileChooser.showSaveDialog(component);
        if (res == JFileChooser.APPROVE_OPTION) {
            try (OutputStream out = new FileOutputStream(fileChooser.getSelectedFile())) {
                    out.write(value.getBytes());
                    JOptionPane.showMessageDialog(component, "Render saved to file");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(component, "Can't save render to file");
            }
        }
    }

}
