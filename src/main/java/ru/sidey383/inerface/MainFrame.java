package ru.sidey383.inerface;

import ru.sidey383.inerface.actions.AboutDialog;
import ru.sidey383.inerface.actions.RenderSettingsDialog;
import ru.sidey383.inerface.actions.SettingLoader;
import ru.sidey383.inerface.actions.SettingSaver;
import ru.sidey383.inerface.view.PreviewSceneView;
import ru.sidey383.inerface.view.RenderSceneView;
import ru.sidey383.model.ApplicationParameters;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Optional;

public class MainFrame extends JFrame implements SceneHolder {

    public static void main(String[] args) {
        new MainFrame();
    }

    private final ApplicationParameters parameters = new ApplicationParameters();

    private final PreviewSceneView previewSceneView = new PreviewSceneView(parameters);

    private RenderSceneView renderSceneView = null;

    private final JMenuBar menuBar = new JMenuBar();
    private final JMenu sceneItem = new JMenu("Scene");
    private final JRadioButtonMenuItem previewItem = new JRadioButtonMenuItem("Preview");
    private final JRadioButtonMenuItem renderItem = new JRadioButtonMenuItem("Render");
    private final JMenuItem renderSettings = new JMenuItem("Render settings");
    private final JMenuItem initCamera = new JMenuItem("Init camera");
    private final JMenu filesItem = new JMenu("Files");
    private final JMenuItem saveImage = new JMenuItem("Save image");
    private final JMenuItem openScene = new JMenuItem("Open scene");
    private final JMenuItem loadRender = new JMenuItem("Load render settings");
    private final JMenuItem saveRender = new JMenuItem("Save render settings");
    private final JMenuItem about = new JMenuItem("About");
    private final AboutDialog aboutDialog = new AboutDialog();

    private final SettingLoader settingLoader;
    private final SettingSaver settingSaver;


    public MainFrame() {
        super("ICGRaytracing");
        setLayout(new BorderLayout());
        settingSaver = new SettingSaver(parameters, () -> this.getActive().getSize());
        settingLoader = new SettingLoader(parameters, this::update, () -> this.getActive().getSize());
        setSizeAndPosition();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setFocusable(true);
        buildMenu();
        setJMenuBar(menuBar);
        add(previewSceneView, BorderLayout.CENTER);
        pack();
        setVisible(true);
    }

    private void buildMenu() {

        ButtonGroup group = new ButtonGroup();
        group.add(renderItem);
        group.add(previewItem);

        filesItem.add(saveImage);
        saveImage.addActionListener(this::saveImageAction);
        filesItem.add(openScene);
        openScene.addActionListener((a) -> settingLoader.loadScene(getActive()));
        filesItem.add(loadRender);
        loadRender.addActionListener((a) -> settingLoader.loadRender(getActive()));
        filesItem.add(saveRender);
        saveRender.addActionListener((a) -> settingSaver.saveRender(getActive()));
        sceneItem.add(previewItem);
        previewItem.addActionListener((a) -> setPreview());
        sceneItem.add(renderItem);
        renderItem.addActionListener((a) -> setRender());
        sceneItem.add(renderSettings);
        renderSettings.addActionListener((a) -> new RenderSettingsDialog(parameters.getRaytraceSettings(), this::update));
        sceneItem.add(initCamera);
        initCamera.addActionListener((a) -> {
            Dimension d = getActive().getSize();
            parameters.cameraInit(d.width, d.height);
        });
        menuBar.add(filesItem);
        menuBar.add(sceneItem);

        about.addActionListener(this::aboutAction);
        about.setMaximumSize(about.getPreferredSize());
        about.setBackground(Color.WHITE);
        menuBar.add(about);
    }

    private void setPreview() {
        if (renderSceneView != null) {
            renderSceneView.stopRender();
            remove(renderSceneView.getScrollPane());
            renderSceneView = null;
        }
        add(previewSceneView, BorderLayout.CENTER);
        previewItem.setSelected(true);
        revalidate();
        previewSceneView.revalidate();
        repaint();
        previewSceneView.repaint();
    }

    private void setRender() {
        if (renderSceneView != null) {
            renderSceneView.stopRender();
            remove(renderSceneView.getScrollPane());
        }
        renderSceneView = new RenderSceneView(parameters, previewSceneView.createImage());
        remove(previewSceneView);
        add(renderSceneView.getScrollPane(), BorderLayout.CENTER);
        renderItem.setSelected(true);
        revalidate();
        repaint();
        renderSceneView.startRender();
        renderSceneView.revalidate();
        renderSceneView.repaint();
    }

    private JPanel getActive() {
        return getRenderSceneView().map(r -> (JPanel) r).orElse(getPreviewSceneView());
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
        if (renderSceneView != null) {
            renderSceneView.updateGamma();
        } else {
            previewSceneView.repaint();
        }
    }

    private void saveImageAction(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("PNG", "png"));
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                ImageIO.write(renderSceneView.getImage(), "png", fileChooser.getSelectedFile());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }


    @Override
    public Optional<RenderSceneView> getRenderSceneView() {
        return Optional.ofNullable(renderSceneView);
    }

    @Override
    public PreviewSceneView getPreviewSceneView() {
        return previewSceneView;
    }
}
