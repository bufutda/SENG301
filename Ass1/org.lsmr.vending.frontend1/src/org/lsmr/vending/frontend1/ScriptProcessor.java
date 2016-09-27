package org.lsmr.vending.frontend1;

import java.io.FileReader;
import java.io.IOException;

import org.lsmr.vending.frontend1.parser.ParseException;
import org.lsmr.vending.frontend1.parser.Parser;

/**
 * Provides a simple facade class that interacts with the parser appropriately.
 */
public class ScriptProcessor {
    /**
     * Basic constructor. Constructs a parser to read the script file located at
     * the indicated path. Registers the indicated factory with the parser.
     * Attempts to parse and interpret the script.
     * 
     * @param path
     *            The file path to the script file to be read.
     * @param factory
     *            A factory object that allows vending machines to be created.
     * @param debug
     *            A flag that indicates whether debugging information should be
     *            sent to the standard error stream.
     * @throws IOException
     *             If the path does not exist, or if the file located there
     *             cannot be opened for whatever reason.
     * @throws ParseException
     *             If the script file does not conform to the correct syntax.
     */
    public ScriptProcessor(String path, IVendingMachineFactory factory, boolean debug) throws IOException, ParseException {
	Parser p = new Parser(new FileReader(path));
	p.register(factory);
	p.setDebug(debug);
	p.process(path);
    }
}
