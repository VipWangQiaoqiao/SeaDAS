package org.esa.beam.visat.actions;

import org.esa.beam.framework.ui.command.CommandEvent;
import org.esa.beam.framework.ui.command.ExecCommand;
import org.esa.beam.util.SystemUtils;

import java.awt.*;
import java.io.IOException;
import java.net.URI;

/**
 * Created with IntelliJ IDEA.
 * User: knowles
 * Date: 3/31/16
 * Time: 1:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class ShowOceanColorPageAction  extends ExecCommand {
    @Override
    public void updateState(final CommandEvent event) {
        setEnabled(Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE));
    }

    /**
     * Launches the default browser to display the BEAM Wiki.
     * Invoked when a command action is performed.
     *
     * @param event the command event.
     */
    @Override
    public void actionPerformed(CommandEvent event) {
        final String homePageUrl = SystemUtils.getApplicationHomepageUrl();

        try {
            final Desktop desktop = Desktop.getDesktop();
            desktop.browse(URI.create("https://oceancolor.gsfc.nasa.gov"));
        } catch (IOException e) {
            // TODO - handle
        } catch (UnsupportedOperationException e) {
            // TODO - handle
        }
    }
}
