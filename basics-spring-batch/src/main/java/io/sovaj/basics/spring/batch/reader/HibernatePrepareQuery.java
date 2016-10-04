package io.sovaj.basics.spring.batch.reader;

import org.hibernate.Query;

/**
 * 
 * @author Mickael Dubois
 */
public interface HibernatePrepareQuery {

    /**
     * Prepare the query just before executing it.
     * 
     * @param toPrepare - The query to prepare
     * @return The prepared query
     */
    Query prepareQuery(Query toPrepare);
}
