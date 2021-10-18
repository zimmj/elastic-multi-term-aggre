package com.joel.zimmerli.elastic.low;

import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JsonPathUtilTest {

    private SearchSourceBuilder getBaseSearchSourceBuilder() {
        var queryBuilder = QueryBuilders.matchAllQuery();
        var searchSource = new SearchSourceBuilder();
        searchSource.size(0);
        searchSource.query(queryBuilder);
        return searchSource;
    }

    private SearchSourceBuilder getSearchSourceWithAggregation() {
        var searchSource = getBaseSearchSourceBuilder();
        var aggregation = AggregationBuilders.terms("names_agg")
                .field("name");
        searchSource.aggregation(aggregation);
        return searchSource;
    }

    @Test
    void addJsonToJsonPath_toExistingAggregation_shouldAddItAtGivenPath() {
        var searchSource = getSearchSourceWithAggregation();
        var multiAggregation = MultiTermsAggregation.builder()
                .aggregationName("multi_aggregation")
                .field1("name")
                .field2("age")
                .build();
        var searchJson = JsonPathUtil.addJsonToJsonPath(
                "$.aggregations",
                searchSource.toString(),
                multiAggregation
        );
        assertEquals(
                "{\"size\":0,\"query\":{\"match_all\":{\"boost\":1.0}},\"aggregations\":{\"names_agg\":{\"terms\":{\"field\":\"name\",\"size\":10,\"min_doc_count\":1,\"shard_min_doc_count\":0,\"show_term_doc_count_error\":false,\"order\":[{\"_count\":\"desc\"},{\"_key\":\"asc\"}]}},\"multi_aggregation\":{\"multi_terms\":{\"terms\":[{\"field\":\"name\"},{\"field\":\"age\"}],\"size\":5}}}}",
                searchJson
        );
    }

    @Test
    void addJsonToJsonPath_withoutAggregation_ShouldAddToGivenPath() {
        var searchSource = getBaseSearchSourceBuilder();
        var multiAggregation = MultiTermsAggregation.builder()
                .aggregationName("multi_aggregation")
                .field1("name")
                .field2("age")
                .build();
        var searchJson = JsonPathUtil.addJsonToJsonPath(
                "$.aggregations",
                searchSource.toString(),
                multiAggregation
        );
        assertEquals(
                "{\"size\":0,\"query\":{\"match_all\":{\"boost\":1.0}},\"aggregations\":{\"" +
                        "multi_aggregation\":{\"multi_terms\":{\"terms\":[{\"field\":\"name\"},{\"field\":\"age\"}],\"size\":5}}}}",
                searchJson
        );
    }

    @Test
    void addJsonToJsonPath_WithAggregationToChildAggregation_ShouldAddToGivenPath() {
        var searchSource = getSearchSourceWithAggregation();
        var multiAggregation = MultiTermsAggregation.builder()
                .aggregationName("multi_aggregation")
                .field1("address.consignee.region")
                .field2("address.shipper.region")
                .build();
        var searchJson = JsonPathUtil.addJsonToJsonPath(
                "$.aggregations." + "names_agg"
                        + ".aggregations",
                searchSource.toString(),
                multiAggregation
        );
        assertEquals(
                "{\"size\":0,\"query\":{\"match_all\":{\"boost\":1.0}},\"aggregations\":{\"names_agg\":{\"terms\":{\"field\":\"name\",\"size\":10,\"min_doc_count\":1,\"shard_min_doc_count\":0,\"show_term_doc_count_error\":false,\"order\":[{\"_count\":\"desc\"},{\"_key\":\"asc\"}]},\"aggregations\":{\"multi_aggregation\":{\"multi_terms\":{\"terms\":[{\"field\":\"address.consignee.region\"},{\"field\":\"address.shipper.region\"}],\"size\":5}}}}}}",
                searchJson
        );
    }
}