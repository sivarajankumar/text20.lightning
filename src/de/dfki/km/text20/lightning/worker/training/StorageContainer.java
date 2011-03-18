/*
 * StorageContainer.java
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
package de.dfki.km.text20.lightning.worker.training;

import java.awt.Point;
import java.io.Serializable;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementArray;

/**
 * Container which stores collected trainings data.
 * 
 * @author Christoph Käding
 *
 */
public class StorageContainer implements Serializable {

    /** generated serial id */
    private static final long serialVersionUID = 1135235125761821153L;

    /** fixation point */
    @Element
    private Point fixation;

    /** mouse position */
    @Element
    private Point mousePoint;

    /** timestamp of the trainings step */
    @Attribute
    private long timestamp;

    /** size of pupils
     *  0 = left
     *  1 = right
     */
    @ElementArray
    private float[] pupils;

    /**
     * Creates new container an initializes its values.
     * 
     * @param user
     * @param timestamp
     * @param mousePoint
     */
    public StorageContainer(long timestamp, Point fixation, Point mousePoint,
                            float[] pubils) {
        this.timestamp = timestamp;
        this.mousePoint = mousePoint;
        this.fixation = fixation;
        this.pupils = pubils;
    }

    /**
     * @return the mousePoint
     */
    public Point getMousePoint() {
        return this.mousePoint;
    }

    /**
     * @return the timestamp
     */
    public long getTimestamp() {
        return this.timestamp;
    }

    /**
     * @return the fixation point
     */
    public Point getFixation() {
        return this.fixation;
    }

    /**
     * @return the pupilsize
     *  0 = left
     *  1 = right
     */
    public float[] getPupils() {
        return this.pupils;
    }

    /**
     * the pupilsize to set
     * 0 = left
     * 1 = right
     * 
     * @param pupils 
     */
    public void setPupils(float[] pupils) {
        this.pupils[0] = pupils[0];
        this.pupils[1] = pupils[1];
    }
}
