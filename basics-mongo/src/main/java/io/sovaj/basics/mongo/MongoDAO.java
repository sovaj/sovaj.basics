package io.sovaj.basics.mongo;

import com.mongodb.WriteResult;
import io.sovaj.basics.mongo.domain.BusinessObject;
import io.sovaj.basics.mongo.domain.PerMonthStatistic;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Fields.fields;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

/**
 *
 * @author Mickael Dubois
 * @param <T>
 */
public abstract class MongoDAO<T extends BusinessObject> {

    @Autowired
    protected MongoOperations mongoOps;

    abstract public String getCollection();

    abstract public Class<T> getClazz();

    public void create(final T pObject) {
        pObject.setCreationDate(new Date());
        pObject.setUpdatedDate(new Date());
        this.mongoOps.insert(pObject, getCollection());
    }

    public T getById(final String pObjectId) {
        Query query = new Query(Criteria.where("_id").is(pObjectId));
        return this.mongoOps.findOne(query, getClazz(), getCollection());
    }

    public void update(final T pObject) {
        pObject.setUpdatedDate(new Date());
        this.mongoOps.save(pObject, getCollection());
    }

    public int delete(final String pObjectId) {
        Query query = new Query(Criteria.where("_id").is(pObjectId));
        WriteResult result = this.mongoOps.remove(query, getClazz(), getCollection());
        return result.getN();
    }

    public T getByFieldName(final String pFieldName,final String pFieldValue) {
        Query query = new Query(Criteria.where(pFieldName).is(pFieldValue));
        return this.mongoOps.findOne(query, getClazz(), getCollection());
    }

    public List<T> findByFieldName(final String pFieldName,final String pFieldValue) {
        Query query = new Query(Criteria.where(pFieldName).regex(pFieldValue, "i"));
        return this.mongoOps.find(query, getClazz(), getCollection());
    }

    public List<T> list() {
        return this.mongoOps.findAll(getClazz(), getCollection());
    }
    
    

    public List<PerMonthStatistic> countPerMonthSince(final String dateFieldName, final Date since) {
        Aggregation agg = newAggregation(
                match(Criteria.where(dateFieldName).gt(since)),
                project("total").andExpression("month('"+dateFieldName+"')").as("month")
                .andExpression("year('"+dateFieldName+"')").as("year"),
                group(fields("month").and("year")).count().as("total"));
        return mongoOps.aggregate(agg, getCollection(), PerMonthStatistic.class).getMappedResults();
    }

}
