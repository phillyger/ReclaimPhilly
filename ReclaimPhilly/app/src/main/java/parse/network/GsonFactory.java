package parse.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import retrofit.converter.GsonConverter;

/**
 * Defines a factory which is responsible for creating a {@link Gson}.
 */
public class GsonFactory {

    private static Gson gson;

    /**
     * Creates a {@link Gson}.
     *
     * @return Gson object
     */
    public static Gson provideGson() {
        // Gson is thread safe - singleton pattern can be applied
        if (gson == null) {
            gson = new GsonBuilder()
//                    .setDateFormat("yyyy-MM-dd HH:mm:ssZ")
//                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                    .create();
        }
        return gson;
    }

    /**
     * Creates a {@link GsonConverter}.
     *
     * @return GsonConverter object
     */
    public static GsonConverter provideGsonConverter() {
        return new GsonConverter(GsonFactory.provideGson());
    }
}
