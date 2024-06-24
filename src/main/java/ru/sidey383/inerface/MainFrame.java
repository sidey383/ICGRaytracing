package ru.sidey383.inerface;

import ru.sidey383.inerface.actions.*;
import ru.sidey383.inerface.view.RenderSceneView;
import ru.sidey383.model.ApplicationParameters;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Optional;

public class MainFrame extends JFrame {

    public static void main(String[] args) {
        new MainFrame();
    }

    private final ApplicationParameters parameters = new ApplicationParameters();

    private final ErrorDialog errorDialog = new ErrorDialog("Main frame");

    private final JMenuBar menuBar = new JMenuBar();
    private final JMenu sceneItem = new JMenu("Scene");
    private final SceneController sceneController;
    private final JMenuItem renderSettings = new JMenuItem("Render settings");
    private final JMenuItem initCamera = new JMenuItem("Init camera");
    private final JMenu filesItem = new JMenu("Files");
    private final JMenuItem saveImage = new JMenuItem("Save image");
    private final JMenuItem openScene = new JMenuItem("Open scene");
    private final JMenuItem loadRender = new JMenuItem("Load render settings");
    private final JMenuItem saveRender = new JMenuItem("Save render settings");
    private final JMenuItem loadSTLModel = new JMenuItem("Load STL model");
    private final JMenuItem about = new JMenuItem("About");
    private final AboutDialog aboutDialog = new AboutDialog();
    private final JMenu debug = new JMenu("Debug");
    private final JMenuItem drawLines = new JMenuItem("Draw lines");
    private final JMenuItem hideLines = new JMenuItem("Hinde lines");
    private final SettingLoader settingLoader;
    private final SettingSaver settingSaver;


    public MainFrame() {
        super("ICGRaytracing");
        setLayout(new BorderLayout());
        this.sceneController = new SceneController(parameters);
        settingSaver = new SettingSaver(parameters, () -> this.getActive().getSize());
        settingLoader = new SettingLoader(parameters, this::update, () -> this.getActive().getSize());
        setSizeAndPosition();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        buildMenu();
        setJMenuBar(menuBar);
        add(sceneController, BorderLayout.CENTER);
        pack();
        setVisible(true);
    }

    private void buildMenu() {

        filesItem.add(saveImage);
        saveImage.addActionListener(this::saveImageAction);
        filesItem.add(openScene);
        openScene.addActionListener((a) -> settingLoader.loadScene(getActive()));
        filesItem.add(loadRender);
        loadRender.addActionListener((a) -> settingLoader.loadRender(getActive()));
        filesItem.add(saveRender);
        saveRender.addActionListener((a) -> settingSaver.saveRender(getActive()));
        filesItem.add(loadSTLModel);
        loadSTLModel.addActionListener((a) -> settingLoader.loadSTL(getActive()));
        sceneItem.add(sceneController.getPreviewItem());
        sceneItem.add(sceneController.getRenderItem());
        sceneItem.add(renderSettings);
        renderSettings.addActionListener((a) -> new RenderSettingsDialog(parameters.getRaytraceSettings(), this::update));
        sceneItem.add(initCamera);
        initCamera.addActionListener((a) -> {
            Dimension d = getActive().getSize();
            parameters.cameraInit(d.width, d.height);
        });

        debug.add(drawLines);
        drawLines.addActionListener((a) -> sceneController.getRenderSceneView().ifPresent(d -> d.setDrawLines(true)));
        debug.add(hideLines);
        hideLines.addActionListener((a) -> sceneController.getRenderSceneView().ifPresent(d -> d.setDrawLines(false)));

        about.addActionListener(this::aboutAction);
        about.setMaximumSize(about.getPreferredSize());
        about.setBackground(Color.WHITE);
        menuBar.add(filesItem);
        menuBar.add(sceneItem);
        menuBar.add(debug);
        menuBar.add(about);
    }

    private JPanel getActive() {
        return sceneController.getRenderSceneView().map(r -> (JPanel) r).orElse(sceneController.getPreviewSceneView());
    }

    private void aboutAction(ActionEvent e) {
        aboutDialog.showHelp();
    }

    private void setSizeAndPosition() {
        setMinimumSize(new Dimension(640, 480));
        setPreferredSize(new Dimension(800, 800));
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(screenSize.width / 2 - 400, screenSize.height / 2 - 400);
    }

    private void update() {
        sceneController.getRenderSceneView().ifPresentOrElse(
                RenderSceneView::updateImage,
                () -> sceneController.getPreviewSceneView().repaint()
        );
    }

    private void saveImageAction(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("PNG", "png"));
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            final BufferedImage image;
            Optional<RenderSceneView> ors = sceneController.getRenderSceneView();
            if (ors.isPresent()) {
                image = ors.get().getImage();
            } else {
                image = sceneController.getPreviewSceneView().createImage();
            }
            try {
                ImageIO.write(image, "png", fileChooser.getSelectedFile());
            } catch (IOException ex) {
                errorDialog.show(ex);
            }
        }
    }

}
