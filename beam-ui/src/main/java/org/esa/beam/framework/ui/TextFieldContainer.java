package org.esa.beam.framework.ui;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

/**
 * Created by knowles on 4/5/17.
 */
public class TextFieldContainer {

    private boolean valid = false;
    private double value = 0.0;
    private JTextField textfield = new JTextField(3);
    private JLabel label;

    private String valueString;
    private String name;
    private int minval;
    private int maxval;
    private int defval;
    private Container parentDialogContentPane = null;



    public TextFieldContainer(String name, int defval, int minval, int maxval, Container parentDialogContentPane) {
        this.name = name;
        this.defval = defval;
        this.minval = minval;
        this.maxval = maxval;
        this.parentDialogContentPane = parentDialogContentPane;

        label = new JLabel(name);
        getTextfield().setName(name);
        getTextfield().setText(Integer.toString(defval));
        textfieldHandler();

        setValid(validate(false, true));
    }


    private void textfieldHandler() {

        getTextfield().getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                textfieldHandlerAction();
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                textfieldHandlerAction();
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                textfieldHandlerAction();
            }
        });
    }

    private void textfieldHandlerAction() {

        setValid(validate(true, true));
    }


    public boolean validate(boolean showDialog, boolean ignoreEmptyString) {

        try {
            value = Integer.parseInt(getTextfield().getText().toString());

            if (getValue() < getMinval() || getValue() > getMaxval()) {
                if (showDialog) {
                    JOptionPane.showMessageDialog(getParentDialogContentPane(),
                            "ERROR: Valid " + getName() + " range is (" + getMinval() + " to " + getMaxval() + ")",
                            "Invalid Input",
                            JOptionPane.ERROR_MESSAGE);
                }

                return false;
            }
        } catch (NumberFormatException exception) {

            if (ignoreEmptyString && getTextfield().getText().toString().trim().length() == 0) {
                showDialog = false;
            }
            if (showDialog) {
                JOptionPane.showMessageDialog(getParentDialogContentPane(),
                        getName() + "  " + exception.toString(),
                        "Invalid Input",
                        JOptionPane.ERROR_MESSAGE);
            }
            return false;
        }

        return true;
    }

    private void setValid(boolean valid) {
        this.valid = valid;
    }


    public JTextField getTextfield() {
        return textfield;
    }

    public JLabel getLabel() {
        return label;
    }

    public boolean isValid(boolean showDialog) {
        if (!valid && showDialog) {
            validate(true, false);
        }
        return valid;
    }

    public double getValue() {
        return value;
    }

    public String getValueString() {
        return valueString;
    }

    public void setValueString(String valueString) {
        this.valueString = valueString;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMinval() {
        return minval;
    }

    public void setMinval(int minval) {
        this.minval = minval;
    }

    public int getMaxval() {
        return maxval;
    }

    public void setMaxval(int maxval) {
        this.maxval = maxval;
    }

    public int getDefval() {
        return defval;
    }

    public void setDefval(int defval) {
        this.defval = defval;
    }

    public Container getParentDialogContentPane() {
        return parentDialogContentPane;
    }

    public void setParentDialogContentPane(Container parentDialogContentPane) {
        this.parentDialogContentPane = parentDialogContentPane;
    }
}