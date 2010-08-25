/**
 * DownloadJob.java
 * 
 * Created on 25.08.2010
 */

package net.slightlymagic.laterna.dl;


import java.io.File;
import java.net.URL;

import javax.swing.BoundedRangeModel;
import javax.swing.DefaultBoundedRangeModel;

import net.slightlymagic.beans.properties.Property;
import net.slightlymagic.laterna.AbstractLaternaBean;


/**
 * The class DownloadJob.
 * 
 * @version V0.0 25.08.2010
 * @author Clemens Koza
 */
public class DownloadJob extends AbstractLaternaBean {
    public static enum State {
        NEW, WORKING, FINISHED, ABORTED;
    }
    
    private final String              name;
    private final URL                 source;
    private final File                destination;
    private final Property<State>     state    = properties.property("state", State.NEW);
    private final Property<String>    message  = properties.property("message");
    private final Property<Exception> error    = properties.property("error");
    private final BoundedRangeModel   progress = new DefaultBoundedRangeModel();
    
    public DownloadJob(String name, URL source, File destination) {
        this.name = name;
        this.source = source;
        this.destination = destination;
    }
    
    /**
     * Sets the job's state. If the state is {@link State#ABORTED}, it instead sets the error to "ABORTED"
     */
    public void setState(State state) {
        if(state == State.ABORTED) setError("ABORTED");
        else this.state.setValue(state);
    }
    
    /**
     * Sets the job's state to {@link State#ABORTED} and the error message to the given message. Logs a warning
     * with the given message.
     */
    public void setError(String message) {
        setError(message, null);
    }
    
    /**
     * Sets the job's state to {@link State#ABORTED} and the error to the given exception. Logs a warning with the
     * given exception.
     */
    public void setError(Exception error) {
        setError(null, error);
    }
    
    /**
     * Sets the job's state to {@link State#ABORTED} and the error to the given exception. Logs a warning with the
     * given message and exception.
     */
    public void setError(String message, Exception error) {
        if(message == null) message = error.toString();
        log.warn(message, error);
        this.state.setValue(State.ABORTED);
        this.error.setValue(error);
        this.message.setValue(message);
    }
    
    /**
     * Sets the job's message.
     */
    public void setMessage(String message) {
        this.message.setValue(message);
    }
    
    public BoundedRangeModel getProgress() {
        return progress;
    }
    
    public State getState() {
        return state.getValue();
    }
    
    public Exception getError() {
        return error.getValue();
    }
    
    public String getMessage() {
        return message.getValue();
    }
    
    
    public String getName() {
        return name;
    }
    
    public URL getSource() {
        return source;
    }
    
    public File getDestination() {
        return destination;
    }
}
