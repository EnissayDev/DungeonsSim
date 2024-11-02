package org.enissay.dungeonssim.commands.party;

import com.samjakob.spigui.SpiGUI;
import com.samjakob.spigui.buttons.SGButton;
import com.samjakob.spigui.item.ItemBuilder;
import com.samjakob.spigui.menu.SGMenu;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.optional.OptionalArg;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.enissay.dungeonssim.DungeonsSim;
import org.enissay.dungeonssim.commands.Generator2D;
import org.enissay.dungeonssim.commands.dungeonloc.TempDungeonBuilds;
import org.enissay.dungeonssim.dungeon.DungeonTemplate;
import org.enissay.dungeonssim.dungeon.party.*;
import org.enissay.dungeonssim.dungeon.system.*;
import org.enissay.dungeonssim.gui.InventoryGUI;
import org.enissay.dungeonssim.gui.impl.ClassGUI;
import org.enissay.dungeonssim.gui.impl.DifficultyGUI;
import org.enissay.dungeonssim.handlers.DungeonHandler;
import org.enissay.dungeonssim.handlers.PartyHandler;
import org.enissay.dungeonssim.utils.InventoryFill;
import org.enissay.dungeonssim.utils.ItemUtils;
import org.enissay.dungeonssim.utils.MessageUtils;

import java.time.Instant;
import java.util.*;

@Command(name = "party", aliases = {"dungeonparty", "dungeonp", "dp", "p"})
public class PartyCommands {

    private void sendInfo(final Player player, final String msg) {
        sendMessage(player, "&7" + msg);
    }

    private void sendNormal(final Player player, final String msg) {
        sendMessage(player, "&a" + msg);
    }

    private void sendError(final Player player, final String msg) {
        sendMessage(player, "&c" + msg);
    }

    private void sendMessage(final Player player, final String msg) {
        player.sendMessage(msg.replace('&', '§'));
    }


    @Execute(name = "setup")
    void setup(@Context CommandSender sender) {
        if (sender instanceof Player) {
            final Player player = (Player) sender;
            player.performCommand("dp create " + player.getName());
            //long testSeed = 7081120085L;
            player.performCommand("dp start EASY");
        }
    }

    @Execute(name = "create")
    void create(@Context CommandSender sender, @Arg String partyName) {
        if (sender instanceof Player) {
            final Player player = (Player) sender;
            final DungeonParty dungeonParty = PartyHandler.getPartyOf(player.getUniqueId());
            if (dungeonParty != null) {
                sendError(player, "You already have a party.");
            }else if (!PartyHandler.isNameAvailable(partyName)) {
                sendError(player, "This name is already used.");
            }else {
                if (partyName.length() <= 16) {
                    final LinkedList<Player> players = new LinkedList<>();
                    players.add(player);
                /*final Dungeon dungeon = new Dungeon(DungeonHandler.getDungeons().size() + 1,
                        players.stream().map(Player::getUniqueId).toList());*/
                    final DungeonParty dp = new DungeonParty((PartyHandler.getDungeonPartys().size() + 1), 6, partyName, null, false);
                    dp.addPlayer(player.getUniqueId(), DungeonRole.HOST);
                    PartyHandler.addDungeonParty(dp);
                    sendNormal(player, "You have created the party " + partyName + ".");
                }else sendError(player, "The party name must not exceed 16 characters.");
            }
        }
    }

    @Execute(name = "join")
    void join(@Context CommandSender sender, @Arg String partyName) {
        if (sender instanceof Player) {
            final Player player = (Player) sender;
            final DungeonParty dungeonParty = PartyHandler.getPartyOf(player.getUniqueId());
            if (dungeonParty != null) {
                sendError(player, "You already have a party.");
            }else if (PartyHandler.getParty(partyName) == null) {
                sendError(player, "This party does not exist.");
            }else if (DungeonHandler.isPlayerInADungeon(player)) {
                sendError(player, "You can't join a party while you're in a dungeon.");
            }else {
                if (PartyHandler.getParty(partyName).getStatus() != DungeonPartyStatus.LOBBY) {
                    sendError(player, "You can't join this party since it's full or it's already playing.");
                } else {
                    final DungeonParty targetParty = PartyHandler.getParty(partyName);
                    if (targetParty != null && targetParty.isPublic()) {
                        targetParty.addPlayer(player.getUniqueId(), DungeonRole.PLAYER);
                        targetParty.getPlayers().forEach((uuid, dungeonRole) -> {
                            if (Bukkit.getOfflinePlayer(uuid).isOnline()) {
                                sendMessage(Bukkit.getPlayer(uuid), "&b" + player.getName() + "&e has joined the party.");
                            }
                        });
                    } else sendError(player, "This party is private.");
                }
            }
        }
    }

