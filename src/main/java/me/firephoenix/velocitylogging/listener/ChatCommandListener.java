package me.firephoenix.velocitylogging.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;
import me.firephoenix.velocitylogging.VelocityLogging;

/**
 * @author NieGestorben
 * Copyright© (c) 2024, All Rights Reserved.
 */
public class ChatCommandListener {

    @Subscribe
    public void onChatEvent(PlayerChatEvent event) {
        Player player = event.getPlayer();

        VelocityLogging.INSTANCE.addToLogFile(event.getMessage(), player.getUsername(), player.getCurrentServer().isEmpty() ? "Unknown" : player.getCurrentServer().get().getServerInfo().getName());
    }

    @Subscribe
    public void onCommand(CommandExecuteEvent event) {
        if (!(event.getCommandSource() instanceof Player player)) return;

        VelocityLogging.INSTANCE.addToLogFile("(Command)" + event.getCommand(), player.getUsername(), player.getCurrentServer().isEmpty() ? "Unknown" : player.getCurrentServer().get().getServerInfo().getName());
    }

}
