package com.techern.minecraft.permissions.command;

import java.util.Iterator;

import com.techern.minecraft.permissions.PermissionsManaged;
import net.minecraft.command.*;
import net.minecraft.command.common.CommandReplaceItem;
import net.minecraft.command.server.CommandAchievement;
import net.minecraft.command.server.CommandBanIp;
import net.minecraft.command.server.CommandBanPlayer;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.command.server.CommandBroadcast;
import net.minecraft.command.server.CommandDeOp;
import net.minecraft.command.server.CommandEmote;
import net.minecraft.command.server.CommandListBans;
import net.minecraft.command.server.CommandListPlayers;
import net.minecraft.command.server.CommandMessage;
import net.minecraft.command.server.CommandMessageRaw;
import net.minecraft.command.server.CommandOp;
import net.minecraft.command.server.CommandPardonIp;
import net.minecraft.command.server.CommandPardonPlayer;
import net.minecraft.command.server.CommandPublishLocalServer;
import net.minecraft.command.server.CommandSaveAll;
import net.minecraft.command.server.CommandSaveOff;
import net.minecraft.command.server.CommandSaveOn;
import net.minecraft.command.server.CommandScoreboard;
import net.minecraft.command.server.CommandSetBlock;
import net.minecraft.command.server.CommandSetDefaultSpawnpoint;
import net.minecraft.command.server.CommandStop;
import net.minecraft.command.server.CommandSummon;
import net.minecraft.command.server.CommandTeleport;
import net.minecraft.command.server.CommandTestFor;
import net.minecraft.command.server.CommandTestForBlock;
import net.minecraft.command.server.CommandWhitelist;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;

/**
 * A drop-in replacement of {@link ServerCommandManager}
 *
 * @since 0.0.1
 */
public class PermissionControlledServerCommandManager extends CommandHandler implements IAdminCommand {

    /**
     * Creates a new {@link PermissionControlledServerCommandManager}
     *
     * @since 0.0.1
     */
    public PermissionControlledServerCommandManager() {
        this.registerCommand(new CommandTime());
        this.registerCommand(new CommandGameMode());
        this.registerCommand(new CommandDifficulty());
        this.registerCommand(new CommandDefaultGameMode());
        this.registerCommand(new CommandKill());
        this.registerCommand(new CommandToggleDownfall());
        this.registerCommand(new CommandWeather());
        this.registerCommand(new CommandXP());
        this.registerCommand(new CommandTeleport());
        this.registerCommand(new CommandGive());
        this.registerCommand(new CommandReplaceItem());
        this.registerCommand(new CommandStats());
        this.registerCommand(new CommandEffect());
        this.registerCommand(new CommandEnchant());
        this.registerCommand(new CommandParticle());
        this.registerCommand(new CommandEmote());
        this.registerCommand(new CommandShowSeed());
        this.registerCommand(new CommandHelp());
        this.registerCommand(new CommandDebug());
        this.registerCommand(new CommandMessage());
        this.registerCommand(new CommandBroadcast());
        this.registerCommand(new CommandSetSpawnpoint());
        this.registerCommand(new CommandSetDefaultSpawnpoint());
        this.registerCommand(new CommandGameRule());
        this.registerCommand(new CommandClearInventory());
        this.registerCommand(new CommandTestFor());
        this.registerCommand(new CommandSpreadPlayers());
        this.registerCommand(new CommandPlaySound());
        this.registerCommand(new CommandScoreboard());
        this.registerCommand(new CommandExecuteAt());
        this.registerCommand(new CommandTrigger());
        this.registerCommand(new CommandAchievement());
        this.registerCommand(new CommandSummon());
        this.registerCommand(new CommandSetBlock());
        this.registerCommand(new CommandFill());
        this.registerCommand(new CommandClone());
        this.registerCommand(new CommandCompare());
        this.registerCommand(new CommandBlockData());
        this.registerCommand(new CommandTestForBlock());
        this.registerCommand(new CommandMessageRaw());
        this.registerCommand(new CommandWorldBorder());
        this.registerCommand(new CommandTitle());
        this.registerCommand(new CommandEntityData());

        if (MinecraftServer.getServer().isDedicatedServer()) {
            this.registerCommand(new CommandOp());
            this.registerCommand(new CommandDeOp());
            this.registerCommand(new CommandStop());
            this.registerCommand(new CommandSaveAll());
            this.registerCommand(new CommandSaveOff());
            this.registerCommand(new CommandSaveOn());
            this.registerCommand(new CommandBanIp());
            this.registerCommand(new CommandPardonIp());
            this.registerCommand(new CommandBanPlayer());
            this.registerCommand(new CommandListBans());
            this.registerCommand(new CommandPardonPlayer());
            this.registerCommand(new CommandServerKick());
            this.registerCommand(new CommandListPlayers());
            this.registerCommand(new CommandWhitelist());
            this.registerCommand(new CommandSetPlayerTimeout());
        } else {
            this.registerCommand(new CommandPublishLocalServer());
        }

        CommandBase.setAdminCommander(this);
    }

    /**
     * Registers a {@link ICommand}, logging it
     *
     * @param command The {@link ICommand} being registered
     * @return The results of super.registerCommand(command)
     */
    @Override
    public ICommand registerCommand(ICommand command) {
        PermissionsManaged.LOGGER.trace("Registered command: " + command.getName());
        return super.registerCommand(command);
    }

    public void notifyOperators(ICommandSender sender, ICommand command, int p_152372_3_, String msgFormat, Object... msgParams) {
        boolean flag = true;
        MinecraftServer minecraftserver = MinecraftServer.getServer();

        if (!sender.sendCommandFeedback()) {
            flag = false;
        }

        ChatComponentTranslation chatcomponenttranslation = new ChatComponentTranslation("chat.type.admin", new Object[]{sender.getName(), new ChatComponentTranslation(msgFormat, msgParams)});
        chatcomponenttranslation.getChatStyle().setColor(EnumChatFormatting.GRAY);
        chatcomponenttranslation.getChatStyle().setItalic(true);

        if (flag) {
            Iterator iterator = minecraftserver.getConfigurationManager().playerEntityList.iterator();

            while (iterator.hasNext()) {
                EntityPlayer entityplayer = (EntityPlayer) iterator.next();

                if (entityplayer != sender && minecraftserver.getConfigurationManager().canSendCommands(entityplayer.getGameProfile()) && command.canCommandSenderUse(sender)) {
                    entityplayer.addChatMessage(chatcomponenttranslation);
                }
            }
        }

        if (sender != minecraftserver && minecraftserver.worldServers[0].getGameRules().getGameRuleBooleanValue("logAdminCommands")) {
            minecraftserver.addChatMessage(chatcomponenttranslation);
        }

        boolean flag1 = minecraftserver.worldServers[0].getGameRules().getGameRuleBooleanValue("sendCommandFeedback");

        if (sender instanceof CommandBlockLogic) {
            flag1 = ((CommandBlockLogic) sender).shouldTrackOutput();
        }

        if ((p_152372_3_ & 1) != 1 && flag1) {
            sender.addChatMessage(new ChatComponentTranslation(msgFormat, msgParams));
        }
    }
}