    @Execute(name = "public")
    void create(@Context CommandSender sender) {
        if (sender instanceof Player) {
            final Player player = (Player) sender;
            final DungeonParty dungeonParty = PartyHandler.getPartyOf(player.getUniqueId());
            if (dungeonParty == null) sendError(player, "You are not in a party.");
            else if (dungeonParty != null && dungeonParty.getRoleOf(player.getUniqueId()) != DungeonRole.HOST) {
                sendError(player, "Only host can make the party public.");
            }else if (dungeonParty != null && dungeonParty.getRoleOf(player.getUniqueId()) != DungeonRole.HOST && (dungeonParty.getDungeon() != null || dungeonParty.getStatus() == DungeonPartyStatus.PLAYING)){
                sendError(player, "You are currently in a dungeon.");
            }else {
                dungeonParty.setPublic(!dungeonParty.isPublic());
                dungeonParty.sendMessageToMembers(ChatColor.AQUA + player.getName() + ChatColor.YELLOW + " has changed the status of the party.");
                if (dungeonParty.isPublic()) {
                    /*final net.kyori.adventure.text.TextComponent textComponent2 = Component.text()
                            .content("You're a ")
                            .color(TextColor.color(0x443344))
                            .append(Component.text().content("Bunny").color(NamedTextColor.LIGHT_PURPLE))
                            .append(Component.text("! Press "))
                            .append(
                                    Component.keybind().keybind("key.sneak")
                                            .color(NamedTextColor.LIGHT_PURPLE)
                                            .decoration(TextDecoration.BOLD, true)
                                            .build()
                            )
                            .append(Component.text(" to jump!"))
                            .build();*/
                    final net.kyori.adventure.text.TextComponent textComponent2 = Component.text()
                            /*.append(Component.text("PARTY ").color(NamedTextColor.GOLD)
                                    .decorate(TextDecoration.BOLD))*/
                            .color(NamedTextColor.GRAY)
                            /*.append(Component.text().content("click")
                                    .clickEvent(net.kyori.adventure.text.event.ClickEvent.clickEvent(net.kyori.adventure.text.event.ClickEvent.Action.RUN_COMMAND, "/party join " + dungeonParty.getName()))
                                    .color(NamedTextColor.GREEN)
                                    .decorate(TextDecoration.UNDERLINED))*/
                            .append(Component.text(Bukkit.getOfflinePlayer(dungeonParty.getPlayers(DungeonRole.HOST).get(0)).getName()).color(NamedTextColor.YELLOW))
                            .append(Component.text("'s party "))
                            .append(Component.text("[" + dungeonParty.getName() + "]")
                                    .hoverEvent(net.kyori.adventure.text.event.HoverEvent.showText(Component.text()
                                            .append(Component.text("Players: ").color(NamedTextColor.WHITE)
                                                    .append(Component.text("[" + dungeonParty.getPlayers().size() + "/" + dungeonParty.getMaxPlayers() + "]").color(NamedTextColor.GRAY))).build()))
                                    .color(NamedTextColor.DARK_GRAY))
                            .append(Component.text(" is now public! "))
                            .append(Component.text().content("Click to join")
                                    .clickEvent(net.kyori.adventure.text.event.ClickEvent.clickEvent(net.kyori.adventure.text.event.ClickEvent.Action.RUN_COMMAND, "/party join " + dungeonParty.getName()))
                                    .color(NamedTextColor.GREEN)
                                    .decorate(TextDecoration.UNDERLINED))
                            .build();
                    Bukkit.getOnlinePlayers().forEach(onlinePlayer -> {
                        DungeonsSim.getInstance().adventure().player(onlinePlayer).sendMessage(textComponent2);
                    });
                }
            }
        }
    }

