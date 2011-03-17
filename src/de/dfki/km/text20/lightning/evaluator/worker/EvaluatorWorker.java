/*
 * EvaluatorWorker.java
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
package de.dfki.km.text20.lightning.evaluator.worker;

import static net.jcores.CoreKeeper.$;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import javax.imageio.ImageIO;

import net.xeoh.plugins.diagnosis.local.DiagnosisChannel;

import de.dfki.km.text20.lightning.plugins.saliency.SaliencyDetector;
import de.dfki.km.text20.lightning.worker.training.StorageContainer;

/**
 * The EvaluationWorker runs the given plugin with the given container, collects the data,
 * draws the results in a png-file and writes the log-file.
 * 
 * @author Christoph Käding
 *
 */
public class EvaluatorWorker {

    /** map which stores evaluation container as value with given identifiers as key */
    private Map<String, EvaluationContainer> results;

    /** timestamp of the start of this evaluation session */
    private long currentTimeStamp;

    /** stores over all results */
    private EvaluationContainer overAllResults;

    /** logging channel */
    private DiagnosisChannel<String> channel;

    /**
     * creates a new evaluation worker and initializes necessary variables
     * 
     * @param currentTimeStamp
     * @param channel 
     */
    public EvaluatorWorker(long currentTimeStamp, DiagnosisChannel<String> channel) {
        this.results = new Hashtable<String, EvaluationContainer>();
        this.currentTimeStamp = currentTimeStamp;
        this.overAllResults = null;
        this.channel = channel;
    }

