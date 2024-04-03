package com.yuyu.srwildentity.config.condition;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.HashMap;
import java.util.List;

/**
 * @BelongsProject: SrWildEntity
 * @BelongsPackage: com.yuyu.srwildentity.config.condition
 * @FileName: BiomeEntity
 * @Author: 峰。
 * @Date: 2024/4/3-15:06
 * @Version: 1.0
 * @Description: 读取实体刷新的配置相关文件
 */
public class BiomeEntityRefreshSettings {
    private final HashMap<String,List<String>> biomeEntityMap;

    private final HashMap<String,HashMap<String,EntityCondition>> biomeEntityConditionMap;


    public BiomeEntityRefreshSettings(Plugin plugin) {
        this.biomeEntityMap = new HashMap<>();
        this.biomeEntityConditionMap = new HashMap<>();

        loadBiomeEntitiesConfig(plugin);

        loadEntityConditionConfig(plugin);

    }

    private void loadEntityConditionConfig(Plugin plugin) {
        //保存文件
        plugin.saveResource("entityCondition.yml",false);

        FileConfiguration config;
        File file = new File(plugin.getDataFolder(), "entityCondition.yml");
        config = YamlConfiguration.loadConfiguration(file);
        for (String biomeName : this.biomeEntityMap.keySet()){
            HashMap<String,EntityCondition> entityConditionHashMap = new HashMap<>();

            List<String> entities = this.biomeEntityMap.get(biomeName);
            for (String entityName : entities){
                EntitySite entitySite = EntitySite.fromId(config.getInt(biomeName + "." + entityName + ".site"));
                int light = config.getInt(biomeName+"."+entityName+".light");
                long time = config.getLong(biomeName+"."+entityName+".timing");
                int nums = config.getInt(biomeName+"."+entityName+".nums");
                int yMax = config.getInt(biomeName+"."+entityName+".yMax");
                int yMin = config.getInt(biomeName+"."+entityName+".yMin");
                EntityCondition entityCondition = new EntityCondition(entityName, biomeName, entitySite, light, time, nums, yMax, yMin);

                //存入单个群系刷新map
                entityConditionHashMap.put(entityName, entityCondition);
            }
            //存入记录总群系的map
            biomeEntityConditionMap.put(biomeName,entityConditionHashMap);
        }

        plugin.getLogger().info("群系刷新相关文件读取完毕");
    }

    public void loadBiomeEntitiesConfig(Plugin plugin){
        //保存文件
        plugin.saveResource("biomeEntity.yml",false);

        FileConfiguration config;
        File file = new File(plugin.getDataFolder(), "biomeEntity.yml");
        config = YamlConfiguration.loadConfiguration(file);

        //获取所有生物群系
        plugin.saveResource("biome.yml",false);
        FileConfiguration biomeconfig = null;
        File biomeName = new File(plugin.getDataFolder(),"biome.yml");
        biomeconfig = YamlConfiguration.loadConfiguration(biomeName);
        List<String> biomeList = biomeconfig.getStringList("biome");

        if (config == null || biomeconfig == null){
            plugin.getLogger().info("biomeEntity.yml或者 biome.yml文件读取失败!");
            return;
        }

        //遍历所有可能的生物群系
        for (String section : biomeList){
            //判断是否存在
            if (config.contains(section)){
                List<String> entityList = config.getStringList(section+".ENTITY");
                this.biomeEntityMap.put(section,entityList);
            }
        }

        plugin.getLogger().info("biomeEntity.读取完成");
    }

    public HashMap<String, List<String>> getEntityMap() {
        return biomeEntityMap;
    }
}