    @Execute(name = "start")
    void start(@Context CommandSender sender, @OptionalArg DungeonDifficulty dungeonDifficulty, @OptionalArg Long seed) {
        if (sender instanceof Player) {
            final Player player = (Player) sender;
            final DungeonParty dungeonParty = PartyHandler.getPartyOf(player.getUniqueId());

            if (dungeonParty == null) sendError(player, "You are not in a party.");
            else if (dungeonParty != null && dungeonParty.getRoleOf(player.getUniqueId()) != DungeonRole.HOST) {
                sendError(player, "Only host can start the dungeon.");
            }else if (dungeonParty != null &&
                    dungeonParty.getRoleOf(player.getUniqueId()) == DungeonRole.HOST &&
                    (dungeonParty.getDungeon() != null && dungeonParty.getStatus() == DungeonPartyStatus.PLAYING)){
                sendError(player, "You are already in a dungeon.");
            }else {
                if (dungeonDifficulty == null) {
                    //GUI WITH DIFFICULTIES
                    InventoryGUI inventoryGUI = new DifficultyGUI();
                    DungeonsSim.getInstance().getGuiManager().openGUI(inventoryGUI, player);
                }else{
                    final LinkedList<Player> players = new LinkedList<>();
                    final LinkedList<OfflinePlayer> removedPlayers = new LinkedList<>();
                    dungeonParty.getPlayers().forEach(((uuid, dungeonRole) -> {
                        if (Bukkit.getOfflinePlayer(uuid).isOnline())
                            players.add(Bukkit.getPlayer(uuid));
                        else {
                            dungeonParty.removePlayer(uuid);
                            removedPlayers.add(Bukkit.getOfflinePlayer(uuid));
                        }
                    }));
                    MessageUtils.broadcast("&e&lA DUNGEON HAS BEEN STARTED BY &6&l" + player.getName(), MessageUtils.BroadcastType.MESSAGE, MessageUtils.TargetType.PARTY, dungeonParty);
                    //dungeonParty.sendMessageToMembers("&e&lA DUNGEON HAS BEEN STARTED BY &6&l" + player.getName());
                    if (removedPlayers.size() > 0)
                        dungeonParty.sendMessageToMembers("&c" + String.join(", ", removedPlayers.stream().map(OfflinePlayer::getName).toList()) + " were removed from the party since they have left the server.");
                    long startTime = System.currentTimeMillis();

                    final Dungeon dungeon = new Dungeon(DungeonHandler.getDungeons().size() + 1,
                            players.stream().map(Player::getUniqueId).toList(), dungeonDifficulty, Instant.now(), 3);
                    dungeonParty.setDungeon(dungeon);
                    final DungeonGeneration dungeonGeneration = new DungeonGeneration(dungeon);
                    if (!Objects.isNull(seed)) {
                        MessageUtils.broadcast("&aSeed: " + seed, MessageUtils.BroadcastType.MESSAGE, MessageUtils.TargetType.PARTY, dungeonParty);
                        dungeonGeneration.setSeed(seed);
                    }else
                        MessageUtils.broadcast("&aSeed: " + dungeonGeneration.getSeed(), MessageUtils.BroadcastType.MESSAGE, MessageUtils.TargetType.PARTY, dungeonParty);

                    dungeonGeneration.setGridBlocks(33)
                            .setGridOptions(6, 6)
                            .setMinRooms(10)
                            .setMaxRooms(10)
                            .build();

                    final double DUNGEONS_GAP = 1.1;
                    //final Location loc = new Location(player.getWorld(), 19013.5 + ((DungeonHandler.getDungeons().size()) * (dungeonGeneration.getGridWidth()) * dungeonGeneration.getGridBlocks() * DUNGEONS_GAP), 141, 20846.5);
                    final Location loc = new Location(player.getWorld(), 2000 + ((DungeonHandler.getDungeons().size()) * (dungeonGeneration.getGridWidth()) * dungeonGeneration.getGridBlocks() * DUNGEONS_GAP), 141, 2000);

                    DungeonParser.parse(dungeon, dungeonGeneration, loc/*player.getLocation()*/);

                    final HashMap<DungeonGeneration.GridCell, String> roomNames = new HashMap<>();
                    final HashMap<DungeonGeneration.GridCell, String> roomTemplates = new HashMap<>();

                    dungeon.getRooms().forEach(room -> {
                        roomNames.put(room.getGridCell(), room.getRoomName());
                        roomTemplates.put(room.getGridCell(), room.getTemplate().getName());
                    });

                    final DungeonRoom room = dungeon.getRooms().stream().filter(dungeonRoom -> dungeonRoom.getTemplate().getName().equals("SPAWN_ROOM")).findAny().orElse(null);
                    final TempDungeonBuilds tempDungeonBuilds = DungeonHandler.loadRoom(room.getTemplate().getName(), room.getRoomName());

                    dungeonParty.setStatus(DungeonPartyStatus.PLAYING);
                    final Generator2D generator2D = new Generator2D(dungeonGeneration, dungeonGeneration.getGridHeight(), dungeonGeneration.getGridWidth(), dungeonGeneration.getGridMap(), dungeonGeneration.getGridBlocks(), roomNames, roomTemplates);
                    generator2D.generateMap(loc, player.getWorld());

                    final ItemStack item = ItemUtils.item(Material.FILLED_MAP, "dungeon#" + dungeon.getID(), "");
                    MapMeta meta = (MapMeta) item.getItemMeta();
                    MapView view = Bukkit.createMap(player.getWorld());

                    view.getRenderers().clear();
                    view.addRenderer(generator2D);
                    view.setTrackingPosition(true);
                    view.setUnlimitedTracking(true);
                    view.setCenterX(loc.getBlockX());
                    view.setCenterZ(loc.getBlockZ());
                    view.setScale(MapView.Scale.CLOSE);
                    meta.setMapView(view);
                    item.setItemMeta(meta);

                    players.forEach(dungeonPlayer -> {
                        //scheduler.runTask(DungeonsSim.getInstance(), () -> {
                        if (tempDungeonBuilds.getRoomLocations().get("spawnLocation") != null && room.getLocationFromTemplate("spawnLocation") != null) {
                            dungeonPlayer.teleport(room.getLocationFromTemplate("spawnLocation"));
                        }else if (room.getCuboid() != null) {
                            dungeonPlayer.teleport(room.getCuboid().getCenter());
                            final PotionEffect potionEffect = new PotionEffect(PotionEffectType.NIGHT_VISION, PotionEffect.INFINITE_DURATION, 0);
                            if (!dungeonPlayer.hasPotionEffect(PotionEffectType.NIGHT_VISION))
                                dungeonPlayer.addPotionEffect(potionEffect);
                        }
                        //});
                        dungeonPlayer.getInventory().setItemInOffHand(item);

                        long endTime = System.currentTimeMillis();
                        long deltaTime = endTime - startTime;

                        sendNormal(dungeonPlayer, "Dungeon generated! took " + deltaTime + "ms" + " (" + deltaTime * Math.pow(10, -3) + "s)");
                    });
                    MessageUtils.broadcast("&6&lDUNGEON&b#&l" + dungeonParty.getID(), MessageUtils.BroadcastType.TITLE, MessageUtils.TargetType.DUNGEON, dungeonParty.getDungeon());
                    MessageUtils.broadcastSound(Sound.ENTITY_WITHER_SPAWN, 1f, 1f, MessageUtils.TargetType.DUNGEON, dungeonParty.getDungeon());
                    dungeonParty.getDungeon().setTimeStarted(Instant.now());
                }
            }
        }
    }

