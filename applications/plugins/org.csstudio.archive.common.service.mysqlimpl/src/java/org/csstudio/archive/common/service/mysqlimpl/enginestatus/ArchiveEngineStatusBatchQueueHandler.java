/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.archive.common.service.mysqlimpl.enginestatus;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import javax.annotation.Nonnull;

import org.csstudio.archive.common.service.enginestatus.IArchiveEngineStatus;
import org.csstudio.archive.common.service.mysqlimpl.dao.AbstractBatchQueueHandler;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;

/**
 * DBO specific batch strategy for high write throughput.
 *
 * @author bknerr
 * @since 20.07.2011
 */
final class ArchiveEngineStatusBatchQueueHandler extends AbstractBatchQueueHandler<IArchiveEngineStatus> {
    private static final String VAL_WILDCARDS = "(?, ?, ?, ?)";
    /**
     * Constructor.
     */
    public ArchiveEngineStatusBatchQueueHandler(@Nonnull final String databaseName) {
        super(databaseName, new LinkedBlockingQueue<IArchiveEngineStatus>());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    protected String composeSqlString() {
        return "INSERT INTO " + getDatabase() + "." + ArchiveEngineStatusDaoImpl.TAB + " (engine_id, status, time, info) VALUES " + VAL_WILDCARDS;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void fillStatement(@Nonnull final PreparedStatement stmt,
                              @Nonnull final IArchiveEngineStatus status) throws SQLException {
        stmt.setInt(1, status.getEngineId().intValue());
        stmt.setString(2, status.getStatus().name());
        stmt.setLong(3, status.getTimestamp().getNanos());
        stmt.setString(4, status.getInfo());
    }
    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public Class<IArchiveEngineStatus> getType() {
        return IArchiveEngineStatus.class;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public Collection<String> convertToStatementString(@Nonnull final List<IArchiveEngineStatus> elements) {
        final String sqlStr = composeSqlString();
        final Collection<String> statements =
            Collections2.transform(elements,
                                   new Function<IArchiveEngineStatus, String>() {
                                       @Override
                                       @Nonnull
                                       public String apply(@Nonnull final IArchiveEngineStatus input) {
                                           final String replacement =
                                               "(" +
                                               Joiner.on(",").join("'" + input.getEngineId().asString() + "'",
                                                                   "'" + input.getStatus().name() + "'",
                                                                   input.getTimestamp().getNanos(),
                                                                   "'" + input.getInfo() + "'") +
                                               ")";
                                           return sqlStr.replaceAll(VAL_WILDCARDS, replacement);
                                       }
                                   });
        return statements;
    }
}
