package org.sovaj.basics.spring.batch.statetementsetter;


import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.jdbc.core.PreparedStatementSetter;

/**
 *
 * @author Mickael Dubois
 */
public class PaginationStatementSetter implements PreparedStatementSetter {

    /**
     * Page number
     */
    private static final AtomicLong PAGE = new AtomicLong(0);

    /**
     * Page size
     */
    private Integer pageSize;

    @Override
    public void setValues(PreparedStatement ps) throws SQLException {
        final long pg = PAGE.getAndIncrement();
        ps.setLong(1, pg * pageSize);
        ps.setLong(2, (pg + 1) * pageSize);
    }

    /**
     * Set the page size of the request.
     *
     * @param pPageSize - The page size of the request to set
     */
    public void setPageSize(int pPageSize) {
        this.pageSize = pPageSize;
    }

}
