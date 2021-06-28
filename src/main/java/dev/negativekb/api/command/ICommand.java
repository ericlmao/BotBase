package dev.negativekb.api.command;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import dev.negativekb.api.Bot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class ICommand extends Command {
    private final ArrayList<ISubCommand> subCommands = new ArrayList<>();

    public ICommand(String command) {
        this(command, Collections.emptyList());
    }

    public ICommand(String command, List<String> aliases) {
        this.name = command;

        if (!aliases.isEmpty()) {
            this.aliases = aliases.toArray(new String[0]);
        }

        Bot.getInstance().getBuilder().addCommand(this);
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split(" ");
        if (args.length == 0) {
            this.runCommand(event, args);
            return;
        }

        String arg = args[0];
        String[] newArgs = Arrays.copyOfRange(args, 1, args.length);
        for (ISubCommand subCommand : subCommands) {
            if (subCommand.getArgument().equalsIgnoreCase(arg)) {
                subCommand.execute(event, newArgs);
                return;
            }

            List<String> aliases = subCommand.getAliases();
            if (aliases == null || aliases.isEmpty())
                continue;

            for (String alias : aliases) {
                if (alias.equalsIgnoreCase(arg)) {
                    subCommand.execute(event, newArgs);
                    return;
                }
            }
        }

        runCommand(event, args);
    }

    public abstract void runCommand(CommandEvent event, String[] args);

    public void addSubCommands(ISubCommand... subCommands) {
        this.subCommands.addAll(Arrays.asList(subCommands));
    }

}
