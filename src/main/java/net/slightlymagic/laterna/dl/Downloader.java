/**
 * Downloader.java
 * 
 * Created on 25.08.2010
 */

package net.slightlymagic.laterna.dl;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.util.List;
import java.util.concurrent.Executors;

import net.slightlymagic.laterna.AbstractLaternaBean;
import net.slightlymagic.laterna.dl.DownloadJob.State;

import org.jetlang.channels.Channel;
import org.jetlang.channels.MemoryChannel;
import org.jetlang.core.Callback;
import org.jetlang.fibers.Fiber;
import org.jetlang.fibers.PoolFiberFactory;


/**
 * The class Downloader.
 * 
 * @version V0.0 25.08.2010
 * @author Clemens Koza
 */
public class Downloader extends AbstractLaternaBean {
    private final List<DownloadJob>    jobs = properties.list("jobs");
    private final Channel<DownloadJob> channel;
    
    public Downloader() {
        channel = new MemoryChannel<DownloadJob>();
        
        PoolFiberFactory f = new PoolFiberFactory(Executors.newCachedThreadPool());
        //sbscribe multiple fibers for parallel execution
        for(int i = 0, numThreads = 10; i < numThreads; i++) {
            Fiber fiber = f.create();
            fiber.start();
            channel.subscribe(fiber, new DownloadCallback());
        }
    }
    
    public void add(DownloadJob job) {
        if(job.getState() == State.WORKING) throw new IllegalArgumentException("Job already running");
        if(job.getState() == State.FINISHED) throw new IllegalArgumentException("Job already finished");
        job.setState(State.NEW);
        jobs.add(job);
        channel.publish(job);
    }
    
    public List<DownloadJob> getJobs() {
        return jobs;
    }
    
    private class DownloadCallback implements Callback<DownloadJob> {
        @Override
        public void onMessage(DownloadJob job) {
            //the job won't be processed by multiple threads
            synchronized(job) {
                if(job.getState() != State.NEW) return;
                job.setState(State.WORKING);
            }
            try {
                File dest = job.getDestination().getAbsoluteFile();
                File parent = dest.getParentFile();
                if(!parent.exists() && !parent.mkdirs()) {
                    throw new IOException(parent + ": directory could not be created");
                }
                
                if(dest.isFile()) {
                    job.getProgress().setMaximum(1);
                    job.getProgress().setValue(1);
                } else {
                    URLConnection c = job.getSource().openConnection();
                    job.getProgress().setMaximum(c.getContentLength());
                    InputStream is = new BufferedInputStream(c.getInputStream());
                    try {
                        OutputStream os = new BufferedOutputStream(new FileOutputStream(dest));
                        try {
                            byte[] buf = new byte[8 * 1024];
                            int total = 0;
                            for(int len; (len = is.read(buf)) != -1;) {
                                if(job.getState() == State.ABORTED) throw new IOException("Job was aborted");
                                job.getProgress().setValue(total += len);
                                os.write(buf, 0, len);
                            }
                        } catch(IOException ex) {
                            if(!dest.delete()) log.warn("Incomplete file " + dest + " could not be deleted");
                            throw ex;
                        } finally {
                            try {
                                os.close();
                            } catch(IOException ex) {
                                log.warn("While closing", ex);
                            }
                        }
                    } finally {
                        try {
                            is.close();
                        } catch(IOException ex) {
                            log.warn("While closing", ex);
                        }
                    }
                }
                job.setState(State.FINISHED);
            } catch(IOException ex) {
                job.setError(ex);
            }
        }
    }
}
