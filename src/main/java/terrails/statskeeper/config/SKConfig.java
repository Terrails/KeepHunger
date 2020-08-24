package terrails.statskeeper.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import terrails.statskeeper.config.adapter.HealthItemAdapter;

import java.io.*;

public class SKConfig {

    public static boolean keep_experience;
    public static boolean drop_experience;

    public static void initialize() {
        File configFile = new File(FabricLoader.getInstance().getConfigDir().toFile(), "statskeeper.json");

        SKConfigDummy instance = new SKConfigDummy();
        if (configFile.exists()) {
            try {
                Gson gson = new GsonBuilder().registerTypeAdapter(SKHealthConfig.HealthItem.class, new HealthItemAdapter()).create();
                instance = gson.fromJson(new FileReader(configFile), SKConfigDummy.class);
            } catch (FileNotFoundException e) {
                throw new IllegalStateException(e);
            }
        }

        try {
            if (instance == null) instance = new SKConfigDummy();
            FileWriter writer = new FileWriter(configFile);
            Gson gson = new GsonBuilder().registerTypeAdapter(SKHealthConfig.HealthItem.class, new HealthItemAdapter()).setPrettyPrinting().create();
            gson.toJson(instance, writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        SKConfig.keep_experience = instance.keep_experience;
        SKConfig.drop_experience = instance.drop_experience;
        SKHealthConfig.init(instance);
        SKHungerConfig.init(instance);
    }
}
