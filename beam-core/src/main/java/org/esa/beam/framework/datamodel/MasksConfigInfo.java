package org.esa.beam.framework.datamodel;

import org.esa.beam.util.StringUtils;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: knowles
 * Date: 7/6/16
 * Time: 3:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class MasksConfigInfo {

    private String name;
    private String[] components;
    private boolean isUnion;
    private String description;
    private Color color;
    private double tranparency;


    public MasksConfigInfo(String name, String[] components, boolean isUnion, Color color, double tranparency, String description) {
        if (name != null && components != null && color != null) {
            this.setName(name);
            this.setComponents(components);
            this.setUnion(isUnion);
            this.setColor(color);
            this.setTranparency(tranparency);
            this.setDescription(description);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getComponents() {
        return components;
    }

    public void setComponents(String[] components) {
        this.components = components;
    }

    public boolean isUnion() {
        return isUnion;
    }

    public void setUnion(boolean union) {
        isUnion = union;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLogicalExpression() {
        String unionExpression = StringUtils.join(components, " or ");
        if (isUnion()) {
            return unionExpression;
        } else {
            return "!( " + unionExpression + " )";
        }
    }


    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public double getTranparency() {
        return tranparency;
    }

    public void setTranparency(double tranparency) {
        this.tranparency = tranparency;
    }
}
