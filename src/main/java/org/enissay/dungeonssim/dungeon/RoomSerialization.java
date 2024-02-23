package org.enissay.dungeonssim.dungeon;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.enissay.dungeonssim.commands.dungeonloc.TempDungeonBuilds;

public class RoomSerialization {

    private Gson gson;

    public RoomSerialization(){
        this.gson = createGsonInstance();
    }

    private Gson createGsonInstance(){
        return new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .disableHtmlEscaping()
                .disableHtmlEscaping()
                .create();
    }

    public String serialize(final TempDungeonBuilds cim){
        return this.gson.toJson(cim);
    }

    public TempDungeonBuilds deserialize(String json){
        return this.gson.fromJson(json, TempDungeonBuilds.class);
    }

}
