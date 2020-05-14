package net.testusuke.open.CommandCanceler

import net.testusuke.open.CommandCanceler.Main.Companion.commandList
import net.testusuke.open.CommandCanceler.Main.Companion.executeCommand
import net.testusuke.open.CommandCanceler.Main.Companion.permission
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent

object EventListener : Listener {

    @EventHandler
    fun onCommandExecute(event: PlayerCommandPreprocessEvent) {
        if (!Main.mode) return
        val command = event.message
        val lowCommand = command.toLowerCase()
        val args = lowCommand.split(" ")
        if (!commandList.contains(args[0])) return
        val player = event.player
        //  ExecuteCommand
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), executeCommand.replace("%player%",player.name))
        //  SendMessage
        for (onlinePlayer in Bukkit.getOnlinePlayers()) {
            if (!onlinePlayer.hasPermission(permission)) return
            onlinePlayer.sendMessage("${player.name} executed disallow command.command: $command")
        }
        //  Logger
        Bukkit.getLogger().info("${player.name} executed disallow command.command: $command")
    }
}