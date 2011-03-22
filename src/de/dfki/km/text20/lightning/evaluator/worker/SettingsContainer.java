/*
 * SettingsContainer.java
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

/**
 * @author Christoph Käding
 *
 */
public class SettingsContainer {

    private int dimension;

    private String screenBright;

    private String settingBright;
    
    private int count;

    /**
     * 
     * @param dimension
     * @param screenBright
     * @param settingBright
     */
    public SettingsContainer(int dimension, String screenBright, String settingBright) {
        this.dimension = dimension;
        this.screenBright = screenBright;
        this.settingBright = settingBright;
        this.count = 0;
    }

    /**
     * @return the dimension
     */
    public int getDimension() {
        return this.dimension;
    }

    /**
     * @return the screenBright
     */
    public String getScreenBright() {
        return this.screenBright;
    }

    /**
     * @return the settingBright
     */
    public String getSettingBright() {
        return this.settingBright;
    }

    /**
     * 
     */
    public void addOutOfDim() {
        this.count++;
    }
    
    /**
     * 
     * @return count
     */
    public int getOutOfDim() {
        return this.count;
    }
}
