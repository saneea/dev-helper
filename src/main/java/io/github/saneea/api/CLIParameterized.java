package io.github.saneea.api;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

public interface CLIParameterized {

	Options createOptions();

	void setCommandLine(CommandLine commandLine);
}
