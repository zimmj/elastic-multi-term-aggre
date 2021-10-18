package com.joel.zimmerli.elastic.low;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;

import java.util.StringJoiner;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonPathUtil {

    public static String addJsonToJsonPath(
            String jsonPath,
            String targetJson,
            MultiTermsAggregation multiTermsAggregation) {
        var documentContext = JsonPath.parse(targetJson);

        try {
            documentContext.read(jsonPath);
            documentContext.put(
                    jsonPath,
                    multiTermsAggregation.getAggregationName(),
                    JsonPath.parse(multiTermsAggregation.getMultiTermsAggJson()).json()
            );
        } catch (PathNotFoundException pathNotFoundException) {
            String[] splitPath = jsonPath.split("\\.");
            var stringJoiner = new StringJoiner(".");
            stringJoiner.add(splitPath[0]);
            for (var index = 1; index < splitPath.length; index++) {
                documentContext.put(
                        stringJoiner.toString(),
                        splitPath[index],
                        getContextForPath(documentContext, stringJoiner.add(splitPath[index]).toString())
                );
            }

            documentContext.put(
                    stringJoiner.toString(),
                    multiTermsAggregation.getAggregationName(),
                    JsonPath.parse(multiTermsAggregation.getMultiTermsAggJson()).json()
            );
        }

        return documentContext.jsonString();
    }

    private static Object getContextForPath(DocumentContext original, String path) {
        try {
            return original.read(path);
        } catch (PathNotFoundException pathNotFoundException) {
            return JsonPath.parse("{}").json();
        }
    }
}
