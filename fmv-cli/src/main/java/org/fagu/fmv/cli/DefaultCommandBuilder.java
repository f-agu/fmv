package org.fagu.fmv.cli;

/*
 * #%L
 * fmv-cli
 * %%
 * Copyright (C) 2014 fagu
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.fagu.fmv.cli.annotation.Alias;
import org.fagu.fmv.cli.annotation.Aliases;
import org.fagu.fmv.cli.annotation.Completion;
import org.fagu.fmv.cli.command.AliasCustomCommandFactory;
import org.fagu.fmv.cli.completion.CompleterFactory;
import org.fagu.fmv.cli.exception.LineParseException;
import org.fagu.fmv.core.project.Project;
import org.fagu.fmv.utils.ClassResolver;
import org.fagu.fmv.utils.collection.MapList;
import org.fagu.fmv.utils.collection.MultiValueMaps;

import jline.console.ConsoleReader;
import jline.console.completer.Completer;


/**
 * @author f.agu
 */
public class DefaultCommandBuilder implements CommandBuilder {

	private Environnement environnement;

	private final FMVCLIConfig fmvCliConfig;

	private final Project project;

	private final ConsoleReader consoleReader;

	private Map<String, CommandFactory> cmdBuilderMap;

	private MapList<String, Completer> completerMap;

	/**
	 * @param environnement
	 */
	public DefaultCommandBuilder(Environnement environnement) {
		this.environnement = environnement;
		this.fmvCliConfig = environnement.getFMVCLIConfig();
		this.project = environnement.getProject();
		this.consoleReader = environnement.getConsoleReader();
		cmdBuilderMap = new HashMap<String, CommandFactory>(32);
		completerMap = MultiValueMaps.hashMapArrayList();
	}

