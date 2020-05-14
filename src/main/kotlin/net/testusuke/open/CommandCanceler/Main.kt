package net.testusuke.open.CommandCanceler

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin(), CommandExecutor {

    companion object {
        var commandList = mutableListOf<String>()
        const val prefix = "§e[§6Command§cCanceler§e]§f"
        var mode = false
        var executeCommand = "kick %player%"
        const val permission = "commandcanceler.admin"
    }

    private val pluginName = "CommandCanceler"
    private val version = "1.0"

    override fun onEnable() {
        //  Logger
        logger.info("==============================")
        logger.info("Plugin: $pluginName")
        logger.info("Ver: $version  Author: testusuke")
        logger.info("==============================")
        //  Config
        saveDefaultConfig()
        //  Event
        server.pluginManager.registerEvents(EventListener, this)
        //  Command
        getCommand("cc")?.setExecutor(this)
        //  LoadConfig
        loadCommand()
        loadMode()
    }

    override fun onDisable() {
        saveCommand()
        saveMode()
        saveConfig()
    }

    private fun loadCommand() {
        try {
            executeCommand = config.getString("execute").toString()
            for (key in config.getConfigurationSection("command")!!.getKeys(false)) {
                commandList.add(config.getString("command.$key").toString())
            }
        } catch (e: NullPointerException) {
            logger.warning("データが存在しません。")
        }
    }

    private fun saveCommand() {
        config.set("execute", executeCommand)
        saveConfig()
        for ((i, command) in commandList.withIndex()) {
            config.set("command.$i", command)
        }
        saveConfig()
        commandList.clear()
    }

    private fun loadMode() {
        mode = config.getBoolean("mode")
    }

    private fun saveMode() {
        config.set("mode", mode)
        this.saveConfig()
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player || !sender.hasPermission(permission)) return false

        if (args.isEmpty()) {
            sendHelp(sender)
            return true
        }
        val ars0 = args[0]
        when (ars0) {
            "help" -> {
                sendHelp(sender)
            }
            "reload" -> {
                saveCommand()
                saveMode()
                loadCommand()
                loadMode()
                sender.sendMessage("${prefix}コンフィグを再読み込みしました。")
            }
            "add" -> {
                if (args.size <= 1) {
                    sendError(sender)
                    sendHelp(sender)
                    return false
                }
                val commands = getHeadCommand(args)?.toLowerCase()
                if (commands == null) {
                    sendError(sender)
                    return false
                }
                if (commandList.contains(commands)) {
                    sender.sendMessage("${prefix}§cすでに登録されています。")
                    return true
                }
                commandList.add(commands)
                sender.sendMessage("${prefix}§aコマンドを登録しました。command: $commands")
                return true
            }
            "remove" -> {
                if (args.size <= 1) {
                    sendError(sender)
                    sendHelp(sender)
                    return false
                }
                val commands = getHeadCommand(args)?.toLowerCase()
                if (commands == null) {
                    sendError(sender)
                    return false
                }
                if (!commandList.contains(commands)) {
                    sender.sendMessage("${prefix}§cコマンドが存在しません。")
                    return true
                }
                commandList.remove(commands)
                sender.sendMessage("${prefix}§aコマンドを削除しました。command: $commands")
                return true
            }
            "command" -> {
                if (args.size <= 1) {
                    sendError(sender)
                    sendHelp(sender)
                    return false
                }
                val commands = getCommandFromStrings(args)?.toLowerCase()
                if (commands == null) {
                    sendError(sender)
                    return false
                }
                executeCommand = commands
                sender.sendMessage("${prefix}§a処罰コマンドを登録しました。command: $commands")
                return true
            }
            "list" -> {
                if (!mode) {
                    sendDisable(sender)
                    return false
                }
                sender.sendMessage("${prefix}§c処罰コマンド: $executeCommand")
                sender.sendMessage("${prefix}§aコマンドリストを表示します。")
                if (commandList.isEmpty()) return false
                for (commands in commandList) {
                    sender.sendMessage("- $commands")
                }
            }

            "on" -> {
                if (mode) {
                    sender.sendMessage("${prefix}§cすでに有効です。")
                    return true
                }
                mode = true
                sender.sendMessage("${prefix}§a有効になりました。")
                return true
            }
            "off" -> {
                if (!mode) {
                    sender.sendMessage("${prefix}§cすでに無効です。")
                    return true
                }
                mode = false
                sender.sendMessage("${prefix}§a無効になりました。")
                return true
            }
            else -> sendHelp(sender)
        }

        return false
    }

    private fun sendError(player: Player) {
        player.sendMessage("${prefix}§c使用方法が不正です。")
    }

    private fun sendDisable(player: Player) {
        player.sendMessage("${prefix}§c現在使用できません。")
    }

    private fun sendHelp(player: Player) {
        player.sendMessage("§e===================================")
        player.sendMessage("§6/cc [help] <- ヘルプを表示します。")
        player.sendMessage("§6/cc reload <- コンフィグを再読み込みします。")
        player.sendMessage("§6/cc command <- 処罰コマンドを設定します。プレイヤーの名前を入れるときは%player%を使ってください。コマンドはスラッシュを入れないでください。")
        player.sendMessage("§6/cc add <command> <- 禁止コマンドを追加します。スラッシュを入れた状態にしてください。(例:/op)")
        player.sendMessage("§6/cc remove <command> <- 禁止コマンドを削除します。スラッシュを入れた状態にしてください。")
        player.sendMessage("§6/cc list <- 処罰コマンドと禁止コマンドのリストを表示します。")
        player.sendMessage("§6/cc on <- プラグインを有効にします。")
        player.sendMessage("§6/cc off <- プラグインを無効にします。")
        player.sendMessage("§d§lCreated by testusuke Version: $version")
        player.sendMessage("§e===================================")
    }

    private fun getCommandFromStrings(command: Array<out String>): String? {
        var s1: String? = null
        var i = 0
        for (s in command) {
            if (i < 1) {
                i++
                continue
            }
            if (s1 == null) {
                s1 = s
            } else {
                s1 += " $s"
            }
            i++

        }
        return s1
    }
    private fun getHeadCommand(command: Array<out String>): String? {
        var s1: String? = null
        var i = 0
        for (s in command) {
            if (i < 1) {
                i++
                continue
            }
            s1 = s
        }
        return s1
    }
}