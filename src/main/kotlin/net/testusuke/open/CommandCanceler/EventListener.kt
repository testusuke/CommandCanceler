package net.testusuke.open.CommandCanceler

import net.testusuke.open.CommandCanceler.Main.Companion.commandList
import net.testusuke.open.CommandCanceler.Main.Companion.executeCommand
import net.testusuke.open.CommandCanceler.Main.Companion.permission
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent

object EventListener:Listener {

    @EventHandler
    fun onCommandExecute(event:PlayerCommandPreprocessEvent){
        if(!Main.mode)return
        val command = event.message
        val lowCommand = command.toLowerCase()
        if(!commandList.contains(lowCommand))return
        val player = event.player
        //  ExecuteCommand
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),executeCommand)
        //  SendMessage
        for(player in Bukkit.getOnlinePlayers()){
            if(!player.hasPermission(permission))return
            player.sendMessage("${player.name} was execute command.command: $command")
        }
        //  Logger
        Bukkit.getLogger().info("${player.name} was execute command.command: $command")
    }
}