    /*public int getMinSlot(final SGMenu menu, final int currentPage) {
        for (int r = (currentPage * menu.getInventory().getSize()) + 1; r <= (menu.getInventory().getSize() + currentPage * menu.getInventory().getSize()) - 10; r++) {
            if (menu.getButton(r) == null ||
                    (menu.getButton(r) != null &&
                            menu.getButton(r).getIcon().getType() == Material.AIR)) {
                return r;
            }
        }
        return 0;
    }

    public int getNormalMinSlot(final Inventory menu) {
        for (int r = 0; r < menu.getSize() - 10; r++) {
            if (menu.getContents()[r] == null ||
                    (menu.getContents()[r] != null &&
                            menu.getContents()[r].getType() == Material.AIR)) {
                return r;
            }
        }
        return 0;
    }*/

    @Execute(name = "gui")
    void gui(@Context CommandSender sender) {
        if (sender instanceof Player) {
            final Player player = (Player) sender;
            final DungeonParty dungeonParty = PartyHandler.getPartyOf(player.getUniqueId());
            final SpiGUI spiGUI = DungeonsSim.getInstance().getSpiGUI();
            final SGMenu GUI = spiGUI.create("PARTYS", 5);

            final LinkedList<DungeonParty> partys = new LinkedList<>(PartyHandler.getDungeonPartys());
            /*for (int i = 0; i < 100; i++) {
                partys.add(new DungeonParty((partys.size() + 1), 1, "nigger#" + (partys.size() + 1), null, true));
            }*/
            InventoryFill inventoryFill = new InventoryFill(GUI.getInventory(), GUI.getInventory().getSize() - 9);
            inventoryFill.fillSidesWithItem(new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
            GUI.setOnPageChange(sgMenu -> {
                inventoryFill.getSideSlots().forEach(side -> GUI.setButton(GUI.getCurrentPage(), side, new SGButton(new ItemStack(Material.BLACK_STAINED_GLASS_PANE))));
                player.updateInventory();
                GUI.refreshInventory(player);
            });
            GUI.setToolbarBuilder((slot, page, defaultType, menu) -> {
                switch (defaultType) {
                    case UNASSIGNED:
                        if (dungeonParty != null && slot == 1)
                            return new SGButton(
                                    new ItemBuilder(Material.ANVIL)
                                            .name(ChatColor.YELLOW + "Set your party to public/reverse")
                                            .build()
                            ).withListener((event) -> {
                                player.closeInventory();
                                player.performCommand("dp public");
                            });
                        else return null;
                    case PREV_BUTTON:
                        if (menu.getCurrentPage() > 0) return new SGButton(new ItemBuilder(Material.ARROW)
                                .name("&7\u2190 Previous Page")
                                .lore("",
                                        "&fClick to move to",
                                        "&fpage &a" + (menu.getCurrentPage()) + "&f.",
                                        ""
                                ).build()
                        ).withListener(event -> {
                            event.setCancelled(true);
                            menu.previousPage(event.getWhoClicked());
                        });
                        else return null;
                    case CURRENT_BUTTON:
                        return new SGButton(
                                new ItemBuilder(Material.BOOK)
                                        .name(ChatColor.YELLOW + "Page: " + ChatColor.GRAY + "(" + ChatColor.AQUA + (menu.getCurrentPage() + 1) + ChatColor.DARK_GRAY + "/" + ChatColor.DARK_AQUA + menu.getMaxPage() + ChatColor.GRAY + ")")
                                        .build()
                        ).withListener((event) -> {
                            menu.refreshInventory(event.getWhoClicked());
                        });
                    case NEXT_BUTTON:
                        if (menu.getCurrentPage() < menu.getMaxPage() - 1)
                            return new SGButton(new ItemBuilder(Material.ARROW)
                                    .name("&7Next page \u2192")
                                    .lore("",
                                            "&fClick to move to",
                                            "&fpage &a" + (menu.getCurrentPage() + 2) + "&f.",
                                            ""
                                    ).build()
                            ).withListener(event -> {
                                event.setCancelled(true);
                                menu.nextPage(event.getWhoClicked());
                            });
                        else return null;
                }

                return spiGUI.getDefaultToolbarBuilder().buildToolbarButton(slot, page, defaultType, menu);
            });

            GUI.getOnPageChange().accept(GUI);

            int page = 0;
            List<Integer> slotsToFill = inventoryFill.getNonSideSlots();
            int ind = 0;
            int curr = slotsToFill.get(0);
            if (partys.size() > 0) {
                for (int in = 0; in < partys.size(); in++) {
                    final DungeonParty chosen = partys.get(in);

                    List<String> names = new ArrayList<>();
                    chosen.getPlayers(DungeonRole.PLAYER).forEach((uuid) -> {
                        names.add(Bukkit.getOfflinePlayer(uuid).getName());
                    });

                    String value = null;
                    //String value = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2E1Nzc1ZWFiYmJmODFmOGE5ZTNmYjE0MGZiN2RjYjBlNjhjNWIyZDAyZTEwYjEwNDM1NjQxMjU0OTRmMWEyZiJ9fX0=";
                    if (!chosen.isPublic())
                        value = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWNlODk0MTE1NjhiOWY2NGMwM2IyMzY4YTQyNzZjMGZhZGZjMWE4MDBjNzFiNjQ2MDA3ZjIwMzA4MmE3YTc5MCJ9fX0=";
                    switch (chosen.getStatus()) {
                        case FULL:
                            value = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWNlODk0MTE1NjhiOWY2NGMwM2IyMzY4YTQyNzZjMGZhZGZjMWE4MDBjNzFiNjQ2MDA3ZjIwMzA4MmE3YTc5MCJ9fX0=";
                            break;
                        case PLAYING:
                            value = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2QzYTQ0OTFkMWI2NWQ3ZTlhYjc3ODAzMTRlYTZhNDE1YzI1YmI3YWM1ZTJlZjg2MTY0ODZiMjRlYTYyMTJlNyJ9fX0=";
                            break;
                    }

                    if (in != 0 && (in) % slotsToFill.size() == 0) page++;
                    //ItemStack result = SkullCreator.itemFromBase64(value);
                    ItemStack head = null;
                    ItemStack result = null;
                    String name = "&b#&l" + chosen.getID() + " &a" + chosen.getName() + " &7- &d" + chosen.getPlayers().size() + "&8/&5" + chosen.getMaxPlayers();
                    String[] lore = new String[]{"",
                            "&fPublic: " + (chosen.isPublic() ? "&aYes" : "&cNo"),
                            "",
                            "&fStatus: &a" + chosen.getStatus().name(),
                            "&fPlayers: &d" + chosen.getPlayers().size() + "&8/&5" + chosen.getMaxPlayers(),
                            "",
                            "&fLeader: &a" + Bukkit.getOfflinePlayer(chosen.getPlayers(DungeonRole.HOST).get(0)).getName(),
                            "&fMembers: &b" +
                                    (chosen.getPlayers(DungeonRole.PLAYER).size() > 0 ?
                                            String.join(", ", names) : "&cNone"),
                            "",
                            "&eClick to join this party"};
                    if (value != null) {
                        head = ItemUtils.skullCustom(name, value);

                        result = new ItemBuilder(head)
                                .flag(ItemFlag.HIDE_ATTRIBUTES)
                                .lore(lore)
                                .build();
                    }else {
                        result = new ItemBuilder(Material.PLAYER_HEAD)
                                .name(name)
                                .flag(ItemFlag.HIDE_ATTRIBUTES)
                                .lore(lore)
                                .skullOwner(Bukkit.getOfflinePlayer(chosen.getPlayers(DungeonRole.HOST).get(0)).getName())
                                .build();
                    }
                    final SGButton sgButton = new SGButton(result).withListener(event -> {
                        event.setCancelled(true);
                        player.closeInventory();

                        sendInfo(player, "Attempting to join " + chosen.getName() + "...");
                        player.performCommand("dp join " + chosen.getName());
                    });
                    GUI.setButton(page, curr, sgButton);

                    if (curr < slotsToFill.get(slotsToFill.size() - 1)) {
                        ind++;
                        curr = slotsToFill.get(ind);
                    }
                    else {
                        ind = 0;
                        curr = slotsToFill.get(ind);
                    }
                }
            }else {
                final SGButton sgButton = new SGButton(ItemUtils.item(Material.BEDROCK, ChatColor.RED + "No party available.")).withListener(event -> {
                    event.setCancelled(true);
                });
                GUI.setButton(22, sgButton);
            }

            GUI.getOnPageChange().accept(GUI);

            GUI.refreshInventory(player);
            player.updateInventory();
            player.openInventory(GUI.getInventory());
        }
    }

    @Execute(name = "invite")
    void invite(@Context CommandSender sender, @Arg Player target) {
        if (sender instanceof Player) {
            final Player player = (Player) sender;
            final DungeonParty dungeonParty = PartyHandler.getPartyOf(player.getUniqueId());
            if (dungeonParty == null) sendError(player, "You are not in a party.");
            else if (dungeonParty != null && dungeonParty.getRoleOf(player.getUniqueId()) != DungeonRole.HOST)
                sendError(player, "Only host can invite members.");
            else if (dungeonParty != null && DungeonHandler.isPlayerInADungeon(target))
                sendError(player, "This player is currently in a dungeon.");
            else if (dungeonParty != null && dungeonParty.getStatus() != DungeonPartyStatus.LOBBY)
                sendError(player, "You can't invite anyone in your current party state.");
            else {
                final DungeonParty targetDungeonParty = PartyHandler.getPartyOf(target.getUniqueId());
                if (targetDungeonParty != null) sendError(player, "This player is already in a party.");
                else {
                    final DungeonInvite invite = new DungeonInvite(target.getUniqueId(), dungeonParty);
                    if (!invite.isInvited()) {
                        invite.create();
                        TextComponent inviteMessage = new TextComponent(ChatColor.AQUA + player.getName() + ChatColor.YELLOW + " has invited you to join their party " + ChatColor.GOLD + dungeonParty.getName() + ChatColor.YELLOW + ". Type /dungeonparty accept or /dungeonparty refuse to respond.");
                        inviteMessage.setClickEvent( new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/dp accept"));
                        inviteMessage.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.GRAY + "-> " + ChatColor.YELLOW + "Click to join the party " + ChatColor.GOLD + dungeonParty.getName() + ChatColor.GRAY + " <-")));

                        //dungeonParty.invite(target.getUniqueId(), 60);
                        target.spigot().sendMessage(inviteMessage);
                        //sendNormal(target, player.getName() + " has invited you to join their party. Type /dungeonparty accept or /dungeonparty refuse to respond.");
                        sendNormal(player, "Invitation sent to " + target.getName());
                    }else sendError(player, "You have already invited this player to join your party.");
                }
            }
        }
    }

    @Execute(name = "kick")
    void kick(@Context CommandSender sender, @Arg PartyMember target) {
        if (sender instanceof Player) {
            final Player player = (Player) sender;
            final DungeonParty dungeonParty = PartyHandler.getPartyOf(player.getUniqueId());
            if (dungeonParty == null) sendError(player, "You are not in a party.");
            else if (dungeonParty != null && dungeonParty.getRoleOf(player.getUniqueId()) != DungeonRole.HOST)
                sendError(player, "Only host can kick members.");
            else {
                //final DungeonParty targetDungeonParty = PartyHandler.getPartyOf(target.getUniqueId());
                if (!dungeonParty.getPlayers().containsKey(target.getPlayer().getUniqueId())) sendError(player, "This player is not in your party.");
                else if (target.getPlayer().getUniqueId().compareTo(player.getUniqueId()) == 0) sendError(player, "You can't kick yourself.");
                else {
                    dungeonParty.removePlayer(target.getPlayer().getUniqueId());
                    if (Bukkit.getOfflinePlayer(target.getPlayer().getUniqueId()).isOnline()) {
                        sendMessage(Bukkit.getPlayer(target.getPlayer().getUniqueId()), "&cYou have been kicked by &b" + player.getName() + " &cfrom your party.");
                    }
                    dungeonParty.sendMessageToMembers("&b" + target.getPlayer().getName() + " &ehas been kicked from your party.");
                }
            }
        }
    }

    /*@Execute(name = "kick")
    void kick(@Context CommandSender sender, @Arg OfflinePlayer target) {
        if (sender instanceof Player) {
            final Player player = (Player) sender;
            final DungeonParty dungeonParty = PartyHandler.getPartyOf(player.getUniqueId());
            if (dungeonParty == null) sendError(player, "You are not in a party.");
            else if (dungeonParty != null && dungeonParty.getRoleOf(player.getUniqueId()) != DungeonRole.HOST)
                sendError(player, "Only host can kick members.");
            else {
                //final DungeonParty targetDungeonParty = PartyHandler.getPartyOf(target.getUniqueId());
                if (!dungeonParty.getPlayers().containsKey(target.getUniqueId())) sendError(player, "This is not in your party.");
                else {
                    dungeonParty.removePlayer(target.getUniqueId());
                    if (Bukkit.getOfflinePlayer(target.getUniqueId()).isOnline()) {
                        sendMessage(Bukkit.getPlayer(target.getUniqueId()), "&cYou have been kicked by &b" + player.getName() + " &cfrom your party.");
                    }
                    dungeonParty.sendMessageToMembers("&b" + target.getName() + " &ehas been kicked from your party.");
                }
            }
        }
    }*/

    @Execute(name = "accept")
    void accept(@Context CommandSender sender) {
        if (sender instanceof Player) {
            final Player player = (Player) sender;
            final DungeonInvite pendingInvitation = PartyHandler.getDungeonInvite(player.getUniqueId());
            if (pendingInvitation == null)
                sendError(player, "You have no pending party invitations.");
            else if (DungeonHandler.isPlayerInADungeon(player))
                sendError(player, "You can't accept invitations while you're in a dungeon.");
            else {
                pendingInvitation.getPartyInvitation().addPlayer(player.getUniqueId(), DungeonRole.PLAYER);
                Player leader = Bukkit.getPlayer(pendingInvitation.getPartyInvitation().getPlayers(DungeonRole.HOST).get(0));
                if (leader.isOnline()) {
                    sendNormal(player, "You have accepted " + leader.getName() + "'s invitation.");
                }
                pendingInvitation.getPartyInvitation().getPlayers().forEach((uuid, dungeonRole) -> {
                    if (Bukkit.getOfflinePlayer(uuid).isOnline()) {
                        sendMessage(Bukkit.getPlayer(uuid), "&b" + player.getName() + "&e has joined the party.");
                    }
                });
                pendingInvitation.accepted();
                // Remove the invitation
                //PartyHandler.removeInvitation(player.getUniqueId());
            }
        }
    }

    @Execute(name = "leave")
    void leave(@Context CommandSender sender) {
        if (sender instanceof Player) {
            final Player player = (Player) sender;
            final DungeonParty party = PartyHandler.getPartyOf(player.getUniqueId());
            if (party == null) {
                sendError(player, "You are not in a party.");
            }else if (party.getPlayers(DungeonRole.HOST).get(0).compareTo(player.getUniqueId()) != 0){
                party.removePlayer(player.getUniqueId());
                party.sendMessageToMembers("&b" + player.getName() + " &ehas left the party.");
                sendNormal(player, "You have left the party.");
            }else {
                party.sendMessageToMembers("&c" + player.getName() + " has disbanded the party.");
                PartyHandler.removeDungeonParty(party);
            }
        }
    }

    @Execute(name = "list")
    void list(@Context CommandSender sender) {
        if (sender instanceof Player) {
            final Player player = (Player) sender;
            final DungeonParty party = PartyHandler.getPartyOf(player.getUniqueId());
            if (party == null) sendError(player, "You are not in a party.");
            else {
                List<String> names = new ArrayList<>();
                party.getPlayers(DungeonRole.PLAYER).forEach((uuid) -> {
                    ChatColor cl = ChatColor.GREEN;
                    if (!Bukkit.getOfflinePlayer(uuid).isOnline()) cl = ChatColor.RED;
                    names.add(Bukkit.getOfflinePlayer(uuid).getName() + cl + " ●" + ChatColor.AQUA);
                });
                sendMessage(player, "     &6&lPARTY&b#" + party.getID());
                sendMessage(player, "");
                sendMessage(player, "&8- &7 Name: &6" + party.getName());
                sendMessage(player, "&8- &7 Status: &a" + party.getStatus().name());
                sendMessage(player, "&8- &7 Players: &d" + party.getPlayers().size());
                sendMessage(player, "");
                ChatColor cl = ChatColor.GREEN;
                if (!Bukkit.getOfflinePlayer(party.getPlayers(DungeonRole.HOST).get(0)).isOnline()) cl = ChatColor.RED;
                sendMessage(player, "&eLeader: &a" + Bukkit.getOfflinePlayer(party.getPlayers(DungeonRole.HOST).get(0)).getName() + cl + " ●" );
                sendMessage(player, "&eMembers: &b" + (party.getPlayers(DungeonRole.PLAYER).size() > 0 ?
                        String.join(", ", names) : "&cNone"));
                sendMessage(player, "");
            }
            /*if (PartyHandler.getDungeonPartys().size() > 0) {
                player.sendMessage(ChatColor.GREEN + "PARTYS: " + PartyHandler.getDungeonPartys().size());

                PartyHandler.getDungeonPartys().forEach(party -> {
                    List<String> names = new ArrayList<>();
                    party.getPlayers().forEach((uuid, role) -> {
                        names.add(role.name() + "|" + Bukkit.getPlayer(uuid).getName());
                    });
                    player.sendMessage(ChatColor.GREEN + "-> PARTY" + ChatColor.AQUA + "#" + party.getID() + ChatColor.GREEN +
                            " - Players: " + ChatColor.YELLOW + String.join(",", names) + ChatColor.GREEN +
                            " - Status: " + ChatColor.YELLOW + party.getStatus().name());
                });
            }else sendError(player, "No party available. Create one with /dugeonparty create <name>");*/
        }
    }
}