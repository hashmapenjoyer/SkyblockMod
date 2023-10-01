package me.Danker.commands.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.Danker.DankersSkyblockMod;
import me.Danker.config.ModConfig;
import me.Danker.handlers.APIHandler;
import me.Danker.handlers.HypixelAPIHandler;
import me.Danker.utils.Utils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;

import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;

public class WeightCommand extends CommandBase {

    ArrayList<String> t12Minions = new ArrayList<>(Arrays.asList(
            "WHEAT_12",
            "CARROT_12",
            "POTATO_12",
            "PUMPKIN_12",
            "MELON_12",
            "MUSHROOM_12",
            "COCOA_12",
            "CACTUS_12",
            "SUGAR_CANE_12",
            "NETHER_WARTS_12"
    ));

    @Override
    public String getCommandName() {
        return "weight";
    }

    @Override
    public List<String> getCommandAliases() {
        return Collections.singletonList("we");
    }

    @Override
    public String getCommandUsage(ICommandSender arg0) {
        return "/" + getCommandName() + " [name] [lily/farming]";
    }

    public static String usage(ICommandSender arg0) {
        return new WeightCommand().getCommandUsage(arg0);
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        if (args.length == 1) {
            return Utils.getMatchingPlayers(args[0]);
        } else if (args.length == 2) {
            return getListOfStringsMatchingLastWord(args, "lily", "farming");
        }
        return null;
    }

