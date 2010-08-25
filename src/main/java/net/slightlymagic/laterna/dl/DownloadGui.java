/**
 * DownloadGui.java
 * 
 * Created on 25.08.2010
 */

package net.slightlymagic.laterna.dl;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.IndexedPropertyChangeEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoundedRangeModel;
import javax.swing.BoxLayout;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;

import net.slightlymagic.laterna.dl.DownloadJob.State;
import net.slightlymagic.laterna.dl.sources.GathererCardBack;
import net.slightlymagic.laterna.dl.sources.GathererCards;
import net.slightlymagic.laterna.dl.sources.GathererSymbols;
import net.slightlymagic.laterna.magica.LaternaMagica;

import com.google.common.collect.Iterables;


/**
 * The class DownloadGui.
 * 
 * @version V0.0 25.08.2010
 * @author Clemens Koza
 */
public class DownloadGui extends JPanel {
    private static final long serialVersionUID = -7346572382493844327L;
    
    public static void main(String[] args) throws IOException {
        LaternaMagica.init();
        
        DownloadGui g = new DownloadGui();
        Iterable<DownloadJob> it = Iterables.concat(new GathererSymbols(), new GathererCardBack(),
                new GathererCards());
        
        for(DownloadJob job:it)
            g.getDownloader().add(job);
        
        JDialog d = new JDialog((Frame) null, true);
        d.setLayout(new BorderLayout());
        d.add(g);
        d.pack();
        d.setVisible(true);
        
    }
    
    private Downloader                      d          = new Downloader();
    private DownloadListener                l          = new DownloadListener();
    private BoundedRangeModel               model      = new DefaultBoundedRangeModel(0, 0, 0, 0);
    private JProgressBar                    progress   = new JProgressBar(model);
    
    private Map<DownloadJob, DownloadPanel> progresses = new HashMap<DownloadJob, DownloadPanel>();
    private JPanel                          panel      = new JPanel();
    
    public DownloadGui() {
        super(new BorderLayout());
        d.addPropertyChangeListener(l);
        
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createTitledBorder("Progress:"));
        p.add(progress);
        JButton b = new JButton("X");
        b.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for(DownloadJob j:d.getJobs()) {
                    switch(j.getState()) {
                        case NEW:
                        case WORKING:
                            j.setState(State.ABORTED);
                    }
                }
            }
        });
        p.add(b, BorderLayout.EAST);
        add(p, BorderLayout.NORTH);
        
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JScrollPane pane = new JScrollPane(panel);
        pane.setPreferredSize(new Dimension(500, 300));
        add(pane);
        for(int i = 0; i < d.getJobs().size(); i++)
            addJob(i, d.getJobs().get(i));
    }
    
    public Downloader getDownloader() {
        return d;
    }
    
    private class DownloadListener implements PropertyChangeListener {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            String name = evt.getPropertyName();
            if(evt.getSource() instanceof DownloadJob) {
                DownloadPanel p = progresses.get(evt.getSource());
                if("state".equals(name)) {
                    if(evt.getOldValue() == State.FINISHED || evt.getOldValue() == State.ABORTED) {
                        changeProgress(-1, 0);
                    }
                    if(evt.getNewValue() == State.FINISHED || evt.getOldValue() == State.ABORTED) {
                        changeProgress(+1, 0);
                    }
                    if(p != null) {
                        p.setVisible(p.getJob().getState() != State.FINISHED);
                        p.revalidate();
                    }
                } else if("message".equals(name)) {
                    if(p != null) {
                        JProgressBar bar = p.getBar();
                        String message = p.getJob().getMessage();
                        bar.setStringPainted(message != null);
                        bar.setString(message);
                    }
                }
            } else if(evt.getSource() == d) {
                if("jobs".equals(name)) {
                    IndexedPropertyChangeEvent ev = (IndexedPropertyChangeEvent) evt;
                    int index = ev.getIndex();
                    
                    DownloadJob oldValue = (DownloadJob) ev.getOldValue();
                    if(oldValue != null) removeJob(index, oldValue);
                    
                    DownloadJob newValue = (DownloadJob) ev.getNewValue();
                    if(newValue != null) addJob(index, newValue);
                }
            }
        }
    }
    
    private synchronized void addJob(int index, DownloadJob job) {
        job.addPropertyChangeListener(l);
        changeProgress(0, +1);
        DownloadPanel p = new DownloadPanel(job);
        progresses.put(job, p);
        panel.add(p, index);
        panel.revalidate();
    }
    
    private synchronized void removeJob(int index, DownloadJob job) {
        assert progresses.get(job) == panel.getComponent(index);
        job.removePropertyChangeListener(l);
        changeProgress(0, -1);
        progresses.remove(job);
        panel.remove(index);
        panel.revalidate();
    }
    
    private synchronized void changeProgress(int progress, int total) {
        progress += model.getValue();
        total += model.getMaximum();
        model.setMaximum(total);
        model.setValue(progress);
        this.progress.setStringPainted(true);
        this.progress.setString(progress + "/" + total);
    }
    
    private class DownloadPanel extends JPanel {
        private static final long serialVersionUID = 1187986738303477168L;
        
        private DownloadJob       job;
        private JProgressBar      bar;
        
        public DownloadPanel(DownloadJob job) {
            super(new BorderLayout());
            this.job = job;
            
            setBorder(BorderFactory.createTitledBorder(job.getName()));
            add(bar = new JProgressBar(job.getProgress()), BorderLayout.SOUTH);
            
            if(job.getState() == State.FINISHED | job.getState() == State.ABORTED) {
                changeProgress(+1, 0);
            }
            setVisible(job.getState() != State.FINISHED);
            
            String message = job.getMessage();
            bar.setStringPainted(message != null);
            bar.setString(message);
            
            Dimension d = getPreferredSize();
            d.width = Integer.MAX_VALUE;
            setMaximumSize(d);
            d.width = 500;
            setMinimumSize(d);
        }
        
        public DownloadJob getJob() {
            return job;
        }
        
        public JProgressBar getBar() {
            return bar;
        }
    }
}
