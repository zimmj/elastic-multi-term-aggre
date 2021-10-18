package com.joel.zimmerli.elastic.low;

import lombok.Builder;
import lombok.Getter;

@Builder
public class MultiTermsAggregation {

    private final String field1;
    private final String field2;
    @Getter
    private final String aggregationName;

    private static final String BASE_JSON =
            "{ \"multi_terms\":" +
                    "{\"terms\":[" +
                    "{\"field\": \"{{field1}}\"}," +
                    "{\"field\": \"{{field2}}\"}" +
                    "]," +
                    "\"size\":5" +
                    "}}";

    public String getMultiTermsAggJson() {
        return BASE_JSON.replace("{{field1}}", field1)
                .replace("{{field2}}", field2);
    }
}