    @Override
    public void processCommand(ICommandSender arg0, String[] arg1) throws CommandException {
        // MULTI THREAD DRIFTING
        new Thread(() -> {
            EntityPlayer player = (EntityPlayer) arg0;

            // Get UUID for Hypixel API requests
            String username;
            String uuid;
            if (arg1.length == 0) {
                username = player.getName();
                uuid = player.getUniqueID().toString().replaceAll("[\\-]", "");
            } else {
                username = arg1[0];
                uuid = APIHandler.getUUID(username);
            }
            player.addChatMessage(new ChatComponentText(ModConfig.getColour(ModConfig.mainColour) + "Checking weight of " + ModConfig.getColour(ModConfig.secondaryColour) + username + ModConfig.getColour(ModConfig.mainColour) + " using Polyfrost's API."));

            if (arg1.length < 2) {
                System.out.println("Fetching weight from SkyShiiyu API...");
                String weightURL = "https://sky.shiiyu.moe/api/v2/profile/" + username;
                JsonObject weightResponse = APIHandler.getResponse(weightURL, true);
                if (weightResponse.has("error")) {
                    String reason = weightResponse.get("error").getAsString();
                    player.addChatMessage(new ChatComponentText(ModConfig.getColour(ModConfig.errorColour) + "Failed with reason: " + reason));
                    return;
                }

                String latestProfileID = HypixelAPIHandler.getLatestProfileID(uuid);
                if (latestProfileID == null) return;

                JsonObject data = weightResponse.get("profiles").getAsJsonObject().get(latestProfileID).getAsJsonObject().get("data").getAsJsonObject().get("weight").getAsJsonObject().get("senither").getAsJsonObject();

                double weight = data.get("overall").getAsDouble();

                double skillWeight = data.get("skill").getAsJsonObject().get("total").getAsDouble();
                double farmingWeight = data.get("skill").getAsJsonObject().get("skills").getAsJsonObject().get("farming").getAsDouble();
                double miningWeight = data.get("skill").getAsJsonObject().get("skills").getAsJsonObject().get("mining").getAsDouble();
                double combatWeight = data.get("skill").getAsJsonObject().get("skills").getAsJsonObject().get("combat").getAsDouble();
                double foragingWeight = data.get("skill").getAsJsonObject().get("skills").getAsJsonObject().get("foraging").getAsDouble();
                double fishingWeight = data.get("skill").getAsJsonObject().get("skills").getAsJsonObject().get("fishing").getAsDouble();
                double enchantingWeight = data.get("skill").getAsJsonObject().get("skills").getAsJsonObject().get("enchanting").getAsDouble();
                double alchemyWeight = data.get("skill").getAsJsonObject().get("skills").getAsJsonObject().get("alchemy").getAsDouble();
                double tamingWeight = data.get("skill").getAsJsonObject().get("skills").getAsJsonObject().get("taming").getAsDouble();

                double slayerWeight = data.get("slayer").getAsJsonObject().get("total").getAsDouble();
                double zombieWeight = data.get("slayer").getAsJsonObject().get("slayers").getAsJsonObject().get("zombie").getAsDouble();
                double spiderWeight = data.get("slayer").getAsJsonObject().get("slayers").getAsJsonObject().get("spider").getAsDouble();
                double wolfWeight = data.get("slayer").getAsJsonObject().get("slayers").getAsJsonObject().get("wolf").getAsDouble();
                double endermanWeight = data.get("slayer").getAsJsonObject().get("slayers").getAsJsonObject().get("enderman").getAsDouble();
                double blazeWeight = data.get("slayer").getAsJsonObject().get("slayers").getAsJsonObject().get("blaze").getAsDouble();
                double vampireWeight = data.get("slayer").getAsJsonObject().get("slayers").getAsJsonObject().get("vampire").getAsDouble();

                double dungeonWeight = data.get("dungeon").getAsJsonObject().get("total").getAsDouble();
                double cataWeight = data.get("dungeon").getAsJsonObject().get("dungeons").getAsJsonObject().get("catacombs").getAsJsonObject().get("weight").getAsDouble();
                double healerWeight = data.get("dungeon").getAsJsonObject().get("classes").getAsJsonObject().get("healer").getAsJsonObject().get("weight").getAsDouble();
                double mageWeight = data.get("dungeon").getAsJsonObject().get("classes").getAsJsonObject().get("mage").getAsJsonObject().get("weight").getAsDouble();
                double berserkWeight = data.get("dungeon").getAsJsonObject().get("classes").getAsJsonObject().get("berserk").getAsJsonObject().get("weight").getAsDouble();
                double archerWeight = data.get("dungeon").getAsJsonObject().get("classes").getAsJsonObject().get("archer").getAsJsonObject().get("weight").getAsDouble();
                double tankWeight = data.get("dungeon").getAsJsonObject().get("classes").getAsJsonObject().get("tank").getAsJsonObject().get("weight").getAsDouble();

                NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
                player.addChatMessage(new ChatComponentText(ModConfig.getDelimiter() + "\n" +
                        EnumChatFormatting.AQUA + " " + username + "'s Weight:\n" +
                        ModConfig.getColour(ModConfig.typeColour) + " Total Weight: " + ModConfig.getColour(ModConfig.valueColour) + nf.format(weight) + "\n\n" +
                        ModConfig.getColour(ModConfig.typeColour) + " Skill Weight: " + ModConfig.getColour(ModConfig.valueColour) + nf.format(skillWeight) + "\n" +
                        ModConfig.getColour(ModConfig.typeColour) + "   Farming Weight: " + ModConfig.getColour(ModConfig.valueColour) + nf.format(farmingWeight) + "\n" +
                        ModConfig.getColour(ModConfig.typeColour) + "   Mining Weight: " + ModConfig.getColour(ModConfig.valueColour) + nf.format(miningWeight) + "\n" +
                        ModConfig.getColour(ModConfig.typeColour) + "   Combat Weight: " + ModConfig.getColour(ModConfig.valueColour) + nf.format(combatWeight) + "\n" +
                        ModConfig.getColour(ModConfig.typeColour) + "   Foraging Weight: " + ModConfig.getColour(ModConfig.valueColour) + nf.format(foragingWeight) + "\n" +
                        ModConfig.getColour(ModConfig.typeColour) + "   Fishing Weight: " + ModConfig.getColour(ModConfig.valueColour) + nf.format(fishingWeight) + "\n" +
                        ModConfig.getColour(ModConfig.typeColour) + "   Enchanting Weight: " + ModConfig.getColour(ModConfig.valueColour) + nf.format(enchantingWeight) + "\n" +
                        ModConfig.getColour(ModConfig.typeColour) + "   Alchemy Weight: " + ModConfig.getColour(ModConfig.valueColour) + nf.format(alchemyWeight) + "\n" +
                        ModConfig.getColour(ModConfig.typeColour) + "   Taming Weight: " + ModConfig.getColour(ModConfig.valueColour) + nf.format(tamingWeight) + "\n\n" +
                        ModConfig.getColour(ModConfig.typeColour) + " Slayers Weight: " + ModConfig.getColour(ModConfig.valueColour) + nf.format(slayerWeight) + "\n" +
                        ModConfig.getColour(ModConfig.typeColour) + "   Zombie Weight: " + ModConfig.getColour(ModConfig.valueColour) + nf.format(zombieWeight) + "\n" +
                        ModConfig.getColour(ModConfig.typeColour) + "   Spider Weight: " + ModConfig.getColour(ModConfig.valueColour) + nf.format(spiderWeight) + "\n" +
                        ModConfig.getColour(ModConfig.typeColour) + "   Wolf Weight: " + ModConfig.getColour(ModConfig.valueColour) + nf.format(wolfWeight) + "\n" +
                        ModConfig.getColour(ModConfig.typeColour) + "   Enderman Weight: " + ModConfig.getColour(ModConfig.valueColour) + nf.format(endermanWeight) + "\n" +
                        ModConfig.getColour(ModConfig.typeColour) + "   Blaze Weight: " + ModConfig.getColour(ModConfig.valueColour) + nf.format(blazeWeight) + "\n" +
                        ModConfig.getColour(ModConfig.typeColour) + "   Vampire Weight: " + ModConfig.getColour(ModConfig.valueColour) + nf.format(vampireWeight) + "\n\n" +
                        ModConfig.getColour(ModConfig.typeColour) + " Dungeons Weight: " + ModConfig.getColour(ModConfig.valueColour) + nf.format(dungeonWeight) + "\n" +
                        ModConfig.getColour(ModConfig.typeColour) + "   Catacombs XP Weight: " + ModConfig.getColour(ModConfig.valueColour) + nf.format(cataWeight) + "\n" +
                        ModConfig.getColour(ModConfig.typeColour) + "   Healer Weight: " + ModConfig.getColour(ModConfig.valueColour) + nf.format(healerWeight) + "\n" +
                        ModConfig.getColour(ModConfig.typeColour) + "   Mage Weight: " + ModConfig.getColour(ModConfig.valueColour) + nf.format(mageWeight) + "\n" +
                        ModConfig.getColour(ModConfig.typeColour) + "   Berserk Weight: " + ModConfig.getColour(ModConfig.valueColour) + nf.format(berserkWeight) + "\n" +
                        ModConfig.getColour(ModConfig.typeColour) + "   Archer Weight: " + ModConfig.getColour(ModConfig.valueColour) + nf.format(archerWeight) + "\n" +
                        ModConfig.getColour(ModConfig.typeColour) + "   Tank Weight: " + ModConfig.getColour(ModConfig.valueColour) + nf.format(tankWeight) + "\n" +
                        ModConfig.getDelimiter()));
            } else if (arg1[1].equalsIgnoreCase("lily")) {
                System.out.println("Fetching weight from SkyShiiyu API...");
                String weightURL = "https://sky.shiiyu.moe/api/v2/profile/" + username;
                JsonObject weightResponse = APIHandler.getResponse(weightURL, true);
                if (weightResponse.has("error")) {
                    String reason = weightResponse.get("error").getAsString();
                    player.addChatMessage(new ChatComponentText(ModConfig.getColour(ModConfig.errorColour) + "Failed with reason: " + reason));
                    return;
                }

                String latestProfileID = HypixelAPIHandler.getLatestProfileID(uuid);
                if (latestProfileID == null) return;

                JsonObject data = weightResponse.get("profiles").getAsJsonObject().get(latestProfileID).getAsJsonObject().get("data").getAsJsonObject().get("weight").getAsJsonObject().get("lily").getAsJsonObject();

                double weight = data.get("total").getAsDouble();
                double skillWeight = data.get("skill").getAsJsonObject().get("base").getAsDouble();
                double skillOverflow = data.get("skill").getAsJsonObject().get("overflow").getAsDouble();
                double slayerWeight = data.get("slayer").getAsDouble();
                double catacombsXPWeight = data.get("catacombs").getAsJsonObject().get("experience").getAsDouble();
                double catacombsBaseWeight = data.get("catacombs").getAsJsonObject().get("completion").getAsJsonObject().get("base").getAsDouble();
                double catacombsMasterWeight = data.get("catacombs").getAsJsonObject().get("completion").getAsJsonObject().get("master").getAsDouble();

                NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
                player.addChatMessage(new ChatComponentText(ModConfig.getDelimiter() + "\n" +
                        EnumChatFormatting.AQUA + " " + username + "'s Weight (Lily):\n" +
                        ModConfig.getColour(ModConfig.typeColour) + " Total Weight: " + ModConfig.getColour(ModConfig.valueColour) + nf.format(weight) + "\n" +
                        ModConfig.getColour(ModConfig.typeColour) + " Skill Weight: " + ModConfig.getColour(ModConfig.valueColour) + nf.format(skillWeight + skillOverflow) + " (" + nf.format(skillWeight) + " + " + nf.format(skillOverflow) + ")\n" +
                        ModConfig.getColour(ModConfig.typeColour) + " Slayers Weight: " + ModConfig.getColour(ModConfig.valueColour) + nf.format(slayerWeight) + "\n" +
                        ModConfig.getColour(ModConfig.typeColour) + " Catacombs XP Weight: " + ModConfig.getColour(ModConfig.valueColour) + nf.format(catacombsXPWeight) + "\n" +
                        ModConfig.getColour(ModConfig.typeColour) + " Catacombs Completion Weight: " + ModConfig.getColour(ModConfig.valueColour) + nf.format(catacombsBaseWeight) + "\n" +
                        ModConfig.getColour(ModConfig.typeColour) + " Catacombs Master Completion Weight: " + ModConfig.getColour(ModConfig.valueColour) + nf.format(catacombsMasterWeight) + "\n" +
                        ModConfig.getDelimiter()));
            } else if (arg1[1].equalsIgnoreCase("farming")) {
                String profileURL = "https://api.elitebot.dev/weight/" + uuid;
                System.out.println("Fetching weight from elitebot.dev...");

                JsonObject weightResponse = APIHandler.getResponse(profileURL, true);

                String selectedProfileId;
                JsonObject profileWeight = null;

                try {
                    selectedProfileId = weightResponse.get("selectedProfileId").getAsString();
                    JsonArray profiles = weightResponse.get("profiles").getAsJsonArray();

                    for (JsonElement element : profiles) {
                        if (!element.isJsonObject()) continue;

                        JsonObject profileObj = element.getAsJsonObject();
                        String profileId = profileObj.get("profileId").getAsString();

                        if (!profileId.equals(selectedProfileId)) continue;

                        profileWeight = profileObj;
                        break;
                    }
                } catch (Exception e) {
                    player.addChatMessage(new ChatComponentText(ModConfig.getColour(ModConfig.errorColour) + "Failed to get farming weight! Try again later."));
                    return;
                }

                if (profileWeight == null) {
                    player.addChatMessage(new ChatComponentText(ModConfig.getColour(ModConfig.errorColour) + "Failed to get farming weight! Try again later."));
                    return;
                }

                double mainWeight = profileWeight.get("totalWeight").getAsDouble();

                if (mainWeight == 0) {
                    player.addChatMessage(new ChatComponentText(ModConfig.getColour(ModConfig.errorColour) + username + " does not have collection API on (or has zero farming weight)!"));
                    return;
                }

                NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);

                JsonObject bonusWeight = profileWeight.getAsJsonObject("bonusWeight");

                StringBuilder bonusSources = new StringBuilder();
                int bonusWeightTotal = 0;

                for (Map.Entry<String, JsonElement> bonus : bonusWeight.entrySet()) {
                    if (bonus.getValue().isJsonNull()) continue;

                    String bonusName = bonus.getKey();
                    int bonusAmount = bonus.getValue().getAsInt();

                    bonusWeightTotal += bonusAmount;

                    bonusSources
                        .append(ModConfig.getColour(ModConfig.typeColour))
                        .append(bonusName).append(": ")
                        .append(ModConfig.getColour(ModConfig.valueColour))
                        .append(nf.format(bonusAmount)).append("\n");
                }

                player.addChatMessage(new ChatComponentText(ModConfig.getDelimiter() + "\n" +
                        EnumChatFormatting.AQUA + username + "'s Weight (Farming):\n" +
                        ModConfig.getColour(ModConfig.typeColour) + "Total Weight: " + ModConfig.getColour(ModConfig.valueColour) + nf.format(mainWeight) + "\n" +
                        ModConfig.getColour(ModConfig.typeColour) + "Collection Weight: " + ModConfig.getColour(ModConfig.valueColour) + nf.format(mainWeight - bonusWeightTotal) + "\n" +
                        ModConfig.getColour(ModConfig.typeColour) + "Bonus Weight: " + ModConfig.getColour(ModConfig.valueColour) + nf.format(bonusWeightTotal) + "\n" +
                        bonusSources +
                        ModConfig.getDelimiter()));
            } else {
                player.addChatMessage(new ChatComponentText(ModConfig.getColour(ModConfig.errorColour) + "Usage: " + getCommandUsage(arg0)));
            }
        }).start();
    }

}
