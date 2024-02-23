package org.enissay.dungeonssim.handlers;

import org.enissay.dungeonssim.DungeonsSim;
import org.enissay.dungeonssim.commands.dungeonloc.TempDungeonBuilds;
import org.enissay.dungeonssim.dungeon.DungeonTemplate;
import org.enissay.dungeonssim.dungeon.RoomLocation;
import org.enissay.dungeonssim.dungeon.RoomSerialization;
import org.enissay.dungeonssim.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class DungeonHandler {

    private static LinkedList<DungeonTemplate> templateList = new LinkedList<>();

    public static DungeonTemplate getTemplateFromID(final int ID) {
        DungeonTemplate template = null;
        switch (ID) {
            case 1:
                template = DungeonHandler.getTemplate("NORMAL_ROOM");
                break;
            case 2:
                template = DungeonHandler.getTemplate("BOSS_ROOM");
                break;
            case 3:
                template = DungeonHandler.getTemplate("SPAWN_ROOM");
                break;

        }
        return template;
    }

    public static DungeonTemplate getTemplateFromName(final String templateName) {
        return templateList.stream().filter(template ->
                template.getName().equals(templateName)).findFirst().get();
    }

    public static void register(final DungeonTemplate... templates) {
        Arrays.asList(templates).forEach(temp -> {
            templateList.add(temp);
            System.out.println("Added " + temp.getName());
        });
    }

    public static DungeonTemplate getTemplate(final String templateName) {
        return templateList.stream().filter(temp -> temp.getName().equals(templateName)).findFirst().get();
    }

    public static LinkedList<DungeonTemplate> getTemplateList() {
        return templateList;
    }

    public static void publishRoom(final TempDungeonBuilds tempDungeonBuilds) {
        final String json = new RoomSerialization().serialize(tempDungeonBuilds);
        FileUtils.save(new File(DungeonsSim.getInstance().getDataFolder().getAbsolutePath() +
                "/template/" + tempDungeonBuilds.getTemplateName() + "/" + tempDungeonBuilds.getName() + ".json"), json);
    }

    public static TempDungeonBuilds loadRoom(final String templateName, final String roomName) {
        final File file = new File(DungeonsSim.getInstance().getDataFolder().getAbsolutePath() +
                "/template/" + templateName + "/" + roomName + ".json");
        final TempDungeonBuilds tempDungeonBuilds = new RoomSerialization().deserialize(FileUtils.loadContent(file));
        return tempDungeonBuilds;
    }

    public static List<TempDungeonBuilds> loadRooms(final String templateName) {
        final File file = new File(DungeonsSim.getInstance().getDataFolder().getAbsolutePath() +
                "/template/" + templateName);
        List<TempDungeonBuilds> builds = new ArrayList<>();
        if (file != null && file.exists()) {
            final String[] mapsName = file.list();

            if (mapsName.length > 0) {

                for (String names : mapsName) {
                    names = names.replace(".json", "");
                    builds.add(loadRoom(templateName, names));
                }
            }
        }
        return builds;
    }
}
