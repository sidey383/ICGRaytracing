package ru.sidey383.linemodel;

import ru.sidey383.linemodel.model.LinesSupplier;

import java.awt.*;
import java.util.Collection;

public interface LinesPainter {

    void createImage(Collection<LinesSupplier> supplier, Graphics2D g, int width, int height);

}
