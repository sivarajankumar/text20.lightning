/*
 * PluginInformation.java
 * 
 * Copyright (c) 2011, Christoph Käding, DFKI. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer. Redistributions in binary form must reproduce the
 * above copyright notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 
 * Neither the name of the author nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS
 * OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 */
package de.dfki.km.text20.lightning.plugins;


/**
 * Provides information about the given filter.
 * 
 * @author Christoph Käding
 */
public class PluginInformation {
    
    /** The name to display in the interface for this filter */
    private String displayName;
    
    /** The description of the method. */
    private String toolTip;
    
    /** number to identify the plugin */
    private int id;
    
    /** indicates if a configuration gui is available*/
    private boolean guiAvailable;
    
    /**
     * returns a information object
     * be sure you always return the same object, because it will be set a id in it!
     * 
     * @param name
     * @param description
     * @param guiAvailable 
     */
    public PluginInformation(String name, String description, boolean guiAvailable){
        this.displayName = name;
        this.toolTip = description;
        this.guiAvailable = guiAvailable;
    }

    /**
     * @return the displayName
     */
    public String getDisplayName() {
        return this.displayName;
    }

    /**
     * @return the toolTip
     */
    public String getToolTip() {
        return this.toolTip;
    }

    /**
     * @return the id
     */
    public int getId() {
        return this.id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
        //System.out.println("id " + this.id);
    }

    /**
     * @return the guiAvailable
     */
    public boolean isGuiAvailable() {
        return this.guiAvailable;
    }     
}