    /**
     * evaluates the given container with the given detector
     * 
     * @param identifier
     *      for the map where the results are stored
     * @param user
     *      which should be used
     * @param detector
     *      which should be used
     * @param container
     *      which should be used
     * @param drawImage
     *      boolean which indicates if the results should be written into an png-file
     * @param path
     *      path were the data-directory is located, where the container-file and the screenshots are
     */
    public void evaluate(String identifier, String user, SaliencyDetector detector,
                         StorageContainer container, boolean drawImage, String path) {

        // initialize variables
        BufferedImage screenShot = null;
        Point point = new Point();

        // test if associated screenshot is available
        File screenFile = new File(path + "/data/" + user + "/" + user + "_" + container.getTimestamp() + ".png");
        if (!screenFile.exists()) return;

        // read screenshot
        try {
            screenShot = ImageIO.read(screenFile);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        // calculate offset by running the detector
        point = detector.analyse(screenShot);
        point.translate(screenShot.getHeight() / 2, screenShot.getWidth() / 2);

        // write the png-file
        if (drawImage)
            this.drawPicture(detector, point, path + "/evaluation/" + user + "_" + this.currentTimeStamp + "/" + user + "_" + container.getTimestamp() + "_evaluated.png", screenShot, container.getMousePoint());

        // add results to over all storage
        if (this.overAllResults == null) this.overAllResults = new EvaluationContainer(detector.getInformation().getId(), point.distance(container.getMousePoint()), path + "/evaluation/evaluation.log", user, this.currentTimeStamp);
        else
            this.overAllResults.add(detector.getInformation().getId(), point.distance(container.getMousePoint()));

        // check if the identifier is already in the map
        if (this.results.containsKey(identifier)) {

            // add distance to the already existing identifier
            this.results.get(identifier).add(detector.getInformation().getId(), point.distance(container.getMousePoint()));
        } else {

            // creates net map entry by identifier and adds new evaluation container to it
            this.results.put(identifier, new EvaluationContainer(detector.getInformation().getId(), point.distance(container.getMousePoint()), path + "/evaluation/evaluation.log", user, this.currentTimeStamp));
        }

    }

    /**
     * A call of this method indicates that the evaluation is finished and the result file can be updated.
     * 
     * @param writeLog 
     * @param detectors 
     * 
     * @return name of best ranked detector 
     */
    @SuppressWarnings("boxing")
    public String getBestResult(boolean writeLog, ArrayList<SaliencyDetector> detectors) {

        // test if some data are collected
        if(this.results.size() == 0) 
            return "...nothing";
        
        // initialize variables
        double bestValue = Double.MAX_VALUE;
        int bestKey = -1;
        double veryBestValue = Double.MAX_VALUE;
        int veryBestKey = -1;

        // run through the keyset of the result map
        for (String key : this.results.keySet()) {

            // run through the ids of every entry of the result map
            for (int i = 0; i < this.results.get(key).getIds().size(); i++) {

                // check if the current value is better then the best value, the best value is only for this identifier
                if (bestValue > this.results.get(key).getAverage(this.results.get(key).getIds().get(i))) {

                    // store new best value
                    bestKey = this.results.get(key).getIds().get(i);
                    bestValue = this.results.get(key).getAverage(this.results.get(key).getIds().get(i));
                }

                // write the evaluation.log
                if (writeLog) {
                    if (i == 0)
                        $(this.results.get(key).getLogPath()).file().append("Session - User: " + this.results.get(key).getName() + ", Timestamp: " + this.results.get(key).getTimeStamp() + ", Number of DataSets: " + this.results.get(key).getSize() + "\n");
                    $(this.results.get(key).getLogPath()).file().append(detectors.get(this.results.get(key).getIds().get(i)).getInformation().getDisplayName() + ": " + ((double) Math.round(this.results.get(key).getAverage(this.results.get(key).getIds().get(i)) * 100) / 100) + " Pixel distance averaged.\n");
                    if (i == this.results.get(key).getIds().size() - 1)
                        $(this.results.get(key).getLogPath()).file().append("-> best result for " + detectors.get(bestKey).getInformation().getDisplayName() + "\n\n");
                }
            }

            // reset values
            bestValue = Double.MAX_VALUE;
            bestKey = -1;
        }

        if (writeLog)
            $(this.overAllResults.getLogPath()).file().append("- over all results for the session above -\nTimestamp: " + this.overAllResults.getTimeStamp() + ", Number of DataSets overall: " + this.overAllResults.getSize() + "\n");

        // run through all included ids
        for (int id : this.overAllResults.getIds()) {

            // write result to log
            if (writeLog)
                $(this.overAllResults.getLogPath()).file().append(detectors.get(id).getInformation().getDisplayName() + ": " + ((double) Math.round(this.overAllResults.getAverage(id) * 100) / 100) + " Pixel distance averaged.\n");

            // check if the current value is better then the best value
            if (veryBestValue > this.overAllResults.getAverage(id)) {

                // store new best value
                veryBestKey = id;
                veryBestValue = this.overAllResults.getAverage(id);
            }
        }
        if (writeLog)
            $(this.overAllResults.getLogPath()).file().append("-> best result for " + detectors.get(veryBestKey).getInformation().getDisplayName() + "\n------------------------------------------------------\n\n\n");
        
        // log best result
        this.channel.status("best result: " + detectors.get(veryBestKey).getInformation().getDisplayName() + " with " + this.overAllResults.getSize() + "datasets");

        // return the name of the very best detector
        return detectors.get(veryBestKey).getInformation().getDisplayName();
    }

    /**
     * draws the png-file withe the calculated results
     * 
     * @param detector
     *      used to get the name
     * @param point
     *      where the detector recognized a target
     * @param path
     *      where the image will be written
     * @param screenShot
     *      screenshot where the data will be written in,
     *      maybe it will be not used when the screenshot is already drawn to a file
     * @param mousePoint
     *      target that is pointed by the mouse
     */
    private void drawPicture(SaliencyDetector detector, Point point, String path,
                             BufferedImage screenShot, Point mousePoint) {
        // initialize variables
        int color = (int) (Math.random() * 255);
        int dimension = screenShot.getHeight();
        File file = new File(path);
        boolean alreadyExists = file.exists();

        try {
            // if the screenshot file already exists, the given screenshot is overwritten by the existing one to update new data 
            if (alreadyExists) screenShot = ImageIO.read(file);

            // create screenshot graphic
            Graphics2D graphic = screenShot.createGraphics();
            graphic.setFont(graphic.getFont().deriveFont(5));

            if (!alreadyExists) {
                // visualize fixation point 
                graphic.setColor(new Color(255, 255, 0, 255));
                graphic.drawOval(dimension / 2 - 5, dimension / 2 - 5, 10, 10);
                graphic.drawChars(("fixation point").toCharArray(), 0, 14, 12 + dimension / 2, 12 + dimension / 2);
                graphic.setColor(new Color(255, 255, 0, 32));
                graphic.fillOval(dimension / 2 - 5, dimension / 2 - 5, 10, 10);

                // visualize mouse point 
                graphic.setColor(new Color(255, 0, 0, 255));
                graphic.drawOval(mousePoint.x - 5, mousePoint.y - 5, 10, 10);
                graphic.drawChars(("mouse target").toCharArray(), 0, 12, 12 + mousePoint.x, 12 + mousePoint.y);
                graphic.setColor(new Color(255, 0, 0, 32));
                graphic.fillOval(mousePoint.x - 5, mousePoint.y - 5, 10, 10);
            }

            // visualize calculations
            color = (50 + color) % 256;
            graphic.setColor(new Color(0, 255 - color, color, 255));
            graphic.drawOval(point.x - 5, point.y - 5, 10, 10);
            graphic.drawChars(detector.getInformation().getDisplayName().toCharArray(), 0, detector.getInformation().getDisplayName().toCharArray().length, point.x + 12, point.y + 12);
            graphic.setColor(new Color(0, 255 - color, color, 32));
            graphic.fillOval(point.x - 5, point.y - 5, 10, 10);

            // write the image
            file.mkdirs();
            ImageIO.write(screenShot, "png", file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
