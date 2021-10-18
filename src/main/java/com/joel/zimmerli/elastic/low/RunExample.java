package com.joel.zimmerli.elastic.low;

import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.text.MessageFormat;

public class RunExample {

    private static CallService callService = new CallService();

    public static void main(String[] args) {

        String searchJson = JsonPathUtil.addJsonToJsonPath(
                "$.aggregation.names_agg.aggregation",
                getExampleSearchSource().toString(),
                getMyMultiTermsAgg()
        );

        try {
            //SearchResult is a json and need to be parsed
            String searchResult = callService.makeCall(searchJson);
            System.out.println(searchResult);
        } catch (IOException| IllegalArgumentException exception) {
            System.err.println(MessageFormat.format(
                    "Call to Elastic was unsuccessfully: {}",
                    exception.getMessage()
            ));
            exception.printStackTrace();
        }
    }

    private static SearchSourceBuilder getExampleSearchSource() {
        var matchAll = QueryBuilders.matchAllQuery();
        var searchSource = new SearchSourceBuilder();
        searchSource.size(1);
        searchSource.query(matchAll);

        var exraAggs = AggregationBuilders.terms("names_agg")
                .field("mame");

        searchSource.aggregation(exraAggs);
        return searchSource;
    }

    private static MultiTermsAggregation getMyMultiTermsAgg() {
        return MultiTermsAggregation.builder()
                .aggregationName("name_age")
                .field1("name")
                .field2("age")
                .build();
    }

}
