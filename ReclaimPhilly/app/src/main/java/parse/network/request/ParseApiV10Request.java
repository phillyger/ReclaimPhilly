package parse.network.request;

import parse.network.ParseApiV10;

/**
 * Created by gosullivan on 3/7/14.
 */
public abstract class ParseApiV10Request<RESULT>
        extends BaseParseRequest<RESULT, ParseApiV10> {

    public ParseApiV10Request(Class<RESULT> clazz) {
        super(clazz, ParseApiV10.class);
    }


    /**
     * Converts a id string value into a Parse.com pointer type
     * @param idType String the type of the identifier (i.e. userId, geoPointId)
     * @param idValue String the value of the identifier
     * @param className String the value of the className
     * @return
     */
    public static String convertIdToPointer(String idType, String idValue, String className) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"");
        sb.append(idType);
        sb.append("\":{\"__type\":\"Pointer\",");
        sb.append("\"className\":\""+className+"\",\"objectId\":");
        sb.append("\"");
        sb.append(idValue);
        sb.append("\"");
        sb.append("}}");

        return sb.toString();
    }
}