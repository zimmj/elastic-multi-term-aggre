# Multiterms Aggregation example

This is an example, how to use Multiterms Aggregation from Elasticsearch without support from Elastic High Rest Client.
This feature is not yet implemented in the High Rest CLient, and therfore we need to use the Low Rest Client.
With some JSON manipulation, we can use SearchSourceBuilder to build our main query and add the missing aggregation to the json.
THese system makes it simple.

To see how it works, see Class CallService and Test of JsonPathUtil, which adds aggregation to existing or not existing aggregations in searchsource Json.

Feel free to use this code snippets.

Unfortunately the parsing of the answer need to be made manually, and depending on JSON libary in use is different. 
But best option is to get JSONArray Aggregation, get Buckets, and loop over the bucket in deserialize it to Java.
