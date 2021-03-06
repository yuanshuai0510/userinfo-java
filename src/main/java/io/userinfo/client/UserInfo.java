package io.userinfo.client;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import io.userinfo.client.model.Info;
import org.joda.time.DateTime;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

import java.lang.reflect.Type;
import java.util.Date;

/**
 * userinfo.io API wrapper.
 *
 * @author Vincent DURMONT [vdurmont@gmail.com]
 */
public class UserInfo {
    private static final UserInfoService SERVICE;
    protected static final String CLIENT_VERSION_ID = "userinfo-java:1.1.1-SNAPSHOT";

    /**
     * Initializes the UserInfo wrapper: loads a retrofit service for userinfo API
     */
    static {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(DateTime.class, new DateTimeTypeConverter())
                .create();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("https://api.userinfo.io")
                .setConverter(new GsonConverter(gson))
                .build();
        SERVICE = restAdapter.create(UserInfoService.class);
    }

    /**
     * Gets the {@link io.userinfo.client.model.Info} object associated with the given IP address.
     *
     * @param ipAddress the IP address to locate
     *
     * @return the {@link io.userinfo.client.model.Info} associated to the given IP address
     * @throws retrofit.RetrofitError is thrown if the API is not available or if the IP address is malformed
     */
    public static Info getInfo(String ipAddress) {
        return SERVICE.getInfos(ipAddress);
    }

    private static class DateTimeTypeConverter
            implements JsonSerializer<DateTime>, JsonDeserializer<DateTime> {
        @Override
        public JsonElement serialize(DateTime src, Type srcType, JsonSerializationContext context) {
            return new JsonPrimitive(src.toString());
        }

        @Override
        public DateTime deserialize(JsonElement json, Type type, JsonDeserializationContext context)
                throws JsonParseException {
            try {
                return new DateTime(json.getAsString());
            } catch (IllegalArgumentException e) {
                // May be it came in formatted as a java.util.Date, so try that
                Date date = context.deserialize(json, Date.class);
                return new DateTime(date);
            }
        }
    }
}
