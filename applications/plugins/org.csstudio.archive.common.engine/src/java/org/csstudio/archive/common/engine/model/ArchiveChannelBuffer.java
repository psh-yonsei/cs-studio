/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.common.engine.model;

import java.io.Serializable;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;

import org.csstudio.archive.common.engine.service.IServiceProvider;
import org.csstudio.archive.common.service.channel.ArchiveChannelId;
import org.csstudio.archive.common.service.sample.IArchiveSample;
import org.csstudio.domain.desy.system.ISystemVariable;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVFactory;
import org.csstudio.utility.pv.PVListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Base for archived channels.
 *
 *  @author Kay Kasemir
 *  @author Bastian Knerr
 *  @param <V> the basic value type
 *  @param <T> the system variable for the basic value type
 */
@SuppressWarnings("nls")
public class ArchiveChannelBuffer<V extends Serializable, T extends ISystemVariable<V>> {

    private static final Logger LOG = LoggerFactory.getLogger(PVListener.class);

    /** Channel name.
     *  This is the name by which the channel was created,
     *  not the PV name that might include decorations.
     */
    private final String _name;

    private final ArchiveChannelId _id;

    /** Control system PV */
    private final PV _pv;

    /** Buffer of received samples, periodically written */
    private final SampleBuffer<V, T, IArchiveSample<V, T>> _buffer;


    /** Is this channel currently running?
     *  <p>
     *  PV sends another 'disconnected' event
     *  as the result of 'stop', but we don't
     *  want to log that, so we keep track of
     *  the 'running' state.
     */
    @GuardedBy("this")
    private volatile boolean _isStarted;

    /** Most recent value of the PV.
     *  <p>
     *  This is the value received from the PV,
     *  is is not necessarily written to the archive.
     *  <p>
     */
    @GuardedBy("this")
    private T _mostRecentSysVar;

    /**
     * Counter for received values (monitor updates)
     */
    private long _receivedSampleCount;

    private final IServiceProvider _provider;

    private final Class<V> _typeClazz;
    private final Class<V> _collClazz;

    @SuppressWarnings("rawtypes")
    private final DesyArchivePVListener _listener;

    private TimeInstant _timeOfLastSampleBeforeChannelStart;

    /**
     * Constructor
     * @param timeInstant
     * @throws EngineModelException on failure while creating PV
     */
    public ArchiveChannelBuffer(@Nonnull final String name,
                                @Nonnull final ArchiveChannelId id,
                                @Nullable final TimeInstant timeOfLastSample,
                                @Nonnull final Class<V> typeClazz,
                                @Nonnull final IServiceProvider provider) throws EngineModelException {
        this(name, id, timeOfLastSample, null, typeClazz, provider);
    }


    /**
     * Constructor.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public ArchiveChannelBuffer(@Nonnull final String name,
                                @Nonnull final ArchiveChannelId id,
                                @Nullable final TimeInstant timeOfLastSample,
                                @Nullable final Class<V> collClazz,
                                @Nonnull final Class<V> typeClazz,
                                @Nonnull final IServiceProvider provider) throws EngineModelException {
        _name = name;
        _id = id;
        _timeOfLastSampleBeforeChannelStart = timeOfLastSample;
        _buffer = new SampleBuffer<V, T, IArchiveSample<V, T>>(name);
        _typeClazz = typeClazz;
        _collClazz = collClazz;
        _provider = provider;

        try {
            _pv = PVFactory.createPV(name);
        } catch (final Exception e) {
            throw new EngineModelException("Creation of pv failed for channel " + name, e);
        }

        _listener = new DesyArchivePVListener(_provider, _name, _id, _collClazz, _typeClazz) {
            @SuppressWarnings("synthetic-access")
            @Override
            protected boolean addSampleToBuffer(@Nonnull final IArchiveSample sample) {
                synchronized (this) {
                    _receivedSampleCount++;
                    _mostRecentSysVar = (T) sample.getSystemVariable();
                }
                return _buffer.add(sample);
            }
        };
        _pv.addListener(_listener);
    }


    /** @return Name of channel */
    @Nonnull
    public String getName() {
        return _name;
    }

    /** @return Short description of sample mechanism */
    @Nonnull
    public String getMechanism() {
        return "MONITOR (on change)";
    }

    /** @return <code>true</code> if connected */
    public boolean isConnected() {
        return _pv.isConnected();
    }

    /** @return <code>true</code> if connected */
    public boolean isStarted() {
        return _isStarted;
    }

    /** @return Human-readable info on internal state of PV */
    @CheckForNull
    public String getInternalState() {
        return _pv.getStateInfo();
    }

    @CheckForNull
    public TimeInstant getTimeOfMostRecentSample() {
        return _mostRecentSysVar != null ? _mostRecentSysVar.getTimestamp() : _timeOfLastSampleBeforeChannelStart;
    }

    /**
     * Start archiving this channel.
     * @throws EngineModelException
     */
    public void start(@Nonnull final String info) throws EngineModelException {
        try {
            synchronized (this) {
                if (_isStarted) {
                    return;
                }
                _isStarted = true;
            }
            _listener.setStartInfo(info);
            _pv.start();
        } catch (final Exception e) {
            LOG.error("PV " + _pv.getName() + " could not be started with state info " + _pv.getStateInfo(), e);
            throw new EngineModelException("Something went wrong within Kasemir's PV stuff on channel/PV startup", e);
        }

    }


    /**
     * Stop archiving this channel
     */
    public void stop(@Nonnull final String info) {
        synchronized (this) {
            if (!_isStarted) {
                return;
            }
            _isStarted = false;
        }
        _listener.setStopInfo(info);
        _pv.stop();
    }

    @Nonnull
    public synchronized T getMostRecentSample() {
        return _mostRecentSysVar;
    }

    /** @return Count of received values */
    public synchronized long getReceivedValues() {
        return _receivedSampleCount;
    }

    /** @return Sample buffer */
    @Nonnull
    public final SampleBuffer<V, T, IArchiveSample<V, T>> getSampleBuffer() {
        return _buffer;
    }


    /** Reset counters */
    public void reset() {
        _buffer.statsReset();
        synchronized (this) {
            _receivedSampleCount = 0;
        }
    }

    @Override
    @Nonnull
    public String toString() {
        return "Channel " + getName() + ", " + getMechanism();
    }

    public boolean isMultiScalar() {
        return _collClazz != null;
    }

    @Nonnull
    public ArchiveChannelId getId() {
        return _id;
    }
}