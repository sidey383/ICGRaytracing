package ru.sidey383.inerface;

import ru.sidey383.ConfigurationUtility;
import ru.sidey383.configuration.RenderConfiguration;
import ru.sidey383.configuration.SceneConfiguration;
import ru.sidey383.inerface.panel.LineSceneView;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;

public class MainFrame extends JFrame {

    public static void main(String[] args) throws IOException {
        new MainFrame();
    }

    public MainFrame() throws IOException {
        super("ICGRaytracing");
        setSizeAndPosition();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
//        RenderConfiguration configuration = RenderConfiguration.parseConfiguration(ConfigurationUtility.readFile(Path.of("sample.render")));
//        SceneConfiguration sceneConfiguration = SceneConfiguration.readConfiguration(ConfigurationUtility.readFile(Path.of("sample.scene")));
//        LineSceneView lineSceneView = new LineSceneView(sceneConfiguration, configuration);
//        lineSceneView.setFocusable(true);
//        setFocusable(false);
//        add(lineSceneView, BorderLayout.CENTER);
        pack();
        setVisible(true);
    }

    private void setSizeAndPosition() {
        setMinimumSize(new Dimension(640, 480));
        setPreferredSize(new Dimension(800, 800));
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(screenSize.width / 2 - 400, screenSize.height / 2 - 400);
    }

}
