package terrails.statskeeper.config.adapter;

import com.google.gson.*;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import terrails.statskeeper.config.SKHealthConfig;

import java.lang.reflect.Type;

public class HealthItemAdapter implements JsonSerializer<SKHealthConfig.HealthItem>, JsonDeserializer<SKHealthConfig.HealthItem> {

    @Override
    public JsonElement serialize(SKHealthConfig.HealthItem src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        object.add("name", new JsonPrimitive(Registry.ITEM.getId(src.getItem()).toString()));
        object.add("amount", new JsonPrimitive(src.getHealthAmount()));
        return object;
    }

    @Override
    public SKHealthConfig.HealthItem deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = json.getAsJsonObject();
        Item item = Registry.ITEM.get(new Identifier(object.get("name").getAsString()));
        int amount = object.has("amount") ? object.get("amount").getAsInt() : 2;

        if (item == Items.AIR) {
            throw new IllegalArgumentException("Expected an item for " + object.toString());
        }

        return new SKHealthConfig.HealthItem(item, amount);
    }
}