	/**
	 * @see org.fagu.fmv.cli.CommandBuilder#add(java.lang.Class)
	 */
	@Override
	public void add(final Class<? extends Command> cmdClass) {
		final org.fagu.fmv.cli.annotation.Command cmd = cmdClass.getAnnotation(org.fagu.fmv.cli.annotation.Command.class);
		if(cmd == null) {
			throw new RuntimeException("Annotation @Command not found: " + cmdClass.getName());
		}
		add(new CommandFactory() {

			/**
			 * @see org.fagu.fmv.cli.CommandFactory#getCommandName()
			 */
			@Override
			public String getCommandName() {
				return cmd.value();
			}

			/**
			 * @see org.fagu.fmv.cli.CommandFactory#create(jline.console.ConsoleReader, org.fagu.fmv.cli.CommandBuilder,
			 *      org.fagu.fmv.core.project.Project, java.lang.String, String[])
			 */
			@Override
			public Command create(ConsoleReader consoleReader, CommandBuilder commandBuilder, Project project, String executable, String[] args)
					throws LineParseException {
				return build(cmdClass);
			}
		});

		// alias
		List<Alias> aliases = new ArrayList<>();

		Aliases aliasesAnno = cmdClass.getAnnotation(Aliases.class);
		if(aliasesAnno != null) {
			aliases.addAll(Arrays.asList(aliasesAnno.value()));
		}

		Alias aliasAnno = cmdClass.getAnnotation(Alias.class);
		if(aliasAnno != null) {
			aliases.add(aliasAnno);
		}
		if( ! aliases.isEmpty()) {
			for(Alias alias : aliases) {
				String aliasCommand = alias.command();
				if(StringUtils.isBlank(aliasCommand)) {
					aliasCommand = cmd.value();
				}
				addAlias(alias.value(), aliasCommand);
			}
		}

		// completion
		Completion completion = cmdClass.getAnnotation(Completion.class);
		if(completion != null) {
			try {
				@SuppressWarnings("unchecked")
				Class<CompleterFactory> completerFactoryClass = (Class<CompleterFactory>)Class.forName(completion.value());
				CompleterFactory completerFactory = completerFactoryClass.newInstance();
				Completer completer = completerFactory.create(cmd.value());
				completerMap.add(cmd.value(), completer);
			} catch(InstantiationException | IllegalAccessException | ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * @see org.fagu.fmv.cli.CommandBuilder#add(java.lang.String)
	 */
	@Override
	public void add(String packageName) {
		ClassResolver classResolver = new ClassResolver();
		try {
			for(Class<?> findCls : classResolver.find(packageName, c -> {
				if(Command.class.isAssignableFrom(c)) {
					int mod = c.getModifiers();
					return ! Modifier.isAbstract(mod) && ! Modifier.isInterface(mod) && Modifier.isPublic(mod);
				}
				return false;
			})) {
				@SuppressWarnings("unchecked")
				Class<Command> cmdClass = (Class<Command>)findCls;
				add(cmdClass);
			}
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see org.fagu.fmv.cli.CommandBuilder#add(java.lang.Package)
	 */
	@Override
	public void add(Package pckg) {
		add(pckg.getName());
	}

	/**
	 * @see org.fagu.fmv.cli.CommandBuilder#add(org.fagu.fmv.cli.CommandFactory)
	 */
	@Override
	public void add(CommandFactory commandFactory) {
		cmdBuilderMap.put(commandFactory.getCommandName().toLowerCase(), commandFactory);
	}

	/**
	 * @see org.fagu.fmv.cli.CommandBuilder#addAlias(java.lang.String, java.lang.String)
	 */
	@Override
	public void addAlias(String name, String line) {
		add(new AliasCustomCommandFactory(name, line));
	}

	/**
	 * @see org.fagu.fmv.cli.CommandBuilder#getCommandFactory(java.lang.String)
	 */
	@Override
	public CommandFactory getCommandFactory(String name) {
		return cmdBuilderMap.get(name);
	}

	/**
	 * @see org.fagu.fmv.cli.CommandBuilder#getCommandFactories()
	 */
	@Override
	public Collection<CommandFactory> getCommandFactories() {
		return cmdBuilderMap.values();
	}

	/**
	 * @see org.fagu.fmv.cli.CommandBuilder#remove(java.lang.String)
	 */
	@Override
	public CommandFactory remove(String name) {
		String lcName = name.toLowerCase();
		CommandFactory removed = cmdBuilderMap.remove(lcName);
		for(Completer completer : completerMap.get(lcName)) {
			consoleReader.removeCompleter(completer);
		}
		return removed;
	}

	/**
	 * @see org.fagu.fmv.cli.CommandBuilder#createAndExec(java.lang.String)
	 */
	@Override
	public Command createAndExec(String line) throws LineParseException {
		CommandLine parse = new CommandLine("ignore");
		parse.addArguments(line, false);
		String[] arguments = parse.getArguments();
		String executable = arguments[0];

		CommandFactory commandFactory = cmdBuilderMap.get(executable.toLowerCase());
		if(commandFactory == null) {
			throw new LineParseException(line);
		}

		String[] args = new String[arguments.length - 1];
		System.arraycopy(arguments, 1, args, 0, arguments.length - 1);

		Command command = commandFactory.create(consoleReader, this, project, commandFactory.getCommandName(), args);

		Options options = new Options();
		options.addOption("h", "help", false, "");
		CommandLineParser parser = new GnuParser();
		try {
			org.apache.commons.cli.CommandLine commandLine = parser.parse(options, args);
			if(commandLine.hasOption('h')) {
				command.help();
				return command;
			}
		} catch(ParseException e) {
			// ignore
		}

		command.run(args);
		return command;
	}

	/**
	 * @param cmdClass
	 * @return
	 */
	protected Command build(Class<? extends Command> cmdClass) {
		try {
			Command command = cmdClass.newInstance();
			for(Class<?> cls : ClassUtils.hierarchy(cmdClass)) {
				for(Field field : cls.getDeclaredFields()) {
					Object instanceOf = getInstanceOf(field.getType());
					if(instanceOf != null) {
						field.setAccessible(true);
						field.set(command, instanceOf);
					}
				}
			}
			return command;
		} catch(InstantiationException e) {
			throw new RuntimeException(e);
		} catch(IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @param c
	 * @return
	 */
	protected Object getInstanceOf(Class<?> c) {
		if(c == Environnement.class) {
			return environnement;
		}
		if(c == ConsoleReader.class) {
			return consoleReader;
		}
		if(c == CommandBuilder.class) {
			return this;
		}
		if(c == Project.class) {
			return project;
		}
		if(c == FMVCLIConfig.class) {
			return fmvCliConfig;
		}
		return null;
	}

}
