/*
 * CoverageDetectorConfig.java
 * 
 * Copyright (c) 2011, Christoph Käding, DFKI. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package de.dfki.km.text20.lightning.plugins.saliency.textdetector;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import de.dfki.km.text20.lightning.plugins.PluginInformation;
import de.dfki.km.text20.lightning.plugins.saliency.SaliencyDetector;
import de.dfki.km.text20.lightning.plugins.saliency.textdetector.gui.TextDetectorConfigImpl;

/**
 * @author Christoph Käding
 *
 */
@PluginImplementation
public class TextDetector implements SaliencyDetector {

    private PluginInformation information;

    private TextDetectorProperties properties;

    private TextDetectorWorker worker;

    private long timeStamp;

    /**
     * 
     */
    public TextDetector() {
        this.information = new PluginInformation("Text Detector", "alpha 0.1", true);
    }

    /* (non-Javadoc)
     * @see de.dfki.km.text20.lightning.plugins.CommonPluginInterface#showGui()
     */
    @Override
    public JFrame getGui() {
        return new TextDetectorConfigImpl();
    }

    /* (non-Javadoc)
     * @see de.dfki.km.text20.lightning.plugins.CommonPluginInterface#start()
     */
    @Override
    public void start() {
        this.properties = TextDetectorProperties.getInstance();
        this.timeStamp = System.currentTimeMillis();
        this.worker = new TextDetectorWorker();
    }

    /* (non-Javadoc)
     * @see de.dfki.km.text20.lightning.plugins.CommonPluginInterface#stop()
     */
    @Override
    public void stop() {
        this.properties.writeProperties();
    }

    /* (non-Javadoc)
     * @see de.dfki.km.text20.lightning.plugins.saliency.SaliencyDetector#analyse(java.awt.image.BufferedImage)
     */
    @SuppressWarnings("rawtypes")
    @Override
    public Point analyse(BufferedImage screenShot) {
        // initialize variables
        int textSize = 0;
        double coverage = 0;
        GetImageText analyzer = new GetImageText(screenShot, this.properties.getDestinyFact(), this.properties.getMass(), this.properties.getDist1(), this.properties.getDistFact(), this.properties.getDist2(), this.properties.getLetterHeight(), this.properties.getStemSize(), this.properties.getLineSize(), this.properties.isUseMerge(), this.properties.isUseDelete());
        LinkedList boxes = analyzer.getTextBoxes();

        // calculate coverage
        for (Object textRegion : boxes) {
            if (textRegion instanceof TextRegion) {
                textSize = textSize + (((TextRegion) textRegion).width() * ((TextRegion) textRegion).height());
            }
        }
        coverage = ((double) 100 / (double) (screenShot.getWidth() * screenShot.getHeight())) * textSize;

        System.out.print("coverage: " + ((double) Math.round(coverage * 100) / 100) + "% -> ");

        if (this.properties.isDebug()) {
            try {
                new File("./plugins/TextDetector/debug/Session_" + this.timeStamp).mkdirs();
                ImageIO.write(analyzer.isolateText(boxes), "png", new File("./plugins/TextDetector/debug/Session_" + this.timeStamp + "/" + System.currentTimeMillis() + "_TextDetectorDebug.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (coverage > this.properties.getThreshold()) {
            System.out.println("text search");
            return this.worker.textAnalyse(boxes, screenShot.getHeight());
        }
        System.out.println("normal search");
        return this.worker.normalAnalyse(analyzer.getContrastImage());

    }

    /* (non-Javadoc)
     * @see de.dfki.km.text20.lightning.plugins.saliency.SaliencyDetector#getInformation()
     */
    @Override
    public PluginInformation getInformation() {
        return this.information;
    }

}
