package org.esa.beam.framework.ui.layer;

import com.bc.ceres.binding.Property;
import com.bc.ceres.binding.PropertyDescriptor;
import com.bc.ceres.binding.PropertySet;
import com.bc.ceres.swing.TableLayout;
import com.bc.ceres.swing.binding.BindingContext;
import com.bc.ceres.swing.binding.PropertyEditor;
import com.bc.ceres.swing.binding.PropertyEditorRegistry;
import org.esa.beam.framework.ui.GridBagUtils;
import org.esa.beam.framework.ui.product.ProductSceneView;
import org.esa.beam.glayer.GraticuleLayerType;

import javax.swing.*;

import java.awt.*;

import static com.bc.ceres.swing.TableLayout.cell;

/**
 * Created by knowles on 2/16/17.
 */
public class PropertyPaneRevised {
    private final BindingContext bindingContext;

    public PropertyPaneRevised(PropertySet propertySet) {
        this(new BindingContext(propertySet));
    }

    public PropertyPaneRevised(BindingContext bindingContext) {
        this.bindingContext = bindingContext;
    }

    public BindingContext getBindingContext() {
        return bindingContext;
    }

    public JPanel createPanel(String layerName) {
        PropertySet propertyContainer = bindingContext.getPropertySet();
        Property[] properties = propertyContainer.getProperties();
        final PropertyEditorRegistry registry = PropertyEditorRegistry.getInstance();


        boolean displayUnitColumn = wantDisplayUnitColumn(properties);
        TableLayout layout = new TableLayout(displayUnitColumn ? 3 : 2);
        layout.setTableAnchor(TableLayout.Anchor.NORTHWEST);
        layout.setTableFill(TableLayout.Fill.HORIZONTAL);
        layout.setTablePadding(3, 3);
        final JPanel leftPanel = new JPanel(layout);
        int rowIndex = 0;


        TableLayout layoutRight = new TableLayout(displayUnitColumn ? 3 : 2);
        layoutRight.setTableAnchor(TableLayout.Anchor.NORTHWEST);
        layoutRight.setTableFill(TableLayout.Fill.HORIZONTAL);
        layoutRight.setTablePadding(3, 3);
        final JPanel rightPanel = new JPanel(layoutRight);
        int rowIndexRight = 0;



        int halfLength = properties.length/2;
        boolean leftSide = true;

        for (Property property : properties) {

            if (ProductSceneView.GRATICULE_LAYER_NAME.equals(layerName)) {
                if (GraticuleLayerType.PROPERTY_NAME_RES_LAT.equals(property.getName())) {
                    leftSide = false;
                }
            } else {
                if (rowIndex > halfLength) {
                    leftSide = false;
                }
            }

            if (leftSide) {
                addPropertyToPanel(leftPanel, rowIndex, layout, registry, property, displayUnitColumn);
            } else {

                addPropertyToPanel(rightPanel, rowIndexRight, layoutRight, registry, property, displayUnitColumn);
                rowIndexRight++;
            }
            rowIndex++;
        }

        JPanel groupPanel = GridBagUtils.createPanel();
        GridBagConstraints gbcGroup = GridBagUtils.createConstraints("");
        gbcGroup.gridx = 0;
        gbcGroup.gridy = 0;
        gbcGroup.weightx = 0.0;
        gbcGroup.fill = GridBagConstraints.NONE;
        gbcGroup.anchor = GridBagConstraints.NORTHWEST;
        gbcGroup.insets.bottom=10;
        gbcGroup.insets.right=20;
        groupPanel.add(leftPanel, gbcGroup);
        gbcGroup.gridx++;
        gbcGroup.weightx = 1.0;
        gbcGroup.weighty = 1.0;
        gbcGroup.insets.right=0;
        gbcGroup.fill = GridBagConstraints.HORIZONTAL;

        groupPanel.add(rightPanel, gbcGroup);


//        layout.setCellColspan(rowIndex, 0, 2);
//        layout.setCellWeightX(rowIndex, 0, 1.0);
//        layout.setCellWeightY(rowIndex, 0, 0.5);
//        leftPanel.add(new JPanel());
        return groupPanel;
    }

    private void addPropertyToPanel(JPanel panel, int rowIndex, TableLayout layout, PropertyEditorRegistry registry, Property property, boolean displayUnitColumn) {
        PropertyDescriptor descriptor = property.getDescriptor();
        if (isInvisible(descriptor)) {
            return;
        }
        PropertyEditor propertyEditor = registry.findPropertyEditor(descriptor);
        JComponent[] components = propertyEditor.createComponents(descriptor, bindingContext);
        if (components.length == 2) {
            layout.setCellWeightX(rowIndex, 0, 0.0);
            panel.add(components[1], cell(rowIndex, 0));
            layout.setCellWeightX(rowIndex, 1, 1.0);
            if(components[0] instanceof JScrollPane) {
                layout.setRowWeightY(rowIndex, 1.0);
                layout.setRowFill(rowIndex, TableLayout.Fill.BOTH);
            }
            panel.add(components[0], cell(rowIndex, 1));
        } else {
            layout.setCellColspan(rowIndex, 0, 2);
            layout.setCellWeightX(rowIndex, 0, 1.0);
            panel.add(components[0], cell(rowIndex, 0));
        }
        if (displayUnitColumn) {
            final JLabel label = new JLabel("");
            if (descriptor.getUnit() != null) {
                label.setText(descriptor.getUnit());
            }
            layout.setCellWeightX(rowIndex, 2, 0.0);
            panel.add(label, cell(rowIndex, 2));
        }
    }

    private boolean isInvisible(PropertyDescriptor descriptor) {
        return Boolean.FALSE.equals(descriptor.getAttribute("visible")) || descriptor.isDeprecated();
    }

    private boolean wantDisplayUnitColumn(Property[] models) {
        boolean showUnitColumn = false;
        for (Property model : models) {
            PropertyDescriptor descriptor = model.getDescriptor();
            if (isInvisible(descriptor)) {
                continue;
            }
            String unit = descriptor.getUnit();
            if (!(unit == null || unit.length() == 0)) {
                showUnitColumn = true;
                break;
            }
        }
        return showUnitColumn;
    }
}
