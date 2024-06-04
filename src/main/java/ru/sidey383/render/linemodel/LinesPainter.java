package ru.sidey383.render.linemodel;

import ru.sidey383.render.linemodel.model.LinesSupplier;

import java.awt.*;
import java.util.Collection;

public interface LinesPainter {

    void createImage(Collection<? extends LinesSupplier> supplier, Graphics2D g, int width, int height);

}
